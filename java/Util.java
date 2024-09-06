//package whatever;

import javax.validation.constraints.NotNull;
import java.util.Optional;

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
}
