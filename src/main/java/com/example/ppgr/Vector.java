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
        return "(" + x + ", " + y + ", " + z + ")";
    }

    public Vector cross(Vector v2) {
        return new Vector(this.y * v2.z - this.z * v2.y,
                -this.x * v2.z + this.z * v2.x,
                this.x * v2.y - this.y * v2.x);
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

    public Vector add(Vector v2) {
        return new Vector(this.x + v2.x, this.y + v2.y, this.z + v2.z);
    }

    public Vector divideBy(double c) { return new Vector(x / c, y / c, z / c); }

    public Vector affinize() {
        return new Vector(Math.round(x / z), Math.round(y / z), 1.0);
    }
}