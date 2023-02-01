package bo.sqlite;
import android.content.Context;

import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import bo.entity.NbCurrentTrack;
import bo.entity.NbLogSearch;
import bo.entity.NbMap;
import bo.entity.NbPoi;
import bo.entity.TTExceptionLog;

@Database(entities = {TTExceptionLog.class, NbMap.class, NbPoi.class, NbCurrentTrack.class, NbLogSearch.class}, version = 5
        ,  exportSchema = true)
public abstract class AppDatabaseMap extends RoomDatabase {
    public static String DATABASE_NAME = "NaghsheBazDB";
    private static AppDatabaseMap INSTANCE;

    public abstract TTExceptionLogDao TTExceptionLogDao();
    public abstract NbMapDao NbMapDao();
    public abstract NbPoiDao NbPoiDao();
    public abstract NbCurrentTrackDao NbCurrentTrackDao();
    public abstract NbLogSearchDao NbLogSearchDao();

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE 'NbPoi' (NbPoiId INTEGER PRIMARY KEY AUTOINCREMENT, 'Name' TEXT NOT NULL"+
                    ", 'Level' INTEGER NOT NULL, 'ParentId' INTEGER NOT NULL, 'Address' TEXT NOT NULL, 'LatS' REAL NOT NULL, 'LonW' REAL NOT NULL, 'LatN' REAL NOT NULL, 'LonE' REAL NOT NULL" +
                    ", 'Color' INTEGER NOT NULL, 'ShowStatus' INTEGER NOT NULL, 'PoiType' INTEGER NOT NULL, 'ServerId' INTEGER NOT NULL, 'CreatorType' INTEGER NOT NULL, 'ValidityLevel' INTEGER NOT NULL" +
                    ", 'ZoomMin' INTEGER NOT NULL, 'ZoomMax' INTEGER NOT NULL, 'ActivityType' INTEGER NOT NULL, 'DisplaySize' INTEGER NOT NULL" +
                    ")");
        }
    };
    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE 'NbCurrentTrack' (NbCurrentTrackId INTEGER PRIMARY KEY AUTOINCREMENT, 'Latitude' REAL NOT NULL"+
                    ", 'Longitude' REAL NOT NULL, 'Time' INTEGER NOT NULL, 'Elevation' REAL NOT NULL" +
                    ")");
        }
    };
    //for ver 27 to 28 at 1401-03-03
    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE 'NbMap' " + " ADD COLUMN 'MapCategory' INTEGER NOT NULL DEFAULT 1");
            database.execSQL("ALTER TABLE 'NbMap' " + " ADD COLUMN 'MapType' INTEGER NOT NULL DEFAULT 1");
            database.execSQL("ALTER TABLE 'NbMap' " + " ADD COLUMN 'PreviewImage' TEXT NOT NULL DEFAULT ''");
            database.execSQL("ALTER TABLE 'NbPoi' " + " ADD COLUMN 'Priority' INTEGER NOT NULL DEFAULT 1");
            database.execSQL("ALTER TABLE 'NbPoi' " + " ADD COLUMN 'AltName' TEXT NOT NULL DEFAULT ''");
            database.execSQL("ALTER TABLE 'NbPoi' " + " ADD COLUMN 'LatBegin' REAL NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE 'NbPoi' " + " ADD COLUMN 'LonBegin' REAL NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE 'NbPoi' " + " ADD COLUMN 'addedInfo' TEXT NOT NULL DEFAULT ''");
            database.execSQL("ALTER TABLE 'NbPoi' " + " ADD COLUMN 'IsPolygon' INTEGER NOT NULL DEFAULT 2");
            database.execSQL("ALTER TABLE 'NbPoi' " + " ADD COLUMN 'Elevation' INTEGER NOT NULL DEFAULT 0");
        }
    };

    //for ver 28 to 29 at 1401-05-10
    static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE 'NbLogSearch' ('NbLogSearchId' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL"+
                    ", 'NbCommandType' INTEGER NOT NULL  DEFAULT 0, 'SearchText' TEXT NOT NULL  DEFAULT '', 'Lat' REAL NOT NULL DEFAULT 0, 'Lon' REAL NOT NULL DEFAULT 0" +
                    ", 'RelatedId' INTEGER NOT NULL  DEFAULT 0, 'SearchDateStr' TEXT NOT NULL  DEFAULT ''" +
                    ")");
        }
    };

    public static AppDatabaseMap getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context, AppDatabaseMap.class, DATABASE_NAME)
//Room.inMemoryDatabaseBuilder(context.getApplicationContext(), AppDatabase.class)
                            // To simplify the exercise, allow queries on the main thread.
                            // Don't do this on a real app!
                            .allowMainThreadQueries()
                            .addMigrations(MIGRATION_1_2)
                            .addMigrations(MIGRATION_2_3)
                            .addMigrations(MIGRATION_3_4)
                            .addMigrations(MIGRATION_4_5)
                            // recreate the database if necessary
                            .fallbackToDestructiveMigration()
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}