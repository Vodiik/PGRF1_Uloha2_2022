import clip.Clipper;
import fill.PatternFillSquare;
import fill.SeedFiller;
import model.Line;
import model.Point;
import model.Polygon;
import raster.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class Canvas {
    private final JPanel panel;
    Raster raster;
    private final LineRasterizer lineRasterizer;
    private final LineRasterizer dottedLineRasterizer;
    Point point;
    private Polygon polygon;
    private final Polygon clipPolygon;

    private int clipIndexEditPoint;
    private int clipIndexAddPoint;
    int indexEditPoint;
    int indexAddPoint;

    private enum Modes {POLYGON, CLIPPER, SEEDFILLER}

    private enum Patterns {CLEAR, CHESS}

    private Patterns pattern = Patterns.CLEAR;

    private Modes mode = Modes.POLYGON;

    public Canvas(int width, int height) {
        // základní konfigurace okna
        JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.setTitle("UHK FIM PGRF1");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // inicializace tříd
        raster = new RasterBufferedImage(width, height);
        lineRasterizer = new LineRasterizerTrivial(raster);
        dottedLineRasterizer = new DottedLineRasterizer(raster);

        point = new Point();
        polygon = new Polygon();

        clipPolygon = new Polygon();

        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(((RasterBufferedImage) raster).getImg(), 0, 0, null);

            }
        };
        panel.setPreferredSize(new Dimension(width, height));
        frame.add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);

        // add key listener
        panel.requestFocus();
        panel.requestFocusInWindow();
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent keyEvent) {
                switch (keyEvent.getKeyCode()) {
                    case KeyEvent.VK_C -> { // mazání plátna a bodů při zmáčknutí C
                        clear();
                        polygon.clearPoints();
                        clipPolygon.clearPoints();
                        drawGUI();
                    }
                    case KeyEvent.VK_T -> { // vykreslení třetí bodu trojúhelníku při zmáčknutí T
                        // triangle
                        if (polygon.getPoints().size() > 2) {
                            for (int i = 2; i < polygon.getPoints().size(); i++) {
                                polygon.getPoints().remove(i);
                            }
                        }
                        if (polygon.getPoints().size() == 2) {
                            clear();
                            polygon.addPoint(polygon.getTrianglePoint());
                            redraw();
                            drawGUI();
                        }
                    }
                    case KeyEvent.VK_M -> { // přepínání režimů
                        for (int i = 0; i < Modes.values().length; i++) {
                            if (mode == Modes.values()[i]) {
                                if ((i + 1) == Modes.values().length) {
                                    mode = Modes.values()[0];
                                } else {
                                    mode = Modes.values()[i + 1];
                                }
                                break;
                            }
                        }
                        clear();
                        redraw();
                        drawGUI();
                    }
                    case KeyEvent.VK_SPACE -> { // provedení ořezání - mezerník
                        if (mode == Modes.CLIPPER) {
                            Clipper clipper = new Clipper();
                            polygon = clipper.clip(polygon, clipPolygon);
                            clear();
                            //clipPolygon.clearPoints();
                            redraw();
                            drawGUI();
                        }
                    }
                    case KeyEvent.VK_P -> { // přepínání patternů pro seedfill, viditelné pouze pokud je aktivní seedfill
                        for (int i = 0; i < Patterns.values().length; i++) {
                            if (pattern == Patterns.values()[i]) {
                                if ((i + 1) == Patterns.values().length) {
                                    pattern = Patterns.values()[0];
                                } else {
                                    pattern = Patterns.values()[i + 1];
                                }
                            }
                        }
                        clear();
                        redraw();
                        drawGUI();
                    }
                }
            }
        });

        //add mouse listener
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                switch (mode) { // přepínání režimů
                    case POLYGON -> {
                        if (mouseEvent.getButton() == MouseEvent.BUTTON1) { // levé tlačítko myši, přidání mezi 2 nejbližší body
                            Point neareastPoint = polygon.findNearestPointOnLine(new Point(mouseEvent.getX(), mouseEvent.getY()));
                            indexAddPoint = neareastPoint.getIndex();
                            clear();
                            drawPolygonEdit(polygon, new Point(mouseEvent.getX(), mouseEvent.getY()), indexAddPoint);

                        } else if (mouseEvent.getButton() == MouseEvent.BUTTON2) { // prostřední tlačítko - smaže nejbližší bod
                            if (polygon.getPoints().size() > 0) {
                                polygon.deletePoint(polygon.findNearestPointIndex(new Point(mouseEvent.getX(), mouseEvent.getY())));
                                redraw();
                                drawGUI();
                            }
                        } else if (mouseEvent.getButton() == MouseEvent.BUTTON3) { // pravé tlačítko myši - editace nejbížšího bodu
                            indexEditPoint = polygon.findNearestPointIndex(new Point(mouseEvent.getX(), mouseEvent.getY()));
                            point = polygon.getPoints().get(indexEditPoint);
                            polygon.deletePoint(indexEditPoint);
                        }
                    }
                    case CLIPPER -> {
                        if (mouseEvent.getButton() == MouseEvent.BUTTON1) { // přidání bodu do ořezávacího polygonu
                            Point nearestPoint = clipPolygon.findNearestPointOnLine(new Point(mouseEvent.getX(), mouseEvent.getY()));
                            clipIndexAddPoint = nearestPoint.getIndex();
                            clear();
                            drawPolygonEdit(clipPolygon, new Point(mouseEvent.getX(), mouseEvent.getY()), clipIndexAddPoint);
                        } else if (mouseEvent.getButton() == MouseEvent.BUTTON2) { // smazání bodu v ořezávacím polygonu
                            if (clipPolygon.getPoints().size() > 0) {
                                clipPolygon.deletePoint(clipPolygon.findNearestPointIndex(new Point(mouseEvent.getX(), mouseEvent.getY())));
                                redraw();
                                drawGUI();
                            }
                        } else if (mouseEvent.getButton() == MouseEvent.BUTTON3) { // přemístění bodu v ořezávacím polygonu
                            clipIndexEditPoint = clipPolygon.findNearestPointIndex(new Point(mouseEvent.getX(), mouseEvent.getY()));
                            point = clipPolygon.getPoints().get(clipIndexEditPoint);
                            clipPolygon.deletePoint(clipIndexEditPoint);
                        }
                    }
                    case SEEDFILLER -> {
                        if (mouseEvent.getButton() == MouseEvent.BUTTON1) { // vybarvení pomocí seedfill po kliknutí
                            SeedFiller seedFiller = new SeedFiller(raster);
                            seedFiller.setSeedPoint(new Point(mouseEvent.getX(), mouseEvent.getY()));
                            switch (pattern) { // přepínání patternů lze jednoduše přidat další
                                case CLEAR -> seedFiller.setPatternFill(null);
                                case CHESS -> seedFiller.setPatternFill(new PatternFillSquare());
                            }
                            seedFiller.fill();
                        }
                    }
                }
                panel.repaint();
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                if (mode == Modes.POLYGON) {
                    if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
                        clear();
                        polygon.addPoint(indexAddPoint, new Point(mouseEvent.getX(), mouseEvent.getY()));
                    } else if (mouseEvent.getButton() == MouseEvent.BUTTON3) {
                        polygon.addPoint(indexEditPoint, new Point(mouseEvent.getX(), mouseEvent.getY()));
                    }
                    clear();
                    redraw();
                    drawGUI();
                } else if (mode == Modes.CLIPPER) {
                    if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
                        clear();
                        clipPolygon.addPoint(clipIndexAddPoint, new Point(mouseEvent.getX(), mouseEvent.getY()));
                    } else if (mouseEvent.getButton() == MouseEvent.BUTTON3) {
                        clipPolygon.addPoint(clipIndexEditPoint, new Point(mouseEvent.getX(), mouseEvent.getY()));
                    }
                    clear();
                    redraw();
                    drawGUI();
                }
            }
        });

        //add mouse motion listener
        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent mouseEvent) {
                if (mode == Modes.POLYGON) {
                    super.mouseDragged(mouseEvent);
                    if (mouseEvent.getModifiersEx() == MouseEvent.BUTTON1_DOWN_MASK) { // přidání bodu
                        clear();
                        drawPolygonEdit(polygon, new Point(mouseEvent.getX(), mouseEvent.getY()), indexAddPoint);
                        drawPolygon(clipPolygon);
                        drawGUI();
                    } else if (mouseEvent.getModifiersEx() == MouseEvent.BUTTON3_DOWN_MASK) { // editace bodu
                        clear();
                        point.setX(mouseEvent.getX());
                        point.setY(mouseEvent.getY());
                        drawPolygonEdit(polygon, point, indexEditPoint);
                        drawPolygon(clipPolygon);
                        drawGUI();
                    }
                } else if (mode == Modes.CLIPPER) {
                    super.mouseDragged(mouseEvent);
                    if (mouseEvent.getModifiersEx() == MouseEvent.BUTTON1_DOWN_MASK) {
                        clear();
                        drawPolygonEdit(clipPolygon, new Point(mouseEvent.getX(), mouseEvent.getY()), clipIndexAddPoint);
                        drawPolygon(polygon);
                        drawGUI();
                    } else if (mouseEvent.getModifiersEx() == MouseEvent.BUTTON3_DOWN_MASK) {
                        clear();
                        point.setX(mouseEvent.getX());
                        point.setY(mouseEvent.getY());
                        drawPolygonEdit(clipPolygon, point, clipIndexEditPoint);
                        drawPolygon(polygon);
                        drawGUI();
                    }
                }
            }

        });
    }

    public void clear() {
        raster.clear();
        panel.repaint();
    }

    public void start() {
        clear();
        drawGUI();
    }

    public void drawPolygon(Polygon polygon) {                                                             // vykreslení N-uhelniku
        for (int i = 0; i < polygon.getPoints().size(); i++) {
            if (i + 1 == polygon.getPoints().size()) {
                lineRasterizer.rasterize(new Line(polygon.getPoints().get(i), polygon.getPoints().get(0)), Color.red.getRGB());
            } else {
                lineRasterizer.rasterize(new Line(polygon.getPoints().get(i), polygon.getPoints().get(i + 1)), Color.red.getRGB());
            }
            panel.repaint();
        }
    }

    public void redraw() {                                                                  // překleslení uloženého N-uhelniku
        drawPolygon(polygon);
        drawPolygon(clipPolygon);
        panel.repaint();
    }

    public void drawPolygonEdit(Polygon polygon, Point editPoint, int indexEditPoint) {                      // vykreslení při editaci, využití tečkované čáry
        List<Point> points = new ArrayList<>();
        points.addAll(0, polygon.getPoints());
        indexEditPoint = indexEditPoint < 0 ? points.size() : indexEditPoint;
        points.add(indexEditPoint, editPoint);
        for (int i = 0; i < points.size(); i++) {
            if (i + 1 == indexEditPoint || i == indexEditPoint) {
                if (i + 1 == points.size()) {
                    dottedLineRasterizer.rasterize(new Line(points.get(i), points.get(0)), Color.red.getRGB());
                } else {
                    dottedLineRasterizer.rasterize(new Line(points.get(i), points.get(i + 1)), Color.red.getRGB());
                }
            } else if (i + 1 == points.size()) {
                lineRasterizer.rasterize(new Line(points.get(i), points.get(0)), Color.red.getRGB());
            } else {
                lineRasterizer.rasterize(new Line(points.get(i), points.get(i + 1)), Color.red.getRGB());
            }
            panel.repaint();
        }
    }

    public void drawGUI() {
        Graphics g = ((RasterBufferedImage) raster).getImg().getGraphics();
        g.drawString("'C' - Clear", 10, 15);
        g.drawString("'B' - Color", 10, 15 + g.getFontMetrics().getHeight());
        g.drawString("'T' - Triangle", 10, 15 + (g.getFontMetrics().getHeight() * 2));
        g.drawString("'M' - Modes | Current: " + mode, 10, 15 + (g.getFontMetrics().getHeight() * 3));
        if (mode == Modes.SEEDFILLER) {
            g.drawString("'P' - Pattern | Current: " + pattern, 10, 15 + (g.getFontMetrics().getHeight() * 4));
        }
        panel.repaint();
    }

}