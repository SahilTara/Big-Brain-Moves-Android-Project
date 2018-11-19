package com.uottawa.bigbrainmoves.servio;

import com.uottawa.bigbrainmoves.servio.models.ServiceProvider;
import com.uottawa.bigbrainmoves.servio.presenters.ViewProfilePresenter;
import com.uottawa.bigbrainmoves.servio.repositories.Repository;
import com.uottawa.bigbrainmoves.servio.util.CurrentAccount;
import com.uottawa.bigbrainmoves.servio.views.ViewProfileView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;

import io.reactivex.Observable;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(CurrentAccount.class)
public class TestViewProfilePresenter {
    @Mock
    private Repository repository;

    @Mock
    private ViewProfileView view;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private ViewProfilePresenter presenter;

    @Before
    public void setup() {
        presenter = new ViewProfilePresenter(view, repository);
    }

    @Test
    public void testGetProfileInfo() {
        when(repository.getServicesProvidedByCurrentUser()).thenReturn(Observable.create(subscriber -> {
            subscriber.onNext(Collections.emptyList());
        }));

        when(repository.getServicesProvidable(anyList())).thenReturn(Observable.create(subscriber -> {
            subscriber.onNext(Collections.emptyList());
        }));

        ServiceProvider serviceProvider = mock(ServiceProvider.class);

        mockStatic(CurrentAccount.class);

        CurrentAccount currentAccount = mock(CurrentAccount.class);
        when(CurrentAccount.getInstance())
                .thenReturn(currentAccount);
        when(currentAccount.getCurrentAccount())
                .thenReturn(serviceProvider);
        when(serviceProvider.getPhoneNumber()).thenReturn("");
        when(serviceProvider.getAddress()).thenReturn("");
        when(serviceProvider.getCompanyName()).thenReturn("");
        when(serviceProvider.getDescription()).thenReturn("");
        when(serviceProvider.isLicensed()).thenReturn(false);

        presenter.showProfileInfo();

        // want silence
        verify(view, never()).displayDbError();
        verify(view, atLeastOnce()).displayServiceProviderInfo(
                anyString(), anyString(),
                anyString(), anyString(),
                anyBoolean(), anyList(),
                anyList());
    }
}