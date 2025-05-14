package shared;

import java.util.HashMap;
import java.util.Map;

public class EvaluationCache {
    private static final Map<String, CachedResult> cache = new HashMap<>();

    public static boolean contains(String levelString) {
        return cache.containsKey(levelString);
    }

    public static CachedResult get(String levelString) {
        return cache.get(levelString);
    }

    public static void put(String levelString, CachedResult result) {
        cache.put(levelString, result);
    }
}