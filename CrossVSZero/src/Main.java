import java.util.Random;
import java.util.Scanner;

public class Main {
    public static final int SIZE = 4; // размер поля игры
    public static final char EMPTY = '-';
    public static final char CROSS = 'X';
    public static final char ZERO = 'O';
    public static final boolean DEMO = false; // true - режим демонстрации, false - режим игры
    public static final int ATTEMPTS = 13; // количество тестовых матриц для демонстрации

    public static void main(String[] args) {
        if (DEMO) {
            System.out.println("ДЕМОНСТРАЦИЯ");
            demonstration();
        } else {
            System.out.println("координаты вводятся через пробел в формате: x y");
            game();
        }
    }

    public static void demonstration() {
        for (int i = 0; i < ATTEMPTS; i++) {
            System.out.println();
            char[][] field = getRandomField();
            printField(field);
            whoIsWinner(field);
        }
    }

    public static void game() {
        char[][] field = new char[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                field[i][j] = EMPTY;
            }
        }

        Scanner scanner = new Scanner(System.in);

        boolean isCrossTurn = true;

        while (true) {
            printField(field);
            System.out.println("Ходят " + (isCrossTurn ? "крестики" : "нолики") + "!");
            int r = 0;
            int c = 0;
            try {
                String input = scanner.nextLine(); // "2 3"
                String[] parts = input.split(" "); // ["2" , "3"]
                r = Integer.parseInt(parts[0]) - 1; // 2-1 = 1
                c = Integer.parseInt(parts[1]) - 1; // 3-1 = 2
            } catch (Exception e) {
                throw new RuntimeException("некорректный ввод координат");
            }

            if (r > (SIZE - 1) || c > (SIZE - 1) || r < 0 || c < 0) {
                System.out.println("координаты должны быть в пределах размера поля, т.е. от 1 до: " + SIZE);
                continue;
            }

            if (field[r][c] != EMPTY) {
                System.out.println("Сюда ходить нельзя");
                continue;
            }

            field[r][c] = isCrossTurn ? CROSS : ZERO;
            if (isWin(field, isCrossTurn ? CROSS : ZERO)) {
                printField(field);
                System.out.println("Победили " + (isCrossTurn ? "крестики" : "нолики"));
                break;
            } else {
                if (isCrossTurn) {
                    isCrossTurn = false;
                } else {
                    isCrossTurn = true;
                }
                //isCrossTurn = !isCrossTurn;
            }
        }

        System.out.println("Игра закончена!");
    }

    public static boolean isWin(char[][] field, char player) {
        // проверка строк
        for (int row = 0; row < SIZE; row++) {
            int count = 0;
            for (int cell = 0; cell < SIZE; cell++) {
                if (field[row][cell] == player) {
                    count++;
                }
            }
            if (count == SIZE) {
                return true;
            }
        }
        // проверка столбцов
        for (int cell = 0; cell < SIZE; cell++) {
            int count = 0;
            for (int row = 0; row < SIZE; row++) {
                if (field[row][cell] == player) {
                    count++;
                }
            }
            if (count == SIZE) {
                return true;
            }
        }
        // проверка основной диагонали
        int countMainDiagonal = 0;
        for (int i = 0; i < SIZE; i++) {
            if (field[i][i] == player) {
                countMainDiagonal++;
            }
        }
        if (countMainDiagonal == SIZE) {
            return true;
        }
        // проверка второстепенной диагонали
        int countSecondaryDiagonal = 0;
        for (int i = 0; i < SIZE; i++) {
            if (field[i][SIZE - 1 - i] == player) {
                countSecondaryDiagonal++;
            }
        }
        if (countSecondaryDiagonal == SIZE) {
            return true;
        }
        return false;
    }

    public static void printField(char[][] field) {
        for (char[] row : field) {
            for (char cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
    }

    public static char[][] getRandomField() {
        char[][] randomField = new char[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                int tempRandom = new Random().nextInt(7);
                switch (tempRandom) {
                    case 0:
                        randomField[i][j] = ZERO;
                        break;
                    case 1:
                        randomField[i][j] = CROSS;
                        break;
                    case 2:
                        randomField[i][j] = ZERO;
                        break;
                    case 3:
                        randomField[i][j] = CROSS;
                        break;
                    case 4:
                        randomField[i][j] = ZERO;
                        break;
                    case 5:
                        randomField[i][j] = CROSS;
                        break;
                    default:
                        randomField[i][j] = EMPTY;
                }
            }
        }
        return randomField;
    }

    public static void whoIsWinner(char[][] field) {
        if (isWin(field, CROSS)) {
            System.out.println("ПОБЕДИЛИ КРЕСТИКИ");
        } else if (isWin(field, ZERO)) {
            System.out.println("ПОБЕДИЛИ НОЛИКИ");
        } else {
            System.out.println("НИКТО НЕ ПОБЕДИЛ");
        }
    }
}