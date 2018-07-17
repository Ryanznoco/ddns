package cn.com.qjun.ddns.service;

import cn.com.qjun.ddns.entity.PPPoEHistory;
import cn.com.qjun.ddns.repository.PPPoEHistoryRepository;
import cn.com.qjun.ddns.support.NetworkInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author RenQiang
 * @date 2018/7/17
 */
@Service
@Transactional(readOnly = true)
public class PPPoEHistoryService {
    @Autowired
    private PPPoEHistoryRepository ppPoEHistoryRepository;

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
