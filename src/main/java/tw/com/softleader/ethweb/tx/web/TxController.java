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
import tw.com.softleader.ethweb.tx.service.RainfallService;
import tw.com.softleader.ethweb.tx.service.TxService;
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
  private RainfallService rainfallService;

  /**
   * 更新今日降雨量至合約上
   */
  @RequestMapping("/renew/rainfall")
  @ResponseBody
  public AjaxResponse<Integer> renewRainfall() {
    log.info("renewRainfall today");
    ZonedDateTime date = LocalDate.now().atStartOfDay().atZone(ZoneId.of("UTC"));
    AjaxResponse<Integer> response = new AjaxResponse<>();
    
    try {
      // 取得今日降雨量
      int rainfall = rainfallService.getTodayRainfall(date);
      
      // 呼叫合約方法
      CallTransaction.Function function = CallTransaction.Function.fromSignature("insertRainfall", "uint", "uint");
      txService.addCallTx(env.getProperty("eth.contract.address"), function, date.toInstant().getEpochSecond(), rainfall);
      
      // 回傳
      response.setData(rainfall);
    } catch (Exception e) {
      log.error("RenewRainfall fail", e);
      response.addException(e);
    }
    return response;
  }

  /**
   * 更新某日天氣至合約上
   */
  @RequestMapping("/renew/weather/{dateStr}/{rainfall}")
  @ResponseBody
  public AjaxResponse<Integer> renewWeather(@PathVariable String dateStr, @PathVariable Integer rainfall) {
    log.info("renewRainfall date:{}, rainfall:{}", dateStr, rainfall);
    ZonedDateTime date = LocalDate.parse(dateStr).atStartOfDay().atZone(ZoneId.of("UTC"));
    AjaxResponse<Integer> response = new AjaxResponse<>();
    
    try {
      // 呼叫合約方法
      CallTransaction.Function function = CallTransaction.Function.fromSignature("insertRainfall", "uint", "uint");
      txService.addCallTx(env.getProperty("eth.contract.address"), function, date.toInstant().getEpochSecond(), rainfall);
      
      // 回傳
      response.setData(rainfall);
    } catch (Exception e) {
      log.error("RenewRainfall fail", e);
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
