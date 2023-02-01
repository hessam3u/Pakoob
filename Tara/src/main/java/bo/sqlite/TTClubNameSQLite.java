package bo.sqlite;

import android.os.AsyncTask;

import java.util.List;

import androidx.lifecycle.LiveData;
import bo.dbConstantsTara;
import bo.entity.TTClubNameDTO;

public class TTClubNameSQLite {
    public static void insert(Integer TTClubNameId, Long MojServiceId, String Name, Integer CCustomerIdRelated, Integer ExtSiteId
    , String Logo, String WebsiteAddress,  String Address,  String Telephone,  String Desc, String ManagerName,  Integer CityId, String CityName, Byte UpdateStatus) {
        TTClubNameDTO obj = new TTClubNameDTO();
        obj.TTClubNameId = TTClubNameId;
        obj.MojServiceId = MojServiceId;
        obj.Name = Name;
        obj.CCustomerIdRelated = CCustomerIdRelated;
        obj.ExtSiteId = ExtSiteId;
        obj.Logo = Logo;
        obj.WebsiteAddress = WebsiteAddress;
        obj.Address = Address;
        obj.Telephone = Telephone;
        obj.Desc = Desc;
        obj.ManagerName = ManagerName;
        obj.CityId = CityId;
        obj.CityName = CityName;
        obj.UpdateStatus  = UpdateStatus;

        insert(obj);
    }

    public static void insert(final TTClubNameDTO TTClubNameDTO) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                dbConstantsTara.appDB.TTClubNameDao().insert(TTClubNameDTO);
                return null;
            }
        }.execute();
    }

    public static void update(final TTClubNameDTO TTClubNameDTO) {
        //TTClubNameDTO.setModifiedAt(AppUtils.getCurrentDateTime());

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                dbConstantsTara.appDB.TTClubNameDao().update(TTClubNameDTO);
                return null;
            }
        }.execute();
    }

    public static void delete(final int id) {
        final LiveData<TTClubNameDTO> task = selectLive(id);
        if(task != null) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    dbConstantsTara.appDB.TTClubNameDao().delete(task.getValue());
                    return null;
                }
            }.execute();
        }
    }

    public static void deleteTask(final TTClubNameDTO TTClubNameDTO) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                dbConstantsTara.appDB.TTClubNameDao().delete(TTClubNameDTO);
                return null;
            }
        }.execute();
    }

    public static LiveData<TTClubNameDTO> selectLive(int id) {
        return dbConstantsTara.appDB.TTClubNameDao().selectLive(id);
    }
    public static TTClubNameDTO select(int id) {
        return dbConstantsTara.appDB.TTClubNameDao().select(id);
    }


    public static LiveData<List<TTClubNameDTO>> selectRowsLive(String filter) {
        return dbConstantsTara.appDB.TTClubNameDao().selectRowsLive(filter);
    }
    public static List<TTClubNameDTO> selectRows(String filter) {
        return dbConstantsTara.appDB.TTClubNameDao().selectRows(filter);
    }
    public static LiveData<List<TTClubNameDTO>> selectAllLive() {return dbConstantsTara.appDB.TTClubNameDao().selectAllLive();}
    public static List<TTClubNameDTO> selectAll() {return dbConstantsTara.appDB.TTClubNameDao().selectAll();}

}
