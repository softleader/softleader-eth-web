package tw.com.softleader.ethweb.policy.entity;

import java.time.LocalDate;

import javax.persistence.Column;

import lombok.Getter;
import lombok.Setter;
import tw.com.softleader.data.entity.GenericCodeEntity;

@SuppressWarnings("serial")
@Setter
@Getter
public abstract class EthPolicy extends GenericCodeEntity<Long> {
  
  @Column(name = "INS_ADDRESS", length = 40, updatable = false)
  private String insAddress; // 被保人錢包
  
  @Column(name = "CONTRACT_ADDRESS", length = 40, updatable = false)
  private String contractAddress; // 合約地址
  
  @Column(name = "AMOUNT")
  private long amount; // 保額
  
  @Column(name = "APPLY_DATE")
  private LocalDate applyDate;
  
  @Column(name = "EFF_DATE")
  private LocalDate effDate;
  
  @Column(name = "EXP_DATE")
  private LocalDate expDate;
  
  @Column(name = "COMPENSATED")
  private boolean compensated;
  
}
