package com.module.project.service;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.module.project.util.HMSUtil;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Slf4j
@Service
@NoArgsConstructor
public class MailService {

    @Value("${application.mail.mail}")
    private String senderEmail;
    @Value("${application.mail.password}")
    private String senderPassword;

    public void sendMailVerifyEmail(String recipientEmail, String username) {
        String subject = "Verify email for HMS";
        StringBuilder content = new StringBuilder();
        content.append("Click this link for verify your account:\n");
        content.append("http://localhost:8888/api/v1/auth/verify")
            .append("?username=")
            .append(username);
        send(recipientEmail, subject, content.toString());
    }

    public void send(String recipientEmail, String subject, String content) {

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        // Create a session with the properties and authenticate
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new javax.mail.PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            // Create a MimeMessage object
            Message message = new MimeMessage(session);

            // Set the sender and recipient addresses
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));

            // Set the subject and text of the email
            message.setSubject(subject);
            message.setText(content);

            // Send the email
            Transport.send(message);

            log.info("Email sent successfully.");

        } catch (Exception e) {
            log.error("Error sending email: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
