package cn.com.qjun.ddns.service;

import cn.com.qjun.ddns.entity.PPPoEHistory;
import cn.com.qjun.ddns.repository.PPPoEHistoryRepository;
import cn.com.qjun.ddns.support.NetworkInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author RenQiang
 * @date 2018/7/17
 */
@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class PPPoEHistoryService {
    @Autowired
    private PPPoEHistoryRepository ppPoEHistoryRepository;

    @Transactional(rollbackFor = Exception.class)
    public PPPoEHistory saveNew(NetworkInterface ni, String ip) {
        PPPoEHistory ppPoEHistory = new PPPoEHistory();
        ppPoEHistory.setNetworkInterface(ni);
        ppPoEHistory.setIpAddress(ip);
        ppPoEHistory.setStartTime(new Date());
        ppPoEHistory.setCurrent(true);
        return ppPoEHistoryRepository.save(ppPoEHistory);
    }

    @Transactional(rollbackFor = Exception.class)
    public PPPoEHistory saveAsHistory(PPPoEHistory ppPoEHistory) {
        ppPoEHistory.setCurrent(false);
        ppPoEHistory.setEndTime(new Date());
        return ppPoEHistoryRepository.save(ppPoEHistory);
    }

    public Map<NetworkInterface, PPPoEHistory> findCurrentByInterface(Collection<NetworkInterface> networkInterfaces) {
        List<PPPoEHistory> ppPoEHistoryList = ppPoEHistoryRepository.findByCurrentAndNetworkInterfaceIn(true, networkInterfaces);
        Map<NetworkInterface, PPPoEHistory> map = new HashMap<>(16);
        for (PPPoEHistory ppPoEHistory :
                ppPoEHistoryList) {
            map.put(ppPoEHistory.getNetworkInterface(), ppPoEHistory);
        }
        return map;
    }
}
