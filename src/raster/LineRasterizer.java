package raster;

import model.Line;

public abstract class LineRasterizer {
    protected final Raster raster;

    public Raster getRaster() {
        return raster;
    }

    public LineRasterizer(Raster raster) {
        this.raster = raster;
    }

    public void rasterize(int x1, int y1, int x2, int y2) {
    }

    public void rasterize(int x1, int y1, int x2, int y2, int color) {
        rasterize(x1, y1, x2, y2);
    }

    public void rasterize(Line line, int color) {
        rasterize(line.getX1(), line.getY1(), line.getX2(), line.getY2(), color);
    }

    public void rasterize(Line line) {
        rasterize(line.getX1(), line.getY1(), line.getX2(), line.getY2());
    }
}
