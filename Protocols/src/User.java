import java.text.MessageFormat;

public class User {
    Boolean isHead = null;
    String name = null;

    EncAndDec.Keys keys = null;

    String[] cards = null;
    String[] remainingDeck = null;

    User(Boolean isHead, String name) {
        this.isHead = isHead;
        this.name = name;

        //keys.genKeys();
    }

    @Override
    public String toString() {
        return name;
    }
}
