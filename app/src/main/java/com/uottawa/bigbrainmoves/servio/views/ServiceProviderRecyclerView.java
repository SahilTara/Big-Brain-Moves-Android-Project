package com.uottawa.bigbrainmoves.servio.views;

import com.uottawa.bigbrainmoves.servio.models.Service;

public interface ServiceProviderRecyclerView {
    void addItem(Service service);
    void removeItem(Service service);
}
