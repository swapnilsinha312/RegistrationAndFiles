package com.Swap.RegistrationAndFiles.Registration;

import org.springframework.stereotype.Service;

import java.util.function.Predicate;

@Service
public class EmailValidator implements Predicate<String>
{
    @Override
    public boolean test(String s) {
//        Validate email here
        return true;
    }
}
