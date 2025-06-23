package com.manish.employara.encode;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class EncodePassword {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode("#123@Asd9860_"); // your raw password
        System.out.println(encodedPassword);
    }
}
