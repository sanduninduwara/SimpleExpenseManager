package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.test.suitebuilder.annotation.MediumTest;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static com.google.common.truth.Truth.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;

@RunWith(AndroidJUnit4.class)
@MediumTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DbAccountDAOTest {
    private DbAccountDAO sqLiteDBHelper;

    @Before
    public void setup() {
        sqLiteDBHelper = new DbAccountDAO(ApplicationProvider.getApplicationContext());
    }

    @After
    public void teardown() {
        sqLiteDBHelper.close();
    }

    @Test
    public void addAccount() {
        Account accout=new Account("100","BOC","sandun",1000);
        boolean result = sqLiteDBHelper.addAccount(accout);
        assertThat(result).isTrue();
    }

    @Test
    public void getAccount() throws InvalidAccountException {
        Account account = sqLiteDBHelper.getAccount("100");

        boolean result = false;
        if (account != null) {
            result = account.getAccountHolderName().equals("sandun") && account.getBankName().equals("BOC") && account.getBalance() == 1000;
        }
        assertThat(result).isTrue();
    }


}
