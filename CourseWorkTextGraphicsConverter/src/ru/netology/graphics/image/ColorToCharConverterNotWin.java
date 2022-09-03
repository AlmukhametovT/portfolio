package ru.netology.graphics.image;

public class ColorToCharConverterNotWin implements TextColorSchema {
    protected static char[] charForPixel = {'▇', '●', '◉', '◍', '◎', '○', '☉', '◌', '-'};

    @Override
    public char convert(int color) {
        return charForPixel[color * charForPixel.length / 256];
    }
}