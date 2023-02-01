package bo.sqlite;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import bo.entity.FmSides;

@Dao
public interface FmSidesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FmSides FmSides);

    @Query("select * from FmSides where :filter")
    public LiveData<List<FmSides>> selectRowsLive(String filter);
    @Query("select * from FmSides where :filter")
    public List<FmSides> selectRows(String filter);
    @Query("select * from FmSides")
    public LiveData<List<FmSides>> selectAllLive();
    @Query("select * from FmSides")
    public List<FmSides> selectAll();

    @Query("select * from FmSides where FmSidesId = :FmSidesId")
    public LiveData<FmSides> selectLive(Integer FmSidesId);
    @Query("select * from FmSides where FmSidesId = :FmSidesId")
    public FmSides select(Integer FmSidesId);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(FmSides FmSides);

    @Query("delete from FmSides")
    void deleteAll();

    @Query("delete from FmSides where :filter")
    void deleteWhere(String filter);

    @Delete
    void delete(FmSides note);

    //Makhsoose in table

    @Query("SELECT MAX(FmSidesId) FROM FmSides")
    Integer selectMaxId();


    //Added Funcs:

    @Query("select * from FmSides where UserSide1 = :UserSide1 and  UserSide2 = :UserSide2 and  AnonymosType = :AnonymosType ")
    public FmSides select(Integer UserSide1, Integer UserSide2, byte AnonymosType);

}
