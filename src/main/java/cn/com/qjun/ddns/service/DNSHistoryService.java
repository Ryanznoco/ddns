package cn.com.qjun.ddns.service;

import cn.com.qjun.ddns.entity.DDNSRecordConfig;
import cn.com.qjun.ddns.entity.DNSHistory;
import cn.com.qjun.ddns.repository.DNSHistoryRepository;
import cn.com.qjun.ddns.vo.DNSInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author Ryan
 * @date 2018/7/17
 */
@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class DNSHistoryService {
    @Autowired
    private DNSHistoryRepository dnsHistoryRepository;

    @Transactional(rollbackFor = Exception.class)
    public DNSHistory saveHistory(DDNSRecordConfig recordConfig, DNSInfo dnsInfo) {
        DNSHistory history = dnsHistoryRepository.findByRrIdAndCurrent(recordConfig.getId(), true);
        if (history != null) {
            history.setEndTime(new Date());
            history.setCurrent(false);
            dnsHistoryRepository.save(history);
        }
        DNSHistory newHistory = new DNSHistory();
        newHistory.setRr(recordConfig);
        newHistory.setValue(dnsInfo.getValue());
        newHistory.setStartTime(new Date());
        newHistory.setCurrent(true);
        return dnsHistoryRepository.save(newHistory);
    }
}
