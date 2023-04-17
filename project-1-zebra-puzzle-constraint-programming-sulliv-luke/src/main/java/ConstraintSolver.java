package main.java;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class ConstraintSolver {

    private Domain dom;
    private List<Variable> variableSet;
    private List<Constraint> constraintSet;
    private List<ConstraintSolver> subproblems;

    /***
     *  @brief Creates the parent ConstraintSolver (the starting problem before any subproblems are created)
     */
    public ConstraintSolver() {
        this.variableSet = new ArrayList<Variable>();
        this.constraintSet = new ArrayList<Constraint>();
        this.subproblems = new ArrayList<ConstraintSolver>();
    }

    /**
     *  @brief Creates a ConstraintSolver based on a parent ConstraintSolver (used to create each subproblem)
     *  @param parent the parent ConstraintSolver
     */
    public ConstraintSolver(ConstraintSolver parent) {
        this.variableSet = new ArrayList<Variable>();
        /** Each variable in this subproblem's variable set needs to be a clone
         * as we do not want to mess with the variables of other subproblems accidentally */
        for (Variable v : parent.variableSet) {
            Variable clone = new Variable(v.name, new Domain(v.getDomain()));
            this.variableSet.add(clone);
        }

        /** Similarly, as each constraint contains references to variables, we need to create
        *  a constraint set consisting of entirely new Constraint objects, which will contain references
        *  to the cloned Variables created above.
        */
        this.constraintSet = new ArrayList<Constraint>();
        for (Constraint c : parent.constraintSet) {
            this.constraintSet.add(c.clone(this.getVar(c.getVariables().get(0).name), this.getVar(c.getVariables().get(1).name)));
        }
        this.subproblems = new ArrayList<ConstraintSolver>();
    }

    /**
     *  @brief A utility function that converts the problem into a readable format
     */
    public String toString() {
        for(int i = 0; i < variableSet.size(); i++)
            System.out.println(variableSet.get(i));
        for (int i = 0; i < constraintSet.size(); i++) {
            System.out.println(constraintSet.get(i));
        }
        return "";
    }

    /**
     * @brief A function to parse the .txt file containing the data for the Zebra Puzzle and create each variable and constraint needed
     * @param fileName
     */
    private void parse(String fileName) {
        try {
            File inputFile = new File(fileName);
            Scanner scanner = new Scanner(inputFile);

            while (scanner.hasNextLine()) {
                String currentLine = scanner.nextLine();

                if(currentLine.startsWith("Domain-")) {
                    //this is our domain - i.e. a datastructure that contains values and can be updated, played with etc.
                    String s = currentLine.replace("Domain-","");
                    String[] array = s.split(","); 
                    int[] vals = new int[array.length];
                    for(int i = 0; i < array.length; i++) {
                        vals[i] = Integer.parseInt(array[i]);
                    }
                    dom = new Domain(vals);
                } else if (currentLine.startsWith("Var-")) {
                    //this is the code for every variable (a name and a domain)
                    String s = currentLine.replace("Var-","");
                    Variable var = new Variable(s, dom); 
                    variableSet.add(var);
                } else if (currentLine.startsWith("Cons-")) {
                    //ConstraintEqualityVarPlusCons:
                    if (currentLine.startsWith("Cons-eqVPC")) {
                        String s = currentLine.replace("Cons-eqVPC", "");
                        String[] values = s.split(" = "); 
                        for (int i = 0; i < values.length; i++) {
                            values[i] = values[i].replaceAll("\\(|\\)", "");
                        }
                        String[] values2 = values[1].split(" \\+ ");
                        String val1Name = values[0];
                        String val2Name = values2[0];
                        Variable v1 = null;
                        Variable v2 = null;
                        if (varOfNameExists(val1Name) && varOfNameExists(val2Name)) {
                            for (Variable element : variableSet) {
                                if (element.name.equals(val1Name)) {
                                    v1 = element;
                                }
                                if(element.name.equals(val2Name)) {
                                    v2 = element; 
                                }
                            }
                        }
                        ConstraintEqualityVarPlusCons eq = new ConstraintEqualityVarPlusCons(v1, v2, Integer.parseInt(values2[1]), false);
                        constraintSet.add(eq);
                    } else if (currentLine.startsWith("Cons-eqVV")) {
                        String s = currentLine.replace("Cons-eqVV", "");
                        String[] values = s.split(" = ");
                        for (int i = 0; i < values.length; i++) {
                            values[i] = values[i].replaceAll("\\(|\\)", "");
                        }
                        String val1Name = values[0];
                        String val2Name = values[1];
                        Variable v1 = null;
                        Variable v2 = null;
                        if (varOfNameExists(val1Name) && varOfNameExists(val2Name)) {
                            for (Variable element : variableSet) {
                                if (element.name.equals(val1Name)) {
                                    v1 = element;
                                }
                                if (element.name.equals(val2Name)){
                                    v2 = element;
                                }
                            }
                        }
                        ConstraintEqualityVarVar eq = new ConstraintEqualityVarVar(v1, v2);
                        constraintSet.add(eq);
                    } else if (currentLine.startsWith("Cons-eqVC")) {
                        String s = currentLine.replace("Cons-eqVC", "");
                        String[] values = s.split(" = ");
                        for (int i = 0; i < values.length; i++) {
                            values[i] = values[i].replaceAll("\\(|\\)", "");
                        }
                        String val1Name = values[0];
                        String val2Name = values[1];
                        Variable v1 = null;
                        int c = 0;
                        if (varOfNameExists(val1Name)) {
                            for (Variable element : variableSet) {
                                if (element.name.equals(val1Name)) {
                                    v1 = element;
                                }
                            }
                        }
                        c = Integer.parseInt(val2Name);
                        ConstraintEqualityVarCons eq = new ConstraintEqualityVarCons(v1, c);
                        constraintSet.add(eq);
                    } else if (currentLine.startsWith("Cons-abs")) {
                        String s = currentLine.replace("Cons-abs", "");
                        String[] values = s.split(" = ");
                        for (int i = 0; i < values.length; i++) {
                            values[i] = values[i].replaceAll("\\(|\\)", "");
                        }
                        String[] values2 = values[0].split(" - ");
                        String val1Name = values2[0];
                        String val2Name = values2[1];
                        Variable v1 = null;
                        Variable v2 = null;
                        int c = 0;
                        if (varOfNameExists(val1Name) && varOfNameExists(val2Name)) {
                            for (Variable element : variableSet) {
                                if (element.name.equals(val1Name)) {
                                    v1 = element;
                                }
                                if (element.name.equals(val2Name)) {
                                    v2 = element;
                                }
                            }
                        }
                        c = Integer.parseInt(values[1]);
                        ConstraintEqualityVarPlusCons eq = new ConstraintEqualityVarPlusCons(v1, v2, c, true);
                        constraintSet.add(eq);

                    } else if (currentLine.startsWith("Cons-diff")) {
                        String s = currentLine.replace("Cons-diff", "");
                        String[] values = s.split(",");
                        for (int i = 0; i < values.length; i++) {
                            values[i] = values[i].replaceAll("\\(|\\)", "");
                        }
                        String val1Name = values[0];
                        String val2Name = values[1];
                        Variable v1 = null;
                        Variable v2 = null;
                        if (varOfNameExists(val1Name) && varOfNameExists(val2Name)) {
                            for (Variable element : variableSet) {
                                if (element.name.equals(val1Name)) {
                                    v1 = element;
                                }
                                if (element.name.equals(val2Name)) {
                                    v2 = element;
                                }
                            }
                        }
                        ConstraintDifferenceVarVar eq = new ConstraintDifferenceVarVar(v1, v2);
                        constraintSet.add(eq);
                    }
                } 

            }

            scanner.close();
            
        } catch (FileNotFoundException e) {
            System.out.println("Error.");
            e.printStackTrace();
        }
    }

    /**
     * @brief A utility function to check if there is a variable in the variable set of a given name
     * @param target The name of the variable we are checking
     * @return  true if there is a variable in the variable set with this name
     *          false otherwise
     */
    private boolean varOfNameExists(String target) {
        for (Variable v : this.variableSet) {
            if (v.name.equals(target)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @brief   A utility function to get a variable with a given name
     * @param target The name of the variable we are getting
     * @return  The variable object if the variable is in the variable set
     *          null otherwise
     */
    private Variable getVar(String target) {
        for (Variable v : this.variableSet) {
            if (v.name.equals(target)) {
                return v;
            }
        }
        return null;
    }

    /**
     * @brief   A utility function to check whether a domain was made empty due to reduction
     * @return  true if there is a variable in the variable set with an empty domain
     *          false otherwise
     */
    private boolean emptyDomainFound() {
        for (Variable v : this.variableSet) {
            if (v.getDomain().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * @brief   A utility function that checks if every constraint is satisfied under the current variable domains
     * @return  true if every constraint is satisfied
     *          false otherwise
     */
    private boolean legal() {
        for (Constraint c : this.constraintSet) {
            if (!c.isSatisfied()) {
                return false;
            }
        } 
        return true;
    }

    /**
     * @brief   A utility function to check whether we have reached a solution or not
     *          (this does not take into account constraint satisfaction, @see ConstraintSolver#legal())
     * @return  true if there is a variable in the variable set with an empty domain
     *          false otherwise
     */
    private boolean hasSolution() {
        for (Variable v : this.variableSet) {
            if (v.getDomain().getSize() != 1) {
                return false;
            }
        }
        return legal();
    }

    /**
     * @brief   A utility function to recursively count the number of subproblems created due to domain splitting
     * @return  the number of subproblems created
     */
    private int countSubproblems() {
        int total = 0;
        for (int i = 0; i < this.subproblems.size(); i++) {
            //count recursively
            total += ((ConstraintSolver) this.subproblems.get(i)).countSubproblems();
        }
        if (total == 0) total = 1;
        return total;
    }

    /**
     * @brief   A utility function to recursively count the number of solutions encountered 
     *          throughout the entire tree of subproblems
     * @return  The number of solutions created
     */
    private int countSolutions() {
        int total = 0;
        for (int i = 0; i < this.subproblems.size(); i++) {
            total += ((ConstraintSolver) this.subproblems.get(i)).countSolutions();
        }
        if (this.hasSolution()) {
            System.out.println(this);
            return 1;
        }
        return total;
    }

    private ConstraintSolver getSolution() {
        if (this.hasSolution()) {
            return this;
        }
        ConstraintSolver solution = null;
        boolean solutionFound = false;
        for (int i = 0; i < this.subproblems.size(); i++) {
            if (!solutionFound) {
                solution = ((ConstraintSolver) this.subproblems.get(i).getSolution());
                if (solution != null) {
                    solutionFound = true;
                }
            }
        }
        return solution;
    }



    public ArrayList<String> printAnswer(String fileName) {
        ArrayList<String> answer = new ArrayList<String>();
        this.parse(fileName);
        this.reduceCycle();
        this.tree();
        ConstraintSolver solution = this.getSolution();
        for (int i = 0; i < solution.variableSet.size(); i++) {
            Variable var = solution.variableSet.get(i);
            String pref = "Sol-";
            pref += var.name;
            pref += "-";
            pref += var.getDomain().vals[0];
            answer.add(pref);

        }
        return answer;
    }

    /**
     * @brief   A function that iterates through each variable in the variable set, reducing the domain
     */
    private void reduceCycle() {
        boolean reduced = true;
        while (reduced && !this.emptyDomainFound()) {
            reduced = false; // Have to reset the boolean 
            for (Constraint c : this.constraintSet) {
                if (c.isSatisfied()) {
                    if (c.reduce()) {
                        reduced = true;
                    }
                }
                
            }
        }
}

    /**
     * @brief   A function that checks which variables need to be split, creates a new subproblem (a new ConstraintSolver object),
     *          splits the variable in this subproblem with the smallest domain, and runs @see ConstraintSolver#reduceCycle()
     *          
     *          (I.E. recursively creates the tree of subproblems until we find a solution)
     */
    public void tree() {
        List<Variable> needsToSplit = new ArrayList<Variable>();
        for (Variable v : this.variableSet) {
            if (v.getDomain().getSize() > 1) {
                needsToSplit.add(v);
            }
        }
        if (needsToSplit.size() > 0) {
            Variable toBeSplit = needsToSplit.get(0);
            
            for (int i = 1; i < needsToSplit.size(); i++) {
                // When you use largest domain each time, you need to generate 4570 subproblems to get a solution
                // When you use smallest domain each time, you only need to generate 8 subproblems to get a solution
                if (toBeSplit.getDomain().getSize() > needsToSplit.get(i).getDomain().getSize()) {
                    toBeSplit =  needsToSplit.get(i);
                }
            }

            ConstraintSolver subPr = new ConstraintSolver(this);
            ConstraintSolver subPrTwo = new ConstraintSolver(this);

            subPr.getVar(toBeSplit.name).setDomain(toBeSplit.getDomain().split()[0]);
            subPrTwo.getVar(toBeSplit.name).setDomain(toBeSplit.getDomain().split()[1]);

            this.subproblems.add(subPr);
            this.subproblems.add(subPrTwo);
            for (ConstraintSolver c : this.subproblems) {
                if (c.legal() && !c.hasSolution() && !c.emptyDomainFound()) {
                    c.reduceCycle();
                    c.tree();
                }
            }
        }
    }

    // The main
    public static void main(String[] args) {
        ConstraintSolver problem = new ConstraintSolver();
        ArrayList<String> answer = problem.printAnswer("data.txt");
        for (String str : answer) {
            System.out.println(str);
        }
        //problem.parse("data.txt");
        //problem.reduceCycle();
        //problem.tree();
        //System.out.println("Subproblems Made: " + problem.countSubproblems());
        //System.out.println("Solutions Found: " + problem.countSolutions());
    }

}
