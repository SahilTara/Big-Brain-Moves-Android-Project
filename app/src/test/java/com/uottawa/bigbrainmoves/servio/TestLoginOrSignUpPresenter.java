package com.uottawa.bigbrainmoves.servio;

import com.uottawa.bigbrainmoves.servio.presenters.LoginOrSignUpPresenter;
import com.uottawa.bigbrainmoves.servio.repositories.Repository;
import com.uottawa.bigbrainmoves.servio.views.LoginOrSignUpView;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;

import io.reactivex.Observable;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TestLoginOrSignUpPresenter {
    @Mock
    private Repository repository;
    @Mock
    private LoginOrSignUpView view;

    @InjectMocks
    private LoginOrSignUpPresenter presenter;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();



    private final String user = "Test";
    private final String password = "Test";

    @Captor
    private  ArgumentCaptor<String> userCaptor;

    @Captor
    private ArgumentCaptor<String> passwordCaptor;

    @Test
    public void testBlankUser() {
        presenter.login("", password);

        verify(view, atLeastOnce()).displayInvalidUser();
        verify(view, never()).displayInvalidPassword();
        verify(view, never()).displayDataError();
        verify(view, never()).displayInvalidLogin();
        verify(view, never()).displayValidLogin();
    }

    @Test
    public void testBlankPassword() {
        presenter.login(user, "");

        verify(view, never()).displayInvalidUser();
        verify(view, atLeastOnce()).displayInvalidPassword();
        verify(view, never()).displayDataError();
        verify(view, never()).displayInvalidLogin();
        verify(view, never()).displayValidLogin();
    }

    @Test
    public void testDatabaseError() {
        when(repository.login(any(String.class), any(String.class))).thenReturn(Observable.create(subscriber -> {
            subscriber.onError(new Exception("Test"));
        }));

        presenter.login(user, password);

        verify(repository).login(userCaptor.capture(), passwordCaptor.capture());

        verify(view, never()).displayInvalidUser();
        verify(view, never()).displayInvalidPassword();
        verify(view, atLeastOnce()).displayDataError();
        verify(view, never()).displayInvalidLogin();
        verify(view, never()).displayValidLogin();

        assertEquals("user passed to the database isn't the passed username.",
                user, userCaptor.getValue());
        assertEquals("password passed to the database isn't the passed password.",
                password, passwordCaptor.getValue());
    }

    @Test
    public void testInvalidCredentials() {
        when(repository.login(any(String.class), any(String.class)))
                .thenReturn(Observable.create(subscriber -> {
                    subscriber.onNext(false);
                    subscriber.onComplete();
                }));

        presenter.login(user, password);

        verify(repository).login(userCaptor.capture(), passwordCaptor.capture());

        verify(view, never()).displayInvalidUser();
        verify(view, never()).displayInvalidPassword();
        verify(view, never()).displayDataError();
        verify(view, atLeastOnce()).displayInvalidLogin();
        verify(view, never()).displayValidLogin();

        assertEquals("user passed to the database isn't the passed username.",
                user, userCaptor.getValue());
        assertEquals("password passed to the database isn't the passed password.",
                password, passwordCaptor.getValue());
    }

    @Test
    public void testValidCredentials() {
        when(repository.login(any(String.class), any(String.class)))
                .thenReturn(Observable.create(subscriber -> {
                    subscriber.onNext(true);
                    subscriber.onComplete();
                }));

        presenter.login(user, password);

        verify(repository).login(userCaptor.capture(), passwordCaptor.capture());

        verify(view, never()).displayInvalidUser();
        verify(view, never()).displayInvalidPassword();
        verify(view, never()).displayDataError();
        verify(view, never()).displayInvalidLogin();
        verify(view, atLeastOnce()).displayValidLogin();

        assertEquals("user passed to the database isn't the passed username.",
                user, userCaptor.getValue());
        assertEquals("password passed to the database isn't the passed password.",
                password, passwordCaptor.getValue());
    }
}
