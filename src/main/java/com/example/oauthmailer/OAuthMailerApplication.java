package com.example.oauthmailer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@SpringBootApplication
@RestController
public class OAuthMailerApplication extends WebSecurityConfigurerAdapter {

    public OAuthMailerApplication(EmailSenderService emailSenderService) {
        this.emailSenderService = emailSenderService;
    }

    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http
                .authorizeRequests(a -> a
                        .antMatchers("/", "/error", "/webjars/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .csrf(c -> c
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                )
                .logout(l -> l
                        .logoutSuccessUrl("/").permitAll()
                )
                .oauth2Login();
        // @formatter:on
    }

    @Autowired
    private EmailSenderService emailSenderService;

    public void EmailSenderController(EmailSenderService emailSenderService) {
        this.emailSenderService = emailSenderService;
    }

    @Autowired
    UserRepository userRepository;

    @GetMapping("/user")
    public String user(@AuthenticationPrincipal OAuth2User principal) {
        Map<String, Object> _user = Collections.singletonMap("name", principal.getAttribute("name"));
        String name = principal.getAttribute("name");
        int id = principal.getAttribute("id");

        boolean isMailSent = false;

        Optional<UserModel> savedUser = userRepository.findById(id);
        if (savedUser.isPresent()) {
            isMailSent = savedUser.get().isRegisMailSent();
        }

        if (!isMailSent) {
            String email = principal.getAttribute("email");
            String username = principal.getAttribute("login");

            UserModel user = new UserModel();
            user.setId(id);
            user.setUsername(username);
            user.setEmail(email);
            user.setFull_name(name);

            userRepository.save(user);
            System.out.println("User deets saved to MySQL");
            //Send mail
            new Thread(() -> {
                emailSenderService.sendEmail(email,"Thanks for Signing up to OAuthMailer!", "Registration Confirmed");

                user.setRegisMailSent(true);
                userRepository.save(user);
            }).start();
        }
        System.out.println(principal);
        return name;
    }

    public static void main(String[] args) {
        SpringApplication.run(OAuthMailerApplication.class, args);
    }

}
