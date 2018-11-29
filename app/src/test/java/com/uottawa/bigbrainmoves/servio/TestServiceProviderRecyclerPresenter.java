package com.uottawa.bigbrainmoves.servio;

import com.uottawa.bigbrainmoves.servio.models.Service;
import com.uottawa.bigbrainmoves.servio.presenters.ServiceProviderRecyclerPresenter;
import com.uottawa.bigbrainmoves.servio.views.ServiceProviderRecyclerView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TestServiceProviderRecyclerPresenter {
    @Mock
    private ServiceProviderRecyclerView first;
    @Mock
    private ServiceProviderRecyclerView second;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    ServiceProviderRecyclerPresenter presenter;

    @Before
    public void setup() {
        presenter = new ServiceProviderRecyclerPresenter(first, second);
    }

    @Test
    public void testTransferItem() {
        Service service = mock(Service.class);

        presenter.transferItem(service);

        verify(first, atLeastOnce()).removeItem(any(Service.class));
        verify(second, atLeastOnce()).addItem(any(Service.class));

        verify(first, never()).addItem(any(Service.class));
        verify(second, never()).removeItem(any(Service.class));

    }
}
