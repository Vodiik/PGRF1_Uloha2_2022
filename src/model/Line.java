package model;

public class Line {
    private final int x1, y1, x2, y2;


    public Line(Point start, Point end) {
        x1 = start.getX();
        y1 = start.getY();
        x2 = end.getX();
        y2 = end.getY();
    }

    public int getX1() {
        return x1;
    }


    public int getY1() {
        return y1;
    }


    public int getX2() {
        return x2;
    }


    public int getY2() {
        return y2;
    }


    public int length() {
        int length = 0;
        int x1 = getX1(), x2 = getX2(), y1 = getY1(), y2 = getY2();
        float k, q;
        k = ((float) (y2 - y1) / (x2 - x1));
        if (Math.abs(y2 - y1) < Math.abs(x2 - x1)) {                    // zjištění řídící osy
            if (x2 < x1) {                                              // výměna koncových bodů
                int temp = x1;
                x1 = x2;
                x2 = temp;
                temp = y1;
                y1 = y2;
                y2 = temp;
            }
            q = y1 - (k * x1);
            for (int x = x1; x <= x2; x++) {                            // vykreslení - řídící osa X
                float y = (k * x) + q;
                length++;
            }
        } else {
            if (y2 < y1) {                                              // výměna koncových bodů
                int temp = x1;
                x1 = x2;
                x2 = temp;
                temp = y1;
                y1 = y2;
                y2 = temp;
            }
            q = y1 - (k * x1);
            for (int y = y1; y <= y2; y++) {                            // vykreslení pomocí řídící osy Y
                float x = (y - q) / k;
                if (x1 == x2) {
                    x = x1;
                }
                length++;
            }
        }
        return length;
    }

    public static Point nearestPoint(Point pointLine1, Point pointLine2, Point mouseLocation, float length) {
        Point newPoint = new Point();

        int x1 = pointLine1.getX(), x2 = pointLine2.getX(), y1 = pointLine1.getY(), y2 = pointLine2.getY();
        float k, q;
        k = ((float) (y2 - y1) / (x2 - x1));
        if (Math.abs(y2 - y1) < Math.abs(x2 - x1)) {                    // zjištění řídící osy
            if (x2 < x1) {                                              // výměna koncových bodů
                int temp = x1;
                x1 = x2;
                x2 = temp;
                temp = y1;
                y1 = y2;
                y2 = temp;
            }
            q = y1 - (k * x1);
            for (int x = x1; x <= x2; x++) {                            // vykreslení - řídící osa X
                float y = (k * x) + q;

                float lengthBetweenPoints = (float) Math.sqrt((mouseLocation.getX() - (x)) * (mouseLocation.getX() - (x))
                        + (mouseLocation.getY() - (y)) * (mouseLocation.getY() - (y)));
                if (lengthBetweenPoints < length) {
                    length = lengthBetweenPoints;
                    newPoint.setX(x);
                    newPoint.setY((int) y);
                }
            }
        } else {
            if (y2 < y1) {                                              // výměna koncových bodů
                int temp = x1;
                x1 = x2;
                x2 = temp;
                temp = y1;
                y1 = y2;
                y2 = temp;
            }
            q = y1 - (k * x1);
            for (int y = y1; y <= y2; y++) {                            // vykreslení pomocí řídící osy Y
                float x = (y - q) / k;
                if (x1 == x2) {
                    x = x1;
                }
                float lengthBetweenPoints = (float) Math.sqrt((mouseLocation.getX() - (x)) * (mouseLocation.getX() - (x))
                        + (mouseLocation.getY() - (y)) * (mouseLocation.getY() - (y)));
                if (lengthBetweenPoints < length) {
                    length = lengthBetweenPoints;
                    newPoint.setX((int) x);
                    newPoint.setY(y);
                }
            }
        }
        newPoint.setDistance(length);
        return newPoint;
    }

    public static Point findCenter(Point pointFirst, Point pointSecond) { // vrací středový bod mezi dvěma zadanými body
        Point newPoint = new Point();
        int length = new Line(pointFirst, pointSecond).length() / 2;
        int actual = 0;
        int x1 = pointFirst.getX(), x2 = pointSecond.getX(), y1 = pointFirst.getY(), y2 = pointSecond.getY();
        float k, q;
        k = ((float) (y2 - y1) / (x2 - x1));
        if (Math.abs(y2 - y1) < Math.abs(x2 - x1)) {                    // zjištění řídící osy
            if (x2 < x1) {                                              // výměna koncových bodů
                int temp = x1;
                x1 = x2;
                x2 = temp;
                temp = y1;
                y1 = y2;
                y2 = temp;
            }
            q = y1 - (k * x1);
            for (int x = x1; x <= x2; x++) {                            // vykreslení - řídící osa X
                float y = (k * x) + q;

                actual++;
                if (actual == length) {
                    newPoint.setX(x);
                    newPoint.setY((int) y);
                }
            }
        } else {
            if (y2 < y1) {                                              // výměna koncových bodů
                int temp = x1;
                x1 = x2;
                x2 = temp;
                temp = y1;
                y1 = y2;
                y2 = temp;
            }
            q = y1 - (k * x1);
            for (int y = y1; y <= y2; y++) {                            // vykreslení pomocí řídící osy Y
                float x = (y - q) / k;
                if (x1 == x2) {
                    x = x1;
                }
                actual++;
                if (actual == length) {
                    newPoint.setX((int) x);
                    newPoint.setY(y);
                }
            }
        }
        return newPoint;
    }
}
