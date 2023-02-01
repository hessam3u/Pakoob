package bo.sqlite;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

import bo.dbConstantsMap;
import bo.dbConstantsTara;
import bo.entity.NbAdv;

public class NbAdvSQLite {
    public static long insert(final NbAdv NbAdv) {
        return dbConstantsTara.appDB.NbAdvDao().insert(NbAdv);
//        new AsyncTask<Void, Void, Integer>() {
//            @Override
//            protected Integer doInBackground(Void... voids) {
//                return dbConstantsTara.appDB.NbAdvDao().insert(NbAdv);
//                //return null;
//            }
//        }.execute();
    }

    public static void update(final NbAdv NbAdv) {
        //NbAdv.setModifiedAt(AppUtils.getCurrentDateTime());

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                dbConstantsTara.appDB.NbAdvDao().update(NbAdv);
                return null;
            }
        }.execute();
    }
    public static void deleteAll() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                dbConstantsTara.appDB.NbAdvDao().deleteAll();
                return null;
            }
        }.execute();
    }

    public static void delete(final NbAdv poi) {
        dbConstantsTara.appDB.NbAdvDao().delete(poi);
//        final LiveData<NbAdv> task = selectLive(id);
//        if(task != null) {
//            new AsyncTask<Void, Void, Void>() {
//                @Override
//                protected Void doInBackground(Void... voids) {
//                    dbConstantsTara.appDB.NbAdvDao().delete(task.getValue());
//                    return null;
//                }
//            }.execute();
//        }
    }

    public static void deleteTask(final NbAdv NbAdv) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                dbConstantsTara.appDB.NbAdvDao().delete(NbAdv);
                return null;
            }
        }.execute();
    }

    public static LiveData<NbAdv> selectLive(long id) {
        return dbConstantsTara.appDB.NbAdvDao().selectLive(id);
    }
    public static NbAdv select(long id) {
        return dbConstantsTara.appDB.NbAdvDao().select(id);
    }


    public static LiveData<List<NbAdv>> selectRowsLive(String filter) {
        return dbConstantsTara.appDB.NbAdvDao().selectRowsLive(filter);
    }
    public static List<NbAdv> selectRows(String filter) {
        return dbConstantsTara.appDB.NbAdvDao().selectRows(filter);
    }
    public static LiveData<List<NbAdv>> selectAllLive() {return dbConstantsTara.appDB.NbAdvDao().selectAllLive();}
    public static List<NbAdv> selectAll() {return dbConstantsTara.appDB.NbAdvDao().selectAll();}

    //مخصوص این فرم
    public static List<NbAdv> selectAllForHome() {return dbConstantsTara.appDB.NbAdvDao().selectAllForHome();}

}
