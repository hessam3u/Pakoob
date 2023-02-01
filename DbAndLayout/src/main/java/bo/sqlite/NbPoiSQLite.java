package bo.sqlite;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import bo.entity.NbPoi;
import bo.dbConstantsMap;

public class NbPoiSQLite {
    public static long insert(String Name, String AltName, byte Level, long ParentId, String Address, Double LatS, Double LonW, Double LatN, Double LonE
            , int Color, byte ShowStatus, short PoiType, long ServerId, byte CreatorType, byte ValidityLevel
            , byte ZoomMin, byte ZoomMax, byte ActivityType, byte DisplaySize, String addedInfo, int Priority, Double LatBegin, Double LonBegin) {
        NbPoi obj = new NbPoi();
        //obj.NbPoiId = NbPoiId;//Moheeeeeeeem baraye Identity lazeme
        obj.Name = Name;
        obj.AltName = AltName;
        obj.Level = Level;
        obj.ParentId = ParentId;
        obj.Address = Address;
        obj.LatS = LatS;
        obj.LatN = LatN;
        obj.LonE = LonE;
        obj.LonW = LonW;
        obj.Color = Color;
        obj.ShowStatus = ShowStatus;
        obj.PoiType = PoiType;
        obj.ServerId = ServerId;
        obj.CreatorType = CreatorType;
        obj.ValidityLevel = ValidityLevel;
        obj.ZoomMin = ZoomMin;
        obj.ZoomMax = ZoomMax;
        obj.ActivityType = ActivityType;
        obj.DisplaySize = DisplaySize;
        obj.addedInfo = addedInfo;
        obj.Priority = Priority;
        obj.LatBegin = LatBegin;
        obj.LonBegin = LonBegin;

        return insert(obj);
    }

    public static long insert(final NbPoi NbPoi) {
        return dbConstantsMap.appDB.NbPoiDao().insert(NbPoi);
//        new AsyncTask<Void, Void, Integer>() {
//            @Override
//            protected Integer doInBackground(Void... voids) {
//                return dbConstantsMap.appDB.NbPoiDao().insert(NbPoi);
//                //return null;
//            }
//        }.execute();
    }

    public static void update(final NbPoi NbPoi) {
        //NbPoi.setModifiedAt(AppUtils.getCurrentDateTime());

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                dbConstantsMap.appDB.NbPoiDao().update(NbPoi);
                return null;
            }
        }.execute();
    }
    public static void deleteAll() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                dbConstantsMap.appDB.NbPoiDao().deleteAll();
                return null;
            }
        }.execute();
    }

    public static void delete(final NbPoi poi) {
        dbConstantsMap.appDB.NbPoiDao().delete(poi);
//        final LiveData<NbPoi> task = selectLive(id);
//        if(task != null) {
//            new AsyncTask<Void, Void, Void>() {
//                @Override
//                protected Void doInBackground(Void... voids) {
//                    dbConstantsMap.appDB.NbPoiDao().delete(task.getValue());
//                    return null;
//                }
//            }.execute();
//        }
    }

    public static void deleteTask(final NbPoi NbPoi) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                dbConstantsMap.appDB.NbPoiDao().delete(NbPoi);
                return null;
            }
        }.execute();
    }

    public static LiveData<NbPoi> selectLive(long id) {
        return dbConstantsMap.appDB.NbPoiDao().selectLive(id);
    }
    public static NbPoi select(long id) {
        return dbConstantsMap.appDB.NbPoiDao().select(id);
    }


    public static LiveData<List<NbPoi>> selectRowsLive(String filter) {
        return dbConstantsMap.appDB.NbPoiDao().selectRowsLive(filter);
    }
    public static List<NbPoi> selectRows(String filter) {
        return dbConstantsMap.appDB.NbPoiDao().selectRows(filter);
    }
    public static List<NbPoi> selectByLevel(int Level, Long ParentId) {
        return dbConstantsMap.appDB.NbPoiDao().selectByLevel(Level, ParentId);
    }
    public static LiveData<List<NbPoi>> selectAllLive() {return dbConstantsMap.appDB.NbPoiDao().selectAllLive();}
    public static List<NbPoi> selectAll() {return dbConstantsMap.appDB.NbPoiDao().selectAll();}


    //----------------New Functions-------------
    public static NbPoi selectLastInserted() {
        return dbConstantsMap.appDB.NbPoiDao().selectLastInserted();
    }
    public static List<NbPoi> SelectWithChilds(NbPoi poi, boolean addFolders){
        List<NbPoi> res = new ArrayList<>();
        if (poi.PoiType == NbPoi.Enums.PoiType_Folder){
            if (addFolders)
                res.add(poi);

            List<NbPoi> childs = NbPoiSQLite.selectByLevel(poi.Level + 1, poi.NbPoiId);
            for (int i = 0; i < childs.size(); i++) {
                res.addAll(SelectWithChilds(childs.get(i), addFolders));
            }
        }
        else{
            res.add(poi);
        }
        return res;
    }

}
