package bo.sqlite;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import bo.entity.NbCurrentTrack;

@Dao
public interface NbCurrentTrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(NbCurrentTrack NbCurrentTrack);

    @Query("select * from NbCurrentTrack where :filter")
    public LiveData<List<NbCurrentTrack>> selectRowsLive(String filter);
    @Query("select * from NbCurrentTrack where :filter")
    public List<NbCurrentTrack> selectRows(String filter);
    @Query("select * from NbCurrentTrack")
    public LiveData<List<NbCurrentTrack>> selectAllLive();
    @Query("select * from NbCurrentTrack")
    public List<NbCurrentTrack> selectAll();
    @Query("select * from NbCurrentTrack ")
    public List<NbCurrentTrack> selectAllActives();

    @Query("select * from NbCurrentTrack where NbCurrentTrackId = :NbCurrentTrackId")
    public LiveData<NbCurrentTrack> selectLive(Integer NbCurrentTrackId);
    @Query("select * from NbCurrentTrack where NbCurrentTrackId = :NbCurrentTrackId")
    public NbCurrentTrack select(Integer NbCurrentTrackId);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(NbCurrentTrack NbCurrentTrack);

    @Query("delete from NbCurrentTrack")
    void deleteAll();

    @Query("delete from NbCurrentTrack where :filter")
    void deleteWhere(String filter);

    @Delete
    void delete(NbCurrentTrack note);
}
