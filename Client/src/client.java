import java.io.*;
import java.net.*;

public class client {

    private static final String HOST = "localhost";
    private static final int PUERTO = 1234;
    private static final String PALABRA_CLAVE = "stop";

    public static void main(String[] args) {
    	System.out.println("Palabra secreta : stop");
        try (Socket socket = new Socket(HOST, PUERTO);
             BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Conectado al servidor... OK");

            System.out.print("Introduce el primer mensaje: ");
            String mensaje = consoleInput.readLine();
            output.println(mensaje);
            System.out.println("Mensaje enviado... OK");

            while (true) {
                String respuesta = input.readLine();
                System.out.println("Respuesta del servidor: " + respuesta);

                if (respuesta.equalsIgnoreCase(PALABRA_CLAVE)) {
                    return;
                }

                System.out.print("Introduce un mensaje para el servidor: ");
                mensaje = consoleInput.readLine();
                output.println(mensaje);

                if (mensaje.equalsIgnoreCase(PALABRA_CLAVE)) {
                    return;
                }
            }
        } catch (IOException e) {
            System.out.println("Error en el cliente: " + e.getMessage());
        }
    }
}
