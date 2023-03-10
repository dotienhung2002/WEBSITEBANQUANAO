package com.application.fusamate.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class JavaEmailSenderConfig {
//    spring.mail.host=smtp.gmail.com
//    spring.mail.port= 25
//    spring.mail.username= test
//    spring.mail.password= test
//    spring.mail.host=smtp.gmail.com
//    spring.mail.port= 25
//    spring.mail.username= test
//    spring.mail.password= test
//    spring.mail.properties.mail.smtp.auth=true
//    spring.mail.properties.mail.smtp.starttls.enable=true
//    spring.mail.properties.mail.smtp.starttls.required=true
    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername(Constants.MY_EMAIL);
        mailSender.setPassword(Constants.MY_PASSWORD);
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");
        return mailSender;
    }
}
