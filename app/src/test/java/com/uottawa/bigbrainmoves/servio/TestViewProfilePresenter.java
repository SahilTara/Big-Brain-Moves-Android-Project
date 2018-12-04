package com.uottawa.bigbrainmoves.servio;

import com.uottawa.bigbrainmoves.servio.models.ServiceProvider;
import com.uottawa.bigbrainmoves.servio.presenters.ViewProfilePresenter;
import com.uottawa.bigbrainmoves.servio.repositories.Repository;
import com.uottawa.bigbrainmoves.servio.util.CurrentAccount;
import com.uottawa.bigbrainmoves.servio.views.ViewProfileView;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.schedulers.ExecutorScheduler;
import io.reactivex.plugins.RxJavaPlugins;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(CurrentAccount.class)
public class TestViewProfilePresenter {
    @Mock
    private Repository repository;

    @Mock
    private ViewProfileView view;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private ViewProfilePresenter presenter;

    @Before
    public void setup() {
        presenter = new ViewProfilePresenter(view, repository);
    }

    private static final String INVALID_PHONE_NUMBER_LENGTH_TOO_SHORT = "3334";
    private static final String INVALID_PHONE_NUMBER_WITH_COUNTRY_CODE_TOO_SHORT = "+1 3333";
    private static final String INVALID_PHONE_NUMBER_WITH_COUNTRY_CODE_NOT_SEPERATE = "+133333333";
    private static final String VALID_PHONE_NUMBER = "+1 12345";
    private static final String INVALID_ADDRESS = "abc";
    private static final String VALID_ADDRESS = "123 abc";
    private static final String INVALID_COMPANY = "";
    private static final String VALID_COMPANY = "abc";
    private static final String DESCRIPTON = "";
    private static final boolean IS_LICENSED = true;

