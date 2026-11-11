package sandrakorpi.csnfribeloppapi.Configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import sandrakorpi.csnfribeloppapi.Security.JwtAuthenticationFilter;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@Configuration
@EnableWebSecurity
// Utan den här annoteringen ignoreras @PreAuthorize, och vem som helst som är inloggad når admin-endpoints.
@EnableMethodSecurity
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private static final List<String> ALLOWED_HEADERS = List.of(CONTENT_TYPE, AUTHORIZATION, ACCEPT);

    // Swagger. Är avstängt om inte SWAGGER_ENABLED=true, och svarar då 404.
    private static final String[] SWAGGER_WHITELIST = {
            "/swagger-resources/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/webjars/**"
    };

    @Autowired
    public SecurityConfig(
            AuthenticationProvider authenticationProvider,
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity,
                                                   CorsConfigurationSource corsConfigurationSource) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable) // Stateless API med Bearer-token, inga cookies.
                .cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource))
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(SWAGGER_WHITELIST).permitAll();
                    // Bara registrering och inloggning är öppna. Resten under /api/auth, som byte av lösenord, kräver inloggning.
                    auth.requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/signIn").permitAll();
                    auth.anyRequest().authenticated();
                })
                .exceptionHandling(exceptions ->
                        exceptions.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    /**
     * Tillåtna origins kommer från CORS_ALLOWED_ORIGINS (kommaseparerad lista).
     * För en iOS-app som paketerats med Capacitor är origin vanligtvis capacitor://localhost.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${app.cors.allowed-origins}") String allowedOrigins) {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .toList());
        configuration.setAllowedMethods(List.of(
                HttpMethod.GET.name(), HttpMethod.POST.name(), HttpMethod.PUT.name(),
                HttpMethod.DELETE.name(), HttpMethod.OPTIONS.name()));
        configuration.setAllowedHeaders(ALLOWED_HEADERS);
        // Token skickas i Authorization-headern, inte i cookies.
        configuration.setAllowCredentials(false);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
