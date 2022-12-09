package solids;

import transforms.Point3D;

public class Pyramid extends Solid {
    public Pyramid() {
        name = "Pyramid";
        vb.add(new Point3D(-1, -1, -1));
        vb.add(new Point3D(-1, 1, -1));
        vb.add(new Point3D(1, 1, -1));
        vb.add(new Point3D(1, -1, -1));
        vb.add(new Point3D(0, 0, 2));

        addIndices(0, 1, 1, 2, 2, 3, 3, 0, 0, 4, 3, 4, 2, 4, 1, 4);

        colors.add(0xff00ff);
    }
}
