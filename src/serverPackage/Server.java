package serverPackage;

import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Server extends Thread{
    private static AtomicInteger clientCount = new AtomicInteger(0);
    public static void main(String[] args) {
        new Server().start();

    }
    public void run(){
        ServerSocket socketServer = null;
        try {
            socketServer = new ServerSocket(1234);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        while (true) 
        {
            int clientNumber = clientCount.incrementAndGet();
            Socket socket = null;
            try {
                socket = socketServer.accept();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println("nouveau client connecte : " + socket.getRemoteSocketAddress()+ " (Client n " + clientNumber + ")");
            new ClientProcess(socket,clientNumber).start();
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
            try{            System.out.println("le client n "+clientNumber+" est connecte");
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

           
            String operande1 = in.readLine();
            String operateur = in.readLine();
            String operande2 = in.readLine();

            System.out.println("Reçu client n "+clientNumber+" : " + operande1 + " " + operateur + " " + operande2);

       
            double resultat = 0;
            try {
                double a = Double.parseDouble(operande1);
                double b = Double.parseDouble(operande2);

                switch (operateur) {
                    case "+": resultat = a + b; break;
                    case "-": resultat = a - b; break;
                    case "*": resultat = a * b; break;
                    case "/":
                        if (b != 0) resultat = a / b;
                        else {
                            out.println("Erreur");
                            socket.close();
                            return;
                        }
                        break;
                    default:
                        out.println("Erreur");
                        socket.close();
                        return;
                }

                out.println("Resultat de client n "+clientNumber+"= " + resultat);
                System.out.println("Resultat envoyé au client n "+clientNumber);

            } catch (NumberFormatException e) {
                out.println("Erreur");
            }

            socket.close();
            System.out.println("Connexion fermée.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    }}
        

    
        
    

