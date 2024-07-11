package App;

import java.awt.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import Utils.*;
import Math.*;

//code beginnt
public class FlightSimulator extends Animation {
    @Override
    protected ArrayList<JFrame> createFrames(ApplicationTime applicationTimeThread) {
        ArrayList<JFrame> frames = new ArrayList<>();

        JFrame animationFrame = createAnimationFrame(applicationTimeThread);
        frames.add(animationFrame);

        ControlPanel controlPanel = new ControlPanel();
        frames.add(controlPanel);

        return frames;
    }

    //hier entsteht greatness
    private JFrame createAnimationFrame(ApplicationTime applicationTimeThread){
        JFrame frame = new JFrame("Mathematik und Simulation - Gruppe 3.2");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new FlightSimulatorPanel(applicationTimeThread);
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);

        return frame;
    }
}

//erstes Panel für die Zeichnung/Animation
class FlightSimulatorPanel extends JPanel{
    final ApplicationTime thread;
    private final Logger logger = Logger.getLogger(FlightSimulatorPanel.class.getName());

    // finish this function
    public FlightSimulatorPanel(ApplicationTime thread) {
        this.thread = thread;
    }
    public Dimension getPreferredSize() {
        return new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
    }
    //Methode mit Rechnungen und Zeichnung
    @Override
    protected void paintComponent(Graphics g) {
        try {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            g.setColor(Color.WHITE);
            g.fillRect(0, 0, getWidth(), getHeight());

            //offset Werte für die Verschiebung von Java Koordinatenursprung ins Center
            int offsetHeight = getHeight() / 2;
            int offsetWidth = getWidth() / 2;
            int width = this.getWidth();
            int height = this.getHeight();
            int length = Constants.Length;
            if (width < Constants.WINDOW_WIDTH) {
                length = width / 4;
            }

            Origin U = new Origin(width, height);
            //definiere s1
            double s1 = Constants.PROJECTION_S1;
            //definiere alpha
            double alpha = Constants.PROJECTION_ALPHA;
            // definiere phi
            double phi = Math.toDegrees(Math.atan(s1 * Math.sin(Math.toRadians(alpha))));
            //definiere theta
            double theta = Math.toDegrees(Math.atan(-s1 * Math.cos(Math.toRadians(alpha)) * Math.cos(Math.toRadians(phi))));
            //definiere Erdradius
            double r = Constants.EARTH_RADIUS;

            //Einheitsvektoren
            Vector x = new Vector(1, 0, 0);
            Vector y = new Vector(0, 1, 0);
            Vector z = new Vector(0, 0, 1);
            Matrix proj = new Matrix(s1, (int) alpha, true);

            //Projektion und Skalierung Einheitsvektoren
            Vector X = new Vector(x.multiply(proj));
            double lengthX = X.length();
            double scaleX = length / lengthX * s1;
            Vector Y = new Vector(y.multiply(proj));
            double lengthY = Y.length();
            double scaleY = length / lengthY;
            Vector Z = new Vector(z.multiply(proj));
            double lengthZ = Z.length();
            double scaleZ = length / lengthZ;

            calculate_and_draw_wireframe(s1, phi, theta, proj, offsetWidth, offsetHeight, g);
            Vector[] ellipsePoints = calculate_outline_ellipse(r, phi, theta, proj, length);
            calculate_and_draw_flightpath(r, phi, theta, proj, length, offsetWidth, offsetHeight, g, g2);
            draw_outline_ellipse(g, g2, ellipsePoints, offsetWidth, offsetHeight);
            draw_cartesian_coordinatesystem(g, g2, X, scaleX, Y, scaleY, Z, scaleZ, width, height, U, offsetWidth, offsetHeight);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "An error occurred: ", e);
        }
    }

    void calculate_and_draw_wireframe(double s1, double phi, double theta, Matrix proj, int offsetWidth, int offsetHeight, Graphics g) {
        //Erdkugel definieren und zeichnen
        double[] bgs = {-30.0, -60.0, 0.0, 30.0, 60.0};
        double[] lgs = {0.0, 45.0, 90.0, 135.0, 180.0, 225.0, 270.0, 315.0};

        // doppelte for-schleife fürs Zeichnen der Längengerade und anschliessend Breitengrade
        for (double lg : lgs) {
            for (int bg = -90; bg <= 90; bg += 2) {
                Coordinate k = new Coordinate(lg, bg);
                Vector v = new Vector(k.vector());

                double bg_distance_to_plane = v.distance_to_plane(v, phi, theta);

                v = new Vector(v.multiply(proj));
                v = new Vector(v.translate(offsetWidth, offsetHeight));

                if (bg_distance_to_plane >= 0 || s1 == 0) {
                    g.setColor(Color.BLACK);
                } else {
                    g.setColor(Color.LIGHT_GRAY);
                }
                g.fillOval((int)v.x, (int)v.y, 3, 3);

            }
        }
        for (double bg : bgs){
            for (int lg = -180; lg <= 180; lg += 2) {
                Coordinate k = new Coordinate(lg, bg);
                Vector v = new Vector(k.vector());

                double lg_distance_to_plane = v.distance_to_plane(v, phi, theta);

                v = new Vector(v.multiply(proj));
                v = new Vector(v.translate(offsetWidth, offsetHeight));

                if (lg_distance_to_plane >= 0 || s1 == 0) {
                    g.setColor(Color.BLACK);
                } else {
                    g.setColor(Color.LIGHT_GRAY);
                }
                g.fillOval((int)v.x, (int)v.y, 3, 3);
            }
        }
    }
    //Umriss Ellipse
    Vector[] calculate_outline_ellipse(double r, double phi, double theta, Matrix proj, double length) {
        int n = 100;
        //Vekotr Repräsentation der einzelnen Punkte der Ellipse
        Vector u;
        //Winkel in Bogenmaß
        double t;

        //Array zum Speichern der Ellips-Vektoren
        Vector[] ellipsePoints = new Vector[n];
        for (int i = 0; i < n; i++) {
            t = 2 * Math.PI * i / n;

            u = new Vector(0.0, r * Math.cos(t), r * Math.sin(t));

            u = u.rotateY(-theta);
            u = u.rotateZ(phi);

            u = u.multiply(proj);
            u = u.scale(length / r);

            ellipsePoints[i] = u;
        }

        return ellipsePoints;
    }
    //Berechnung der Entfernung 2er-Punkte
    void calculate_and_draw_flightpath(double r, double phi, double theta, Matrix proj, double length, int offsetWidth, int offsetHeight, Graphics g, Graphics2D g2) {
        int n = 100;
        double t;

        //Koordinaten
        Coordinate P = new Coordinate(Constants.COORD_START_LONG, Constants.COORD_START_LAT);
        Coordinate Q = new Coordinate(Constants.COORD_END_LONG, Constants.COORD_END_LAT);
        //Vektor Repräsentation der Koordinaten
        Vector p = new Vector(P.vector());
        Vector q = new Vector(Q.vector());

        //Winkel zwischen den Punkten P und Q
        double delta = Math.acos(p.multiply(q) / (p.length() * q.length()));
        //Abstand
        double d = r * delta;

        if (Constants.COORD_ON_GLOBE) {
            //Vektor Projektionen
            Vector pNormal = p.normalize();
            Vector nNormal = new Vector(p.cross_product(q)).normalize();
            Vector uNormal = new Vector(nNormal.cross_product(pNormal)).normalize();

            //Berechnung der Kurve
            Vector[] curve = new Vector[n];

            for (int i = 0; i < n; i++) {
                t = delta / n * i;

                Vector k = new Vector(pNormal.multiply(r * Math.cos(t)).add(uNormal.multiply(r * Math.sin(t))));
                k = k.multiply(proj);
                k = k.scale(length / r);

                curve[i] = k;
            }
            //draw P, Q und Kurve
            Vector pDraw = p.multiply(proj);
            pDraw = pDraw.translate(offsetWidth,offsetHeight);
            double p_distance_to_plane = pDraw.distance_to_plane(p, phi, theta);

            Vector qDraw = q.multiply(proj);
            qDraw = qDraw.translate(offsetWidth, offsetHeight);
            double q_distance_to_plane = qDraw.distance_to_plane(q, phi, theta);

            Color orange_front = new Color(238, 155, 13);
            Color orange_back = new Color(238, 193, 119);

            if (p_distance_to_plane >= 0) {
                g.setColor(orange_front);
                g.fillOval((int)pDraw.x - 7, (int)pDraw.y - 7, 14, 14);
            } else {
                g.setColor(orange_back);
                g.fillOval((int)pDraw.x - 7, (int)pDraw.y - 7, 14, 14);
            }

            if (q_distance_to_plane >= 0) {
                g.setColor(orange_front);
                g.fillOval((int)qDraw.x - 7, (int)qDraw.y - 7, 14, 14);
            } else {
                g.setColor(orange_back);
                g.fillOval((int)qDraw.x - 7, (int)qDraw.y - 7, 14, 14);
            }

            Color final_cyan = new Color(0, 255, 240);
            Color flight_cyan = new Color(150, 200, 240);
            g2.setStroke(new BasicStroke(3.0f));
            if (System.currentTimeMillis() - Constants.FLIGHT_START_TIME > Constants.DURATION) {
                for (int i = 0; i < n - 1; i++) {
                    Vector draw = new Vector(curve[i].translate(offsetWidth, offsetHeight));
                    Vector draw2 = new Vector(curve[i + 1].translate(offsetWidth, offsetHeight));

                    g.setColor(Color.BLACK);
                    g.setFont(new Font("Serif", Font.BOLD, 20));
                    g.drawString(STR."Distance: \{Math.round(d)}", 10, 25);

                    g.setColor(final_cyan);
                    g2.drawLine((int)draw.x, (int)draw.y, (int)draw2.x, (int)draw2.y);
                }
            } else {
                for (int i = 0; i < (n * (System.currentTimeMillis() - Constants.FLIGHT_START_TIME) / Constants.DURATION) - 1; i++) {
                    Vector draw = new Vector(curve[i].translate(offsetWidth, offsetHeight));
                    Vector draw2 = new Vector(curve[i + 1].translate(offsetWidth, offsetHeight));

                    g.setColor(Color.BLACK);
                    g.setFont(new Font("Serif", Font.BOLD, 20));
                    g.drawString(STR."Distance: \{Math.round(d * (System.currentTimeMillis() - Constants.FLIGHT_START_TIME) / Constants.DURATION)}", 10, 25);

                    g.setColor(flight_cyan);
                    g2.drawLine((int)draw.x, (int)draw.y, (int)draw2.x, (int)draw2.y);
                }
            }
        }
    }
    // draw umriss ellipse
    void draw_outline_ellipse(Graphics g, Graphics2D g2, Vector[] ellipsePoints, int offsetWidth, int offsetHeight) {
        int n = 100;
        g.setColor(Color.RED);
        g2.setStroke(new BasicStroke(2.0f));
        for (int i = 0; i < n - 1; i++) {
            Vector draw = new Vector(ellipsePoints[i].translate(offsetWidth, offsetHeight));
            Vector draw2 = new Vector(ellipsePoints[i + 1].translate(offsetWidth, offsetHeight));
            g2.drawLine((int)draw.x, (int)draw.y, (int)draw2.x, (int)draw2.y);
        }
        g2.drawLine((int)ellipsePoints[n-1].x + offsetWidth, (int)ellipsePoints[n-1].y + offsetHeight, (int)ellipsePoints[0].x + offsetWidth, (int)ellipsePoints[0].y + offsetHeight);

    }
    //draw Kartesisches-Koordinatensystem
    void draw_cartesian_coordinatesystem(Graphics g, Graphics2D g2, Vector X, double scaleX, Vector Y, double scaleY, Vector Z, double scaleZ, int width, int height, Origin U, int offsetWidth, int offsetHeight) {
        //Java-Origin-Werte
        int oX = 0;
        int oY = 0;

        g.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(1));
        g.drawLine(oX, (int) U.y, width, (int) U.y);
        g.drawLine((int) U.x, oY, (int) U.x, height);

        // draw x-einheitsvektor
        g.setColor(new Color(255, 100, 100));
        int dicke = 3;
        g2.setStroke(new BasicStroke(dicke));
        Vector X_draw;
        if (X.x == 0  && X.y == 0) {
            X_draw = new Vector(X.translate(offsetWidth, offsetHeight));
        } else {
            X_draw = X.scale(scaleX);
            X_draw = new Vector(X_draw.translate(offsetWidth, offsetHeight));
        }
        g.drawLine((int) U.x, (int) U.y, (int) X_draw.x, (int) X_draw.y);
        g.fillOval((int)X_draw.x - 5, (int)X_draw.y - 5, 10, 10);

        //draw y-Einheitsvektor
        g.setColor(new Color(178, 255, 100));
        Vector Y_draw = Y.scale(scaleY).translate(offsetWidth,offsetHeight);
        g.drawLine((int)U.x, (int) U.y - dicke / 2, (int) Y_draw.x, (int) Y_draw.y - dicke / 2);
        g.fillOval((int)Y_draw.x - 5, (int) Y_draw.y - 5, 10, 10);

        //draw z-einheitsvektor
        g.setColor(new Color(100, 180, 255));
        Vector Z_draw = Z.scale(scaleZ).translate(offsetWidth, offsetHeight);
        g.drawLine((int) U.x, (int) U.y, (int) Z_draw.x, (int)Z_draw.y);
        g.fillOval((int)Z_draw.x - 5, (int)Z_draw.y - 5, 10, 10);

        //beschrifte alle Achsen
        g.setColor(Color.BLACK);
        g.setFont(new Font("Serif", Font.BOLD, 20));
        g.drawString("E1", (int) X_draw.x + 8, (int) X_draw.y + 8);
        g.drawString("E2", (int) Y_draw.x, (int) Y_draw.y - 8);
        g.drawString("E3", (int) Z_draw.x + 8, (int) Z_draw.y - 5);
        g.fillOval((int) U.x - 5, (int) U.y - 5, 10, 10);
    }
}

