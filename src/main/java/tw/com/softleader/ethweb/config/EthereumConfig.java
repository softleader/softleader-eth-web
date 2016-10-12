package tw.com.softleader.ethweb.config;

import org.ethereum.config.DefaultConfig;
import org.ethereum.core.CallTransaction.Invocation;
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
    
    // 註冊需要監看的合約與事件
    adapter.watchEvent("32f2933d4eaEEE284908fFdc79f20179Bdb9bEdc", Unchecked.accept(l -> {
      // 接收到logInfo後，首先要做的就是先將裡面的資料轉換成Java的物件
      // 此處需要借用到合約的介面來完成這件事
      final Invocation invocation = contractLoader.weatherPolicy.parseEvent(l);
      final EthWeatherPolicyModel model = contractLoader.invocationToPojo(invocation, EthWeatherPolicyModel.class);
      final EthWeatherPolicy entity = model.toEntity();
      EthereumAdapter.txLogs.add(model.toEntity().toString());
      ethPolicyService.insert(entity);
      msgSender.convertAndSend("/topic/onevent", entity);
    }));
    
    return adapter;
  }
  
}
