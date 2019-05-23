package com.portal.gateway.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
    private JwtTokenProvider jwtTokenProvider;
	
	@Autowired
	private RestAccessDeniedHandler accessDeniedHandler;
	
	@Autowired
	private RestAuthenticationEntryPoint unauthorizedHandler;
	
	@Override
    protected void configure(HttpSecurity http) throws Exception {
	
		// Disable CSRF (cross site request forgery)
        http.cors().and().csrf().disable();

        // No session will be created or used by spring security
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Entry points
        http.authorizeRequests()
                .antMatchers("/**/signin/**").permitAll()
                .antMatchers("/**/register/**").permitAll()
                .antMatchers("/**/actuator/**").permitAll()
                .antMatchers("/**/getadmin/**").hasAnyAuthority("ROLE_ADMIN")
                .antMatchers("/**/getstudent/**").hasAnyAuthority("ROLE_SUPERADMIN")
        		.anyRequest().authenticated();
        
        // If a user try to access a resource without having enough permissions
        http.exceptionHandling().accessDeniedHandler(accessDeniedHandler).authenticationEntryPoint(unauthorizedHandler);
        
        // Apply JWT
        http.apply(new JwtTokenFilterConfigurer(jwtTokenProvider));
	}
	
	@Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/*/")//
                .antMatchers("**/actuator/**")//
                .antMatchers(HttpMethod.OPTIONS, "/**"); // Request type options should be allowed.
    }
	
	@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager customAuthenticationManager() throws Exception {
        return authenticationManager();
    }
	
}
