package org.example.eda;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SpatialTemporalOccupancyRecord {

  private String vesselId;
  private String polygonName;
  private long entry;
  private long exit;
  private long pingCounts;

}
