package com.Swap.RegistrationAndFiles.Email;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@AllArgsConstructor
public class EmailService implements EmailSender
{

    private final JavaMailSender emailSender;
    private final static Logger LOGGER= LoggerFactory.getLogger(EmailService.class);

    @Override
    @Async
    public void send(String to, String email)
    {
        try
        {
            MimeMessage mimeMessage=emailSender.createMimeMessage();
            MimeMessageHelper help=new MimeMessageHelper(mimeMessage,"utf-8");
            help.setText(email,true);
            help.setTo(to);
            help.setSubject("Confirm your email");
            help.setFrom("mail@swapJava.com");
            emailSender.send(mimeMessage);
        }
        catch (MessagingException e)
        {
            LOGGER.error("Failed to send mail",e);
            throw new IllegalStateException("Failed to send email");
        }

    }
}
