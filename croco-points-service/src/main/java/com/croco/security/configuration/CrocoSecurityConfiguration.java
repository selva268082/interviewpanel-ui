package com.croco.security.configuration;

import com.croco.security.exception.CrocoJwtAuthenticationEnrypoint;
import com.croco.security.filter.CrocoInterceptorFilter;
import com.croco.security.service.CrocoUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class CrocoSecurityConfiguration extends WebSecurityConfigurerAdapter{

    @Autowired
    CrocoUserDetailsService userDetailsService;

    @Autowired
    CrocoInterceptorFilter crocoInterceptorFilter;

    @Autowired
    CrocoJwtAuthenticationEnrypoint crocoJwtAuthenticationEnrypoint;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    @Override
    public void configure(HttpSecurity http) throws Exception {
        //Allow the /authenticate url without any authentication

        http.csrf().disable()
                .authorizeRequests().antMatchers("/helloadmin").hasRole("ADMIN")
                .antMatchers("/hellouser").hasAnyRole("USER","ADMIN")
                .antMatchers("/points").hasAnyRole("ADMIN","USER")
                .antMatchers("/token","/newuser/register").permitAll().anyRequest().authenticated()
                .and().exceptionHandling().authenticationEntryPoint(crocoJwtAuthenticationEnrypoint)
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().addFilterBefore(crocoInterceptorFilter, UsernamePasswordAuthenticationFilter.class);
    }



}
