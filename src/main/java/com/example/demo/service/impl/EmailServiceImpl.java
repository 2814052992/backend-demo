package com.example.demo.service.impl;

import com.example.demo.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    //注入 Spring Boot 的发信工具
    @Autowired
    private JavaMailSender mailSender;

    //从配置文件 application.properties 读取发件人账号
    // 这样做的好处是：以后换邮箱了，不用改代码，只改配置就行
    @Value("${spring.mail.username}")
    private String from;

    @Override
    public void sendSimpleMail(String to, String subject, String content) {
        //创建邮件
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(from);           //发送人
        message.setTo(to);               //接受者
        message.setSubject(subject);     //标题
        message.setText(content);        //正文

        //发送
        mailSender.send(message);

        System.out.println("邮件已发送给：" + to);
    }
}
