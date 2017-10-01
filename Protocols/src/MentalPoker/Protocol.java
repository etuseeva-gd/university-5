package MentalPoker;

import com.sun.corba.se.spi.ior.TaggedProfileTemplate;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Protocol {
    static class User {
        String name;
        CryptoSystem.Keys keys;

        Boolean isHead;

        User(String name, BigInteger p, Boolean isHead) throws NoSuchAlgorithmException {
            this.isHead = isHead;
            this.name = name;

            this.keys = new CryptoSystem.Keys(p);
        }

        StringBuilder encrypt(List<String> cards, Boolean isCardsStr) {
            StringBuilder encryptCards = new StringBuilder();
            cards.forEach(card -> {
                if (isCardsStr) {
                    encryptCards.append(CryptoSystem.encryptString(card, this.keys)).append("\n");
                } else {
                    encryptCards.append(CryptoSystem.encrypt(card, this.keys)).append("\n");
                }
            });
            return encryptCards;
        }

        StringBuilder decrypt(List<String> cryptCards, Boolean isCardsStr) {
            StringBuilder decryptCards = new StringBuilder();
            cryptCards.forEach(card -> {
                if (isCardsStr) {
                    decryptCards.append(CryptoSystem.decryptString(card, this.keys)).append("\n");
                } else {
                    decryptCards.append(CryptoSystem.decrypt(card, this.keys)).append("\n");
                }
            });
            return decryptCards;
        }


        @Override
        public String toString() {
            return name;
        }
    }

    static class CryptoSystem {
        private static final int numLen = 1024;

        static class Keys {
            BigInteger c, d, p;

            Keys(BigInteger p) throws NoSuchAlgorithmException {
                this.p = p;
                genKeys(p);
            }

            private void genKeys(BigInteger p) throws NoSuchAlgorithmException {
                //Генерация с взаимно простого с p - 1
                this.c = CryptoSystem.getRandBigInteger();
                BigInteger p1 = p.subtract(BigInteger.ONE);
                while (!this.c.gcd(p1).equals(BigInteger.ONE)) {
                    this.c = CryptoSystem.getRandBigInteger();
                }
                //Нахождение d
                this.d = this.c.modInverse(p1);

                //Запись всего этого добра в файл
            }
        }

        //Шифровать
        static BigInteger encryptString(String message, Keys keys) {
            byte[] byteMessage = message.getBytes();
            return new BigInteger(byteMessage).modPow(keys.c, keys.p);
        }

        static BigInteger encrypt(String message, Keys keys) {
            return new BigInteger(message).modPow(keys.c, keys.p);
        }

        //Дешифроавть
        static String decryptString(String message, Keys keys) {
            BigInteger res = CryptoSystem.decrypt(message, keys);
            byte[] byteRes = res.toByteArray();
            return new String(byteRes);
        }

        static BigInteger decrypt(String message, Keys keys) {
            return new BigInteger(message).modPow(keys.d, keys.p);
        }

        BigInteger genPrimeNum() throws NoSuchAlgorithmException {
            System.out.println("Генерация простого большого числа");
            return this.getRandBigInteger().nextProbablePrime();
        }

        static BigInteger getRandBigInteger() throws NoSuchAlgorithmException {
            return new BigInteger(numLen, SecureRandom.getInstance("SHA1PRNG"));
        }
    }

    public static class Cards {
        static String[] getCards() {
            String[] suits = new String[]{"Hearts", "Spades", "Clubs", "Diamonds"};
            String[] digs = new String[]{"Jack", "Queen", "King", "Ace"};
            String[] cards = new String[52];

            int k = 0;
            for (String suit : suits) {
                for (int i = 2; i < 11; i++) {
                    cards[k++] = i + "-" + suit;
                }

                for (String dig : digs) {
                    cards[k++] = dig + "-" + suit;
                }
            }
            return cards;
        }
    }

    static class Transport {
        private static final String pathPrefix = "./Protocols/protocolWork/";

        public static List<String> read(String file) {
            List<String> lines = new ArrayList<>();
            try (Stream<String> stream = Files.lines(Paths.get(pathPrefix + file))) {
                lines = stream.collect(Collectors.toList());
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < lines.size(); i++) {
                if (Objects.equals(lines.get(i), "")) {
                    lines.remove(i--);
                }
            }
            return lines;
        }

        public static void write(String file, String out) {
            try {
                PrintWriter writer = new PrintWriter(pathPrefix + file, "UTF-8");
                writer.println(out);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    Protocol() {

    }

    void init() throws NoSuchAlgorithmException {
        //Первый шаг - генерация простого числа
        BigInteger p = new CryptoSystem().genPrimeNum();

        List<User> users = new ArrayList<>();
        //Создание пользователей и генерация ключей для них
        users.add(new User("Alice", p, true));
        users.add(new User("Bob", p, false));
        users.add(new User("Mark", p, false));
        users.add(new User("Lena", p, false));
        users.add(new User("Masha", p, false));

        this.run(users);
    }

    static int[] getFiveRandIndexes(int n) {
        boolean[] used = new boolean[n];
        for (int i = 0; i < n; i++) {
            used[i] = false;
        }

        int[] indexes = new int[5];
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            int r = random.nextInt(n);
            while (used[r]) {
                r = random.nextInt(n);
            }
            indexes[i] = r;
            used[r] = true;
        }

        return indexes;
    }

    static List<String> getFiveCards(List<String> cards, int[] indexes) {
        List<String> fiveCards = new ArrayList<>();
        for (int index : indexes) {
            fiveCards.add(cards.get(index));
        }
        return fiveCards;
    }

    static List<String> getCardsForNextUser(List<String> cards, int[] indexes) {
        List<String> nextCards = new ArrayList<>();
        for (int i = 0; i < cards.size(); i++) {
            boolean ok = true;
            for (int j = 0; j < indexes.length; j++) {
                if (i == j) {
                    ok = false;
                    break;
                }
            }
            if (ok) {
                nextCards.add(cards.get(i));
            }
        }
        return nextCards;
    }

    StringBuilder listToStringBuilder(List<String> items) {
        StringBuilder str = new StringBuilder();
        items.forEach(item -> {
            str.append(item).append("\n");
        });
        return str;
    }

    void run(List<User> users) {
        User alice = users.get(0);
        User bob = users.get(1);

        //Alice
        System.out.println("Alice 1");
        StringBuilder out = alice.encrypt(Arrays.asList(Cards.getCards()), true);
        Transport.write(alice.name + "_1_step_cards.txt", String.valueOf(out));

        //Other users
        System.out.println("Other 1");
        for (int i = 1; i < users.size(); i++) {
            User user = users.get(i);
            List<String> cards = Transport.read(users.get(i - 1).name + "_1_step_cards.txt");

            int[] randIndexes = getFiveRandIndexes(cards.size());
            List<String> fiveCards = getFiveCards(cards, randIndexes);
            StringBuilder userOut = user.encrypt(fiveCards, false);
            Transport.write(user.name + "_1_step_five_card.txt", String.valueOf(userOut));

            List<String> nextCards = getCardsForNextUser(cards, randIndexes);
            userOut = listToStringBuilder(nextCards);
            Transport.write(user.name + "_1_step_cards.txt", String.valueOf(userOut));

            if (i == users.size() - 1) {
                cards = Transport.read(user.name + "_1_step_cards.txt");

                randIndexes = getFiveRandIndexes(cards.size());
                fiveCards = getFiveCards(cards, randIndexes);
                userOut = listToStringBuilder(fiveCards);
                Transport.write(user.name + "_1_step_five_card_for_alice.txt", String.valueOf(userOut));

                //Maybe don't need
                nextCards = getCardsForNextUser(cards, randIndexes);
                userOut = listToStringBuilder(nextCards);
                Transport.write(user.name + "_1_step_cards.txt", String.valueOf(userOut));
            }
        }

        //Alice do this
        System.out.println("Alice 2");
        for (int i = 0; i < users.size(); i++) {
            if (i == 0) {
                List<String> cards = Transport.read(users.get(users.size() - 1).name + "_1_step_five_card_for_alice.txt");
                StringBuilder userOut = alice.decrypt(cards, true);
                Transport.write(alice.name + "_2_step_five_cards_end.txt", String.valueOf(userOut));
            } else {
                List<String> cards = Transport.read(users.get(i).name + "_1_step_five_card.txt");
                StringBuilder userOut = alice.decrypt(cards, false);
                Transport.write(alice.name + "_2_step_five_cards_for" + users.get(i).name + "_decrypt.txt", String.valueOf(userOut));
            }
        }

        //Other
        System.out.println("Other 2");
        for (int i = 1; i < users.size(); i++) {
            User user = users.get(i);
            List<String> cards = Transport.read(alice.name + "_2_step_five_cards_for" + user.name + "_decrypt.txt");
            StringBuilder userOut = user.decrypt(cards, true);
            Transport.write(user.name + "_2_step_five_cards_end.txt", String.valueOf(userOut));
        }

        System.out.println("End");
    }
}
