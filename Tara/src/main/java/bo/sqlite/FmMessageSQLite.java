package bo.sqlite;

import android.os.AsyncTask;

import java.util.List;
import java.util.concurrent.ExecutionException;

import androidx.lifecycle.LiveData;
import bo.dbConstantsTara;
import bo.entity.FmMessage;
import bo.entity.FmSides;

public class FmMessageSQLite {

    public static void insert(final FmMessage FmMessage) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                dbConstantsTara.appDB.FmMessageDao().insert(FmMessage);
                return null;
            }
        }.execute();
    }

    public static void update(final FmMessage FmMessage) {
        //FmMessage.setModifiedAt(AppUtils.getCurrentDateTime());

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                dbConstantsTara.appDB.FmMessageDao().update(FmMessage);
                return null;
            }
        }.execute();
    }

    public static void delete(final int id) {
        final LiveData<FmMessage> task = selectLive(id);
        if(task != null) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    dbConstantsTara.appDB.FmMessageDao().delete(task.getValue());
                    return null;
                }
            }.execute();
        }
    }

    public static void deleteTask(final FmMessage FmMessage) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                dbConstantsTara.appDB.FmMessageDao().delete(FmMessage);
                return null;
            }
        }.execute();
    }

    public static LiveData<FmMessage> selectLive(int id) {
        return dbConstantsTara.appDB.FmMessageDao().selectLive(id);
    }
    public static FmMessage select(int id) {
        return dbConstantsTara.appDB.FmMessageDao().select(id);
    }


    public static LiveData<List<FmMessage>> selectRowsLive(String filter) {
        return dbConstantsTara.appDB.FmMessageDao().selectRowsLive(filter);
    }
    public static List<FmMessage> selectRows(String filter) {
        return dbConstantsTara.appDB.FmMessageDao().selectRows(filter);
    }
    public static LiveData<List<FmMessage>> selectAllLive() {return dbConstantsTara.appDB.FmMessageDao().selectAllLive();}
    public static List<FmMessage> selectAll() {return dbConstantsTara.appDB.FmMessageDao().selectAll();}

    public static Integer selectMaxId() throws ExecutionException, InterruptedException {
        return new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... voids) {
                return dbConstantsTara.appDB.FmMessageDao().selectMaxId();
            }
        }.execute().get();
    }


    //Added Functions
    public static void saveRecievedMessage(FmMessage data) {
        if (data.FmMessageType == FmMessage.FmMessageType_System){
            return;
        }
        insert(data);
        int tmpUserSide1 = 0;
        int tmpUserSide2 = 0;
        byte LastSenderSide = 0;

        if (data.CCustomerIdSend > data.RecieverId){
            tmpUserSide1 = (int)data.RecieverId.intValue();
            tmpUserSide2 = data.CCustomerIdSend.intValue();
            LastSenderSide = 2;
        }
        else{
            tmpUserSide2 = data.RecieverId.intValue();
            tmpUserSide1 = data.CCustomerIdSend.intValue();
            LastSenderSide = 1;
        }
        FmSides side = FmSidesSQLite.select(tmpUserSide1, tmpUserSide2, data.AnonymosType);
        boolean isNewSide = false;
        if (!(side != null && side.IdLocal > 0)){
            isNewSide = true;
            side = new FmSides();
            side.UserSide1 = tmpUserSide1;
            side.UserSide2 = tmpUserSide2;
            side.AnonymosType = data.AnonymosType;
            side.UnreadCount = 0;
            side.Name = data.CCustomerNameSend;
        }
        side.LastSenderSide = LastSenderSide;
        side.FmMessageIdLast = data.FmMessageId;
        side.LastContentType = data.ContentType;
        side.LastDate = data.SendDate;
//        side.FmChanalid = data.RecType == 1? 0 : data.FmMessageId;
        side.Text1 = data.Text1;
        if (data.CCustomerNameSend != null && data.CCustomerNameSend.length() > 0)
            side.Name = data.CCustomerNameSend;
//        side.SiteId = ProjectStatics;
        side.UnreadCount++;

        if (isNewSide){
            FmSidesSQLite.insert(side);
        }
        else{
            FmSidesSQLite.update(side);
        }

    }
}
