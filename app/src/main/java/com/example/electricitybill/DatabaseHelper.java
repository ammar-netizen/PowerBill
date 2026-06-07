package com.example.electricitybill;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "electricity_bills.db";
    private static final int DATABASE_VERSION = 1;

    // Table name and columns
    public static final String TABLE_BILLS = "bills";
    public static final String COL_ID = "id";
    public static final String COL_MONTH = "month";
    public static final String COL_UNIT = "unit";
    public static final String COL_REBATE = "rebate";
    public static final String COL_TOTAL_CHARGES = "total_charges";
    public static final String COL_FINAL_COST = "final_cost";

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_BILLS + " (" +
            COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_MONTH + " TEXT NOT NULL, " +
            COL_UNIT + " INTEGER NOT NULL, " +
            COL_REBATE + " REAL NOT NULL, " +
            COL_TOTAL_CHARGES + " REAL NOT NULL, " +
            COL_FINAL_COST + " REAL NOT NULL)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BILLS);
        onCreate(db);
    }

    // Insert a new bill record
    public long insertBill(String month, int unit, double rebate, double totalCharges, double finalCost) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_MONTH, month);
        values.put(COL_UNIT, unit);
        values.put(COL_REBATE, rebate);
        values.put(COL_TOTAL_CHARGES, totalCharges);
        values.put(COL_FINAL_COST, finalCost);
        long id = db.insert(TABLE_BILLS, null, values);
        db.close();
        return id;
    }

    // Get all bills (for list view)
    public List<BillModel> getAllBills() {
        List<BillModel> bills = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_BILLS, null, null, null, null, null, COL_ID + " DESC");
        if (cursor.moveToFirst()) {
            do {
                BillModel bill = new BillModel(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_MONTH)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_UNIT)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_REBATE)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_TOTAL_CHARGES)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_FINAL_COST))
                );
                bills.add(bill);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return bills;
    }

    // Get single bill by ID
    public BillModel getBillById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_BILLS, null, COL_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);
        BillModel bill = null;
        if (cursor.moveToFirst()) {
            bill = new BillModel(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_MONTH)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_UNIT)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COL_REBATE)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COL_TOTAL_CHARGES)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COL_FINAL_COST))
            );
        }
        cursor.close();
        db.close();
        return bill;
    }

    // Update a bill record
    public int updateBill(int id, String month, int unit, double rebate, double totalCharges, double finalCost) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_MONTH, month);
        values.put(COL_UNIT, unit);
        values.put(COL_REBATE, rebate);
        values.put(COL_TOTAL_CHARGES, totalCharges);
        values.put(COL_FINAL_COST, finalCost);
        int rows = db.update(TABLE_BILLS, values, COL_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }

    // Delete a bill record
    public void deleteBill(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BILLS, COL_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }
}
