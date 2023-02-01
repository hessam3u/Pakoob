package bo.sqlite;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import bo.entity.TTClubNameDTO;

@Dao
public interface TTClubNameDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TTClubNameDTO TTClubNameDTO);

    @Query("select * from TTClubNameDTO where :filter")
    public LiveData<List<TTClubNameDTO>> selectRowsLive(String filter);
    @Query("select * from TTClubNameDTO where :filter")
    public List<TTClubNameDTO> selectRows(String filter);
    @Query("select * from TTClubNameDTO")
    public LiveData<List<TTClubNameDTO>> selectAllLive();
    @Query("select * from TTClubNameDTO")
    public List<TTClubNameDTO> selectAll();
    @Query("select * from TTClubNameDTO where UpdateStatus=1")
    public List<TTClubNameDTO> selectAllActives();

    @Query("select * from TTClubNameDTO where TTClubNameId = :TTClubNameDTOId")
    public LiveData<TTClubNameDTO> selectLive(Integer TTClubNameDTOId);
    @Query("select * from TTClubNameDTO where TTClubNameId = :TTClubNameDTOId")
    public TTClubNameDTO select(Integer TTClubNameDTOId);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(TTClubNameDTO TTClubNameDTO);

    @Query("delete from TTClubNameDTO")
    void deleteAll();

    @Query("delete from TTClubNameDTO where :filter")
    void deleteWhere(String filter);

    @Delete
    void delete(TTClubNameDTO note);
}
