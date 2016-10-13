package tw.com.softleader.ethweb.policy.model;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;

import lombok.Setter;
import tw.com.softleader.ethweb.policy.entity.EthRainfallPolicy;

@Setter
public class EthWeatherPolicyModel {
  
  private String applyAddress;
  
  private String contractOwner;
  
  private BigDecimal amount;
  
  private int applyDate;
  
  private int effDate;
  
  private int rainfall;
  
  private boolean compensated;
  
  public EthRainfallPolicy toEntity() {
    EthRainfallPolicy entity = new EthRainfallPolicy();
    
    entity.setInsAddress(applyAddress);
    entity.setContractAddress(contractOwner);
    entity.setAmount(amount.longValue());
    entity.setApplyDate(LocalDateTime.ofInstant(Instant.ofEpochSecond(applyDate), Clock.systemUTC().getZone()).toLocalDate());
    entity.setEffDate(LocalDateTime.ofInstant(Instant.ofEpochSecond(effDate), Clock.systemUTC().getZone()).toLocalDate());
    entity.setExpDate(LocalDateTime.ofInstant(Instant.ofEpochSecond(effDate), Clock.systemUTC().getZone()).toLocalDate());
    entity.setRainfall(rainfall);
    entity.setCompensated(compensated);
    
    return entity;
  }
  
}
