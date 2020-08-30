package org.example.eda.noise;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;
import org.example.eda.SpatialTemporalOccupancyRecord;

public class DedupNoiseCancel implements INoiseCancel {

  @Override
  public List<SpatialTemporalOccupancyRecord> cancelNoise(final List<SpatialTemporalOccupancyRecord> records) {
    final TreeSet<SpatialTemporalOccupancyRecord> orderedSequence = new TreeSet<>((o1, o2) -> Comparator
        .comparing(SpatialTemporalOccupancyRecord::getEntry)
        .thenComparing(SpatialTemporalOccupancyRecord::getExit)
        .thenComparing(SpatialTemporalOccupancyRecord::getPolygonName)
        .compare(o1, o2));
    orderedSequence.addAll(records);
    final List<SpatialTemporalOccupancyRecord> result = new ArrayList<>();
    final StringBuilder stringBuffer = new StringBuilder();
    SpatialTemporalOccupancyRecord lastEntryPointer = null;
    for (final SpatialTemporalOccupancyRecord record : orderedSequence) {
      if (Objects.isNull(lastEntryPointer)) {
        lastEntryPointer = copyFromRecord(record);
        result.add(lastEntryPointer);
      } else {
        if (record.getPolygonName().equals(lastEntryPointer.getPolygonName())) {
          lastEntryPointer.setPingCounts(lastEntryPointer.getPingCounts() + record.getPingCounts());
          lastEntryPointer.setExit(record.getExit());
        } else {
          lastEntryPointer = copyFromRecord(record);
          result.add(lastEntryPointer);
        }
      }
    }
    return result;
  }

  private SpatialTemporalOccupancyRecord copyFromRecord(final SpatialTemporalOccupancyRecord record) {
    return new SpatialTemporalOccupancyRecord(
        record.getVesselId(),
        record.getPolygonName(),
        record.getEntry(),
        record.getExit(),
        record.getPingCounts()
    );
  }


}
