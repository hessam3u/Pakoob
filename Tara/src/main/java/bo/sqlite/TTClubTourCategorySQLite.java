package bo.sqlite;

import android.os.AsyncTask;

import java.util.List;

import androidx.lifecycle.LiveData;
import bo.dbConstantsTara;
import bo.entity.TTClubTourCategoryDTO;

public class TTClubTourCategorySQLite {
    public static void insert(Integer TTClubTourCategoryId, String Title, String Color, String DescUser, String PhotoAddress, Byte UpdateStatus) {
        TTClubTourCategoryDTO obj = new TTClubTourCategoryDTO();
        obj.Color = Color;
        obj.DescUser = DescUser;
        obj.PhotoAddress = PhotoAddress;
        obj.Title = Title;
        obj.TTClubTourCategoryId = TTClubTourCategoryId;
        obj.UpdateStatus = UpdateStatus;

        insert(obj);
    }

    public static void insert(final TTClubTourCategoryDTO TTClubTourCategoryDTO) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                dbConstantsTara.appDB.TTClubTourCategoryDao().insert(TTClubTourCategoryDTO);
                return null;
            }
        }.execute();
    }

    public static void update(final TTClubTourCategoryDTO TTClubTourCategoryDTO) {
        //TTClubTourCategoryDTO.setModifiedAt(AppUtils.getCurrentDateTime());

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                dbConstantsTara.appDB.TTClubTourCategoryDao().update(TTClubTourCategoryDTO);
                return null;
            }
        }.execute();
    }

    public static void delete(final int id) {
        final LiveData<TTClubTourCategoryDTO> task = selectLive(id);
        if(task != null) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    dbConstantsTara.appDB.TTClubTourCategoryDao().delete(task.getValue());
                    return null;
                }
            }.execute();
        }
    }

    public static void deleteTask(final TTClubTourCategoryDTO TTClubTourCategoryDTO) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                dbConstantsTara.appDB.TTClubTourCategoryDao().delete(TTClubTourCategoryDTO);
                return null;
            }
        }.execute();
    }

    public static LiveData<TTClubTourCategoryDTO> selectLive(int id) {
        return dbConstantsTara.appDB.TTClubTourCategoryDao().selectLive(id);
    }
    public static TTClubTourCategoryDTO select(int id) {
        return dbConstantsTara.appDB.TTClubTourCategoryDao().select(id);
    }


    public static LiveData<List<TTClubTourCategoryDTO>> selectRowsLive(String filter) {
        return dbConstantsTara.appDB.TTClubTourCategoryDao().selectRowsLive(filter);
    }
    public static List<TTClubTourCategoryDTO> selectRows(String filter) {
        return dbConstantsTara.appDB.TTClubTourCategoryDao().selectRows(filter);
    }
    public static LiveData<List<TTClubTourCategoryDTO>> selectAllLive() {return dbConstantsTara.appDB.TTClubTourCategoryDao().selectAllLive();}
    public static List<TTClubTourCategoryDTO> selectAll() {return dbConstantsTara.appDB.TTClubTourCategoryDao().selectAll();}

}
