package bo.sqlite;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import bo.entity.NbScreenTime;

@Dao
public interface NbScreenTimeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(NbScreenTime NbScreenTime);

    @Query("select * from nb_screen_time where :filter")
    public LiveData<List<NbScreenTime>> selectRowsLive(String filter);
    @Query("select * from nb_screen_time where :filter")
    public List<NbScreenTime> selectRows(String filter);
    @Query("select * from nb_screen_time")
    public LiveData<List<NbScreenTime>> selectAllLive();
    @Query("select * from nb_screen_time")
    public List<NbScreenTime> selectAll();
    @Query("select * from nb_screen_time ")
    public List<NbScreenTime> selectAllActives();

    @Query("select * from nb_screen_time where NbScreenTimeId = :NbScreenTimeId")
    public LiveData<NbScreenTime> selectLive(Integer NbScreenTimeId);
    @Query("select * from nb_screen_time where NbScreenTimeId = :NbScreenTimeId")
    public NbScreenTime select(Integer NbScreenTimeId);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(NbScreenTime NbScreenTime);

    @Query("delete from nb_screen_time")
    void deleteAll();

    @Query("delete from nb_screen_time where :filter")
    void deleteWhere(String filter);

    @Delete
    void delete(NbScreenTime note);
}
