package bo.sqlite;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import bo.entity.CityDTO;

@Dao
public interface CityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CityDTO CityDTO);

    @Query("select * from CityDTO where :filter")
    public LiveData<List<CityDTO>> selectRowsLive(String filter);
    @Query("select * from CityDTO where :filter")
    public List<CityDTO> selectRows(String filter);
    @Query("select * from CityDTO")
    public LiveData<List<CityDTO>> selectAllLive();
    @Query("select * from CityDTO")
    public List<CityDTO> selectAll();
    @Query("select * from CityDTO where UpdateStatus=1")
    public List<CityDTO> selectAllActives();

    @Query("select * from CityDTO where CityId = :CityDTOId")
    public LiveData<CityDTO> selectLive(Integer CityDTOId);
    @Query("select * from CityDTO where CityId = :CityDTOId")
    public CityDTO select(Integer CityDTOId);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(CityDTO CityDTO);

    @Query("delete from CityDTO")
    void deleteAll();

    @Query("delete from CityDTO where :filter")
    void deleteWhere(String filter);

    @Delete
    void delete(CityDTO note);
}
