package org.example.eda.io;

import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import org.example.eda.SpatialTemporalOccupancyRecord;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class CSVSpatialTemporalDataWriter implements ISpatialTemporalDataWriter {

  private static final DateTimeFormatter DTF = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS");
  private static final String[] HEADER = new String[]{"polygon_name", "vessel_id", "entry_ts",
      "exit_ts", "ping_count"};
  private final String fileLocation;

  public CSVSpatialTemporalDataWriter(final String location) {
    this.fileLocation = location;
  }

  @Override
  public void write(final Collection<SpatialTemporalOccupancyRecord> records) {
    File file = new File(fileLocation);
    try {
      // create FileWriter object with file as parameter
      FileWriter outputfile = new FileWriter(file);

      // create CSVWriter object filewriter object as parameter
      CSVWriter writer = new CSVWriter(outputfile);

      // adding header to csv
      writer.writeNext(HEADER);

      for (final SpatialTemporalOccupancyRecord record : records) {
        writer.writeNext(mapToData(record));
      }

      // closing writer connection
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static String[] mapToData(SpatialTemporalOccupancyRecord record) {
    return new String[]{
        record.getPolygonName(),
        record.getVesselId(),
        DTF.print(record.getEntry()),
        DTF.print(record.getExit()),
        record.getPingCounts() + ""}
        ;
  }
}
