package com.bework.config;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.common.DefaultExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

@Configuration
@EnableAuthorizationServer
public class OAuth2AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

	@Autowired
	@Qualifier("authenticationManagerBean")
	private AuthenticationManager authenticationManager;

	@Bean
	public BCryptPasswordEncoder encoder() {
		return new BeeduBCryptPasswordEncoder();
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory().withClient("beeduClient").secret("beeduSecret")
				.authorizedGrantTypes("authorization_code", "refresh_token", "password").scopes("beeduScope");
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
		oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints.tokenStore(tokenStore()).authenticationManager(authenticationManager)
				.accessTokenConverter(jwtAccessTokenConverter())
				.reuseRefreshTokens(true);
	}

	@Bean
	public JwtAccessTokenConverter jwtAccessTokenConverter() {
		JwtAccessTokenConverter converter = new BeeJwtAccessTokenConverter();
		converter.setSigningKey("beedu@2019");
		return converter;
	}

	@Bean
	public FilterRegistrationBean oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(filter);
		registration.setOrder(-100);
		return registration;
	}
    
	class BeeJwtAccessTokenConverter extends JwtAccessTokenConverter {
		@Override
		public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
			DefaultOAuth2AccessToken beeAccessToken = new DefaultOAuth2AccessToken(accessToken);

			if (authentication != null && authentication.getPrincipal() instanceof AuthUser) {
				AuthUser user = (AuthUser) authentication.getPrincipal();
				Map<String, Object> info = new LinkedHashMap<String, Object>(accessToken.getAdditionalInformation());
				info.put("rfr_id", user.getRfrId());
				info.put("country_code", user.getCountryCode());
				
				beeAccessToken.setAdditionalInformation(info);
			}else if(accessToken.getRefreshToken() != null && accessToken.getRefreshToken().getValue() != null) {
				Map<String, Object> info = decode(accessToken.getRefreshToken().getValue());
				Map<String, Object> newInfo = new HashMap<>();
				newInfo.put("rfr_id", info.get("rfr_id"));
				newInfo.put("country_code", info.get("country_code"));
				
				beeAccessToken.setAdditionalInformation(newInfo);
				beeAccessToken.setValue(encode(beeAccessToken, authentication));
//				return beeAccessToken;
			}
			
			DefaultOAuth2AccessToken result = new DefaultOAuth2AccessToken(beeAccessToken);
			Map<String, Object> info = new LinkedHashMap<String, Object>(beeAccessToken.getAdditionalInformation());
			String tokenId = result.getValue();
			if (!info.containsKey(TOKEN_ID)) {
				info.put(TOKEN_ID, tokenId);
			}
			result.setAdditionalInformation(info);
			result.setValue(encode(result, authentication));
			OAuth2RefreshToken refreshToken = result.getRefreshToken();
			if (refreshToken != null) {
				DefaultOAuth2AccessToken encodedRefreshToken = new DefaultOAuth2AccessToken(beeAccessToken);
				
				Jwt jwt = null;
				
				try {
					jwt = JwtHelper.decode(encodedRefreshToken.getRefreshToken().toString());
				} catch (Exception e) {
				}
				
				if(jwt == null) {
					encodedRefreshToken.setValue(refreshToken.getValue());
					Map<String, Object> refreshTokenInfo = new LinkedHashMap<String, Object>(beeAccessToken.getAdditionalInformation());
					refreshTokenInfo.put(TOKEN_ID, encodedRefreshToken.getValue());
					refreshTokenInfo.put(ACCESS_TOKEN_ID, tokenId);
					encodedRefreshToken.setAdditionalInformation(refreshTokenInfo);
					DefaultOAuth2RefreshToken token = new DefaultOAuth2RefreshToken(encode(encodedRefreshToken, authentication));
					if (refreshToken instanceof ExpiringOAuth2RefreshToken) {
						Date expiration = ((ExpiringOAuth2RefreshToken) refreshToken).getExpiration();
						encodedRefreshToken.setExpiration(expiration);
						token = new DefaultExpiringOAuth2RefreshToken(encode(encodedRefreshToken, authentication), expiration);
					}
					
					result.setRefreshToken(token);
				}else {
					Map<String, Object> refreshTokenInfo = new LinkedHashMap<String, Object>(beeAccessToken.getAdditionalInformation());
					refreshTokenInfo.put(TOKEN_ID, encodedRefreshToken.getValue());
					refreshTokenInfo.put(ACCESS_TOKEN_ID, tokenId);
					encodedRefreshToken.setAdditionalInformation(refreshTokenInfo);
					
					DefaultOAuth2RefreshToken token = new DefaultOAuth2RefreshToken(encodedRefreshToken.getRefreshToken().toString());
					result.setRefreshToken(token);
				}
			}
			return result;
		}
	}
	
	@Value("${spring.authentication.datasource.driver-class-name}")
    private String authenticationDriverClassName;
	
	@Value("${spring.authentication.datasource.url}")
    private String authenticationUrl;
	
	@Value("${spring.authentication.datasource.username}")
    private String authenticationUsername;
	
	@Value("${spring.authentication.datasource.password}")
    private String authenticationPassword;
	
	@Bean
    public TokenStore tokenStore() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(authenticationDriverClassName);
		dataSource.setUrl(authenticationUrl);
		dataSource.setUsername(authenticationUsername);
		dataSource.setPassword(authenticationPassword);
		
        return new BeedJdbcTokenStore(dataSource);
    }


	class BeedJdbcTokenStore extends JdbcTokenStore{
    	public BeedJdbcTokenStore(DataSource dataSource) {
    		super(dataSource);
    	}
    	
    	@Override
    	public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
    		OAuth2AccessToken accessToken = null;

    		return accessToken;
    	}
    	
    	@Override
    	protected String extractTokenKey(String value) {
    		if (value == null) {
    			return null;
    		}
    		MessageDigest digest;
    		try {
    			digest = MessageDigest.getInstance("MD5");
    		}
    		catch (NoSuchAlgorithmException e) {
    			throw new IllegalStateException("MD5 algorithm not available.  Fatal (should be in the JDK).");
    		}

    		try {
    			byte[] bytes = digest.digest(value.getBytes("UTF-8"));
    			String tokenId = String.format("%032x", new BigInteger(1, bytes));
    			return tokenId;
    		}
    		catch (UnsupportedEncodingException e) {
    			throw new IllegalStateException("UTF-8 encoding not available.  Fatal (should be in the JDK).");
    		}
    	}
    }
}