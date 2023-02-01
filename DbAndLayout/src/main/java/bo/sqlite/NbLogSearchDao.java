package bo.sqlite;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import bo.entity.NbLogSearch;

@Dao
public interface NbLogSearchDao {
    @Insert(onConflict = OnConflictStrategy.FAIL)
    long insert(NbLogSearch NbLogSearch);

    @Query("select * from NbLogSearch where :filter order by NbLogSearchId desc")
    public LiveData<List<NbLogSearch>> selectRowsLive(String filter);
    @Query("select * from NbLogSearch where :filter order by NbLogSearchId desc")
    public List<NbLogSearch> selectRows(String filter);
    @Query("select * from NbLogSearch order by NbLogSearchId desc")
    public LiveData<List<NbLogSearch>> selectAllLive();
    @Query("select * from NbLogSearch order by NbLogSearchId desc")
    public List<NbLogSearch> selectAll();
    @Query("select * from NbLogSearch order by NbLogSearchId desc ")
    public List<NbLogSearch> selectAllActives();

    @Query("select * from NbLogSearch where NbLogSearchId = :NbLogSearchId")
    public LiveData<NbLogSearch> selectLive(long NbLogSearchId);
    @Query("select * from NbLogSearch where NbLogSearchId = :NbLogSearchId")
    public NbLogSearch select(long NbLogSearchId);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(NbLogSearch NbLogSearch);

    @Query("delete from NbLogSearch")
    void deleteAll();

    @Query("delete from NbLogSearch where :filter")
    void deleteWhere(String filter);

    @Delete
    void delete(NbLogSearch note);


    //----------------New Functions-------------
    @Query("SELECT * FROM NbLogSearch ORDER BY NbLogSearchId DESC LIMIT 1")
    public NbLogSearch selectLastInserted();


}
