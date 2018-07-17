package cn.com.qjun.ddns.service;

import cn.com.qjun.ddns.vo.PPPoEInfo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ryan
 * @date 2018/5/5
 */
@Slf4j
@Service
public class LuyouService {
    private static final String MSG_SUCCESS = "\"Succeess\",\"Success\"";
    private static final String RESULT_MSG_KEY = "ErrMsg";
    private static final String URL_LUYOU_LOGIN = "/Action/login";
    private static final String URL_LUYOU_CALL = "/Action/call";

    @Autowired
    private HttpService httpService;
    @Value("${app.luyou.address}")
    private String luyouAddress;
    @Value("${app.luyou.username}")
    private String luyouUserName;
    @Value("${app.luyou.passwd}")
    private String luyouPassword;
    private boolean logged = false;

    public List<PPPoEInfo> getAllPPPoEInfo() {
        if (!logged || !checkLogin()) {
            logged = luyouLogin();
        }
        return logged ? getLuyouPPPoEInfo() : new ArrayList<>();
    }

    /**
     * 获取路由器网口的拨号信息
     *
     * @return
     */
    private List<PPPoEInfo> getLuyouPPPoEInfo() {
        JSONObject postData = new JSONObject();
        postData.put("func_name", "wan");
        postData.put("action", "show");
        JSONObject param = new JSONObject();
        param.put("TYPE", "vlan_data,vlan_total");
        param.put("vlan_internet", 2);
        param.put("interface", "wan1");
        postData.put("param", param);
        try {
            String resultString = httpService.post(luyouAddress + URL_LUYOU_CALL, postData.toString());
            JSONObject resultData = JSON.parseObject(resultString);
            String resultMsg = resultData.getString(RESULT_MSG_KEY);
            if (MSG_SUCCESS.contains(resultMsg)) {
                List<PPPoEInfo> ppPoEInfos = new ArrayList<>();
                resultData.getJSONObject("Data").getJSONArray("vlan_data").iterator().forEachRemaining(node -> {
                    JSONObject item = (JSONObject) node;
                    PPPoEInfo ppPoEInfo = new PPPoEInfo();
                    ppPoEInfo.setName(item.getString("vlan_name"));
                    ppPoEInfo.setAccount(item.getString("username"));
                    ppPoEInfo.setIpAddress(item.getString("pppoe_ip_addr"));
                    ppPoEInfo.setRemark(item.getString("comment"));
                    ppPoEInfos.add(ppPoEInfo);
                });
                log.info("获取网口拨号信息成功：{}", ppPoEInfos);
                return ppPoEInfos;
            }
            log.error("请求网口数据失败：{}", resultString);
        } catch (Exception e) {
            log.error("请求失败：" + e.getMessage(), e);
        }
        return new ArrayList<>();
    }

    /**
     * 检查登录是否仍有效
     *
     * @return
     */
    private boolean checkLogin() {
        JSONObject postData = JSON.parseObject("{\"func_name\":\"dhcp_server\",\"action\":\"show\",\"param\":{\"TYPE\":\"dhcp_available_num\"}}");
        try {
            String resultString = httpService.post(luyouAddress + URL_LUYOU_CALL, postData.toString());
            JSONObject resultData = JSON.parseObject(resultString);
            String resultMsg = resultData.getString(RESULT_MSG_KEY);
            if (MSG_SUCCESS.contains(resultMsg)) {
                log.info("登录验证成功");
                return true;
            }
            log.error("登录验证失败：{}", resultString);
        } catch (Exception e) {
            log.error("请求失败：" + e.getMessage(), e);
        }
        return false;
    }

    /**
     * 使用用户名和密码登录路由器
     *
     * @return
     */
    private boolean luyouLogin() {
        JSONObject postData = new JSONObject();
        postData.put("username", luyouUserName);
        postData.put("passwd", luyouPassword);
        String postString = postData.toString();
        try {
            String loginResult = httpService.post(luyouAddress + URL_LUYOU_LOGIN, postString);
            JSONObject resultData = JSON.parseObject(loginResult);
            String resultMsg = resultData.getString(RESULT_MSG_KEY);
            if (MSG_SUCCESS.contains(resultMsg)) {
                log.info("登录路由器成功：{}", luyouUserName);
                return true;
            }
            log.error("登录路由器失败：{}", loginResult);
        } catch (Exception e) {
            log.error("请求失败：" + e.getMessage(), e);
        }
        return false;
    }
}
