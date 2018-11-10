package com.uottawa.bigbrainmoves.servio;

import com.uottawa.bigbrainmoves.servio.models.ServiceType;
import com.uottawa.bigbrainmoves.servio.presenters.ManageServiceTypesPresenter;
import com.uottawa.bigbrainmoves.servio.repositories.Repository;
import com.uottawa.bigbrainmoves.servio.util.Pair;
import com.uottawa.bigbrainmoves.servio.views.ManageServiceTypesView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.Observable;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class TestManageServiceTypesPresenter {

    @Mock
    private Repository repository;

    @Mock
    private ManageServiceTypesView view;

    @InjectMocks
    private ManageServiceTypesPresenter presenter;

    @Captor
    private ArgumentCaptor<ServiceType> first;

    @Captor
    private ArgumentCaptor<Boolean> second;

    @Test
    public void testDataError() {
        when(repository.listenForServiceTypeChanges()).thenReturn(Observable.create(subscriber -> {
            subscriber.onError(new Exception("Test"));
        }));

        presenter.listenForServiceTypeUpdates();

        // Silence for this one
        verify(view, never()).displayDataError();
        verify(view, never()).displayServiceTypeUpdate(any(ServiceType.class), anyBoolean());
    }

    @Test
    public void testDataUpdateNotDelete() {
        ServiceType toSend = new ServiceType("Type", 300);
        when(repository.listenForServiceTypeChanges()).thenReturn(Observable.create(subscriber -> {
            subscriber.onNext(new Pair<>(toSend, false));
            subscriber.onComplete();
        }));

        presenter.listenForServiceTypeUpdates();
        verify(view, never()).displayDataError();
        verify(view, atLeastOnce()).displayServiceTypeUpdate(first.capture(), second.capture());
        assertEquals("Expected service types to be the same!", toSend, first.getValue());
        assertEquals("Expected the data to not indicate a deletion!",
                false, second.getValue());

    }

    @Test
    public void testDataUpdateDelete() {
        ServiceType toSend = new ServiceType("Type", 300);
        when(repository.listenForServiceTypeChanges()).thenReturn(Observable.create(subscriber -> {
            subscriber.onNext(new Pair<>(toSend, true));
            subscriber.onComplete();
        }));

        presenter.listenForServiceTypeUpdates();
        verify(view, never()).displayDataError();
        verify(view, atLeastOnce()).displayServiceTypeUpdate(first.capture(), second.capture());
        assertEquals("Expected service types to be the same!", toSend, first.getValue());
        assertEquals("Expected the data to indicate a deletion!",
                true, second.getValue());

    }

}
