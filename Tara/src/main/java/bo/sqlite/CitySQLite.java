package bo.sqlite;

import android.os.AsyncTask;

import java.util.List;

import androidx.lifecycle.LiveData;
import bo.dbConstantsTara;
import bo.entity.CityDTO;

public class CitySQLite {
    public static void insert(Integer CityId, String Name, Integer ProvinceId, Double Latitude, Double Longtitude, Byte UpdateStatus) {
        CityDTO obj = new CityDTO();
        obj.CityId = CityId;
        obj.Name = Name;
        obj.ProvinceId = ProvinceId;
        obj.Latitude = Latitude;
        obj.Longtitude = Longtitude;
        obj.UpdateStatus = UpdateStatus;

        insert(obj);
    }

    public static void insert(final CityDTO CityDTO) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                dbConstantsTara.appDB.CityDao().insert(CityDTO);
                return null;
            }
        }.execute();
    }

    public static void update(final CityDTO CityDTO) {
        //CityDTO.setModifiedAt(AppUtils.getCurrentDateTime());

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                dbConstantsTara.appDB.CityDao().update(CityDTO);
                return null;
            }
        }.execute();
    }

    public static void delete(final int id) {
        final LiveData<CityDTO> task = selectLive(id);
        if(task != null) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    dbConstantsTara.appDB.CityDao().delete(task.getValue());
                    return null;
                }
            }.execute();
        }
    }

    public static void deleteTask(final CityDTO CityDTO) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                dbConstantsTara.appDB.CityDao().delete(CityDTO);
                return null;
            }
        }.execute();
    }

    public static LiveData<CityDTO> selectLive(int id) {
        return dbConstantsTara.appDB.CityDao().selectLive(id);
    }
    public static CityDTO select(int id) {
        return dbConstantsTara.appDB.CityDao().select(id);
    }


    public static LiveData<List<CityDTO>> selectRowsLive(String filter) {
        return dbConstantsTara.appDB.CityDao().selectRowsLive(filter);
    }
    public static List<CityDTO> selectRows(String filter) {
        return dbConstantsTara.appDB.CityDao().selectRows(filter);
    }
    public static LiveData<List<CityDTO>> selectAllLive() {return dbConstantsTara.appDB.CityDao().selectAllLive();}
    public static List<CityDTO> selectAll() {return dbConstantsTara.appDB.CityDao().selectAll();}

}
