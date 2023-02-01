package bo.sqlite;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import bo.entity.NbPoi;

@Dao
public interface NbPoiDao {
    @Insert(onConflict = OnConflictStrategy.FAIL)
    long insert(NbPoi NbPoi);

    @Query("select * from NbPoi where :filter order by NbPoiId desc")
    public LiveData<List<NbPoi>> selectRowsLive(String filter);
    @Query("select * from NbPoi where :filter order by NbPoiId desc")
    public List<NbPoi> selectRows(String filter);
    @Query("select * from NbPoi order by NbPoiId desc")
    public LiveData<List<NbPoi>> selectAllLive();
    @Query("select * from NbPoi order by NbPoiId desc")
    public List<NbPoi> selectAll();
    @Query("select * from NbPoi order by NbPoiId desc ")
    public List<NbPoi> selectAllActives();

    @Query("select * from NbPoi where NbPoiId = :NbPoiId")
    public LiveData<NbPoi> selectLive(long NbPoiId);
    @Query("select * from NbPoi where NbPoiId = :NbPoiId")
    public NbPoi select(long NbPoiId);
    @Query("select * from NbPoi where Level = :Level and (:ParentId = 0 or ParentId=:ParentId) order by PoiType asc, NbPoiId desc")
    public List<NbPoi> selectByLevel(Integer Level, Long ParentId);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(NbPoi NbPoi);

    @Query("delete from NbPoi")
    void deleteAll();

    @Query("delete from NbPoi where :filter")
    void deleteWhere(String filter);

    @Delete
    void delete(NbPoi note);


    //----------------New Functions-------------
    @Query("SELECT * FROM NbPoi ORDER BY NbPoiId DESC LIMIT 1")
    public NbPoi selectLastInserted();

    @Query("UPDATE NbPoi SET ShowStatus = :ShowStatus WHERE NbPoiId =:NbPoiId")
    void update(long NbPoiId, Byte ShowStatus);

    @Query("SELECT * FROM NbPoi Where PoiType > 1 and ShowStatus=:ShowStatus ORDER BY NbPoiId DESC LIMIT :Limit")
    public List<NbPoi> getVisiblePOIs(byte ShowStatus, int Limit);

}
