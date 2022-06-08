package com.Swap.RegistrationAndFiles.Registration;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path="user/registration")
@AllArgsConstructor
public class RegistrationController
{
    private final RegistrationService registrationService;

    @PostMapping
    public String register(@RequestBody RegistrationRequest req)
    {
        return registrationService.register(req);
    }

    @GetMapping(path="confirm")
    public String confirm(@RequestParam("token") String token)
    {
        return registrationService.confirmToken(token);
    }

}
