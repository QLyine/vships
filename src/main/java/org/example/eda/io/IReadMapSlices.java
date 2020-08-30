package org.example.eda.io;

import java.io.IOException;
import org.example.eda.data.MapSlices;

public interface IReadMapSlices {

  MapSlices readFromFile(final String fileLocation) throws IOException;

}
