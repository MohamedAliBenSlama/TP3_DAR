package clientPackage;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import operationPackage.*;

public class Client {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 1234;
        try (Socket socket = new Socket(host, port)) {
            System.out.println("Connecté au serveur " + host + ":" + port);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            Scanner sc = new Scanner(System.in);
            System.out.print("Entrez le premier nombre : ");
            double op1 = sc.nextDouble();
            System.out.print("Entrez l’opérateur (+, -, *, /) : ");
            String operateur = sc.next();
            System.out.print("Entrez le deuxième nombre : ");
            double op2 = sc.nextDouble();   
            Operation operation = new Operation(op1, operateur, op2);
            out.writeObject(operation);
            String reponse = (String) in.readObject();
            System.out.println("Réponse du serveur : " + reponse);
            sc.close();
            socket.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}