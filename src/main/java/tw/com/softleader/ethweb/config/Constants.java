package tw.com.softleader.ethweb.config;

import java.time.format.DateTimeFormatter;

import tw.com.softleader.commons.time.CommonDateTimeFormatterBuilder;

public class Constants {
  
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE;

  public static final DateTimeFormatter DATE_TIME_FORMATTER = new CommonDateTimeFormatterBuilder().betweenDateAndTime(" ").build();

  public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ISO_TIME;

}
