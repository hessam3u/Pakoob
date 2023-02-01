package bo.sqlite;

import android.os.AsyncTask;

import java.util.List;

import androidx.lifecycle.LiveData;
import bo.entity.NbCurrentTrack;
import bo.dbConstantsMap;

public class NbCurrentTrackSQLite {
    public static void insert(final NbCurrentTrack NbCurrentTrack) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                dbConstantsMap.appDB.NbCurrentTrackDao().insert(NbCurrentTrack);
                return null;
            }
        }.execute();
    }

    public static void update(final NbCurrentTrack NbCurrentTrack) {
        //NbCurrentTrack.setModifiedAt(AppUtils.getCurrentDateTime());

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                dbConstantsMap.appDB.NbCurrentTrackDao().update(NbCurrentTrack);
                return null;
            }
        }.execute();
    }
    public static void deleteAll() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                dbConstantsMap.appDB.NbCurrentTrackDao().deleteAll();
                return null;
            }
        }.execute();
    }

    public static void delete(final int id) {
        final LiveData<NbCurrentTrack> task = selectLive(id);
        if(task != null) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    dbConstantsMap.appDB.NbCurrentTrackDao().delete(task.getValue());
                    return null;
                }
            }.execute();
        }
    }

    public static void deleteTask(final NbCurrentTrack NbCurrentTrack) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                dbConstantsMap.appDB.NbCurrentTrackDao().delete(NbCurrentTrack);
                return null;
            }
        }.execute();
    }

    public static LiveData<NbCurrentTrack> selectLive(int id) {
        return dbConstantsMap.appDB.NbCurrentTrackDao().selectLive(id);
    }
    public static NbCurrentTrack select(int id) {
        return dbConstantsMap.appDB.NbCurrentTrackDao().select(id);
    }


    public static LiveData<List<NbCurrentTrack>> selectRowsLive(String filter) {
        return dbConstantsMap.appDB.NbCurrentTrackDao().selectRowsLive(filter);
    }
    public static List<NbCurrentTrack> selectRows(String filter) {
        return dbConstantsMap.appDB.NbCurrentTrackDao().selectRows(filter);
    }
    public static LiveData<List<NbCurrentTrack>> selectAllLive() {return dbConstantsMap.appDB.NbCurrentTrackDao().selectAllLive();}
    public static List<NbCurrentTrack> selectAll() {return dbConstantsMap.appDB.NbCurrentTrackDao().selectAll();}

}
