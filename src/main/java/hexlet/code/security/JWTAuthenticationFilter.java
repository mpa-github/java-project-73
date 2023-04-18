package hexlet.code.security;

import hexlet.code.exception.JWTValidationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private static final String TOKEN_TYPE_NAME = "Bearer";
    private final UserDetailsService userDetailsService;
    private final JWTUtils jwtUtils;

    private final HandlerExceptionResolver resolver;

    private final RequestMatcher ignoredPaths = new OrRequestMatcher(
        new AntPathRequestMatcher("/welcome", HttpMethod.GET.name()),
        new AntPathRequestMatcher("/api/users", HttpMethod.GET.name()),
        new AntPathRequestMatcher("/api/users", HttpMethod.POST.name()),
        new AntPathRequestMatcher("/api/login", HttpMethod.POST.name())
    );

    public JWTAuthenticationFilter(AppUserDetailsService userDetailsService,
                                   JWTUtils jwtUtils,
                                   @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.userDetailsService = userDetailsService;
        this.jwtUtils = jwtUtils;
        this.resolver = resolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (this.ignoredPaths.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(TOKEN_TYPE_NAME)) {
            Exception ex = new JWTValidationException("Authorization header is empty or incorrect!");
            resolver.resolveException(request, response, null, ex);
            return;
        }

        String jwt = authHeader.replaceFirst(TOKEN_TYPE_NAME, "").trim();
        Claims claims;
        try {
            claims = jwtUtils.validateAndRetrieveClaims(jwt);
        } catch (JwtException ex) {
            resolver.resolveException(request, response, null, ex);
            return;
        }

        String email = (String) claims.get("email"); // TODO mb extract email with JWTUtils.getEmail()?
        SecurityContext currentSecurityContext = SecurityContextHolder.getContext();
        if (!email.isBlank() && currentSecurityContext.getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            var springAuthToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
            );
            springAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            currentSecurityContext.setAuthentication(springAuthToken);
        }

        filterChain.doFilter(request, response);
    }
}
