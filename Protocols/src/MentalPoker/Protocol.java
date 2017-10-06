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
            this.keys = genKeys(p);
        }

        User(String name) {
            List<String> r = Transport.read("!" + name + "_params.txt");
            this.name = name;

            List<String> r1 = Transport.read("!common_params.txt");

            this.keys = new CryptoSystem.Keys(new BigInteger(r.get(2)),
                    new BigInteger(r.get(4)),
                    new BigInteger(r1.get(1)));
        }

        static CryptoSystem.Keys genKeys(BigInteger p) throws NoSuchAlgorithmException {
            return new CryptoSystem.Keys(p);
        }

        private StringBuilder encrypt(List<String> cards) {
            StringBuilder encryptCards = new StringBuilder();
            cards.forEach(card -> {
                encryptCards.append(CryptoSystem.encrypt(card, this.keys)).append("\n");
            });
            return encryptCards;
        }

        private StringBuilder decrypt(List<String> cryptCards) {
            StringBuilder decryptCards = new StringBuilder();
            cryptCards.forEach(card -> {
                decryptCards.append(CryptoSystem.decrypt(card, this.keys)).append("\n");
            });
            return decryptCards;
        }

        //Выбрать amount рандомных индексов
        private int[] getRandIndexes(int amount, int n) throws Exception {
            if (amount > n) {
                throw new Exception("Карты закончились");
            }

            boolean[] used = new boolean[n];
            for (int i = 0; i < n; i++) {
                used[i] = false;
            }

            int[] indexes = new int[amount];
            Random random = new Random();
            for (int i = 0; i < amount; i++) {
                int r = random.nextInt(n);
                while (used[r]) {
                    r = random.nextInt(n);
                }
                indexes[i] = r;
                used[r] = true;
            }

            return indexes;
        }

        //Выбрать карты по индексам
        private List<String> getCardsByIndexes(List<String> cards, int[] indexes) {
            List<String> fiveCards = new ArrayList<>();
            for (int index : indexes) {
                fiveCards.add(cards.get(index));
            }
            return fiveCards;
        }

        //Новые карты
        private List<String> getNewCards(List<String> cards, int[] indexes) {
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

        private StringBuilder listToStringBuilder(List<String> items) {
            StringBuilder str = new StringBuilder();
            items.forEach(item -> {
                str.append(item).append("\n");
            });
            return str;
        }

        void selectCards(String path, String outPath, int amountCard, boolean withCrypt) throws Exception {
            List<String> cards = Transport.read(path);
            int[] indexes = getRandIndexes(amountCard, cards.size());
            List<String> c = getCardsByIndexes(cards, indexes);

            StringBuilder out;
            if (withCrypt) {
                out = this.encrypt(c);
            } else {
                out = listToStringBuilder(c);
            }
            Transport.write(outPath, String.valueOf(out));

            List<String> newCards = getNewCards(cards, indexes);
            out = listToStringBuilder(newCards);

            Transport.write("cards.txt", String.valueOf(out));
        }

        void encryptCards(String path, String outPath) {
            List<String> cards = Transport.read(path);
            StringBuilder out = this.encrypt(cards);
            Transport.write(outPath, String.valueOf(out));
        }

        void decryptCards(String path, String outPath) {
            List<String> cards = Transport.read(path);
            StringBuilder userOut = this.decrypt(cards);
            Transport.write(outPath, String.valueOf(userOut));
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

            Keys(BigInteger c, BigInteger d, BigInteger p) {
                this.c = c;
                this.d = d;
                this.p = p;
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
                return "c\n" + this.c + "\nd\n" + this.d;
            }
        }

        //Шифровать
        static String encrypt(String message, Keys keys) {
            byte[] byteMessage = message.getBytes();
            BigInteger encrypt = new BigInteger(byteMessage).modPow(keys.c, keys.p);
            byte[] byteRes = encrypt.toByteArray();
            return new String(byteRes);
        }

        //Дешифроавть
        static String decrypt(String message, Keys keys) {
            byte[] byteMessage = message.getBytes();
            BigInteger decrypt = new BigInteger(byteMessage).modPow(keys.d, keys.p);
            byte[] byteRes = decrypt.toByteArray();
            return new String(byteRes);
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

    void init() throws Exception {
        Scanner sc = new Scanner(System.in);

        System.out.println("Введите имя участника:");
        String name = sc.nextLine();

        System.out.println("Что вы хотите сделать?");
        System.out.println("1 - Сгенерировать простое p");
        System.out.println("2 - Сгенерировать параметры системы (c, d)");
        System.out.println("3 - Сгенерировать новую колоду карт");

        System.out.println("4 - Выбрать карты (с/без шифрование)");

        System.out.println("5 - Шифрование карт");
        System.out.println("6 - Дешифрование карт");
        int action = sc.nextInt();

        switch (action) {
            case 1: {
                genPrimal();
                break;
            }
            case 2: {
                genParams(name);
                break;
            }
            case 3: {
                genCards();
                break;
            }
            case 4: {
                User user = new User(name);
                System.out.println("Введите количество выбираемых карт:"); // sc.nextLine(); //??
                int amountCard = sc.nextInt();
                System.out.println("Карты нужно шифровать? (да - 1, нет - 2)"); // sc.nextLine(); //??
                boolean withCrypt = sc.nextInt() == 1;

                System.out.println("Введите имя файла, в котором хранятся карты"); sc.nextLine();
                String path = sc.nextLine();
                System.out.println("Введите имя файла, в котором будет храниться результат действий");
                String outPath = sc.nextLine();

                user.selectCards(path, outPath, amountCard, withCrypt);
                break;
            }
            case 5: {
                System.out.println("Введите имя файла, в котором хранятся карты"); sc.nextLine();
                String path = sc.nextLine();
                System.out.println("Введите имя файла, в котором будет храниться результат действий");
                String outPath = sc.nextLine();

                User user = new User(name);
                user.encryptCards(path, outPath);
                break;
            }
            case 6: {
                System.out.println("Введите имя файла, в котором хранятся карты"); sc.nextLine();
                String path = sc.nextLine();
                System.out.println("Введите имя файла, в котором будет храниться результат действий");
                String outPath = sc.nextLine();

                User user = new User(name);
                user.decryptCards(path, outPath);
                break;
            }
        }
    }

    void genPrimal() throws NoSuchAlgorithmException {
        //Генерация простого p
        BigInteger p = new CryptoSystem().genPrimeNum();

        StringBuilder out = new StringBuilder();
        out.append("p\n").append(p).append('\n');
        Transport.write("!common_params.txt", String.valueOf(out));
    }

    void genParams(String user) {
        List<String> pStr = Transport.read("!common_params.txt");
        BigInteger p = new BigInteger(pStr.get(1));

        StringBuilder outTemp = new StringBuilder();
        try {
            outTemp.append(user).append('\n').append(User.genKeys(p).toString()).append('\n');
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        Transport.write("!" + user + "_params.txt", String.valueOf(outTemp));
    }

    void genCards() {
        Random random = new Random();

        List<String> cards = Arrays.asList(Cards.getCards());
        //Вывод карт
        StringBuilder out = new StringBuilder();
        cards.forEach(card -> {
            out.append(card).append(random.nextInt(10000000)).append('\n');
        });
        Transport.write("!start_cards.txt", String.valueOf(out));
    }
}