package ru.netology.graphics;

import ru.netology.graphics.image.ColorToCharConverter;
import ru.netology.graphics.image.ColorToCharConverterNotWin;
import ru.netology.graphics.image.ImageToTextConverter;
import ru.netology.graphics.image.TextGraphicsConverter;
import ru.netology.graphics.server.GServer;

import java.io.File;
import java.io.PrintWriter;

public class Main {
    public static void main(String[] args) throws Exception {
        TextGraphicsConverter converter = new ImageToTextConverter(); // Создайте тут объект вашего класса конвертера
//        converter.setMaxRatio(2);  // выставляет максимально допустимое соотношение сторон картинки
//        converter.setMaxHeight(50);
//        converter.setMaxWidth(70);
//        converter.setTextColorSchema(new ColorToCharConverterNotWin());

        GServer server = new GServer(converter); // Создаём объект сервера
        server.start(); // Запускаем

        // Или то же, но с выводом на экран:
        String url = "https://itproger.com/img/news/1572594722.png";

        // URL если картинка с ПК
//        String url = "C:\\Users\\User\\Desktop\\1.jpg";

        // вывод сконвертированного изображения на консоль
//        String imgTxt = converter.convert(url);
//        System.out.println(imgTxt);
    }
}