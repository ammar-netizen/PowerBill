package com.example.electricitybill;

public class BillModel {
    private int id;
    private String month;
    private int unit;
    private double rebate;
    private double totalCharges;
    private double finalCost;

    public BillModel(int id, String month, int unit, double rebate, double totalCharges, double finalCost) {
        this.id = id;
        this.month = month;
        this.unit = unit;
        this.rebate = rebate;
        this.totalCharges = totalCharges;
        this.finalCost = finalCost;
    }

    public int getId() { return id; }
    public String getMonth() { return month; }
    public int getUnit() { return unit; }
    public double getRebate() { return rebate; }
    public double getTotalCharges() { return totalCharges; }
    public double getFinalCost() { return finalCost; }

    public void setId(int id) { this.id = id; }
    public void setMonth(String month) { this.month = month; }
    public void setUnit(int unit) { this.unit = unit; }
    public void setRebate(double rebate) { this.rebate = rebate; }
    public void setTotalCharges(double totalCharges) { this.totalCharges = totalCharges; }
    public void setFinalCost(double finalCost) { this.finalCost = finalCost; }
}
