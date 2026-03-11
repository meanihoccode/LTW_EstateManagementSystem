package com.example.ltw_quanlybds.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomLoginSuccessHandler loginSuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth
                // Public
                .requestMatchers("/", "/css/**", "/js/**", "/img/**").permitAll()
                .requestMatchers("/api/auth/login").permitAll()

                // change-password: user tự đổi mk
                .requestMatchers(HttpMethod.PUT, "/api/auth/accounts/*/change-password").authenticated()

                // Chỉ Admin
                .requestMatchers("/accounts").hasAuthority("ROLE_Admin")
                .requestMatchers("/api/auth/accounts/**").hasAuthority("ROLE_Admin")

                // Admin + Quản lý quản lý nhân viên
                .requestMatchers("/staff").hasAnyAuthority("ROLE_Admin", "ROLE_Quản lý")
                .requestMatchers(HttpMethod.POST, "/api/staffs/**").hasAnyAuthority("ROLE_Admin", "ROLE_Quản lý")
                .requestMatchers(HttpMethod.PUT, "/api/staffs/**").hasAnyAuthority("ROLE_Admin", "ROLE_Quản lý")
                .requestMatchers(HttpMethod.DELETE, "/api/staffs/**").hasAnyAuthority("ROLE_Admin", "ROLE_Quản lý")
                // GET staffs: tất cả role xem được
                .requestMatchers(HttpMethod.GET, "/api/staffs/**").authenticated()

                // Trang - tất cả role xem được
                .requestMatchers("/properties", "/owners", "/tenants").hasAnyAuthority("ROLE_Admin", "ROLE_Quản lý", "ROLE_Nhân viên")

                // GET - tất cả role xem được
                .requestMatchers(HttpMethod.GET, "/api/properties/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/owners/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/tenants/**").authenticated()

                // properties
                .requestMatchers(HttpMethod.POST, "/api/properties/**").hasAnyAuthority("ROLE_Admin", "ROLE_Quản lý")
                .requestMatchers(HttpMethod.PUT, "/api/properties/**").hasAnyAuthority("ROLE_Admin", "ROLE_Quản lý", "ROLE_Nhân viên")
                .requestMatchers(HttpMethod.DELETE, "/api/properties/**").hasAnyAuthority("ROLE_Admin", "ROLE_Quản lý")

                // owners
                .requestMatchers(HttpMethod.POST, "/api/owners/**").hasAnyAuthority("ROLE_Admin", "ROLE_Quản lý")
                .requestMatchers(HttpMethod.PUT, "/api/owners/**").hasAnyAuthority("ROLE_Admin", "ROLE_Quản lý")
                .requestMatchers(HttpMethod.DELETE, "/api/owners/**").hasAnyAuthority("ROLE_Admin", "ROLE_Quản lý")

                // tenants
                .requestMatchers(HttpMethod.POST, "/api/tenants/**").hasAnyAuthority("ROLE_Admin", "ROLE_Quản lý", "ROLE_Nhân viên")
                .requestMatchers(HttpMethod.PUT, "/api/tenants/**").hasAnyAuthority("ROLE_Admin", "ROLE_Quản lý", "ROLE_Nhân viên")
                .requestMatchers(HttpMethod.DELETE, "/api/tenants/**").hasAnyAuthority("ROLE_Admin", "ROLE_Quản lý")

                // Còn lại cần đăng nhập
                .requestMatchers("/dashboard", "/contracts", "/payments", "/change-password").authenticated()
                .requestMatchers("/api/contracts/**", "/api/payments/**").authenticated()
                .requestMatchers("/api/me/**").authenticated()
                .anyRequest().authenticated()
            )

            .formLogin(form -> form
                .loginPage("/")
                .loginProcessingUrl("/login")
                .successHandler(loginSuccessHandler)
                .failureUrl("/?error=true")
                .usernameParameter("username")
                .passwordParameter("password")
                .permitAll()
            )

            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}

