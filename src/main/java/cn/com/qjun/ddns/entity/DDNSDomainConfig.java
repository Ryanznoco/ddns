package cn.com.qjun.ddns.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

/**
 * DDNS域名配置
 *
 * @author RenQiang
 * @date 2018/7/16
 */
@Getter
@Setter
@Entity
@Table(name = "ddns_domain_config")
public class DDNSDomainConfig {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 域名
     */
    @Column(name = "name", length = 20, nullable = false)
    private String name;
    /**
     * 是否可用
     */
    @Column(name = "available", nullable = false)
    private Boolean available;
    /**
     * 解析记录列表
     */
    @OneToMany(mappedBy = "domain", fetch = FetchType.LAZY)
    private List<DDNSRecordConfig> records;
}
