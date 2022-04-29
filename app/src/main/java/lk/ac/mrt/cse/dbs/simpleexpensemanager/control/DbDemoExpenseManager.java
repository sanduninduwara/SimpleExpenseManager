package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;


import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;

public class DbDemoExpenseManager extends ExpenseManager{

    private TransactionDAO dbTransactionDAO;
    private AccountDAO dbAccountDAO;



    public DbDemoExpenseManager(TransactionDAO dbTransactionDAO, AccountDAO dbAccountDAO) {
        this.dbTransactionDAO = dbTransactionDAO;
        this.dbAccountDAO = dbAccountDAO;
        setup();
    }

    @Override
    public void setup() {
        /*** Begin generating dummy data for In-Memory implementation ***/


        setTransactionsDAO(dbTransactionDAO);

        setAccountsDAO(dbAccountDAO);

        // dummy data
//        Account dummyAcct1 = new Account("12345A", "Yoda Bank", "Anakin Skywalker", 10000.0);
//        Account dummyAcct2 = new Account("78945Z", "Clone BC", "Obi-Wan Kenobi", 80000.0);
//        getAccountsDAO().addAccount(dummyAcct1);
//        getAccountsDAO().addAccount(dummyAcct2);

        /*** End ***/
    }
}

