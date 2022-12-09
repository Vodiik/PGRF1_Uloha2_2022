package render;

import raster.LineRasterizer;
import solids.Scene;
import solids.Solid;
import transforms.Mat4;
import transforms.Mat4Identity;
import transforms.Point3D;
import transforms.Vec3D;

public class WireRenderer implements Renderer {


    Mat4 view = new Mat4Identity();
    Mat4 projection = new Mat4Identity();
    LineRasterizer lineRasterizer;

    public WireRenderer(LineRasterizer lineRasterizer) {
        this.lineRasterizer = lineRasterizer;
    }

    @Override
    public void render(Scene scene) {
        for (Solid solid : scene.getSolids()
        ) {
            render(solid);
        }
    }

    @Override
    public void render(Solid solid) {
        Mat4 trans = solid.getModel().mul(view.mul(projection));
        for (int i = 0; i < solid.getIb().size(); i += 2) {
            int indexA = solid.getIb().get(i);
            int indexB = solid.getIb().get(i + 1);
            Point3D a = solid.getVb().get(indexA);
            Point3D b = solid.getVb().get(indexB);
            a = a.mul(trans);
            b = b.mul(trans);
            int color = solid.getColors().get(i / 2 % solid.getColors().size());
            render(a, b, color);
        }
    }

    private void render(Point3D a, Point3D b, int color) {

        // dehomog
        if (!a.dehomog().isPresent() || !b.dehomog().isPresent()) {
            return;// neregulerni trojuhelnik(w=0)

        }

        Vec3D va = a.dehomog().get();
        Vec3D vb = b.dehomog().get();
        //clip
        if (Math.min(va.getX(), vb.getX()) < -1.0D || Math.max(va.getX(), vb.getX()) > 1.0D ||
                Math.min(va.getY(), vb.getY()) < -1.0D || Math.max(va.getY(), vb.getY()) > 1.0D ||
                Math.min(va.getZ(), vb.getZ()) < 0.0D || Math.max(va.getZ(), vb.getZ()) > 1.0D) {
            return;
        }


        //3D->2D
        //viewport
        int x1, x2, y1, y2;
        x1 = (int) ((lineRasterizer.getRaster().getWidth() - 1) * (a.getX() + 1) / 2);
        y1 = (int) ((lineRasterizer.getRaster().getHeight() - 1) * (1 - a.getY()) / 2);
        x2 = (int) ((lineRasterizer.getRaster().getWidth() - 1) * (b.getX() + 1) / 2);
        y2 = (int) ((lineRasterizer.getRaster().getHeight() - 1) * (1 - b.getY()) / 2);

        //lineRasterizer
        lineRasterizer.rasterize(x1, y1, x2, y2, color);
    }

    @Override
    public void setView(Mat4 view) {
        this.view = view;
    }

    @Override
    public void setProjection(Mat4 projection) {
        this.projection = projection;
    }
}

