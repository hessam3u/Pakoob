package bo.sqlite;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import androidx.lifecycle.LiveData;
import bo.entity.TTExceptionLog;
import bo.dbConstantsMap;
import utils.hutilities;

public class TTExceptionLogSQLite {
    public static void insert(String ExMessage, String ExText, int InternalPage, int InternalCode) {
        TTExceptionLog obj = new TTExceptionLog();
        obj.CCustomerId = Integer.valueOf(hutilities.CCustomerId).longValue();
        obj.ExDate = Calendar.getInstance();
        obj.AppId = hutilities.AppId;
        obj.OsVersion = hutilities.getOsVersion();
        obj.PhoneSerial = hutilities.getPhoneSerial(12);
        obj.Platform = hutilities.getPlatform();
        obj.IP = hutilities.getIP();
        obj.DeviceModel = hutilities.getDeviceModel(20);
        obj.ManuIdiom = hutilities.getManuIdiom();
        obj.VersionNumber = hutilities.VersionCode;
        obj.ExMessage = ExMessage;
        obj.ExText = ExText;
        obj.InternalCode = InternalCode;
        obj.InternalPage = InternalPage;

        insert(obj);
    }
    public static void insert(Long CCustomerId, Calendar ExDate, Byte AppId, String Platform, int OsVersion, String PhoneSerial, String DeviceModel, String ManuIdiom, String IP, Integer VersionNumber, String ExMessage, String ExText, int InternalPage, int InternalCode) {
        TTExceptionLog obj = new TTExceptionLog();
        obj.CCustomerId = CCustomerId;
        obj.ExDate = ExDate;
        obj.AppId = AppId;
        obj.OsVersion = OsVersion;
        obj.PhoneSerial = PhoneSerial;
        obj.Platform = Platform;
        obj.IP = IP;
        obj.DeviceModel = DeviceModel;
        obj.ManuIdiom = ManuIdiom;
        obj.VersionNumber = VersionNumber;
        obj.ExMessage = ExMessage;
        obj.ExText = ExText;
        obj.InternalCode = InternalCode;
        obj.InternalPage = InternalPage;
        insert(obj);
    }

    public static void insert(final TTExceptionLog TTExceptionLog) {
        dbConstantsMap.appDB.TTExceptionLogDao().insert(TTExceptionLog);
        //MOHEM : Age AsyncTask mibood, moghe Exception haa Error midad va kar NEMIKARD.

//        new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... voids) {
//                dbConstantsMap.appDB.TTExceptionLogDao().insert(TTExceptionLog);
//                return null;
//            }
//        }.execute();
    }

    public static void update(final TTExceptionLog TTExceptionLog) {
        //TTExceptionLog.setModifiedAt(AppUtils.getCurrentDateTime());

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                dbConstantsMap.appDB.TTExceptionLogDao().update(TTExceptionLog);
                return null;
            }
        }.execute();
    }

    public static void delete(final int id) {
        final LiveData<TTExceptionLog> task = selectLive(id);
        if(task != null) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    dbConstantsMap.appDB.TTExceptionLogDao().delete(task.getValue());
                    return null;
                }
            }.execute();
        }
    } public static void deleteAll() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                dbConstantsMap.appDB.TTExceptionLogDao().deleteAll();
                return null;
            }
        }.execute();
    }

    public static void deleteTask(final TTExceptionLog TTExceptionLog) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                dbConstantsMap.appDB.TTExceptionLogDao().delete(TTExceptionLog);
                return null;
            }
        }.execute();
    }

    public static LiveData<TTExceptionLog> selectLive(int id) {
        return dbConstantsMap.appDB.TTExceptionLogDao().selectLive(id);
    }
    public static TTExceptionLog select(int id) {
        return dbConstantsMap.appDB.TTExceptionLogDao().select(id);
    }


    public static LiveData<List<TTExceptionLog>> selectRowsLive(String filter) {
        return dbConstantsMap.appDB.TTExceptionLogDao().selectRowsLive(filter);
    }
    public static List<TTExceptionLog> selectRows(String filter) {
        return dbConstantsMap.appDB.TTExceptionLogDao().selectRows(filter);
    }
    public static LiveData<List<TTExceptionLog>> selectAllLive() {return dbConstantsMap.appDB.TTExceptionLogDao().selectAllLive();}
    public static List<TTExceptionLog> selectAll() {return dbConstantsMap.appDB.TTExceptionLogDao().selectAll();}

    public static Integer selectMaxId() throws ExecutionException, InterruptedException {
        return new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... voids) {
                return dbConstantsMap.appDB.TTExceptionLogDao().selectMaxId();
            }
        }.execute().get();
    }

}
