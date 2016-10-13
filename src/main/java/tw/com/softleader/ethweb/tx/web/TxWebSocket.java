package tw.com.softleader.ethweb.tx.web;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.ethereum.core.CallTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import lombok.extern.slf4j.Slf4j;
import tw.com.softleader.ethweb.tx.model.RainfallModel;
import tw.com.softleader.ethweb.tx.service.TxService;

@Slf4j
@Controller
public class TxWebSocket {

  @Autowired
  private Environment env;

  @Autowired
  private TxService txService;

  @MessageMapping("/insertRainfall")
  @SendTo("/topic/onInsertRainfall")
  public String insertRainfall(RainfallModel data) {
    log.info("socket insertRainfall: {}", data);
    ZonedDateTime utcDate = data.getDate().atStartOfDay().atZone(ZoneId.of("UTC"));
    
    try {
      // 呼叫合約方法
      CallTransaction.Function function = CallTransaction.Function.fromSignature("insertRainfall", "uint", "uint");
      txService.addCallTx(env.getProperty("eth.contract.address"), function, utcDate.toInstant().getEpochSecond(), data.getRainfall());
      
      // 回傳
    } catch (Exception e) {
      log.error("RenewRainfall fail", e);
      return "fail";
    }
    
    return "success";
  }
  
}
