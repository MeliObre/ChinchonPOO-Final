package juego.utilidades;

public class MetodosUtiles {
    public static boolean esInt(String text) {
        try {
            Integer.valueOf(text);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static int[] arrayStringAInt(String[] arrayIn) {
        try {
            int[] arraySalida = new int[arrayIn.length];
            for (int i = 0; i < arrayIn.length; i++) {
                arraySalida[i] = Integer.valueOf(arrayIn[i]);
            }
            return arraySalida;
        } catch (Exception e) {
            return null;
        }
    }

}
