package be.pxl.citygame.data.database;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;

/**
 * Created by Christina on 3/02/2015.
 */
public class GameDbHelper extends SQLiteOpenHelper {

    public GameDbHelper(Context ctx) {
        super(ctx, GameDB.DB_NAME, null, GameDB.DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlCreate = "CREATE TABLE " + GameDB.Games.TABLE_NAME + " (" +
                GameDB.Games._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                GameDB.Games.COL_GID + " INTEGER UNIQUE, " +
                GameDB.Games.COL_TITLE + " TEXT, " +
                GameDB.Games.COL_COMPLETED + " INTEGER DEFAULT 0, " +
                GameDB.Games.COL_SCORE + " INTEGER); ";
        String sqlCreate2 = "CREATE TABLE " + GameDB.Questions.TABLE_NAME + " (" +
                GameDB.Questions._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                GameDB.Questions.COL_QID + " INTEGER, " +
                GameDB.Questions.COL_GID + " INTEGER, " +
                GameDB.Questions.COL_QUESTION + " TEXT, " +
                GameDB.Questions.COL_TEXT_ANSWER + " TEXT, " +
                GameDB.Questions.COL_MULTI_ANSWER + " INTEGER, " +
                GameDB.Questions.COL_PLACENAME + " TEXT, " +
                GameDB.Questions.COL_EXTRAINFO + " TEXT, " +
                GameDB.Questions.COL_LOCALURL + " TEXT, " +
                GameDB.Questions.COL_LATITUDE + " REAL, " +
                GameDB.Questions.COL_LONGITUDE + " REAL, " +
                GameDB.Questions.COL_ANSWERED + " INTEGER, " +
                GameDB.Questions.COL_ANSWERED_CORRECT + " INTEGER, " +
                GameDB.Questions.COL_ANSWERED_CONTENT + " TEXT, " +
                "CONSTRAINT unq UNIQUE (" + GameDB.Questions.COL_QID + ", " + GameDB.Questions.COL_GID + "));";

        db.execSQL(sqlCreate);
        db.execSQL(sqlCreate2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // No upgrades yet
    }
}
