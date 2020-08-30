package org.example.eda.io;

import java.util.Collection;
import org.example.eda.SpatialTemporalOccupancyRecord;

public interface ISpatialTemporalDataWriter {

  void write(Collection<SpatialTemporalOccupancyRecord> records);

}
