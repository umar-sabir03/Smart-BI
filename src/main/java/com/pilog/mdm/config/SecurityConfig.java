package com.pilog.mdm.config;

//import com.pilog.mdm.filters.JwtRequestFilter;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.DefaultSecurityFilterChain;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.security.web.authentication.logout.LogoutHandler;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import org.springframework.web.filter.CorsFilter;
//
//import java.util.Arrays;
//
//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor
//public class SecurityConfig {
//
//    private final AuthenticationProvider authProvider;
//    private final JwtRequestFilter jwtFilter;
//    private final LogoutHandler logoutHandler;
//    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
//    public static final String [] PUBLIC_URLS= {
//            "/login",
//            "/v3/api-docs",
//            "/v2/api-docs",
//            "/swagger-resource/",
//            "/swagger-ui.html",
//            "/swagger-ui/index.html",
//            "/webjars/**",
//            "/auth/login",
//            "/auth/register",
//            "/**"
//    };
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
//        httpSecurity.cors().and().
//                csrf()
//                .disable()
//                .authorizeHttpRequests()
//                .anyRequest()
//                .authenticated()
//                .and().exceptionHandling()
//                .authenticationEntryPoint(this.jwtAuthenticationEntryPoint)
//                .and()
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//
//        httpSecurity.addFilterBefore(this.jwtFilter, UsernamePasswordAuthenticationFilter.class);
//
//        httpSecurity.authenticationProvider(authProvider);
//        DefaultSecurityFilterChain defaultSecurityFilterChain = httpSecurity.build();
//
//        return defaultSecurityFilterChain;
////        httpSecurity.
////                csrf()
////                .disable()
////                .authorizeHttpRequests()
////                .antMatchers(PUBLIC_URLS)
////                .permitAll()
////                .anyRequest().authenticated()
////                .and()
////                .authenticationProvider(authProvider)
////                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
////                .logout()
////                .logoutUrl("/auth/logout")
////                .addLogoutHandler(logoutHandler)
////                .logoutSuccessHandler((request, response, authentication) -> {
////                    SecurityContextHolder.clearContext();
////                })
////                .and()
////                .sessionManagement()
////                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
////                .maximumSessions(1);
////        return httpSecurity.build();
//
//
//    }
//
//    @Bean
//    CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(Arrays.asList("*"));
//        configuration.setAllowedMethods(Arrays.asList("*"));
//        configuration.setAllowedHeaders(Arrays.asList("*"));
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }
//
//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return (web) -> web.ignoring().antMatchers("/v2/api-docs/**","/v3/api-docs/**",
//                "/swagger-ui/**","/swagger-resources/**","/swagger-ui.html","/webjars/**","/auth/login","/auth/register","/charts/**");
//    }
//}
import com.pilog.mdm.filters.JwtRequestFilter;
import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer.AuthorizedUrl;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final AuthenticationProvider authProvider;
    private final JwtRequestFilter jwtFilter;
    private final LogoutHandler logoutHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        ((HttpSecurity)((HttpSecurity)((AuthorizedUrl)((AuthorizedUrl)((HttpSecurity)((HttpSecurity)httpSecurity.cors().and()).csrf().disable()).authorizeRequests().antMatchers(new String[]{"/**"})).permitAll().anyRequest()).authenticated().and()).authenticationProvider(this.authProvider).addFilterBefore(this.jwtFilter, UsernamePasswordAuthenticationFilter.class).logout().logoutUrl("/auth/logout").addLogoutHandler(this.logoutHandler).logoutSuccessHandler((request, response, authentication) -> {
            SecurityContextHolder.clearContext();
        }).and()).sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).maximumSessions(1);
        return (SecurityFilterChain)httpSecurity.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    public SecurityConfig(final AuthenticationProvider authProvider, final JwtRequestFilter jwtFilter, final LogoutHandler logoutHandler) {
        this.authProvider = authProvider;
        this.jwtFilter = jwtFilter;
        this.logoutHandler = logoutHandler;
    }
}