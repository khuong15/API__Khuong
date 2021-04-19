package com.bework.config;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

public class AuthUser extends org.springframework.security.core.userdetails.User {
    /**
	 * 
	 */
	private static final long serialVersionUID = -2654547478565382629L;
	private Long rfrId;
	private String countryCode;

    public AuthUser(String username, String password, Collection<GrantedAuthority> authorities, Long rfrId, String countryCode) {
        super(username, password, authorities);

        this.rfrId = rfrId;
        this.countryCode = countryCode;
    }
    
    public Long getRfrId() {
        return rfrId;
    }
    
    public String getCountryCode() {
        return countryCode;
    }
}
