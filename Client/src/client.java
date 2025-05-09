import java.io.*;
import java.net.*;
import java.util.Scanner;

public class client {
    private String host;
    private int port;
    private String clientKeyword;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Scanner scanner;
    private boolean isRunning = true;

    public client(String host, int port, String clientKeyword) {
        this.host = host;
        this.port = port;
        this.clientKeyword = clientKeyword;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.println("> Client chat to port " + port);
            System.out.println("> Inicializing client... OK");

            out.println(clientKeyword);
            System.out.println("> Inicializing chat... OK");

            Thread readThread = new Thread(() -> {
                try {
                    String message;
                    while (isRunning && (message = in.readLine()) != null) {
                        System.out.println("#Rebut del servidor: " + message);
                    }
                } catch (IOException e) {
                    if (!socket.isClosed()) {
                        System.err.println("Error reading from server: " + e.getMessage());
                    }
                }
            });
            readThread.start();

            String userInput;
            while (isRunning && (userInput = scanner.nextLine()) != null) {
                System.out.println("#Enviar al servidor: " + userInput);
                out.println(userInput);

                if (userInput.equals(clientKeyword)) {
                    System.out.println("> Client keyword detected!");
                    isRunning = false;
                }
            }

        } catch (IOException e) {
            System.err.println("Error connecting to server: " + e.getMessage());
        } finally {
            close();
        }
    }

    private void close() {
        try {
            isRunning = false;
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
            if (scanner != null) scanner.close();
            System.out.println("> Closing chat... OK");
            System.out.println("> Closing client... OK");
            System.out.println("> Bye!");
        } catch (IOException e) {
            System.err.println("Error closing client: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        boolean validArgs = true;
        if (args.length != 3) {
            System.out.println("Usage: java Client <host> <port> <client_keyword>");
            validArgs = false;
        }

        if (validArgs) {
            try {
                String host = args[0];
                int port = Integer.parseInt(args[1]);
                String clientKeyword = args[2];

                client client = new client(host, port, clientKeyword);
                client.start();
            } catch (NumberFormatException e) {
                System.out.println("Invalid port number");
            }
        }
    }
} 