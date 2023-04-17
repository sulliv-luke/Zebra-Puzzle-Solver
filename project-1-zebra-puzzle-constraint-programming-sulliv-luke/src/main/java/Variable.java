package main.java;

public class Variable {

    String name;
    Domain d;

    /**
     * @brief Variable constructor
     * @param name the name of the variable
     * @param d the domain of the variable
     */
    public Variable(String name, Domain d) {
        this.name = name;
        this.d = new Domain(d);
    }

    /**
     * @brief A utility function that converts the variable into a readable format
     * @return A string of the form "Variable A = {1,2,3,4,5}"
     */
    public String toString() {
        return this.name + "= " + d;
    }

    /**
     * @brief A utility function to get the domain of the variable
     * @return the domain of the variable
     */
    public Domain getDomain() {
        return this.d;
    }

    /**
     * @brief A function to set the domain of the variable
     * @param dom the domain to set the current variable's domain to
     */
    public void setDomain(Domain dom) {
        this.d = dom;
    }

}
