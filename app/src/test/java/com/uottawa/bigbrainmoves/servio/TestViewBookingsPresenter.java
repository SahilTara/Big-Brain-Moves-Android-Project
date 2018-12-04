package com.uottawa.bigbrainmoves.servio;

import com.uottawa.bigbrainmoves.servio.models.Account;
import com.uottawa.bigbrainmoves.servio.models.Booking;
import com.uottawa.bigbrainmoves.servio.presenters.UserListPresenter;
import com.uottawa.bigbrainmoves.servio.presenters.ViewBookingsPresenter;
import com.uottawa.bigbrainmoves.servio.repositories.Repository;
import com.uottawa.bigbrainmoves.servio.util.Pair;
import com.uottawa.bigbrainmoves.servio.views.UserListView;
import com.uottawa.bigbrainmoves.servio.views.ViewBookingsView;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.reactivex.Observable;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TestViewBookingsPresenter {
    @ClassRule
    public static final RxJavaSchedulerRule schedulers = new RxJavaSchedulerRule();

    @Mock
    private Repository repository;

    @Mock
    private ViewBookingsView view;

    private ViewBookingsPresenter presenter;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setup() {
        presenter = new ViewBookingsPresenter(view, repository);
    }

    @Test
    public void testDataError() {
        when(repository.listenForCurrentUserBookingChanges()).thenReturn(Observable.create(subscriber -> {
            subscriber.onError(new Exception("Test"));
        }));

        presenter.listenForBookingUpdates();

        // Never want this to go off, since we prefer silence for this one.
        verify(view, never()).displayDataError();
        verify(view, never()).displayBookingUpdate(any(Booking.class), anyBoolean());
    }

    @Test
    public void testValidBooking() {
        Booking booking = mock(Booking.class);
        when(repository.listenForCurrentUserBookingChanges()).thenReturn(Observable.create(subscriber -> {
            subscriber.onNext(new Pair<>(booking, false));
            subscriber.onNext(new Pair<>(booking, true));
            subscriber.onComplete();
        }));

        presenter.listenForBookingUpdates();

        verify(view, never()).displayDataError();
        verify(view, times(2)).displayBookingUpdate(any(Booking.class), anyBoolean());
    }
}
