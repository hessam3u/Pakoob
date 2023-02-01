package bo.sqlite;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import bo.entity.NbAdv;

@Dao
public interface NbAdvDao {
    @Insert(onConflict = OnConflictStrategy.FAIL)
    long insert(NbAdv NbAdv);

    @Query("select * from NbAdv where :filter order by NbAdvId desc")
    public LiveData<List<NbAdv>> selectRowsLive(String filter);
    @Query("select * from NbAdv where :filter order by NbAdvId desc")
    public List<NbAdv> selectRows(String filter);
    @Query("select * from NbAdv order by NbAdvId desc")
    public LiveData<List<NbAdv>> selectAllLive();
    @Query("select * from NbAdv order by NbAdvId desc")
    public List<NbAdv> selectAll();
    @Query("select * from NbAdv order by NbAdvId desc ")
    public List<NbAdv> selectAllActives();

    @Query("select * from NbAdv where NbAdvId = :NbAdvId")
    public LiveData<NbAdv> selectLive(long NbAdvId);
    @Query("select * from NbAdv where NbAdvId = :NbAdvId")
    public NbAdv select(long NbAdvId);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(NbAdv NbAdv);

    @Query("delete from NbAdv")
    void deleteAll();

    @Query("delete from NbAdv where :filter")
    void deleteWhere(String filter);

    @Delete
    void delete(NbAdv note);

    //مخصوص این فرم
    @Query("select * from NbAdv  where AdvBoxNo < 10 ")
    public List<NbAdv> selectAllForHome();
}
