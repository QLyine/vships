package org.example.eda.io;


import static org.assertj.core.api.Assertions.assertThat;

import io.vavr.control.Either;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import org.example.eda.data.VesselDataPoint;
import org.example.eda.io.VesselDataPointsCSVReader.ParseException;
import org.junit.Test;

public class VesselDataPointsCSVReaderTest {

  @Test
  public void readFromFile() throws IOException {
    String fileLocation = "ship_positions.csv";
    VesselDataPointsCSVReader vesselDataPointsCSVReader = new VesselDataPointsCSVReader();
    List<Either<ParseException, VesselDataPoint>> vesselDataPoints = vesselDataPointsCSVReader.readFromFile(
        fileLocation);

    long count = Files.lines(Paths.get(fileLocation)).count();

    assertThat(vesselDataPoints).isNotNull();
    assertThat(vesselDataPoints).hasSize((int) (count - 1));
    vesselDataPoints.stream().filter(e -> e.isLeft())
        .forEach(e -> System.out.println(e));
    assertThat(vesselDataPoints.stream().anyMatch(e -> e.isLeft())).isFalse();
  }
}