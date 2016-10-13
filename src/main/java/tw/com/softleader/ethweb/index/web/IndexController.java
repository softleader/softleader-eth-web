package tw.com.softleader.ethweb.index.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import tw.com.softleader.ethweb.policy.entity.EthRainfallPolicy;
import tw.com.softleader.ethweb.policy.service.EthPolicyService;


@Controller
public class IndexController {
  
  @Autowired
  private EthPolicyService ethPolicyService;

  @RequestMapping("/")
  public String index(Model model) {
    List<EthRainfallPolicy> policys = ethPolicyService.getAll();
    model.addAttribute("policys", policys);
    return "index";
  }

}