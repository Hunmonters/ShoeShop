package com.shoeshop.service;

import com.shoeshop.entity.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    // 🔹 1. Gửi email kích hoạt tài khoản
    public void sendActivationEmail(String toEmail, String activationLink, String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail.trim());
        message.setSubject("Kích hoạt tài khoản ShoeShop");
        message.setText("Xin chào " + username + ",\n\n"
                + "Cảm ơn bạn đã đăng ký tài khoản tại ShoeShop.\n"
                + "Vui lòng click vào link sau để kích hoạt tài khoản:\n"
                + activationLink + "\n\n"
                + "Trân trọng,\nShoeShop Team");

        mailSender.send(message);
    }

    // 🔹 2. Gửi email đặt lại mật khẩu – kèm thông tin tài khoản
    public void sendResetPasswordEmail(Account account, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(account.getEmail().trim());
        message.setSubject("Đặt lại mật khẩu tài khoản ShoeShop");

        // Lấy thông tin người dùng
        String username = account.getUsername();
        String fullname = account.getFullname() != null ? account.getFullname() : "(Chưa cập nhật)";
        String createdDate = account.getCreateDate() != null
                ? account.getCreateDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                : "(Không xác định)";
        String status = account.getActivated() ? "Đang hoạt động" : "Chưa kích hoạt";

        // Nội dung email
        String body = "Xin chào " + fullname + ",\n\n"
                + "Bạn đã yêu cầu đặt lại mật khẩu cho tài khoản của mình tại ShoeShop.\n\n"
                + "🔹 Thông tin tài khoản của bạn:\n"
                + " - Tên đăng nhập: " + username + "\n"
                + " - Email: " + account.getEmail() + "\n"
                + " - Ngày tạo: " + createdDate + "\n"
                + " - Trạng thái: " + status + "\n\n"
                + "Vui lòng click vào liên kết bên dưới để đặt lại mật khẩu:\n"
                + resetLink + "\n\n"
                + "Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.\n\n"
                + "Trân trọng,\nShoeShop Team";

        message.setText(body);
        mailSender.send(message);
    }

    // 🔹 3. Gửi email xác nhận đơn hàng
    public void sendOrderConfirmation(String toEmail, Long orderId) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail.trim());
        message.setSubject("Xác nhận đơn hàng #" + orderId);
        message.setText("Xin chào,\n\n"
                + "Đơn hàng #" + orderId + " của bạn đã được xác nhận.\n"
                + "Chúng tôi sẽ liên hệ với bạn trong thời gian sớm nhất.\n\n"
                + "Trân trọng,\nShoeShop Team");

        mailSender.send(message);
    }
}
