package solids;

import transforms.Point3D;

public class Tetrahedron extends Solid {
    public Tetrahedron() {
        name = "Tetrahedron";

        vb.add(new Point3D(1, 1, 1));
        vb.add(new Point3D(-1, -1, 1));
        vb.add(new Point3D(-1, 1, -1));
        vb.add(new Point3D(1, -1, -1));
        addIndices(0, 1, 2, 0, 2, 3, 0, 3, 1, 3, 2, 1);

        colors.add(0xffffff);
    }
}
