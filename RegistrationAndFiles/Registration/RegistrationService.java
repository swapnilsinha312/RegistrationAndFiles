package com.Swap.RegistrationAndFiles.Registration;

import com.Swap.RegistrationAndFiles.AppUser.AppUser;
import com.Swap.RegistrationAndFiles.AppUser.AppUserRole;
import com.Swap.RegistrationAndFiles.AppUser.AppUserService;
import com.Swap.RegistrationAndFiles.Email.EmailSender;
import com.Swap.RegistrationAndFiles.Registration.Token.ConfirmationToken;
import com.Swap.RegistrationAndFiles.Registration.Token.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class RegistrationService
{
    private final EmailValidator emailValidator;
    private final AppUserService appUserService;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailSender emailSender;
//    private final AppUserRole appUserRole;


    public String register(RegistrationRequest req)
    {
        boolean isValid=emailValidator.test(req.getEmail());
        if(!isValid)
            throw new IllegalStateException("Invalid Email");

        String token= appUserService.signUpUser(new AppUser(
                    req.getFirstName(),
                    req.getLastName(),
                    req.getEmail(),
                    req.getPassword(),
                    AppUserRole.USER
        ));

        String link="http://localhost:8080/user/registration/confirm?token="+token;

        emailSender.send(req.getEmail(),buildEmail(req.getFirstName(),link));

        return token;

    }

    @Transactional
    public String confirmToken(String token)
    {
        ConfirmationToken confirmationToken=confirmationTokenService
                .getToken(token)
                .orElseThrow(()->
                        new IllegalStateException("Token not found"));

        if(confirmationToken.getConfirmedAt()!=null)
            throw new IllegalStateException("Email already confirmed");

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now()))
            throw new IllegalStateException("token expired");


        confirmationTokenService.setConfirmedAt(token);
        appUserService.enableAppUser(
                confirmationToken.getAppUser().getEmail());
        return "confirmed";
    }

    public String buildEmail(String name,String link)
    {
        return "<h1>Email</h1>" +
                    "<h2>Click to confirm</h2>" +
                    "<a href="+link+">Activate Now</a>"
                ;
    }

}
