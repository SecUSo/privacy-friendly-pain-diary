/*
    This file is part of Privacy Friendly Pain Diary.

    Privacy Friendly Pain Diary is free software: you can redistribute it
    and/or modify it under the terms of the GNU General Public License as
    published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package org.secuso.privacyfriendlypaindiary.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Susanne Felsen
 * @version 20171229
 */
public class DBService extends SQLiteOpenHelper implements DBServiceInterface {

    private static DBService instance = null;

    private static final String TAG = DBService.class.getSimpleName();
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "paindiary";

    public static final String DATE_PATTERN = "yyyy-MM-dd";

    private DBService(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DBService getInstance(Context context) {
        if (instance == null && context != null) {
            instance = new DBService(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createAll(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropAll(db);
        onCreate(db);
    }

    @Override
    public void initializeDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        createAll(db);
    }

    @Override
    public void reinitializeDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        dropAll(db);
        initializeDatabase();
    }

    private void createAll(SQLiteDatabase db) {
        db.execSQL(User.TABLE_CREATE);
        db.execSQL(Drug.TABLE_CREATE);
        db.execSQL(PainDescription.TABLE_CREATE);
        db.execSQL(DiaryEntry.TABLE_CREATE);
        db.execSQL(DrugIntake.TABLE_CREATE);
        Log.i(TAG, "Created database.");
    }

    private void dropAll(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + User.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Drug.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PainDescription.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DiaryEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DrugIntake.TABLE_NAME);
    }

    @Override
    public long storeUser(UserInterface user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = getUserContentValues(user);

        long id = db.insert(User.TABLE_NAME, null, values);
        Log.d(TAG, "Created user.");

        return id;
    }

    @Override
    public void updateUser(UserInterface user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = getUserContentValues(user);
        db.update(User.TABLE_NAME, values, User.COLUMN_ID + " = ?",
                new String[]{String.valueOf(user.getObjectID())});
    }

    private ContentValues getUserContentValues(UserInterface user) {
        ContentValues values = new ContentValues();
        values.put(User.COLUMN_FIRST_NAME, user.getFirstName());
        values.put(User.COLUMN_LAST_NAME, user.getLastName());
        Gender gender = user.getGender();
        if(gender != null) {
            values.put(User.COLUMN_GENDER, gender.getValue());
        } else {
            values.put(User.COLUMN_GENDER, (Integer) null);
        }
        Date dateOfBirth = user.getDateOfBirth();
        if (dateOfBirth != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
            values.put(User.COLUMN_DATE_OF_BIRTH, dateFormat.format(dateOfBirth));
        } else {
            values.put(User.COLUMN_DATE_OF_BIRTH, (String) null);
        }
        return values;
    }

    @Override
    public void deleteUser(UserInterface user) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d(TAG, "Deleted user with name '" + user.getFirstName() + " " + user.getLastName() + "'.");
        db.delete(User.TABLE_NAME, User.COLUMN_ID + " = ?",
                new String[]{Long.toString(user.getObjectID())});
    }

    @Override
    public UserInterface getUserByID(long id) {
//        Log.d("DATABASE", Integer.toString(id));
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(User.TABLE_NAME, null, User.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        UserInterface user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = instantiateUserFromCursor(cursor);
        }
        cursor.close();

        return user;
    }

    @Override
    public List<UserInterface> getAllUsers() {
        List<UserInterface> users = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + User.TABLE_NAME;

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        UserInterface user = null;

        if (cursor != null && cursor.moveToFirst()) {
            do {
                user = instantiateUserFromCursor(cursor);
                users.add(user);
            } while (cursor.moveToNext());
        }

        return users;
    }

