package com.company.books.backend.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.company.books.backend.filter.JwtReqFilter;

@CrossOrigin(origins = { "http://localhost:4200", "http://dpsk54y4tsoea.cloudfront.net" })
@Configuration
@EnableWebSecurity
public class ConfigSecurity {
	
	@Autowired
	@Lazy
	private JwtReqFilter jwtReqFilter;

	@Bean
	UserDetailsManager userDetailsManager(DataSource dataSource) {
		return new JdbcUserDetailsManager(dataSource);
	}

	/*
	 * @Bean InMemoryUserDetailsManager inmemoryUserDetailsManager() { UserDetails
	 * william =
	 * User.builder().username("william").password("{noop}123").roles("EMPLEADO").
	 * build(); UserDetails andres =
	 * User.builder().username("andres").password("{noop}456").roles("JEFE").build()
	 * ; UserDetails pilar =
	 * User.builder().username("pilar").password("{noop}789").roles("JEFE").build();
	 * 
	 * return new InMemoryUserDetailsManager(william, andres, pilar); }
	 */
	
	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration 
			authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(requests -> {
			requests.requestMatchers(HttpMethod.GET, "/v1/libros").hasRole("EMPLEADO")
					.requestMatchers(HttpMethod.GET, "/v1/libros/**").hasRole("EMPLEADO")
					.requestMatchers(HttpMethod.POST, "/v1/libros").hasRole("JEFE")
					.requestMatchers(HttpMethod.PUT, "/v1/libros/**").hasRole("JEFE")
					.requestMatchers(HttpMethod.DELETE, "/v1/libros/**").hasRole("JEFE")
					//.requestMatchers(HttpMethod.GET, "/v1/categorias").hasRole("EMPLEADO")
					.requestMatchers(HttpMethod.GET, "/v1/categorias/**").hasRole("EMPLEADO")
					.requestMatchers(HttpMethod.POST, "/v1/categorias").hasRole("JEFE")
					.requestMatchers(HttpMethod.PUT, "/v1/categorias/**").hasRole("JEFE")
					.requestMatchers(HttpMethod.DELETE, "/v1/categorias/**").hasRole("JEFE")
					.requestMatchers("/v1/authenticate", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll();
		}).addFilterBefore(jwtReqFilter, UsernamePasswordAuthenticationFilter.class)
		.sessionManagement( (session) -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		);
		http.httpBasic(Customizer.withDefaults());
		return http.build();
	}

}
