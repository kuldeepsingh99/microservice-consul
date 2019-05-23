package com.portal.gateway.security;

import java.io.Serializable;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.portal.gateway.bean.JwtToken;
import com.portal.gateway.bean.MongoUserDetails;
import com.portal.gateway.repository.JwtTokenRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * JWT Token Provider
 * @author Kuldeep
 *
 */
@Component
public class JwtTokenProvider implements Serializable {  
	
	private static final long serialVersionUID = 1L;

    @Value("${jwt.security.key}")
    private String secretKey;
    
	private static final String AUTH = "auth";
	private static final String AUTHORIZATION = "Authorization";
	private long validityInMilliseconds = 1000 * 60 * 60; // 1h

    @Autowired
    private JwtTokenRepository jwtTokenRepository;
    

    public String createToken(String username, String roles) {

        Claims claims = Jwts.claims().setSubject(username);
        claims.put(AUTH,roles);

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        String token =  Jwts.builder()//
                .setClaims(claims)//
                .setIssuedAt(now)//
                .setExpiration(validity)//
                .signWith(SignatureAlgorithm.HS256, secretKey)//
                .compact();
        jwtTokenRepository.save(new JwtToken(token));
        return token;
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader(AUTHORIZATION);
        if (bearerToken != null ) {
            return bearerToken;
        }
        return null;
    }

    public boolean validateToken(String token) throws JwtException, IllegalArgumentException{
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
    }
    
    
    public boolean isTokenPresentInDB (String token) {
        return jwtTokenRepository.findById(token).isPresent();
    }
    
    
    //user details with out database hit
    public UserDetails getUserDetails(String token) {
        String userName =  getUsername(token);
        String roleName = getRoleList(token);
        UserDetails userDetails = new MongoUserDetails(userName,roleName);
        return userDetails;
    }
    
    
    public String getRoleList(String token) {
        return (String) Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).
                getBody().get(AUTH);
    }

    
    public String getUsername(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }
    public Authentication getAuthentication(String token) {
        
        //UserDetails userDetails = userDetailsService.loadUserByUsername(getUsername(token));
        
        UserDetails userDetails = getUserDetails(token);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
    
    
}