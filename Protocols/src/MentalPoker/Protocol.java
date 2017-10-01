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
    class User {
        String name;
        CryptoSystem.Keys keys;

        Boolean isHead;

        User(String name, BigInteger p, Boolean isHead) throws NoSuchAlgorithmException {
            this.isHead = isHead;
            this.name = name;

            this.keys = new CryptoSystem.Keys(p);
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
            BigInteger res = new BigInteger(message).modPow(keys.d, keys.p);
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
            String[] suits = new String[]{"Черви", "Пики", "Крести", "Буби"};
            String[] digs = new String[]{"Валет", "Дама", "Король", "Туз"};
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

        this.run(users);
    }

    void run(List<User> users) {
        User alice = users.get(0);
        User bob = users.get(1);

        //Alice 1 step
        String[] cards = Cards.getCards();

        StringBuilder aliceOut = new StringBuilder();
        for (int i = 0; i < cards.length; i++) {
            //byte
            aliceOut.append(CryptoSystem.encryptString(cards[i], alice.keys)).append("\n");
        }

        Transport.write("alice_1_step.txt", String.valueOf(aliceOut));

        //Bob 2 step
        Random random = new Random();
        List<String> aliceCards = Transport.read("alice_1_step.txt");

        boolean[] usedCards = new boolean[52];
        for (int i = 0; i < usedCards.length; i++) {
            usedCards[i] = false;
        }

        int[] numCardsForAlice = new int[5];
        for (int i = 0; i < 5; i++) {
            int r = random.nextInt(52);
            while (usedCards[r]) {
                r = random.nextInt(52);
            }
            numCardsForAlice[i] = r;
            usedCards[r] = true;
        }

        StringBuilder bobOut = new StringBuilder();
        for (int i = 0; i < numCardsForAlice.length; i++) {
            bobOut.append(aliceCards.get(numCardsForAlice[i])).append("\n");
        }

        Transport.write("bob_2_step_cards_for_alice.txt", String.valueOf(bobOut));

        //Bob 3 step
        int[] numCardsForBob = new int[5];
        for (int i = 0; i < 5; i++) {
            int r = random.nextInt(52);
            while (usedCards[r]) {
                r = random.nextInt(52);
            }
            numCardsForBob[i] = r;
            usedCards[r] = true;
        }

        //Check this encrypt
        bobOut = new StringBuilder();
        for (int i = 0; i < numCardsForBob.length; i++) {
            bobOut.append(CryptoSystem.encrypt(aliceCards.get(numCardsForBob[i]), bob.keys)).append("\n");
        }

        Transport.write("bob_3_step_cards_for_bob.txt", String.valueOf(bobOut));

        //Alice 2 step
        List<String> aliceCardsForGame = Transport.read("bob_2_step_cards_for_alice.txt");

        StringBuilder aliceOut1 = new StringBuilder();
        aliceCardsForGame.forEach(card -> {
            aliceOut1.append(CryptoSystem.decryptString(card, alice.keys)).append("\n");
        });

        Transport.write("alice_2_step_alice_5_card_end.txt", String.valueOf(aliceOut1));

        //Alice 4 step
        List<String> bobCardsForGameEncrypt = Transport.read("bob_3_step_cards_for_bob.txt");

        StringBuilder aliceOut2 = new StringBuilder();
        bobCardsForGameEncrypt.forEach(card -> {
            aliceOut2.append(CryptoSystem.decrypt(card, alice.keys)).append("\n");
        });

        Transport.write("alice_4_step_bob_5_card.txt", String.valueOf(aliceOut2));

        //Bob 4 step
        List<String> bobCardsForGame = Transport.read("bob_3_step_cards_for_bob.txt");

        StringBuilder bobOut1 = new StringBuilder();
        bobCardsForGame.forEach(card -> {
            bobOut1.append(CryptoSystem.decryptString(card, bob.keys)).append("\n");
        });

        Transport.write("bob_4_step_bob_5_card_end.txt", String.valueOf(bobOut1));
    }
}
