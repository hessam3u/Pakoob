package bo.sqlite;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import bo.entity.TTExceptionLog;

@Dao
public interface TTExceptionLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TTExceptionLog TTExceptionLog);

    @Query("select * from TTExceptionLog where :filter")
    public LiveData<List<TTExceptionLog>> selectRowsLive(String filter);
    @Query("select * from TTExceptionLog where :filter")
    public List<TTExceptionLog> selectRows(String filter);
    @Query("select * from TTExceptionLog")
    public LiveData<List<TTExceptionLog>> selectAllLive();
    @Query("select * from TTExceptionLog")
    public List<TTExceptionLog> selectAll();

    @Query("select * from TTExceptionLog where TTExceptionLogId = :TTExceptionLogId")
    public LiveData<TTExceptionLog> selectLive(Integer TTExceptionLogId);
    @Query("select * from TTExceptionLog where TTExceptionLogId = :TTExceptionLogId")
    public TTExceptionLog select(Integer TTExceptionLogId);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(TTExceptionLog TTExceptionLog);

    @Query("delete from TTExceptionLog")
    void deleteAll();

    @Query("delete from TTExceptionLog where :filter")
    void deleteWhere(String filter);

    @Delete
    void delete(TTExceptionLog note);

    //Makhsoose in table

    @Query("SELECT MAX(TTExceptionLogId) FROM TTExceptionLog")
    Integer selectMaxId();
}
