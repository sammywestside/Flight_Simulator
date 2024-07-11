package Math;

import App.Constants;

import java.util.Objects;

public class Matrix {
    public double[][] matrix;
    public int rows;
    public int cols;

    public Matrix(double[][] data) {
        this.rows = data.length;
        this.cols = data[0].length;
        this.matrix = new double[data.length][data[0].length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                matrix[i][j] = data[i][j];
            }
        }
    }
    public double get(int col, int row) {return this.matrix[row][col];}
    public Matrix (int rows, int cols) {
        matrix = new double[rows][cols];
        this.rows = rows;
        this.cols = cols;
    }
    //projektions matrix konstruktor
    public Matrix(double s1, int alpha, boolean four) {
        if (four) {
            matrix = new double[][]{
                    {-s1 * (Math.sin(Math.toRadians(alpha))), 1.0, 0.0, (double) Constants.WINDOW_WIDTH / 2.},
                    {-s1 * (Math.cos(Math.toRadians(alpha))), 0.0, -1.0, (double) Constants.WINDOW_HEIGHT / 2.}
            };
        } else {
            matrix = new double[][]{
                    {-s1 * (Math.sin(Math.toRadians(alpha))), 1.0, 0.0},
                    {-s1 * (Math.cos(Math.toRadians(alpha))), 0.0, 1.0}
            };
        }
        rows = matrix.length;
        cols = matrix[0].length;
    }
    public Matrix multiply(Matrix other) throws Exception {
        if (this.cols != other.rows) {
            throw new Exception("Matrix dimension do not fit");
        }

        Matrix result = new Matrix(this.matrix.length, other.matrix[0].length);

        for (int i = 0; i < result.rows; i++){
            for (int j = 0; j < result.cols; j++) {
                for (int k = 0; k < this.cols; k++) {
                    result.matrix[i][j] += this.matrix[i][k] * other.matrix[k][j];
                }
            }
        }

        return result;
    }
    @Override
    public String toString() {
        StringBuilder matrixText = new StringBuilder();
        for (double[] row : matrix) {
            for(double column : row) {
                matrixText.append(STR."\{column}   ");
            }
            matrixText.append("\n");
        }

        return matrixText.toString();
    }
}
