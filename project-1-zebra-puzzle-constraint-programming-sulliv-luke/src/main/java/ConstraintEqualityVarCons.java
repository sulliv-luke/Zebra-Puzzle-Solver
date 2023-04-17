package main.java;
import java.util.*;

public class ConstraintEqualityVarCons extends Constraint{

    Variable v1;
    int constant;

    /**
     * @brief ConstraintEqualityVarCons constructor
     * @param v1 Variable A
     * @param constant The constant (1, etc)
     */
    public ConstraintEqualityVarCons(Variable v1, int constant) {
        this.v1 = v1;
        this.constant = constant;
    }

    /**
     * @brief A utility function that converts the constraint to a readable string format
     * @return The constraint in the form "Variable A = Constant"
     */
    public String toString() {
        return v1.name + " = " + constant;
    }

    /**
     * @brief A utility function that creates a clone of the constraint
     * @param v1 Variable A
     * @return A clone of this constraint (Note we do not pass in the constant again as we do not need to create a clone of an int)
     */
    public Constraint clone(Variable v1, Variable v2) {
        return (new ConstraintEqualityVarCons(v1, this.constant));
    }

    /**
     * @brief A utility function to get the Variables in the constraint
     * @return A list containing the two variables in the constraint
     */
    public List<Variable> getVariables() {
        List<Variable> vArr = new ArrayList<Variable>();
        vArr.add(v1);
        int[] dummyArr = {};
        vArr.add(new Variable("dummy", new Domain(dummyArr)));
        return vArr;
    }

    /**
     * @brief A function to check if the constraint is satisfied under the current domain of each variable
     * @return true if it is satisfied (if the domain of each variable is valid for the constraint)
     *         false otherwise
     */
    protected boolean isSatisfied() {
    // The domain should be sorted anyway, so we can use binary search as it has a worst case time complexity of O(logN)
        return binarySearch(v1.getDomain().vals, constant);
    }

    /**
     * @brief A function that reduces the domain of each variable to satisfy the constraint
     * @return true if either of the domains were reduced
     *         false otherwise
     */
    protected boolean reduce() {
        // if constant is in the domain (ie it is satisfied), then we set the domain of v1 to an array just containing constant
        Domain copy = new Domain(v1.getDomain());
        int[] constantArr = {constant};
        v1.setDomain(new Domain(constantArr));
        if (v1.getDomain().equals(copy)) {
            return false;
        }
        return true;
    
}
}
