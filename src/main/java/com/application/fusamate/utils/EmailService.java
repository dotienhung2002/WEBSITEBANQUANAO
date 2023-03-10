package com.application.fusamate.utils;

import com.application.fusamate.configuration.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
@RequiredArgsConstructor
public class EmailService {
   private final JavaMailSender javaMailSender;
    public  boolean sendMail(String to,String text) {
        MimeMessage mail = javaMailSender.createMimeMessage();
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(text, true);
            helper.setTo(to);
//            helper.setSubject("Xác thực quên mật khẩu");
            helper.setFrom(Constants.MY_EMAIL);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new IllegalStateException("failed to send email");
        }
        return true;
    }
}
