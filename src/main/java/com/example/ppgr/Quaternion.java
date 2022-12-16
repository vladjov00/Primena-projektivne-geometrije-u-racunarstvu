package com.example.ppgr;

public class Quaternion {
    private final Vector p;
    private final double w;

    public Quaternion(Vector p, double w) {
        this.p = p;
        this.w = w;
    }

    public Quaternion(double x, double y, double z, double w) {
        this.p = new Vector(x,y,z);
        this.w = w;
    }

    public Vector getP() {
        return p;
    }

    public double getW() {
        return w;
    }

    @Override
    public String toString() {
        return String.format("%.5f", this.p.getX()) + "*i + "
                + String.format("%.5f", this.p.getY()) + "*j + "
                + String.format("%.5f", this.p.getZ()) + "*k + "
                + String.format("%.5f", w);
    }

    public Quaternion multiplyBy(double scalar) {
        Vector pp = this.p.multiplyBy(scalar);
        double wp = this.w * scalar;

        return new Quaternion(pp, wp);
    }
}
