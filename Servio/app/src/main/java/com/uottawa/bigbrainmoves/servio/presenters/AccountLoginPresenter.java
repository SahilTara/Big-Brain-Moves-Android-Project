package com.uottawa.bigbrainmoves.servio.presenters;

import com.uottawa.bigbrainmoves.servio.models.Account;
import com.uottawa.bigbrainmoves.servio.repositories.Repository;
import com.uottawa.bigbrainmoves.servio.util.CurrentAccount;
import com.uottawa.bigbrainmoves.servio.views.AccountLoginView;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class AccountLoginPresenter {
    private AccountLoginView view;
    private Repository repository;
    private CurrentAccount currentAccount = CurrentAccount.getInstance();

    public AccountLoginPresenter(AccountLoginView view, Repository repository) {
        this.view = view;
        this.repository = repository;
    }

    /**
     * Method attempts to get the account info if the user's session is still valid,
     * and calls the correct view method based on the repository result.
     */
    public void attemptGettingAccountInfo() {
        // if the user isn't logged in or the uid is null an account can not be found.
        if (!repository.checkIfUserIsLoggedIn()) {
            view.displayNoAccountFound();
            return;
        }

        String uid = repository.getUserId();

        if (uid == null) {
            view.displayNoAccountFound();
            return;
        }

        // Simple observer set up.
        Observer<Account> accountObserver = new Observer<Account>() {
            Disposable disposable;

            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            // gets account from the repository stream.
            @Override
            public void onNext(Account account) {
                if (account == null) { // account may have been deleted somehow
                    view.displayNoAccountFound();
                } else { // Account is valid!
                    currentAccount.setCurrentAccount(account);
                    view.displayValidAccount();
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

        // subscribe to the getUserFromDatabaseMethod.
        repository.getUserFromDataBase(uid).subscribe(accountObserver);

    }
}
