package com.bework.config;

import java.util.Arrays;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bework.common.Consts;
import com.bework.common.LockedException;

@Service("userService")
public class UserServiceImpl implements UserDetailsService {
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		String[] login = username.split(":");
		
		if(login != null && login.length > 0) {
			String countryCode = login[0];
			username = login[1];
			
			UserManager userManager = new UserManager();
			User user = userManager.getUser(username, countryCode);
			if (user != null) {
				if(Consts.USER_STATUS_LOCKED.equals(user.getStatus())) {
					throw new LockedException(Consts.USER_LOCKED.toString());
				}else if(Consts.USER_STATUS_ACTIVE.equals(user.getStatus())) {
					GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getType());
					
					if(user.getPassword() == null) {
						return new AuthUser(username, Consts.AUTH_PARTNER, 
								Arrays.asList(authority), user.getRfrId(), countryCode);
					}
					return new AuthUser(username, user.getPassword().replace("$2y$", "$2a$"), 
							Arrays.asList(authority), user.getRfrId(), countryCode);
				}else if(Consts.USER_STATUS_DELETED.equals(user.getStatus())) {
					throw new LockedException(Consts.USER_DELETED.toString());
				}else {
					throw new UsernameNotFoundException("Invalid username or password.");
				}
			}
		}
		throw new UsernameNotFoundException("Invalid username or password.");
	}
}


