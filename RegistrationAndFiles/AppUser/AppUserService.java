package com.Swap.RegistrationAndFiles.AppUser;

import com.Swap.RegistrationAndFiles.Registration.Token.ConfirmationToken;
import com.Swap.RegistrationAndFiles.Registration.Token.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService
{
    private static final String USER_NOT_FOUND="User with email %s not found";
    private final BCryptPasswordEncoder passwordEncoder;
    private final AppUserRepo appUserRepo;
    private final ConfirmationTokenService confirmationTokenService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException
    {
        return appUserRepo.findByEmail(email)
                .orElseThrow(()->
                        new UsernameNotFoundException(
                                String.format(USER_NOT_FOUND,email)));
    }

    public String signUpUser(AppUser appUser)
    {
//        boolean presentFlag=false;
        Optional<AppUser> userOptional=appUserRepo.findByEmail(appUser.getEmail());
        boolean isAlreadyPresent = userOptional.isPresent();

        if(isAlreadyPresent)
        {
            AppUser user=userOptional.get();

//            String encodedPassword = passwordEncoder.encode(appUser.getPassword());
//            appUser.setPassword(encodedPassword);

            if(!user.isSame(appUser))
                throw new IllegalStateException("User with this email already exists");

            if(appUser.isEnabled())
                throw new IllegalStateException("This email is already confirmed");
//            presentFlag=true;
            appUser=user;
        }

        if(!isAlreadyPresent)
        {
            appUserRepo.save(appUser);
            String encodedPassword = passwordEncoder.encode(appUser.getPassword());
            appUser.setPassword(encodedPassword);
        }

        String token= UUID.randomUUID().toString();
        ConfirmationToken confirmationToken=new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                appUser
        );

        confirmationTokenService.saveConfirmationToken(confirmationToken);
//        return "It works";
        return token;
    }

    public int enableAppUser(String email) {
        return appUserRepo.enableAppUser(email);
    }

}
