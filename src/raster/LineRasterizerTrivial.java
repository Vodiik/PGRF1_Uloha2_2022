package raster;

public class LineRasterizerTrivial extends LineRasterizer {

    public LineRasterizerTrivial(Raster raster) {
        super(raster);
    }

    /**
     *    explicitní vyjádření funkce přímky
     *    y = f(x) = kx + q
     *    x = f(y) = (y - q) / k;
     *    -------------------------------------
     *    k je směrnice přímky, vyjadřuje sklon (tg alpha) „o kolik se změní y pokud x se změní o 1“
     *    q je posunutí na ose y, „místo kde úsečka protíná osu y“
     *   --------------------------------------
     *    Pro rasterizaci byl použit Triviální algoritmus. Výhodou tohoto algoritmu je například
     *    využití pro všechny druhy křivek, po úpravě do všech kvadrantů. Nevýhody algoritmu je
     *    zapotřebý zaokrouhlovat, sčítání a násobení pouze v desetinných číslech nebo je velmi náročný pro výpočet.
     */

    @Override
    public void rasterize(int x1, int y1, int x2, int y2, int color) {
        float k, q;
        k = ((float) (y2 - y1) / (x2 - x1));
        if (Math.abs(y2 - y1) < Math.abs(x2 - x1)) {            // zjištění řídící osy
            if (x2 < x1) {                                      // výměna koncových bodů
                int temp = x1;
                x1 = x2;
                x2 = temp;
                temp = y1;
                y1 = y2;
                y2 = temp;
            }
            q = y1 - (k * x1);
            for (int x = x1; x <= x2; x++) {                    // vykreslení - řídící osa X
                float y = (k * x) + q;
                raster.setPixel(x, (int) y, 0xffff00);
            }
        } else {
            if (y2 < y1) {                                      // výměna koncových bodů
                int temp = x1;
                x1 = x2;
                x2 = temp;
                temp = y1;
                y1 = y2;
                y2 = temp;
            }
            q = y1 - (k * x1);
            for (int y = y1; y <= y2; y++) {                    // vykreslení pomocí řídící osy Y
                float x = (y - q) / k;
                if (x1 == x2) {
                    x = x1;
                }
                raster.setPixel((int) x, y, 0xffff00);
            }
        }
    }
}






































