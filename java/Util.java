package whatever;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collector;

import static java.util.Objects.*;
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

    public static String jsonPrint(Object object) {
        return jsonPrint(new ObjectMapper(), object);
    }

    public static String jsonPrint(String jsonString) {
        return jsonPrint(new ObjectMapper(), jsonString);
    }

    private static String jsonPrint(ObjectMapper mapper, String jsonString) {
        try {
            return nonNull(jsonString)
                    ? jsonPrint(mapper, mapper.readValue(jsonString, JsonNode.class))
                    : "";
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String jsonPrint(ObjectMapper mapper, Object object) {
        try {
            return nonNull(object)
                    ? mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object)
                    : "";
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String indent(String orig, int width) {
        String indent = StringUtils.repeat(' ', width);
        return indent + orig;
    }

    public static String[] indentLines(String[] orig, int width) {
        return Arrays.stream(orig).map(o -> indent(o, width)).toArray(String[]::new);
    }

    public static String indentLines(String orig, int width) {
        String[] indentedLines = indentLines(orig.split("\n"), width);
        return String.join("\n", indentedLines);
    }

    public static @NotNull <K,V> Collector<Pair<K, V>, ?, Map<K, V>> toMapOfPairs() {
        return toMap(Pair::getKey, Pair::getValue);
    }

    public static <A, B> boolean equal(A a, B b) {
        return Objects.equals(a, b);
    }

    public static <A, B> boolean notEqual(A a, B b) {
        return ! equal(a, b);
    }
}
