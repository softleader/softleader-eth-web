package tw.com.softleader.ethweb.model;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;

import lombok.Setter;
import tw.com.softleader.ethweb.policy.entity.EthWeatherPolicy;
import tw.com.softleader.ethweb.policy.enums.WeatherType;

@Setter
public class EthWeatherPolicyModel {
  
  private String applyAddress;
  
  private String contractOwner;
  
  private int amount;
  
  private int applyDate;
  
  private int effDate;
  
  private String weatherType;
  
  private boolean compensated;
  
  public EthWeatherPolicy toEntity() {
    EthWeatherPolicy entity = new EthWeatherPolicy();
    
    entity.setInsAddress(applyAddress);
    entity.setContractAddress(contractOwner);
    entity.setAmount(amount);
    entity.setApplyDate(LocalDateTime.ofInstant(Instant.ofEpochSecond(applyDate), Clock.systemUTC().getZone()).toLocalDate());
    entity.setEffDate(LocalDateTime.ofInstant(Instant.ofEpochSecond(effDate), Clock.systemUTC().getZone()).toLocalDate());
    entity.setExpDate(LocalDateTime.ofInstant(Instant.ofEpochSecond(effDate), Clock.systemUTC().getZone()).toLocalDate());
    entity.setWeatherType(WeatherType.valueOf(weatherType));
    entity.setCompensated(compensated);
    
    return entity;
  }
  
}
