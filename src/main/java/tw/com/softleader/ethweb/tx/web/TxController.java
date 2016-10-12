package tw.com.softleader.ethweb.tx.web;

import java.time.LocalDate;
import java.time.ZoneId;
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
  
  @Autowired
  private WeatherService weatherService;

  /**
   * 更新今日天氣至合約上
   */
  @RequestMapping("/renew/weather")
  @ResponseBody
  public AjaxResponse<WeatherType> renewWeather() {
    ZonedDateTime date = LocalDate.now().atStartOfDay().atZone(ZoneId.of("UTC"));
    AjaxResponse<WeatherType> response = new AjaxResponse<>();
    
    try {
      // 取得今日天氣
      WeatherType weather = weatherService.getTodayWeather(date);
      
      // 呼叫合約方法
      CallTransaction.Function function = CallTransaction.Function.fromSignature("insertWeather", "uint", "string");
      txService.addCallTx(env.getProperty("eth.contract.address"), function, date.toInstant().getEpochSecond(), weather.toString());
      
      // 回傳
      response.setData(weather);
    } catch (Exception e) {
      log.error("RenewWeather fail", e);
      response.addException(e);
    }
    return response;
  }

  /**
   * 更新某日天氣至合約上
   */
  @RequestMapping("/renew/weather/{dateStr}/{weather}")
  @ResponseBody
  public AjaxResponse<WeatherType> renewWeather(@PathVariable String dateStr, @PathVariable WeatherType weather) {
    ZonedDateTime date = LocalDate.parse(dateStr).atStartOfDay().atZone(ZoneId.of("UTC"));
    AjaxResponse<WeatherType> response = new AjaxResponse<>();
    
    try {
      // 呼叫合約方法
      CallTransaction.Function function = CallTransaction.Function.fromSignature("insertWeather", "uint", "string");
      txService.addCallTx(env.getProperty("eth.contract.address"), function, date.toInstant().getEpochSecond(), weather.toString());
      
      // 回傳
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
  
  public static void main(String[] args) {
    System.out.println(LocalDate.parse("2016-12-01").atStartOfDay().atZone(ZoneId.of("UTC")).toInstant().getEpochSecond());
  }
  
}
