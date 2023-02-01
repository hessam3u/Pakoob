package bo.sqlite;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import bo.entity.TTClubTourCategoryDTO;

@Dao
public interface TTClubTourCategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TTClubTourCategoryDTO TTClubTourCategoryDTO);

    @Query("select * from TTClubTourCategoryDTO where :filter")
    public LiveData<List<TTClubTourCategoryDTO>> selectRowsLive(String filter);
    @Query("select * from TTClubTourCategoryDTO where :filter")
    public List<TTClubTourCategoryDTO> selectRows(String filter);
    @Query("select * from TTClubTourCategoryDTO")
    public LiveData<List<TTClubTourCategoryDTO>> selectAllLive();
    @Query("select * from TTClubTourCategoryDTO")
    public List<TTClubTourCategoryDTO> selectAll();
    @Query("select * from TTClubTourCategoryDTO where UpdateStatus=1")
    public List<TTClubTourCategoryDTO> selectAllActives();

    @Query("select * from TTClubTourCategoryDTO where TTClubTourCategoryId = :TTClubTourCategoryDTOId")
    public LiveData<TTClubTourCategoryDTO> selectLive(Integer TTClubTourCategoryDTOId);
    @Query("select * from TTClubTourCategoryDTO where TTClubTourCategoryId = :TTClubTourCategoryDTOId")
    public TTClubTourCategoryDTO select(Integer TTClubTourCategoryDTOId);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(TTClubTourCategoryDTO TTClubTourCategoryDTO);

    @Query("delete from TTClubTourCategoryDTO")
    void deleteAll();

    @Query("delete from TTClubTourCategoryDTO where :filter")
    void deleteWhere(String filter);

    @Delete
    void delete(TTClubTourCategoryDTO note);
}
