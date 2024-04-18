package com.pilog.mdm.config;

import com.pilog.mdm.filters.JwtRequestFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationProvider authProvider;
    private final JwtRequestFilter jwtFilter;
    private final LogoutHandler logoutHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    public static final String [] PUBLIC_URLS= {
            "/login",
            "/v3/api-docs",
            "/v2/api-docs",
            "/swagger-resource/",
            "/swagger-ui.html",
            "/swagger-ui/index.html",
            "/webjars/**",
            "/auth/login",
            "/auth/register",
            "/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.
                csrf()
                .disable()
                .authorizeHttpRequests()
                .anyRequest()
                .authenticated()
                .and().exceptionHandling()
                .authenticationEntryPoint(this.jwtAuthenticationEntryPoint)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        httpSecurity.addFilterBefore(this.jwtFilter, UsernamePasswordAuthenticationFilter.class);

        httpSecurity.authenticationProvider(authProvider);
        DefaultSecurityFilterChain defaultSecurityFilterChain = httpSecurity.build();

        return defaultSecurityFilterChain;
//        httpSecurity.
//                csrf()
//                .disable()
//                .authorizeHttpRequests()
//                .antMatchers(PUBLIC_URLS)
//                .permitAll()
//                .anyRequest().authenticated()
//                .and()
//                .authenticationProvider(authProvider)
//                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
//                .logout()
//                .logoutUrl("/auth/logout")
//                .addLogoutHandler(logoutHandler)
//                .logoutSuccessHandler((request, response, authentication) -> {
//                    SecurityContextHolder.clearContext();
//                })
//                .and()
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .maximumSessions(1);
//        return httpSecurity.build();


    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedOrigins(Arrays.asList("*"));
        corsConfiguration.setAllowedHeaders(Arrays.asList("Origin", "Access-Control-Allow-Origin",
                "Content-Type", "Accept", "Authorization", "Origin,Accept", "X-Requested-With",
                "Access-Control-Request-Method", "Access-Control-Request-Headers"));
        corsConfiguration.setExposedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "Authorization",
                "Access-Control-Allow-Origin", "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"));
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "PUT", "POST", "DELETE", "OPTIONS"));
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(urlBasedCorsConfigurationSource);
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers("/v2/api-docs/**","/v3/api-docs/**",
                "/swagger-ui/**","/swagger-resources/**","/swagger-ui.html","/webjars/**","/auth/login","/auth/register");
    }
}
