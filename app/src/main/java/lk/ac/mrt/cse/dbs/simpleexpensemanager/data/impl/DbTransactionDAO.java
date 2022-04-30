package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class DbTransactionDAO extends SQLiteOpenHelper implements TransactionDAO {
    public static final String TRANSACTIONS = "Transactions";
    public static final String DATE = "date";
    public static final String ACCOUNT_NO = "accountNo";
    public static final String EXPENSE_TYPE = "expenseType";
    public static final String AMOUNT = "amount";
    private final List<Transaction> transactions;

    public DbTransactionDAO(Context context) {
        super(context, "TransactionData.db", null, 1);
        transactions = new LinkedList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TRANSACTIONS, null);
        int rows = cursor.getCount();

        if (cursor.moveToFirst()) {
            do {
                String dateString = cursor.getString(0);
                String accountNo = cursor.getString(1);
                String expenseTypeString = cursor.getString(2);
                double amount = cursor.getDouble(3);

                try {
                    Date date = new SimpleDateFormat("dd/MM/yyyy").parse(dateString);
                    ExpenseType expenseType = ExpenseType.valueOf(expenseTypeString);
                    transactions.add(new Transaction(date, accountNo, expenseType, amount));
                } catch (Exception e) {
                    Log.e(e.toString(),"error!!");
                    e.printStackTrace();
                }
            }while (cursor.moveToNext());

        }
        cursor.close();
        db.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TRANSACTIONS + "(" + DATE + " TEXT, " + ACCOUNT_NO + " TEXT, " + EXPENSE_TYPE + " TEXT, " + AMOUNT + " REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int old, int newv) {

    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Transaction transaction = new Transaction(date, accountNo, expenseType, amount);
        transactions.add(transaction);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DATE, dateFormat.format(date));
        cv.put(ACCOUNT_NO, accountNo);
        cv.put(EXPENSE_TYPE, expenseType.name());
        cv.put(AMOUNT, amount);
        db.insert(TRANSACTIONS, null, cv);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        int size = transactions.size();
        if (size <= limit) {
            return transactions;
        }

        return transactions.subList(size - limit, size);
    }
}