package bo.sqlite;

import android.os.AsyncTask;

import java.util.List;
import java.util.concurrent.ExecutionException;

import androidx.lifecycle.LiveData;
import bo.dbConstantsTara;
import bo.entity.FmSides;

public class FmSidesSQLite {

    public static void insert(final FmSides FmSides) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                dbConstantsTara.appDB.FmSidesDao().insert(FmSides);
                return null;
            }
        }.execute();
    }

    public static void update(final FmSides FmSides) {
        //FmSides.setModifiedAt(AppUtils.getCurrentDateTime());

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                dbConstantsTara.appDB.FmSidesDao().update(FmSides);
                return null;
            }
        }.execute();
    }

    public static void delete(final int id) {
        final LiveData<FmSides> task = selectLive(id);
        if(task != null) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    dbConstantsTara.appDB.FmSidesDao().delete(task.getValue());
                    return null;
                }
            }.execute();
        }
    }

    public static void deleteTask(final FmSides FmSides) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                dbConstantsTara.appDB.FmSidesDao().delete(FmSides);
                return null;
            }
        }.execute();
    }

    public static LiveData<FmSides> selectLive(int id) {
        return dbConstantsTara.appDB.FmSidesDao().selectLive(id);
    }
    public static FmSides select(int id) {
        return dbConstantsTara.appDB.FmSidesDao().select(id);
    }


    public static LiveData<List<FmSides>> selectRowsLive(String filter) {
        return dbConstantsTara.appDB.FmSidesDao().selectRowsLive(filter);
    }
    public static List<FmSides> selectRows(String filter) {
        return dbConstantsTara.appDB.FmSidesDao().selectRows(filter);
    }
    public static LiveData<List<FmSides>> selectAllLive() {return dbConstantsTara.appDB.FmSidesDao().selectAllLive();}
    public static List<FmSides> selectAll() {return dbConstantsTara.appDB.FmSidesDao().selectAll();}

    public static Integer selectMaxId() throws ExecutionException, InterruptedException {
        return new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... voids) {
                return dbConstantsTara.appDB.FmSidesDao().selectMaxId();
            }
        }.execute().get();
    }

    //Added Funcs:
    public static FmSides select(Integer UserSide1, Integer UserSide2, byte AnonymosType) {
        return dbConstantsTara.appDB.FmSidesDao().select(UserSide1, UserSide2, AnonymosType);
    }


}
