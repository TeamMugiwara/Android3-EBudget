package edu.ucucite.androidbudget.Model;

public class DataMylist {

    private int amount;
    private String type;
    private String id;


    public DataMylist(int amount, String type, String id) {
        this.amount = amount;
        this.type = type;
        this.id = id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DataMylist(){

    }
}
