package Math;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Vector {
    private final Logger logger = Logger.getLogger(Vector.class.getName());
    public double x;
    public double y;
    public double z;
    public double[] v;

    public Vector() {
        this.x = 0;
        this.y = 0;
        this.z = 0;

        this.v = new double[] {0, 0, 0};
    }
    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
        this.z = 0;
    }
    public Vector(double x, double y, double z) {
        v = new double[]{x, y, z};
        this.x = x; this.y = y; this.z = z;
    }
    public Vector(Vector other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;

        this.v = new double[]{other.x, other.y, other.z};
//        System.out.println("Other: " + other);
//        System.out.println("Vector: " + this.x + ", " + this.y + ", " + this.z);
//        System.out.println("Array: " + Arrays.toString(this.v));
    }
    public Vector add(Vector v) {
        return new Vector(this.x + v.x, this.y + v.y, this.z + v.z);
    }
    public void substract(Vector v) {
        this.x -= v.x;
        this.y -= v.y;
        this.z -= v.z;
    }
    public double x() {return this.x;}
    public double y() {return this.y;}
    public double z() {return this.z;}
    public Vector scale(double f) {
        return new Vector(this.x * f, this.y * f);
    }
    public Vector multiply(double f) {return new Vector(this.x * f, this.y * f, this.z * f);}
    public double length() {return Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2) + Math.pow(this.z, 2));}
    public Vector normalize() {
        double length = length();

        double newX = this.x / length;
        double newY = this.y / length;
        double newZ = this.z / length;

        return new Vector(newX, newY, newZ);
    }
    public Vector cross_product(Vector v) {
        return new Vector(
                this.y * v.z - this.z * v.y,
                this.z * v.x - this.x * v.z,
                this.x * v.y - this.y * v.x
        );
    }
    public double multiply(Vector v) {
        return this.x * v.x + this.y * v.y + this.z * v.z;
    }
    public Vector multiply(Matrix m) {
        Vector result = new Vector();

        if (m.rows == 3 && m.cols == 3) {
            result.x += m.matrix[0][0] * this.x + m.matrix[0][1] * this.y + m.matrix[0][2] * this.z;
            result.y += m.matrix[1][0] * this.x + m.matrix[1][1] * this.y + m.matrix[1][2] * this.z;
            result.z += m.matrix[2][0] * this.x + m.matrix[2][1] * this.y + m.matrix[2][2] * this.z;
        } else if(m.cols == 4){
            result.x = m.matrix[0][0] * this.x + m.matrix[0][1] * this.y + m.matrix[0][2] * this.z + m.matrix[0][3] * 0;
            result.y = m.matrix[1][0] * this.x + m.matrix[1][1] * this.y + m.matrix[1][2] * this.z + m.matrix[1][3] * 0;
        } else {
            result.x = m.matrix[0][0] * this.x + m.matrix[0][1] * this.y + m.matrix[0][2] * this.z;
            result.y = m.matrix[1][0] * this.x + m.matrix[1][1] * this.y + m.matrix[1][2] * this.z;
        }

        return result;
    }
    public Vector translate(int offsetX, int offsetY) {
        return new Vector(this.x + offsetX, this.y + offsetY);
    }
    public double distance_to_plane(Vector x, double phi, double theta) {
        return ((Math.cos(Math.toRadians(phi)) * Math.cos(Math.toRadians(theta)) * x.x) + ((Math.sin(Math.toRadians(phi)) * Math.cos(Math.toRadians(theta)) * x.y) + (Math.sin(Math.toRadians(theta)) * x.z)));
    };

    public Vector rotateZ( double angle) {

        angle = Math.toRadians(angle);

        Matrix rotMat = new Matrix(new double[][]{
                {Math.cos(angle), -Math.sin(angle), 0.0},
                {Math.sin(angle), Math.cos(angle), 0.0},
                {0.0, 0.0, 1.0}
        });

        this.rotateWithMatrix(rotMat);

        return this;
    }
    public Vector rotateY(double angle) {

        angle = Math.toRadians(angle);

        Matrix rotMat = new Matrix(new double[][]{
                {Math.cos(angle), 0.0, Math.sin(angle)},
                {0.0, 1.0, 0.0},
                {-Math.sin(angle), 0.0, Math.cos(angle)}
        });

        this.rotateWithMatrix(rotMat);

        return this;
    }
    public Vector rotateX(double angle) {
        angle = Math.toRadians(angle);

        Matrix rotMat = new Matrix(new double[][]{
                {1.0, 0.0, 0.0},
                {0.0 , Math.cos(angle), -Math.sin(angle)},
                {0.0, Math.sin(angle), Math.cos(angle)}
        });

        this.rotateWithMatrix(rotMat);

        return this;
    }
    private void rotateWithMatrix(Matrix m) {
        try {
            Matrix rotated = m.multiply(new Matrix(new double[][]{
                    {this.x},
                    {this.y},
                    {this.z}
            }));

            this.x = rotated.get(0, 0);
            this.y = rotated.get(0, 1);
            this.z = rotated.get(0, 2);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "An error occurred: ", e);
        }
    }
    @Override
    public String toString() {
        return String.format("(%.2f, %.2f, %.2f)", this.x, this.y, this.z);
    }
}
