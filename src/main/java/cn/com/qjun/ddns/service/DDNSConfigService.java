package cn.com.qjun.ddns.service;

import cn.com.qjun.ddns.entity.DDNSRecordConfig;
import cn.com.qjun.ddns.repository.DDNSRecordConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author RenQiang
 * @date 2018/7/17
 */
@Service
@Transactional(readOnly = true)
public class DDNSConfigService {
    @Autowired
    private DDNSRecordConfigRepository recordConfigRepository;

    public List<DDNSRecordConfig> findAllAvailable() {
        return recordConfigRepository.findByAvailableAndDomainAvailable(true, true);
    }
}
