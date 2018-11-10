package com.uottawa.bigbrainmoves.servio.views;

import com.uottawa.bigbrainmoves.servio.models.ServiceType;

public interface ManageServiceTypesView {
    void displayDataError();
    void displayServiceTypeUpdate(ServiceType serviceType, boolean removed);
}
