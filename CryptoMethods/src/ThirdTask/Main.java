package ThirdTask;

import java.text.MessageFormat;
import java.util.List;

import static FirstTask.Main.outToFile;
import static FirstTask.Main.readFromFile;

public class Main {

    static int getLegendreSymbol(int a, int p) {
        if (a == 0) {
            return 0;
        }
        if (a == 1) {
            return 1;
        }
        if (a % 2 == 0) {
            return (int) (getLegendreSymbol(a / 2, p) * Math.pow(-1, (p * p - 1) / 8));
        } else {
            return (int) (getLegendreSymbol(p % a, a) * Math.pow(-1, (a - 1) * (p - 1) / 4));
        }
    }

    public static void main(String[] args) {
        List<String> lines = readFromFile();
        String[] nums = lines.get(0).split(" ");

        int a = Integer.parseInt(nums[0]),
                p = Integer.parseInt(nums[1]);

        outToFile(MessageFormat.format("L({0}, {1}) = {2}", a, p, getLegendreSymbol(a, p)));
    }
}