    private UserInterface instantiateUserFromCursor(Cursor cursor) {
        long objectID = cursor.getLong(cursor.getColumnIndex(User.COLUMN_ID));
        int indexFirstName = cursor.getColumnIndex(User.COLUMN_FIRST_NAME);
        String firstName = null;
        if (!cursor.isNull(indexFirstName)) {
            firstName = cursor.getString(indexFirstName);
        }
        int indexLastName = cursor.getColumnIndex(User.COLUMN_LAST_NAME);
        String lastName = null;
        if (!cursor.isNull(indexLastName)) {
            lastName = cursor.getString(indexLastName);
        }
        int indexGender = cursor.getColumnIndex(User.COLUMN_GENDER);
        Gender gender = null;
        if (!cursor.isNull(indexGender)) {
            gender = Gender.valueOf(cursor.getInt(indexGender));
        }
        int indexDate = cursor.getColumnIndex(User.COLUMN_DATE_OF_BIRTH);
        Date dateOfBirth = null;
        if (!cursor.isNull(indexDate)) {
            String date = cursor.getString(indexDate);
            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
            try {
                dateOfBirth = dateFormat.parse(date);
            } catch (ParseException e) {
                Log.e(TAG, "Error parsing user date of birth." + e);
            }
        }
        UserInterface user = new User(firstName, lastName, gender, dateOfBirth);
        user.setObjectID(objectID);

        return user;
    }

    @Override
    public long storeDiaryEntryAndAssociatedObjects(DiaryEntryInterface diaryEntry) {
        SQLiteDatabase db = this.getWritableDatabase();

        PainDescriptionInterface painDescription = diaryEntry.getPainDescription();
        long painDescriptionID = storePainDescription(painDescription);

        ContentValues values = new ContentValues();
        values.put(PainDescription.TABLE_NAME + "_id", painDescriptionID); //foreign key
        Date date = diaryEntry.getDate();
        if (date != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
            values.put(DiaryEntry.COLUMN_DATE, dateFormat.format(date));
        }
        if(diaryEntry.getCondition() != null) {
            values.put(DiaryEntry.COLUMN_CONDITION, diaryEntry.getCondition().getValue());
        }
        values.put(DiaryEntry.COLUMN_NOTES, diaryEntry.getNotes());
        long diaryEntryID = db.insert(DiaryEntry.TABLE_NAME, null, values);

        for (DrugIntakeInterface intake : diaryEntry.getDrugIntakes()) {
            intake.getDiaryEntry().setObjectID(diaryEntryID); //maybe get diary entry from database instead
            storeDrugIntakeAndAssociatedDrug(intake);
        }
        return diaryEntryID;
    }

    @Override
    public void updateDiaryEntryAndAssociatedObjects(DiaryEntryInterface diaryEntry) {
        SQLiteDatabase db = this.getWritableDatabase();
        PainDescriptionInterface painDescription = diaryEntry.getPainDescription();
        long painDescriptionID;
        if(painDescription.isPersistent()) {
            updatePainDescription(painDescription);
            painDescriptionID = painDescription.getObjectID();
        } else {
            painDescriptionID = storePainDescription(painDescription);
        }
        ContentValues values = new ContentValues();
        values.put(PainDescription.TABLE_NAME + "_id", painDescriptionID); //foreign key
        Date date = diaryEntry.getDate();
        if (date != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
            values.put(DiaryEntry.COLUMN_DATE, dateFormat.format(date));
        }
        if(diaryEntry.getCondition() != null) {
            values.put(DiaryEntry.COLUMN_CONDITION, diaryEntry.getCondition().getValue());
        } else {
            values.put(DiaryEntry.COLUMN_CONDITION, (Integer) null);
        }
        values.put(DiaryEntry.COLUMN_NOTES, diaryEntry.getNotes());
        db.update(DiaryEntry.TABLE_NAME, values, DiaryEntry.COLUMN_ID + " = ?",
                new String[]{String.valueOf(diaryEntry.getObjectID())});

        Set<DrugIntakeInterface> oldIntakes = getDrugIntakesForDiaryEntry(diaryEntry.getObjectID());
        Set<DrugIntakeInterface> newIntakes = diaryEntry.getDrugIntakes();
        Set<Long> newIntakeIDs = new HashSet<>();
        for(DrugIntakeInterface intake : newIntakes) {
            if(!intake.isPersistent()) {
                storeDrugIntakeAndAssociatedDrug(intake);
            } else {
                newIntakeIDs.add(intake.getObjectID());
                updateDrugIntakeAndAssociatedDrug(intake);
            }
        }
        //all drug intake objects that are no longer associated with the diary entry object are deleted
        for(DrugIntakeInterface intake : oldIntakes) {
            if(!newIntakeIDs.contains(intake.getObjectID())) {
                deleteDrugIntake(intake);
            }
        }
    }

