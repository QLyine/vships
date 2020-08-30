package org.example.eda;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.example.eda.data.MapSlice;
import org.example.eda.data.MapSlices;
import org.example.eda.data.VesselDataPoint;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;

public class MapSlicesOperations implements IMapSlicesOperations {

  private final MapSlices mapSlices;

  public MapSlicesOperations(final MapSlices mapSlices) {
    this.mapSlices = mapSlices;
  }

  public MapSlices getMapSlices() {
    return mapSlices;
  }

  public Optional<MapSlice> getAnyMapSliceContainingPoint(final VesselDataPoint vesselDataPoint) {
    final Point point = createPoint(vesselDataPoint);
    for (final MapSlice slice : mapSlices.getListOfSlices()) {
      final String name = slice.getName();
      System.out.println(name);
      final Geometry geometry = slice.getGeometry();
      if (geometry.contains(point)) {
        return Optional.of(slice);
      }
    }
    return Optional.empty();
  }

  @Override
  public List<MapSlice> getMapSlicesContainingPoint(final VesselDataPoint vesselDataPoint) {
    return mapSlices.getListOfSlices()
        .stream()
        .filter(e -> e.getGeometry().contains(createPoint(vesselDataPoint)))
        .collect(Collectors.toList());
  }

  @Override
  public Map<String, MapSlice> getMapSlicesContainingPointByName(final VesselDataPoint vesselDataPoint) {
    return mapSlices.getListOfSlices()
        .stream()
        .filter(e -> e.getGeometry().contains(createPoint(vesselDataPoint)))
        .collect(Collectors.toMap(o -> o.getName(), o -> o));
  }


  public static Point createPoint(final VesselDataPoint vesselDataPoint) {
    return new Point(
        new CoordinateArraySequence(new Coordinate[]{
            new Coordinate(vesselDataPoint.getLon(), vesselDataPoint.getLat())}),
        new GeometryFactory()
    );
  }

}
