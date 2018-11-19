package com.uottawa.bigbrainmoves.servio;

import com.uottawa.bigbrainmoves.servio.models.ServiceProvider;
import com.uottawa.bigbrainmoves.servio.models.WeeklyAvailabilities;
import com.uottawa.bigbrainmoves.servio.presenters.ManageAvailabilitiesPresenter;
import com.uottawa.bigbrainmoves.servio.repositories.Repository;
import com.uottawa.bigbrainmoves.servio.util.CurrentAccount;
import com.uottawa.bigbrainmoves.servio.util.DayOfWeek;
import com.uottawa.bigbrainmoves.servio.util.Pair;
import com.uottawa.bigbrainmoves.servio.views.ManageAvailabilitiesView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Optional;

import io.reactivex.Observable;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
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

    @Test
    public void testGetAvailabilitiesDataError() {
        when(repository.getAvailabilities()).thenReturn(Observable.create(subscriber -> {
            subscriber.onError(new Exception("Test"));
        }));

        presenter.getAvailabilities();

        verify(view, atLeastOnce()).displayDbError();
        verify(view, never()).displayTimes(
                anyString(), anyString(),
                anyString(), anyString(),
                anyString(), anyString(),
                anyString(), anyString(),
                anyString(), anyString(),
                anyString(), anyString(),
                anyString(), anyString());
    }

    @Test
    public void testGetAvailabilitiesValidAvailabilities() {
        mockStatic(CurrentAccount.class);
        CurrentAccount currentAccount = mock(CurrentAccount.class);
        when(CurrentAccount.getInstance())
                .thenReturn(currentAccount);
        when(currentAccount.getCurrentAccount())
                .thenReturn(new ServiceProvider(
                        "Test", "Test", "Test",
                        "613-343-3333", "33 Test", "Test",
                        "Test",true));

        when(repository.getAvailabilities()).thenReturn(Observable.create(subscriber -> {
            subscriber.onNext(Optional.empty());
            subscriber.onComplete();
        }));

        presenter.getAvailabilities();

        verify(view, never()).displayDbError();
        verify(view, atLeastOnce()).displayTimes(
                anyString(), anyString(),
                anyString(), anyString(),
                anyString(), anyString(),
                anyString(), anyString(),
                anyString(), anyString(),
                anyString(), anyString(),
                anyString(), anyString());
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

        presenter.setTime("0:00", "mondaystart");
        
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

        presenter.setTime("0:00", "mondayend");

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
        presenter.setTime("0:00", "tuesdayStart");

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

        presenter.setTime("0:00", "tuesdayEnd");

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
        when(availabilities.getMondayStart()).thenReturn("");
        when(availabilities.getMondayEnd()).thenReturn("1:00");

        presenter.saveTimes();

        verify(view, atLeastOnce()).displayDayInvalid(DayOfWeek.MONDAY);
        verify(view, never()).displayDayInvalid(DayOfWeek.TUESDAY);
        verify(view, never()).displayDayInvalid(DayOfWeek.WEDNESDAY);
        verify(view, never()).displayDayInvalid(DayOfWeek.THURSDAY);
        verify(view, never()).displayDayInvalid(DayOfWeek.FRIDAY);
        verify(view, never()).displayDayInvalid(DayOfWeek.SATURDAY);
        verify(view, never()).displayDayInvalid(DayOfWeek.SUNDAY);
        verify(view, never()).displaySuccesfulSave();
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
        when(availabilities.getMondayStart()).thenReturn("");
        when(availabilities.getMondayEnd()).thenReturn("");

        when(availabilities.getTuesdayStart()).thenReturn("");
        when(availabilities.getTuesdayEnd()).thenReturn("");

        when(availabilities.getWednesdayStart()).thenReturn("");
        when(availabilities.getWednesdayEnd()).thenReturn("");

        when(availabilities.getThursdayStart()).thenReturn("");
        when(availabilities.getThursdayEnd()).thenReturn("");

        when(availabilities.getFridayStart()).thenReturn("");
        when(availabilities.getFridayEnd()).thenReturn("1:00");

        presenter.saveTimes();

        verify(view, never()).displayDayInvalid(DayOfWeek.MONDAY);
        verify(view, never()).displayDayInvalid(DayOfWeek.TUESDAY);
        verify(view, never()).displayDayInvalid(DayOfWeek.WEDNESDAY);
        verify(view, never()).displayDayInvalid(DayOfWeek.THURSDAY);
        verify(view, atLeastOnce()).displayDayInvalid(DayOfWeek.FRIDAY);
        verify(view, never()).displayDayInvalid(DayOfWeek.SATURDAY);
        verify(view, never()).displayDayInvalid(DayOfWeek.SUNDAY);
        verify(view, never()).displaySuccesfulSave();
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


        when(availabilities.getMondayStart()).thenReturn("");
        when(availabilities.getMondayEnd()).thenReturn("");

        when(availabilities.getTuesdayStart()).thenReturn("");
        when(availabilities.getTuesdayEnd()).thenReturn("");

        when(availabilities.getWednesdayStart()).thenReturn("");
        when(availabilities.getWednesdayEnd()).thenReturn("");

        when(availabilities.getThursdayStart()).thenReturn("");
        when(availabilities.getThursdayEnd()).thenReturn("");

        when(availabilities.getFridayStart()).thenReturn("");
        when(availabilities.getFridayEnd()).thenReturn("");

        when(availabilities.getSaturdayStart()).thenReturn("");
        when(availabilities.getSaturdayEnd()).thenReturn("");

        when(availabilities.getSundayStart()).thenReturn("");
        when(availabilities.getSundayEnd()).thenReturn("");

        presenter.saveTimes();

        verify(view, never()).displayDayInvalid(DayOfWeek.MONDAY);
        verify(view, never()).displayDayInvalid(DayOfWeek.TUESDAY);
        verify(view, never()).displayDayInvalid(DayOfWeek.WEDNESDAY);
        verify(view, never()).displayDayInvalid(DayOfWeek.THURSDAY);
        verify(view, never()).displayDayInvalid(DayOfWeek.FRIDAY);
        verify(view, never()).displayDayInvalid(DayOfWeek.SATURDAY);
        verify(view, never()).displayDayInvalid(DayOfWeek.SUNDAY);
        verify(view, atLeastOnce()).displaySuccesfulSave();
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

        when(availabilities.getSaturdayEnd()).thenReturn("");

        Pair<String, Boolean> result = presenter.getTimeRestriction("saturdayStart");
        assertEquals("Expected result to be 23:30 for a day with no end defined", result.first, "23:30");
        assertFalse("Expected result for an ending restriction to be false", result.second);
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

        Pair<String, Boolean> result = presenter.getTimeRestriction("saturdayStart");
        assertEquals("Expected result to be 20:00 for this day since the end is defined as that",
                result.first, "20:00");
        assertFalse("Expected result for an ending restriction to be false", result.second);
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

        when(availabilities.getWednesdayStart()).thenReturn("");

        Pair<String, Boolean> result = presenter.getTimeRestriction("wednesdayEnd");
        assertEquals("Expected result to be 00:00 for a day with no start defined", result.first, "00:00");
        assertTrue("Expected result for an start restriction to be true", result.second);
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

        Pair<String, Boolean> result = presenter.getTimeRestriction("wednesdayEnd");
        assertEquals("Expected result to be 15:00 for this day since the start is defined to be that",
                result.first, "15:00");
        assertTrue("Expected result for an start restriction to be true", result.second);
    }


}
