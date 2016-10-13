package tw.com.softleader.ethweb.tx.service;

import java.time.ZonedDateTime;

import org.springframework.stereotype.Service;

@Service
public class RainfallService {
  
  public int getTodayRainfall(ZonedDateTime utc) {
    // FIXME this is a dummy
    return 100;
  }

}
