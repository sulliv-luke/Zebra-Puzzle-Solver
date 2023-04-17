package main.java;
import java.util.*;
public abstract class Constraint {

    public abstract String toString();
    public abstract List<Variable> getVariables();
    public abstract Constraint clone(Variable v1, Variable v2);
    protected abstract boolean isSatisfied();
    protected abstract boolean reduce();

    /**
     * @brief Binary Search
     * @param arr the array we are searching in
     * @param target the value we are looking for
     * @return true if value is present, false otherwise
     */
    public static boolean binarySearch(int[] arr, int target) {
        int left = 0;
        int right = arr.length - 1;

        while (left <= right) {
            int mid = (left + right) / 2;
            if (arr[mid] == target) {
                return true;
            } else if (arr[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return false;
    }

}
