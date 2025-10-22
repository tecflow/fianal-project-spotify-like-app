package com.spotify_final_project;

import com.spotify_final_project.service.MailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.*;

class MailServiceTest {

    private JavaMailSender mailSender;
    private MailService mailService;

    @BeforeEach
    void setUp() {
        mailSender = mock(JavaMailSender.class);
        mailService = new MailService(mailSender);
    }

    @Test
    void sendVerificationEmail_ShouldSendEmail() {
        String email = "test@example.com";
        String code = "123456";

        // Call the service method
        mailService.sendVerificationEmail(email, code);

        // Capture the sent message
        verify(mailSender, times(1)).send(argThat((SimpleMailMessage message) ->
                message.getTo()[0].equals(email) &&
                        message.getSubject().equals("Verify your account") &&
                        message.getText().contains(code)
        ));
    }
}
