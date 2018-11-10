package com.uottawa.bigbrainmoves.servio.presenters;

import com.uottawa.bigbrainmoves.servio.repositories.Repository;
import com.uottawa.bigbrainmoves.servio.views.CreateServiceTypeView;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class CreateServiceTypePresenter {

    private final CreateServiceTypeView view;
    private final Repository repository;

    public CreateServiceTypePresenter(CreateServiceTypeView view, Repository repository) {
        this.view = view;
        this.repository = repository;
    }

    public void createServiceType(String name, String value) {
        try {
            double val = Double.valueOf(value);
            val = Math.round(val * 100.0) / 100.0; // round to 2 decimal places.
            name = name.trim();
            name = name.toLowerCase();
            if (name.equals("") || !name.matches("(([A-Za-z]+\\s?)+)") || name.length() > 30) {
                view.displayInvalidName();
            } else if (val < 0 || val > Double.valueOf("1.7E308")) {
                view.displayInvalidValue();
            } else {
                // Simple observer set up.
                Observer<Boolean> booleanObserver = new Observer<Boolean>() {
                    Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    // gets boolean from the repository stream.
                    @Override
                    public void onNext(Boolean createdSuccessfully) {
                        if (createdSuccessfully) {
                            view.displaySuccess();
                        } else { // name is already taken.
                            view.displayNameTaken();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        view.displayDataError();
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
                repository.createServiceTypeIfNotInDatabase(name, val).subscribe(booleanObserver);
            }
        } catch (NumberFormatException e) {
            view.displayInvalidValue();
        }
    }
}
