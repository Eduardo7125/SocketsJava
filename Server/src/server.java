import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class server {
    private int port;
    private String serverKeyword;
    private ServerSocket serverSocket;
    private Map<ClientHandler, String> clientHandlers;
    private ExecutorService threadPool;
    private int maxClients;
    private boolean hasHadClients = false;
    private boolean isRunning = true;

    public server(int port, String serverKeyword, int maxClients) {
        this.port = port;
        this.serverKeyword = serverKeyword;
        this.maxClients = maxClients;
        this.clientHandlers = Collections.synchronizedMap(new HashMap<>());
        this.threadPool = Executors.newFixedThreadPool(maxClients);
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("> Server chat at port " + port);
            System.out.println("> Inicializing server... OK");

            while (isRunning && !serverSocket.isClosed()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    if (clientHandlers.size() < maxClients) {
                        ClientHandler clientHandler = new ClientHandler(clientSocket);
                        clientHandlers.put(clientHandler, "");
                        threadPool.execute(clientHandler);
                        hasHadClients = true;
                        System.out.println("> Connection from client " + clientHandlers.size() + " ... OK");
                    } else {
                        clientSocket.close();
                    }
                } catch (IOException e) {
                    if (!serverSocket.isClosed()) {
                        System.err.println("Error accepting client connection: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
        } finally {
            shutdown();
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
            System.out.println("> Closing server... OK");
            System.out.println("> Bye!");
        } catch (IOException | InterruptedException e) {
            System.err.println("Error during shutdown: " + e.getMessage());
        }
    }

    private class ClientHandler implements Runnable {
        private Socket clientSocket;
        private BufferedReader in;
        private PrintWriter out;
        private String clientKeyword;
        private boolean isConnected = true;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
            try {
                this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                this.out = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                System.err.println("Error creating client handler: " + e.getMessage());
                isConnected = false;
            }
        }

        @Override
        public void run() {
            try {
                clientKeyword = in.readLine();
                System.out.println("> Inicializing chat... OK");

                String message;
                while (isConnected && (message = in.readLine()) != null) {
                    System.out.println("#Rebut del client " + getClientNumber() + ": " + message);

                    if (message.equals(clientKeyword)) {
                        System.out.println("> Client " + getClientNumber() + " keyword detected!");
                        isConnected = false;
                        closeClientConnection();
                    } else if (message.equals(serverKeyword)) {
                        System.out.println("> Server keyword detected!");
                        isRunning = false;
                        closeAllConnections();
                    } else {
                        String response = "Server received: " + message;
                        out.println(response);
                        System.out.println("#Enviar al client " + getClientNumber() + ": " + response);
                    }
                }
            } catch (IOException e) {
                System.err.println("Error handling client: " + e.getMessage());
            } finally {
                closeClientConnection();
            }
        }

        private int getClientNumber() {
            int number = 1;
            for (ClientHandler handler : clientHandlers.keySet()) {
                if (handler == this) {
                    return number;
                }
                number++;
            }
            return -1;
        }

        private void closeClientConnection() {
            try {
                clientHandlers.remove(this);
                if (in != null) in.close();
                if (out != null) out.close();
                if (clientSocket != null) clientSocket.close();
                System.out.println("> Closing chat... OK");

                if (hasHadClients && clientHandlers.isEmpty()) {
                    System.out.println("> No more clients connected. Shutting down server...");
                    isRunning = false;
                    serverSocket.close();
                }
            } catch (IOException e) {
                System.err.println("Error closing client connection: " + e.getMessage());
            }
        }
    }

    private void closeAllConnections() {
        for (ClientHandler handler : new ArrayList<>(clientHandlers.keySet())) {
            handler.closeClientConnection();
        }
        try {
            serverSocket.close();
        } catch (IOException e) {
            System.err.println("Error closing server socket: " + e.getMessage());
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