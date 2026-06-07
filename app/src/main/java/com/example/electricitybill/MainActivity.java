package com.example.electricitybill;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {

    private Spinner spinnerMonth;
    private TextInputLayout tilUnit;
    private TextInputEditText etUnit;
    private Slider sliderRebate;
    private TextView tvRebateValue;
    private Button btnCalculate, btnClear;
    private CardView cardResult;
    private TextView tvTotalCharges, tvFinalCost, tvResultMonth, tvResultUnit, tvResultRebate;

    private DatabaseHelper dbHelper;
    private double currentTotalCharges = 0.0;
    private double currentFinalCost = 0.0;
    private boolean isCalculated = false;

    private final String[] MONTHS = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHelper = new DatabaseHelper(this);

        initViews();
        setupListeners();
    }

    private void initViews() {
        spinnerMonth = findViewById(R.id.spinnerMonth);
        tilUnit = findViewById(R.id.tilUnit);
        etUnit = findViewById(R.id.etUnit);
        sliderRebate = findViewById(R.id.sliderRebate);
        tvRebateValue = findViewById(R.id.tvRebateValue);
        btnCalculate = findViewById(R.id.btnCalculate);
        btnClear = findViewById(R.id.btnClear);
        cardResult = findViewById(R.id.cardResult);
        tvTotalCharges = findViewById(R.id.tvTotalCharges);
        tvFinalCost = findViewById(R.id.tvFinalCost);
        tvResultMonth = findViewById(R.id.tvResultMonth);
        tvResultUnit = findViewById(R.id.tvResultUnit);
        tvResultRebate = findViewById(R.id.tvResultRebate);

        // Month Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                MONTHS
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(adapter);

        // Rebate Slider
        sliderRebate.setValueFrom(0f);
        sliderRebate.setValueTo(5f);
        sliderRebate.setStepSize(0.1f); // Allow 0.1 increments
        sliderRebate.setValue(0f);

        tvRebateValue.setText("0.0%");

        cardResult.setVisibility(View.GONE);
    }

    private void setupListeners() {

        sliderRebate.addOnChangeListener((slider, value, fromUser) -> {

            tvRebateValue.setText(
                    String.format("%.1f%%", value)
            );

            if (isCalculated) {
                recalculateFinalCost(value);
            }
        });

        btnCalculate.setOnClickListener(v -> calculateBill());

        btnClear.setOnClickListener(v -> clearForm());
    }

    private void calculateBill() {

        String unitStr = etUnit.getText() != null
                ? etUnit.getText().toString().trim()
                : "";

        if (unitStr.isEmpty()) {
            tilUnit.setError("Please enter the number of units used");
            return;
        }

        int unit;

        try {
            unit = Integer.parseInt(unitStr);
        } catch (NumberFormatException e) {
            tilUnit.setError("Please enter a valid number");
            return;
        }

        if (unit < 1 || unit > 1000) {
            tilUnit.setError("Units must be between 1 and 1000 kWh");
            return;
        }

        tilUnit.setError(null);

        double rebatePercent = sliderRebate.getValue();

        String selectedMonth = spinnerMonth.getSelectedItem().toString();

        currentTotalCharges =
                BillCalculator.calculateTotalCharges(unit);

        currentFinalCost =
                BillCalculator.calculateFinalCost(
                        currentTotalCharges,
                        rebatePercent
                );

        isCalculated = true;

        tvResultMonth.setText("Month: " + selectedMonth);
        tvResultUnit.setText("Units Used: " + unit + " kWh");
        tvResultRebate.setText(
                String.format("Rebate Applied: %.1f%%", rebatePercent)
        );

        tvTotalCharges.setText(
                String.format("RM %.2f", currentTotalCharges)
        );

        tvFinalCost.setText(
                String.format("RM %.2f", currentFinalCost)
        );

        cardResult.setVisibility(View.VISIBLE);

        long result = dbHelper.insertBill(
                selectedMonth,
                unit,
                rebatePercent,
                currentTotalCharges,
                currentFinalCost
        );

        if (result != -1) {
            Toast.makeText(
                    this,
                    "Bill calculated and saved!",
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            Toast.makeText(
                    this,
                    "Calculation done but failed to save.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void recalculateFinalCost(double rebatePercent) {

        currentFinalCost =
                BillCalculator.calculateFinalCost(
                        currentTotalCharges,
                        rebatePercent
                );

        tvFinalCost.setText(
                String.format("RM %.2f", currentFinalCost)
        );

        tvResultRebate.setText(
                String.format("Rebate Applied: %.1f%%", rebatePercent)
        );
    }

    private void clearForm() {

        spinnerMonth.setSelection(0);

        etUnit.setText("");

        sliderRebate.setValue(0f);

        tvRebateValue.setText("0.0%");

        tilUnit.setError(null);

        cardResult.setVisibility(View.GONE);

        isCalculated = false;
        currentTotalCharges = 0.0;
        currentFinalCost = 0.0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menu_history) {

            startActivity(
                    new Intent(this, HistoryActivity.class)
            );

            return true;

        } else if (id == R.id.menu_about) {

            startActivity(
                    new Intent(this, AboutActivity.class)
            );

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}