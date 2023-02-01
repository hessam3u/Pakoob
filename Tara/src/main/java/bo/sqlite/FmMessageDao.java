package bo.sqlite;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import bo.entity.FmMessage;

@Dao
public interface FmMessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FmMessage FmMessage);

    @Query("select * from FmMessage where :filter")
    public LiveData<List<FmMessage>> selectRowsLive(String filter);
    @Query("select * from FmMessage where :filter")
    public List<FmMessage> selectRows(String filter);
    @Query("select * from FmMessage")
    public LiveData<List<FmMessage>> selectAllLive();
    @Query("select * from FmMessage")
    public List<FmMessage> selectAll();

    @Query("select * from FmMessage where FmMessageId = :FmMessageId")
    public LiveData<FmMessage> selectLive(Integer FmMessageId);
    @Query("select * from FmMessage where FmMessageId = :FmMessageId")
    public FmMessage select(Integer FmMessageId);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(FmMessage FmMessage);

    @Query("delete from FmMessage")
    void deleteAll();

    @Query("delete from FmMessage where :filter")
    void deleteWhere(String filter);

    @Delete
    void delete(FmMessage note);

    //Makhsoose in table

    @Query("SELECT MAX(FmMessageId) FROM FmMessage")
    Integer selectMaxId();

}
