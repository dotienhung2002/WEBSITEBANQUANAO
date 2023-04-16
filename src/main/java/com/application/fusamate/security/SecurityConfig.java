package com.application.fusamate.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.application.fusamate.exception.JWTAccessDeniedHandler;
import com.application.fusamate.exception.JwtAuthenticationEntryPoint;
import com.application.fusamate.filter.JwtFilter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final UserDetailsService userDetailsService;
    private final JwtFilter filter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JWTAccessDeniedHandler accessDeniedHandler;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
//    @Bean
//    public FirebaseApp firebaseApp() throws IOException {
//        FileInputStream serviceAccount =
//                new FileInputStream("src/main/resources/serviceAccountKey.json");
//
//        FirebaseOptions options = new FirebaseOptions.Builder()
//                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//                .setDatabaseUrl("https://your-app.firebaseio.com")
//                .setStorageBucket("your-app-70415.appspot.com")
//                .build();
//
//        return FirebaseApp.initializeApp(options);
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
http.cors().and().csrf().disable()
.authorizeRequests()
.antMatchers("/login").permitAll()
.antMatchers("/change-password").permitAll()
.antMatchers("/reset-password").permitAll()
.antMatchers("/category/get-all","/brand/get-all").hasAnyAuthority("employee","admin")

.antMatchers("/employee/**").hasAnyAuthority("admin")
.antMatchers("/customer/**").hasAnyAuthority("admin","employee")
.antMatchers("/product-set/**").hasAnyAuthority("admin")
.antMatchers("/brand/**").hasAnyAuthority("admin")

.antMatchers("/category/**").hasAnyAuthority("admin")
.antMatchers("/madein/**").hasAnyAuthority("admin","employee")
.antMatchers("/size/**").hasAnyAuthority("admin","employee")
.antMatchers("/color/**").hasAnyAuthority("admin","employee")
.antMatchers("/product/**").hasAnyAuthority("admin","employee")
.antMatchers("/promotion/**").hasAnyAuthority("admin")
.antMatchers("/order/**").hasAnyAuthority("admin","employee")
.antMatchers("/stats/**").hasAnyAuthority("admin")
.antMatchers("/voucher/**").hasAnyAuthority("admin")

.antMatchers("/public/**").permitAll()
.anyRequest().authenticated()
.and()
.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)
.and()
.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().exceptionHandling().accessDeniedHandler(accessDeniedHandler).authenticationEntryPoint(jwtAuthenticationEntryPoint);
http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
    }
}