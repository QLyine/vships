package org.example.eda.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class VesselDataPoint {

  private String vesselId;
  private long timestamp;
  private double lat;
  private double lon;
  private int speed;
  private int heading;


}
