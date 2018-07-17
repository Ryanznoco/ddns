package cn.com.qjun.ddns;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Ryan
 */
@EnableScheduling
@SpringBootApplication
public class DdnsApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(DdnsApplication.class);
        application.setBannerMode(Banner.Mode.OFF);
        application.run(args);
    }
}
