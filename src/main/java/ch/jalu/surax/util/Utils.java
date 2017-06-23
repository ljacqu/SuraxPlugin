package ch.jalu.surax.util;

import java.util.Random;

/**
 * General utilities.
 */
public final class Utils {

    private static Random random = new Random();

    private Utils() {
    }

    public static int randomInt(int boundExclusive) {
        return random.nextInt(boundExclusive);
    }

    public static boolean passWithProbability(int percentage) {
        return random.nextInt(100) < percentage;
    }
}
