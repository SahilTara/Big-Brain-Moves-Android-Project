package com.uottawa.bigbrainmoves.servio;

import com.uottawa.bigbrainmoves.servio.presenters.SignupPresenter;
import com.uottawa.bigbrainmoves.servio.repositories.Repository;
import com.uottawa.bigbrainmoves.servio.util.SignupResult;
import com.uottawa.bigbrainmoves.servio.views.SignUpView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;

import io.reactivex.Observable;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TestSignUpPresenter {

    @Mock
    private SignUpView view;
    @Mock
    private Repository repository;


    private SignupPresenter presenter;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private static final String EMAIL = "sahil@hotmail.com";
    private static final String INVALID_EMAIL = "sahil123@hotmail";

    private static final String USER = "supert6593";
    private static final String INVALID_USER = "supert65 93";

    private static final String PASSWORD = "password123";
    private static final String INVALID_PASSWORD = "pass";

    private static final String DISPLAY_NAME = "Test Ing";
    private static final String INVALID_DISPLAY_NAME = "TESTING4832";

    private static final String TYPE = "home";
    private static final String INVALID_TYPE = "";

    @Before
    public void setup() {
        presenter = new SignupPresenter(view, repository);
    }



    @Test
    public void testInvalidEmail() {
        presenter.createAccount(INVALID_EMAIL, USER, PASSWORD, DISPLAY_NAME, TYPE);

        verify(view, atLeastOnce()).displayInvalidEmail();
        verify(view, never()).displaySignUpSuccess();
        verify(view, never()).displayUserNameTaken();
        verify(view, never()).displayEmailTaken();
    }

    @Test
    public void testInvalidUser() {
        presenter.createAccount(EMAIL, INVALID_USER, PASSWORD, DISPLAY_NAME, TYPE);

        verify(view, never()).displayInvalidEmail();
        verify(view, atLeastOnce()).displayInvalidUserName();
        verify(view, never()).displaySignUpSuccess();
        verify(view, never()).displayUserNameTaken();
        verify(view, never()).displayEmailTaken();
    }

    @Test
    public void testInvalidPassword() {
        presenter.createAccount(EMAIL, USER, INVALID_PASSWORD, DISPLAY_NAME, TYPE);

        verify(view, never()).displayInvalidEmail();
        verify(view, never()).displayInvalidUserName();
        verify(view, atLeastOnce()).displayInvalidPassword();
        verify(view, never()).displaySignUpSuccess();
        verify(view, never()).displayUserNameTaken();
        verify(view, never()).displayEmailTaken();
    }

    @Test
    public void testInvalidDisplayName() {
        presenter.createAccount(EMAIL, USER, PASSWORD, INVALID_DISPLAY_NAME, TYPE);

        verify(view, never()).displayInvalidEmail();
        verify(view, never()).displayInvalidUserName();
        verify(view, never()).displayInvalidPassword();
        verify(view, atLeastOnce()).displayInvalidDisplayName();
        verify(view, never()).displaySignUpSuccess();
        verify(view, never()).displayUserNameTaken();
        verify(view, never()).displayEmailTaken();
    }

    @Test
    public void testInvalidType() {
        presenter.createAccount(EMAIL, USER, PASSWORD, DISPLAY_NAME, INVALID_TYPE);

        verify(view, never()).displayInvalidEmail();
        verify(view, never()).displayInvalidUserName();
        verify(view, never()).displayInvalidPassword();
        verify(view, never()).displayInvalidDisplayName();
        verify(view, atLeastOnce()).displayInvalidType();
        verify(view, never()).displaySignUpSuccess();
        verify(view, never()).displayUserNameTaken();
        verify(view, never()).displayEmailTaken();
    }

    @Test
    public void testDatabaseError() {
        when(repository.createUserIfNotInDataBase(anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString())).thenReturn(Observable.create(subscriber -> {
            subscriber.onError(new Exception("Test"));
        }));
        presenter.createAccount(EMAIL, USER, PASSWORD, DISPLAY_NAME, TYPE);

        verify(view, never()).displayInvalidEmail();
        verify(view, never()).displayInvalidUserName();
        verify(view, never()).displayInvalidPassword();
        verify(view, never()).displayInvalidDisplayName();
        verify(view, never()).displayInvalidType();
        verify(view, atLeastOnce()).displayDataError();
        verify(view, never()).displaySignUpSuccess();
        verify(view, never()).displayUserNameTaken();
        verify(view, never()).displayEmailTaken();
    }

    @Test
    public void testUserNameTaken() {
        when(repository.createUserIfNotInDataBase(anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString())).thenReturn(Observable.create(subscriber -> {
            subscriber.onNext(SignupResult.USERNAME_TAKEN);
            subscriber.onComplete();
        }));
        presenter.createAccount(EMAIL, USER, PASSWORD, DISPLAY_NAME, TYPE);

        verify(view, never()).displayInvalidEmail();
        verify(view, never()).displayInvalidUserName();
        verify(view, never()).displayInvalidPassword();
        verify(view, never()).displayInvalidDisplayName();
        verify(view, never()).displayInvalidType();
        verify(view, never()).displayDataError();
        verify(view, never()).displaySignUpSuccess();
        verify(view, atLeastOnce()).displayUserNameTaken();
        verify(view, never()).displayEmailTaken();
    }

    @Test
    public void testEmailTaken() {
        when(repository.createUserIfNotInDataBase(anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString())).thenReturn(Observable.create(subscriber -> {
            subscriber.onNext(SignupResult.EMAIL_TAKEN);
            subscriber.onComplete();
        }));
        presenter.createAccount(EMAIL, USER, PASSWORD, DISPLAY_NAME, TYPE);

        verify(view, never()).displayInvalidEmail();
        verify(view, never()).displayInvalidUserName();
        verify(view, never()).displayInvalidPassword();
        verify(view, never()).displayInvalidDisplayName();
        verify(view, never()).displayInvalidType();
        verify(view, never()).displayDataError();
        verify(view, never()).displaySignUpSuccess();
        verify(view, never()).displayUserNameTaken();
        verify(view, atLeastOnce()).displayEmailTaken();
    }

    @Test
    public void testSuccessfulSignUp() {
        when(repository.createUserIfNotInDataBase(anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString())).thenReturn(Observable.create(subscriber -> {
            subscriber.onNext(SignupResult.ACCOUNT_CREATED);
            subscriber.onComplete();
        }));
        presenter.createAccount(EMAIL, USER, PASSWORD, DISPLAY_NAME, TYPE);

        verify(view, never()).displayInvalidEmail();
        verify(view, never()).displayInvalidUserName();
        verify(view, never()).displayInvalidPassword();
        verify(view, never()).displayInvalidDisplayName();
        verify(view, never()).displayInvalidType();
        verify(view, never()).displayDataError();
        verify(view, atLeastOnce()).displaySignUpSuccess();
        verify(view, never()).displayUserNameTaken();
        verify(view, never()).displayEmailTaken();
    }

    @Test
    public void testAdminDoesNotExist() {
        when(repository.doesAdminAccountExist()).thenReturn(Observable.create(subscriber -> {
            subscriber.onNext(false);
            subscriber.onComplete();
        }));

        presenter.checkIfAdminExists();

        verify(view, atLeastOnce()).displayAdminDoesNotExist();
    }

    @Test
    public void testAdminExists() {
        when(repository.doesAdminAccountExist()).thenReturn(Observable.create(subscriber -> {
            subscriber.onNext(true);
            subscriber.onComplete();
        }));

        presenter.checkIfAdminExists();

        verify(view, never()).displayAdminDoesNotExist();
    }

    @Test
    public void testAdminDataError() {
        // we want silence for this one.
        when(repository.doesAdminAccountExist()).thenReturn(Observable.create(subscriber -> {
            subscriber.onError(new Exception("Test"));
        }));

        presenter.checkIfAdminExists();

        verify(view, never()).displayDataError();
    }
}
