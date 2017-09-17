public class GCD {
    public static long gcd(long a, long b) {
        if (b > a) {
            long c = a;
            a = b;
            b = c;
        }
        long result = 0;
        if (a > 0 && b > 0) {
            long r[] = {a, b};
            int i = 1;
            while (true) {
                r[(i + 1) % 2] = r[(i + 1) % 2] % r[i];
                if (r[(i + 1) % 2] == 0) {
                    result = r[i];
                    break;
                } else {
                    i = (i + 1) % 2;
                }
            }
        }
        return result;
    }

    public static long binaryGCD(long a, long b) {
        if (b > a) {
            long c = a;
            a = b;
            b = c;
        }
        long result = 0;
        if (a > 0 && b > 0) {
            result = 1;
            while (a % 2 == 0 && b % 2 == 0) {
                a /= 2;
                b /= 2;
                result *= 2;
            }
            long u = a;
            long v = b;
            while (u != 0) {
                while (u % 2 == 0) {
                    u /= 2;
                }
                while (v % 2 == 0) {
                    v /= 2;
                }
                if (u >= v) {
                    u = u - v;
                } else {
                    v = v - u;
                }
            }
            result = result * v;
        }
        return result;
    }

    public static long[] extendedGCD(long a, long b) {
        boolean rotate = false;
        if (b > a) {
            long c = a;
            a = b;
            b = c;
            rotate = true;
        }
        long result[] = {0, 0, 0};

        if (a > 0 && b > 0) {
            long r[] = {a, b};
            long x[] = {1, 0};
            long y[] = {0, 1};
            int i = 1;
            while (true) {
                long q = r[(i + 1) % 2] / r[i];
                r[(i + 1) % 2] = r[(i + 1) % 2] % r[i];
                if (r[(i + 1) % 2] == 0) {
                    result[0] = r[i];
                    result[1] = x[i];
                    result[2] = y[i];
                    break;
                } else {
                    x[(i + 1) % 2] = x[(i + 1) % 2] - q * x[i];
                    y[(i + 1) % 2] = y[(i + 1) % 2] - q * y[i];
                    i = (i + 1) % 2;
                }
            }
        }

        if (rotate) {
            result[1] += result[2];
            result[2] = result[1] - result[2];
            result[1] -= result[2];
        }
        return result;
    }

    public static long[] polynomialGCD(long[] a, long[] b) {
        long[] result = null;
        if (a.length < b.length) {
            result = polynomialGCD(b, a);
        } else {
            int coeff = 0;
            int i = 0;
            for (int j = 0; j < b.length; j++) {
                if (!(a[j] == 0 && b[j] == 0)) {
                    i = j;
                    break;
                } else
                    coeff++;
            }
            if (i != 0) {
                for (int j = 0; j < a.length - i; j++) {
                    a[j] = a[j + i];
                    a[j + i] = 0;
                }
                for (int j = 0; j < b.length - i; j++) {
                    b[j] = b[j + i];
                    b[j + i] = 0;
                }
            }
            boolean flagOfNull = true;
            while (flagOfNull) {
                int sizeOfA = 0;
                int sizeOfB = 0;
                for (int j = 0; j < a.length; j++) {
                    if (a[j] != 0) {
                        i = j;
                        break;
                    }
                }
                if (i != 0) {
                    for (int j = 0; j < a.length - i; j++) {
                        a[j] = a[j + i];
                        a[j + i] = 0;
                    }
                }
                for (int j = 0; j < a.length; j++) {
                    if (a[j] != 0) {
                        sizeOfA = j;
                    }
                }

                for (int j = 0; j < b.length; j++) {
                    if (b[j] != 0) {
                        i = j;
                        break;
                    }
                }

                if (i != 0) {
                    for (int j = 0; j < b.length - i; j++) {
                        b[j] = b[j + i];
                        b[j + i] = 0;
                    }
                }

                for (int j = 0; j < b.length; j++) {
                    if (b[j] != 0) {
                        sizeOfB = j;
                    }
                }

                long[] c;
                c = new long[Math.max(sizeOfA, sizeOfB) + 1];
                long e = -1;
                boolean reverse = false;
                for (int j = 0; j < c.length; j++) {
                    c[j] = b[0] * (j < a.length ? a[j] : 0) - (j < b.length ? b[j] : 0) * a[0];
                    reverse = c[j] < 0;
                    if (c[j] != 0) {
                        if (e == -1)
                            e = Math.abs(c[j]);
                        else
                            e = GCD.binaryGCD(e, Math.abs(c[j]));
                    }
                }
                for (int j = 0; j < c.length; j++) {
                    if (e != -1)
                        c[j] /= e;
                    if (reverse) {
                        c[j] *= -1;
                    }
                }

                if (sizeOfA >= sizeOfB) {
                    a = c.clone();
                } else
                    b = c.clone();

                flagOfNull = false;
                for (long anA : a) {
                    if (anA != 0) {
                        flagOfNull = true;
                        break;
                    }
                }
            }
            int sizeOfB = 0;
            for (int j = 0; j < b.length; j++) {
                if (b[j] != 0) {
                    sizeOfB = j + 1;
                }
            }
            result = new long[sizeOfB + coeff];
            System.arraycopy(b, 0, result, coeff, sizeOfB);
        }
        return result;
    }
}
