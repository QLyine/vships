package org.example.eda;

import java.util.List;
import org.example.eda.noise.INoiseCancel;

public class NoNoiseCancel implements INoiseCancel {

  @Override
  public List<SpatialTemporalOccupancyRecord> cancelNoise(final List<SpatialTemporalOccupancyRecord> spatialTemporalOccupancyRecords) {
    return spatialTemporalOccupancyRecords;
  }
}
