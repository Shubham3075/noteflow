package com.noteflow.service;

import com.noteflow.dto.AuthDto;
import com.noteflow.dto.UserDto;
import com.noteflow.entity.OtpStore;
import com.noteflow.entity.User;
import com.noteflow.repository.OtpRepository;
import com.noteflow.repository.UserRepository;
import com.noteflow.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

@Service
public class AuthService {

    @Autowired private UserRepository userRepository;
    @Autowired private OtpRepository otpRepository;
    @Autowired private JwtUtils jwtUtils;
    @Autowired private EmailService emailService;

    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    @Transactional
    public Map<String, Object> sendOtp(String email) {
        email = email.trim().toLowerCase();

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new RuntimeException("Please enter a valid email address");
        }

        // Remove old OTPs for this email
        otpRepository.deleteByEmail(email);

        // Generate 6-digit OTP
        String otp = String.format("%06d", new Random().nextInt(900000) + 100000);

        OtpStore otpStore = OtpStore.builder()
                .email(email)
                .otp(otp)
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .isUsed(false)
                .build();
        otpRepository.save(otpStore);

        Map<String, Object> result = new HashMap<>();

        if (emailService.isEmailEnabled()) {
            // Send REAL email via Gmail SMTP
            boolean sent = emailService.sendOtpEmail(email, otp, "verify your email and log in");
            if (sent) {
                result.put("message", "OTP sent to " + email);
                result.put("emailEnabled", true);
            } else {
                // Fallback to dev mode if email fails
                System.out.println("=== FALLBACK DEV OTP for " + email + ": " + otp + " ===");
                result.put("message", "Email failed — showing OTP for testing");
                result.put("otp", otp);
                result.put("emailEnabled", false);
            }
        } else {
            // DEV MODE: show OTP in response + console
            System.out.println("=== DEV OTP for " + email + ": " + otp + " ===");
            result.put("message", "OTP generated (dev mode — email not configured)");
            result.put("otp", otp);
            result.put("emailEnabled", false);
        }

        return result;
    }

    @Transactional
    public AuthDto.AuthResponse verifyOtp(AuthDto.VerifyOtpRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        OtpStore otpStore = otpRepository
                .findTopByEmailAndIsUsedFalseOrderByCreatedAtDesc(email)
                .orElseThrow(() -> new RuntimeException("OTP not found. Please request a new one."));

        if (LocalDateTime.now().isAfter(otpStore.getExpiresAt())) {
            throw new RuntimeException("OTP has expired. Please request a new one.");
        }

        if (!otpStore.getOtp().equals(request.getOtp())) {
            throw new RuntimeException("Invalid OTP. Please try again.");
        }

        // Mark OTP as used
        otpStore.setIsUsed(true);
        otpRepository.save(otpStore);

        // Get or create user
        boolean isNewUser = !userRepository.existsByEmail(email);
        User user;

        if (isNewUser) {
            String name = (request.getName() != null && !request.getName().isBlank())
                    ? request.getName() : "User";
            user = User.builder()
                    .email(email)
                    .name(name)
                    .role(User.Role.USER)
                    .isActive(true)
                    .build();
            user = userRepository.save(user);
        } else {
            user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            if (!user.getIsActive()) {
                throw new RuntimeException("Your account has been disabled.");
            }
        }

        String token = jwtUtils.generateToken(user.getEmail(), user.getId(), user.getRole().name());

        AuthDto.AuthResponse response = new AuthDto.AuthResponse();
        response.setToken(token);
        response.setUser(UserDto.from(user));
        response.setNewUser(isNewUser);
        return response;
    }
}
