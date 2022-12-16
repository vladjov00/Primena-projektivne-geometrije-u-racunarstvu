package com.example.ppgr;

public class Vector4 extends Vector {

    private double w;

    public Vector4(double x, double y, double z) {
        super(x, y, z);
        w = 1;
    }

    public Vector4(double x, double y, double z, double w) {
        super(x,y,z);
        this.w = w;
    }

    @Override
    public double getX() {
        return super.getX();
    }

    @Override
    public double getY() {
        return super.getY();
    }

    @Override
    public double getZ() {
        return super.getZ();
    }

    public double getW() {
        return w;
    }

    @Override
    public String toString() {
        return "(" + String.format("%.3f", this.getX()) + ", " + String.format("%.3f", this.getY()) + ", "
                + String.format("%.3f", this.getZ()) + ", " + String.format("%.3f", this.getW()) + ")";
    }
}
