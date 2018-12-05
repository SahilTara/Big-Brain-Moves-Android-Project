package com.uottawa.bigbrainmoves.servio;

import com.uottawa.bigbrainmoves.servio.models.ServiceProvider;
import com.uottawa.bigbrainmoves.servio.models.WeeklyAvailabilities;
import com.uottawa.bigbrainmoves.servio.presenters.ManageAvailabilitiesPresenter;
import com.uottawa.bigbrainmoves.servio.repositories.Repository;
import com.uottawa.bigbrainmoves.servio.util.CurrentAccount;
import com.uottawa.bigbrainmoves.servio.util.Pair;
import com.uottawa.bigbrainmoves.servio.util.enums.DayOfWeek;
import com.uottawa.bigbrainmoves.servio.views.ManageAvailabilitiesView;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.schedulers.ExecutorScheduler;
import io.reactivex.plugins.RxJavaPlugins;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(CurrentAccount.class)
public class TestManageAvailabilitiesPresenter {
    @Mock
    private Repository repository;

    @Mock
    private ManageAvailabilitiesView view;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private ManageAvailabilitiesPresenter presenter;

    @Before
    public void setup() {
        presenter = new ManageAvailabilitiesPresenter(view, repository);
    }

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
    public void testGetAvailabilitiesDataError() {
        when(repository.getAvailabilities()).thenReturn(Observable.create(subscriber -> {
            subscriber.onError(new Exception("Test"));
        }));

        presenter.getAvailabilities();

        verify(view, atLeastOnce()).displayDbError();
        verify(view, never()).displayTimes(any(WeeklyAvailabilities.class));
    }

    @Test
    public void testGetAvailabilitiesValidAvailabilities() {
        mockStatic(CurrentAccount.class);
        CurrentAccount currentAccount = mock(CurrentAccount.class);
        when(CurrentAccount.getInstance())
                .thenReturn(currentAccount);
        when(currentAccount.getCurrentAccount())
                .thenReturn(new ServiceProvider(
                        "Test", "Test",
                        "613-343-3333", "33 Test", "Test",
                        "Test",true));

        when(repository.getAvailabilities()).thenReturn(Observable.create(subscriber -> {
            subscriber.onNext(Optional.empty());
            subscriber.onComplete();
        }));

        presenter.getAvailabilities();

        verify(view, never()).displayDbError();
        verify(view, atLeastOnce()).displayTimes(any(WeeklyAvailabilities.class));
    }

    @Test
    public void testSetTimeMonday() {
        WeeklyAvailabilities availabilities = mock(WeeklyAvailabilities.class);
        ServiceProvider serviceProvider = mock(ServiceProvider.class);

        mockStatic(CurrentAccount.class);

        CurrentAccount currentAccount = mock(CurrentAccount.class);
        when(CurrentAccount.getInstance())
                .thenReturn(currentAccount);
        when(currentAccount.getCurrentAccount())
                .thenReturn(serviceProvider);
        when(serviceProvider.getAvailabilities()).thenReturn(availabilities);

        presenter.setTime("0:00", WeeklyAvailabilities.TimeSlot.MONDAY_START);
        
        verify(availabilities, atLeastOnce()).setMondayStart(anyString());
        verify(availabilities, never()).setMondayEnd(anyString());
        verify(availabilities, never()).setTuesdayStart(anyString());
        verify(availabilities, never()).setTuesdayEnd(anyString());
        verify(availabilities, never()).setWednesdayStart(anyString());
        verify(availabilities, never()).setWednesdayEnd(anyString());
        verify(availabilities, never()).setThursdayStart(anyString());
        verify(availabilities, never()).setThursdayEnd(anyString());
        verify(availabilities, never()).setFridayStart(anyString());
        verify(availabilities, never()).setFridayEnd(anyString());
        verify(availabilities, never()).setSaturdayStart(anyString());
        verify(availabilities, never()).setSaturdayEnd(anyString());
        verify(availabilities, never()).setSundayStart(anyString());
        verify(availabilities, never()).setSundayEnd(anyString());

        presenter.setTime("0:00", WeeklyAvailabilities.TimeSlot.MONDAY_END);

        verify(availabilities, atLeastOnce()).setMondayStart(anyString());
        verify(availabilities, atLeastOnce()).setMondayEnd(anyString());
        verify(availabilities, never()).setTuesdayStart(anyString());
        verify(availabilities, never()).setTuesdayEnd(anyString());
        verify(availabilities, never()).setWednesdayStart(anyString());
        verify(availabilities, never()).setWednesdayEnd(anyString());
        verify(availabilities, never()).setThursdayStart(anyString());
        verify(availabilities, never()).setThursdayEnd(anyString());
        verify(availabilities, never()).setFridayStart(anyString());
        verify(availabilities, never()).setFridayEnd(anyString());
        verify(availabilities, never()).setSaturdayStart(anyString());
        verify(availabilities, never()).setSaturdayEnd(anyString());
        verify(availabilities, never()).setSundayStart(anyString());
        verify(availabilities, never()).setSundayEnd(anyString());

    }

