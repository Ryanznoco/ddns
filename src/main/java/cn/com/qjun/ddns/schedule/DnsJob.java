package cn.com.qjun.ddns.schedule;

import cn.com.qjun.ddns.service.DnsService;
import cn.com.qjun.ddns.service.LuyouService;
import com.aliyuncs.alidns.model.v20150109.DescribeDomainRecordsResponse;
import com.aliyuncs.exceptions.ClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ryan
 * @date 2018/5/5
 */
@Slf4j
@Component
public class DnsJob {
    private static final String SEPARATOR = "-";
    private static final String RR_ROOT = "@";

    @Autowired
    private DnsService dnsService;
    @Autowired
    private LuyouService luyouService;

    public void execute() {
        log.info("域名解析更新任务开始执行！");
        Map<String, Map<String, String>> dnsMap = getDnsList();
        for (Map.Entry<String, Map<String, String>> entry :
                dnsMap.entrySet()) {
            String domain = entry.getKey();
            List<DescribeDomainRecordsResponse.Record> oldRecords = null;
            try {
                oldRecords = dnsService.getRecordList(domain);
            } catch (ClientException e) {
                log.error("获取域名[{}]解析记录列表出错：{}, {}", domain, e.getMessage(), e);
            }
            Map<String, String> newRecords = entry.getValue();
            for (Map.Entry<String, String> newRecord :
                    newRecords.entrySet()) {
                String rr = newRecord.getKey();
                String value = newRecord.getValue();
                boolean hasRecord = false;
                for (DescribeDomainRecordsResponse.Record oldRecord :
                        oldRecords) {
                    if (oldRecord.getRR().equals(rr)) {
                        hasRecord = true;
                        if (oldRecord.getValue().equals(value)) {
                            log.info("无需更新域名解析：{}.{} -> {}", rr, domain, value);
                        } else {
                            try {
                                dnsService.updateRecord(oldRecord.getRecordId(), rr, value);
                                log.info("更新域名映射：{}.{} - {} -> {}", rr, domain, oldRecord.getValue(), value);
                            } catch (ClientException e) {
                                log.error("更新域名解析出错：{} -> {}, {} -> {}", oldRecord, rr + "." + domain + ":" + value, e.getMessage(), e);
                            }
                        }
                        break;
                    }
                }
                if (!hasRecord) {
                    try {
                        dnsService.addRecord(domain, rr, value);
                    } catch (ClientException e) {
                        log.error("添加域名解析出错：{}, {} -> {}", rr + "." + domain + ":" + value, e.getMessage(), e);
                    }
                }
            }
        }
        log.info("域名解析更新任务执行完毕！");
    }

    private Map<String, Map<String, String>> getDnsList() {
        Map<String, String> ipMap = luyouService.getPublicIps();
        String regex = "(?<=\\()([^)]+)(?=\\))";
        Pattern pattern = Pattern.compile(regex);
        Map<String, Map<String, String>> dnsMap = new HashMap<>(16);
        for (Map.Entry<String, String> entry :
                ipMap.entrySet()) {
            Matcher matcher = pattern.matcher(entry.getKey());
            while (matcher.find()) {
                String recordStr = matcher.group();
                String rr;
                String domain;
                if (recordStr.contains(SEPARATOR)) {
                    String[] rrAndDomain = recordStr.split(SEPARATOR);
                    rr = rrAndDomain[0];
                    domain = rrAndDomain[1];
                } else {
                    rr = RR_ROOT;
                    domain = recordStr;
                }
                Map<String, String> recordMap = dnsMap.get(domain);
                if (recordMap == null) {
                    recordMap = new HashMap<>(16);
                    dnsMap.put(domain, recordMap);
                }
                recordMap.put(rr, entry.getValue());
            }
        }
        return dnsMap;
    }
}
