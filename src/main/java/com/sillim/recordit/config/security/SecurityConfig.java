package com.sillim.recordit.config.security;

import com.sillim.recordit.config.security.filter.AuthExceptionTranslationFilter;
import com.sillim.recordit.config.security.filter.JwtAuthenticationFilter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher.Builder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final AuthExceptionTranslationFilter authExceptionTranslationFilter;

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return web ->
				web.ignoring().requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**"));
	}

	@Bean
	MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
		return new Builder(introspector);
	}

	@Bean
	public SecurityFilterChain securityFilterChain(
			HttpSecurity httpSecurity, MvcRequestMatcher.Builder mvc) throws Exception {
		return httpSecurity
				.csrf(CsrfConfigurer::disable)
				.authorizeHttpRequests(
						authorize ->
								authorize
										.requestMatchers(
												mvc.pattern("/api/v1/login"),
												mvc.pattern("/api/v1/invite/info/**"))
										.permitAll()
										.requestMatchers(mvc.pattern("api/**"))
										.authenticated()
										.anyRequest()
										.authenticated())
				.sessionManagement(
						configurer ->
								configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(
						jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(authExceptionTranslationFilter, JwtAuthenticationFilter.class)
				.cors(config -> config.configurationSource(corsConfigurationSource()))
				.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(
				List.of("http://localhost:3000", "https://letsrecordit.vercel.app"));
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		config.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}
}