    @Test
    public void testSetTimeTuesday() {
        WeeklyAvailabilities availabilities = mock(WeeklyAvailabilities.class);
        ServiceProvider serviceProvider = mock(ServiceProvider.class);

        mockStatic(CurrentAccount.class);

        CurrentAccount currentAccount = mock(CurrentAccount.class);
        when(CurrentAccount.getInstance())
                .thenReturn(currentAccount);
        when(currentAccount.getCurrentAccount())
                .thenReturn(serviceProvider);
        when(serviceProvider.getAvailabilities()).thenReturn(availabilities);
        presenter.setTime("0:00", WeeklyAvailabilities.TimeSlot.TUESDAY_START);

        verify(availabilities, never()).setMondayStart(anyString());
        verify(availabilities, never()).setMondayEnd(anyString());
        verify(availabilities, atLeastOnce()).setTuesdayStart(anyString());
        verify(availabilities, never()).setTuesdayEnd(anyString());
        verify(availabilities, never()).setWednesdayStart(anyString());
        verify(availabilities, never()).setWednesdayEnd(anyString());
        verify(availabilities, never()).setThursdayStart(anyString());
        verify(availabilities, never()).setThursdayEnd(anyString());
        verify(availabilities, never()).setFridayStart(anyString());
        verify(availabilities, never()).setFridayEnd(anyString());
        verify(availabilities, never()).setSaturdayStart(anyString());
        verify(availabilities, never()).setSaturdayEnd(anyString());
        verify(availabilities, never()).setSundayStart(anyString());
        verify(availabilities, never()).setSundayEnd(anyString());

        presenter.setTime("0:00", WeeklyAvailabilities.TimeSlot.TUESDAY_END);

        verify(availabilities, never()).setMondayStart(anyString());
        verify(availabilities, never()).setMondayEnd(anyString());
        verify(availabilities, atLeastOnce()).setTuesdayStart(anyString());
        verify(availabilities, atLeastOnce()).setTuesdayEnd(anyString());
        verify(availabilities, never()).setWednesdayStart(anyString());
        verify(availabilities, never()).setWednesdayEnd(anyString());
        verify(availabilities, never()).setThursdayStart(anyString());
        verify(availabilities, never()).setThursdayEnd(anyString());
        verify(availabilities, never()).setFridayStart(anyString());
        verify(availabilities, never()).setFridayEnd(anyString());
        verify(availabilities, never()).setSaturdayStart(anyString());
        verify(availabilities, never()).setSaturdayEnd(anyString());
        verify(availabilities, never()).setSundayStart(anyString());
        verify(availabilities, never()).setSundayEnd(anyString());
    }


