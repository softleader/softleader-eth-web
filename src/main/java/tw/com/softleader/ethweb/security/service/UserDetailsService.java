package tw.com.softleader.ethweb.security.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import tw.com.softleader.security.authentication.MoreUserDetailsService;
import tw.com.softleader.security.pojo.SimpleUserDetails;
import tw.com.softleader.util.StringUtils;

public class UserDetailsService implements MoreUserDetailsService {

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Override
  public UserDetails loadUserByUsername(String username, Map<String, String> details)
      throws UsernameNotFoundException {
    if (StringUtils.isBlank(username)) {
      throw new BadCredentialsException("username required");
    }
    return new SimpleUserDetails(username, passwordEncoder.encode(username));
  }

}