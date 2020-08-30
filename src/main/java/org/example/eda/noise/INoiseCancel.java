package org.example.eda.noise;

import java.util.List;
import org.example.eda.SpatialTemporalOccupancyRecord;

public interface INoiseCancel {

  /**
   * @param spatialTemporalOccupancyRecords ordered SpatialTemporalOccupancy Records of only one
   * vesselId
   * @return collection of noise filtered
   */
  List<SpatialTemporalOccupancyRecord> cancelNoise(List<SpatialTemporalOccupancyRecord> spatialTemporalOccupancyRecords);

}
