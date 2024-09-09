//package whatever;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.Arrays;
import java.util.Optional;

import static java.util.stream.Collectors.*;

public class Util {
    public static @NotNull <T> Optional<T> opt(T t) {
        return Optional.ofNullable(t);
    }

    public static <T> T onNull(T t, T def) {
        return opt(t) .orElse(def);
    }

    public static String prettyPrint(Object object) {
        return prettyPrint(opt(object).map(Object::toString).orElse(null));
    }

    public static String prettyPrint(String toStringVal) {
        return opt(toStringVal).map(s -> s
                .replaceAll("=", " = ")
                .replaceAll("([(\\[]|, ?)", "$1\n")
                .replaceAll("([)\\]])", "\n$1")
        ).orElse(null);
    }

    private static @NotNull String listFiles(File file) {
        String fileList = "";
        if (file.isDirectory()) {
            if (file.canRead()) {
                String subDirList = Arrays.stream(onNull(file.listFiles(), new File[0]))
                        .map(Util::listFiles)
                        .collect(joining("\n"));
                return join(":\n", file.getAbsolutePath(),subDirList);
            } else {
                return file.getAbsolutePath() + ": cannot list";
            }
        } else if (file.isFile()) {
            return file.getAbsolutePath();
        } else {
            return file.getAbsolutePath() + ": cannot list";
    }
}
