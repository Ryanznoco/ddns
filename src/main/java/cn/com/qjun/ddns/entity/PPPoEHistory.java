package cn.com.qjun.ddns.entity;

import cn.com.qjun.ddns.support.NetworkInterface;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * 拨号历史
 *
 * @author RenQiang
 * @date 2018/7/16
 */
@Getter
@Setter
@Entity
@Table(name = "pppoe_history")
public class PPPoEHistory {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 网卡
     */
    @Column(name = "network_interface")
    @Enumerated(EnumType.STRING)
    private NetworkInterface networkInterface;
    /**
     * IP地址
     */
    @Column(name = "ip_address", nullable = false, length = 15)
    private String ipAddress;
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
     * 网口当前IP是否是该记录
     */
    @Column(name = "is_current", nullable = false)
    private Boolean current;
}
