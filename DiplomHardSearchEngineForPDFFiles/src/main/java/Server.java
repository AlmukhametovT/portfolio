import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Server {
    private int port;
    private BooleanSearchEngine engine;

    public Server(int port, BooleanSearchEngine engine) {
        this.port = port;
        this.engine = engine;
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
                    final String knock = in.readLine();

                    if (knock.equals("end")) {
                        System.out.println("Сonnection closed");
                        return;
                    }

                    if (!engine.getAllWords().contains(knock.toLowerCase())) {
                        out.println("Указанное слово не содержится среди pdf-файлов!");
                    }

                    System.out.println("Результат поиска слова \'" + knock + "\':");
                    System.out.println(listToJson(engine.search(knock)));
                    out.println(listToJson(engine.search(knock)));
                }
            }
        } catch (IOException e) {
            System.out.println("Не могу стартовать сервер");
            e.printStackTrace();
        }
    }

    public static String listToJson(List<PageEntry> pageEntryList) {
        Type listType = new TypeToken<List<PageEntry>>() {
        }.getType();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
//        Gson gson = builder.create();
        String json = gson.toJson(pageEntryList, listType);
        return json;
    }
}