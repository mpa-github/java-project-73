package hexlet.code.security;

import hexlet.code.exception.JWTValidationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
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
    private final ApplicationContext context;
    private final HandlerExceptionResolver resolver;

    public JWTAuthenticationFilter(AppUserDetailsService userDetailsService,
                                   JWTUtils jwtUtils,
                                   ApplicationContext context,
                                   @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.userDetailsService = userDetailsService;
        this.jwtUtils = jwtUtils;
        this.context = context;
        this.resolver = resolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        RequestMatcher ignoredPaths = context.getBean(WebSecurityConfig.class).getPublicUrlPaths();
        if (ignoredPaths.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(TOKEN_TYPE_NAME)) {
            Exception ex = new JWTValidationException(authHeader);
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
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (!email.isBlank() && securityContext.getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            var springAuthToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
            );
            springAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            securityContext.setAuthentication(springAuthToken);
        }
        filterChain.doFilter(request, response);
    }
}
