import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class server {
    private int port;
    private String serverKeyword;
    private ServerSocket serverSocket;
    private Map<Socket, String> clients;
    private ExecutorService threadPool;
    private int maxClients;
    private boolean hasHadClients = false;
    private boolean isRunning = true;
    private Socket lastClient = null;
    private Scanner scanner;

    public server(int port, String serverKeyword, int maxClients) {
        this.port = port;
        this.serverKeyword = serverKeyword;
        this.maxClients = maxClients;
        this.clients = Collections.synchronizedMap(new HashMap<>());
        this.threadPool = Executors.newFixedThreadPool(maxClients);
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("> Server chat at port " + port);
            System.out.println("> Inicializing server... OK");
            System.out.print("> ");

            Thread serverInputThread = new Thread(() -> {
                while (isRunning) {
                    String serverMessage = scanner.nextLine();
                    if (lastClient != null && !lastClient.isClosed()) {
                        sendMessage(lastClient, "Server: " + serverMessage);
                        System.out.println("#Enviar al client " + getClientNumber(lastClient) + ": " + serverMessage);
                    }
                    System.out.print("> ");
                }
            });
            serverInputThread.start();

            while (isRunning && !serverSocket.isClosed()) {
            	Socket clientSocket;
                try {
                	clientSocket = serverSocket.accept();
                    if (clients.size() < maxClients) {
                        handleNewClient(clientSocket);
                    } else {
                        clientSocket.close();
                    }
                } catch (IOException e) {
                    if (!serverSocket.isClosed()) {
                        System.err.println("Error accepting client connection: " + e.getMessage());
                        clientSocket.close();
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
        } finally {
            shutdown();
        }
    }

    private void handleNewClient(Socket clientSocket) {
        clients.put(clientSocket, "");
        threadPool.execute(() -> handleClient(clientSocket));
        hasHadClients = true;
        System.out.println("> Connection from client " + clients.size() + " ... OK");
        System.out.print("> ");
    }

    private void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
            
            String clientKeyword = in.readLine();
            System.out.println("> Inicializing chat... OK");

            String message;
            while (!clientSocket.isClosed() && (message = in.readLine()) != null) {
                System.out.println("#Rebut del client " + getClientNumber(clientSocket) + ": " + message);
                lastClient = clientSocket;
                System.out.print("> ");

                if (message.equals(clientKeyword)) {
                    System.out.println("> Client " + getClientNumber(clientSocket) + " keyword detected!");
                    closeClient(clientSocket);
                } else if (message.equals(serverKeyword)) {
                    System.out.println("> Server keyword detected!");
                    isRunning = false;
                    closeAllClients();
                } else {
                    String response = "Server received: " + message;
                    sendMessage(clientSocket, response);
                    System.out.println("#Enviar al client " + getClientNumber(clientSocket) + ": " + response);
                }
            }
        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            closeClient(clientSocket);
        }
    }

    private void sendMessage(Socket client, String message) {
        try {
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
            out.println(message);
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }

    private int getClientNumber(Socket client) {
        int number = 1;
        for (Socket socket : clients.keySet()) {
            if (socket == client) {
                return number;
            }
            number++;
        }
        return -1;
    }

    private void closeClient(Socket client) {
        try {
            clients.remove(client);
            if (!client.isClosed()) {
                client.close();
            }
            System.out.println("> Closing chat... OK");

            if (hasHadClients && clients.isEmpty()) {
                System.out.println("> No more clients connected. Shutting down server...");
                isRunning = false;
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing client: " + e.getMessage());
        }
    }

    private void closeAllClients() {
        for (Socket client : new ArrayList<>(clients.keySet())) {
            closeClient(client);
        }
        try {
            serverSocket.close();
        } catch (IOException e) {
            System.err.println("Error closing server socket: " + e.getMessage());
        }
    }

    private void shutdown() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            threadPool.shutdown();
            if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
            if (scanner != null) scanner.close();
            System.out.println("> Closing server... OK");
            System.out.println("> Bye!");
        } catch (IOException | InterruptedException e) {
            System.err.println("Error during shutdown: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        boolean validArgs = true;
        if (args.length != 3) {
            System.out.println("Usage: java Server <port> <server_keyword> <max_clients>");
            validArgs = false;
        }

        if (validArgs) {
            try {
                int port = Integer.parseInt(args[0]);
                String serverKeyword = args[1];
                int maxClients = Integer.parseInt(args[2]);

                server server = new server(port, serverKeyword, maxClients);
                server.start();
            } catch (NumberFormatException e) {
                System.out.println("Invalid port or max_clients number");
            }
        }
    }
} 