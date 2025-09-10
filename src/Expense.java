import java.util.Map;

public class Expense {
    String description;
    double amount;
    int paidById; // who paid
    Map<Integer, Double> splitMap; // userId -> how much they owe

    public Expense(String description, double amount, int paidById, Map<Integer, Double> splitMap) {
        this.description = description;
        this.amount = amount;
        this.paidById = paidById;
        this.splitMap = splitMap;
    }

    @Override
    public String toString() {
        return description + " : ₹" + amount + " (paid by User " + paidById + ")";
    }

    public int getPaidById() {
        return paidById;
    }

    public double getAmount() {
        return amount;
    }

}
