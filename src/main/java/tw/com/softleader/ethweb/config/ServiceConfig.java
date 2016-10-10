package tw.com.softleader.ethweb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import tw.com.softleader.ethweb.eth.ContractLoader;
import tw.com.softleader.security.supplier.CurrentUsernameSupplier;

@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan(basePackages = {"tw.com.softleader.ethweb.**.service", "tw.com.softleader.ethweb.**.web"})
public class ServiceConfig {

  @Bean
  public CurrentUsernameSupplier currentUsernameSupplier() {
    return new CurrentUsernameSupplier();
  }

  @Bean
  public ContractLoader contractLoader() {
    return new ContractLoader();
  }

}
