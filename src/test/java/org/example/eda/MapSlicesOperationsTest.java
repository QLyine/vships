package org.example.eda;


import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.example.eda.data.MapSlice;
import org.example.eda.data.MapSlices;
import org.example.eda.data.VesselDataPoint;
import org.example.eda.io.MapSlicesGeoJSONReader;
import org.junit.Before;
import org.junit.Test;

public class MapSlicesOperationsTest {

  private MapSlicesOperations mapSlicesOperations;
  @Before
  public void setUp() throws IOException {
    MapSlices mapSlices = new MapSlicesGeoJSONReader().readFromFile("polygons.geojson");
    mapSlicesOperations = new MapSlicesOperations(mapSlices);
  }

  @Test
  public void vesselDataPointIsInsideChannelWest() {
    VesselDataPoint vessel1 = new VesselDataPoint("vessel1", 1L, 29.7416, -95.1309, 0, 0);
    Optional<MapSlice> mapSlice = mapSlicesOperations.getAnyMapSliceContainingPoint(
        vessel1);

    assertThat(mapSlice).isPresent();
    assertThat(mapSlice.get().getName()).isEqualTo("channel_west");
  }


  @Test
  public void vesselDataPointIsInsideChannelWestAndBerthWest() {
    VesselDataPoint vessel1 = new VesselDataPoint("vessel1", 1L, 	29.7374, 	-95.1274, 0, 0);
    List<MapSlice> mapSlices = mapSlicesOperations.getMapSlicesContainingPoint(
        vessel1);

    assertThat(mapSlices).isNotNull();
    assertThat(mapSlices).hasSize(2);
    List<String> names = mapSlices.stream().map(e -> e.getName()).distinct().collect(Collectors.toList());
    assertThat(names).hasSize(2);
    assertThat(names).containsExactlyInAnyOrder("channel_west", "berth_west");
  }
}