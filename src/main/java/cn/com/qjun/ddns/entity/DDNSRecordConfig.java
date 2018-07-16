package cn.com.qjun.ddns.entity;

import cn.com.qjun.ddns.support.DNSRecordType;
import cn.com.qjun.ddns.support.NetworkInterface;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * DDNS解析配置
 *
 * @author RenQiang
 * @date 2018/7/16
 */
@Getter
@Setter
@Entity
@Table(name = "ddns_record_config")
public class DDNSRecordConfig {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 域名
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "domain_id")
    private DDNSDomainConfig domain;
    /**
     * 主机记录
     */
    @Column(name = "rr", length = 20, nullable = false)
    private String rr;
    /**
     * 解析网卡
     */
    @Column(name = "network_interface")
    @Enumerated(EnumType.STRING)
    private NetworkInterface networkInterface;
    /**
     * 记录类型
     */
    @Column(name = "record_type", length = 20, nullable = false)
    @Enumerated(value = EnumType.STRING)
    private DNSRecordType recordType;
    /**
     * 是否可用
     */
    @Column(name = "available", nullable = false)
    private Boolean available;
}
