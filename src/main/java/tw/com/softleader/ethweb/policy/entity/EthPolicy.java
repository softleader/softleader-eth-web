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
  protected String insAddress; // 被保人錢包
  
  @Column(name = "CONTRACT_ADDRESS", length = 40, updatable = false)
  protected String contractAddress; // 合約地址
  
  @Column(name = "AMOUNT")
  protected long amount; // 投保金額
  
  @Column(name = "APPLY_DATE")
  protected LocalDate applyDate; // 申請日
  
  @Column(name = "EFF_DATE")
  protected LocalDate effDate; // 生效日
  
  @Column(name = "EXP_DATE")
  protected LocalDate expDate; // 失效日
  
  @Column(name = "COMPENSATED")
  protected boolean compensated; // 已理賠
  
}
