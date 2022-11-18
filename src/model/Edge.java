package model;

public class Edge { // pomocná třída pro hranu
    public Point start;
    public Point end;

    public Edge(Point start, Point end) {
        this.start = start;
        this.end = end;
    }
}