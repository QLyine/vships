package org.example.eda.noise;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import org.example.eda.SpatialTemporalOccupancyRecord;
import org.junit.Test;

public class BerthFrequencyNoiseCancelTest {

  @Test
  public void test_single() {
    final INoiseCancel duplicateNoiseCancel = BerthFrequencyNoiseCancel.of(0.8d);
    final List<SpatialTemporalOccupancyRecord> spatialTemporalOccupancyRecords = Arrays.asList(
        createSimpleRecord("berth_1", 1L, 1L, 1));
    final List<SpatialTemporalOccupancyRecord> filtered = duplicateNoiseCancel.cancelNoise(
        spatialTemporalOccupancyRecords);

    assertThat(filtered).hasSize(1);
  }


  @Test
  public void test_same_sequence_polygon() {
    final INoiseCancel duplicateNoiseCancel = BerthFrequencyNoiseCancel.of(0.8d);
    final List<SpatialTemporalOccupancyRecord> spatialTemporalOccupancyRecords = Arrays.asList(createSimpleRecord("berth_1", 1L, 2L, 1),
        createSimpleRecord("berth_1", 2L, 5L, 3)
    );
    final List<SpatialTemporalOccupancyRecord> filtered = duplicateNoiseCancel.cancelNoise(
        spatialTemporalOccupancyRecords);

    assertThat(filtered).hasSize(1);
    final SpatialTemporalOccupancyRecord record = filtered.get(0);
    assertThat(record.getPingCounts()).isEqualTo(4);
  }


  @Test
  public void test_flickering_sequence_polygon() {
    final INoiseCancel duplicateNoiseCancel = BerthFrequencyNoiseCancel.of(0.8d);
    final List<SpatialTemporalOccupancyRecord> spatialTemporalOccupancyRecords = Arrays.asList(
        createSimpleRecord("berth_1", 1L, 2L, 1),
        createSimpleRecord("berth_2", 2L, 5L, 3),
        createSimpleRecord("berth_1", 5L, 8L, 13)
    );
    final List<SpatialTemporalOccupancyRecord> filtered = duplicateNoiseCancel.cancelNoise(
        spatialTemporalOccupancyRecords);

    assertThat(filtered).hasSize(1);
    final SpatialTemporalOccupancyRecord record = filtered.get(0);
    assertThat(record.getPingCounts()).isEqualTo(14);
    assertThat(record.getExit()).isEqualTo(8L);
  }


  @Test
  public void test_flickering_sequence_polygon_where_confidence_is_not_attained() {
    final INoiseCancel duplicateNoiseCancel = BerthFrequencyNoiseCancel.of(0.8d);
    final List<SpatialTemporalOccupancyRecord> spatialTemporalOccupancyRecords = Arrays.asList(
        createSimpleRecord("berth_1", 1L, 2L, 10),
        createSimpleRecord("berth_2", 2L, 5L, 20),
        createSimpleRecord("berth_1", 5L, 8L, 10)
    );
    final List<SpatialTemporalOccupancyRecord> filtered = duplicateNoiseCancel.cancelNoise(
        spatialTemporalOccupancyRecords);

    assertThat(filtered).hasSize(3);
    assertFromListIndexValues(filtered, 0, 1L, 2L, 10);
    assertFromListIndexValues(filtered, 1, 2L, 5L, 20);
    assertFromListIndexValues(filtered, 2, 5L, 8L, 10);

  }


  private static final void assertFromListIndexValues(
      final List<SpatialTemporalOccupancyRecord> records,
      final int idx,
      final long entry,
      final long exit,
      final long count
  ) {
    final SpatialTemporalOccupancyRecord record = records.get(idx);
    assertThat(record.getEntry()).isEqualTo(entry);
    assertThat(record.getExit()).isEqualTo(exit);
    assertThat(record.getPingCounts()).isEqualTo(count);
  }

  private SpatialTemporalOccupancyRecord createSimpleRecord(
      final String polygon, final long entry, final long exit, final long count
  ) {
    return new SpatialTemporalOccupancyRecord("v1", polygon, entry, exit, count);
  }
}