package org.example.eda.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class MapSlices {

  private List<MapSlice> listOfSlices;
  private Map<String, MapSlice> mapSlicesByName;

  public MapSlices() {
    mapSlicesByName = new HashMap<>();
    listOfSlices = new ArrayList<>();
  }

  public void add(MapSlice mapSlice) {
    listOfSlices.add(mapSlice);
    mapSlicesByName.put(mapSlice.getName(), mapSlice);
  }

}