    @Test
    public void testSaveTimeMondayInvalid() {
        WeeklyAvailabilities availabilities = mock(WeeklyAvailabilities.class);
        ServiceProvider serviceProvider = mock(ServiceProvider.class);

        mockStatic(CurrentAccount.class);

        CurrentAccount currentAccount = mock(CurrentAccount.class);
        when(CurrentAccount.getInstance())
                .thenReturn(currentAccount);
        when(currentAccount.getCurrentAccount())
                .thenReturn(serviceProvider);
        when(serviceProvider.getAvailabilities()).thenReturn(availabilities);

        when(availabilities.getInvalidTimeSlot()).thenReturn(Optional.of(DayOfWeek.MONDAY));

        presenter.saveTimes();

        verify(view, atLeastOnce()).displayDayInvalid(DayOfWeek.MONDAY);
        verify(view, never()).displayDayInvalid(DayOfWeek.TUESDAY);
        verify(view, never()).displayDayInvalid(DayOfWeek.WEDNESDAY);
        verify(view, never()).displayDayInvalid(DayOfWeek.THURSDAY);
        verify(view, never()).displayDayInvalid(DayOfWeek.FRIDAY);
        verify(view, never()).displayDayInvalid(DayOfWeek.SATURDAY);
        verify(view, never()).displayDayInvalid(DayOfWeek.SUNDAY);
        verify(view, never()).displaySuccessfulSave();
    }

    @Test
    public void testSaveTimeFridayInvalid() {
        WeeklyAvailabilities availabilities = mock(WeeklyAvailabilities.class);
        ServiceProvider serviceProvider = mock(ServiceProvider.class);

        mockStatic(CurrentAccount.class);

        CurrentAccount currentAccount = mock(CurrentAccount.class);
        when(CurrentAccount.getInstance())
                .thenReturn(currentAccount);
        when(currentAccount.getCurrentAccount())
                .thenReturn(serviceProvider);
        when(serviceProvider.getAvailabilities()).thenReturn(availabilities);
        when(availabilities.getInvalidTimeSlot()).thenReturn(Optional.of(DayOfWeek.FRIDAY));

        presenter.saveTimes();

        verify(view, never()).displayDayInvalid(DayOfWeek.MONDAY);
        verify(view, never()).displayDayInvalid(DayOfWeek.TUESDAY);
        verify(view, never()).displayDayInvalid(DayOfWeek.WEDNESDAY);
        verify(view, never()).displayDayInvalid(DayOfWeek.THURSDAY);
        verify(view, atLeastOnce()).displayDayInvalid(DayOfWeek.FRIDAY);
        verify(view, never()).displayDayInvalid(DayOfWeek.SATURDAY);
        verify(view, never()).displayDayInvalid(DayOfWeek.SUNDAY);
        verify(view, never()).displaySuccessfulSave();
    }
    
    @Test
    public void testSaveTimeSuccessful() {
        WeeklyAvailabilities availabilities = mock(WeeklyAvailabilities.class);
        ServiceProvider serviceProvider = mock(ServiceProvider.class);

        mockStatic(CurrentAccount.class);

        CurrentAccount currentAccount = mock(CurrentAccount.class);

        when(CurrentAccount.getInstance())
                .thenReturn(currentAccount);
        when(currentAccount.getCurrentAccount())
                .thenReturn(serviceProvider);
        when(serviceProvider.getAvailabilities()).thenReturn(availabilities);


        when(availabilities.getInvalidTimeSlot()).thenReturn(Optional.empty());

        presenter.saveTimes();

        verify(view, never()).displayDayInvalid(DayOfWeek.MONDAY);
        verify(view, never()).displayDayInvalid(DayOfWeek.TUESDAY);
        verify(view, never()).displayDayInvalid(DayOfWeek.WEDNESDAY);
        verify(view, never()).displayDayInvalid(DayOfWeek.THURSDAY);
        verify(view, never()).displayDayInvalid(DayOfWeek.FRIDAY);
        verify(view, never()).displayDayInvalid(DayOfWeek.SATURDAY);
        verify(view, never()).displayDayInvalid(DayOfWeek.SUNDAY);
        verify(view, atLeastOnce()).displaySuccessfulSave();
    }

