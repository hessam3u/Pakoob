package bo.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import utils.MyDate;
import utils.TimestampConverter;

@Entity(indices = {@Index("TTExceptionLogId")})
public class TTExceptionLog
{
	public String toJSON(){

//        SimpleDateFormat fmt=  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        final SimpleDateFormat fmt = MyDate.cSJSONFormatter;//new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);

		JSONObject jsonObject= new JSONObject();
		try {
			jsonObject.put("TTExceptionLogId", TTExceptionLogId.intValue());
			jsonObject.put("CCustomerId", CCustomerId);
			jsonObject.put("ExDate", MyDate.CalendarToCSharpJSONAcceptable(ExDate));//fmt.format(ExDate.getTime()));
			jsonObject.put("AppId", AppId);
			jsonObject.put("ManuIdiom", ManuIdiom);
			jsonObject.put("DeviceModel", DeviceModel);
			jsonObject.put("PhoneSerial", PhoneSerial);
			jsonObject.put("OsVersion", OsVersion);
			jsonObject.put("Platform", Platform);
			jsonObject.put("IP", IP);
			jsonObject.put("VersionNumber", VersionNumber);
			jsonObject.put("ExMessage", ExMessage);
			jsonObject.put("ExText", ExText);
			jsonObject.put("InternalPage", InternalPage);
			jsonObject.put("InternalCode", InternalCode);

			return jsonObject.toString();
		} catch (JSONException e) {
			// Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}
	@PrimaryKey(autoGenerate = true)
	@SerializedName(value="TTExceptionLogId", alternate = {"tTExceptionLogId"})
	public Integer TTExceptionLogId;
	@SerializedName(value="CCustomerId", alternate = {"cCustomerId"})
	public Long CCustomerId;

	@SerializedName(value="ExDate", alternate = {"exDate"})
	@TypeConverters({TimestampConverter.class})
	@Expose
	public Calendar ExDate;

	@SerializedName(value="exs")
	@Ignore
	public String ExDateStr;

	@SerializedName(value="AppId", alternate = {"appId"})
	public Byte AppId;
	@SerializedName(value="Platform", alternate = {"platform"})
	public String Platform;
	@SerializedName(value="OsVersion", alternate = {"osVersion"})
	public int OsVersion;
	@SerializedName(value="PhoneSerial", alternate = {"phoneSerial"})
	public String PhoneSerial;
	@SerializedName(value="DeviceModel", alternate = {"deviceModel"})
	public String DeviceModel;
	@SerializedName(value="ManuIdiom", alternate = {"manuIdiom"})
	public String ManuIdiom;

	@SerializedName(value="IP", alternate = {"iP"})
	public String IP;
	@SerializedName(value="VersionNumber", alternate = {"versionNumber"})
	public Integer VersionNumber;
	@SerializedName(value="ExMessage", alternate = {"exMessage"})
	public String ExMessage;
	@SerializedName(value="ExText", alternate = {"exText"})
	public String ExText;
	@SerializedName(value="InternalPage", alternate = {"internalPage"})
	public Integer InternalPage;
	@SerializedName(value="InternalCode", alternate = {"internalCode"})
	public Integer InternalCode;

	public TTExceptionLog() {
    }
}