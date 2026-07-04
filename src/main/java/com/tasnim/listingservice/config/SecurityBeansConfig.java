package com.tasnim.listingservice.config;

import com.tasnim.commonlibrary.filters.JwtAuthenticationFilter;
import com.tasnim.commonlibrary.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityBeansConfig {
    @Bean
    public JwtUtil jwtUtil(@Value("${jwt.secret-key}") String secretKey) {
        return new JwtUtil(secretKey);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtUtil jwtUtil) {
        return new JwtAuthenticationFilter(jwtUtil);
    }
}
