package com.portal.gateway.security;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portal.gateway.bean.ErrorResponse;
import com.portal.gateway.exception.CustomException;
import io.jsonwebtoken.JwtException;

/**
 * JWT Token filter
 * @author Kuldeep
 *
 */
@Configuration
public class JwtTokenFilter implements Filter {

	
	private JwtTokenProvider jwtTokenProvider;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)
			throws IOException, ServletException {
		HttpServletResponse response = (HttpServletResponse) res;
		String token = jwtTokenProvider.resolveToken((HttpServletRequest) req);
		if (token != null) {
			if (!jwtTokenProvider.isTokenPresentInDB(token)) {

				ErrorResponse errorResponse = new ErrorResponse("Invalid JWT token");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				response.setCharacterEncoding("UTF-8");
				response.getWriter().write(convertObjectToJson(errorResponse));
				throw new CustomException("Invalid JWT token", HttpStatus.UNAUTHORIZED);
			}
			try {
				jwtTokenProvider.validateToken(token);
			} catch (JwtException e) {
				ErrorResponse errorResponse = new ErrorResponse("Invalid JWT token");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				response.setCharacterEncoding("UTF-8");
				response.getWriter().write(convertObjectToJson(errorResponse));
				throw new CustomException("Invalid JWT token", HttpStatus.UNAUTHORIZED);

			} catch (IllegalArgumentException e) {
				ErrorResponse errorResponse = new ErrorResponse("Invalid JWT token");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				response.setCharacterEncoding("UTF-8");
				response.getWriter().write(convertObjectToJson(errorResponse));
				throw new CustomException("Invalid JWT token", HttpStatus.UNAUTHORIZED);

			}
			Authentication auth = token != null ? jwtTokenProvider.getAuthentication(token) : null;

			SecurityContextHolder.getContext().setAuthentication(auth);
		}
		filterChain.doFilter(req, res);

	}
	
	 public String convertObjectToJson(Object object) throws JsonProcessingException {
	        if (object == null) {
	            return null;
	        }
	        ObjectMapper mapper = new ObjectMapper();
	        return mapper.writeValueAsString(object);
	    }
}