//Methode für das 2. Panel(Control Panel)
class ControlPanel extends JFrame {
    public ControlPanel() {
        setTitle("Control Panel");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel controlPanel = new JPanel();

        //alpha slider
        JPanel alphaRow = new JPanel();
        JLabel alpha = new JLabel("alpha");
        alpha.setPreferredSize(new Dimension(100, 15));
        JLabel projectionAlpha = new JLabel(String.format("%d", (int) Constants.PROJECTION_ALPHA));
        projectionAlpha.setPreferredSize(new Dimension(30, 15));
        JSlider alphaSlider = new JSlider(0, 360, (int) Constants.PROJECTION_ALPHA);
        alphaSlider.addChangeListener (e -> {
            Constants.PROJECTION_ALPHA = alphaSlider.getValue();
            projectionAlpha.setText(String.format("%d", (int) Constants.PROJECTION_ALPHA));
        });

        alphaRow.add(alpha, BorderLayout.LINE_START);
        alphaRow.add(alphaSlider, BorderLayout.CENTER);
        alphaRow.add(projectionAlpha, BorderLayout.LINE_END);

        // s1 slider
        JPanel s1Row = new JPanel();
        JLabel s1Label = new JLabel("s1");
        s1Label.setPreferredSize(new Dimension(100, 15));
        JLabel projectionS1 = new JLabel(String.format("%.2f", Constants.PROJECTION_S1));
        projectionS1.setPreferredSize(new Dimension(30, 30));
        JSlider s1Slider = new JSlider(0, 100, (int)Constants.PROJECTION_S1);
        s1Slider.addChangeListener(e -> {
            Constants.PROJECTION_S1 = (double)s1Slider.getValue() / 100.0;
            projectionS1.setText(String.format("%.2f", Constants.PROJECTION_S1));
        });
        s1Row.add(s1Label);
        s1Row.add(projectionS1);
        s1Row.add(s1Slider);

        //c(Geschwindigkeit) slider
        JPanel c_row = new JPanel();
        JLabel c_label = new JLabel("Flugdauer ");
        c_label.setPreferredSize(new Dimension(100, 15));
        JLabel c_label_num = new JLabel(String.format("%.2f", Constants.DURATION / 1000.0));
        c_label_num.setPreferredSize(new Dimension(30, 20));
        JSlider c_slider = new JSlider(0, 10);
        c_slider.addChangeListener(e -> {
            Constants.DURATION = (double)c_slider.getValue() * 1000.0;
            c_label_num.setText(String.format("%.2f", Constants.DURATION / 1000.0));
        });
        c_row.add(c_label);
        c_row.add(c_label_num);
        c_row.add(c_slider);

        // divider
        JPanel dividerRow = new JPanel();
        dividerRow.setLayout(new GridBagLayout());
        dividerRow.setBorder(new MatteBorder(1, 0, 0, 0, Color.BLACK));
        dividerRow.setPreferredSize(new Dimension(300, 30));

        JLabel label_Coord = new JLabel("Distance between two coordinates");
        label_Coord.setPreferredSize(new Dimension(200, 20));

        GridBagConstraints c;
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        dividerRow.add(label_Coord);

        //coordinate 1
        JPanel coordinate1Row = new JPanel();
        coordinate1Row.setLayout(new GridBagLayout());
        coordinate1Row.setBorder(new EmptyBorder(10, 10, 0, 10));

        JLabel startCoordLabel = new JLabel("Start");
        startCoordLabel.setPreferredSize(new Dimension(50, 20));
        JTextField startCoordTextField = new JTextField();
        startCoordTextField.setText("52.5200, 13.4050");
        startCoordTextField.setPreferredSize(new Dimension(300, 20));

        c = new GridBagConstraints();
        c.gridx = 0;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        coordinate1Row.add(startCoordLabel, c);
        c = new GridBagConstraints();
        c.gridx = 1;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 6.0;
        coordinate1Row.add(startCoordTextField, c);

        //coordinate 2
        JPanel coordinate2Row = new JPanel();
        coordinate2Row.setLayout(new GridBagLayout());
        coordinate2Row.setBorder(new EmptyBorder(10, 10, 0, 10));

        JLabel endCoordLabel = new JLabel("End");
        endCoordLabel.setPreferredSize(new Dimension(50, 20));
        JTextField endCoordTextField = new JTextField();
        endCoordTextField.setText("40.7128, -74.0060");
        endCoordTextField.setPreferredSize(new Dimension(300, 20));

        c = new GridBagConstraints();
        c.gridx = 1;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 2.0;
        coordinate2Row.add(endCoordLabel, c);
        c = new GridBagConstraints();
        c.gridx = 2;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 12.0;
        coordinate2Row.add(endCoordTextField, c);

        //Start/End Button
        JPanel buttonRow = new JPanel();
        buttonRow.setLayout(new BorderLayout(10, 0));
        buttonRow.setBorder((new EmptyBorder(10, 10, 10, 10)));

        JButton runButton = new JButton("start");
        runButton.setPreferredSize(new Dimension(100, 30));
        runButton.addActionListener(e -> {
            if(!Constants.COORD_ON_GLOBE) {
                String regex = "^(-?\\d+\\.\\d*),\\s?(-?\\d+\\.\\d*)$";
                Pattern pattern = Pattern.compile(regex);
                Matcher matchStart = pattern.matcher(startCoordTextField.getText().trim());
                Matcher matchEnd = pattern.matcher(endCoordTextField.getText().trim());

                if(matchStart.find() && matchEnd.find()) {
                    Constants.COORD_START_LAT = Double.parseDouble(matchStart.group(1));
                    Constants.COORD_START_LONG = Double.parseDouble(matchStart.group(2));
                    Constants.COORD_END_LAT = Double.parseDouble(matchEnd.group(1));
                    Constants.COORD_END_LONG = Double.parseDouble(matchEnd.group(2));

                    Constants.COORD_ON_GLOBE = true;

                    Constants.FLIGHT_START_TIME = System.currentTimeMillis();
                } else {
                    System.err.printf("Coordinate must fit pattern %s", regex);
                }
            } else {
                Constants.COORD_ON_GLOBE = false;
            }

            runButton.setText(Constants.COORD_ON_GLOBE ? "Stop" : "Start");
        });
        buttonRow.add(runButton, BorderLayout.NORTH);

        //füge alle Elemente zum Control Panel
        controlPanel.add(alphaRow);
        controlPanel.add(s1Row);
        controlPanel.add(c_row);
        controlPanel.add(dividerRow);
        controlPanel.add(coordinate1Row);
        controlPanel.add(coordinate2Row);
        controlPanel.add(buttonRow);

        getContentPane().add(controlPanel);
        pack();
        setSize(new Dimension(400, 300));
        setLocation(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT / 3);
        setVisible(true);
    }
}