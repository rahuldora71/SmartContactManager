package com.smart.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class MyConfiguration {

	// user create and login using java code with in memory service
	// @Bean
	// public UserDetailsService getUserDetailService() {
	// 	UserDetails user1 = User
	// 			.withDefaultPasswordEncoder()
	// 			.username("admin123")
	// 			.password("admin123")
	// 			.roles("ADMIN","USER")
	// 			.build();


	// 			UserDetails user2 = User.withDefaultPasswordEncoder()
	// 			.username("rahul")
	// 			.password("rahul")
	// 			.build();
	// 	var inMemoryUserDetailsManager = new InMemoryUserDetailsManager(user1, user2);
	// 	return inMemoryUserDetailsManager;
	// }

	@Bean
	public UserDetailsService getUserDetailService() {
		return new UserDetailsServiceImpl();
	}

	//Configuration of authentication provider
	@Bean
	public AuthenticationProvider authenticationProvider(){
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		//User Details Service object
		daoAuthenticationProvider.setUserDetailsService(getUserDetailService());
		//Password Encoder object
		daoAuthenticationProvider.setPasswordEncoder(bCryptPasswordEncoder());
		return daoAuthenticationProvider;
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		//Configuration  urls which are allowed to access without login
		http.authorizeHttpRequests(authorize->{
			authorize.requestMatchers("/admin/**").hasAuthority("ADMIN");
			authorize.requestMatchers("/user/**").hasAuthority("USER");
			authorize.anyRequest().permitAll();
		});  
		//form default login 
		// if we have to cahnde any thing relatrd to form login
		
		// http.formLogin(Customizer.withDefaults());
		http.formLogin(formLogin->{
			formLogin.loginPage("/login");
			formLogin.defaultSuccessUrl("/user/index");
			formLogin.failureUrl("/login?error");
			formLogin.usernameParameter("username");
			formLogin.passwordParameter("password");
		formLogin.permitAll();
//            try {
//                http.rememberMe(httpSecurityRememberMeConfigurer -> {
//                    httpSecurityRememberMeConfigurer.key("rahuldora19000");
//                    httpSecurityRememberMeConfigurer.tokenValiditySeconds(2 );
//                });
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
        });



	http.csrf(crsf->{
		crsf.disable();
	});
			//logout configuration
			http.logout(logout->{
				logout.logoutUrl("/logout");
				logout.logoutSuccessUrl("/login?logout");
				logout.invalidateHttpSession(true);
				logout.deleteCookies("JSESSIONID");
				logout.permitAll();
			});

			// http.logout(Customizer.withDefaults());
			



		return http.build();
	}


	

}
