package tw.com.softleader.ethweb.policy.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import tw.com.softleader.commons.collect.Maps;
import tw.com.softleader.ethweb.policy.entity.EthWeatherPolicy;

/**
 * FIXME dummy now
 */
@Service
public class EthPolicyService {
  
  private final Map<String, EthWeatherPolicy> datas = Maps.newHashMap();
  
  public List<EthWeatherPolicy> getAll() {
    return datas.values().stream()
      .sorted(Comparator.comparing(EthWeatherPolicy::getCreatedTime).reversed())
      .collect(Collectors.toList());
  }

  public EthWeatherPolicy insert(EthWeatherPolicy entity) {
    if (datas.containsKey(entity.getInsAddress())) {
      datas.remove(entity.getInsAddress());
    }
    entity.setCreatedTime(LocalDateTime.now());
    datas.put(entity.getInsAddress(), entity);
    return entity;
  }

}
