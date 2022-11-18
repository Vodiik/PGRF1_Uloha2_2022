package fill;

import model.Point;
import raster.Raster;

public class SeedFiller extends Filler {

    private int backgroundColor = 0;

    private Point seedPoint;

    private PatternFill patternFill = null;

    private int fillColor = 0x4F0000;

    public SeedFiller(Raster raster) {
        super(raster);
    }

    @Override
    public void fill() {
        backgroundColor = raster.getPixel(seedPoint); // nastavení barvy pozadí
        // kontrola zda pixel již není obarven barvou a zároveň se nejedná o pattern vyplnění
        // kontrola zda již není obarven z důvodu stackoverflow chyby při opakovaném vybarvení bez změny nastavení
        if (!Integer.toHexString(raster.getPixel(seedPoint)).equals("ff" + Integer.toHexString(fillColor)) && patternFill == null) {
            seedFiller(seedPoint.getX(), seedPoint.getY(), fillColor);
        } else if (patternFill != null) { // pokud je zvolen nějaký pattern
            if (!Integer.toHexString(raster.getPixel(seedPoint)).equals // kontrola zda se pixel nervoná barvě podle aktuálního patternu
                    ("ff" + Integer.toHexString(patternFill.getColor(seedPoint.getX(), seedPoint.getY())))) {
                seedFiller(seedPoint.getX(), seedPoint.getY(), fillColor);
            }
        }
    }

    private void seedFiller(int x, int y, int color) {
        if (isInside(x, y) && isInRaster(x, y)) { // pokud je uvnitř (kontrola barvy pozadí a souřadnic podle velikosti rasteru)
            if (patternFill != null) { // pokud je zvolen pattern získa barvu na obarvení z něho
                raster.setPixel(x, y, patternFill.getColor(x, y));
            } else {
                raster.setPixel(x, y, color);
            }
            seedFiller(x + 1, y, color);
            seedFiller(x - 1, y, color);
            seedFiller(x, y - 1, color);
            seedFiller(x, y + 1, color);
        }
    }

    private boolean isInside(int x, int y) {
        return raster.getPixel(new Point(x, y)) == backgroundColor;
    }

    private boolean isInRaster(int x, int y) {
        return raster.getHeight() > y && y >= 0 && raster.getWidth() > x && x >= 0;
    }

    public void setSeedPoint(Point seedPoint) {
        this.seedPoint = seedPoint;
    }

    public void setPatternFill(PatternFill patternFill) {
        this.patternFill = patternFill;
    }
}
