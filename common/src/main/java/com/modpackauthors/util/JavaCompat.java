package com.modpackauthors.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class JavaCompat {
    private JavaCompat() {
    }

    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static <T> List<T> immutableList(Collection<? extends T> values) {
        if (values == null || values.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(new ArrayList<T>(values));
    }

    public static <T> List<T> immutableList(T[] values) {
        if (values == null || values.length == 0) {
            return Collections.emptyList();
        }
        List<T> copy = new ArrayList<T>(values.length);
        Collections.addAll(copy, values);
        return Collections.unmodifiableList(copy);
    }

    public static <K, V> Map<K, V> immutableMap(Map<? extends K, ? extends V> values) {
        if (values == null || values.isEmpty()) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(new LinkedHashMap<K, V>(values));
    }
}
