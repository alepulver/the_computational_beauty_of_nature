package cbn.viewers;

/*****************************************************************************/

public class Obj3d
{
    public static final byte MOVE = 0;
    public static final byte VECTOR = 1;
    public static final byte LINE = 2;
    public static final byte CIRCLE = 3;

    byte type;
    double point[];

    public Obj3d() {
        type = MOVE;
        point = new double[3];
    }

    public Obj3d(byte t, double x, double y, double z) {
        point = new double[3];
        type = t; point[0] = x; point[1] = y; point[2] = z;
    }
}
