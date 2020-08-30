package org.example.eda.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.locationtech.jts.geom.Geometry;

@Data
@AllArgsConstructor
public class MapSlice {

  private String name;
  private Geometry geometry;

}
