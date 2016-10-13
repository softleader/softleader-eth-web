package tw.com.softleader.ethweb.tx.model;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RainfallModel {
  
  private LocalDate date;
  private Integer rainfall;

}
