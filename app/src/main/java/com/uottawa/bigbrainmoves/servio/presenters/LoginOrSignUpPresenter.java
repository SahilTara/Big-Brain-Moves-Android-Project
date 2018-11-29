package com.uottawa.bigbrainmoves.servio.presenters;

import com.uottawa.bigbrainmoves.servio.repositories.Repository;
import com.uottawa.bigbrainmoves.servio.views.LoginOrSignUpView;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LoginOrSignUpPresenter extends AccountLoginPresenter {
    private LoginOrSignUpView view;
    private Repository repository;

    public LoginOrSignUpPresenter(LoginOrSignUpView view, Repository repository) {
        super(view, repository);
        this.view = view;
        this.repository = repository;
    }

    public void login(String input, String password) {
        input = input.trim();

        if (input.length() == 0) {
            view.displayInvalidUser();
            return;
        } else if (password.length() == 0) {
            view.displayInvalidPassword();
            return;
        }

        // Simple observer set up.
        Observer<Boolean> booleanObserver = new Observer<Boolean>() {
            Disposable disposable;

            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            // gets boolean from the repository stream.
            @Override
            public void onNext(Boolean isValid) {
                if (isValid) {
                    view.displayValidLogin();
                } else { // not valid credentials
                    view.displayInvalidLogin();
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

        repository.login(input, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(booleanObserver);
    }


}
