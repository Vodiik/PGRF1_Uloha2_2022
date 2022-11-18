package fill;

import raster.Raster;

public abstract class Filler {
    protected Raster raster;

    public Filler(Raster raster) {
        this.raster = raster;
    }

    public abstract void fill();
}
