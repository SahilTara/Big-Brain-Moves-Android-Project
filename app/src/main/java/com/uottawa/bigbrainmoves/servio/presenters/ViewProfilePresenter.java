package com.uottawa.bigbrainmoves.servio.presenters;

import com.uottawa.bigbrainmoves.servio.models.Service;
import com.uottawa.bigbrainmoves.servio.models.ServiceProvider;
import com.uottawa.bigbrainmoves.servio.repositories.Repository;
import com.uottawa.bigbrainmoves.servio.util.CurrentAccount;
import com.uottawa.bigbrainmoves.servio.views.ViewProfileView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class ViewProfilePresenter {
    private final ViewProfileView view;
    private final Repository repository;

    public ViewProfilePresenter(ViewProfileView view, Repository repository) {
        this.view = view;
        this.repository = repository;
    }

    /**
     * Grabs profile info for service provider and sends to view.
     */
    public void showProfileInfo() {
        ServiceProvider provider = (ServiceProvider) CurrentAccount.getInstance().getCurrentAccount();

        String phoneNumber = provider.getPhoneNumber();
        String address = provider.getAddress();
        String companyName = provider.getCompanyName();
        String description = provider.getDescription();
        boolean isLicensed = provider.isLicensed();

        // make a nested observer to grab the provided list and providable list.

        Observer<List<Service>> providedObserver = new Observer<List<Service>>() {
            Disposable disposable;

            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(List<Service> providedList) {

                // This is a gross nested observer, which is required :( for now...
                //TODO TRY TO CLEAN THIS UP

                Observer<List<Service>> providableObserver = new Observer<List<Service>>() {
                    Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(List<Service> providableList) {
                        provider.setServicesProvided(providedList);
                        view.displayServiceProviderInfo(phoneNumber, address, companyName,
                                description, isLicensed, providedList, providableList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        disposable.dispose();
                        disposable = null;
                    }

                    // do some cleanup
                    @Override
                    public void onComplete() {
                        disposable.dispose();
                        disposable = null;
                    }
                };
                // Convert service to list of strings given the service type.

                List<String> strings = new ArrayList<>();
                for (Service s : providedList) {
                    strings.add(s.getType());
                }

                repository.getServicesProvidable(strings).subscribe(providableObserver);
            }

            @Override
            public void onError(Throwable e) {
                disposable.dispose();
                disposable = null;
            }

            // do some cleanup
            @Override
            public void onComplete() {
                disposable.dispose();
                disposable = null;
            }
        };

        repository.getServicesProvidedByCurrentUser().subscribe(providedObserver);


    }

    public void saveProfile(String phoneNumber, String address, String companyName,
                            String description, boolean isLicensed, List<Service> services) {

        String[] phoneNumberSplit = phoneNumber.trim().split(" ");
        String phoneNumberNoCountryCode = phoneNumberSplit.length > 1 ? phoneNumberSplit[1] :
                                                                     phoneNumberSplit[0];
        phoneNumberNoCountryCode = phoneNumberNoCountryCode.replace("-", "");

        if (phoneNumberNoCountryCode.length() < 5
        || !phoneNumber.matches("([+]\\d{1,2}[\\s])?[0-9.-]+")) {
            view.displayInvalidPhoneNumber();
        } else if (!address.matches("\\d{1,5}\\s(([A-Za-z]+\\s?)+)")) {
            view.displayInvalidAddress();
        } else if (companyName.trim().length() == 0) {
            view.displayInvalidCompanyName();
        } else {
            ServiceProvider provider = (ServiceProvider) CurrentAccount.getInstance().getCurrentAccount();

            List<Service> modified = new ArrayList<>();
            // get all the newly added ones.
            for (Service service : services) {
                if (!service.isOffered()) {
                    Service s = new Service(service.getType(),
                            service.getRating(),
                            service.getServiceProviderUser(), true, false);
                    s.setOffered(true);
                    modified.add(s);
                }
            }

            // get all the removed ones.
            for (Service service : provider.getServicesProvided()) {
                if (!services.contains(service)) {
                    service.setOffered(false);
                    modified.add(service);
                }
            }



            Observer<Boolean> saveObserver = new Observer<Boolean>() {
                Disposable disposable;

                @Override
                public void onSubscribe(Disposable d) {
                    disposable = d;
                }

                @Override
                public void onNext(Boolean isSuccessful) {
                    if (isSuccessful) {
                        provider.setPhoneNumber(phoneNumber);
                        provider.setAddress(address);
                        provider.setCompanyName(companyName);
                        provider.setDescription(description);
                        provider.setLicensed(isLicensed);
                        provider.setServicesProvided(services);
                        view.displaySuccessfullySaved();
                    } else {
                        view.displaySaveUnsuccessful();
                    }
                }

                @Override
                public void onError(Throwable e) {
                    view.displayDbError();
                    disposable.dispose();
                    disposable = null;
                }

                // do some cleanup
                @Override
                public void onComplete() {
                    disposable.dispose();
                    disposable = null;
                }
            };

            repository.saveProfile(phoneNumber, address, companyName, description, isLicensed, modified)
                    .subscribe(saveObserver);

        }
    }


}