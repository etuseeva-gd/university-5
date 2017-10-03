package MentalPoker;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Protocol {
    static class User {
        String name;
        CryptoSystem.Keys keys;

        User(String name, BigInteger p) throws NoSuchAlgorithmException {
            this.name = name;
            this.keys = new CryptoSystem.Keys(p);
        }

        private StringBuilder encrypt(List<String> cards, Boolean isCardsStr) {
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

        private StringBuilder decrypt(List<String> cryptCards, Boolean isCardsStr) {
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

        private int[] getFiveRandIndexes(int n) {
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

        private List<String> getFiveCards(List<String> cards, int[] indexes) {
            List<String> fiveCards = new ArrayList<>();
            for (int index : indexes) {
                fiveCards.add(cards.get(index));
            }
            return fiveCards;
        }

        private List<String> getCardsForNextUser() {
            List<String> nextCards = new ArrayList<>();
            for (int i = 0; i < this.cards.size(); i++) {
                boolean ok = true;
                for (int j = 0; j < this.indexes.length; j++) {
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

        private StringBuilder listToStringBuilder(List<String> items) {
            StringBuilder str = new StringBuilder();
            items.forEach(item -> {
                str.append(item).append("\n");
            });
            return str;
        }

        List<String> cards = null;
        int[] indexes = null;

        void selectionOfFiveCards(String path, String outPath) {
            this.cards = Transport.read(path, true);
            this.indexes = getFiveRandIndexes(this.cards.size());
            List<String> fiveCards = getFiveCards(this.cards, this.indexes);
            StringBuilder out = this.encrypt(fiveCards, false);
            Transport.write(this.name + "-" + outPath, String.valueOf(out));
        }

        void selectionOfFiveCardsForHead(String path, String outPath) {
            List<String> cards = Transport.read(path, true);
            int[] randIndexes = getFiveRandIndexes(cards.size());
            List<String> fiveCards = getFiveCards(cards, randIndexes);
            StringBuilder out = this.listToStringBuilder(fiveCards);
            Transport.write(this.name + "-" + outPath, String.valueOf(out));
        }

        void sendCardsForNextUser(String outPath) {
            List<String> cards = getCardsForNextUser();
            Collections.shuffle(cards);
            StringBuilder out = listToStringBuilder(cards);
            Transport.write(this.name + "-" + outPath, String.valueOf(out));

            this.cards = null;
            this.indexes = null;
        }

        void decryptCards(String path, String outPath) {
            List<String> cards = Transport.read(path, true);
            StringBuilder userOut = this.decrypt(cards, true);
            Transport.write(this.name + "-" + outPath, String.valueOf(userOut));
        }

        //For head
        void sendCardsForNextUserHead(String outPath) {
            List<String> cards = Arrays.asList(Cards.getCards());
            Collections.shuffle(cards);
            StringBuilder out = this.encrypt(cards, true);
            Transport.write(this.name + "-" + outPath, String.valueOf(out));
        }

        void decryptCardsHead(String path, String outPath) {
            List<String> cards = Transport.read(path, true);
            StringBuilder userOut = this.decrypt(cards, false);
            Transport.write(this.name + "-" + outPath, String.valueOf(userOut));
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
            }

            @Override
            public String toString() {
                return "c = " + this.c + "\nd = " + this.d + " ";
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

        public static List<String> read(String file, boolean withPrefix) {
            String prefix = withPrefix ? pathPrefix : "";

            List<String> lines = new ArrayList<>();
            try (Stream<String> stream = Files.lines(Paths.get(prefix + file))) {
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

    void init() throws NoSuchAlgorithmException {
        BigInteger p = new CryptoSystem().genPrimeNum();

        List<User> users = new ArrayList<>();
        List<String> usersName = Transport.read("input.txt", false);
        usersName.forEach(name -> {
            try {
                users.add(new User(name, p));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        });

        StringBuilder out = new StringBuilder();
        out.append("p = ").append(p).append('\n');
        users.forEach(user -> {
            out.append(user.name).append('\n').append(user.keys.toString()).append('\n');
        });
        Transport.write("!user_params.txt", String.valueOf(out));

        if (users.size() > 1 && users.size() < 6) {
            this.run(users);
        } else {
            System.out.println("У вас введено не корректное число участников.");
        }
    }

    void continueExecution(Scanner scanner) {
        //System.out.println("Продолжить? (enter)");
        //scanner.nextLine();
    }

    void run(List<User> users) {
        String CARDS = "cards.txt", FIVE_CARDS = "five_cards.txt", FIVE_CARDS_HEAD = "five_cards_head.txt", FIVE_CARDS_END = "five_cards_end.txt";

        Scanner scanner = new Scanner(System.in);

        System.out.println("---------------------");
        System.out.println("Раздача началась");

        User headUser = users.get(0);

        //Head
        System.out.println("Организатор игры взял колоду, зашифровал, передал");
        headUser.sendCardsForNextUserHead(CARDS);

        continueExecution(scanner);

        //Other users
        System.out.println("Участники выбирают себе карты (5 шт), шифруют их, и отправляют организатору");
        for (int i = 1; i < users.size(); i++) {
            User user = users.get(i);

            user.selectionOfFiveCards(users.get(i - 1).name + "-" + CARDS, FIVE_CARDS);
            user.sendCardsForNextUser(CARDS);

            continueExecution(scanner);

            if (i == users.size() - 1) {
                System.out.println("Последний участник выбирает карты организатору (5 шт)");

                user.selectionOfFiveCardsForHead(user.name + "-" + CARDS, FIVE_CARDS_HEAD);
                continueExecution(scanner);
            }
        }

        //Head do this
        System.out.println("Организатор дешифрует полученные карты и отправляет участникам");
        for (int i = 0; i < users.size(); i++) {
            if (i == 0) {
                System.out.println("Организатор дешифрует свои карты");
                headUser.decryptCards(users.get(users.size() - 1).name + "-" + FIVE_CARDS_HEAD, FIVE_CARDS_END);
            } else {
                headUser.decryptCardsHead(users.get(i).name + "-" + FIVE_CARDS, "for-" + users.get(i).name + "-" + FIVE_CARDS);
            }
            continueExecution(scanner);
        }

        //Other
        System.out.println("Участники дешифруют свои карты");
        for (int i = 1; i < users.size(); i++) {
            User user = users.get(i);
            user.decryptCards(headUser.name + "-for-" + users.get(i).name + "-" + FIVE_CARDS, FIVE_CARDS_END);
            continueExecution(scanner);
        }

        System.out.println("Конец раздачи");
    }
}