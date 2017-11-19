package org.secuso.privacyfriendlyexample.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import org.secuso.privacyfriendlyexample.database.entities.enums.Gender;
import org.secuso.privacyfriendlyexample.database.entities.impl.AbstractPersistentObject;
import org.secuso.privacyfriendlyexample.database.entities.impl.DiaryEntry;
import org.secuso.privacyfriendlyexample.database.entities.impl.Drug;
import org.secuso.privacyfriendlyexample.database.entities.impl.PainDescription;
import org.secuso.privacyfriendlyexample.database.entities.impl.User;
import org.secuso.privacyfriendlyexample.database.entities.interfaces.UserInterface;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Karola Marky, Susanne Felsen
 * @version 20171118
 * Structure based on http://www.androidhive.info/2011/11/android-sqlite-database-tutorial/ (last access: 18.11.17)
 *
 * This class defines the structure of our database.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "paindiary";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private void createAll(SQLiteDatabase db) {
        db.execSQL(User.TABLE_CREATE);
        db.execSQL(Drug.TABLE_CREATE);
        db.execSQL(PainDescription.TABLE_CREATE);
        db.execSQL(DiaryEntry.TABLE_CREATE);
        db.execSQL(DiaryEntry.TABLE_ASSOCIATIVE_CREATE);
        Log.d("DATABASE HANDLER","Database initialized!");
        Calendar c = Calendar.getInstance();
        c.set(2010, 2, 7);
        long id = createUser(db, "Max", "Mustermann", Gender.MALE, c.getTime());
        UserInterface user = getUserByID(db, id);
        Log.d("DATABASE HANDLER", user.getFirstName() + ", " + user.getLastName() + ", " + user.getGender() + ", " + user.getDateOfBirth().toString());
    }

    private void dropAll(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + User.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Drug.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PainDescription.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DiaryEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DiaryEntry.TABLE_ASSOCIATIVE_NAME);
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

    public long createUser(SQLiteDatabase db, String firstName, String lastName, Gender gender, Date dateOfBirth) {
        ContentValues values = new ContentValues();
        values.put(User.COLUMN_FIRST_NAME, firstName);
        values.put(User.COLUMN_LAST_NAME, lastName);
        values.put(User.COLUMN_GENDER, gender.getValue());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        values.put(User.COLUMN_DATE_OF_BIRTH, dateFormat.format(dateOfBirth));

        long id = db.insert(User.TABLE_NAME, null, values);
//        db.close();
        Log.d("DATABASE HANDLER","User created");

        return id;
    }

    public long updateUser(SQLiteDatabase db, UserInterface user) {
//        if(!user.isPersistent()) {
//            throw new IllegalArgumentException();
//        }

        ContentValues values = new ContentValues();
        values.put(User.COLUMN_FIRST_NAME, user.getFirstName());
        values.put(User.COLUMN_LAST_NAME, user.getLastName());
        values.put(User.COLUMN_GENDER, user.getGender().getValue());
        Date dateOfBirth = user.getDateOfBirth();
        if(dateOfBirth != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            values.put(User.COLUMN_DATE_OF_BIRTH, dateFormat.format(dateOfBirth));
        }
        return db.update(User.TABLE_NAME, values, User.COLUMN_ID + " = ?",
                new String[] { String.valueOf(user.getObjectID()) });
    }

    public UserInterface getUserByID(SQLiteDatabase db, long id) {
//        Log.d("DATABASE", Integer.toString(id));

        Cursor cursor = db.query(User.TABLE_NAME, null, User.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        UserInterface user = null;
        if( cursor != null && cursor.moveToFirst() ){
            long objectID = cursor.getLong(cursor.getColumnIndex(User.COLUMN_ID));

            int indexFirstName = cursor.getColumnIndex(User.COLUMN_FIRST_NAME);
            String firstName = null;
            if(!cursor.isNull(indexFirstName)) {
                firstName = cursor.getString(indexFirstName);
            }

            int indexLastName = cursor.getColumnIndex(User.COLUMN_LAST_NAME);
            String lastName = null;
            if(!cursor.isNull(indexLastName)) {
                lastName = cursor.getString(indexLastName);
            }

            int indexGender = cursor.getColumnIndex(User.COLUMN_GENDER);
            Gender gender = null;
            if(!cursor.isNull(indexGender)) {
                gender = Gender.valueOf(cursor.getInt(indexGender));
            }

            int indexDate = cursor.getColumnIndex(User.COLUMN_DATE_OF_BIRTH);
            Date dateOfBirth = null;
            if(!cursor.isNull(indexDate)) {
                String date = cursor.getString(cursor.getColumnIndex(User.COLUMN_DATE_OF_BIRTH));
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                try {
                    dateOfBirth = dateFormat.parse(date);
                } catch (ParseException e) {
                    Log.d("DATABASE HANDLER", "Error parsing date of birth.");
                }
            }

            user = new User(objectID, firstName, lastName, gender, dateOfBirth);
            cursor.close();
        }

        return user;
    }
}
