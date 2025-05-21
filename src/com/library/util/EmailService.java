package com.library.util;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
import java.sql.*;

public class EmailService {
    private static final String FROM_EMAIL = "library.system@yourdomain.com";
    private static String smtpHost;
    private static String smtpPort;
    private static String smtpUsername;
    private static String smtpPassword;
    
    static {
        loadEmailSettings();
    }
    
    private static void loadEmailSettings() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT setting_value FROM settings WHERE setting_key LIKE 'smtp_%'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                String key = rs.getString("setting_key");
                String value = rs.getString("setting_value");
                
                switch (key) {
                    case "smtp_host":
                        smtpHost = value;
                        break;
                    case "smtp_port":
                        smtpPort = value;
                        break;
                    case "smtp_username":
                        smtpUsername = value;
                        break;
                    case "smtp_password":
                        smtpPassword = value;
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void sendOverdueNotification(String toEmail, String studentName, String bookTitle, String dueDate) {
        String subject = "Library Book Overdue Notice";
        String message = String.format(
            "Dear %s,\n\n" +
            "This is a reminder that the following book is overdue:\n\n" +
            "Book: %s\n" +
            "Due Date: %s\n\n" +
            "Please return the book as soon as possible to avoid additional fines.\n\n" +
            "Best regards,\n" +
            "Library Management System",
            studentName, bookTitle, dueDate
        );
        
        sendEmail(toEmail, subject, message);
    }
    
    public static void sendBorrowConfirmation(String toEmail, String studentName, String bookTitle, String dueDate) {
        String subject = "Library Book Borrowed Successfully";
        String message = String.format(
            "Dear %s,\n\n" +
            "You have successfully borrowed the following book:\n\n" +
            "Book: %s\n" +
            "Due Date: %s\n\n" +
            "Please return the book by the due date to avoid fines.\n\n" +
            "Best regards,\n" +
            "Library Management System",
            studentName, bookTitle, dueDate
        );
        
        sendEmail(toEmail, subject, message);
    }
    
    private static void sendEmail(String toEmail, String subject, String message) {
        if (!isEmailEnabled()) {
            return;
        }
        
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", smtpPort);
        
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(smtpUsername, smtpPassword);
            }
        });
        
        try {
            Message mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(FROM_EMAIL));
            mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            mimeMessage.setSubject(subject);
            mimeMessage.setText(message);
            
            Transport.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
    
    private static boolean isEmailEnabled() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT setting_value FROM settings WHERE setting_key = 'email_notifications'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            if (rs.next()) {
                return "true".equals(rs.getString("setting_value"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
} 