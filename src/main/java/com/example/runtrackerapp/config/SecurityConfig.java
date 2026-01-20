package com.example.runtrackerapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.Customizer;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // disable for APIs
                .authorizeHttpRequests(auth -> auth
                        //ensuring that get is public
                        .requestMatchers(HttpMethod.GET, "/runs").permitAll()

                        //ensuring that all POST requests has role admin
                        .requestMatchers(HttpMethod.POST, "/runs/**").hasRole("ADMIN")

                        //Make DELETE `/runs/{id}` admin-only.
                        .requestMatchers(HttpMethod.DELETE, "/runs/{id}").hasRole("ADMIN")
                        //all requests require authentication
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults()); // basic auth for now

        return http.build();
    }

    //Creating users as we won't be using DB for now
    @Bean
    public UserDetailsService userDetailsService(){
        UserDetails user = User.builder()
                .username("sam")
                .password("{noop}password")
                .roles("USER")
                .build();

        UserDetails admin = User.builder()
                .username("admin")
                .password("{noop}1")
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user,admin);
    }

}