package com.uottawa.bigbrainmoves.servio;

import com.uottawa.bigbrainmoves.servio.models.Booking;
import com.uottawa.bigbrainmoves.servio.models.Rating;
import com.uottawa.bigbrainmoves.servio.models.Service;
import com.uottawa.bigbrainmoves.servio.presenters.BookingRecyclerPresenter;
import com.uottawa.bigbrainmoves.servio.presenters.LoginOrSignUpPresenter;
import com.uottawa.bigbrainmoves.servio.repositories.Repository;
import com.uottawa.bigbrainmoves.servio.util.enums.BookingStatus;
import com.uottawa.bigbrainmoves.servio.views.BookingRecyclerView;
import com.uottawa.bigbrainmoves.servio.views.LoginOrSignUpView;

import org.junit.ClassRule;
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

import java.util.Optional;

import io.reactivex.Observable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.doubleThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TestBookingRecyclerPresenter {
    @ClassRule
    public static final RxJavaSchedulerRule schedulers = new RxJavaSchedulerRule();

    @Mock
    private Repository repository;

    @Mock
    private BookingRecyclerView view;

    @InjectMocks
    private BookingRecyclerPresenter presenter;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test
    public void testGetServiceByNameDbError() {
        when(repository.getServiceByName(anyString())).thenReturn(Observable.create(subscriber -> {
            subscriber.onError(new Exception("Test"));
        }));

        presenter.getService("test");

        verify(view, atLeastOnce()).displayDbError();
        verify(view, never()).displayService(any(Service.class));
        verify(view, never()).displayServiceNotOffered();
    }

    @Test
    public void testGetServiceByNameUnavailable() {
        Service serviceFirst = mock(Service.class);
        Service serviceSecond = mock(Service.class);
        Service serviceThird = mock(Service.class);

        when(serviceFirst.isDisabled()).thenReturn(true);

        when(serviceSecond.isDisabled()).thenReturn(false);
        when(serviceSecond.isOffered()).thenReturn(false);

        when(serviceThird.isDisabled()).thenReturn(true);

        when(repository.getServiceByName("test")).thenReturn(Observable.create(subscriber -> {
            subscriber.onNext(Optional.empty());
            subscriber.onComplete();
        }));

        when(repository.getServiceByName("test2")).thenReturn(Observable.create(subscriber -> {
            subscriber.onNext(Optional.of(serviceFirst));
            subscriber.onComplete();
        }));

        when(repository.getServiceByName("test3")).thenReturn(Observable.create(subscriber -> {
            subscriber.onNext(Optional.of(serviceSecond));
            subscriber.onComplete();
        }));

        when(repository.getServiceByName("test4")).thenReturn(Observable.create(subscriber -> {
            subscriber.onNext(Optional.of(serviceThird));
            subscriber.onComplete();
        }));

        presenter.getService("test");
        presenter.getService("test2");
        presenter.getService("test3");
        presenter.getService("test4");

        verify(view, never()).displayDbError();
        verify(view, times(4)).displayServiceNotOffered();
        verify(view, never()).displayService(any(Service.class));
    }

    @Test
    public void testGetServiceByNameAvailable() {
        Service servicePassing = mock(Service.class);

        when(servicePassing.isDisabled()).thenReturn(false);
        when(servicePassing.isOffered()).thenReturn(true);

        when(repository.getServiceByName("test")).thenReturn(Observable.create(subscriber -> {
            subscriber.onNext(Optional.of(servicePassing));
            subscriber.onComplete();
        }));

        presenter.getService("test");
        verify(view, never()).displayDbError();
        verify(view, never()).displayServiceNotOffered();
        verify(view, atLeastOnce()).displayService(any(Service.class));
    }

    @Test
    public void testUpdateStatusOfBooking() {
        ArgumentCaptor<BookingStatus> argument = ArgumentCaptor.forClass(BookingStatus.class);
        Booking booking = mock(Booking.class);

        presenter.updateStatusOfBooking(booking, BookingStatus.CANCELLED);

        verify(booking).setStatus(argument.capture());
        verify(repository, atLeastOnce()).saveBooking(booking);

        assertEquals("Expected Booking status to be set to cancelled",BookingStatus.CANCELLED, argument.getValue());

    }

    @Test
    public void testLoadRatingDbError() {
        Booking booking = mock(Booking.class);
        String providerUser = "tester";
        String serviceType = "test";
        String rater = "testing";

        when(repository.getRating(providerUser, serviceType, rater)).thenReturn(Observable.create(subscriber -> {
            subscriber.onError(new Exception("Test"));
        }));

        when(booking.getCustomerUser()).thenReturn(rater);
        when(booking.getProviderUser()).thenReturn(providerUser);
        when(booking.getServiceType()).thenReturn(serviceType);

        presenter.loadRating(booking);

        verify(view, atLeastOnce()).displayDbError();
        verify(view, never()).displayRating(any(Rating.class), anyBoolean());
    }

    @Test
    public void testLoadRatingNoPreviousRating() {
        Booking booking = mock(Booking.class);
        String providerUser = "tester";
        String serviceType = "test";
        String rater = "testing";

        when(repository.getRating(providerUser, serviceType, rater)).thenReturn(Observable.create(subscriber -> {
            subscriber.onNext(Optional.empty());
            subscriber.onComplete();
        }));

        when(booking.getCustomerUser()).thenReturn(rater);
        when(booking.getProviderUser()).thenReturn(providerUser);
        when(booking.getServiceType()).thenReturn(serviceType);

        presenter.loadRating(booking);

        verify(view, never()).displayDbError();
        verify(view, atLeastOnce()).displayRating(any(Rating.class), eq(false));
    }

    @Test
    public void testLoadRatingWithPreviousRating() {
        Booking booking = mock(Booking.class);
        String providerUser = "tester";
        String serviceType = "test";
        String rater = "testing";
        Rating rating = mock(Rating.class);

        when(repository.getRating(providerUser, serviceType, rater)).thenReturn(Observable.create(subscriber -> {
            subscriber.onNext(Optional.of(rating));
            subscriber.onComplete();
        }));

        when(booking.getCustomerUser()).thenReturn(rater);
        when(booking.getProviderUser()).thenReturn(providerUser);
        when(booking.getServiceType()).thenReturn(serviceType);

        presenter.loadRating(booking);

        verify(view, never()).displayDbError();
        verify(view, atLeastOnce()).displayRating(rating, true);
    }

    @Test
    public void testSetRatingDbError() {
        Rating rating = mock(Rating.class);

        when(repository.addRating(rating, 0.0, true)).thenReturn(Observable.create(subscriber -> {
            subscriber.onError(new Exception("Test"));
        }));

        presenter.setRating(rating, 0.0, true);

        verify(view, atLeastOnce()).displayDbError();
        verify(view, never()).displayRated();
        verify(view, never()).displayFailedRating();
    }

    @Test
    public void testSetRatingFailedRating() {
        Rating rating = mock(Rating.class);

        when(repository.addRating(rating, 0.0, true)).thenReturn(Observable.create(subscriber -> {
            subscriber.onNext(false);
            subscriber.onComplete();
        }));

        presenter.setRating(rating, 0.0, true);

        verify(view, never()).displayDbError();
        verify(view, never()).displayRated();
        verify(view, atLeastOnce()).displayFailedRating();
    }

    @Test
    public void testSetRatingRated() {
        Rating rating = mock(Rating.class);

        when(repository.addRating(rating, 0.0, true)).thenReturn(Observable.create(subscriber -> {
            subscriber.onNext(true);
            subscriber.onComplete();
        }));

        presenter.setRating(rating, 0.0, true);

        verify(view, never()).displayDbError();
        verify(view, atLeastOnce()).displayRated();
        verify(view, never()).displayFailedRating();
    }
}
