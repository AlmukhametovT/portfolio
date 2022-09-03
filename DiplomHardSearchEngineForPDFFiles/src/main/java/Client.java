import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {

        int port = 8989;
        System.out.println("введите слово для поиска среди pdf-файлов");
        System.out.println("либо введите \"end\" для выхода");

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
                }

                out.println(input);
//                String answer = in.readLine();
//                printPrettyJson(answer);
//                System.out.println(answer);
                in.lines().forEach(System.out::println);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

//    public static void printPrettyJson (String answer) {
//        JSONParser parser = new JSONParser();
//        try {
//            Object obj = parser.parse(answer);
//            JSONObject jsonObject = (JSONObject) obj;
//            System.out.println(jsonObject);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//    }
}