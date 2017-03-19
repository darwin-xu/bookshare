package com.bookshare.utility;

import java.nio.file.Path;
import java.util.Random;
import java.util.UUID;

/**
 * Created by Darwin on 1/1/2017.
 */
public class RandomUtil {

    private static Path genFilePathHelper(Path root, String suffix) {
        UUID uuid = UUID.randomUUID();
        String[] randomUUIDStrings = uuid.toString().split("-");

        Path path = root;
        for (int i = 0; i < randomUUIDStrings.length; ++i) {
            if (i != randomUUIDStrings.length - 1)
                path = path.resolve(randomUUIDStrings[i].substring(0, 2));
            else
                path = path.resolve(randomUUIDStrings[i] + suffix);
        }

        return path;
    }

    public static Path genRandomFilePath(Path root, String suffix) {

        Path path;

        do {
            path = genFilePathHelper(root, suffix);
        } while (path.toFile().exists());

        return path;
    }

    public static String genDigitals(int n) {
        String d = "";
        Random r = new Random();
        for (int i = 0; i < n; ++i) {
            d += r.nextInt(10);
        }
        return d;
    }

}
