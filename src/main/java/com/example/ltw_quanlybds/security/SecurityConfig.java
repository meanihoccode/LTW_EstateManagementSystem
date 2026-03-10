package com.example.ltw_quanlybds.security;

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

/**
 * Cấu hình Spring Security:
 * - Ai được vào trang nào
 * - Trang login ở đâu
 * - Trang logout ở đâu
 *
 * Role trong DB (cột quyen_han):
 * - "Admin"    → ROLE_Admin
 * - "Quản lý"  → ROLE_Quản lý
 * - "Nhân viên" → ROLE_Nhân viên
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // Disable CSRF để API hoạt động

            .authorizeHttpRequests(auth -> auth
                // ✅ Public - Không cần đăng nhập
                .requestMatchers("/", "/css/**", "/js/**", "/img/**").permitAll()

                // ✅ Chỉ Admin mới vào được
                .requestMatchers("/staff", "/accounts").hasRole("Admin")
                .requestMatchers("/api/staffs/**").hasRole("Admin")
                .requestMatchers("/api/accounts/**").hasRole("Admin")

                // ✅ Admin + Quản lý vào được các trang (Nhân viên cũng xem được nhưng hạn chế hành động)
                .requestMatchers("/properties", "/owners", "/tenants").hasAnyRole("Admin", "Quản lý", "Nhân viên")

                // ✅ GET request (xem dữ liệu) - Tất cả role đã login được
                .requestMatchers(HttpMethod.GET, "/api/properties/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/owners/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/tenants/**").authenticated()

                // ✅ POST, PUT, DELETE properties
                // POST, DELETE: Chỉ Admin + Quản lý
                // PUT: Admin + Quản lý + Nhân viên (nhân viên chỉ sửa BĐS của mình, kiểm tra ở service)
                .requestMatchers(HttpMethod.POST, "/api/properties/**").hasAnyRole("Admin", "Quản lý")
                .requestMatchers(HttpMethod.PUT, "/api/properties/**").hasAnyRole("Admin", "Quản lý", "Nhân viên")
                .requestMatchers(HttpMethod.DELETE, "/api/properties/**").hasAnyRole("Admin", "Quản lý")

                // ✅ POST, PUT, DELETE owners - Chỉ Admin + Quản lý được
                .requestMatchers(HttpMethod.POST, "/api/owners/**").hasAnyRole("Admin", "Quản lý")
                .requestMatchers(HttpMethod.PUT, "/api/owners/**").hasAnyRole("Admin", "Quản lý")
                .requestMatchers(HttpMethod.DELETE, "/api/owners/**").hasAnyRole("Admin", "Quản lý")

                // ✅ POST, PUT tenants - Admin + Quản lý + Nhân viên được; DELETE - Chỉ Admin + Quản lý
                .requestMatchers(HttpMethod.POST, "/api/tenants/**").hasAnyRole("Admin", "Quản lý", "Nhân viên")
                .requestMatchers(HttpMethod.PUT, "/api/tenants/**").hasAnyRole("Admin", "Quản lý", "Nhân viên")
                .requestMatchers(HttpMethod.DELETE, "/api/tenants/**").hasAnyRole("Admin", "Quản lý")

                // ✅ Tất cả role đã login đều vào được dashboard, contracts, payments
                .requestMatchers("/dashboard", "/contracts", "/payments", "/change-password").authenticated()
                .requestMatchers("/api/contracts/**", "/api/payments/**").authenticated()
                .requestMatchers("/api/me/**").authenticated()

                // ✅ Tất cả request còn lại phải đăng nhập
                .anyRequest().authenticated()
            )

            // ✅ Cấu hình form login
            .formLogin(form -> form
                .loginPage("/")                     // Trang chứa form login
                .loginProcessingUrl("/login")       // URL submit form login (POST)
                .defaultSuccessUrl("/dashboard", true)  // Redirect sau khi login thành công
                .failureUrl("/?error=true")         // Redirect khi login thất bại
                .usernameParameter("username")      // Tên field username trong form
                .passwordParameter("password")      // Tên field password trong form
                .permitAll()
            )

            // ✅ Cấu hình logout
            .logout(logout -> logout
                .logoutUrl("/logout")               // URL logout
                .logoutSuccessUrl("/")              // Redirect sau logout
                .invalidateHttpSession(true)        // Xóa session
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

