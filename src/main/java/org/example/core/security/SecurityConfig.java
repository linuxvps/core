package org.example.core.security;

import org.example.core.impl.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // جایگزین جدیدتر برای EnableGlobalMethodSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    // 1. تزریق کردن Success Handler سفارشی خودمان
    @Autowired
    private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    /**
     * این Bean مسئول پردازش اطلاعات کاربر پس از دریافت از گوگل و قبل از ساخت توکن است.
     * اینجا بهترین مکان برای ذخیره یا به‌روزرسانی کاربر در دیتابیس است.
     */
    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        return userRequest -> {
            OAuth2User oauth2User = delegate.loadUser(userRequest);
            // TODO: منطق ذخیره یا به‌روزرسانی کاربر در دیتابیس را اینجا پیاده‌سازی کنید.
            // مثلا: userService.processOAuthPostLogin(oauth2User.getAttribute("email"), ...);
            System.out.println("OAuth2 User Attributes have been processed: " + oauth2User.getAttributes());
            return oauth2User;
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // اجازه دسترسی عمومی به اندپوینت‌های احراز هویت و صفحات استاتیک
                        .requestMatchers(
                                "/login.html", "/authenticate", "/oauth2/**", "/login/oauth2/code/**",
                                "/", "/index.html", "/css/**", "/js/**"
                        ).permitAll()
                        // دسترسی به صفحات مدیریتی فقط برای کاربران با نقش "ADMIN"
                        .requestMatchers(
                                "/dashboard.html",
                                "/create-menu-form.html",
                                "/create-permission.html",
                                "/create-role.html",
                                "/manage-role-permissions.html",
                                "/manage-user-menus.html",
                                "/user-list.html"
                        ).hasRole("ADMIN")
                        // سایر درخواست‌ها باید احراز هویت شده باشند
                        .anyRequest().authenticated()
                )
                // 2. پیکربندی جدید برای ورود با OAuth2
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oauth2UserService())
                        )
                        // 3. به جای defaultSuccessUrl از successHandler استفاده می‌کنیم
                        .successHandler(oAuth2LoginSuccessHandler)
                        // 4. آدرس بازگشت در صورت خطا (به فرانت‌اند)
                        .failureUrl("http://localhost:4200/login?error=oauth_failed")
                )
                .logout(logout -> logout
                        .logoutUrl("/perform_logout")
                        .logoutSuccessHandler((request, response, authentication) ->
                                response.sendRedirect("http://localhost:4200/login?logout=true")
                        )
                        .permitAll()
                );

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}