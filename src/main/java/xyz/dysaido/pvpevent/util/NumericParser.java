package xyz.dysaido.pvpevent.util;

public class NumericParser {

    public static int floor(float value) {
        int i = (int)value;
        return value < (float)i ? i - 1 : i;
    }

    public static int floor(double value) {
        int i = (int)value;
        return value < (double)i ? i - 1 : i;
    }

    public static int ceil(float value) {
        int i = (int)value;
        return value > (float)i ? i + 1 : i;
    }

    public static int ceil(double value) {
        int i = (int)value;
        return value > (double)i ? i + 1 : i;
    }

    public static boolean ensureDouble(String string) {
        try {
            Double.parseDouble(string);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean ensureInteger(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean ensureFloat(String string) {
        try {
            Float.parseFloat(string);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}