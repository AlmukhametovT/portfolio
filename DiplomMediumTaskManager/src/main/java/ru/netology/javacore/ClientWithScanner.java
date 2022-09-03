package ru.netology.javacore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

public class ClientWithScanner {
    public static void main(String[] args) throws NotCorrectTaskException {

        int port = 8989;
        System.out.println("введите запрос серверу в формате \"{ \"type\": \"ADD\", \"task\": \"Название задачи\" }\" ");
        System.out.println("где type - тип операции (ADD или REMOVE), а task - сама задача.");
        System.out.println("либо введите \"random\" для добавления случайной задачи, или \"end\" для выхода");

        while (true) {

            try (Socket socket = new Socket("localhost", port);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                Scanner scanner = new Scanner(System.in);
                String input = scanner.nextLine();

                if (input.equals("end")) {
                    out.println(input);
                    System.out.println("Bye-Bye!");
                    return;
                } else if (input.equals("random")) {
                    out.println("{ \"type\": \"ADD\", \"task\": \"task #" + pickRandomChar() + "\" }");
                    System.out.println(in.readLine());
                    continue;
                }

                checkRequest(input);

                out.println(input);
                System.out.println(in.readLine());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static char pickRandomChar() {
        String chars = "ABCDEFG";
        return chars.charAt(new Random().nextInt(chars.length()));
    }

    public static void checkRequest(String input) throws NotCorrectTaskException {
        boolean check1 = input.startsWith("{ \"type\": \"ADD\", \"task\": \"") ||
                input.startsWith("{ \"type\": \"REMOVE\", \"task\": \"");
        boolean check2 = input.endsWith("\" }");
        boolean check3 = input.substring(11).startsWith("ADD") || input.substring(11).startsWith("REMOVE");

        if (!check1 || !check2 || !check3) {
            throw new NotCorrectTaskException("Некорректный запрос, формат запроса \"{ \"type\": \"ADD\", \"task\": \"Название задачи\" }\"");
        }
    }
}