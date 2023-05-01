package hexlet.code.security;

import hexlet.code.exception.JWTValidationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    private final UserDetailsService appUserDetailsService;
    private final HandlerExceptionResolver resolver;
    private final JWTUtils jwtUtils;
    private RequestMatcher ignoredPaths;

    public JWTAuthenticationFilter(AppUserDetailsService appUserDetailsService,
                                   @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver,
                                   JWTUtils jwtUtils) {
        this.appUserDetailsService = appUserDetailsService;
        this.jwtUtils = jwtUtils;
        this.resolver = resolver;
    }

    public void setIgnoredPaths(RequestMatcher ignoredPaths) {
        this.ignoredPaths = ignoredPaths;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // TODO Try to find better way to inject RequestMatcher from WebSecurityConfig
        if (ignoredPaths != null && ignoredPaths.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(AUTHORIZATION);
        if (authHeader == null/* || !authHeader.startsWith(TOKEN_TYPE_NAME)*/) {
            Exception jwtEx = new JWTValidationException("Authorization header is empty or incorrect!");
            resolver.resolveException(request, response, null, jwtEx);
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

        String email = (String) claims.get("email");
        SecurityContext securityContext = SecurityContextHolder.getContext();
        // TODO Should we add 'if (!email.isBlank() && securityContext.getAuthentication() == null)' ?
        UserDetails userDetails;
        try {
            userDetails = appUserDetailsService.loadUserByUsername(email);
        } catch (UsernameNotFoundException ex) {
            Exception jwtEx = new JWTValidationException("The user of authentication token does not exist!");
            resolver.resolveException(request, response, null, jwtEx);
            return;
        }
        var springAuthToken = buildSpringAuthToken(userDetails);
        springAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        securityContext.setAuthentication(springAuthToken);
        filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken buildSpringAuthToken(UserDetails userDetails) {
        return new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities()
        );
    }
}
