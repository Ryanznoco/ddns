package cn.com.qjun.ddns.config;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Ryan
 * @date 2018/5/5
 */
@Configuration
public class AliyunConfig {
    @Value("${app.aliyun.access-key-id}")
    private String accessKeyId;
    @Value("${app.aliyun.access-key-secret}")
    private String accessKeySecret;
    @Value("${app.aliyun.region-id}")
    private String regionId;

    @Bean
    public IAcsClient iAcsClient() {
        IClientProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
        return new DefaultAcsClient(profile);
    }
}
