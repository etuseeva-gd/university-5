import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner in = new Scanner(System.in);

        System.out.println("Введите число игроков (не больше 10)");
        int n = in.nextInt();

        if (n < 0 || n > 10) {
            throw new Exception("Не верное число участников.");
        }

        in.nextLine();
        System.out.println("Ведите имена игроков, раздающего пометьте символом *");

        User[] users = new User[n];
        for (int i = 0; i < n; i++) {
            String line = in.nextLine();
            users[i] = new User(line.contains("*"), line);
        }
        System.out.println(Arrays.toString(users));

    }
}
