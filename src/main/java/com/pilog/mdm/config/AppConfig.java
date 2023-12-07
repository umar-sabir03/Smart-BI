package com.pilog.mdm.config;

import com.pilog.mdm.service.SPersDetailService;
import com.pilog.mdm.utils.InsightsUtils;
import com.pilog.mdm.utils.PilogEncryption;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

private final SPersDetailService spdSer;
    @Bean
    public UserDetailsService userDetailsService(){
        return username -> spdSer.loadUserByUsername(username);
    }
    @Bean
    public AuthenticationProvider authProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setPasswordEncoder(passwordEncoder());//can change to custom enc
        authProvider.setUserDetailsService(userDetailsService());//idk why how
        return authProvider;
    }
    @Bean
    public AuthenticationManager authMgr(AuthenticationConfiguration authConfig) throws Exception {
    return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new PilogEncryption();
    }
    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

    @Bean
    public InsightsUtils insightsUtils() {
        return new InsightsUtils();
    }
}
