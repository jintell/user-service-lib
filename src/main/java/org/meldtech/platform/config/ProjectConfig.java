//package org.meldtech.platform.config;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
///**
// * @Author: Josiah Adetayo
// * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
// * @Date: 12/6/23
// */
//@EnableScheduling
//@Configuration
//public class ProjectConfig {
//
//    @Value("${app.password.strength}")
//    private int PASSWORD_STRENGTH;
//
//    @Bean
//    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(PASSWORD_STRENGTH); }
//}
