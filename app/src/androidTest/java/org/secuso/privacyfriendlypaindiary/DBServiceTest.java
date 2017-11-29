package org.secuso.privacyfriendlypaindiary;

import android.database.sqlite.SQLiteDatabase;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Susanne Felsen
 * @version 20171128
 * @see <a href="http://www.singhajit.com/testing-android-database/"></a>
 */
@RunWith(AndroidJUnit4.class)
public class DBServiceTest {

    DBService service;

    @Before
    public void setUp() {
        service = DBService.getInstance(InstrumentationRegistry.getTargetContext());
        SQLiteDatabase db = service.getWritableDatabase();
        service.onUpgrade(db, 1, 1);
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
        SimpleDateFormat dateFormat = new SimpleDateFormat(DBService.DATE_PATTERN);
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
        SimpleDateFormat dateFormat = new SimpleDateFormat(DBService.DATE_PATTERN);
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
        BodyRegion bodyRegion = BodyRegion.HEAD;
        PainDescriptionInterface painDescription = new PainDescription(painLevel, bodyRegion);
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
        List<DiaryEntryInterface> entries = service.getDiaryEntriesByDate(date);
        assertEquals("Number of entries was incorrect.", 1, entries.size());

        assertEquals("Date was incorrect.", date, entry.getDate());
        assertEquals("Condition was incorrect.", condition, entry.getCondition());
        assertEquals("Notes were incorrect.", notes, entry.getNotes());

        painDescription = entry.getPainDescription();
        assertEquals("Pain Level was incorrect.", painLevel, painDescription.getPainLevel());
        assertEquals("Body Region was incorrect.", bodyRegion, painDescription.getBodyRegion());

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
        drug1 = service.getDrugByID(drug1.getObjectID());
        assertNotNull(drug1);

        //delete
        service.deleteDiaryEntryAndAssociatedObjects(entry);
        entry = service.getDiaryEntryByID(entryID);
        assertNull(entry);
        entries = service.getDiaryEntriesByDate(date);
        assertTrue(entries.isEmpty());
    }

}
