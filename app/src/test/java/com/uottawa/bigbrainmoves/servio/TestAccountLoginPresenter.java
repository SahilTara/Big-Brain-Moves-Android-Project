package com.uottawa.bigbrainmoves.servio;


import com.uottawa.bigbrainmoves.servio.models.Account;
import com.uottawa.bigbrainmoves.servio.presenters.AccountLoginPresenter;
import com.uottawa.bigbrainmoves.servio.repositories.Repository;
import com.uottawa.bigbrainmoves.servio.views.AccountLoginView;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;

import java.util.Optional;

import io.reactivex.Observable;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class TestAccountLoginPresenter {
    @ClassRule
    public static final RxJavaSchedulerRule schedulers = new RxJavaSchedulerRule();

    @Mock
    private Repository repository;
    @Mock
    private AccountLoginView view;

    private AccountLoginPresenter presenter;


    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Captor
    private ArgumentCaptor<String> captor;

    private final String uid = "XdFahga";

    @Before
    public void setup() {
         presenter = new AccountLoginPresenter(view, repository);
    }
    @Test
    public void testUserNotAlreadyLoggedIn() {
        // account not logged in
        when(repository.checkIfUserIsLoggedIn()).thenReturn(false);

        presenter.attemptGettingAccountInfo();

        verify(view, atLeastOnce()).displayNoAccountFound();
        verify(view, never()).displayValidAccount();
        verify(view, never()).displayDataError();
        verifyNoMoreInteractions(view);
    }

    @Test
    public void testUidNoLongerValid() {
        // Account logged in but null uid.
        when(repository.checkIfUserIsLoggedIn()).thenReturn(true);
        when(repository.getUserId()).thenReturn(null);

        presenter.attemptGettingAccountInfo();

        verify(view, atLeastOnce()).displayNoAccountFound();
        verify(view, never()).displayValidAccount();
        verify(view, never()).displayDataError();
        verifyNoMoreInteractions(view);
    }

    @Test
    public void testDatabaseError() {
        when(repository.checkIfUserIsLoggedIn()).thenReturn(true);
        when(repository.getUserId()).thenReturn(uid);

        // Database encounters error.
        when(repository.getUserFromDataBase(any(String.class)))
                .thenReturn(Observable.create(subscriber -> {
                    subscriber.onError(new Exception("hello"));
                }));

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
        when(repository.checkIfUserIsLoggedIn()).thenReturn(true);
        when(repository.getUserId()).thenReturn(uid);

        // Database returns empty optional element (Account not found).
        when(repository.getUserFromDataBase(any(String.class)))
                .thenReturn(Observable.create(subscriber -> {
                    subscriber.onNext(Optional.empty());
                    subscriber.onComplete();
                }));

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
        when(repository.checkIfUserIsLoggedIn()).thenReturn(true);
        when(repository.getUserId()).thenReturn(uid);

        // Database returns proper account object inside optional, (Account found)
        when(repository.getUserFromDataBase(any(String.class)))
                .thenReturn(Observable.create(subscriber -> {
                    subscriber.onNext(Optional.of(mock(Account.class)));
                    subscriber.onComplete();
                }));

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
