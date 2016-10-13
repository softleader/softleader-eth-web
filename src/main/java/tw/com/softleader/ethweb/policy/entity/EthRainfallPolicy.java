package tw.com.softleader.ethweb.policy.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import tw.com.softleader.ethweb.config.Constants;

@SuppressWarnings("serial")
@Setter
@Getter
@Entity
@Table(name = "ETH_WEATHER_POLICY")
public class EthRainfallPolicy extends EthPolicy {
  
  @Column(name = "RAINFALL")
  private int rainfall; // 降雨量
  
  @Override
  public String toString() {
    return "Contract: " + contractAddress + ", ApplyFrom: " + insAddress +
        ", Detail[Amount=" + amount +
          " ApplyDate=" + Constants.DATE_FORMATTER.format(applyDate) +
          " EffDate=" + Constants.DATE_FORMATTER.format(effDate) +
          " ExpDate=" + Constants.DATE_FORMATTER.format(expDate) +
          " Rainfall=" + rainfall + 
          " Compensated=" + compensated + "]";
  }
  
}
