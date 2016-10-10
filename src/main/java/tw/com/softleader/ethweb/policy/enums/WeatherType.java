package tw.com.softleader.ethweb.policy.enums;

public enum WeatherType {
  
  None,
  Sunny,
  Rainy;
  
  public WeatherType fromIdx(int idx) {
    return WeatherType.values()[idx];
  }

}
