package cn.com.qjun.ddns.support;

import lombok.Getter;

/**
 * @author RenQiang
 * @date 2018/7/16
 */
@Getter
public enum NetworkInterface {
    ASDL1("asdl1"),
    ASDL2("asdl2"),
    ASDL3("asdl3");

    private String name;

    NetworkInterface(String name) {
        this.name = name;
    }
}
