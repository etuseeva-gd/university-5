package MentalPoker;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class Protocol {
    List<User> users = new ArrayList<>();

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

        static BigInteger encrypt(String message, Keys keys) {
            return new BigInteger(message).modPow(keys.c, keys.p);
        }

        static BigInteger encrypt(BigInteger message, Keys keys) {
            return message.modPow(keys.c, keys.p);
        }

        static BigInteger decrypt(String message, Keys keys) {
            return new BigInteger(message).modPow(keys.d, keys.p);
        }

        static BigInteger decrypt(BigInteger message, Keys keys) {
            return message.modPow(keys.d, keys.p);
        }

        BigInteger genPrimeNum() throws NoSuchAlgorithmException {
            System.out.println("Генерация простого большого числа");
            return this.getRandBigInteger().nextProbablePrime();
        }

        static BigInteger getRandBigInteger() throws NoSuchAlgorithmException {
            return new BigInteger(numLen, SecureRandom.getInstance("SHA1PRNG"));
        }
    }

    public class Cards {
        String[] suits = new String[]{"Черви", "Пики", "Крести", "Буби"};
        String[] digs = new String[]{"Валет", "Дама", "Король", "Туз"};
        String[] cards = null;

        Cards() {
            createCards();
        }

        void createCards() {
            this.cards = new String[52];
            int k = 0;
            for (String suit : this.suits) {
                for (int i = 2; i < 11; i++) {
                    this.cards[k++] = i + "-" + suit;
                }

                for (String dig : this.digs) {
                    this.cards[k++] = dig + "-" + suit;
                }
            }
        }
    }

    Protocol() {

    }

    void init() throws NoSuchAlgorithmException {
        //Первый шаг - генерация простого числа
        BigInteger p = new CryptoSystem().genPrimeNum();

        //Создание пользователей и генерация ключей для них
        users.add(new User("Alice", p, true));
        users.add(new User("Bob", p, false));

        this.run();
    }

    void run() {

    }
}
