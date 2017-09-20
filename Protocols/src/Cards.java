import java.util.Arrays;

public class Cards {
    String[] suits = new String[]{"Черви", "Пики", "Крести", "Буби"};
    String[] digs = new String[]{"Валет", "Дама", "Король", "Туз"};
    String[] cards = null;

    String[] deck = null;

    Cards() {
        this.cards = new String[52];
        int k = 0;
        for (String suit : this.suits) {
            for (int i = 2; i < 11; i++) {
                this.deck[k++] = i + "-" + suit;
            }

            for (String dig : this.digs) {
                this.deck[k++] = dig + "-" + suit;
            }
        }
        //System.out.println(Arrays.toString(this.cards));
    }
}
