package raster;

import model.Point;

import java.awt.*;
import java.awt.image.BufferedImage;

public class RasterBufferedImage implements Raster {
    private BufferedImage img;

    public RasterBufferedImage(final int width, final int height) {
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    public BufferedImage getImg() {
        return img;
    }

    @Override
    public void setPixel(int x, int y, int color) {
        if (isInWindow(x, y))
            img.setRGB(x, y, color);
    }

    @Override
    public int getPixel(Point point) {
        if (CheckPoint(point.getX(), point.getY())) {
            return img.getRGB(point.getX(), point.getY());
        }
        return 0;
    }

    private boolean CheckPoint(int x, int y) {
        return (x < getWidth() && x >= 0 && y >= 0 && y < getHeight());
    }

    @Override
    public void clear() {
        Graphics g = img.getGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, img.getWidth() - 1, img.getHeight() - 1);
    }

    @Override
    public int getWidth() {
        return img.getWidth();
    }

    @Override
    public int getHeight() {
        return img.getHeight();
    }

    public boolean isInWindow(int x, int y) {                                       //kotrola zda není mimo plátno (Clanvas)
        return (x >= 0 && y >= 0 && y < img.getHeight() && x < img.getWidth());
    }
}
