package cn.com.qjun.ddns.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author Ryan
 * @date 2018/5/5
 */
@Slf4j
@Component
public class DnsScheduler {
    @Autowired
    private DnsJob dnsJob;

    @Scheduled(fixedDelayString = "${app.interval}")
    public void updateDnsInterval() {
        dnsJob.execute();
    }
}
