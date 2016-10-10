package tw.com.softleader.ethweb.policy.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import tw.com.softleader.ethweb.policy.enums.WeatherType;

@SuppressWarnings("serial")
@Setter
@Getter
@Entity
@Table(name = "ETH_WEATHER_POLICY")
public class EthWeatherPolicy extends EthPolicy {
  
  @Enumerated(EnumType.STRING)
  @Column(name = "WEATHER_TYPE")
  private WeatherType weatherType; // 天氣型別

}
