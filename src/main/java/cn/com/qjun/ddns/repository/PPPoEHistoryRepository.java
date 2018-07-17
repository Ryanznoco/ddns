package cn.com.qjun.ddns.repository;

import cn.com.qjun.ddns.entity.PPPoEHistory;
import cn.com.qjun.ddns.support.NetworkInterface;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

/**
 * @author RenQiang
 * @date 2018/7/17
 */
public interface PPPoEHistoryRepository extends JpaRepository<PPPoEHistory, Long> {

    List<PPPoEHistory> findByCurrentAndNetworkInterfaceIn(Boolean current, Collection<NetworkInterface> networkInterfaces);
}
