package com.uottawa.bigbrainmoves.servio;

import com.uottawa.bigbrainmoves.servio.models.Booking;
import com.uottawa.bigbrainmoves.servio.models.Rating;
import com.uottawa.bigbrainmoves.servio.models.ReadOnlyService;
import com.uottawa.bigbrainmoves.servio.presenters.ViewBookingsPresenter;
import com.uottawa.bigbrainmoves.servio.presenters.ViewRatingsPresenter;
import com.uottawa.bigbrainmoves.servio.repositories.Repository;
import com.uottawa.bigbrainmoves.servio.util.Pair;
import com.uottawa.bigbrainmoves.servio.views.ViewBookingsView;
import com.uottawa.bigbrainmoves.servio.views.ViewRatingsView;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;

import io.reactivex.Observable;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TestViewRatingsPresenter {
    @ClassRule
    public static final RxJavaSchedulerRule schedulers = new RxJavaSchedulerRule();

    @Mock
    private Repository repository;

    @Mock
    private ViewRatingsView view;

    private ViewRatingsPresenter presenter;

    @Mock
    private ReadOnlyService service;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setup() {
        presenter = new ViewRatingsPresenter(view, repository);
    }

    @Test
    public void testDataError() {
        when(repository.listenForRatingChanges(service)).thenReturn(Observable.create(subscriber -> {
            subscriber.onError(new Exception("Test"));
        }));

        presenter.listenForRatingUpdates(service);

        // Never want this to go off, since we prefer silence for this one.
        verify(view, never()).displayDataError();
        verify(view, never()).displayRatingUpdate(any(Rating.class), anyBoolean());
    }

    @Test
    public void testValidBooking() {
        Rating rating = mock(Rating.class);
        when(repository.listenForRatingChanges(service)).thenReturn(Observable.create(subscriber -> {
            subscriber.onNext(new Pair<>(rating, false));
            subscriber.onNext(new Pair<>(rating, true));
            subscriber.onComplete();
        }));

        presenter.listenForRatingUpdates(service);

        verify(view, never()).displayDataError();
        verify(view, times(2)).displayRatingUpdate(any(Rating.class), anyBoolean());
    }
}
