package org.example.eda;


import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.NavigableSet;
import org.example.eda.data.MapSlices;
import org.example.eda.data.VesselDataPoint;
import org.example.eda.io.MapSlicesGeoJSONReader;
import org.example.eda.noise.INoiseCancel;
import org.junit.Before;
import org.junit.Test;

public class GenerateSpatialTemporalOccupancyTest {

  private MapSlicesOperations mapSlicesOperations;
  private INoiseCancel noiseCancel;

  @Before
  public void setUp() throws IOException {
    final MapSlices mapSlices = new MapSlicesGeoJSONReader().readFromFile("polygons.geojson");
    mapSlicesOperations = new MapSlicesOperations(mapSlices);
    noiseCancel = new NoNoiseCancel();
  }

  @Test
  public void test_empty_result() {
    final GenerateSpatialTemporalOccupancy generateSpatialTemporalOccupancy = new GenerateSpatialTemporalOccupancy(
        mapSlicesOperations,
        noiseCancel
    );
    final NavigableSet<SpatialTemporalOccupancyRecord> generate = generateSpatialTemporalOccupancy.generate(
        Collections.emptyList());

    assertThat(generate).isNotNull();
    assertThat(generate).isEmpty();
  }


  @Test
  public void test_single_channel_west() {
    final GenerateSpatialTemporalOccupancy generateSpatialTemporalOccupancy = new GenerateSpatialTemporalOccupancy(
        mapSlicesOperations,
        noiseCancel
    );

    final VesselDataPoint vessel1 = new VesselDataPoint("vessel1", 1L, 29.7416, -95.1309, 0, 0);
    final NavigableSet<SpatialTemporalOccupancyRecord> generate = generateSpatialTemporalOccupancy.generate(
        Collections.singletonList(vessel1));

    assertThat(generate).isNotNull();
    assertThat(generate).hasSize(1);
    final SpatialTemporalOccupancyRecord first = generate.first();
    assertThat(first.getVesselId()).isEqualTo(vessel1.getVesselId());
    assertThat(first.getEntry()).isEqualTo(1L);
    assertThat(first.getExit()).isEqualTo(1L);
    assertThat(first.getPingCounts()).isEqualTo(1L);
    assertThat(first.getPolygonName()).isEqualTo("channel_west");
  }


  @Test
  public void test_twice_channel_west() {
    final GenerateSpatialTemporalOccupancy generateSpatialTemporalOccupancy = new GenerateSpatialTemporalOccupancy(
        mapSlicesOperations,
        noiseCancel
    );

    final VesselDataPoint vessel1a = new VesselDataPoint("vessel1", 1L, 29.7416, -95.1309, 0, 0);
    final VesselDataPoint vessel1b = new VesselDataPoint("vessel1", 3L, 29.7416, -95.1309, 0, 0);
    final NavigableSet<SpatialTemporalOccupancyRecord> generate = generateSpatialTemporalOccupancy.generate(
        Arrays.asList(vessel1a, vessel1b));

    assertThat(generate).isNotNull();
    assertThat(generate).hasSize(1);
    final SpatialTemporalOccupancyRecord record1 = generate.pollFirst();
    assertSpatialRecord(record1, "vessel1", 1L, 3L, 2, "channel_west");
  }

  @Test
  public void test_twice_blank_channel_west() {
    final GenerateSpatialTemporalOccupancy generateSpatialTemporalOccupancy = new GenerateSpatialTemporalOccupancy(
        mapSlicesOperations,
        noiseCancel
    );

    final VesselDataPoint vessel1a = new VesselDataPoint("vessel1", 1L, 29.7416, -95.1309, 0, 0);
    final VesselDataPoint vessel1b = new VesselDataPoint("vessel1", 2L, 329.7416, -195.1309, 0, 0);
    final VesselDataPoint vessel1c = new VesselDataPoint("vessel1", 3L, 29.7416, -95.1309, 0, 0);
    final NavigableSet<SpatialTemporalOccupancyRecord> generate = generateSpatialTemporalOccupancy.generate(
        Arrays.asList(vessel1a, vessel1b, vessel1c));

    assertThat(generate).isNotNull();
    assertThat(generate).hasSize(2);
    final SpatialTemporalOccupancyRecord record1 = generate.pollFirst();
    assertSpatialRecord(record1, "vessel1", 1L, 1L, 1, "channel_west");
    final SpatialTemporalOccupancyRecord record2 = generate.pollFirst();
    assertSpatialRecord(record2, "vessel1", 3L, 3L, 1, "channel_west");
  }


  @Test
  public void test_twice_channel_west_and_east() {
    final GenerateSpatialTemporalOccupancy generateSpatialTemporalOccupancy = new GenerateSpatialTemporalOccupancy(
        mapSlicesOperations,
        noiseCancel
    );

    final VesselDataPoint vessel1a = new VesselDataPoint("vessel1", 1L, 29.7416, -95.1309, 0, 0);
    final VesselDataPoint vessel1b = new VesselDataPoint("vessel1", 2L, 29.7416, -95.1309, 0, 0);
    final VesselDataPoint vessel1c = new VesselDataPoint("vessel1", 3L, 29.7405, -95.1090, 0, 0);
    final NavigableSet<SpatialTemporalOccupancyRecord> generate = generateSpatialTemporalOccupancy.generate(
        Arrays.asList(vessel1a, vessel1b, vessel1c));

    assertThat(generate).isNotNull();
    assertThat(generate).hasSize(2);
    final SpatialTemporalOccupancyRecord record1 = generate.pollFirst();
    assertSpatialRecord(record1, "vessel1", 1L, 2L, 2, "channel_west");
    final SpatialTemporalOccupancyRecord record2 = generate.pollFirst();
    assertSpatialRecord(record2, "vessel1", 3L, 3L, 1, "channel_east");
  }

  private void assertSpatialRecord(
      final SpatialTemporalOccupancyRecord first,
      final String vesselId,
      final long startTs,
      final long endTs,
      final int counter,
      final String channel
  ) {
    assertThat(first.getVesselId()).isEqualTo(vesselId);
    assertThat(first.getEntry()).isEqualTo(startTs);
    assertThat(first.getExit()).isEqualTo(endTs);
    assertThat(first.getPingCounts()).isEqualTo(counter);
    assertThat(first.getPolygonName()).isEqualTo(channel);
  }

}