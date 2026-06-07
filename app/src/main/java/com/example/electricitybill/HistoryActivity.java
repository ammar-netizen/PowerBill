package com.example.electricitybill;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private ListView listViewBills;
    private View tvEmpty;
    private DatabaseHelper dbHelper;
    private BillAdapter adapter;
    private List<BillModel> bills;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dbHelper = new DatabaseHelper(this);
        listViewBills = findViewById(R.id.listViewBills);
        tvEmpty = findViewById(R.id.tvEmpty);

        loadBills();

        listViewBills.setOnItemClickListener((parent, view, position, id) -> {
            BillModel bill = bills.get(position);
            Intent intent = new Intent(HistoryActivity.this, DetailActivity.class);
            intent.putExtra("bill_id", bill.getId());
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBills();
    }

    private void loadBills() {
        bills = dbHelper.getAllBills();
        if (bills.isEmpty()) {
            listViewBills.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            listViewBills.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
            adapter = new BillAdapter(this, bills);
            listViewBills.setAdapter(adapter);
        }
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
