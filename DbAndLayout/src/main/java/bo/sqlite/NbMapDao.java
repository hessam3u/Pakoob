package bo.sqlite;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import bo.entity.NbMap;

@Dao
public interface NbMapDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(NbMap NbMap);

    @Query("select * from NbMap where :filter")
    public LiveData<List<NbMap>> selectRowsLive(String filter);
    @Query("select * from NbMap where :filter")
    public List<NbMap> selectRows(String filter);
    @Query("select * from NbMap")
    public LiveData<List<NbMap>> selectAllLive();
    @Query("select * from NbMap")
    public List<NbMap> selectAll();
    @Query("select * from NbMap ")
    public List<NbMap> selectAllActives();

    @Query("select * from NbMap where NbMapId = :NbMapId")
    public LiveData<NbMap> selectLive(Integer NbMapId);
    @Query("select * from NbMap where NbMapId = :NbMapId")
    public NbMap select(Integer NbMapId);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(NbMap NbMap);

    @Query("delete from NbMap")
    void deleteAll();

    @Query("delete from NbMap where :filter")
    void deleteWhere(String filter);

    @Delete
    void delete(NbMap note);
}
