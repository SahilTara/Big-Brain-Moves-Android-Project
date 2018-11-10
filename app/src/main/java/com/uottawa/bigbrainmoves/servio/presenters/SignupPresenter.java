package com.uottawa.bigbrainmoves.servio.presenters;


import com.uottawa.bigbrainmoves.servio.repositories.Repository;
import com.uottawa.bigbrainmoves.servio.util.SignupResult;
import com.uottawa.bigbrainmoves.servio.views.SignUpView;

import java.util.regex.Pattern;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class SignupPresenter extends  AccountLoginPresenter {
    private final SignUpView view;
    private final Repository repository;
    private static final Pattern EMAIL_ADDRESS
            = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+");

    public SignupPresenter(SignUpView view, Repository repository) {
        super(view, repository);
        this.view = view;
        this.repository = repository;
    }


    public void createAccount(String email,
                              String username,
                              String password,
                              String displayName,
                              String typeSelected) {

        if (!EMAIL_ADDRESS.matcher(email).matches()) {
            view.displayInvalidEmail();
        } else if (!username.matches("\\b[a-zA-Z][a-zA-Z0-9\\-._]{3,}\\b")) {
            view.displayInvalidUserName();
        } else if (!password.matches("^(?=\\S+$).{6,}$")) {
            view.displayInvalidPassword();
        } else if (!displayName.matches("(([A-Za-z]+\\s?)+)")) {
            view.displayInvalidDisplayName();
        } else if (typeSelected.equals("")) {
            view.displayInvalidType();
        } else {
            Observer<SignupResult> signupObserver = new Observer<SignupResult>() {
                Disposable disposable;

                @Override
                public void onSubscribe(Disposable d) {
                    disposable = d;
                }

                // gets boolean from the login helper.
                @Override
                public void onNext(SignupResult result) {
                    switch (result) {
                        case EMAIL_TAKEN:
                            view.displayEmailTaken();
                            break;
                        case USERNAME_TAKEN:
                            view.displayUserNameTaken();
                            break;
                        case ACCOUNT_CREATED:
                            view.displaySignUpSuccess();
                            break;
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
            repository.createUserIfNotInDataBase(email,
                                                 username,
                                                 password,
                                                 displayName,
                                                 typeSelected).subscribe(signupObserver);
        }
    }

    public void checkIfAdminExists() {
        // Simple observer set up.
        Observer<Boolean> booleanObserver = new Observer<Boolean>() {
            Disposable disposable;

            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            // gets boolean from the repository stream.
            @Override
            public void onNext(Boolean isExisting) {
                if (!isExisting) { // admin doesn't exist
                    view.displayAdminDoesNotExist();
                }
            }

            @Override
            public void onError(Throwable e) {
                // want to be silent here.
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

        repository.doesAdminAccountExist().subscribe(booleanObserver);
    }
}
