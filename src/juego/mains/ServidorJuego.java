package juego.mains;
import java.net.*;
import java.rmi.RemoteException;
import java.util.Enumeration;

import ar.edu.unlu.rmimvc.RMIMVCException;
import ar.edu.unlu.rmimvc.servidor.Servidor;
import juego.modelo.Juego;

public class ServidorJuego {
    public static void main(String[] args) {

        String listenIP = "0.0.0.0";
        String port = "8888";


        String publicIP = getLocalIPAddress();
        if (publicIP == null) {
            System.err.println("No se pudo determinar la IP local. Abortando.");
            return;
        }

        // Informamos a RMI  nuestra IP real
        System.setProperty("java.rmi.server.hostname", publicIP);

        try {

            Juego modelo = new Juego();
            Servidor servidor = new Servidor(listenIP, Integer.parseInt(port));

            servidor.iniciar(modelo);
            System.out.println("==========================================");
            System.out.println(">>> SERVIDOR DE CHINCHÓN INICIADO <<<");
            System.out.println("IP para conectar clientes: " + publicIP);
            System.out.println("Puerto: " + port);
            System.out.println("==========================================");

        } catch (RemoteException | RMIMVCException e) {
            e.printStackTrace();
        }
    }

    // obtener la IP real (IPv4)
    private static String getLocalIPAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                if (!ni.isUp() || ni.isLoopback() || ni.isVirtual())
                    continue;

                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (address instanceof Inet4Address && !address.isLoopbackAddress()) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }
}
