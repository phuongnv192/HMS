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
        send(recipientEmail, subject, content.toString(), false);
    }

    public void sendMailUpdatingStatusOfSchedule(String recipientEmail,
                                                 String hostName,
                                                 String hostAddress,
                                                 String hostPhone,
                                                 String date,
                                                 String status) {
        String subject = "Notification for change of schedule status";
        String content = "You have a booking cleaning schedule that recently have been updated status of process.<br>" +
                "Host name: <b>" + hostName + "</b><br>" +
                "Host address: <b>" + hostAddress + "</b><br>" +
                "Host phone: <b>" + hostPhone + "</b><br>" +
                "Current status of schedule on " + date + " - <b>" + status + "</b>";
        send(recipientEmail, subject, content, true);
    }

    public void send(String recipientEmail, String subject, String content, boolean isHtml) {

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
            
            if (isHtml) {
                message.setContent(content, "text/html; charset=UTF-8");
            } else {
                message.setText(content);
            }

            // Send the email
            Transport.send(message);

            log.info("Email sent successfully.");

        } catch (Exception e) {
            log.error("Error sending email: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
