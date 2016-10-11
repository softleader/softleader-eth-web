package tw.com.softleader.ethweb.tx.service;

import java.time.ZonedDateTime;

import org.springframework.stereotype.Service;

import tw.com.softleader.ethweb.policy.enums.WeatherType;

@Service
public class WeatherService {
  
  public WeatherType getTodayWeather(ZonedDateTime utc) {
    // FIXME this is a dummy
    return WeatherType.Sunny;
  }

}
