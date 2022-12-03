package com.example.ppgr;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 *  A general class of matrices that contains different classes and operations for matrices of
 *  specific size.
 */
public class Matrix {

    /**
     * Matrix of size 3 x 3
     */
    public static class Matrix3x3 {
        private Vector v1;
        private Vector v2;
        private Vector v3;

        public Matrix3x3(Vector v1, Vector v2, Vector v3) {
            this.v1 = v1;
            this.v2 = v2;
            this.v3 = v3;
        }

        public Matrix3x3(double a00, double a01, double a02,
                         double a10, double a11, double a12,
                         double a20, double a21, double a22) {
            this.v1 = new Vector(a00, a10, a20);
            this.v2 = new Vector(a01, a11, a21);
            this.v3 = new Vector(a02, a12, a22);
        }

        public Matrix3x3(double[] elements) throws Exception {
            if(elements.length != 9) {
                throw new Exception("Length of array must be 9!");
            }
            this.v1 = new Vector(elements[0], elements[3], elements[6]);
            this.v2 = new Vector(elements[1], elements[4], elements[7]);
            this.v3 = new Vector(elements[2], elements[5], elements[8]);
        }

        public Vector getV1() {
            return v1;
        }

        public Vector getV2() {
            return v2;
        }

        public Vector getV3() {
            return v3;
        }

        public double[] getElementsAsArray() {
            return new double[]{
                    this.v1.getX(), this.v2.getX(), this.v3.getX(),
                    this.v1.getY(), this.v2.getY(), this.v3.getY(),
                    this.v1.getZ(), this.v2.getZ(), this.v3.getZ()
            };
        }

        public Matrix3x3 conv(Matrix3x3 M) {
            return new Matrix3x3(v1.getX() * M.getV1().getX(), v2.getX() * M.getV2().getX(), v3.getX() * M.getV3().getX(),
                    v1.getY() * M.getV1().getY(), v2.getY() * M.getV2().getY(), v3.getY() * M.getV3().getY(),
                    v1.getZ() * M.getV1().getZ(), v2.getZ() * M.getV2().getZ(), v3.getZ() * M.getV3().getZ());
        }

        public Matrix3x3 multiplyBy(double scalar) {
            return new Matrix3x3(v1.multiplyBy(scalar), v2.multiplyBy(scalar), v3.multiplyBy(scalar));
        }

        public Vector multiplyBy(Vector v) {
            double[] a = this.getElementsAsArray();
            return new Vector(a[0] * v.getX() + a[1] * v.getY() + a[2] * v.getZ(),
                    a[3] * v.getX() + a[4] * v.getY() + a[5] * v.getZ(),
                    a[6] * v.getX() + a[7] * v.getY() + a[8] * v.getZ());
        }

        private long euclid(long a, long b) {
            if(a == 0) return b;
            if(b == 0) return a;

            if(a == b) return a;
            else if(a > b) return euclid(a%b, b);
            else return euclid(a, b%a);
        }

        public Matrix3x3 reduce() {
            long gcf = 0;
            double[] elements = this.getElementsAsArray();

            for(int i = 0; i < 9; i++) {
                gcf = euclid(gcf, (long)Math.abs(elements[i]));
            }

            return this.multiplyBy(1.0 / gcf);
        }

        public Matrix3x3 add(Matrix3x3 A) {
            return new Matrix3x3(v1.add(A.getV1()), v2.add(A.getV2()), v3.add(A.getV3()));
        }

        public Matrix3x3 subtract(Matrix3x3 A) {
            return new Matrix3x3(v1.subtract(A.getV1()), v2.subtract(A.getV2()), v3.subtract(A.getV3()));
        }

        @Override
        public String toString() {
            return  "[ [ " + String.format("%.3f", v1.getX()) + " " + String.format("%.3f", v2.getX()) + " " + String.format("%.3f", v3.getX()) + " ]\n" +
                    "  [ " + String.format("%.3f", v1.getY()) + " " + String.format("%.3f", v2.getY()) + " " + String.format("%.3f", v3.getY()) + " ]\n" +
                    "  [ " + String.format("%.3f", v1.getZ()) + " " + String.format("%.3f", v2.getZ()) + " " + String.format("%.3f", v3.getZ()) + " ] ]";
        }
    }

