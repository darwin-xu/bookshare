package com.bookshare.utility;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class RandomFile {

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

    public static Path genFilePath(Path root, String suffix) {

        Path path;

        do {
            path = genFilePathHelper(root, suffix);
        } while (path.toFile().exists());

        return path;
    }

}
