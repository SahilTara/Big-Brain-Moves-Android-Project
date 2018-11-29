package com.uottawa.bigbrainmoves.servio;

import com.uottawa.bigbrainmoves.servio.models.Account;
import com.uottawa.bigbrainmoves.servio.presenters.UserListPresenter;
import com.uottawa.bigbrainmoves.servio.repositories.Repository;
import com.uottawa.bigbrainmoves.servio.views.UserListView;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.reactivex.Observable;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TestUserListPresenter {
    @ClassRule
    public static final RxJavaSchedulerRule schedulers = new RxJavaSchedulerRule();

    @Mock
    private Repository repository;
    @Mock
    private UserListView view;

    private UserListPresenter presenter;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setup() {
        presenter = new UserListPresenter(view, repository);
    }

    @Test
    public void testDataError() {
        when(repository.getAllUsersFromDataBase()).thenReturn(Observable.create(subscriber -> {
            subscriber.onError(new Exception("Test"));
        }));

        presenter.showUserList();

        verify(view, atLeastOnce()).displayDataError();
        verify(view, never()).displayUsers(any());
    }

    @Test
    public void testValidAccountList() {
        when(repository.getAllUsersFromDataBase()).thenReturn(Observable.create(subscriber -> {
            subscriber.onNext(Collections.emptyList());
            subscriber.onNext(Stream.of(new Account(), new Account()).collect(Collectors.toList()));
            subscriber.onComplete();
        }));

        presenter.showUserList();

        verify(view, never()).displayDataError();
        verify(view, times(2)).displayUsers(any());
    }
}
