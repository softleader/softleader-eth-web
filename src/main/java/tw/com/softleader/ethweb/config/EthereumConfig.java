package tw.com.softleader.ethweb.config;

import org.ethereum.config.DefaultConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import tw.com.softleader.commons.function.Unchecked;
import tw.com.softleader.ethweb.eth.ContractLoader;
import tw.com.softleader.ethweb.eth.EthereumAdapter;
import tw.com.softleader.ethweb.policy.entity.EthWeatherPolicy;
import tw.com.softleader.ethweb.policy.model.EthWeatherPolicyModel;
import tw.com.softleader.ethweb.policy.service.EthPolicyService;

@Configuration
@PropertySource({"classpath:ethereum.properties"})
public class EthereumConfig extends DefaultConfig {
  
  @Autowired
  private ContractLoader contractLoader;
  
  @Autowired
  private SimpMessagingTemplate msgSender;
  
  @Autowired
  private EthPolicyService ethPolicyService;

  @Bean
  public EthereumAdapter ethereumAdapter() {
    EthereumAdapter adapter = new EthereumAdapter();
    // for test
//    adapter.watchEvent("c153F8cF2116156a323B346326e3f1b0B34C937B", Unchecked.accept(l -> {
//      LogTest log = contractLoader.invocationToPojo(contractLoader.contract01.parseEvent(l), LogTest.class);
//      EthereumAdapter.txLogs.add(log.toString());
//    }));
    adapter.watchEvent("32f2933d4eaEEE284908fFdc79f20179Bdb9bEdc", Unchecked.accept(l -> {
      EthWeatherPolicyModel model = contractLoader.invocationToPojo(contractLoader.weatherPolicy.parseEvent(l), EthWeatherPolicyModel.class);
      EthWeatherPolicy entity = model.toEntity();
      EthereumAdapter.txLogs.add(model.toEntity().toString());
      ethPolicyService.datas.add(entity);
      msgSender.convertAndSend("/topic/onevent", entity);
    }));
    return adapter;
  }
  
}
