package cn.com.qjun.ddns.service;

import cn.com.qjun.ddns.vo.PPPoEInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ryan
 * @date 2018/5/5
 */
@Slf4j
@Service
public class LuyouService {
    private static final String MSG_SUCCESS = "\"Succeess\",\"Success\"";
    private static final String RESULT_CODE_KEY = "Result";
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
        return logged ? getLuyouPPPoEInfo() : null;
    }

    /**
     * 获取路由器网口的拨号信息
     *
     * @return
     */
    private List<PPPoEInfo> getLuyouPPPoEInfo() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode postData = objectMapper.createObjectNode();
        postData.put("func_name", "wan");
        postData.put("action", "show");
        ObjectNode param = objectMapper.createObjectNode();
        param.put("TYPE", "vlan_data,vlan_total");
        param.put("vlan_internet", 2);
        param.put("interface", "wan1");
        postData.set("param", param);
        try {
            String resultString = httpService.post(luyouAddress + URL_LUYOU_CALL, postData.toString());
            JsonNode resultNode = objectMapper.readValue(resultString, JsonNode.class);
            String resultMsg = resultNode.get(RESULT_MSG_KEY).asText();
            if (MSG_SUCCESS.contains(resultMsg)) {
                List<PPPoEInfo> ppPoEInfos = new ArrayList<>();
                resultNode.get("Data").get("vlan_data").iterator().forEachRemaining(node -> {
                    PPPoEInfo ppPoEInfo = new PPPoEInfo();
                    ppPoEInfo.setName(node.get("name").asText());
                    ppPoEInfo.setAccount(node.get("account").asText());
                    ppPoEInfo.setIpAddress(node.get("pppoe_ip_addr").asText());
                    ppPoEInfo.setRemark(node.get("comment").asText());
                    ppPoEInfos.add(ppPoEInfo);
                });
                log.info("获取网口拨号信息成功：{}", ppPoEInfos);
                return ppPoEInfos;
            }
            log.error("请求网口数据失败：{}, {}", resultNode.get(RESULT_CODE_KEY), resultMsg);
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
        return false;
    }

    /**
     * 使用用户名和密码登录路由器
     *
     * @return
     */
    private boolean luyouLogin() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode postData = objectMapper.createObjectNode();
        postData.put("username", luyouUserName);
        postData.put("passwd", luyouPassword);
        String postString = postData.toString();
        try {
            String loginResult = httpService.post(luyouAddress + URL_LUYOU_LOGIN, postString);
            ObjectNode resultNode = objectMapper.readValue(loginResult, ObjectNode.class);
            String resultMsg = resultNode.get(RESULT_MSG_KEY).toString();
            if (MSG_SUCCESS.contains(resultMsg)) {
                log.info("登录路由器成功：{}", luyouUserName);
                return true;
            }
            log.error("登录路由器失败：{}, {}", resultNode.get(RESULT_CODE_KEY), resultMsg);
        } catch (Exception e) {
            log.error("请求失败：" + e.getMessage(), e);
        }
        return false;
    }
}
