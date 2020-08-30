package org.example.eda.io;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.example.eda.io.MapSlicesGeoJSONReader.INVALID_NAME;

import java.io.IOException;
import org.example.eda.data.MapSlice;
import org.example.eda.data.MapSlices;
import org.junit.Test;

public class MapSlicesGeoJSONReaderTest {

  @Test
  public void testRead() throws IOException {
    MapSlicesGeoJSONReader mapSlicesGeoJSONReader = new MapSlicesGeoJSONReader();
    MapSlices mapSlices = mapSlicesGeoJSONReader.readFromFile("polygons.geojson");
    assertThat(mapSlices).isNotNull();
    assertThat(mapSlices.getListOfSlices()).isNotNull();
    assertThat(mapSlices.getListOfSlices().size()).isEqualTo(4);

    for (final MapSlice listOfSlice : mapSlices.getListOfSlices()) {
      assertThat(listOfSlice.getName()).isNotEqualTo(INVALID_NAME);
      assertThat(listOfSlice.getGeometry()).isNotNull();
    }
  }
}