package bo.sqlite;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

import bo.dbConstantsMap;
import bo.entity.NbLogSearch;

public class NbLogSearchSQLite {
  public static long insert(final NbLogSearch NbLogSearch) {
        return dbConstantsMap.appDB.NbLogSearchDao().insert(NbLogSearch);
//        new AsyncTask<Void, Void, Integer>() {
//            @Override
//            protected Integer doInBackground(Void... voids) {
//                return dbConstantsMap.appDB.NbLogSearchDao().insert(NbLogSearch);
//                //return null;
//            }
//        }.execute();
    }

    public static void update(final NbLogSearch NbLogSearch) {
        //NbLogSearch.setModifiedAt(AppUtils.getCurrentDateTime());

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                dbConstantsMap.appDB.NbLogSearchDao().update(NbLogSearch);
                return null;
            }
        }.execute();
    }
    public static void deleteAll() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                dbConstantsMap.appDB.NbLogSearchDao().deleteAll();
                return null;
            }
        }.execute();
    }

    public static void delete(final NbLogSearch poi) {
        dbConstantsMap.appDB.NbLogSearchDao().delete(poi);
//        final LiveData<NbLogSearch> task = selectLive(id);
//        if(task != null) {
//            new AsyncTask<Void, Void, Void>() {
//                @Override
//                protected Void doInBackground(Void... voids) {
//                    dbConstantsMap.appDB.NbLogSearchDao().delete(task.getValue());
//                    return null;
//                }
//            }.execute();
//        }
    }

    public static void deleteTask(final NbLogSearch NbLogSearch) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                dbConstantsMap.appDB.NbLogSearchDao().delete(NbLogSearch);
                return null;
            }
        }.execute();
    }

    public static LiveData<NbLogSearch> selectLive(long id) {
        return dbConstantsMap.appDB.NbLogSearchDao().selectLive(id);
    }
    public static NbLogSearch select(long id) {
        return dbConstantsMap.appDB.NbLogSearchDao().select(id);
    }


    public static LiveData<List<NbLogSearch>> selectRowsLive(String filter) {
        return dbConstantsMap.appDB.NbLogSearchDao().selectRowsLive(filter);
    }
    public static List<NbLogSearch> selectRows(String filter) {
        return dbConstantsMap.appDB.NbLogSearchDao().selectRows(filter);
    }
    public static LiveData<List<NbLogSearch>> selectAllLive() {return dbConstantsMap.appDB.NbLogSearchDao().selectAllLive();}
    public static List<NbLogSearch> selectAll() {return dbConstantsMap.appDB.NbLogSearchDao().selectAll();}
}
