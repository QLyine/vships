package org.example.eda.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import org.example.eda.data.MapSlice;
import org.example.eda.data.MapSlices;
import org.locationtech.jts.geom.Geometry;
import org.wololo.geojson.Feature;
import org.wololo.geojson.FeatureCollection;
import org.wololo.geojson.GeoJSONFactory;
import org.wololo.jts2geojson.GeoJSONReader;

public class MapSlicesGeoJSONReader implements IReadMapSlices {

  public static final String NAME = "name";
  public static final String INVALID_NAME = "invalid_name";

  @Override
  public MapSlices readFromFile(final String fileLocation) throws IOException {

    byte[] bytes = Files.readAllBytes(Paths.get("polygons.geojson"));

    String json = new String(bytes);

    //Feature feature = (Feature) GeoJSONFactory.create(json);
    FeatureCollection featureCollection = (FeatureCollection) GeoJSONFactory.create(json);

    // parse Geometry from Feature
    GeoJSONReader reader = new GeoJSONReader();

    MapSlices mapSlices = new MapSlices();
    for (final Feature feature : featureCollection.getFeatures()) {
      Geometry geometry = reader.read(feature.getGeometry());
      String name = Optional.ofNullable(feature.getProperties())
          .map(e -> e.get(NAME))
          .map(e -> e.toString())
          .orElse(INVALID_NAME);
      mapSlices.add(new MapSlice(name, geometry));
    }
    return mapSlices;
  }
}
