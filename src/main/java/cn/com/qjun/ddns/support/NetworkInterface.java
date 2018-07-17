package cn.com.qjun.ddns.support;

import lombok.Getter;

/**
 * @author RenQiang
 * @date 2018/7/16
 */
@Getter
public enum NetworkInterface {
    WAN1("adsl1"),
    WAN2("adsl2"),
    WAN3("adsl3");

    private String name;

    NetworkInterface(String name) {
        this.name = name;
    }

    public static NetworkInterface nameOf(String name) {
        for (NetworkInterface ni :
                NetworkInterface.values()) {
            if (ni.getName().equals(name)) {
                return ni;
            }
        }
        return null;
    }
}
