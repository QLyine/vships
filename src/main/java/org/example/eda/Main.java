package org.example.eda;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.wololo.geojson.Feature;
import org.wololo.geojson.FeatureCollection;
import org.wololo.geojson.GeoJSONFactory;
import org.wololo.jts2geojson.GeoJSONReader;

public class Main {

  public static void main(final String[] args) throws IOException, ParseException {
    final byte[] bytes = Files.readAllBytes(Paths.get("polygons.geojson"));

    final String json = new String(bytes);

    //Feature feature = (Feature) GeoJSONFactory.create(json);
    final FeatureCollection featureCollection = (FeatureCollection) GeoJSONFactory.create(json);

    // parse Geometry from Feature
    final GeoJSONReader reader = new GeoJSONReader();

    final Point point = createPoint();

    Geometry berth_east = null;
    Geometry berth_west = null;
    for (final Feature feature : featureCollection.getFeatures()) {
      final Geometry geometry = reader.read(feature.getGeometry());

      final String name = (String) feature.getProperties().get("name");

      if (name.equals("berth_east")) {
        berth_east = geometry;
      }

      if (name.equals("berth_west")) {
        berth_west = geometry;
      }
      System.out.println(geometry);

      System.out.println(geometry.contains(point));
    }

    final double distance = berth_east.distance(berth_west);

    System.out.println(distance);
  }

  public static Point createPoint() {
    final double lat = (double) 29.741796666666666;
    final double lan = (double) -95.15984666666667;
    return new Point(
        new CoordinateArraySequence(new Coordinate[]{new Coordinate(lat, lan)}),
        new GeometryFactory()
    );
  }
}
