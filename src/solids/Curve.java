package solids;

import transforms.Cubic;
import transforms.Mat4;
import transforms.Point3D;

public class Curve extends Solid {
    public Curve(Mat4 type) {
        name = "Curve";

        updateType(type);
        addIndices(0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10);

        colors.add(0xff0000);
    }

    public void updateType(Mat4 type) {
        vb.clear();
        // ridici body
        Point3D r1 = new Point3D(3, 1, 1);
        Point3D r2 = new Point3D(1.5, 2, 2);
        Point3D r3 = new Point3D(1, 1, 3);
        Point3D r4 = new Point3D(1, 3, 3);

        Cubic cub = new Cubic(type, r1, r2, r3, r4);
        double param = 0.1;
        for (double i = 0; i <= 1; i += param) {

            Point3D res = cub.compute(i);
            vb.add(new Point3D(res));
        }
    }
}
