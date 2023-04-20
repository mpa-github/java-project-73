package hexlet.code.security;

import hexlet.code.exception.handler.CustomAccessDeniedHandler;
import hexlet.code.exception.handler.CustomAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Configuration
@EnableWebSecurity
// To use @PreAuthorize("SpEL — Spring Expression Language")
// @PreAuthorize throws AccessDeniedException
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    private final JWTAuthenticationFilter jwtFilter;
    private final AccessDeniedHandler customAccessDeniedHandler;
    private final AuthenticationEntryPoint customAuthenticationEntryPoint;

    private final RequestMatcher publicPaths = new OrRequestMatcher(
        new AntPathRequestMatcher("/api/users", GET.name()),
        new AntPathRequestMatcher("/api/users", POST.name()),
        new AntPathRequestMatcher("/api/login", POST.name()),
        new AntPathRequestMatcher("/api/statuses", GET.name()),
        new AntPathRequestMatcher("/api/statuses/{id}", HttpMethod.GET.name()),
        new NegatedRequestMatcher(new AntPathRequestMatcher("/api/**"))
    );

    public WebSecurityConfig(JWTAuthenticationFilter jwtFilter,
                             CustomAccessDeniedHandler customAccessDeniedHandler,
                             CustomAuthenticationEntryPoint customAuthenticationEntryPoint) {
        this.jwtFilter = jwtFilter;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
    }

    @Bean // Replace DefaultSecurityFilterChain
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()/*.headers().frameOptions().disable()*/
            .httpBasic().disable()
            .formLogin().disable()
            .logout().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authorizeRequests()
            .requestMatchers(publicPaths).permitAll()
            .anyRequest().authenticated();

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        http.exceptionHandling().authenticationEntryPoint(customAuthenticationEntryPoint);
        //http.exceptionHandling().accessDeniedHandler(customAccessDeniedHandler);

        return http.build();
    }

    @Bean // Spring will use this implementation by default
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
