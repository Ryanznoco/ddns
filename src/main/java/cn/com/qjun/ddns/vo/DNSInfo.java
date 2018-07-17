package cn.com.qjun.ddns.vo;

import cn.com.qjun.ddns.support.DNSRecordType;
import lombok.Data;

/**
 * @author Ryan
 * @date 2018/7/17
 */
@Data
public class DNSInfo {
    private String domainName;
    private String rr;
    private String value;
    private DNSRecordType recordType;
}
