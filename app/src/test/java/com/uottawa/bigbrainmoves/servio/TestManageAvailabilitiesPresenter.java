package com.uottawa.bigbrainmoves.servio;

import com.uottawa.bigbrainmoves.servio.models.ServiceProvider;
import com.uottawa.bigbrainmoves.servio.models.TimeSlot;
import com.uottawa.bigbrainmoves.servio.models.WeeklyAvailabilities;
import com.uottawa.bigbrainmoves.servio.presenters.ManageAvailabilitiesPresenter;
import com.uottawa.bigbrainmoves.servio.repositories.Repository;
import com.uottawa.bigbrainmoves.servio.util.CurrentAccount;
import com.uottawa.bigbrainmoves.servio.util.Pair;
import com.uottawa.bigbrainmoves.servio.util.enums.DayOfWeek;
import com.uottawa.bigbrainmoves.servio.util.enums.TimeSlotEntryType;
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

import java.sql.Time;
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
import static org.mockito.Mockito.times;
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
        when(repository.getAvailabilities()).thenReturn(Observable.create(subscriber ->
                subscriber.onError(new Exception("Test"))));

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
    public void testSetTimeMondayStart() {
        WeeklyAvailabilities availabilities = mock(WeeklyAvailabilities.class);
        ServiceProvider serviceProvider = mock(ServiceProvider.class);

        TimeSlot slot = mock(TimeSlot.class);

        mockStatic(CurrentAccount.class);

        CurrentAccount currentAccount = mock(CurrentAccount.class);
        when(CurrentAccount.getInstance())
                .thenReturn(currentAccount);
        when(currentAccount.getCurrentAccount())
                .thenReturn(serviceProvider);
        when(serviceProvider.getAvailabilities()).thenReturn(availabilities);
        when(availabilities.getTimeSlotOnDay(any())).thenReturn(slot);

        presenter.setTime("0:00", DayOfWeek.MONDAY, TimeSlotEntryType.START);

        verify(availabilities, atLeastOnce()).setTimeSlotOnDay(DayOfWeek.MONDAY, slot);
    }

    @Test
    public void testSetTimeMondayEnd() {
        WeeklyAvailabilities availabilities = mock(WeeklyAvailabilities.class);
        ServiceProvider serviceProvider = mock(ServiceProvider.class);

        TimeSlot slot = mock(TimeSlot.class);

        mockStatic(CurrentAccount.class);

        CurrentAccount currentAccount = mock(CurrentAccount.class);
        when(CurrentAccount.getInstance())
                .thenReturn(currentAccount);
        when(currentAccount.getCurrentAccount())
                .thenReturn(serviceProvider);
        when(serviceProvider.getAvailabilities()).thenReturn(availabilities);
        when(availabilities.getTimeSlotOnDay(any())).thenReturn(slot);

        presenter.setTime("0:00", DayOfWeek.MONDAY, TimeSlotEntryType.END);

        verify(availabilities, atLeastOnce()).setTimeSlotOnDay(DayOfWeek.MONDAY, slot);

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
        TimeSlot slot = mock(TimeSlot.class);

        mockStatic(CurrentAccount.class);

        CurrentAccount currentAccount = mock(CurrentAccount.class);

        when(CurrentAccount.getInstance())
                .thenReturn(currentAccount);
        when(currentAccount.getCurrentAccount())
                .thenReturn(serviceProvider);
        when(serviceProvider.getAvailabilities()).thenReturn(availabilities);

        when(availabilities.getTimeSlotOnDay(DayOfWeek.SATURDAY)).thenReturn(slot);
        when(slot.getEndTime()).thenReturn("End Time");

        Pair<String, Boolean> result = presenter.getTimeRestriction(DayOfWeek.SATURDAY, TimeSlotEntryType.START);
        assertEquals("Expected result to be 23:30 for a day with no end defined", result.getFirst(), "23:30");
        assertFalse("Expected result for an ending restriction to be false", result.getSecond());
    }

    @Test
    public void testGetTimeRestrictionSaturdayStartWithEnd() {
        WeeklyAvailabilities availabilities = mock(WeeklyAvailabilities.class);
        ServiceProvider serviceProvider = mock(ServiceProvider.class);
        TimeSlot slot = mock(TimeSlot.class);

        mockStatic(CurrentAccount.class);

        CurrentAccount currentAccount = mock(CurrentAccount.class);

        when(CurrentAccount.getInstance())
                .thenReturn(currentAccount);
        when(currentAccount.getCurrentAccount())
                .thenReturn(serviceProvider);
        when(serviceProvider.getAvailabilities()).thenReturn(availabilities);

        when(availabilities.getTimeSlotOnDay(DayOfWeek.SATURDAY)).thenReturn(slot);
        when(slot.getEndTime()).thenReturn("20:00");

        Pair<String, Boolean> result = presenter.getTimeRestriction(DayOfWeek.SATURDAY, TimeSlotEntryType.START);
        assertEquals("Expected result to be 20:00 for this day since the end is defined as that",
                result.getFirst(), "20:00");
        assertFalse("Expected result for an ending restriction to be false", result.getSecond());
    }
}
