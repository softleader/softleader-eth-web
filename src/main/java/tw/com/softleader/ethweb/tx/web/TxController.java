package tw.com.softleader.ethweb.tx.web;

import java.util.List;

import org.ethereum.facade.Ethereum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import tw.com.softleader.ethweb.eth.EthereumAdapter;

@Controller
@RequestMapping("/tx")
public class TxController {

  @Autowired
  protected Ethereum ethereum;
  
  @RequestMapping
  public String txPage() {
    return "/tx/tx_main";
  }

  @RequestMapping("/logs")
  @ResponseBody
  public List<String> txLogs() {
    return EthereumAdapter.txLogs;
  }

}
