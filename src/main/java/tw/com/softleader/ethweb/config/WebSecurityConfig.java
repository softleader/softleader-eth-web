package tw.com.softleader.ethweb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

import tw.com.softleader.ethweb.security.service.UserDetailsService;
import tw.com.softleader.security.authentication.MoreDetailsBinder;
import tw.com.softleader.security.authentication.MoreUserDetailsService;
import tw.com.softleader.security.config.MoreWebSecurityConfiguration;
import tw.com.softleader.security.supplier.CurrentUsernameSupplier;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends MoreWebSecurityConfiguration {

  @Bean
  @Override
  public MoreDetailsBinder moreDetailsBinder() {
    return (request, details) -> {};
  }

  @Bean
  @Override
  public MoreUserDetailsService moreUserDetailsService() {
    return new UserDetailsService();
  }

  @Bean
  public CurrentUsernameSupplier currentUsernameSupplier() {
    return new CurrentUsernameSupplier();
  }
  
  @Override
  protected void authorizeRequests(
      ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry authorizeRequests) {
    authorizeRequests.antMatchers("/**").permitAll().anyRequest()
    .authenticated();
  }
}