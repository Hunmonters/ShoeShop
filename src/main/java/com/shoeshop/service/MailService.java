package com.shoeshop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendActivationEmail(String toEmail, String activationLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Kích hoạt tài khoản ShoeShop");
        message.setText("Xin chào,\n\n" +
                "Vui lòng click vào link sau để kích hoạt tài khoản:\n" +
                activationLink + "\n\n" +
                "Trân trọng,\nShoeShop Team");

        mailSender.send(message);
    }

    public void sendResetPasswordEmail(String toEmail, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Đặt lại mật khẩu ShoeShop");
        message.setText("Xin chào,\n\n" +
                "Vui lòng click vào link sau để đặt lại mật khẩu:\n" +
                resetLink + "\n\n" +
                "Trân trọng,\nShoeShop Team");

        mailSender.send(message);
    }

    public void sendOrderConfirmation(String toEmail, Long orderId) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Xác nhận đơn hàng #" + orderId);
        message.setText("Xin chào,\n\n" +
                "Đơn hàng #" + orderId + " của bạn đã được xác nhận.\n" +
                "Chúng tôi sẽ liên hệ với bạn trong thời gian sớm nhất.\n\n" +
                "Trân trọng,\nShoeShop Team");

        mailSender.send(message);
    }
}