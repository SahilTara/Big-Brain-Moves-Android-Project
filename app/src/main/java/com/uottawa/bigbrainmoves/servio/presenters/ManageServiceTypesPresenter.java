package com.uottawa.bigbrainmoves.servio.presenters;

import com.uottawa.bigbrainmoves.servio.models.ServiceType;
import com.uottawa.bigbrainmoves.servio.repositories.Repository;
import com.uottawa.bigbrainmoves.servio.util.Pair;
import com.uottawa.bigbrainmoves.servio.views.ManageServiceTypesView;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class ManageServiceTypesPresenter {
    private ManageServiceTypesView view;
    private Repository repository;

    public ManageServiceTypesPresenter(ManageServiceTypesView view, Repository repository) {
        this.view = view;
        this.repository = repository;
    }

    public void listenForServiceTypeUpdates() {
        Observer<Pair<ServiceType, Boolean>> resultObserver = new Observer<Pair<ServiceType, Boolean>>() {
            Disposable disposable;

            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(Pair<ServiceType, Boolean> result) {
                view.displayServiceTypeUpdate(result.first, result.second);
            }

            @Override
            public void onError(Throwable e) {
                // want silence for this one.
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
        repository.listenForServiceTypeChanges().subscribe(resultObserver);
    }
}
