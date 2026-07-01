package com.noteflow.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.email.from.name}")
    private String fromName;

    @Value("${app.email.enabled:false}")
    private boolean emailEnabled;

    public boolean isEmailEnabled() {
        return emailEnabled;
    }

    /**
     * Sends a styled HTML email containing the OTP code.
     * Returns true if sent successfully, false otherwise.
     */
    public boolean sendOtpEmail(String toEmail, String otp, String purpose) {
        if (!emailEnabled) {
            return false;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject("Your NoteFlow Verification Code: " + otp);
            helper.setText(buildHtmlEmail(otp, purpose), true);

            mailSender.send(message);
            System.out.println("✅ OTP email sent to: " + toEmail);
            return true;
        } catch (MessagingException | java.io.UnsupportedEncodingException e) {
            System.err.println("❌ Failed to send OTP email: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("❌ Email send error: " + e.getMessage());
            return false;
        }
    }

    private String buildHtmlEmail(String otp, String purpose) {
        return "<!DOCTYPE html>"
            + "<html><body style='margin:0;padding:0;background:#f4f4f9;font-family:Segoe UI,Arial,sans-serif;'>"
            + "<table width='100%' cellpadding='0' cellspacing='0' style='padding:40px 0;'>"
            + "<tr><td align='center'>"
            + "<table width='420' cellpadding='0' cellspacing='0' style='background:#ffffff;border-radius:16px;overflow:hidden;box-shadow:0 4px 24px rgba(0,0,0,0.08);'>"
            + "<tr><td style='background:linear-gradient(135deg,#6366f1,#8b5cf6);padding:32px;text-align:center;'>"
            + "<div style='width:56px;height:56px;background:rgba(255,255,255,0.2);border-radius:16px;display:inline-flex;align-items:center;justify-content:center;font-size:26px;margin-bottom:12px;'>⚡</div>"
            + "<h1 style='color:#fff;font-size:22px;margin:0;font-weight:800;'>NoteFlow</h1>"
            + "</td></tr>"
            + "<tr><td style='padding:36px 32px;'>"
            + "<p style='color:#1e1b3a;font-size:15px;margin:0 0 8px;'>Hi there,</p>"
            + "<p style='color:#5c5a7a;font-size:14px;line-height:1.6;margin:0 0 24px;'>"
            + "Use the verification code below to " + purpose + ". This code expires in <strong>10 minutes</strong>.</p>"
            + "<div style='background:#f1f0f7;border-radius:12px;padding:20px;text-align:center;margin-bottom:24px;'>"
            + "<span style='font-size:36px;font-weight:800;letter-spacing:8px;color:#6366f1;'>" + otp + "</span>"
            + "</div>"
            + "<p style='color:#9896b8;font-size:12.5px;line-height:1.6;margin:0;'>"
            + "If you didn't request this code, you can safely ignore this email. "
            + "Never share this code with anyone.</p>"
            + "</td></tr>"
            + "<tr><td style='padding:20px 32px;background:#f8f8fc;text-align:center;border-top:1px solid #f0eff8;'>"
            + "<p style='color:#9896b8;font-size:11.5px;margin:0;'>© NoteFlow — Your notes &amp; tasks, always with you</p>"
            + "</td></tr>"
            + "</table>"
            + "</td></tr>"
            + "</table>"
            + "</body></html>";
    }
}
