package render;

import raster.LineRasterizer;
import solids.Scene;
import solids.Solid;
import transforms.Mat4;

public interface Renderer {
    void render(Scene scene);
    void render(Solid solid);
    void setView(Mat4 view);
    void setProjection(Mat4 projection);
    void setRasterizer(LineRasterizer lineRasterizer);

}