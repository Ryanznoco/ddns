package cn.com.qjun.ddns.support;

import lombok.Getter;

/**
 * @author RenQiang
 * @date 2018/7/16
 */
@Getter
public enum DNSRecordType {
    A("A记录", "A");

    private String name;
    private String value;

    DNSRecordType(String name, String value) {
        this.name = name;
        this.value = value;
    }
}
