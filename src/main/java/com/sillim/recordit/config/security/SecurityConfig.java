package com.sillim.recordit.config.security;

import com.sillim.recordit.config.security.filter.AuthExceptionTranslationFilter;
import com.sillim.recordit.config.security.filter.JwtAuthenticationFilter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
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
	private final OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService;
	private final AuthenticationSuccessHandler successHandler;

	@Value("${client.url}")
	private String clientUrl;

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
				.httpBasic(Customizer.withDefaults())
				.oauth2Login(setOAuth2Config())
				.csrf(CsrfConfigurer::disable)
				.authorizeHttpRequests(
						authorize ->
								authorize
										.requestMatchers(
												mvc.pattern("/api/v1/login"),
												mvc.pattern("/api/v1/invite/info/**"),
												mvc.pattern("/api/v1/web-login"),
												mvc.pattern("/actuator/**"))
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

	private Customizer<OAuth2LoginConfigurer<HttpSecurity>> setOAuth2Config() {
		return configurer ->
				configurer
						.authorizationEndpoint(oauth2 -> oauth2.baseUri("/oauth2/authorize"))
						.userInfoEndpoint(endpoint -> endpoint.userService(oAuth2UserService))
						.successHandler(successHandler);
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(List.of("http://localhost:3000", clientUrl));
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		config.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}
}
