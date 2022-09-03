import java.io.File;

public class Main {
    public static void main(String[] args) throws Exception {
        BooleanSearchEngine engine = new BooleanSearchEngine(new File("pdfs"));
//        System.out.println(engine.search("БиЗнес"));
//
//         здесь создайте сервер, который отвечал бы на нужные запросы
//         слушать он должен порт 8989
//         отвечать на запросы /{word} -> возвращённое значение метода search(word) в JSON-формате

        Server server = new Server(8989, engine);
        server.start();
    }
}