    @Test
    public void testGetTimeRestrictionSaturdayStartNoEnd() {
        WeeklyAvailabilities availabilities = mock(WeeklyAvailabilities.class);
        ServiceProvider serviceProvider = mock(ServiceProvider.class);

        mockStatic(CurrentAccount.class);

        CurrentAccount currentAccount = mock(CurrentAccount.class);

        when(CurrentAccount.getInstance())
                .thenReturn(currentAccount);
        when(currentAccount.getCurrentAccount())
                .thenReturn(serviceProvider);
        when(serviceProvider.getAvailabilities()).thenReturn(availabilities);

        when(availabilities.getSaturdayEnd()).thenReturn("End Time");

        Pair<String, Boolean> result = presenter.getTimeRestriction("SATURDAY_START");
        assertEquals("Expected result to be 23:30 for a day with no end defined", result.getFirst(), "23:30");
        assertFalse("Expected result for an ending restriction to be false", result.getSecond());
    }

    @Test
    public void testGetTimeRestrictionSaturdayStartWithEnd() {
        WeeklyAvailabilities availabilities = mock(WeeklyAvailabilities.class);
        ServiceProvider serviceProvider = mock(ServiceProvider.class);

        mockStatic(CurrentAccount.class);

        CurrentAccount currentAccount = mock(CurrentAccount.class);

        when(CurrentAccount.getInstance())
                .thenReturn(currentAccount);
        when(currentAccount.getCurrentAccount())
                .thenReturn(serviceProvider);
        when(serviceProvider.getAvailabilities()).thenReturn(availabilities);

        when(availabilities.getSaturdayEnd()).thenReturn("20:00");

        Pair<String, Boolean> result = presenter.getTimeRestriction("SATURDAY_START");
        assertEquals("Expected result to be 20:00 for this day since the end is defined as that",
                result.getFirst(), "20:00");
        assertFalse("Expected result for an ending restriction to be false", result.getSecond());
    }

    @Test
    public void testGetTimeRestrictionWednesdayEndNoStart() {
        WeeklyAvailabilities availabilities = mock(WeeklyAvailabilities.class);
        ServiceProvider serviceProvider = mock(ServiceProvider.class);

        mockStatic(CurrentAccount.class);

        CurrentAccount currentAccount = mock(CurrentAccount.class);

        when(CurrentAccount.getInstance())
                .thenReturn(currentAccount);
        when(currentAccount.getCurrentAccount())
                .thenReturn(serviceProvider);
        when(serviceProvider.getAvailabilities()).thenReturn(availabilities);

        when(availabilities.getWednesdayStart()).thenReturn("Start Time");

        Pair<String, Boolean> result = presenter.getTimeRestriction("WEDNESDAY_END");
        assertEquals("Expected result to be 00:00 for a day with no start defined", result.getFirst(), "00:00");
        assertTrue("Expected result for an start restriction to be true", result.getSecond());
    }

    @Test
    public void testGetTimeRestrictionWednesdayEndWithStart() {
        WeeklyAvailabilities availabilities = mock(WeeklyAvailabilities.class);
        ServiceProvider serviceProvider = mock(ServiceProvider.class);

        mockStatic(CurrentAccount.class);

        CurrentAccount currentAccount = mock(CurrentAccount.class);

        when(CurrentAccount.getInstance())
                .thenReturn(currentAccount);
        when(currentAccount.getCurrentAccount())
                .thenReturn(serviceProvider);
        when(serviceProvider.getAvailabilities()).thenReturn(availabilities);

        when(availabilities.getWednesdayStart()).thenReturn("15:00");

        Pair<String, Boolean> result = presenter.getTimeRestriction("WEDNESDAY_END");
        assertEquals("Expected result to be 15:00 for this day since the start is defined to be that",
                result.getFirst(), "15:00");
        assertTrue("Expected result for an start restriction to be true", result.getSecond());
    }


}
