import raster.LineRasterizer;
import raster.LineRasterizerGraphics;
import raster.Raster;
import raster.RasterBufferedImage;
import render.WireRenderer;
import solids.*;
import transforms.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Controller3D {
    private final Thread animation;
    private boolean animationActive = false;
    private final JPanel panel;
    private final Raster raster;
    private final WireRenderer wireRenderer;
    private List<String> solidList;
    private int activeSolid = 0;
    private boolean orthoProjection = true;
    private boolean axisActive = true;
    private int curveIndex = 0;
    private final List<Mat4> curveTypes = new ArrayList<>(Arrays.asList(Cubic.BEZIER, Cubic.FERGUSON, Cubic.COONS));
    private Scene scene;

    private Camera camera = new Camera()
            .withPosition(new Vec3D(10, 0, 0))
            .withAzimuth(Math.PI)
            .withZenith(0)
            .withFirstPerson(true);
    private final double cameraSpeed = 0.1;

    private int ox, oy;

    public Controller3D(int width, int height) {

        JFrame frame = new JFrame();

        frame.setLayout(new BorderLayout());
        frame.setTitle("PGRF1 - Uloha 3: Martin Voda");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        raster = new RasterBufferedImage(width, height);
        LineRasterizer lineRasterizer = new LineRasterizerGraphics(raster);
        wireRenderer = new WireRenderer(lineRasterizer);

        animation = new Thread(() -> {
            while (true) {
                if (animationActive) {
                    solidRotate(new Mat4RotXYZ(0.01, 0.01, 0.01));
                    display();
                }
                try {
                    Thread.sleep(1000 / 60);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        });
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
                    scene.getSolids().forEach(solid -> {
                        solid.setModel(solid.getModel().mul(new Mat4Scale(1.0 + cameraSpeed)));
                    });
                }
                if (e.getKeyCode() == KeyEvent.VK_A) {
                    camera = camera.left(cameraSpeed);
                }
                if (e.getKeyCode() == KeyEvent.VK_S) {
                    camera = camera.backward(cameraSpeed);
                    scene.getSolids().forEach(solid -> {
                        solid.setModel(solid.getModel().mul(new Mat4Scale(1.0 - cameraSpeed)));
                    });
                }
                if (e.getKeyCode() == KeyEvent.VK_D) {
                    camera = camera.right(cameraSpeed);
                }
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    camera = camera.up(cameraSpeed);
                }
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    camera = camera.down(cameraSpeed);
                }
                if (e.getKeyCode() == KeyEvent.VK_P) {
                    orthoProjection = !orthoProjection;
                }
                if (e.getKeyCode() == KeyEvent.VK_O) {
                    axisActive = !axisActive;
                }
                if (e.getKeyCode() == KeyEvent.VK_V) {
                    curveIndex = (curveIndex + 1) % curveTypes.size();
                    scene.getSolids().forEach(solid -> {
                        if (solid.getName().equals("Curve")) {
                            ((Curve)solid).updateType(curveTypes.get(curveIndex));
                        }
                    });
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    activeSolid = (activeSolid + 1) % solidList.size();
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    activeSolid = (activeSolid - 1) % solidList.size();
                    if (activeSolid < 0) {
                        activeSolid = solidList.size() - 1;
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_1) {
                    solidTransl(-0.5, 0, 0);
                }
                if (e.getKeyCode() == KeyEvent.VK_2) {
                    solidTransl(0.5, 0, 0);
                }
                if (e.getKeyCode() == KeyEvent.VK_3) {
                    solidTransl(0, -0.5, 0);
                }
                if (e.getKeyCode() == KeyEvent.VK_4) {
                    solidTransl(0, 0.5, 0);
                }
                if (e.getKeyCode() == KeyEvent.VK_5) {
                    solidTransl(0, 0, -0.5);
                }
                if (e.getKeyCode() == KeyEvent.VK_6) {
                    solidTransl(0, 0, 0.5);
                }
                if (e.getKeyCode() == KeyEvent.VK_X) {
                    solidRotate(new Mat4RotX(0.01));
                }
                if (e.getKeyCode() == KeyEvent.VK_Y) {
                    solidRotate(new Mat4RotY(0.01));
                }
                if (e.getKeyCode() == KeyEvent.VK_Z) {
                    solidRotate(new Mat4RotZ(0.01));
                }
                if (e.getKeyCode() == KeyEvent.VK_R) {
                    solidRotate(new Mat4RotXYZ(0.01, 0.01, 0.01));
                }
                if (e.getKeyCode() == KeyEvent.VK_0) {
                    animationActive = !animationActive;
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

        panel.addMouseWheelListener(e -> {
            if (e.getWheelRotation() == 1) {
                //ZOOM OUT
                solidZOOM(0.9);
            } else {
                //ZOOM IN
                solidZOOM(1.1);
            }

            display();
        });

        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int dy = oy - e.getY();
                int dx = ox - e.getX();
                double azimuth = dx / 1000.;
                double zenith = dy / 1000.;

                camera = camera.addAzimuth(azimuth);
                camera = camera.addZenith(zenith);

                ox = e.getX();
                oy = e.getY();

                display();
            }
        });

        panel.requestFocus();
        panel.requestFocusInWindow();
    }

    private void display() {
        raster.clear();

        wireRenderer.setView(camera.getViewMatrix());
        if (orthoProjection) {
            wireRenderer.setProjection(new Mat4OrthoRH(6, 4, 0.1, 30));
        } else {
            wireRenderer.setProjection(new Mat4PerspRH(1.5, raster.getWidth() / raster.getHeight(), 0.1, 30));
        }
        wireRenderer.render(scene);
        if (axisActive) {
            wireRenderer.render(new Axis());
        }

        Graphics graphics = ((RasterBufferedImage) raster).getImg().getGraphics();
        graphics.drawString("Active Solid: " + solidList.get(activeSolid), raster.getWidth() / 2, 15);

        panel.repaint();
    }

    public void start() {
        Solid cube = new Cube();
        cube.setModel(cube.getModel().mul(new Mat4Transl(0, 0, 0)));
        Solid pyramid = new Pyramid();
        pyramid.setModel(pyramid.getModel().mul(new Mat4Transl(0, 3, 0)));
        Solid roof = new Roof();
        roof.setModel(roof.getModel().mul(new Mat4Transl(0, 3, 3)));
        Solid tetrahedron = new Tetrahedron();
        tetrahedron.setModel(tetrahedron.getModel().mul(new Mat4Transl(0, -3, 0)));


        scene = new Scene();
        scene.addSolid(cube);
        scene.addSolid(pyramid);
        scene.addSolid(roof);
        scene.addSolid(tetrahedron);
        scene.addSolid(new Curve(curveTypes.get(curveIndex)));


        solidList = new ArrayList<>();
        solidList.add("All");
        scene.getSolids().forEach(solid -> {
            solidList.add(solid.getName());
        });
        solidZOOM(0.5);
        display();

        animation.start();
    }

    private void solidTransl(double x, double y, double z) {
        if (activeSolid == 0) {
            scene.getSolids().forEach(solid -> {
                Mat4 temp = solid.getModel();
                temp = new Mat4Transl(x, y, z).mul(temp);
                solid.setModel(temp);
            });
        } else {
            Mat4 temp = scene.getSolids().get(activeSolid - 1).getModel();
            temp = new Mat4Transl(x, y, z).mul(temp);
            scene.getSolids().get(activeSolid - 1).setModel(temp);
        }
    }

    private void solidRotate(Mat4 rotate) {
        if (activeSolid == 0) {
            scene.getSolids().forEach(solid -> {
                Mat4 temp = solid.getModel();
                temp = rotate.mul(temp);
                solid.setModel(temp);
            });
        } else {
            Mat4 temp = scene.getSolids().get(activeSolid - 1).getModel();
            temp = rotate.mul(temp);
            scene.getSolids().get(activeSolid - 1).setModel(temp);
        }
    }

    private void solidZOOM(double alpha) {
        if (activeSolid == 0) {
            scene.getSolids().forEach(solid -> {
                Mat4 temp = solid.getModel();
                temp = temp.mul(new Mat4Scale(alpha));
                solid.setModel(temp);
            });
        } else {
            Mat4 temp = scene.getSolids().get(activeSolid - 1).getModel();
            temp = temp.mul(new Mat4Scale(alpha));
            scene.getSolids().get(activeSolid - 1).setModel(temp);
        }
    }
}

