package com.example.electricitybill;

public class BillCalculator {

    // Block rates in RM
    private static final double RATE_BLOCK1 = 0.218; // 1-200 kWh
    private static final double RATE_BLOCK2 = 0.334; // 201-300 kWh
    private static final double RATE_BLOCK3 = 0.516; // 301-600 kWh
    private static final double RATE_BLOCK4 = 0.546; // 601-1000 kWh

    public static double calculateTotalCharges(int unit) {
        double total = 0.0;

        if (unit <= 0) return 0.0;

        if (unit <= 200) {
            total = unit * RATE_BLOCK1;
        } else if (unit <= 300) {
            total = 200 * RATE_BLOCK1;
            total += (unit - 200) * RATE_BLOCK2;
        } else if (unit <= 600) {
            total = 200 * RATE_BLOCK1;
            total += 100 * RATE_BLOCK2;
            total += (unit - 300) * RATE_BLOCK3;
        } else {
            total = 200 * RATE_BLOCK1;
            total += 100 * RATE_BLOCK2;
            total += 300 * RATE_BLOCK3;
            total += (unit - 600) * RATE_BLOCK4;
        }

        return Math.round(total * 100.0) / 100.0;
    }

    public static double calculateFinalCost(double totalCharges, double rebatePercent) {
        double rebateAmount = totalCharges * (rebatePercent / 100.0);
        double finalCost = totalCharges - rebateAmount;
        return Math.round(finalCost * 100.0) / 100.0;
    }
}
