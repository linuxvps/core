package org.example.core.security; // Or your security package

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private JwtUtil jwtUtil; // ابزار JWT خود را تزریق کنید

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");

        // با استفاده از ایمیل، توکن JWT تولید کنید
        String token = jwtUtil.generateToken(email);

        // آدرس مقصد در فرانت‌اند انگولار
        String targetUrl = "http://localhost:4200/dashboard";

        // افزودن توکن به عنوان پارامتر کوئری به URL
        String redirectUrl = UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", token)
                .build().toUriString();

        // ریدایرکت کردن کاربر به فرانت‌اند به همراه توکن
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}