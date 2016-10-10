package tw.com.softleader.ethweb.policy.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

import tw.com.softleader.ethweb.policy.entity.EthWeatherPolicy;

@Service
public class EthPolicyService {
  
  public final List<EthWeatherPolicy> datas = Lists.newArrayList();

}