    @Override
    public void deleteDiaryEntryAndAssociatedObjects(DiaryEntryInterface diaryEntry) {
        deletePainDescription(diaryEntry.getPainDescription());
        for(DrugIntakeInterface intake : diaryEntry.getDrugIntakes()) {
            deleteDrugIntake(intake);
        }
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DiaryEntry.TABLE_NAME, DiaryEntry.COLUMN_ID + " = ?",
                new String[]{Long.toString(diaryEntry.getObjectID())});
    }

    @Override
    public DiaryEntryInterface getDiaryEntryByID(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(DiaryEntry.TABLE_NAME, null, DiaryEntry.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        DiaryEntryInterface diaryEntry = null;
        if (cursor != null && cursor.moveToFirst()) {
            diaryEntry = instantiateDiaryEntryFromCursor(cursor);
        }
        cursor.close();

        return diaryEntry;
    }

    @Override
    public long getIDOfLatestDiaryEntry() {
        long ID = -1;
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT MAX(" + DiaryEntry.COLUMN_ID + ") FROM " + DiaryEntry.TABLE_NAME;
        Cursor cursor = db.rawQuery(selectQuery, new String[]{});

        Date date;
        if (cursor != null && cursor.moveToFirst()) {
            ID = cursor.getLong(0);
        }
        cursor.close();

        return ID;
    }

    @Override
    public DiaryEntryInterface getDiaryEntryByDate(Date date) {
        SQLiteDatabase db = this.getReadableDatabase();
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);

        Cursor cursor = db.query(DiaryEntry.TABLE_NAME, null, DiaryEntry.COLUMN_DATE + "=?",
                new String[]{dateFormat.format(date)}, null, null, null, null);

        DiaryEntryInterface diaryEntry = null;
        if (cursor != null && cursor.moveToFirst()) {
            do {
                diaryEntry = instantiateDiaryEntryFromCursor(cursor);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return diaryEntry;
    }

    @Override
    public List<DiaryEntryInterface> getDiaryEntriesByMonth(int month, int year) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MONTH, month - 1);
        c.set(Calendar.YEAR, year);
        c.set(Calendar.DAY_OF_MONTH, 1);
        Date startDate = c.getTime();
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date endDate = c.getTime();
        return getDiaryEntriesByTimeSpan(startDate, endDate);
    }

    @Override
    public List<DiaryEntryInterface> getDiaryEntriesByTimeSpan(Date startDate, Date endDate) {
        List<DiaryEntryInterface> diaryEntries = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);

        Cursor cursor = db.query(DiaryEntry.TABLE_NAME, null, "DATE(" + DiaryEntry.COLUMN_DATE + ") >= ? AND " + "DATE(" + DiaryEntry.COLUMN_DATE + ") <= ? ORDER BY DATE(" + DiaryEntry.COLUMN_DATE + ") ASC",
                new String[]{dateFormat.format(startDate), dateFormat.format(endDate)}, null, null, null, null);

        DiaryEntryInterface diaryEntry = null;
        if (cursor != null && cursor.moveToFirst()) {
            do {
                diaryEntry = instantiateDiaryEntryFromCursor(cursor);
                diaryEntries.add(diaryEntry);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return diaryEntries;
    }

    @Override
    public Set<Date> getDiaryEntryDatesByMonth(int month, int year) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MONTH, month - 1);
        c.set(Calendar.YEAR, year);
        c.set(Calendar.DAY_OF_MONTH, 1);
        Date startDate = c.getTime();
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date endDate = c.getTime();
        return getDiaryEntryDatesByTimeSpan(startDate, endDate);
    }

    @Override
    public Set<Date> getDiaryEntryDatesByTimeSpan(Date startDate, Date endDate) {
        Set<Date> dates = new HashSet<>();
        SQLiteDatabase db = this.getReadableDatabase();
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);


        String selectQuery = "SELECT " + DiaryEntry.COLUMN_DATE + " FROM " + DiaryEntry.TABLE_NAME + " WHERE DATE(" + DiaryEntry.COLUMN_DATE + ") >= ? AND " + "DATE(" + DiaryEntry.COLUMN_DATE + ") <= ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{dateFormat.format(startDate), dateFormat.format(endDate)});

        Date date;
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String dateAsString = cursor.getString(cursor.getColumnIndex(DiaryEntry.COLUMN_DATE));
                try {
                    date = dateFormat.parse(dateAsString);
                    dates.add(date);
                } catch (ParseException e) {
                    Log.e(TAG, "Error parsing diary entry date." + e);
                }
            } while (cursor.moveToNext());
        }

        return dates;
    }

    /**
     * Instantiates a diary entry object from the given cursor.
     *
     * @param cursor
     * @return diary entry object with associated pain description and drug intakes.
     */
    private DiaryEntryInterface instantiateDiaryEntryFromCursor(Cursor cursor) {
        long objectID = cursor.getLong(cursor.getColumnIndex(DiaryEntry.COLUMN_ID));
        int indexDate = cursor.getColumnIndex(DiaryEntry.COLUMN_DATE);
        Date dateOfEntry = null;
        if (!cursor.isNull(indexDate)) {
            String date = cursor.getString(indexDate);
            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
            try {
                dateOfEntry = dateFormat.parse(date);
            } catch (ParseException e) {
                Log.e(TAG, "Error parsing diary entry date." + e);
            }
        }
        DiaryEntryInterface diaryEntry = new DiaryEntry(dateOfEntry);
        diaryEntry.setObjectID(objectID);

        int indexNotes = cursor.getColumnIndex(DiaryEntry.COLUMN_NOTES);
        if (!cursor.isNull(indexNotes)) {
            if(!cursor.getString(indexNotes).isEmpty()) {
                diaryEntry.setNotes(cursor.getString(indexNotes));
            }
        }
        int indexCondition = cursor.getColumnIndex(DiaryEntry.COLUMN_CONDITION);
        if(!cursor.isNull(indexCondition)) {
            try {
                diaryEntry.setCondition(Condition.valueOf(cursor.getInt(indexCondition)));
            } catch (IllegalArgumentException e) {
                Log.d(TAG, "Illegal condition was saved to database.");
            }
        }

        long painDescriptionID = cursor.getLong(cursor.getColumnIndex(PainDescription.TABLE_NAME + "_id"));
        PainDescriptionInterface painDescription = getPainDescriptionByID(painDescriptionID);
        diaryEntry.setPainDescription(painDescription);

        Set<DrugIntakeInterface> intakes = getDrugIntakesForDiaryEntry(objectID);
        for(DrugIntakeInterface intake : intakes) {
            diaryEntry.addDrugIntake(intake);
        }

        return diaryEntry;
    }

    private long storePainDescription(PainDescriptionInterface painDescription) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PainDescription.COLUMN_PAIN_LEVEL, painDescription.getPainLevel());
        values.put(PainDescription.COLUMN_BODY_REGION, convertBodyRegionEnumSetToString(painDescription.getBodyRegions()));
        values.put(PainDescription.COLUMN_PAIN_QUALITY, convertPainQualityEnumSetToString(painDescription.getPainQualities()));
        values.put(PainDescription.COLUMN_TIME_OF_PAIN, convertTimeEnumSetToString(painDescription.getTimesOfPain()));
        return db.insert(PainDescription.TABLE_NAME, null, values);
    }

    private void updatePainDescription(PainDescriptionInterface painDescription) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PainDescription.COLUMN_PAIN_LEVEL, painDescription.getPainLevel());
        values.put(PainDescription.COLUMN_BODY_REGION, convertBodyRegionEnumSetToString(painDescription.getBodyRegions()));
        values.put(PainDescription.COLUMN_PAIN_QUALITY, convertPainQualityEnumSetToString(painDescription.getPainQualities()));
        values.put(PainDescription.COLUMN_TIME_OF_PAIN, convertTimeEnumSetToString(painDescription.getTimesOfPain()));
        db.update(PainDescription.TABLE_NAME, values, Drug.COLUMN_ID + " = ?",
                new String[]{String.valueOf(painDescription.getObjectID())});
    }

    private String convertBodyRegionEnumSetToString(EnumSet<BodyRegion> bodyRegions) {
        String bodyRegionsAsString = "";
        for(BodyRegion region : bodyRegions) {
            bodyRegionsAsString += region.getValue() + ",";
        }
        if(!bodyRegionsAsString.isEmpty()) {
            bodyRegionsAsString = bodyRegionsAsString.substring(0, bodyRegionsAsString.length() - 1);
        } else {
            bodyRegionsAsString = null;
        }
        return bodyRegionsAsString;
    }

    private EnumSet<BodyRegion> convertStringToBodyRegionEnumSet(String bodyRegionsAsString) {
        EnumSet<BodyRegion> bodyRegions = EnumSet.noneOf(BodyRegion.class);
        if(bodyRegionsAsString != null && !bodyRegionsAsString.isEmpty()) {
            String[] regions = bodyRegionsAsString.split(",");
            for (String bodyRegion : regions) {
                try {
                    BodyRegion r = BodyRegion.valueOf(Integer.parseInt(bodyRegion));
                    bodyRegions.add(r);
                } catch (IllegalArgumentException e) {
                    Log.d(TAG, "Error parsing body region.");
                }
            }
        }
        return bodyRegions;
    }

    private String convertPainQualityEnumSetToString(EnumSet<PainQuality> painQualities) {
        String painQualitiesAsString = "";
        for(PainQuality quality : painQualities) {
            painQualitiesAsString += quality.toString() + ",";
        }
        if(!painQualitiesAsString.isEmpty()) {
            painQualitiesAsString = painQualitiesAsString.substring(0, painQualitiesAsString.length() - 1);
        } else {
            painQualitiesAsString = null;
        }
        return painQualitiesAsString;
    }

    private EnumSet<PainQuality> convertStringToPainQualityEnumSet(String painQualitiesAsString) {
        EnumSet<PainQuality> painQualities = EnumSet.noneOf(PainQuality.class);
        if(painQualitiesAsString != null && !painQualitiesAsString.isEmpty()) {
            String[] qualities = painQualitiesAsString.split(",");
            for (String quality : qualities) {
                PainQuality q = PainQuality.fromString(quality);
                if (q != null) painQualities.add(q);
            }
        }
        return painQualities;
    }

    private String convertTimeEnumSetToString(EnumSet<Time> times) {
        String timesAsString = "";
        for(Time time : times) {
            timesAsString += time.toString() + ",";
        }
        if(!timesAsString.isEmpty()) {
            timesAsString = timesAsString.substring(0, timesAsString.length() - 1);
        } else {
            timesAsString = null;
        }
        return timesAsString;
    }

    private EnumSet<Time> convertStringToTimeEnumSet(String timesAsString) {
        EnumSet<Time> timesOfPain = EnumSet.noneOf(Time.class);
        if(timesAsString != null && !timesAsString.isEmpty()) {
            String[] times = timesAsString.split(",");
            for (String time : times) {
                Time t = Time.fromString(time);
                if (t != null) timesOfPain.add(t);
            }
        }
        return timesOfPain;
    }

    private void deletePainDescription(PainDescriptionInterface painDescription) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(PainDescription.TABLE_NAME, PainDescription.COLUMN_ID + " = ?",
                new String[]{Long.toString(painDescription.getObjectID())});
    }

    private PainDescriptionInterface getPainDescriptionByID(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(PainDescription.TABLE_NAME, null, PainDescription.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        PainDescriptionInterface painDescription = null;
        if (cursor != null && cursor.moveToFirst()) {
            painDescription = instantiatePainDescriptionFromCursor(cursor);
        }
        cursor.close();

        return painDescription;
    }

    private PainDescriptionInterface instantiatePainDescriptionFromCursor(Cursor cursor) {
        long objectID = cursor.getLong(cursor.getColumnIndex(PainDescription.COLUMN_ID));
        int painLevel = cursor.getInt(cursor.getColumnIndex(PainDescription.COLUMN_PAIN_LEVEL));

        String bodyRegionsAsString = cursor.getString(cursor.getColumnIndex(PainDescription.COLUMN_BODY_REGION));
        String painQualitiesAsString = cursor.getString(cursor.getColumnIndex(PainDescription.COLUMN_PAIN_QUALITY));
        String timesAsString = cursor.getString(cursor.getColumnIndex(PainDescription.COLUMN_TIME_OF_PAIN));
        EnumSet<BodyRegion> bodyRegions = convertStringToBodyRegionEnumSet(bodyRegionsAsString);
        EnumSet<PainQuality> painQualities = convertStringToPainQualityEnumSet(painQualitiesAsString);
        EnumSet<Time> timesOfPain = convertStringToTimeEnumSet(timesAsString);
        PainDescriptionInterface painDescription = new PainDescription(painLevel, bodyRegions, painQualities, timesOfPain);
        painDescription.setObjectID(objectID);
        return painDescription;
    }

    /**
     * Stores the given drug intake and the associated drug (if not yet persistent).
     *
     * @param intake intake to be stored; associated diary entry object has to be persistent (objectID must be set)
     * @return
     */
    private long storeDrugIntakeAndAssociatedDrug(DrugIntakeInterface intake) {
        SQLiteDatabase db = this.getWritableDatabase();
        DrugInterface drug = intake.getDrug();
        long drugID;
        //checks whether a drug object with the same name and dose already exists in the database
        DrugInterface persistentDrug = getDrugByNameAndDose(drug.getName(), drug.getDose());
        if(persistentDrug != null) {
            drugID = persistentDrug.getObjectID();
        } else { //stores the drug if it does not exist yet
            drugID = storeDrug(drug);
        }
        ContentValues values = getDrugIntakeContentValues(intake);
        values.put(Drug.TABLE_NAME + "_id", drugID);
        values.put(DiaryEntry.TABLE_NAME + "_id", intake.getDiaryEntry().getObjectID());
        return db.insert(DrugIntake.TABLE_NAME, null, values);
    }

    /**
     * Updates the given drug intake and the associated drug.
     *
     * @param intake drug intake to update; must be persistent (see {@link DrugIntakeInterface#isPersistent()})
     */
    private void updateDrugIntakeAndAssociatedDrug(DrugIntakeInterface intake) {
        SQLiteDatabase db = this.getWritableDatabase();
        DrugInterface drug = intake.getDrug();
        ContentValues values = getDrugIntakeContentValues(intake);

        DrugIntakeInterface oldIntake = getDrugIntakeByID(intake.getObjectID());
        DrugInterface oldDrug = oldIntake.getDrug();

        boolean drugUpdated = false;
        if(drug != oldDrug) { //associated drug has been updated
            drugUpdated = true;
            long drugID;

            //checks whether a drug object with the same name and dose already exists in the database
            DrugInterface persistentDrug = getDrugByNameAndDose(drug.getName(), drug.getDose());
            if(persistentDrug != null) {
                drugID = persistentDrug.getObjectID();
            } else { //stores the drug if it does not exist yet
                drugID = storeDrug(drug);
            }
            values.put(Drug.TABLE_NAME + "_id", drugID);
        }
        db.update(DrugIntake.TABLE_NAME, values, DrugIntake.COLUMN_ID + " = ?",
                new String[]{String.valueOf(intake.getObjectID())});
        if(drugUpdated) {
            deleteDrug(oldDrug); //drug is deleted if not referenced anymore
        }
    }

    /**
     * Returns a ContentValues object containing all non-foreign key columns and values.
     *
     * @param intake
     * @return
     */
    private ContentValues getDrugIntakeContentValues(DrugIntakeInterface intake) {
        ContentValues values = new ContentValues();
        values.put(DrugIntake.COLUMN_MORNING, intake.getQuantityMorning());
        values.put(DrugIntake.COLUMN_NOON, intake.getQuantityNoon());
        values.put(DrugIntake.COLUMN_EVENING, intake.getQuantityEvening());
        values.put(DrugIntake.COLUMN_NIGHT, intake.getQuantityNight());
        return values;
    }

    /**
     * Deletes the given drug intake object. The associated drug is deleted from the database if there are no more references to it.
     *
     * @param intake drug intake to delete
     */
    private void deleteDrugIntake(DrugIntakeInterface intake) {
        SQLiteDatabase db = this.getWritableDatabase();
        DrugInterface drug = intake.getDrug();
        db.delete(DrugIntake.TABLE_NAME, DrugIntake.COLUMN_ID + " = ?",
                new String[]{Long.toString(intake.getObjectID())});
        if(drug != null && drug.isPersistent()) {
            deleteDrug(drug);
        }
    }

    private DrugIntakeInterface getDrugIntakeByID(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(DrugIntake.TABLE_NAME, null, DrugIntake.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        DrugIntakeInterface drugIntake = null;
        if (cursor != null && cursor.moveToFirst()) {
            drugIntake = instantiateDrugIntakeFromCursor(cursor);
        }
        cursor.close();

        return drugIntake;
    }

    @Override
    public Set<DrugIntakeInterface> getDrugIntakesForDiaryEntry(long diaryEntryID) {
        Set<DrugIntakeInterface> intakes = new HashSet<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + DrugIntake.TABLE_NAME + " WHERE " + DiaryEntry.TABLE_NAME + "_id = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(diaryEntryID)});

        DrugIntakeInterface intake = null;

        if (cursor != null && cursor.moveToFirst()) {
            do {
                intake = instantiateDrugIntakeFromCursor(cursor);
                intakes.add(intake);
            } while (cursor.moveToNext());
        }

        return intakes;
    }

    /**
     * Instantiates a drug intake object from the given cursor and attaches the associated drug object. Diary entry field is not set!
     *
     * @param cursor
     * @return
     */
    private DrugIntakeInterface instantiateDrugIntakeFromCursor(Cursor cursor) {
        long objectID = cursor.getLong(cursor.getColumnIndex(DrugIntake.COLUMN_ID));
        int quantityMorning = cursor.getInt(cursor.getColumnIndex(DrugIntake.COLUMN_MORNING));
        int quantityNoon = cursor.getInt(cursor.getColumnIndex(DrugIntake.COLUMN_NOON));
        int quantityEvening = cursor.getInt(cursor.getColumnIndex(DrugIntake.COLUMN_EVENING));
        int quantityNight = cursor.getInt(cursor.getColumnIndex(DrugIntake.COLUMN_NIGHT));
        long drugID = cursor.getLong(cursor.getColumnIndex(Drug.TABLE_NAME + "_id"));
        DrugInterface drug = getDrugByID(drugID);
        DrugIntakeInterface intake = new DrugIntake(drug, quantityMorning, quantityNoon, quantityEvening, quantityNight);
        intake.setObjectID(objectID);
        return intake;
    }

    @Override
    public long storeDrug(DrugInterface drug) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Drug.COLUMN_NAME, drug.getName());
        values.put(Drug.COLUMN_DOSE, drug.getDose());
        values.put(Drug.COLUMN_CURRENTLY_TAKEN, drug.isCurrentlyTaken());

        long id = db.insert(Drug.TABLE_NAME, null, values);
        Log.d(TAG, "Created drug.");

        return id;
    }

    @Override
    public void updateDrug(DrugInterface drug) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Drug.COLUMN_NAME, drug.getName());
        values.put(Drug.COLUMN_DOSE, drug.getDose());
        values.put(Drug.COLUMN_CURRENTLY_TAKEN, drug.isCurrentlyTaken());

        db.update(Drug.TABLE_NAME, values, Drug.COLUMN_ID + " = ?",
                new String[]{String.valueOf(drug.getObjectID())});
    }

    @Override
    public void deleteDrug(DrugInterface drug) {
        SQLiteDatabase database = this.getWritableDatabase();
        String selectQuery = "SELECT COUNT(*) FROM " + DrugIntake.TABLE_NAME + " WHERE " + Drug.TABLE_NAME + "_id = ?";
        Cursor cursor = database.rawQuery(selectQuery, new String[]{Long.toString(drug.getObjectID())});

        int count = -1;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        if (count == 0) {
            Log.d(TAG, "Drug deleted.");
            database.delete(Drug.TABLE_NAME, Drug.COLUMN_ID + " = ?",
                    new String[]{Long.toString(drug.getObjectID())});
        }
    }

    @Override
    public DrugInterface getDrugByID(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Drug.TABLE_NAME, null, Drug.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        DrugInterface drug = null;
        if (cursor != null && cursor.moveToFirst()) {
            drug = instantiateDrugFromCursor(cursor);
        }
        cursor.close();

        return drug;
    }

    @Override
    public DrugInterface getDrugByNameAndDose(String name, String dose) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor;
        if(dose == null) {
            cursor = db.query(Drug.TABLE_NAME, null, Drug.COLUMN_NAME + " = ? AND " + Drug.COLUMN_DOSE + " IS NULL",
                    new String[]{name}, null, null, null, null);
        } else {
            cursor = db.query(Drug.TABLE_NAME, null, Drug.COLUMN_NAME + " = ? AND " + Drug.COLUMN_DOSE + " = ?",
                    new String[]{name, dose}, null, null, null, null);
        }

        DrugInterface drug = null;
        if (cursor != null && cursor.moveToFirst()) {
            drug = instantiateDrugFromCursor(cursor);
            cursor.close();
        }

        return drug;
    }

    @Override
    public List<DrugInterface> getAllDrugs() {
        List<DrugInterface> drugs = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + Drug.TABLE_NAME;

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        DrugInterface drug = null;

        if (cursor != null && cursor.moveToFirst()) {
            do {
                drug = instantiateDrugFromCursor(cursor);
                drugs.add(drug);
            } while (cursor.moveToNext());
        }

        return drugs;
    }

    @Override
    public List<DrugInterface> getAllCurrentlyTakenDrugs() {
        List<DrugInterface> drugs = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + Drug.TABLE_NAME + "WHERE " + Drug.COLUMN_CURRENTLY_TAKEN + " = 1";

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        DrugInterface drug = null;

        if (cursor != null && cursor.moveToFirst()) {
            do {
                drug = instantiateDrugFromCursor(cursor);
                drugs.add(drug);
            } while (cursor.moveToNext());
        }

        return drugs;
    }

    private DrugInterface instantiateDrugFromCursor(Cursor cursor) {
        long objectID = cursor.getLong(cursor.getColumnIndex(Drug.COLUMN_ID));
        String name = cursor.getString(cursor.getColumnIndex(Drug.COLUMN_NAME));
        int indexDose = cursor.getColumnIndex(Drug.COLUMN_DOSE);
        String dose = null;
        if (!cursor.isNull(indexDose)) {
            dose = cursor.getString(indexDose);
        }
        int currentlyTakenInt = cursor.getInt(cursor.getColumnIndex(Drug.COLUMN_CURRENTLY_TAKEN));
        boolean currentlyTaken = true;
        DrugInterface drug = new Drug(name, dose);
        drug.setObjectID(objectID);
        if (currentlyTakenInt == 0) currentlyTaken = false;
        drug.setCurrentlyTaken(currentlyTaken);

        return drug;
    }

}
