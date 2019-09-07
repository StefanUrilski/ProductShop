package com.softuni.productshop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

import static com.softuni.productshop.common.Constants.*;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApplicationSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .csrf()
                .csrfTokenRepository(csrfTokenRepository())
                .and()
                .authorizeRequests()
                .mvcMatchers("/css/**", "/js/**").permitAll()
                .mvcMatchers("/", "/users/login", "/users/register").anonymous()
                .mvcMatchers("/home", "/logout", "/users/profile", "/viruses/show").authenticated()
                .mvcMatchers("/viruses/add", "/viruses/edit", "/viruses/delete")
                .hasAnyAuthority(ROLE_MODERATOR, ROLE_ADMIN, ROLE_ROOT)
                .mvcMatchers("/users/all").hasAnyAuthority(ROLE_ADMIN, ROLE_ROOT)
                .and()
                .formLogin()
                .loginPage("/users/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .defaultSuccessUrl("/users/profile")
                .and()
                .logout()
                .and()
                .exceptionHandling()
                .accessDeniedPage("/unauthorized")
        ;
    }

    private CsrfTokenRepository csrfTokenRepository() {
        HttpSessionCsrfTokenRepository repository =
                new HttpSessionCsrfTokenRepository();
        repository.setSessionAttributeName("_csrf");
        return repository;
    }
}
