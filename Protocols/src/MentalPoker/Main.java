package MentalPoker;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("Введите в файл input.txt имена участников (их количество > 1 && < 6)");
        System.out.println("Одна строка - один участник");
        System.out.println("1 строка - участник будет считаться организатором");

        System.out.println("Вы заполнили input.txt? (Да - 1, Нет - 0)");
        Scanner scanner = new Scanner(System.in);
        if (scanner.nextInt() == 0) {
            System.out.println("Заполните файл и запусте снова");
            return;
        }

        new Protocol().init();
    }
}
