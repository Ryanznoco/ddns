package cn.com.qjun.ddns.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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

    public Map<String, String> getPublicIps() {
        if (!logged && luyouLogin()) {
            logged = true;
        }
        if (logged) {
            return getIpFromLuyou();
        }
        return null;
    }

    private Map<String, String> getIpFromLuyou() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode postData = objectMapper.createObjectNode();
        postData.put("func_name", "wan");
        postData.put("action", "show");
        ObjectNode param = objectMapper.createObjectNode();
        param.put("TYPE", "vlan_data,vlan_total");
        param.put("vlan_internet", 2);
        param.put("interface", "wan1");
        postData.put("param", param);
        try {
            String resultString = httpService.post(luyouAddress + URL_LUYOU_CALL, postData.toString());
            JsonNode resultNode = objectMapper.readValue(resultString, JsonNode.class);
            String resultMsg = resultNode.get(RESULT_MSG_KEY).asText();
            if (MSG_SUCCESS.contains(resultMsg)) {
                Map<String, String> ips = new HashMap<>();
                resultNode.get("Data").get("vlan_data").iterator().forEachRemaining(node -> {
                    ips.put(node.get("comment").asText(), node.get("pppoe_ip_addr").asText());
                });
                return ips;
            }
            log.error("请求网口数据失败：{}, {}", resultNode.get(RESULT_CODE_KEY), resultMsg);
        } catch (Exception e) {
            log.error("请求失败：{}", e.getMessage());
        }
        return null;
    }

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
                return true;
            }
            log.error("登录路由器失败：{}, {}", resultNode.get(RESULT_CODE_KEY), resultMsg);
        } catch (Exception e) {
            log.error("请求失败：{}", e.getMessage());
        }
        return false;
    }
}
