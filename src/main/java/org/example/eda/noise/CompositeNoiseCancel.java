package org.example.eda.noise;

import java.util.List;
import org.example.eda.SpatialTemporalOccupancyRecord;

public class CompositeNoiseCancel implements INoiseCancel {

  final INoiseCancel[] noiseCancels;

  public CompositeNoiseCancel(final INoiseCancel... noiseCancels) {
    this.noiseCancels = noiseCancels;
  }

  @Override
  public List<SpatialTemporalOccupancyRecord> cancelNoise(final List<SpatialTemporalOccupancyRecord> spatialTemporalOccupancyRecords) {
    List<SpatialTemporalOccupancyRecord> result = spatialTemporalOccupancyRecords;
    for (final INoiseCancel noiseCancel : noiseCancels) {
      result = noiseCancel.cancelNoise(result);
    }
    return result;
  }
}
