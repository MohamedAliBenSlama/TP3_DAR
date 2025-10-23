package clientPackage;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 1234;
        try (Socket socket = new Socket(host, port)) {
            System.out.println(" Connecte au serveur " + host + ":" + port);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner sc = new Scanner(System.in);
            System.out.print("Entrez le premier nombre : ");
            String operande1 = sc.nextLine();
            System.out.print("Entrez l'operateur (+, -, *, /) : ");
            String operateur = sc.nextLine();
            System.out.print("Entrez le deuxieme nombre : ");
            String operande2 = sc.nextLine();
            out.println(operande1);
            out.println(operateur);
            out.println(operande2);
            String reponse = in.readLine();
            System.out.println(" Reponse du serveur : " + reponse);
            socket.close();
            sc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}