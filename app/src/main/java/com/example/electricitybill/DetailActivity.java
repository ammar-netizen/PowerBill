package com.example.electricitybill;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.slider.Slider;

public class DetailActivity extends AppCompatActivity {

    private TextView tvDetailMonth, tvDetailUnit, tvDetailRebate, tvDetailTotal, tvDetailFinal;
    private Spinner spinnerEditMonth;
    private TextInputLayout tilEditUnit;
    private TextInputEditText etEditUnit;
    private Slider sliderEditRebate;
    private TextView tvEditRebateValue;
    private Button btnUpdate, btnDelete, btnEditToggle;
    private CardView cardView, cardEdit;

    private DatabaseHelper dbHelper;
    private BillModel currentBill;
    private int billId;
    private boolean isEditMode = false;

    private final String[] MONTHS = {"January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dbHelper = new DatabaseHelper(this);
        billId = getIntent().getIntExtra("bill_id", -1);

        initViews();
        loadBillDetails();
    }

    private void initViews() {
        // View mode
        tvDetailMonth = findViewById(R.id.tvDetailMonth);
        tvDetailUnit = findViewById(R.id.tvDetailUnit);
        tvDetailRebate = findViewById(R.id.tvDetailRebate);
        tvDetailTotal = findViewById(R.id.tvDetailTotal);
        tvDetailFinal = findViewById(R.id.tvDetailFinal);
        cardView = findViewById(R.id.cardDetailView);

        // Edit mode
        spinnerEditMonth = findViewById(R.id.spinnerEditMonth);
        tilEditUnit = findViewById(R.id.tilEditUnit);
        etEditUnit = findViewById(R.id.etEditUnit);
        sliderEditRebate = findViewById(R.id.sliderEditRebate);
        tvEditRebateValue = findViewById(R.id.tvEditRebateValue);
        cardEdit = findViewById(R.id.cardEdit);

        // Buttons
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
        btnEditToggle = findViewById(R.id.btnEditToggle);

        // Setup edit month spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, MONTHS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEditMonth.setAdapter(adapter);

        sliderEditRebate.setValueFrom(0f);
        sliderEditRebate.setValueTo(5f);
        sliderEditRebate.setStepSize(0.1f);
        sliderEditRebate.addOnChangeListener((slider, value, fromUser) ->
                tvEditRebateValue.setText(String.format("%.1f%%", value)));

        cardEdit.setVisibility(View.GONE);

        btnEditToggle.setOnClickListener(v -> toggleEditMode());
        btnUpdate.setOnClickListener(v -> updateBill());
        btnDelete.setOnClickListener(v -> confirmDelete());
    }

    private void loadBillDetails() {
        currentBill = dbHelper.getBillById(billId);
        if (currentBill == null) {
            Toast.makeText(this, "Record not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        displayBill();
    }

    private void displayBill() {
        tvDetailMonth.setText(currentBill.getMonth());
        tvDetailUnit.setText(currentBill.getUnit() + " kWh");
        tvDetailRebate.setText(
                String.format("%.1f%%", currentBill.getRebate())
        );
        tvDetailTotal.setText(String.format("RM %.2f", currentBill.getTotalCharges()));
        tvDetailFinal.setText(String.format("RM %.2f", currentBill.getFinalCost()));
    }

    private void toggleEditMode() {
        isEditMode = !isEditMode;
        if (isEditMode) {
            cardEdit.setVisibility(View.VISIBLE);
            btnEditToggle.setText("Cancel Edit");
            // Pre-fill edit fields with current values
            for (int i = 0; i < MONTHS.length; i++) {
                if (MONTHS[i].equals(currentBill.getMonth())) {
                    spinnerEditMonth.setSelection(i);
                    break;
                }
            }
            etEditUnit.setText(String.valueOf(currentBill.getUnit()));
            sliderEditRebate.setValue((float) currentBill.getRebate());
            tvEditRebateValue.setText(
                    String.format("%.1f%%", currentBill.getRebate())
            );
        } else {
            cardEdit.setVisibility(View.GONE);
            btnEditToggle.setText("Edit Record");
        }
    }

    private void updateBill() {
        String unitStr = etEditUnit.getText() != null ? etEditUnit.getText().toString().trim() : "";
        if (unitStr.isEmpty()) {
            tilEditUnit.setError("Please enter the number of units");
            return;
        }

        int unit;
        try {
            unit = Integer.parseInt(unitStr);
        } catch (NumberFormatException e) {
            tilEditUnit.setError("Please enter a valid number");
            return;
        }

        if (unit < 1 || unit > 1000) {
            tilEditUnit.setError("Units must be between 1 and 1000 kWh");
            return;
        }

        tilEditUnit.setError(null);

        String month = spinnerEditMonth.getSelectedItem().toString();
        double rebate = sliderEditRebate.getValue();
        double totalCharges = BillCalculator.calculateTotalCharges(unit);
        double finalCost = BillCalculator.calculateFinalCost(totalCharges, rebate);

        int rows = dbHelper.updateBill(billId, month, unit, rebate, totalCharges, finalCost);
        if (rows > 0) {
            currentBill = dbHelper.getBillById(billId);
            displayBill();
            toggleEditMode();
            Toast.makeText(this, "Record updated successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Update failed. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Record")
                .setMessage("Are you sure you want to delete this bill record for " + currentBill.getMonth() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deleteBill(billId);
                    Toast.makeText(this, "Record deleted.", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
