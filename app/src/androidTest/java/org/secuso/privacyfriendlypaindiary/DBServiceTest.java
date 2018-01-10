package org.secuso.privacyfriendlypaindiary;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.secuso.privacyfriendlypaindiary.database.DBService;
import org.secuso.privacyfriendlypaindiary.database.entities.enums.BodyRegion;
import org.secuso.privacyfriendlypaindiary.database.entities.enums.Condition;
import org.secuso.privacyfriendlypaindiary.database.entities.enums.Gender;
import org.secuso.privacyfriendlypaindiary.database.entities.enums.PainQuality;
import org.secuso.privacyfriendlypaindiary.database.entities.enums.Time;
import org.secuso.privacyfriendlypaindiary.database.entities.impl.DiaryEntry;
import org.secuso.privacyfriendlypaindiary.database.entities.impl.Drug;
import org.secuso.privacyfriendlypaindiary.database.entities.impl.DrugIntake;
import org.secuso.privacyfriendlypaindiary.database.entities.impl.PainDescription;
import org.secuso.privacyfriendlypaindiary.database.entities.impl.User;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.DiaryEntryInterface;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.DrugIntakeInterface;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.DrugInterface;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.PainDescriptionInterface;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.UserInterface;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Susanne Felsen
 * @version 20180110
 * @see <a href="http://www.singhajit.com/testing-android-database/"></a>
 */
@RunWith(AndroidJUnit4.class)
public class DBServiceTest {

    DBService service;

    @Before
    public void setUp() {
        service = DBService.getInstance(InstrumentationRegistry.getTargetContext());
        service.reinitializeDatabase();
    }

    @After
    public void tearDown() throws Exception {
        service.close();
    }

    @Test
    public void testUser() {
        //create
        String firstName = "Max";
        String lastName = "Mustermann";
        Gender gender = Gender.MALE;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date dateOfBirth = null;
        try {
            dateOfBirth = dateFormat.parse("01.01.1965");
        } catch (ParseException e) {
            fail("Error parsing date.");
        }
        UserInterface user = new User(firstName, lastName, gender, dateOfBirth);
        long id = service.storeUser(user);

        //get
        user = service.getUserByID(id);
        assertEquals("First name incorrect.", firstName, user.getFirstName());
        assertEquals("Last name incorrect.", lastName, user.getLastName());
        assertEquals("Gender incorrect.", gender, user.getGender());
        assertEquals("Date of birth incorrect.", dateOfBirth, user.getDateOfBirth());

        //update
        String newFirstName = "Moritz";
        user.setFirstName(newFirstName);
        service.updateUser(user);
        user = service.getUserByID(id);
        assertEquals("First name incorrect.", newFirstName, user.getFirstName());
        assertEquals("Last name incorrect.", lastName, user.getLastName());

        //delete
        service.deleteUser(user);
        user = service.getUserByID(id);
        assertNull(user);
    }

