package tw.com.softleader.ethweb.config;

import org.ethereum.config.DefaultConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import tw.com.softleader.commons.function.Unchecked;
import tw.com.softleader.ethweb.eth.ContractLoader;
import tw.com.softleader.ethweb.eth.EthereumAdapter;
import tw.com.softleader.ethweb.eth.model.LogTest;

@Configuration
@PropertySource({"classpath:ethereum.properties"})
public class EthereumConfig extends DefaultConfig {
  
  @Autowired
  private ContractLoader contractLoader;

  @Bean
  public EthereumAdapter ethereumAdapter() {
    EthereumAdapter adapter = new EthereumAdapter();
    adapter.watchEvent("c153F8cF2116156a323B346326e3f1b0B34C937B", Unchecked.accept(l -> {
      LogTest log = contractLoader.invocationToPojo(contractLoader.contract01.parseEvent(l), LogTest.class);
      EthereumAdapter.txLogs.add(log.toString());
    }));
    return adapter;
  }
  
}
