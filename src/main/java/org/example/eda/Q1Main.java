package org.example.eda;

import java.io.IOException;
import java.util.List;
import java.util.NavigableSet;
import java.util.stream.Collectors;
import org.example.eda.data.MapSlices;
import org.example.eda.data.VesselDataPoint;
import org.example.eda.io.CSVSpatialTemporalDataWriter;
import org.example.eda.io.MapSlicesGeoJSONReader;
import org.example.eda.io.VesselDataPointsCSVReader;

public class Q1Main {

  public static final String RESULT_CSV = "out.csv";

  public static void main(final String[] args) throws IOException {
    final MapSlices mapSlices = new MapSlicesGeoJSONReader().readFromFile("polygons.geojson");
    final MapSlicesOperations mapSlicesOperations = new MapSlicesOperations(mapSlices);
    final List<VesselDataPoint> collect = new VesselDataPointsCSVReader().readFromFile(
        "ship_positions.csv")
        .stream()
        .filter(e -> e.isRight())
        .map(e -> e.get())
        .collect(Collectors.toList());

    final GenerateSpatialTemporalOccupancy generateSpatialTemporalOccupancy = new GenerateSpatialTemporalOccupancy(mapSlicesOperations,
        new NoNoiseCancel()
    );

    final NavigableSet<SpatialTemporalOccupancyRecord> generate = generateSpatialTemporalOccupancy.generate(
        collect);

    final CSVSpatialTemporalDataWriter csvSpatialTemporalDataWriter = new CSVSpatialTemporalDataWriter(
        RESULT_CSV);

    csvSpatialTemporalDataWriter.write(generate);
  }

}
