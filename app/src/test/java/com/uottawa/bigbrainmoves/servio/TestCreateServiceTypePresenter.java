package com.uottawa.bigbrainmoves.servio;

import com.uottawa.bigbrainmoves.servio.presenters.CreateServiceTypePresenter;
import com.uottawa.bigbrainmoves.servio.repositories.Repository;
import com.uottawa.bigbrainmoves.servio.views.CreateServiceTypeView;

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

import io.reactivex.Observable;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class TestCreateServiceTypePresenter {
    @ClassRule
    public static final RxJavaSchedulerRule schedulers = new RxJavaSchedulerRule();

    @Mock
    private Repository repository;

    @Mock
    private CreateServiceTypeView view;

    private CreateServiceTypePresenter presenter;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private static final String NAME = "Plumbing";
    private static final String INVALID_NAME = "Plumbing BLAHHHHHHHHHHHHHHHH PLUMBINGGG";

    private static final String VALUE = "3042932.21313";
    private static final String INVALID_VALUE = "033A";
    private static final String INVALID_VALUE_BIG = "1.7E309";

    private static final double ROUNDED = 3042932.21;
    @Captor
    private ArgumentCaptor<String> type;

    @Captor
    private ArgumentCaptor<Double> value;

    @Before
    public void setup() {
        presenter = new CreateServiceTypePresenter(view, repository);
    }
    @Test
    public void testInvalidName() {
        presenter.createServiceType(INVALID_NAME, VALUE);
        verify(view, atLeastOnce()).displayInvalidName();
        verify(view, never()).displaySuccess();
        verify(view, never()).displayNameTaken();
    }

    @Test
    public void testInvalidValue() {
        presenter.createServiceType(NAME, INVALID_VALUE);
        presenter.createServiceType(NAME, INVALID_VALUE_BIG);
        verify(view, never()).displayInvalidName();
        verify(view, times(2)).displayInvalidValue();
        verify(view, never()).displaySuccess();
        verify(view, never()).displayNameTaken();
    }

    @Test
    public void testDatabaseError() {
        when(repository.createServiceTypeIfNotInDatabase(anyString(), anyDouble()))
                .thenReturn(Observable.create(subscriber -> {
                    subscriber.onError(new Exception("Test"));
                }));

        presenter.createServiceType(NAME, VALUE);
        verify(view, never()).displayInvalidName();
        verify(view, never()).displayInvalidValue();
        verify(view, atLeastOnce()).displayDataError();
        verify(view, never()).displaySuccess();
        verify(view, never()).displayNameTaken();
    }

    @Test
    public void testNameTaken() {
        when(repository.createServiceTypeIfNotInDatabase(anyString(), anyDouble()))
                .thenReturn(Observable.create(subscriber -> {
                    subscriber.onNext(false);
                    subscriber.onComplete();
                }));

        presenter.createServiceType(NAME, VALUE);
        verify(repository).createServiceTypeIfNotInDatabase(type.capture(), value.capture());
        verify(view, never()).displayInvalidName();
        verify(view, never()).displayInvalidValue();
        verify(view, never()).displayDataError();
        verify(view, never()).displaySuccess();
        verify(view, atLeastOnce()).displayNameTaken();

        assertEquals("Expected type passed to database to be the same as one passed to presenter.",
                NAME.toLowerCase(), type.getValue());
        assertEquals("Expected value passed to database to be rounded to two decimal places of one passed",
                ROUNDED, value.getValue(), 0.000001);
    }

    @Test
    public void testSuccess() {
        when(repository.createServiceTypeIfNotInDatabase(anyString(), anyDouble()))
                .thenReturn(Observable.create(subscriber -> {
                    subscriber.onNext(true);
                    subscriber.onComplete();
                }));

        presenter.createServiceType(NAME, VALUE);

        verify(repository).createServiceTypeIfNotInDatabase(type.capture(), value.capture());
        verify(view, never()).displayInvalidName();
        verify(view, never()).displayInvalidValue();
        verify(view, never()).displayDataError();
        verify(view, atLeastOnce()).displaySuccess();
        verify(view, never()).displayNameTaken();

        assertEquals("Expected type passed to database to be the same as one passed to presenter.",
                NAME.toLowerCase(), type.getValue());
        assertEquals("Expected value passed to database to be rounded to two decimal places of one passed",
                ROUNDED, value.getValue(), 0.000001);
    }
}
