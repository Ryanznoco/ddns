package cn.com.qjun.ddns.schedule;

import cn.com.qjun.ddns.entity.DDNSRecordConfig;
import cn.com.qjun.ddns.entity.PPPoEHistory;
import cn.com.qjun.ddns.service.*;
import cn.com.qjun.ddns.support.NetworkInterface;
import cn.com.qjun.ddns.vo.DNSInfo;
import cn.com.qjun.ddns.vo.PPPoEInfo;
import com.aliyuncs.alidns.model.v20150109.DescribeDomainRecordsResponse;
import com.aliyuncs.exceptions.ClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ryan
 * @date 2018/5/5
 */
@Slf4j
@Component
public class DnsJob {
    @Autowired
    private DnsService dnsService;
    @Autowired
    private LuyouService luyouService;
    @Autowired
    private DDNSConfigService ddnsConfigService;
    @Autowired
    private PPPoEHistoryService ppPoEHistoryService;
    @Autowired
    private DNSHistoryService dnsHistoryService;

    @Transactional(rollbackFor = Exception.class)
    public void execute() {
        log.info("域名解析更新任务开始执行！");
        List<PPPoEInfo> ppPoEInfoList = luyouService.getAllPPPoEInfo();
        if (ppPoEInfoList.isEmpty()) {
            log.error("获取网口拨号信息为空");
            return;
        }
        List<DDNSRecordConfig> recordConfigs = ddnsConfigService.findAllAvailable();
        Map<NetworkInterface, String> wanIpMap = buildWanIpMap(ppPoEInfoList);
        Map<NetworkInterface, PPPoEHistory> currentPPPoEMap = ppPoEHistoryService.findCurrentByInterface(wanIpMap.keySet());
        savePPPoEHistory(wanIpMap, currentPPPoEMap);

        Map<String, List<DescribeDomainRecordsResponse.Record>> domainRecordsMap = new HashMap<>(16);
        for (DDNSRecordConfig recordConfig :
                recordConfigs) {
            String domainName = recordConfig.getDomain().getName();
            String rr = recordConfig.getRr();
            String ip = wanIpMap.get(recordConfig.getNetworkInterface());
            try {
                List<DescribeDomainRecordsResponse.Record> dnsRecordList = domainRecordsMap.get(domainName);
                if (dnsRecordList == null) {
                    dnsRecordList = dnsService.getRecordList(domainName);
                    domainRecordsMap.put(domainName, dnsRecordList);
                }
                DescribeDomainRecordsResponse.Record targetRecord = null;
                for (DescribeDomainRecordsResponse.Record record :
                        dnsRecordList) {
                    if (record.getRR().equals(recordConfig.getRr())) {
                        targetRecord = record;
                        break;
                    }
                }
                DNSInfo dnsInfo = new DNSInfo();
                dnsInfo.setDomainName(domainName);
                dnsInfo.setRr(rr);
                dnsInfo.setRecordType(recordConfig.getRecordType());
                dnsInfo.setValue(ip);
                if (targetRecord == null) {
                    dnsService.addRecord(dnsInfo);
                    dnsHistoryService.saveHistory(recordConfig, dnsInfo);
                    log.info("添加域名解析成功：{}.{} <-> {}", rr, domainName, ip);
                } else if (!targetRecord.getValue().equals(ip)) {
                    dnsService.updateRecord(targetRecord.getRecordId(), dnsInfo);
                    dnsHistoryService.saveHistory(recordConfig, dnsInfo);
                    log.info("修改域名解析成功：{}.{} <-> {} <- {}", rr, domainName, ip, targetRecord.getValue());
                } else {
                    log.info("无需更新域名解析：{}.{} <-> {} -> {}", rr, domainName, targetRecord.getValue(), ip);
                }
            } catch (ClientException e) {
                log.error(String.format("阿里云API调用出错：%s", e.getMessage()), e);
            }
        }
        log.info("域名解析更新任务执行完毕！");
    }

    private void savePPPoEHistory(Map<NetworkInterface, String> wanIpMap, Map<NetworkInterface, PPPoEHistory> currentPPPoEMap) {
        for (Map.Entry<NetworkInterface, String> entry :
                wanIpMap.entrySet()) {
            NetworkInterface ni = entry.getKey();
            String ip = entry.getValue();
            PPPoEHistory ppPoEHistory = currentPPPoEMap.get(ni);
            if (ppPoEHistory == null) {
                ppPoEHistoryService.saveNew(ni, ip);
            } else if (!ppPoEHistory.getIpAddress().equals(ip)) {
                ppPoEHistoryService.saveAsHistory(ppPoEHistory);
                ppPoEHistoryService.saveNew(ni, ip);
            }
        }
    }

    private Map<NetworkInterface, String> buildWanIpMap(List<PPPoEInfo> ppPoEInfoList) {
        Map<NetworkInterface, String> wanIpMap = new HashMap<>(16);
        for (PPPoEInfo ppPoEInfo :
                ppPoEInfoList) {
            wanIpMap.put(NetworkInterface.nameOf(ppPoEInfo.getName()), ppPoEInfo.getIpAddress());
        }
        return wanIpMap;
    }
}
