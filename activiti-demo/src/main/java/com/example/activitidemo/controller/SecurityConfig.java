package com.example.activitidemo.controller;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @author tlj
 * @date 2019/9/5
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http.headers().frameOptions().disable();
        http.csrf().disable().headers().frameOptions().disable()
        .and().authorizeRequests()
                .antMatchers("/").permitAll();
    }
}