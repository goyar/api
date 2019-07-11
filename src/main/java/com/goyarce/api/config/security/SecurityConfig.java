package com.goyarce.api.config.security;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Autowired
    private MySavedRequestAwareAuthenticationSuccessHandler successHandler;

    private SimpleUrlAuthenticationFailureHandler failureHandler = new SimpleUrlAuthenticationFailureHandler();

    @Override
    protected void configure(HttpSecurity http)
            throws Exception{
        http
        .csrf().disable()
        .exceptionHandling()
        .authenticationEntryPoint(restAuthenticationEntryPoint)
        .and()
        .authorizeRequests()
                .antMatchers("/player/**").authenticated()
                .antMatchers("/guardian/**").authenticated()
                .antMatchers(HttpMethod.GET,"/bill/**").authenticated()
                .antMatchers(HttpMethod.GET,"/charge/**").authenticated()
                .antMatchers(HttpMethod.POST, "/bill/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT, "/bill/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/charge/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT, "/charge/**").hasRole("ADMIN")
        .and()
        .formLogin()
        .successHandler(successHandler)
        .failureHandler(failureHandler)
        .and()
        .logout();

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth)
        throws Exception{

        auth.inMemoryAuthentication()
            .withUser("admin")
                .password(encoder().encode("adminPass"))
                    .roles("ADMIN")
        .and()
            .withUser("user")
                .password(encoder().encode("userPass"))
                    .roles(("USER"));
    }

    @Bean
    public PasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }
}
