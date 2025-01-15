package net.thewinnt.planimetry.util;

import java.util.function.Consumer;

/** Contains some utility methods */
public class Util {
    /**
     * Performs an operation on an object, and returns the resulting object.
     * @param input The object to modify
     * @param operation The operation to perform
     * @return The object after the operation
     */
    public static <T> T make(T input, Consumer<T> operation) {
        operation.accept(input);
        return input;
    }
}
