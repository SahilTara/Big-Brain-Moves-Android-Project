package com.uottawa.bigbrainmoves.servio.presenters;

import com.uottawa.bigbrainmoves.servio.models.Account;
import com.uottawa.bigbrainmoves.servio.repositories.Repository;
import com.uottawa.bigbrainmoves.servio.util.CurrentAccount;
import com.uottawa.bigbrainmoves.servio.views.MainView;

public class MainScreenPresenter {

    private final MainView view;
    private final Repository repository;

    public MainScreenPresenter(MainView view, Repository repository) {
        this.view = view;
        this.repository = repository;
    }

    public void signOut() {
        repository.signOutCurrentUser();
        CurrentAccount.getInstance().setCurrentAccount(null);
        view.displaySignOut();
    }

    public void showWelcomeMessage() {
        Account account = CurrentAccount.getInstance().getCurrentAccount();
        view.displayWelcomeText(account.getDisplayName());
    }
}
