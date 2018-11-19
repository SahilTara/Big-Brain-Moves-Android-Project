package com.uottawa.bigbrainmoves.servio.presenters;

import com.uottawa.bigbrainmoves.servio.models.Service;
import com.uottawa.bigbrainmoves.servio.views.ServiceProviderRecyclerView;

public class ServiceProviderRecyclerPresenter {

    private ServiceProviderRecyclerView current;
    // Represents the other recycler view
    private ServiceProviderRecyclerView other;

    public ServiceProviderRecyclerPresenter(ServiceProviderRecyclerView current,
                                            ServiceProviderRecyclerView other) {
        this.current = current;
        this.other = other;
    }

    public void transferItem(Service service) {
        if (current == null || other == null)
            return;

        current.removeItem(service);
        other.addItem(service);
    }



}
