package com.uottawa.bigbrainmoves.servio.views;

import com.uottawa.bigbrainmoves.servio.models.Service;
import com.uottawa.bigbrainmoves.servio.models.ServiceType;

import java.util.List;

public interface FindServicesView {
    void displayLiveServicesInSearch(ServiceType type, boolean isDeleted);
    void displayFiltered(List<Service> services);
    void displayLiveServices(Service service, boolean isDeleted);
}
