package com.otakumap.global.config;

import com.otakumap.global.security.jwt.properties.JwtProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties({
        JwtProperties.class,
})
@Configuration
public class PropertiesConfig {

}