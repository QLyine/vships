package org.example.eda;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.eda.data.MapSlice;
import org.example.eda.data.VesselDataPoint;
import org.example.eda.noise.INoiseCancel;

public class GenerateSpatialTemporalOccupancy {

  private final IMapSlicesOperations mapSlicesOperations;
  private final INoiseCancel noiseCancel;

  public GenerateSpatialTemporalOccupancy(
      final IMapSlicesOperations mapSlicesOperations, final INoiseCancel noiseCancel
  ) {
    this.mapSlicesOperations = mapSlicesOperations;
    this.noiseCancel = noiseCancel;
  }

  public NavigableSet<SpatialTemporalOccupancyRecord> generate(final List<VesselDataPoint> vesselDataPointList) {
    final NavigableSet<SpatialTemporalOccupancyRecord> set = createTreeRecords();
    final Map<String, TreeSet<VesselDataPoint>> spatialTemporalByVesselId = new HashMap<>();
    for (final VesselDataPoint vesselDataPoint : vesselDataPointList) {
      spatialTemporalByVesselId.computeIfPresent(vesselDataPoint.getVesselId(),
          (s, vesselDataPoints) -> {
            vesselDataPoints.add(vesselDataPoint);
            return vesselDataPoints;
          }
      );
      spatialTemporalByVesselId.computeIfAbsent(vesselDataPoint.getVesselId(), s -> {
        final TreeSet<VesselDataPoint> vesselDataPoints = new TreeSet<>(Comparator.comparingLong(
            VesselDataPoint::getTimestamp));
        vesselDataPoints.add(vesselDataPoint);
        return vesselDataPoints;
      });
    }

    spatialTemporalByVesselId.entrySet()
        .parallelStream()
        .flatMap(e -> createSpatialTemporalData(e.getKey(), e.getValue()))
        .forEach(e -> set.add(e));
    return set;
  }

  private Stream<SpatialTemporalOccupancyRecord> createSpatialTemporalData(
      final String vesselId, final TreeSet<VesselDataPoint> value
  ) {
    final List<SpatialTemporalOccupancyRecord> spatialTemporalOccupancyRecords = new ArrayList<>();
    final Iterator<VesselDataPoint> iterator = value.iterator();
    final Map<String, SliceTimeCounter> sliceTimeCounterMap = new HashMap<>();
    while (iterator.hasNext()) {
      final VesselDataPoint next = iterator.next();
      final Map<String, MapSlice> mapSlicesContainingPoint = mapSlicesOperations.getMapSlicesContainingPointByName(
          next);
      final long timestamp = next.getTimestamp();
      // Increment or Add To MapSliceCounting
      mapSlicesContainingPoint.forEach((sliceName, mapSlice) -> {
        sliceTimeCounterMap.computeIfPresent(sliceName, (s, sliceTimeCounter) -> {
          sliceTimeCounter.counter++;
          sliceTimeCounter.maxTs = Math.max(sliceTimeCounter.maxTs, timestamp);
          sliceTimeCounter.minTs = Math.min(sliceTimeCounter.minTs, timestamp);
          return sliceTimeCounter;
        });
        sliceTimeCounterMap.putIfAbsent(sliceName, new SliceTimeCounter(timestamp, timestamp, 1));
      });

      // Remove exited polygon and add to SpatialTemporalOccupancyRecord
      final List<Entry<String, SliceTimeCounter>> collect = sliceTimeCounterMap.entrySet()
          .stream()
          .filter(e -> !mapSlicesContainingPoint.containsKey(e.getKey()))
          .collect(Collectors.toList());
      for (final Entry<String, SliceTimeCounter> stringSliceTimeCounterEntry : collect) {
        final String sliceName = stringSliceTimeCounterEntry.getKey();
        final SliceTimeCounter sliceTimeCounter = stringSliceTimeCounterEntry.getValue();
        spatialTemporalOccupancyRecords.add(new SpatialTemporalOccupancyRecord(vesselId,
            sliceName,
            sliceTimeCounter.minTs,
            sliceTimeCounter.maxTs,
            sliceTimeCounter.counter
        ));
        sliceTimeCounterMap.remove(sliceName);
      }
    }

    sliceTimeCounterMap.forEach((sliceName, sliceTimeCounter) -> {
      spatialTemporalOccupancyRecords.add(new SpatialTemporalOccupancyRecord(vesselId,
          sliceName,
          sliceTimeCounter.minTs,
          sliceTimeCounter.maxTs,
          sliceTimeCounter.counter
      ));
    });
    return noiseCancel.cancelNoise(spatialTemporalOccupancyRecords).stream();
  }

  private static NavigableSet<SpatialTemporalOccupancyRecord> createTreeRecords() {
    return new TreeSet<>((o1, o2) -> Comparator.comparing(SpatialTemporalOccupancyRecord::getVesselId)
        .thenComparing(SpatialTemporalOccupancyRecord::getEntry)
        .thenComparing(SpatialTemporalOccupancyRecord::getExit)
        .thenComparing(SpatialTemporalOccupancyRecord::getPolygonName)
        .compare(o1, o2));
  }

  @Data
  @AllArgsConstructor
  public static class SliceTimeCounter {

    private long minTs;
    private long maxTs;
    private long counter;
  }

}
