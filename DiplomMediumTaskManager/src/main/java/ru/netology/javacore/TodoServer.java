package ru.netology.javacore;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TodoServer {
    private int port;
    private Todos todos;

    public TodoServer(int port, Todos todos) {
        this.port = port;
        this.todos = todos;
    }

    public void start() {
        System.out.println("Starting server at " + port + "...");
        try (ServerSocket serverSocket = new ServerSocket(port);) { // стартуем сервер один(!) раз
            while (true) { // в цикле(!) принимаем подключения
                try (
                        Socket socket = serverSocket.accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter out = new PrintWriter(socket.getOutputStream());
                ) {
                    // обработка одного подключения
                    JSONParser parser = new JSONParser();
                    String type = null;
                    String task = null;
                    final String knock = in.readLine();

                    if (knock.equals("end")) {
                        System.out.println("Сonnection closed");
                        return;
                    }

                    try {
                        Object obj = parser.parse(knock);
                        JSONObject jsonObject = (JSONObject) obj;
                        type = (String) jsonObject.get("type");
                        task = (String) jsonObject.get("task");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (type.equals("ADD")) {
                        todos.addTask(task);
                    } else {
                        if (type.equals("REMOVE")) {
                            todos.removeTask(task);
                        }
                    }
                    out.println(todos.getAllTasks());
                }
            }
        } catch (IOException e) {
            System.out.println("Не могу стартовать сервер");
            e.printStackTrace();
        }
    }
}