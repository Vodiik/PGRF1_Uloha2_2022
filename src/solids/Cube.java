package solids;

import transforms.Point3D;

public class Cube extends Solid {

    public Cube() {

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
    }
}

