package com.aichuangyi.commons.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author Mr.Min
 * @description 断言工具类
 * @date 2019-05-29
 **/
public abstract class Assert {

    public Assert() {
    }

    public static void check(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void check(boolean expression, String message, Object... args) {
        if (!expression) {
            throw new IllegalArgumentException(String.format(message, args));
        }
    }

    public static void check(boolean expression, String message, Object arg) {
        if (!expression) {
            throw new IllegalArgumentException(String.format(message, arg));
        }
    }

    public static void check(boolean expression, Supplier<String> messageSupplier) {
        if (!expression) {
            throw new IllegalArgumentException(nullSafeGet(messageSupplier));
        }
    }

    public static <T> T notNull(T argument, String name) {
        if (argument == null) {
            throw new IllegalArgumentException(name + " may not be null");
        } else {
            return argument;
        }
    }

    public static <T extends CharSequence> T notEmpty(T argument, String name) {
        if (argument == null) {
            throw new IllegalArgumentException(name + " may not be null");
        } else if (StringUtils.isEmpty(argument)) {
            throw new IllegalArgumentException(name + " may not be empty");
        } else {
            return argument;
        }
    }

    public static <T extends CharSequence> T notBlank(T argument, String name) {
        if (argument == null) {
            throw new IllegalArgumentException(name + " may not be null");
        } else if (StringUtils.isBlank(argument)) {
            throw new IllegalArgumentException(name + " may not be blank");
        } else {
            return argument;
        }
    }

    public static <T extends CharSequence> T containsNoBlanks(T argument, String name) {
        if (argument == null) {
            throw new IllegalArgumentException(name + " may not be null");
        } else if (argument.length() == 0) {
            throw new IllegalArgumentException(name + " may not be empty");
        } else if (StringUtils.containsWhitespace(argument)) {
            throw new IllegalArgumentException(name + " may not contain blanks");
        } else {
            return argument;
        }
    }

    public static <E, T extends Collection<E>> T notEmpty(T argument, String name) {
        if (argument == null) {
            throw new IllegalArgumentException(name + " may not be null");
        } else if (argument.isEmpty()) {
            throw new IllegalArgumentException(name + " may not be empty");
        } else {
            return argument;
        }
    }

    public static <K, V, T extends Map<K, V>> T notEmpty(T argument, String name) {
        if (argument == null) {
            throw new IllegalArgumentException(name + " may not be null");
        } else if (argument.isEmpty()) {
            throw new IllegalArgumentException(name + " may not be empty");
        } else {
            return argument;
        }
    }

    public static int positive(int n, String name) {
        if (n <= 0) {
            throw new IllegalArgumentException(name + " may not be negative or zero");
        } else {
            return n;
        }
    }

    public static long positive(long n, String name) {
        if (n <= 0L) {
            throw new IllegalArgumentException(name + " may not be negative or zero");
        } else {
            return n;
        }
    }

    public static int notNegative(int n, String name) {
        if (n < 0) {
            throw new IllegalArgumentException(name + " may not be negative");
        } else {
            return n;
        }
    }

    public static long notNegative(long n, String name) {
        if (n < 0L) {
            throw new IllegalArgumentException(name + " may not be negative");
        } else {
            return n;
        }
    }

    private static String nullSafeGet(Supplier<String> messageSupplier) {
        return messageSupplier != null ? messageSupplier.get() : null;
    }
}
