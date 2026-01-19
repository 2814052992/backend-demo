package com.example.demo.service;

public interface EmailService {
    /**
     * 发送纯文本邮件
     * @param to      收件人邮箱 (比如 "user@qq.com")
     * @param subject 邮件标题
     * @param content 邮件内容
     */
    void sendSimpleMail(String to, String subject, String content);
}
