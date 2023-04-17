package main.java;
import java.util.*;

public class ConstraintDifferenceVarVar extends Constraint{

    Variable v1, v2;

    /**
     * @brief ConstraintDifferenceVarVar Constructor
     * @param v1 Variable A
     * @param v2 Variable B
     */
    public ConstraintDifferenceVarVar(Variable v1, Variable v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    /**
     * @brief A utility function that converts the constraint to a readable string format
     * @return The constraint in the form "Variable A != Variable B"
     */
    public String toString() {
        return v1 + " != " + v2;
    }

    /**
     * @brief A utility function that creates a clone of the constraint
     * @param v1 Variable A
     * @param v2 Variable B
     * @return A clone of this constraint
     */
    public Constraint clone(Variable v1, Variable v2) {
        return (new ConstraintDifferenceVarVar(v1, v2));
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
        if ((v1.getDomain().isReducedToOnlyOneValue() && v2.getDomain().isReducedToOnlyOneValue()) &&
        v1.getDomain().equals(v2.getDomain())) {
            return false;
        }
        return true;
    }

    /**
     * @brief A function that reduces the domain of each variable to satisfy the constraint
     * @return true if either of the domains were reduced
     *         false otherwise
     */
    protected boolean reduce() {
        if (v2.getDomain().isReducedToOnlyOneValue()) {
            Domain copy = new Domain(v1.getDomain());
            int b = v2.getDomain().vals[0];
            if (binarySearch(v1.getDomain().vals, b)) {
                Set<Integer> reducedDomainAlpha = new HashSet<>();
                for(int i : v1.getDomain().vals) {
                    if (i != b) {
                        reducedDomainAlpha.add(i);
                    }
                }
                v1.setDomain(new Domain(reducedDomainAlpha.stream().mapToInt(Number::intValue).toArray()));
                if (v1.getDomain().equals(copy)) {
                    // was not reduced at all
                    return false;
                } else {
                    return true;
                }
            }
        }

        
            if (v1.getDomain().isReducedToOnlyOneValue()) {
            Domain copy = new Domain(v2.getDomain());
            int a = v1.getDomain().vals[0];
            if (binarySearch(v2.getDomain().vals, a)) {
                Set<Integer> reducedDomainBeta = new HashSet<>();
                for(int i : v2.getDomain().vals) {
                    if (i != a) {
                        reducedDomainBeta.add(i);
                    }
                }
                v2.setDomain(new Domain(reducedDomainBeta.stream().mapToInt(Number::intValue).toArray()));
                if (v2.getDomain().equals(copy)) {
                    // was not reduced at all
                    return false;
                } else {
                    return true;
                }
            }
        }


        return false;
    }
    
}
