package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class DbAccountDAO extends SQLiteOpenHelper implements AccountDAO {
    public static final String ACCOUNTS = "Accounts";
    public static final String ACCOUNT_NO = "accountNo";
    public static final String BANK_NAME = "bankName";
    public static final String ACCOUNT_HOLDER_NAME = "accountHolderName";
    public static final String BALANCE = "balance";

    private final Map<String, Account> accounts;

    public DbAccountDAO(Context context) {
        super(context, "AccountData.db", null, 1);
        this.accounts = new HashMap<>();

        //get existing accounts from database
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + ACCOUNTS, null);


        if (cursor.moveToFirst()) {
            do {
                String accountNo = cursor.getString(0);
                String bankName = cursor.getString(1);
                String accountHolderName = cursor.getString(2);
                double balance = cursor.getDouble(3);
                accounts.put(accountNo, new Account(accountNo, bankName, accountHolderName, balance));
            }while (cursor.moveToNext());


        }
        cursor.close();
        db.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + ACCOUNTS + "(" + ACCOUNT_NO + " INTEGER, " + BANK_NAME + " TEXT, " + ACCOUNT_HOLDER_NAME + " TEXT, " + BALANCE + " REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int old, int newv) {
//        db.execSQL("DROP TABLE IF EXISTS " + ACCOUNTS);
    }

    @Override
    public List<String> getAccountNumbersList() {

        return new ArrayList<>(accounts.keySet());
    }

    @Override
    public List<Account> getAccountsList() {

        return new ArrayList<>(accounts.values());
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {

        if (accounts.containsKey(accountNo)) {
            return accounts.get(accountNo);
        }
        String msg = "Account " + accountNo + " is invalid.";
        throw new InvalidAccountException(msg);
    }

    @Override
    public void addAccount(Account account)  {

        accounts.put(account.getAccountNo(), account);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ACCOUNT_NO, account.getAccountNo());
        cv.put(BANK_NAME, account.getBankName());
        cv.put(ACCOUNT_HOLDER_NAME, account.getAccountHolderName());
        cv.put(BALANCE, account.getBalance());

        db.insert(ACCOUNTS, null, cv);
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        if (!accounts.containsKey(accountNo)) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        accounts.remove(accountNo);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + ACCOUNTS + " WHERE " + ACCOUNT_NO + "=?", new String[] {accountNo});
        if (cursor.getCount()>0) {
            db.delete(ACCOUNTS, ACCOUNT_NO + "=?", new String[] {accountNo});
        }
        cursor.close();
        db.close();
        throw new InvalidAccountException("Account not found!");
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + ACCOUNTS + " WHERE " + ACCOUNT_NO + "=?", new String[] {accountNo});

        if (expenseType==ExpenseType.EXPENSE) {
            amount=-amount;
        }
        if (accounts.containsKey(accountNo) && cursor.getCount()>0) {
            Account account = accounts.get(accountNo);
            account.setBalance(account.getBalance() + amount);
            accounts.put(accountNo, account);

            cursor.moveToFirst();
            double newAmount = cursor.getDouble(3)+amount;
            ContentValues contentValues = new ContentValues();
            contentValues.put(ACCOUNT_NO, cursor.getString(0));
            contentValues.put(BANK_NAME, cursor.getString(1));
            contentValues.put(ACCOUNT_HOLDER_NAME, cursor.getString(2));
            contentValues.put(BALANCE, newAmount);

            db.update(ACCOUNTS, contentValues, ACCOUNT_NO + "=?", new String[] {accountNo});
        }else if (accounts.containsKey(accountNo)) {
            accounts.remove(accountNo);
        }
        cursor.close();
        db.close();

        if (!accounts.containsKey(accountNo)) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
    }
}