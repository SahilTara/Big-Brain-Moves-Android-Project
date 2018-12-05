package com.uottawa.bigbrainmoves.servio;

import com.uottawa.bigbrainmoves.servio.models.Account;
import com.uottawa.bigbrainmoves.servio.util.enums.AccountType;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class TestAccount {

    private Account account;

    @Before
    public void setUp() {
        account = new Account("display", AccountType.ADMIN, "Something");
    }


    @Test
    public void testGetDisplay() {
        assertEquals("Expected display name to be display", "display", account.getDisplayName());
    }

}
