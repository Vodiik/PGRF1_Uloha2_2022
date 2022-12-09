package solids;

import transforms.Point3D;

import java.util.ArrayList;
import java.util.Arrays;

public class Cube extends Solid {

    public Cube() {
        name = "Cube";

        // Geometrie
        vb.add(new Point3D(-1, -1, -1));
        vb.add(new Point3D(-1, 1, -1));
        vb.add(new Point3D(1, 1, -1));
        vb.add(new Point3D(1, -1, -1));
        vb.add(new Point3D(-1, -1, 1));
        vb.add(new Point3D(-1, 1, 1));
        vb.add(new Point3D(1, 1, 1));
        vb.add(new Point3D(1, -1, 1));

        // Topologie
        addIndices(0, 1, 1, 2, 2, 3, 3, 0, 0, 4, 4, 5, 5, 1, 5, 6, 6, 2, 6, 7, 7, 3, 4, 7);

        setColors(new ArrayList<>(Arrays.asList(
                0x00FF00, 0xFF0000, 0x0000FF, 0xFF0000, 0x0000FF, 0x00FF00, 0x0000FF, 0x0000FF,0x00FF00,0x00FF00, 0xFF0000, 0xFF0000
        )));
    }
}

