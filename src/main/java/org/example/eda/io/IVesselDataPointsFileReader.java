package org.example.eda.io;

import io.vavr.control.Either;
import java.io.IOException;
import java.util.List;
import org.example.eda.data.VesselDataPoint;
import org.example.eda.io.VesselDataPointsCSVReader.ParseException;

public interface IVesselDataPointsFileReader {

  List<Either<ParseException, VesselDataPoint>> readFromFile(final String fileLocation) throws IOException;

}