    @BeforeClass
    public static void setUpRxSchedulers() {
        Scheduler immediate = new Scheduler() {
            @Override
            public Disposable scheduleDirect(@NonNull Runnable run, long delay, @NonNull TimeUnit unit) {
                // this prevents StackOverflowErrors when scheduling with a delay
                return super.scheduleDirect(run, 0, unit);
            }

            @Override
            public Worker createWorker() {
                return new ExecutorScheduler.ExecutorWorker(Runnable::run);
            }
        };

        RxJavaPlugins.setInitIoSchedulerHandler(scheduler -> immediate);
        RxJavaPlugins.setInitComputationSchedulerHandler(scheduler -> immediate);
        RxJavaPlugins.setInitNewThreadSchedulerHandler(scheduler -> immediate);
        RxJavaPlugins.setInitSingleSchedulerHandler(scheduler -> immediate);
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> immediate);
    }

    @Test
    public void testGetProfileInfo() {
        when(repository.getServicesProvidedByCurrentUser()).thenReturn(Observable.create(subscriber -> {
            subscriber.onNext(Collections.emptyList());
            subscriber.onComplete();
        }));

        when(repository.getServicesProvidable(anyList())).thenReturn(Observable.create(subscriber -> {
            subscriber.onNext(Collections.emptyList());
            subscriber.onComplete();
        }));

        ServiceProvider serviceProvider = mock(ServiceProvider.class);

        mockStatic(CurrentAccount.class);

        CurrentAccount currentAccount = mock(CurrentAccount.class);
        when(CurrentAccount.getInstance())
                .thenReturn(currentAccount);
        when(currentAccount.getCurrentAccount())
                .thenReturn(serviceProvider);
        when(serviceProvider.getPhoneNumber()).thenReturn("");
        when(serviceProvider.getAddress()).thenReturn("");
        when(serviceProvider.getCompanyName()).thenReturn("");
        when(serviceProvider.getDescription()).thenReturn("");
        when(serviceProvider.isLicensed()).thenReturn(false);

        presenter.showProfileInfo();

        // want silence
        verify(view, never()).displayDbError();
        verify(view, atLeastOnce()).displayServiceProviderInfo(
                anyString(), anyString(),
                anyString(), anyString(),
                anyBoolean(), anyList(),
                anyList());
    }

    @Test
    public void testSaveProfileInvalidPhoneNumber() {
        presenter.saveProfile(INVALID_PHONE_NUMBER_LENGTH_TOO_SHORT, INVALID_ADDRESS,
                INVALID_COMPANY, DESCRIPTON, IS_LICENSED, Collections.emptyList());
        presenter.saveProfile(INVALID_PHONE_NUMBER_WITH_COUNTRY_CODE_NOT_SEPERATE, INVALID_ADDRESS,
                INVALID_COMPANY, DESCRIPTON, IS_LICENSED, Collections.emptyList());
        presenter.saveProfile(INVALID_PHONE_NUMBER_WITH_COUNTRY_CODE_TOO_SHORT, INVALID_ADDRESS,
                INVALID_COMPANY, DESCRIPTON, IS_LICENSED, Collections.emptyList());
        presenter.saveProfile(VALID_PHONE_NUMBER, INVALID_ADDRESS,
                INVALID_COMPANY, DESCRIPTON, IS_LICENSED, Collections.emptyList());
        verify(view, times(3)).displayInvalidPhoneNumber();
        verify(view, atMost(3)).displayInvalidAddress();
        verify(view, never()).displayInvalidCompanyName();
        verify(view, never()).displayDbError();
        verify(view, never()).displaySaveUnsuccessful();
        verify(view, never()).displaySuccessfullySaved();
    }

    @Test
    public void testSaveProfileInvalidAddress() {
        presenter.saveProfile(VALID_PHONE_NUMBER, INVALID_ADDRESS,
                INVALID_COMPANY, DESCRIPTON, IS_LICENSED, Collections.emptyList());

        verify(view, never()).displayInvalidPhoneNumber();
        verify(view, atLeastOnce()).displayInvalidAddress();
        verify(view, never()).displayInvalidCompanyName();
        verify(view, never()).displayDbError();
        verify(view, never()).displaySaveUnsuccessful();
        verify(view, never()).displaySuccessfullySaved();
    }

    @Test
    public void testSaveProfileInvalidCompany() {
        presenter.saveProfile(VALID_PHONE_NUMBER, VALID_ADDRESS,
                INVALID_COMPANY, DESCRIPTON, IS_LICENSED, Collections.emptyList());

        verify(view, never()).displayInvalidPhoneNumber();
        verify(view, never()).displayInvalidAddress();
        verify(view, atLeastOnce()).displayInvalidCompanyName();
        verify(view, never()).displayDbError();
        verify(view, never()).displaySaveUnsuccessful();
        verify(view, never()).displaySuccessfullySaved();
    }

    @Test
    public void testSaveProfileDatabaseError() {
        ServiceProvider serviceProvider = mock(ServiceProvider.class);

        mockStatic(CurrentAccount.class);

        CurrentAccount currentAccount = mock(CurrentAccount.class);
        when(CurrentAccount.getInstance())
                .thenReturn(currentAccount);
        when(currentAccount.getCurrentAccount())
                .thenReturn(serviceProvider);

        when(serviceProvider.getServicesProvided()).thenReturn(Collections.emptyList());

        when(repository.saveProfile(anyString(), anyString(),
                anyString(), anyString(), anyBoolean(), anyList())).thenReturn(
                        Observable.create(subscriber -> {
                            subscriber.onError(new Exception("Test"));
                        }));

        presenter.saveProfile(VALID_PHONE_NUMBER, VALID_ADDRESS,
                VALID_COMPANY, DESCRIPTON, IS_LICENSED, Collections.emptyList());

        verify(view, never()).displayInvalidPhoneNumber();
        verify(view, never()).displayInvalidAddress();
        verify(view, never()).displayInvalidCompanyName();
        verify(view, atLeastOnce()).displayDbError();
        verify(view, never()).displaySaveUnsuccessful();
        verify(view, never()).displaySuccessfullySaved();
    }

    @Test
    public void testSaveProfileSaveUnsuccessful() {
        ServiceProvider serviceProvider = mock(ServiceProvider.class);

        mockStatic(CurrentAccount.class);

        CurrentAccount currentAccount = mock(CurrentAccount.class);
        when(CurrentAccount.getInstance())
                .thenReturn(currentAccount);
        when(currentAccount.getCurrentAccount())
                .thenReturn(serviceProvider);

        when(serviceProvider.getServicesProvided()).thenReturn(Collections.emptyList());

        when(repository.saveProfile(anyString(), anyString(),
                anyString(), anyString(), anyBoolean(), anyList())).thenReturn(
                Observable.create(subscriber -> {
                    subscriber.onNext(false);
                    subscriber.onComplete();
                }));

        presenter.saveProfile(VALID_PHONE_NUMBER, VALID_ADDRESS,
                VALID_COMPANY, DESCRIPTON, IS_LICENSED, Collections.emptyList());

        verify(view, never()).displayInvalidPhoneNumber();
        verify(view, never()).displayInvalidAddress();
        verify(view, never()).displayInvalidCompanyName();
        verify(view, never()).displayDbError();
        verify(view, atLeastOnce()).displaySaveUnsuccessful();
        verify(view, never()).displaySuccessfullySaved();
    }

    @Test
    public void testSaveProfileSuccessfullySaved() {
        ServiceProvider serviceProvider = mock(ServiceProvider.class);

        mockStatic(CurrentAccount.class);

        CurrentAccount currentAccount = mock(CurrentAccount.class);
        when(CurrentAccount.getInstance())
                .thenReturn(currentAccount);
        when(currentAccount.getCurrentAccount())
                .thenReturn(serviceProvider);

        when(serviceProvider.getServicesProvided()).thenReturn(Collections.emptyList());

        when(repository.saveProfile(anyString(), anyString(),
                anyString(), anyString(), anyBoolean(), anyList())).thenReturn(
                Observable.create(subscriber -> {
                    subscriber.onNext(true);
                    subscriber.onComplete();
                }));

        presenter.saveProfile(VALID_PHONE_NUMBER, VALID_ADDRESS,
                VALID_COMPANY, DESCRIPTON, IS_LICENSED, Collections.emptyList());

        verify(view, never()).displayInvalidPhoneNumber();
        verify(view, never()).displayInvalidAddress();
        verify(view, never()).displayInvalidCompanyName();
        verify(view, never()).displayDbError();
        verify(view, never()).displaySaveUnsuccessful();
        verify(view, atLeastOnce()).displaySuccessfullySaved();
    }
}