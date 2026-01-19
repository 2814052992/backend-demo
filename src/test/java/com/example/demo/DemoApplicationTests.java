package com.example.demo;

import com.example.demo.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoApplicationTests {

    @Autowired
    private EmailService emailService;

    @Test
    void testSendMail() {
        // 把这里换成你自己的另一个邮箱，或者发给你自己都行
        // ⚠️ 记得把 QQ 邮箱授权码填好，否则这里会报错
        emailService.sendSimpleMail("y2814052992@2925.com", "测试邮件", "你好！这是来自 Spring Boot 的验证码：8888");
    }
}