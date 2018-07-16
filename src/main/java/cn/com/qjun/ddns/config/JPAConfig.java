package cn.com.qjun.ddns.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Created by RenQiang on 2017/8/3.
 */
@Configuration
@EnableJpaRepositories(basePackages = "cn.com.qjun.ddns.repository")
@EntityScan(basePackages = "cn.com.qjun.ddns.entity")
public class JPAConfig {
}
