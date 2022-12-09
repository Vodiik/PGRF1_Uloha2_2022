package solids;

import transforms.Point3D;

public class Axis extends Solid {

    public Axis() {

        name = "Axis";

        vb.add(new Point3D(0, 0, 0));
        vb.add(new Point3D(1, 0, 0));
        vb.add(new Point3D(0, 1, 0));
        vb.add(new Point3D(0, 0, 1));

        addIndices(0, 1, 0, 2, 0, 3);

        colors.add(0xff0000);
        colors.add(0x00ff00);
        colors.add(0x0000ff);
    }
}
