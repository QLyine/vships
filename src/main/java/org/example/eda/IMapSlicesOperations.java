package org.example.eda;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.example.eda.data.MapSlice;
import org.example.eda.data.MapSlices;
import org.example.eda.data.VesselDataPoint;

public interface IMapSlicesOperations {

  MapSlices getMapSlices();
  Optional<MapSlice> getAnyMapSliceContainingPoint(VesselDataPoint vesselDataPoint);
  List<MapSlice> getMapSlicesContainingPoint(VesselDataPoint vesselDataPoint);
  Map<String, MapSlice> getMapSlicesContainingPointByName(VesselDataPoint vesselDataPoint);

}
