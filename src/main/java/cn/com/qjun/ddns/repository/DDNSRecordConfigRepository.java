package cn.com.qjun.ddns.repository;

import cn.com.qjun.ddns.entity.DDNSRecordConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author RenQiang
 * @date 2018/7/17
 */
public interface DDNSRecordConfigRepository extends JpaRepository<DDNSRecordConfig, Long> {

    List<DDNSRecordConfig> findByAvailableAndDomainAvailable(Boolean available, Boolean domainAvailable);
}
