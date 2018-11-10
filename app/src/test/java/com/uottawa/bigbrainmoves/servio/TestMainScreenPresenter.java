package com.uottawa.bigbrainmoves.servio;

import com.uottawa.bigbrainmoves.servio.models.Account;
import com.uottawa.bigbrainmoves.servio.presenters.MainScreenPresenter;
import com.uottawa.bigbrainmoves.servio.repositories.Repository;
import com.uottawa.bigbrainmoves.servio.util.CurrentAccount;
import com.uottawa.bigbrainmoves.servio.views.MainView;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;


@RunWith(PowerMockRunner.class)
@PrepareForTest(CurrentAccount.class)
public class TestMainScreenPresenter {
    @Mock
    private Repository repository;
    @Mock
    private MainView view;

    @InjectMocks
    private MainScreenPresenter presenter;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test
    public void testSignOut() {
        presenter.signOut();
        verify(view, atLeastOnce()).displaySignOut();
        verify(repository, atLeastOnce()).signOutCurrentUser();
        verify(view, never()).displayWelcomeText(anyString());
    }

    @Test
    public void testDisplayWelcomeText() {
        /*
        To mock a singleton class, first mock static must be called.
        Then a mock of the class must be made, which is to be returned when getInstance() is called.
        For any methods you must mock those too! Example: currentAccount.getAccount().
         */
        mockStatic(CurrentAccount.class);
        CurrentAccount currentAccount = mock(CurrentAccount.class);
        when(CurrentAccount.getInstance())
                .thenReturn(currentAccount);
        when(currentAccount.getCurrentAccount())
                .thenReturn(new Account("Test", "Test", "Test"));

        presenter.showWelcomeMessage();

        verify(view, never()).displaySignOut();
        verify(repository, never()).signOutCurrentUser();
        verify(view, atLeastOnce()).displayWelcomeText(anyString());
    }
}
