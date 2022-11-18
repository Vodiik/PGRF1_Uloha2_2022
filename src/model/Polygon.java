package model;

import java.util.ArrayList;
import java.util.List;

public class Polygon {
    private final List<Point> points;

    public Polygon() {
        points = new ArrayList<>();
    }

    public void addPoint(Point point) {
        points.add(point);
    }

    public void addPoint(int index, Point point) {
        points.add(index, point);
    }

    public List<Point> getPoints() {
        return points;
    }

    public void deletePoint(int index) {
        if (!points.isEmpty()) {
            points.remove(index);
        }

    }

    public void clearPoints() {
        points.clear();
    }

    public int findNearestPointIndex(Point mouseLocation) {// hledání indexu pointu nejblížší k pozici myše
        float length = Float.MAX_VALUE;
        int indexToEdit = 0;
        for (int i = 0; i < points.size(); i++) {
            float lengthBetweenPoints = (float) Math.sqrt((mouseLocation.getX() - points.get(i).getX()) * (mouseLocation.getX() - points.get(i).getX())
                    + (mouseLocation.getY() - points.get(i).getY()) * (mouseLocation.getY() - points.get(i).getY()));
            if (lengthBetweenPoints < length) {
                length = lengthBetweenPoints;
                indexToEdit = i;
            }
        }
        return indexToEdit;
    }

    public Point findNearestPointOnLine(Point mouseLocation) { // hledání nejbližšího bodu na přímce k myši. slouží k přidání bodu
        Point newPoint = mouseLocation;
        float distance = Float.MAX_VALUE;
        int newPointIndex = points.size();
        for (int i = 0; i < points.size(); i++) {
            if (i + 1 == points.size()) {
                newPoint = Line.nearestPoint(points.get(i), points.get(0), mouseLocation, distance);
                if (distance > newPoint.getDistance()) {
                    distance = newPoint.getDistance();
                    newPointIndex = points.size();
                }
            } else {
                newPoint = Line.nearestPoint(points.get(i), points.get(i + 1), mouseLocation, distance);
                if (distance > newPoint.getDistance()) {
                    distance = newPoint.getDistance();
                    newPointIndex = i + 1;
                }
            }
        }
        newPoint.setIndex(newPointIndex);
        return newPoint;
    }

    public Point getTrianglePoint() { // určení poslední bodu trojúhelníku
        Point point = new Point();
        int r = new Line(points.get(0), points.get(1)).length();
        int xCenter = Line.findCenter(points.get(0), points.get(1)).getX();
        int yCenter = Line.findCenter(points.get(0), points.get(1)).getY();
        float eps = 0.5f;
        r = r / 2;
        boolean notSet = true;
        for (float fi = 0f; fi < Math.PI / 2; fi += eps) {
            int x = (int) (r * Math.cos(fi));
            int y = (int) (r * Math.sin(fi));
            if (fi > Math.PI / 3 && notSet) {
                point.setY(y + yCenter);
                point.setX(xCenter - x);
                notSet = false;
            }
        }
        return point;
    }

}
