package cn.com.qjun.ddns.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * 域名DNS记录
 *
 * @author RenQiang
 * @date 2018/7/16
 */
@Getter
@Setter
@Entity
@Table(name = "dns_history")
public class DNSHistory {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 主机记录
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "rr_id")
    private DDNSRecordConfig rr;
    /**
     * 记录值
     */
    @Column(name = "value", length = 15, nullable = false)
    private String value;
    /**
     * 开始时间
     */
    @Column(name = "start_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;
    /**
     * 结束时间
     */
    @Column(name = "end_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endTime;
    /**
     * 主机当前是否正解析到该值
     */
    @Column(name = "is_current", nullable = false)
    private Boolean current;
}
