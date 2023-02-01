package bo.sqlite;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;

import bo.dbConstantsTara;
import bo.entity.TTClubTour;
import bo.entity.FmSides;

public class TTClubTourSQLite {

    public static void insert(final TTClubTour TTClubTour) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                dbConstantsTara.appDB.TTClubTourDao().insert(TTClubTour);
                return null;
            }
        }.execute();
    }

    public static void update(final TTClubTour TTClubTour) {
        //TTClubTour.setModifiedAt(AppUtils.getCurrentDateTime());

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                dbConstantsTara.appDB.TTClubTourDao().update(TTClubTour);
                return null;
            }
        }.execute();
    }

    public static void delete(final int id) {
        final LiveData<TTClubTour> task = selectLive(id);
        if(task != null) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    dbConstantsTara.appDB.TTClubTourDao().delete(task.getValue());
                    return null;
                }
            }.execute();
        }
    }

    public static void deleteTask(final TTClubTour TTClubTour) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                dbConstantsTara.appDB.TTClubTourDao().delete(TTClubTour);
                return null;
            }
        }.execute();
    }

    public static LiveData<TTClubTour> selectLive(int id) {
        return dbConstantsTara.appDB.TTClubTourDao().selectLive(id);
    }
    public static TTClubTour select(int id) {
        return dbConstantsTara.appDB.TTClubTourDao().select(id);
    }


    public static LiveData<List<TTClubTour>> selectRowsLive(String filter) {
        return dbConstantsTara.appDB.TTClubTourDao().selectRowsLive(filter);
    }
    public static List<TTClubTour> selectRows(String filter) {
        return dbConstantsTara.appDB.TTClubTourDao().selectRows(filter);
    }
    public static LiveData<List<TTClubTour>> selectAllLive() {return dbConstantsTara.appDB.TTClubTourDao().selectAllLive();}
    public static List<TTClubTour> selectAll() {return dbConstantsTara.appDB.TTClubTourDao().selectAll();}

    public static List<TTClubTour> SelectWithCache(int CasheDbId) {
        return dbConstantsTara.appDB.TTClubTourDao().SelectWithCache(CasheDbId);
    }
    public static void DeleteWithCache(int CasheDbId) {
        dbConstantsTara.appDB.TTClubTourDao().DeleteWithCache(CasheDbId);
    }
}
