package org.example.eda.noise;

import com.google.common.base.Preconditions;
import io.vavr.Tuple2;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import org.example.eda.SpatialTemporalOccupancyRecord;

public class BerthFrequencyNoiseCancel implements INoiseCancel {

  private final double confidence;

  private BerthFrequencyNoiseCancel(final double confidence) {
    this.confidence = confidence;
  }

  private static final String BERTH_POLYGON_NAME_START = "berth_";

  @Override
  public List<SpatialTemporalOccupancyRecord> cancelNoise(final List<SpatialTemporalOccupancyRecord> records) {
    final TreeSet<SpatialTemporalOccupancyRecord> orderedSequence = new TreeSet<>((o1, o2) -> Comparator
        .comparing(SpatialTemporalOccupancyRecord::getEntry)
        .thenComparing(SpatialTemporalOccupancyRecord::getExit)
        .thenComparing(SpatialTemporalOccupancyRecord::getPolygonName)
        .compare(o1, o2));
    orderedSequence.addAll(records);
    final List<SpatialTemporalOccupancyRecord> berthsToAnalyze = new ArrayList<>();
    final List<SpatialTemporalOccupancyRecord> result = new ArrayList<>();
    boolean inMiddleOfBerAnalysis = false;

    for (final SpatialTemporalOccupancyRecord record : orderedSequence) {
      if (record.getPolygonName().startsWith(BERTH_POLYGON_NAME_START)) {
        inMiddleOfBerAnalysis = true;
        berthsToAnalyze.add(record);
      } else {
        if (inMiddleOfBerAnalysis) {
          result.addAll(analyze(berthsToAnalyze));
          inMiddleOfBerAnalysis = false;
          berthsToAnalyze.clear();
        }
        // NOT A BERTH
        result.add(record);
      }
    }

    // IN case the last element was a berth
    if (inMiddleOfBerAnalysis) {
      result.addAll(analyze(berthsToAnalyze));
      berthsToAnalyze.clear();
    }

    return result;
  }

  public List<SpatialTemporalOccupancyRecord> analyze(final List<SpatialTemporalOccupancyRecord> berthFlickeringToAnalyze) {
    final Map<String, SpatialTemporalOccupancyRecord> countByBerth = new HashMap<>();
    int totalPingCounts = 0;
    for (final SpatialTemporalOccupancyRecord record : berthFlickeringToAnalyze) {
      countByBerth.computeIfPresent(record.getPolygonName(), (k, value) -> {
        value.setPingCounts(value.getPingCounts() + record.getPingCounts());
        value.setExit(Math.max(value.getExit(), record.getExit()));
        return value;
      });
      countByBerth.putIfAbsent(record.getPolygonName(), copyFromRecord(record));
      totalPingCounts += record.getPingCounts();
    }
    if (totalPingCounts == 0) {
      return berthFlickeringToAnalyze;
    }
    final int finalTotalPing = totalPingCounts;
    return countByBerth.entrySet()
        .stream()
        .map(e -> new Tuple2<>(e.getKey(),
            calculateFrequency(e.getValue().getPingCounts(), finalTotalPing)
        ))
        .filter(e -> e._2 >= confidence)
        .max(Comparator.comparing(Tuple2::_2))
        .map(e -> Collections.singletonList(countByBerth.get(e._1())))
        .orElse(berthFlickeringToAnalyze);
  }

  double calculateFrequency(final long pingCount, final int totalPingCounts) {
    return (double) (pingCount) / (double) (totalPingCounts);
  }

  public static final BerthFrequencyNoiseCancel of(final double confidence) {
    Preconditions.checkArgument(confidence >= 0.5d || confidence <= 1d,
        "Confidence has to be a value between 0.5 and 1.0"
    );
    return new BerthFrequencyNoiseCancel(confidence);
  }

  private SpatialTemporalOccupancyRecord copyFromRecord(final SpatialTemporalOccupancyRecord record) {
    return new SpatialTemporalOccupancyRecord(record.getVesselId(),
        record.getPolygonName(),
        record.getEntry(),
        record.getExit(),
        record.getPingCounts()
    );
  }


}
