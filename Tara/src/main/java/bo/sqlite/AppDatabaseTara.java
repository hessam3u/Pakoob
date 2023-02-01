package bo.sqlite;

import android.content.Context;
import android.media.projection.MediaProjection;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import bo.entity.CityDTO;
import bo.entity.FmMessage;
import bo.entity.FmSides;
import bo.entity.NbAdv;
import bo.entity.TTClubNameDTO;
import bo.entity.TTClubTour;
import bo.entity.TTClubTourCategoryDTO;
import bo.entity.TTExceptionLog;

@Database(entities = {FmMessage.class, FmSides.class,TTClubTourCategoryDTO.class, CityDTO.class, TTClubNameDTO.class, TTClubTour.class, NbAdv.class}, version = 5, exportSchema = true)
public abstract class AppDatabaseTara extends RoomDatabase {
    public static String DATABASE_NAME = "PakoobDB1";
    private static AppDatabaseTara INSTANCE;

    public abstract FmMessageDao FmMessageDao();
    public abstract FmSidesDao FmSidesDao();
    public abstract TTClubTourCategoryDao TTClubTourCategoryDao();
    public abstract CityDao CityDao();
    public abstract TTClubNameDao TTClubNameDao();
    public abstract TTClubTourDao TTClubTourDao();
    public abstract NbAdvDao NbAdvDao();

    public static AppDatabaseTara getDatabase(Context context) {
        if (INSTANCE == null) {
            try {
                CreateDB(context);
            }
            catch (Exception ex){
//                Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG);
                //1400-04-16 اضافه کردم
                if (Build.VERSION.SDK_INT < 24){
                    context.deleteDatabase(DATABASE_NAME);
                    CreateDB(context);
                }
            }
        }
        return INSTANCE;
    }
    public static void CreateDB(Context context){
        INSTANCE =
                Room.databaseBuilder(context, AppDatabaseTara.class, DATABASE_NAME)
//Room.inMemoryDatabaseBuilder(context.getApplicationContext(), AppDatabase.class)
                        // To simplify the exercise, allow queries on the main thread.
                        // Don't do this on a real app!
                        .allowMainThreadQueries()
                        //.addMigrations(MIGRATION_1_2)//1400-01-16 مجبور شدم اینم به فنا بد و همه چی رو از اول بسازم
                        //.addMigrations(MIGRATION_2_3)//1400-01-16 این که اصلا کار نکرد هی گیر داد
                        .fallbackToDestructiveMigrationFrom(1) //1400-01-16 مجبور شدم
                        .fallbackToDestructiveMigrationFrom(2) //1400-01-16 مجبور شدم
                        .fallbackToDestructiveMigrationFrom(3) //1400-01-16 مجبور شدم
                        .fallbackToDestructiveMigrationFrom(4) //1400-10-19 مجبور شدم
                        // recreate the database if necessary
                        //.fallbackToDestructiveMigration()
                        .build();
    }
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("Alter TABLE 'CityDTO' Add Column 'ProvinceName' TEXT");
        }
    };
    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            try {
                database.execSQL("Drop TABLE 'FmMessage'");
            }catch (Exception ex){
                Log.e("خطای میگ", ex.getMessage());
                ex.printStackTrace();
            }
            database.execSQL("CREATE TABLE 'FmMessage' ('FmMessageId' INTEGER" +
                    ", 'CCustomerIdSend' INTEGER" +
                    ", 'RecieverId' INTEGER" +
                    ", 'AnonymosType' INTEGER" +
                    ", 'CCustomerNameSend' Text" +
                    ", 'RecType' INTEGER" +
                    ", 'FmMessageType' INTEGER" +
                    ", 'OpenAction' INTEGER" +
                    ", 'ActionParam' Text" +
                    ", 'SendDate' Text" +
                    ", 'Text1' Text" +
                    ", 'HasTextAttach' INTEGER" +
                    ", 'HasAttach' INTEGER" +
                    ", 'Status' INTEGER" +
                    ", 'ReplyId' INTEGER" +
                    ", 'FwdId' INTEGER" +
                    ", 'EditDate' Text" +
                    ", 'ContentType' INTEGER" +
                    ", 'ContentCat' INTEGER" +
                    ", 'AlarmPriority' INTEGER" +
                    ", 'ExtMessageId' INTEGER" +
                    ", PRIMARY KEY('FmMessageId'))");
            database.execSQL("CREATE INDEX index_FmMessage_FmMessageId ON  FmMessage(FmMessageId)");

//            try {
//                database.execSQL("Drop TABLE 'FmSides'");
//            }catch (Exception ex){
//                Log.e("خطای میگ2", ex.getMessage());
//                ex.printStackTrace();
//            }
//            database.execSQL("CREATE TABLE 'FmSides' ('IdLocal' INTEGER" +
//                    ", 'Name' Text" +
//                    ", 'Avatar' Text" +
//                    ", 'Username' Text" +
//                    ", 'UserType' INTEGER" +
//                    ", 'FmSidesId' INTEGER" +
//                    ", 'UserSide1' INTEGER" +
//                    ", 'UserSide2' INTEGER" +
//                    ", 'FmChanalId' INTEGER" +
//                    ", 'AnonymosType' INTEGER" +
//                    ", 'FmMessageIdLast' INTEGER" +
//                    ", 'Text1' TEXT" +
//                    ", 'LastDate' TEXT" +
//                    ", 'LastSenderSide' INTEGER" +
//                    ", 'UnreadCount' INTEGER" +
//                    ", 'RepLastContentTypelyId' INTEGER" +
//                    ", PRIMARY KEY('IdLocal'))");
//            database.execSQL("CREATE INDEX index_FmSides_IdLocal ON  FmSides(IdLocal)");
        }
    };

    public static void destroyInstance() {
        INSTANCE = null;
    }
}