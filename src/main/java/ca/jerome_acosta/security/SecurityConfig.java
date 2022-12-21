package ca.jerome_acosta.security;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;


@SuppressWarnings("deprecation")
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	
	// Handle when user is denied access
	private LoggingAccessDeniedHandler accessDeniedHandler;
	private BCryptPasswordEncoder passwordEncoder;
	private DataSource dataSource;
	
	public SecurityConfig(
			LoggingAccessDeniedHandler accessDeniedHandler, 
			@Lazy BCryptPasswordEncoder passwordEncoder, 
			DataSource dataSource) {
		this.accessDeniedHandler = accessDeniedHandler;
		this.passwordEncoder = passwordEncoder;
		this.dataSource = dataSource;
	}
//	public SecurityConfig(
//			LoggingAccessDeniedHandler accessDeniedHandler,  
//			DataSource dataSource) {
//		this.accessDeniedHandler = accessDeniedHandler;
//		this.dataSource = dataSource;
//	}
	
	
	//Beans for BCrypt & JdbcUserDetailsManager
	@Bean
	public BCryptPasswordEncoder createPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public JdbcUserDetailsManager jdbcUserDetailsManager() throws Exception {
		JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager();
		
		jdbcUserDetailsManager.setDataSource(dataSource);
		
		return jdbcUserDetailsManager;
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http.authorizeRequests()
			.antMatchers("/user/**").hasAnyRole("USER", "ADMIN") // All users with Role "USER"/"ADMIN" can access "/user/**"
			.antMatchers("/admin/**").hasRole("ADMIN") // All users with Role "ADMIN" can access "/admin/**"
			.antMatchers("/h2-console/**").permitAll() // Allow use of h2-console
			.antMatchers("/", "/**").permitAll() // All users of any Role can access "/**"
			.and() // Chain configuration calls.
			.formLogin().loginPage("/login") // Override default login page to custom login.html
			.defaultSuccessUrl("/") // If already logged in, redirect to root/home page
			
			.and()
			.logout().invalidateHttpSession(true)
			.clearAuthentication(true)
		
			
			.and()
			.exceptionHandling() 
			.accessDeniedHandler(accessDeniedHandler); // Use our handler
		
		// For h2-console
		http.csrf().disable();
		http.headers().frameOptions().disable();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		auth.jdbcAuthentication() // use jdbc authentication
		.dataSource(dataSource) 
		.passwordEncoder(passwordEncoder); // Use BCrypt: generates salt internally, and encodes the password.
		
//		auth.jdbcAuthentication() // use jdbc authentication
//		.passwordEncoder(NoOpPasswordEncoder.getInstance()) // No pw encoder.
//		.dataSource(dataSource); 
	
	}
}
