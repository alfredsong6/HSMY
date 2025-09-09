package com.hsmy.service.impl;

import com.hsmy.config.CommunicationProperties;
import com.hsmy.dto.SmsResult;
import com.hsmy.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * 邮件服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    
    private final CommunicationProperties communicationProperties;
    
    @Override
    public SmsResult sendVerificationCode(String email, String code) {
        try {
            String subject = "【敲敲木鱼】验证码";
            String content = buildVerificationCodeContent(code);
            
            return sendHtmlEmail(email, subject, content);
        } catch (Exception e) {
            log.error("发送验证码邮件异常，邮箱: {}", email, e);
            return SmsResult.failure("SYSTEM_ERROR", "邮件发送异常：" + e.getMessage());
        }
    }
    
    @Override
    public SmsResult sendNotification(String email, String subject, String content) {
        try {
            return sendSimpleEmail(email, subject, content);
        } catch (Exception e) {
            log.error("发送通知邮件异常，邮箱: {}", email, e);
            return SmsResult.failure("SYSTEM_ERROR", "邮件发送异常：" + e.getMessage());
        }
    }
    
    /**
     * 发送简单文本邮件
     */
    private SmsResult sendSimpleEmail(String to, String subject, String content) {
        try {
            CommunicationProperties.EmailConfig emailConfig = communicationProperties.getEmail();
            
            if (!emailConfig.getEnabled()) {
                return SmsResult.failure("EMAIL_DISABLED", "邮件功能未启用");
            }
            
            if (!isEmailConfigValid(emailConfig)) {
                return SmsResult.failure("CONFIG_ERROR", "邮件配置不完整");
            }
            
            JavaMailSender mailSender = createMailSender(emailConfig);
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(emailConfig.getFrom());
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            
            mailSender.send(message);
            
            log.info("邮件发送成功，收件人: {}, 主题: {}", to, subject);
            return SmsResult.success("EMAIL_" + System.currentTimeMillis());
            
        } catch (Exception e) {
            log.error("发送简单邮件失败，收件人: {}", to, e);
            return SmsResult.failure("SEND_ERROR", "邮件发送失败：" + e.getMessage());
        }
    }
    
    /**
     * 发送HTML邮件
     */
    private SmsResult sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            CommunicationProperties.EmailConfig emailConfig = communicationProperties.getEmail();
            
            if (!emailConfig.getEnabled()) {
                return SmsResult.failure("EMAIL_DISABLED", "邮件功能未启用");
            }
            
            if (!isEmailConfigValid(emailConfig)) {
                return SmsResult.failure("CONFIG_ERROR", "邮件配置不完整");
            }
            
            JavaMailSender mailSender = createMailSender(emailConfig);
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(emailConfig.getFrom(), emailConfig.getFromName());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            
            log.info("HTML邮件发送成功，收件人: {}, 主题: {}", to, subject);
            return SmsResult.success("EMAIL_HTML_" + System.currentTimeMillis());
            
        } catch (Exception e) {
            log.error("发送HTML邮件失败，收件人: {}", to, e);
            return SmsResult.failure("SEND_ERROR", "邮件发送失败：" + e.getMessage());
        }
    }
    
    /**
     * 创建邮件发送器
     */
    private JavaMailSender createMailSender(CommunicationProperties.EmailConfig emailConfig) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(emailConfig.getHost());
        mailSender.setPort(emailConfig.getPort());
        mailSender.setUsername(emailConfig.getFrom());
        mailSender.setPassword(emailConfig.getPassword());
        mailSender.setDefaultEncoding("UTF-8");
        
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        
        if (emailConfig.getSsl()) {
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        }
        
        if (emailConfig.getTls()) {
            props.put("mail.smtp.starttls.enable", "true");
        }
        
        return mailSender;
    }
    
    /**
     * 验证邮件配置是否完整
     */
    private boolean isEmailConfigValid(CommunicationProperties.EmailConfig config) {
        return config != null
                && config.getHost() != null && !config.getHost().trim().isEmpty()
                && config.getFrom() != null && !config.getFrom().trim().isEmpty()
                && config.getPassword() != null && !config.getPassword().trim().isEmpty();
    }
    
    /**
     * 构建验证码邮件内容
     */
    private String buildVerificationCodeContent(String code) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<title>验证码</title>" +
                "</head>" +
                "<body style=\"font-family: Arial, sans-serif; background-color: #f5f5f5; padding: 20px;\">" +
                "<div style=\"max-width: 600px; margin: 0 auto; background-color: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);\">" +
                "<div style=\"text-align: center; margin-bottom: 30px;\">" +
                "<h1 style=\"color: #333; margin: 0;\">敲敲木鱼</h1>" +
                "<p style=\"color: #666; margin: 10px 0 0 0;\">验证码邮件</p>" +
                "</div>" +
                "<div style=\"background-color: #f8f9fa; padding: 20px; border-radius: 5px; text-align: center;\">" +
                "<h2 style=\"color: #333; margin: 0 0 10px 0;\">您的验证码是：</h2>" +
                "<div style=\"font-size: 32px; font-weight: bold; color: #007bff; letter-spacing: 5px; margin: 20px 0;\">" + code + "</div>" +
                "<p style=\"color: #666; margin: 0;\">验证码5分钟内有效，请勿泄露给他人</p>" +
                "</div>" +
                "<div style=\"margin-top: 30px; text-align: center; color: #999; font-size: 12px;\">" +
                "<p>此邮件由系统自动发送，请勿回复</p>" +
                "<p>如果您没有进行相关操作，请忽略此邮件</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}