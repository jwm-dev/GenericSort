package src;
import java.util.Comparator;
// SOURCE: Jon Bentley, Programming Pearls pg. 116; 3rd implementation of insertion sort
// https://www.google.com/books/edition/Programming_Pearls/kse_7qbWbjsC?hl=en&gbpv=1&pg=PA116&printsec=frontcover
public class GenericSort {
    // Generic insertion sort using Comparable
    public static <T extends Comparable<? super T>> void isort3(T[] x) {
        for (int i = 1; i < x.length; i++) {
            T t = x[i];
            int j = i;
            while (j > 0 && x[j - 1].compareTo(t) > 0) {
                x[j] = x[j - 1];
                j--;
            }
            x[j] = t;
        }
    }

    // Overload for custom Comparator
    public static <T> void isort3(T[] x, Comparator<? super T> cmp) {
        for (int i = 1; i < x.length; i++) {
            T t = x[i];
            int j = i;
            while (j > 0 && cmp.compare(x[j - 1], t) > 0) {
                x[j] = x[j - 1];
                j--;
            }
            x[j] = t;
        }
    }
}