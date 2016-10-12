package tw.com.softleader.ethweb.tx.web;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import org.ethereum.core.CallTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;
import tw.com.softleader.ethweb.eth.EthereumAdapter;
import tw.com.softleader.ethweb.policy.enums.WeatherType;
import tw.com.softleader.ethweb.tx.service.TxService;
import tw.com.softleader.ethweb.tx.service.WeatherService;
import tw.com.softleader.web.http.AjaxResponse;

@Slf4j
@Controller
@RequestMapping("/tx")
public class TxController {

  @Autowired
  private Environment env;

  @Autowired
  private TxService txService;
  
//  @Autowired
//  private WeatherService weatherService;

  @RequestMapping("/renew/weather/{date}/{weather}")
  @ResponseBody
  public AjaxResponse<WeatherType> renewWeather(@PathVariable ZonedDateTime date, @PathVariable WeatherType weather) {
//    ZonedDateTime date = ZonedDateTime.now(ZoneOffset.UTC);
    AjaxResponse<WeatherType> response = new AjaxResponse<>();
    
    try {
//      WeatherType weather = weatherService.getTodayWeather(utcToday);
      CallTransaction.Function function = CallTransaction.Function.fromSignature("insertWeather", "uint", "string");
      txService.addCallTx(env.getProperty("eth.contract.address"), function, date.toInstant().getEpochSecond(), weather.toString());
      response.setData(weather);
    } catch (Exception e) {
      log.error("RenewWeather fail", e);
      response.addException(e);
    }
    return response;
  }
  
  @RequestMapping("/logs")
  @ResponseBody
  public AjaxResponse<List<String>> txLogs() {
    return new AjaxResponse<>(EthereumAdapter.txLogs);
  }

}
