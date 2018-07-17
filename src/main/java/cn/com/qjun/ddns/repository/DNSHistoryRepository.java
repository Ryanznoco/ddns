package cn.com.qjun.ddns.repository;

import cn.com.qjun.ddns.entity.DNSHistory;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Ryan
 * @date 2018/7/17
 */
public interface DNSHistoryRepository extends JpaRepository<DNSHistory, Long> {

    DNSHistory findByRrIdAndCurrent(Long rrId, Boolean current);
}
