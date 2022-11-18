package raster;

import model.Point;

public interface Raster {
    void setPixel(int x, int y, int color);

    int getPixel(Point point);

    void clear();

    int getWidth();

    int getHeight();

}
