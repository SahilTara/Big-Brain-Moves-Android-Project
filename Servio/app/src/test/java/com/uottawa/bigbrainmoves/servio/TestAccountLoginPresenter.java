package com.uottawa.bigbrainmoves.servio;


import com.uottawa.bigbrainmoves.servio.models.Account;
import com.uottawa.bigbrainmoves.servio.presenters.AccountLoginPresenter;
import com.uottawa.bigbrainmoves.servio.repositories.Repository;
import com.uottawa.bigbrainmoves.servio.views.AccountLoginView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import io.reactivex.Observable;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class TestAccountLoginPresenter {

    @Captor
    private ArgumentCaptor<String> captor;
    @Test
    public void testUserNotAlreadyLoggedIn() {
        Repository repository = mock(Repository.class);
        AccountLoginView view  = mock(AccountLoginView.class);

        when(repository.checkIfUserIsLoggedIn()).thenReturn(false);

        AccountLoginPresenter presenter = new AccountLoginPresenter(view, repository);
        presenter.attemptGettingAccountInfo();

        verify(view, atLeastOnce()).displayNoAccountFound();
        verify(view, never()).displayValidAccount();
        verify(view, never()).displayDataError();
        verifyNoMoreInteractions(view);
    }

    @Test
    public void testUidNoLongerValid() {
        Repository repository = mock(Repository.class);
        AccountLoginView view  = mock(AccountLoginView.class);

        // Account logged in but null uid.
        when(repository.checkIfUserIsLoggedIn()).thenReturn(true);
        when(repository.getUserId()).thenReturn(null);

        AccountLoginPresenter presenter = new AccountLoginPresenter(view, repository);
        presenter.attemptGettingAccountInfo();

        verify(view, atLeastOnce()).displayNoAccountFound();
        verify(view, never()).displayValidAccount();
        verify(view, never()).displayDataError();
        verifyNoMoreInteractions(view);
    }

    @Test
    public void testDatabaseError() {
        Repository repository = mock(Repository.class);
        AccountLoginView view  = mock(AccountLoginView.class);
        final String uid = "XdFahga";

        when(repository.checkIfUserIsLoggedIn()).thenReturn(true);
        when(repository.getUserId()).thenReturn(uid);

        // Database encounters error.
        when(repository.getUserFromDataBase(uid))
                .thenReturn(Observable.create(subscriber -> {
                    subscriber.onError(new Exception("hello"));
                }));

        AccountLoginPresenter presenter = new AccountLoginPresenter(view, repository);

        presenter.attemptGettingAccountInfo();

        verify(repository).getUserFromDataBase(captor.capture());
        verify(view, never()).displayNoAccountFound();
        verify(view, never()).displayValidAccount();
        verify(view, atLeastOnce()).displayDataError();
        verifyNoMoreInteractions(view);

        assertEquals("Expected that getUserFromDatabase had " + uid + " passed to it.",
                captor.getValue(), uid);

    }

    @Test
    public void testAccountNotFound() {
        Repository repository = mock(Repository.class);
        AccountLoginView view  = mock(AccountLoginView.class);
        final String uid = "XdFahga";

        when(repository.checkIfUserIsLoggedIn()).thenReturn(true);
        when(repository.getUserId()).thenReturn(uid);

        // Database returns empty optional element (Account not found).
        when(repository.getUserFromDataBase(uid))
                .thenReturn(Observable.create(subscriber -> {
                    subscriber.onNext(Optional.empty());
                }));

        AccountLoginPresenter presenter = new AccountLoginPresenter(view, repository);

        presenter.attemptGettingAccountInfo();

        verify(repository).getUserFromDataBase(captor.capture());
        verify(view, atLeastOnce()).displayNoAccountFound();
        verify(view, never()).displayValidAccount();
        verify(view, never()).displayDataError();
        verifyNoMoreInteractions(view);

        assertEquals("Expected that getUserFromDatabase had " + uid + " passed to it.",
                captor.getValue(), uid);
    }

    @Test
    public void testAccountFound() {
        Repository repository = mock(Repository.class);
        AccountLoginView view  = mock(AccountLoginView.class);
        final String uid = "XdFahga";

        when(repository.checkIfUserIsLoggedIn()).thenReturn(true);
        when(repository.getUserId()).thenReturn(uid);

        // Database returns proper account object inside optional, (Account found)
        when(repository.getUserFromDataBase(uid))
                .thenReturn(Observable.create(subscriber -> {
                    subscriber.onNext(Optional.of(mock(Account.class)));
                }));

        AccountLoginPresenter presenter = new AccountLoginPresenter(view, repository);

        presenter.attemptGettingAccountInfo();

        verify(repository).getUserFromDataBase(captor.capture());
        verify(view, never()).displayNoAccountFound();
        verify(view, atLeastOnce()).displayValidAccount();
        verify(view, never()).displayDataError();
        verifyNoMoreInteractions(view);

        assertEquals("Expected that getUserFromDatabase had " + uid + " passed to it.",
                captor.getValue(), uid);
    }

}
