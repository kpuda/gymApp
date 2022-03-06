package com.gymPal.gymApp.configuration;

import com.gymPal.gymApp.filter.CustomAuthenticationFilter;
import com.gymPal.gymApp.filter.CustomAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    String[] WHITELIST_SITES = {"/api/register", "/api/newUser", "/api/verifyRegistration", "/api/resendVerificationToken", "/api/hello", "/api/changePassword", "/api/savePassword",
            "/api/resetPassword", "/api/login/**", "/api/refreshToken/**", "/api/token/refresh/**"};
    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    //TODO change enablewebsecurity over filter chain
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManagerBean());
        customAuthenticationFilter.setFilterProcessesUrl("/api/login");
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(STATELESS);
        http.authorizeRequests().antMatchers(WHITELIST_SITES).permitAll()
                .antMatchers("/workout/**").permitAll() //TODO TESTING ONLY
                .antMatchers( "/api/user/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN","ROLE_MODERATOR")
                .antMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN").anyRequest().authenticated();
       /* http.logout()
                .logoutUrl("/logout")
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .deleteCookies() //TODO
                .logoutSuccessUrl("/logout");
        http.formLogin()
                .loginPage("/login")//todo
                .loginProcessingUrl("/todo")//todo
                .defaultSuccessUrl("/homepage.html", true)//todo
                .failureUrl("/failed");//TODO*/

        http.addFilter(customAuthenticationFilter);
        http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
