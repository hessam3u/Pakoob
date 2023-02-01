package bo.sqlite;

import android.os.AsyncTask;

import java.util.List;

import androidx.lifecycle.LiveData;
import bo.entity.NbMap;
import bo.dbConstantsMap;

public class NbMapSQLite {
    public static void insert(int NbMapId, String Name, String OrginalName, String NCCIndex, String BlockName, Double LatS, Double LatN, Double LonE
            , Double LonW, String Tag, String Desc, Double Price, long AcServiceId, Byte Status, Byte AvailablityType, Byte AdminBuyType
            , String FileAddress, Byte EncType, Byte FileType, String NCCBlock, Double Scale, String AdminDesc, int Version, int Year, int NbPublisherTypeId
    , Double DemoLatS, Double DemoLatN, Double DemoLonE, Double DemoLonW, String DemoFileAddress, byte DemoStatus
            , Byte RequestStatus, Byte BuyStatus, String BuyDesc
            , String LocalFileAddress, byte Extracted, byte IsVisible, String PreviewImage) {
        NbMap obj = new NbMap();
        obj.NbMapId = NbMapId;
        obj.OrginalName = OrginalName;
        obj.Name = Name;
        obj.NCCIndex = NCCIndex;
        obj.BlockName = BlockName;
        obj.LatS = LatS;
        obj.LatN = LatN;
        obj.LonE = LonE;
        obj.LonW = LonW;
        obj.Tag = Tag;
        obj.Desc = Desc;
        obj.Price = Price;
        obj.Status = Status;
        obj.AvailablityType = AvailablityType;
        obj.AdminBuyType = AdminBuyType;
        obj.FileAddress = FileAddress;
        obj.EncType = EncType;
        obj.FileType = FileType;
        obj.NCCBlock = NCCBlock;
        obj.Scale = Scale;
        obj.AdminDesc = AdminDesc;
        obj.Version = Version;
        obj.Year = Year;
        obj.NbPublisherTypeId = NbPublisherTypeId;
        obj.DemoLatS = DemoLatS;
        obj.DemoLatN = DemoLatN;
        obj.DemoLonE = DemoLonE;
        obj.DemoLonW = DemoLonW;
        obj.DemoFileAddress = DemoFileAddress;
        obj.DemoStatus = DemoStatus;
        obj.RequestStatus = RequestStatus;
        obj.BuyStatus = BuyStatus;
        obj.BuyDesc = BuyDesc;
        obj.LocalFileAddress = LocalFileAddress;
        obj.Extracted = Extracted;
        obj.IsVisible = IsVisible;
        obj.PreviewImage = PreviewImage;

        insert(obj);
    }

    public static void insert(final NbMap NbMap) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                dbConstantsMap.appDB.NbMapDao().insert(NbMap);
                return null;
            }
        }.execute();
    }

    public static void update(final NbMap NbMap) {
        //NbMap.setModifiedAt(AppUtils.getCurrentDateTime());

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                dbConstantsMap.appDB.NbMapDao().update(NbMap);
                return null;
            }
        }.execute();
    }
    public static void deleteAll() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                dbConstantsMap.appDB.NbMapDao().deleteAll();
                return null;
            }
        }.execute();
    }

    public static void delete(final int id) {
        final LiveData<NbMap> task = selectLive(id);
        if(task != null) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    dbConstantsMap.appDB.NbMapDao().delete(task.getValue());
                    return null;
                }
            }.execute();
        }
    }

    public static void deleteTask(final NbMap NbMap) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                dbConstantsMap.appDB.NbMapDao().delete(NbMap);
                return null;
            }
        }.execute();
    }

    public static LiveData<NbMap> selectLive(int id) {
        return dbConstantsMap.appDB.NbMapDao().selectLive(id);
    }
    public static NbMap select(int id) {
        return dbConstantsMap.appDB.NbMapDao().select(id);
    }


    public static LiveData<List<NbMap>> selectRowsLive(String filter) {
        return dbConstantsMap.appDB.NbMapDao().selectRowsLive(filter);
    }
    public static List<NbMap> selectRows(String filter) {
        return dbConstantsMap.appDB.NbMapDao().selectRows(filter);
    }
    public static LiveData<List<NbMap>> selectAllLive() {return dbConstantsMap.appDB.NbMapDao().selectAllLive();}
    public static List<NbMap> selectAll() {return dbConstantsMap.appDB.NbMapDao().selectAll();}

}
