package solids;

import transforms.Point3D;

public class Roof extends Solid {
    public Roof() {
        name = "Roof";
        vb.add(new Point3D(-1, -1, -1));
        vb.add(new Point3D(-1, 1, -1));
        vb.add(new Point3D(1, 1, -1));
        vb.add(new Point3D(1, -1, -1));
        vb.add(new Point3D(-1, -1, 1));
        vb.add(new Point3D(-1, 1, 1));

        addIndices(0, 1, 1, 2, 2, 3, 3, 0, 3, 4, 4, 5, 5, 2, 5, 1, 4, 0);

        colors.add(0xffff00);
    }
}
