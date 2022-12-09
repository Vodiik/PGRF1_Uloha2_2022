import raster.LineRasterizer;
import raster.LineRasterizerGraphics;
import raster.Raster;
import raster.RasterBufferedImage;
import render.WireRenderer;
import solids.Cube;
import solids.Pyramid;
import solids.Scene;
import solids.Solid;
import transforms.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Controller3D {
    private final JFrame frame;
    private final JPanel panel;
    private final Raster raster;
    private final LineRasterizer lineRasterizer;
    private final WireRenderer wireRenderer;

    private Scene scene;

    private Camera camera = new Camera()
            .withPosition(new Vec3D(10, 0, 0))
            .withAzimuth(Math.PI)
            .withZenith(0)
            .withFirstPerson(true);
    private final double cameraSpeed = 0.1;

    private int ox, oy;

    public Controller3D(int width, int height) {

        frame = new JFrame();

        frame.setLayout(new BorderLayout());
        frame.setTitle("PGRF1 - Uloha 3");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        raster = new RasterBufferedImage(width, height);
        lineRasterizer = new LineRasterizerGraphics(raster);
        wireRenderer = new WireRenderer(lineRasterizer);

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

        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_W) {
                    camera = camera.forward(cameraSpeed);
                    System.out.println("forward");
                }
                if (e.getKeyCode() == KeyEvent.VK_A) {
                    camera = camera.left(cameraSpeed);
                    System.out.println("left");
                }
                if (e.getKeyCode() == KeyEvent.VK_S) {
                    camera = camera.backward(cameraSpeed);
                    System.out.println("backward");
                }
                if (e.getKeyCode() == KeyEvent.VK_D) {
                    camera = camera.right(cameraSpeed);
                    System.out.println("right");
                }
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    camera = camera.up(cameraSpeed);
                    System.out.println("up");
                }
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    camera = camera.down(cameraSpeed);
                    System.out.println("down");
                }

                display();
            }
        });

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                ox = e.getX();
                oy = e.getY();
            }
        });

        panel.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.getWheelRotation() == 1) {
                    //ZOOM OUT
                    scene.getSolids().forEach(solid -> {
                        solid.setModel(solid.getModel().mul(new Mat4Scale(0.9)));
                    });
                } else {
                    //ZOOM IN
                    scene.getSolids().forEach(solid -> {
                        solid.setModel(solid.getModel().mul(new Mat4Scale(1.1)));
                    });
                }

                display();
            }
        });

        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int dy = oy - e.getY();
                int dx = ox - e.getX();
                double azimuth = dx / 1000.;
                double zenith = dy / 1000.;

                //camera.withAzimuth(camera.getAzimuth() + azimuth);
                //camera.withZenith(camera.getZenith() + zenith);

                camera = camera.addAzimuth(azimuth);
                camera = camera.addZenith(zenith);

                ox = e.getX();
                oy = e.getY();

                System.out.println(dy);
                System.out.println(dx);

                display();
            }
        });

        panel.requestFocus();
        panel.requestFocusInWindow();
    }

    public void clear() {
        raster.clear();
        panel.repaint();
    }

    private void display() {
        raster.clear();

        wireRenderer.setView(camera.getViewMatrix());
        wireRenderer.setProjection(new Mat4OrthoRH(6, 4, 0.1, 30));
        //wireRenderer.setProjection(new Mat4PerspRH(1.5, raster.getWidth() / raster.getHeight(), 0.1,30));

        wireRenderer.render(scene);

        panel.repaint();
    }

    public void start() {
        Solid cube1 = new Cube();
        Solid pyramid = new Pyramid();
        Mat4 scale = new Mat4Scale(0.5);
        Mat4 transl = new Mat4Transl(0.3, 0.3, 0);
        pyramid.setModel(scale.mul(transl));
        cube1.setModel(scale);
        scene = new Scene();
        scene.addSolid(cube1);
        scene.addSolid(pyramid);

        display();
    }
}

