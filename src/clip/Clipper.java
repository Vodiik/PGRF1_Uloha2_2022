package clip;

import model.Edge;
import model.Point;
import model.Polygon;

import java.util.ArrayList;
import java.util.List;

public class Clipper {

    public Polygon clip(Polygon sourcePolygon, Polygon clipPolygon) {
        // kontrola zda je dostatek bodů pro ořezání
        if (sourcePolygon.getPoints().size() < 2 && clipPolygon.getPoints().size() < 2) {
            return sourcePolygon;
        }
        Polygon resultPolygon = new Polygon(); // polygon pro výsledné body

        List<Edge> edgeClipPolygon = new ArrayList<>(); // list hran

        for (int i = 0; i < clipPolygon.getPoints().size(); i++) { // vytvoření hran z ořezávacího polygonu
            int idx = (i + 1) % clipPolygon.getPoints().size();
            Edge edge = new Edge(clipPolygon.getPoints().get(i), clipPolygon.getPoints().get(idx));
            edgeClipPolygon.add(edge);
        }

        for (Edge edgeClip : edgeClipPolygon) {
            Point v1 = sourcePolygon.getPoints().get(sourcePolygon.getPoints().size() - 1); // bod původního polygonu
            for (Point v2 : sourcePolygon.getPoints()) { // průchod druhých bodů z původního polygonu
                if (isInside(v2, edgeClip)) {
                    if (!isInside(v1, edgeClip)) {
                        resultPolygon.addPoint(intersection(v1, v2, edgeClip));
                    }
                    resultPolygon.addPoint(v2);
                } else {
                    if (isInside(v1, edgeClip)) {
                        resultPolygon.addPoint(intersection(v1, v2, edgeClip));
                    }
                }
                v1 = v2;
            }
        }
        return resultPolygon;
    }

    private boolean isInside(Point point, Edge edge) { // výpočet zda je bod vzhledem k přímce uvnitř výpočet z přednášky
        Point v1 = new Point(edge.end.getX() - edge.start.getX(), edge.end.getY() - edge.start.getY());
        Point n = new Point(-v1.getY(), v1.getX());
        Point v2 = new Point(point.getX() - edge.start.getX(), point.getY() - edge.start.getY());
        int s = (n.getX() * v2.getX() + n.getY() * v2.getY());
        return (s < 0.0D);
    }

    private Point intersection(Point v1, Point v2, Edge edge) { // výpočet průsečíku mezi dvěmi body a hrany
        Point v3 = edge.start;
        Point v4 = edge.end;
        int x = ((((v1.getX() * v2.getY()) - (v2.getX()) * v1.getY()) * (v3.getX() - v4.getX())) -
                (((v3.getX() * v4.getY()) - (v4.getX() * v3.getY())) * (v1.getX() - v2.getX()))) /
                (((v1.getX() - v2.getX()) * (v3.getY() - v4.getY())) - ((v1.getY() - v2.getY()) * (v3.getX() - v4.getX())));
        int y = ((((v1.getX() * v2.getY()) - (v2.getX() * v1.getY())) * (v3.getY() - v4.getY())) -
                (((v3.getX() * v4.getY()) - (v4.getX() * v3.getY())) * (v1.getY() - v2.getY()))) /
                (((v1.getX() - v2.getX()) * (v3.getY() - v4.getY())) - ((v1.getY() - v2.getY()) * (v3.getX() - v4.getX())));
        return new Point(x, y);
    }
}
