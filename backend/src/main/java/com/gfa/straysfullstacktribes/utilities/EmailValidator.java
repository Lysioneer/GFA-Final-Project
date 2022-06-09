package com.gfa.straysfullstacktribes.utilities;

import org.springframework.stereotype.Service;

import java.util.function.Predicate;

@Service
public class EmailValidator implements Predicate<String> {

    @Override
    public boolean test(String email) {
        //TODO: Regex to validate email
        return true;
    }
}
