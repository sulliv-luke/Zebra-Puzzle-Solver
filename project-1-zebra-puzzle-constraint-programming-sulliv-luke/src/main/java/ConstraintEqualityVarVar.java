package main.java;
import java.util.*;

public class ConstraintEqualityVarVar extends Constraint {
    
    Variable v1, v2;

    /**
     * @brief ConstraintEqualityVarVar constructor
     * @param v1
     * @param v2
     */
    public ConstraintEqualityVarVar(Variable v1, Variable v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    /**
     * @brief A utility function that converts the constraint to a readable string format
     * @return The constraint in the form "Variable A = Variable B"
     */
    public String toString() {
        return v1.name + " = " + v2.name;
    }

    /**
     * @brief A utility function that creates a clone of the constraint
     * @param v1 Variable A
     * @param v2 Variable B
     * @return A clone of this constraint
     */
    public Constraint clone(Variable v1, Variable v2) {
        return (new ConstraintEqualityVarVar(v1, v2));
    }

    /**
     * @brief A utility function to get the Variables in the constraint
     * @return A list containing the two variables in the constraint
     */
    public List<Variable> getVariables() {
        List<Variable> vArr = new ArrayList<Variable>();
        vArr.add(v1);
        vArr.add(v2);
        return vArr;
    }

    /**
     * @brief A function to check if the constraint is satisfied under the current domain of each variable
     * @return true if it is satisfied (if the domain of each variable is valid for the constraint)
     *         false otherwise
     */
    protected boolean isSatisfied() {
        // Solution using Hashmap, worst case time complexity of O(N) and space complexity of O(N)
        HashMap<Integer, Integer> hashMap = new HashMap<>();
        int[] arr1 = v1.getDomain().vals;
        int[] arr2 = v2.getDomain().vals;
        for (int i = 0; i < arr1.length; i++) {
            if (hashMap.containsKey(arr1[i])) {
                hashMap.put(arr1[i],
                            hashMap.get(arr1[i]) + 1);
            }
            else {
                hashMap.put(arr1[i], 1);
            }
        }
 
        for (int i = 0; i < arr2.length; i++) {
            if (hashMap.containsKey(arr2[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * @brief A function that reduces the domain of each variable to satisfy the constraint
     * @return true if either of the domains were reduced
     *         false otherwise
     */
    protected boolean reduce() {
        Domain copy1 = new Domain(v1.getDomain());
        Domain copy2 = new Domain(v2.getDomain());

        ArrayList<Integer> common = new ArrayList<>();

        // Create a Hashtable to store the values of the first array
        HashMap<Integer, Boolean> map = new HashMap<>();

        int[] arr1 = v1.getDomain().vals;
        int[] arr2 = v2.getDomain().vals;

        for (int i : arr1) {
            map.put(i, true);
        }

        // Check for common values in the second array
        for (int i : arr2) {
            if (map.containsKey(i)) {
                common.add(i);
            }
        }

        // Create int array to create new Domain
        int[] commonArr = new int[common.size()];
        for (int i = 0; i < common.size(); i++) {
            commonArr[i] = common.get(i);
        }

        v1.setDomain(new Domain(commonArr));
        v2.setDomain(new Domain(commonArr));

        if (v1.getDomain().equals(copy1) && v2.getDomain().equals(copy2)) {
            return false;
        } else {
            return true;
        }
    }

}
