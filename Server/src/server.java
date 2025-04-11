import java.io.*;
import java.net.*;

public class server {

    private static final int PUERTO = 1234;
    private static final String PALABRA_CLAVE = "stop";

    public static void main(String[] args) {
    	boolean stop = false;
        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
        	System.out.println("Palabra secreta : stop");
            System.out.println("Iniciando servidor... OK");

            try (Socket clientSocket = serverSocket.accept();
                 BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);
                 BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in))) {

                System.out.println("Cliente conectado... OK");

                String mensajeCliente;
                String mensajeServidor;

                while (true) {
                    mensajeCliente = input.readLine();
                    if (mensajeCliente == null) {
                    	stop = true;
                    };
                    System.out.println("Mensaje recibido del cliente: " + mensajeCliente);

                    if (mensajeCliente.equalsIgnoreCase(PALABRA_CLAVE)) {
                        System.out.println("Cerrando servidor... OK");
                        stop = true;
                    }

                    System.out.print("Escribe tu mensaje para el cliente: ");
                    mensajeServidor = consoleInput.readLine();
                    output.println(mensajeServidor);

                    if (mensajeServidor.equalsIgnoreCase(PALABRA_CLAVE)) {
                    	stop = true;
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Error en el servidor: " + e.getMessage());
        }
    }
}
