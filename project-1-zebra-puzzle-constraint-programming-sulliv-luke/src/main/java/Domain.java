package main.java;

import java.util.Arrays;

public class Domain {

    int[] vals;

    /**
     * @brief Domain constructor based on an int array
     * @param vals the values of the domain
     */
    public Domain(int[] vals) {
        this.vals = vals;
    }

    /**
     * @brief Domain constructor based on an already existing domain
     * @param d2 the domain to base the new domain off of
     */
    public Domain(Domain d2) {
        vals = new int[d2.vals.length];
        for(int i = 0; i < vals.length; i++)
            this.vals[i] = d2.vals[i];
    }

    /**
     * @brief A utility function that converts the domain into a readable format
     * @return The domain in the form {1,2,3,4,5}
     */
    public String toString() {
        String result  = "{";
        for (int i = 0; i < vals.length; i++)
            result += vals[i];
        result += "}";
        return result;
    }

    /** A function that splits the Domain in half
     * @return An array of Domains containing both halves of the split domain
     */
    public Domain[] split() {
        Domain[] splitDomain = new Domain[2];
        int[] valsLower = Arrays.copyOfRange(vals, 0, vals.length/2);
        int[] valsUpper = Arrays.copyOfRange(vals, vals.length/2, vals.length);
        splitDomain[0] = new Domain(valsLower);
        splitDomain[1] = new Domain(valsUpper);
        return splitDomain;
    }

    /** A function that checks if the Domain is empty
     * @return  true if the Domain's vals array is empty
     *          false otherwise
     */
    public boolean isEmpty() {
        if (this.vals.length == 0) {
            return true;
        }
        return false;
    }

    /** A function that checks if the Domain equals a given Domain
     * @param d2 A given Domain
     * @return true if the Domains are equal, false otherwise
     */
    public boolean equals(Domain d2) {
        if (d2.vals.length != this.vals.length) {
            return false;
        }
        for (int i = 0; i < this.vals.length; i++) {
            if (d2.vals[i] != this.vals[i]) {
                return false;
            }
        }
        return true;
    }

    /** A function that checks if the Domain only contains one value
     * @return true if there is only one value in vals
     *         false otherwise
     */
    public boolean  isReducedToOnlyOneValue() {
       if (this.vals.length == 1) {
            return true;
       }
       return false;
    }

    /** A function that gets the size of the Domain
     * @return An int value representing the size of the Domain
     */
    public int getSize() {
        return this.vals.length;
    }



}
