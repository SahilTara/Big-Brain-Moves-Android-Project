package com.uottawa.bigbrainmoves.servio.presenters;

import com.uottawa.bigbrainmoves.servio.models.Account;
import com.uottawa.bigbrainmoves.servio.repositories.Repository;
import com.uottawa.bigbrainmoves.servio.views.UserListView;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class UserListPresenter {
    private final UserListView view;
    private final Repository repository;

    public UserListPresenter(UserListView view, Repository repository) {
        this.view = view;
        this.repository = repository;
    }

    /**
     * Attempts to get the users list and send it to the view for viewing purposes.
     */
    public void showUserList() {
        Observer<List<Account>> listObserver = new Observer<List<Account>>() {
            Disposable disposable;

            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(List<Account> accounts) {
                view.displayUsers(accounts);
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
        repository.getAllUsersFromDataBase()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listObserver);
    }
}
