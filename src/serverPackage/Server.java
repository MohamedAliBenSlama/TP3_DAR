package serverPackage;

import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicInteger;
import operationPackage.Operation;

public class Server extends Thread {
    private static AtomicInteger clientCount = new AtomicInteger(0);
    private static int totalOperations = 0;

    public static void main(String[] args) {
        new Server().start();
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
            int clientNumber = clientCount.incrementAndGet();
            Socket socket = null;
            try {
                socket = socketServer.accept();
                System.out.println("nouveau client connecte : " + socket.getRemoteSocketAddress()+ " (Client n " + clientNumber + ")");
                new ClientProcess(socket,clientNumber).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class ClientProcess extends Thread {
        Socket socket;
        int clientNumber;
        public ClientProcess(Socket socket,int clientNumber)
        {
            this.socket=socket;
            this.clientNumber=clientNumber;
        }
        public void run()
        {
            try{
                System.out.println("le client n "+clientNumber+" est connecte");
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

                Operation op = (Operation) in.readObject();
                System.out.println("Reçu client n "+clientNumber+" : " + op.getOperande1() + " " + op.getOperateur() + " " + op.getOperande2());

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
                    default:
                        erreur = true;
                        break;
                }

                synchronized (Server.class) {
                    totalOperations++;
                    System.out.println("Nombre total d'opérations : " + totalOperations);
                }

                if (erreur)
                    out.writeObject("Erreur");
                else
                    out.writeObject("Resultat de client n "+clientNumber+"= " + resultat);

                System.out.println("Resultat envoyé au client n "+clientNumber);
                socket.close();
                System.out.println("Connexion fermée.");
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
