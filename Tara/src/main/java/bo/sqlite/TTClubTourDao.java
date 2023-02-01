package bo.sqlite;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import bo.entity.TTClubTour;

@Dao
public interface TTClubTourDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TTClubTour TTClubTour);

    @Query("select * from TTClubTour where :filter")
    public LiveData<List<TTClubTour>> selectRowsLive(String filter);
    @Query("select * from TTClubTour where :filter")
    public List<TTClubTour> selectRows(String filter);
    @Query("select * from TTClubTour")
    public LiveData<List<TTClubTour>> selectAllLive();
    @Query("select * from TTClubTour")
    public List<TTClubTour> selectAll();

    @Query("select * from TTClubTour where TTClubTourId = :TTClubTourId")
    public LiveData<TTClubTour> selectLive(Integer TTClubTourId);
    @Query("select * from TTClubTour where TTClubTourId = :TTClubTourId")
    public TTClubTour select(Integer TTClubTourId);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(TTClubTour TTClubTour);

    @Query("delete from TTClubTour")
    void deleteAll();

    @Query("delete from TTClubTour where :filter")
    void deleteWhere(String filter);

    @Delete
    void delete(TTClubTour note);

    //Makhsoose in table

    @Query("select * from TTClubTour where SiteId = :CasheDbId")
    public List<TTClubTour> SelectWithCache(int CasheDbId);
    @Query("delete from TTClubTour where SiteId = :CasheDbId")
    public void DeleteWithCache(int CasheDbId);

}
