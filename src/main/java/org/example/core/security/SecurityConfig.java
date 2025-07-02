package org.example.core.security;

import org.example.core.impl.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity; // اضافه شد
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy; // اضافه شد
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // اضافه شد

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // برای فعال کردن @PreAuthorize
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService; // این UserDetailsService شماست

    @Autowired
    private JwtRequestFilter jwtRequestFilter; // فیلتر JWT

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                // اجازه دسترسی عمومی به صفحه لاگین و اندپوینت /authenticate
                .antMatchers("/", "/index.html", "/login.html", "/css/**", "/js/**", "/authenticate").permitAll()
                // تمام مسیرهای /api/** نیاز به احراز هویت دارند
                .antMatchers("/api/**").authenticated()
                // تمام مسیرهای فایل‌های استاتیک که برای صفحات ادمین استفاده می‌شوند، نیاز به نقش ADMIN دارند
                .antMatchers(
                        "/create-menu-form.html",
                        "/create-permission.html",
                        "/create-role.html",
                        "/manage-role-permissions.html",
                        "/manage-user-menus.html",
                        "/user-list.html",
                        "/dashboard.html"
                ).hasRole("ADMIN") // این صفحات HTML ادمین هستند
                // اگر dashboard.html هم برای ادمین خاص است
                .antMatchers("/dashboard.html").authenticated() // یا hasRole("ADMIN")
                .anyRequest().authenticated() // هر درخواست دیگری نیاز به احراز هویت دارد
                .and()
                .logout()
                .logoutUrl("/perform_logout")
                .logoutSuccessUrl("/login.html?logout=true")
                .permitAll();

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // This bean remains the same as before
    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder())
                .and().build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}