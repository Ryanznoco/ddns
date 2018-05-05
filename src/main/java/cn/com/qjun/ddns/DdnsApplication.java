package cn.com.qjun.ddns;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Ryan
 */
@EnableScheduling
@SpringBootApplication
public class DdnsApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(DdnsApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

    }
}