    public static Matrix3x3 multiply(Matrix3x3 A, Matrix3x3 B) {
        Matrix3x3 At = transpose(A);
        return new Matrix3x3(At.getV1().scalar(B.getV1()), At.getV1().scalar(B.getV2()), At.getV1().scalar(B.getV3()),
                At.getV2().scalar(B.getV1()), At.getV2().scalar(B.getV2()), At.getV2().scalar(B.getV3()),
                At.getV3().scalar(B.getV1()), At.getV3().scalar(B.getV2()), At.getV3().scalar(B.getV3()));
    }

    public static double determinant3x3(Matrix3x3 M) {
        return  M.getV1().getX() * (M.getV2().getY()*M.getV3().getZ() - M.getV3().getY()*M.getV2().getZ()) -
                M.getV2().getX() * (M.getV1().getY()*M.getV3().getZ() - M.getV3().getY()*M.getV1().getZ()) +
                M.getV3().getX() * (M.getV1().getY()*M.getV2().getZ() - M.getV2().getY()*M.getV1().getZ());
    }

    public static Matrix3x3 transpose(Matrix3x3 M) {
        return new Matrix3x3(M.getV1().getX(), M.getV1().getY(), M.getV1().getZ(),
                M.getV2().getX(), M.getV2().getY(), M.getV2().getZ(),
                M.getV3().getX(), M.getV3().getY(), M.getV3().getZ());
    }

    public static Matrix3x3 adj(Matrix3x3 M) {
        return transpose(cofactors(M));
    }

    public static Matrix3x3 inverse(Matrix3x3 M) throws Exception {
        Matrix3x3 adj = adj(M);
        double det = determinant3x3(M);

        if(det == 0) {
            throw new Exception("Determinant equals 0!");
        }

        return adj; // for the purposes of task2 there is no need to multiply by 1/det
    }

    public static Matrix3x3 cofactors(Matrix3x3 M) {
        Matrix3x3 minors = minors(M);
        return minors.conv(new Matrix3x3(1,-1,1,-1,1,-1,1,-1,1));
    }

    public static Matrix3x3 minors(Matrix3x3 M) {
        double M00 = M.getV2().getY() * M.getV3().getZ() - M.getV3().getY() * M.getV2().getZ();
        double M01 = M.getV1().getY() * M.getV3().getZ() - M.getV3().getY() * M.getV1().getZ();
        double M02 = M.getV1().getY() * M.getV2().getZ() - M.getV2().getY() * M.getV1().getZ();
        double M10 = M.getV2().getX() * M.getV3().getZ() - M.getV3().getX() * M.getV2().getZ();
        double M11 = M.getV1().getX() * M.getV3().getZ() - M.getV3().getX() * M.getV1().getZ();
        double M12 = M.getV1().getX() * M.getV2().getZ() - M.getV2().getX() * M.getV1().getZ();
        double M20 = M.getV2().getX() * M.getV3().getY() - M.getV3().getX() * M.getV2().getY();
        double M21 = M.getV1().getX() * M.getV3().getY() - M.getV3().getX() * M.getV1().getY();
        double M22 = M.getV1().getX() * M.getV2().getY() - M.getV2().getX() * M.getV1().getY();

        return new Matrix3x3(M00, M01, M02, M10, M11, M12, M20, M21, M22);
    }

    public static Matrix3x3 identity3() { return new Matrix3x3(1, 0, 0, 0, 1, 0, 0, 0, 1); }

    public static Matrix3x3 loadMatrix3x3FromText(String text) {
        try (Scanner scanner = new Scanner(text)) {

            scanner.next();
            scanner.next();
            double a00 = scanner.nextDouble();
            double a01 = scanner.nextDouble();
            double a02 = scanner.nextDouble();
            scanner.next();
            scanner.next();
            double a10 = scanner.nextDouble();
            double a11 = scanner.nextDouble();
            double a12 = scanner.nextDouble();
            scanner.next();
            scanner.next();
            double a20 = scanner.nextDouble();
            double a21 = scanner.nextDouble();
            double a22 = scanner.nextDouble();

            return new Matrix3x3(a00, a01, a02, a10, a11, a12, a20, a21, a22);
        }
        catch (NoSuchElementException e1) {
            System.out.println("Bad format!");
            return null;
        }

    }

    public static Matrix.Matrix3x3 multiplyVectors(Vector v1, Vector v2) {
        return new Matrix3x3(v1.multiplyBy(v2.getX()), v1.multiplyBy(v2.getY()), v1.multiplyBy(v2.getZ()));
    }
}
