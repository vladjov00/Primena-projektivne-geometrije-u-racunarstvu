package com.example.ppgr;

public class Vector {
    private final double x;
    private final double y;
    private final double z;

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
        this.z = 1;
    }

    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return "(" + String.format("%.3f", x) + ", " + String.format("%.3f", y) + ", " + String.format("%.3f", z) + ")";
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public Vector cross(Vector v2) {
        return new Vector(this.y * v2.z - this.z * v2.y,
                -this.x * v2.z + this.z * v2.x,
                this.x * v2.y - this.y * v2.x);
    }

    public double scalar(Vector v2) {
        return x * v2.getX() + y * v2.getY() + z * v2.getZ();
    }

    public Vector add(Vector v2) {
        return new Vector(this.x + v2.getX(), this.y + v2.getY(), this.z + v2.getZ());
    }

    public Vector subtract(Vector v2) {
        return new Vector(this.x - v2.getX(), this.y - v2.getY(), this.z - v2.getZ());
    }

    public Vector divideBy(double c) { return new Vector(x / c, y / c, z / c); }

    public Vector affinize() {
        return new Vector(Math.round(x / z), Math.round(y / z), 1.0);
    }

    public Vector multiplyBy(double scalar) { return new Vector(x*scalar, y*scalar, z*scalar); }

    public Vector normalize() { return this.divideBy(Math.sqrt(x*x + y*y + z*z)); }

    public boolean isZeroVector() {
        double EPS = 0.0001;
        return Math.abs(x) < EPS && Math.abs(y) < EPS && Math.abs(z) < EPS;
    }
}