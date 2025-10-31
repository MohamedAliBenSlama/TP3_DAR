package serverPackage;

import java.io.*;
import java.net.*;
import operationPackage.Operation;

public class Server extends Thread {

    private static int clientCount = 0; 
    private static int totalOperations = 0;

    public static void main(String[] args) {
        new Server().start();
    }

    // Méthode synchronisée pour incrémenter le nombre de clients
    public static synchronized int incrementClientCount() {
        clientCount++;
        return clientCount;
    }

    public void run() {
        ServerSocket socketServer = null;
        try {
            socketServer = new ServerSocket(1234);
            System.out.println("Serveur prêt sur le port 1234...");
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            int clientNumber = incrementClientCount(); // utilisation de la méthode synchronisée
            try {
                Socket socket = socketServer.accept();
                System.out.println("Nouveau client connecté : " 
                    + socket.getRemoteSocketAddress() + " (Client n° " + clientNumber + ")");
                new ClientProcess(socket, clientNumber).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class ClientProcess extends Thread {
        Socket socket;
        int clientNumber;

        public ClientProcess(Socket socket, int clientNumber) {
            this.socket = socket;
            this.clientNumber = clientNumber;
        }

        public void run() {
            try {
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

                Operation op = (Operation) in.readObject();
                System.out.println("Reçu du client n° " + clientNumber + " : "
                        + op.getOperande1() + " " + op.getOperateur() + " " + op.getOperande2());

                double resultat = 0;
                boolean erreur = false;

                switch (op.getOperateur()) {
                    case "+": resultat = op.getOperande1() + op.getOperande2(); break;
                    case "-": resultat = op.getOperande1() - op.getOperande2(); break;
                    case "*": resultat = op.getOperande1() * op.getOperande2(); break;
                    case "/":
                        if (op.getOperande2() != 0) resultat = op.getOperande1() / op.getOperande2();
                        else erreur = true;
                        break;
                    default: erreur = true; break;
                }

                synchronized (Server.class) {
                    totalOperations++;
                    System.out.println("Nombre total d'opérations effectuées : " + totalOperations);
                }

                if (erreur)
                    out.writeObject("Erreur : division par zéro");
                else
                    out.writeObject("Résultat pour client n° " + clientNumber + " = " + resultat);

                socket.close();
                System.out.println("Connexion client n° " + clientNumber + " fermée.");

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
