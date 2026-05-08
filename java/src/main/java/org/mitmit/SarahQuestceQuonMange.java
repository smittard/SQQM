package org.mitmit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class SarahQuestceQuonMange {

    public static void main(final String[] args) {
        SpringApplication.run(SarahQuestceQuonMange.class, args);
    }

}