    @Test
    public void testDiaryEntryAndAssociatedObjects() {
        //create
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date date = null;
        try {
            date = dateFormat.parse("28.11.2017");
        } catch (ParseException e) {
            fail("Error parsing date.");
        }
        Condition condition = Condition.OKAY;
        String notes = "Additional notes.";
        DiaryEntryInterface entry = new DiaryEntry(date);
        entry.setCondition(condition);
        entry.setNotes(notes);

        int painLevel = 0;
        EnumSet<BodyRegion> bodyRegions = EnumSet.of(BodyRegion.HEAD);
        EnumSet<PainQuality> painQualities = EnumSet.of(PainQuality.SHOOTING);
        EnumSet<Time> timesOfPain = EnumSet.of(Time.MORNING, Time.EVENING);
        PainDescriptionInterface painDescription = new PainDescription(painLevel, bodyRegions, painQualities, timesOfPain);
        entry.setPainDescription(painDescription);

        String drugName = "Ibuprofen";
        String dose1 = "400mg";
        String dose2 = "600mg";
        DrugInterface drug1 = new Drug(drugName, dose1);
        DrugInterface drug2 = new Drug(drugName, dose2);

        int quantityMorning = 0;
        int quantityNoon = 0;
        int quantityEvening = 1;
        int quantityNight = 0;
        DrugIntakeInterface drugIntake = new DrugIntake(drug1, quantityMorning, quantityNoon, quantityEvening, quantityNight);

        entry.addDrugIntake(drugIntake);
        long entryID = service.storeDiaryEntryAndAssociatedObjects(entry);

        //get
        entry = service.getDiaryEntryByID(entryID);
        DiaryEntryInterface diaryEntry = service.getDiaryEntryByDate(date);
        assertNotNull("Diary Entry was not saved.", diaryEntry);

        assertEquals("Date was incorrect.", date, entry.getDate());
        assertEquals("Condition was incorrect.", condition, entry.getCondition());
        assertEquals("Notes were incorrect.", notes, entry.getNotes());

        painDescription = entry.getPainDescription();
        assertEquals("Pain Level was incorrect.", painLevel, painDescription.getPainLevel());
        assertEquals("Body Region was incorrect.", bodyRegions, painDescription.getBodyRegions());
        assertEquals("Pain Qualities were incorrect.", painQualities, painDescription.getPainQualities());
        assertEquals("Times of Pain were incorrect.", timesOfPain, painDescription.getTimesOfPain());

        Set<DrugIntakeInterface> intakes = entry.getDrugIntakes();
        assertEquals("Number of Drug Intakes was incorrect.", 1,  intakes.size());
        drugIntake = intakes.iterator().next();
        assertEquals("Quantity Morning was incorrect.", quantityMorning, drugIntake.getQuantityMorning());
        assertEquals("Quantity Noon was incorrect.", quantityNoon, drugIntake.getQuantityNoon());
        assertEquals("Quantity Evening was incorrect.", quantityEvening, drugIntake.getQuantityEvening());
        assertEquals("Quantity Night was incorrect.", quantityNight, drugIntake.getQuantityNight());

        drug1 = drugIntake.getDrug();
        assertEquals("Drug Name was incorrect.", drugName, drug1.getName());
        assertEquals("Drug Dose was incorrect.", dose1, drug1.getDose());

        //update
        Condition newCondition = Condition.BAD;
        entry.setCondition(newCondition);
        int newPainLevel = 5;
        painDescription.setPainLevel(newPainLevel);
        int newQuantityMorning = 1;
        drugIntake.setQuantityMorning(newQuantityMorning);
        service.updateDiaryEntryAndAssociatedObjects(entry);

        entry = service.getDiaryEntryByID(entryID);
        assertEquals("Condition was incorrect.", newCondition, entry.getCondition());
        painDescription = entry.getPainDescription();
        assertEquals("Pain Level was incorrect.", newPainLevel, painDescription.getPainLevel());
        intakes = entry.getDrugIntakes();
        assertEquals("Number of Drug Intakes was incorrect.", 1, intakes.size());
        drugIntake = intakes.iterator().next();
        assertEquals("Quantity Morning was incorrect.", newQuantityMorning, drugIntake.getQuantityMorning());

        entry.addDrugIntake(new DrugIntake(drug2, quantityMorning, quantityNoon, quantityEvening, quantityNight));
        entry.removeDrugIntake(drugIntake);
        service.updateDiaryEntryAndAssociatedObjects(entry);
        intakes = entry.getDrugIntakes();
        assertEquals("Number of Drug Intakes was incorrect.", 1, intakes.size());
        drugIntake = intakes.iterator().next();
        drug2 = drugIntake.getDrug();
        assertEquals("Drug Name was incorrect.", drugName, drug2.getName());
        assertEquals("Drug Dose was incorrect.", dose2, drug2.getDose());


        //delete
        service.deleteDiaryEntryAndAssociatedObjects(entry);
        entry = service.getDiaryEntryByID(entryID);
        assertNull(entry);
        entry = service.getDiaryEntryByDate(date);
        assertNull(entry);
        drug1 = service.getDrugByID(drug1.getObjectID());
        assertNull(drug1);
    }

    @Test
    public void testGetDiaryEntriesByTimeSpan() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date date1 = null;
        Date date2 = null;
        Date date3 = null;
        Date date4 = null;
        try {
            date1 = dateFormat.parse("01.11.2017");
            date2 = dateFormat.parse("28.11.2017");
            date3 = dateFormat.parse("01.12.2017");
            date4 = dateFormat.parse("30.11.2017");
        } catch (ParseException e) {
            fail("Error parsing date.");
        }
        DiaryEntryInterface entry1 = new DiaryEntry(date1, Condition.GOOD, new PainDescription(2, EnumSet.of(BodyRegion.HEAD)), null, null);
        service.storeDiaryEntryAndAssociatedObjects(entry1);
        List<DiaryEntryInterface> entries = service.getDiaryEntriesByMonth(11, 2017);
        assertEquals(1, entries.size());

        DiaryEntryInterface entry2 = new DiaryEntry(date2, Condition.GOOD, new PainDescription(2, EnumSet.of(BodyRegion.HEAD)), null, null);
        service.storeDiaryEntryAndAssociatedObjects(entry2);
        entries = service.getDiaryEntriesByMonth(11, 2017);
        assertEquals(2, entries.size());

        DiaryEntryInterface entry3 = new DiaryEntry(date3, Condition.GOOD, new PainDescription(2, EnumSet.of(BodyRegion.HEAD)), null, null);
        service.storeDiaryEntryAndAssociatedObjects(entry3);
        entries = service.getDiaryEntriesByMonth(11, 2017);
        assertEquals(2, entries.size());
        entries = service.getDiaryEntriesByMonth(12, 2017);
        assertEquals(1, entries.size());

        DiaryEntryInterface entry4 = new DiaryEntry(date4, Condition.GOOD, new PainDescription(2, EnumSet.of(BodyRegion.HEAD)), null, null);
        service.storeDiaryEntryAndAssociatedObjects(entry4);
        entries = service.getDiaryEntriesByMonth(11, 2017);
        assertEquals(3, entries.size());
    }

}
