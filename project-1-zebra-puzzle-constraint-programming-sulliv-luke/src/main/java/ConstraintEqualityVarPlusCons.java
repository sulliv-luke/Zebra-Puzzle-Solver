package main.java;
import java.util.*;

public class ConstraintEqualityVarPlusCons extends Constraint{

    Variable v1, v2;
    int constant;
    boolean isAbs;

    /**
     * @brief ConstraintEqualityVarPlusCons constructor
     * @param v1 Variable A
     * @param v2 Variable B
     * @param constant The constant (1, etc)
     * @param isAbs is the constant an absolute value? (|Ivory - Green| = 1)
     */
    public ConstraintEqualityVarPlusCons(Variable v1, Variable v2, int constant, boolean isAbs) {
        this.v1 = v1;
        this.v2 = v2;
        this.constant = constant;
        this.isAbs = isAbs;
    }

    /**
     * @brief A utility function that converts the constraint to a readable string format
     * @return The constraint in the form "Variable A = Variable B + Constant"
     */
    public String toString() {
        return v1.name + " = " + v2.name + " + " + constant;
    }

    /**
     * @brief A utility function that creates a clone of the constraint
     * @param v1 Variable A
     * @param v2 Variable B
     * @return A clone of this constraint (Note we do not pass in the constant again as we do not need to create a clone of an int)
     */
    public Constraint clone(Variable v1, Variable v2) {
        return (new ConstraintEqualityVarPlusCons(v1, v2, this.constant, this.isAbs));
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
        // we need to find a some value 'a' in the domain of v1 such that there is a 'b' in domain of v2 where a = b + constant
        int[] arr1 = v1.getDomain().vals;
        int[] arr2 = v2.getDomain().vals;
        for (int i = 0; i < arr2.length; i++) {
            int b = arr2[i];
            int a = b + constant;
            if (binarySearch(arr1, a)) {
                return true;
            }
            if (this.isAbs) {
                a = b - constant;
                if (binarySearch(arr1, a)) {
                    return true;
                }
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

        int[] arr1 = v1.getDomain().vals;
        int[] arr2 = v2.getDomain().vals;

        Set<Integer> reducedDomainAlpha = new HashSet<>();
        Set<Integer> reducedDomainBeta = new HashSet<>();

        for (int b : arr2) {
            int a = b + constant;
            if (binarySearch(arr1, a)) {
                reducedDomainAlpha.add(a);
                reducedDomainBeta.add(b);
            }
            if (this.isAbs) {
                a = b - constant;
                if(binarySearch(arr1, a)) {
                    reducedDomainAlpha.add(a);
                    reducedDomainBeta.add(b);
                }
            }
        }

        // Convert the hashsets to int[] using streams
        v1.setDomain(new Domain(reducedDomainAlpha.stream().mapToInt(Number::intValue).toArray()));
        v2.setDomain(new Domain(reducedDomainBeta.stream().mapToInt(Number::intValue).toArray()));

        if (v1.getDomain().equals(copy1) && v2.getDomain().equals(copy2)) {
            return false;
        } else {
            return true;
        }
    }
    
}
