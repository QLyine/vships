package org.example.eda.io;

import io.vavr.control.Either;
import io.vavr.control.Try;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.eda.data.VesselDataPoint;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class VesselDataPointsCSVReader implements IVesselDataPointsFileReader {

  // 2020-06-16 17:27:30.000
  // DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
  private static DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS");

  public List<Either<ParseException, VesselDataPoint>> readFromFile(final String fileLocation)
      throws IOException {
    return Files.lines(Paths.get(fileLocation))
        .parallel()
        .skip(1)
        .map(e -> e.replace("\"", ""))
        .map(e -> e.split(","))
        .map(e -> map(e))
        .collect(Collectors.toList());
  }

  public static Either<ParseException, VesselDataPoint> map(String[] line) {
    String vesselId = line[0];
    String tsString = line[1];
    String latStr = readCellOnLine(line, 2, "0");
    String lonStr = readCellOnLine(line, 3, "0");
    String speedStr = readCellOnLine(line, 4, "0");
    String headingStr = readCellOnLine(line, 5, "0");

    Either<ParseException, Long> eitherDate = parseDate(tsString, "invalid ts -> " + tsString);
    Either<ParseException, Double> eitherLat = parseDouble(latStr, "invalid lat field - " + latStr);
    Either<ParseException, Double> eitherLon = parseDouble(lonStr, "invalid lon field - " + lonStr);
    Integer speed = parseIntField(speedStr, "invalid speed field - " + speedStr).getOrElse(0);
    Integer heading = parseIntField(headingStr, "invalid heading field - " + speedStr).getOrElse(0);

    return eitherDate.flatMap(tsMillis -> eitherLat.flatMap(lat -> eitherLon.map(lon -> new VesselDataPoint(vesselId,
        tsMillis,
        lat,
        lon,
        speed,
        heading
    ))));

  }

  private static final String readCellOnLine(
      String[] line,
      int cellNumber,
      final String defaultValue
  ) {
    if (line.length > cellNumber) {
      return line[cellNumber];
    }
    return defaultValue;
  }

  public static Either<ParseException, Integer> parseIntField(
      final String field, final String onErrorMsg
  ) {
    return Try.of(() -> Integer.parseInt(field))
        .toEither()
        .mapLeft(e -> new ParseException(onErrorMsg));
  }

  public static Either<ParseException, Long> parseDate(
      final String field, final String onErrorMsg
  ) {
    return Try.of(() -> formatter.parseDateTime(field))
        .toEither()
        .mapLeft(e -> new ParseException(onErrorMsg))
        .map(e -> e.getMillis());
  }

  public static Either<ParseException, Double> parseDouble(
      final String field, final String onErrorMsg
  ) {
    return Try.of(() -> Double.parseDouble(field))
        .toEither()
        .mapLeft(e -> new ParseException(onErrorMsg));
  }

  @Data
  @AllArgsConstructor
  public static class ParseException extends Exception {

    private String msg;
  }

}
