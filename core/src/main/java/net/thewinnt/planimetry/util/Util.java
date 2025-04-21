package net.thewinnt.planimetry.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
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

    public static <T> void shuffle(List<T> list, Random random) {
        for (int i = 0; i < list.size(); i++) {
            int index2 = random.nextInt(i);
            T object1 = list.get(i);
            list.set(i, list.get(index2));
            list.set(index2, object1);
        }
    }

    public static <T> void shuffle(T[] list, Random random) {
        for (int i = 0; i < list.length; i++) {
            int index2 = random.nextInt(i);
            T object1 = list[i];
            list[i] = list[index2];
            list[index2] = object1;
        }
    }

    public static <T> T randomFrom(Collection<T> collection, Random random) {
        int index = random.nextInt(collection.size());
        Iterator<T> iterator = collection.iterator();
        for (int i = 0; i < index; i++) {
            iterator.next();
        }
        return iterator.next();
    }
}
