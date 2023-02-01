package bo.entity;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.Calendar;
import utils.MyDate;
import utils.TextFormat;
import utils.hutilities;

@Entity(indices = {@Index("NbAdvIdLocal")})
public class NbAdv
{
	@Nullable
	@PrimaryKey(autoGenerate = true)
	@SerializedName(value="NbAdvIdLocal", alternate = {"nbAdvIdLocal"})
	public int NbAdvIdLocal;
	@Nullable
	@SerializedName(value="LocalAddress", alternate = {"localAddress"})
	public String LocalAddress;

	@SerializedName(value="NbAdvId", alternate = {"nbAdvId"})
	public int NbAdvId;
	@Nullable
	@SerializedName(value="CCustomerId", alternate = {"cCustomerId"})
	public int CCustomerId;
	@Nullable
	@SerializedName(value="AdvContentType", alternate = {"advContentType"})
	public byte AdvContentType;
	@Nullable
	@SerializedName(value="AdvShowType", alternate = {"advShowType"})
	public byte AdvShowType;
	@Nullable
	@SerializedName(value="ExternalId", alternate = {"externalId"})
	public long ExternalId;
	@Nullable
	@SerializedName(value="PhotoAddress", alternate = {"photoAddress"})
	public String PhotoAddress;
	@Nullable
	@SerializedName(value="Link", alternate = {"link"})
	public String Link;
	@Nullable
	@SerializedName(value="Text1", alternate = {"text1"})
	public String Text1;
	@Nullable
	@SerializedName(value="Text2", alternate = {"text2"})
	public String Text2;
	@Nullable
	@SerializedName(value="Text3", alternate = {"text3"})
	public String Text3;
	@Nullable
	@SerializedName(value="NbFilterId", alternate = {"nbFilterId"})
	public int NbFilterId;
	@Nullable
	@SerializedName(value="Status", alternate = {"status"})
	public byte Status;
	@Nullable
	@SerializedName(value="ShowDateFrom", alternate = {"showDateFrom"})
	public String ShowDateFrom;
	@Nullable
	@SerializedName(value="ShowDateTo", alternate = {"showDateTo"})
	public String ShowDateTo;
	@Nullable
	@SerializedName(value="AdvBoxNo", alternate = {"advBoxNo"})
	public byte AdvBoxNo;
	@Nullable
	@SerializedName(value="BuyStatus", alternate = {"buyStatus"})
	public byte BuyStatus;
	@Nullable
	@SerializedName(value="AdvVersion", alternate = {"advVersion"})
	public int AdvVersion;
	@Nullable
	@SerializedName(value="PriorityInBox", alternate = {"priorityInBox"})
	public int PriorityInBox;
	@Nullable
	@SerializedName(value="RecUpdateDate", alternate = {"recUpdateDate"})
	public String RecUpdateDate;

	public Calendar getShowDateFrom(){return MyDate.CalendarFromCSharpSQLString(this.ShowDateFrom);}
	public String getShowDateFromView(){return MyDate.sQLStringToPersianSrting(this.ShowDateFrom, MyDate.DateToStringFormat.yyyymmdd, MyDate.TimeToStringFormat.None, "");}
	public void setShowDateFrom(Calendar value){this.ShowDateFrom = MyDate.CalendarToCSharpSQLString(value);}
	public Calendar getShowDateTo(){return MyDate.CalendarFromCSharpSQLString(this.ShowDateTo);}
	public String getShowDateToView(){return MyDate.sQLStringToPersianSrting(this.ShowDateTo, MyDate.DateToStringFormat.yyyymmdd, MyDate.TimeToStringFormat.None, "");}
	public void setShowDateTo(Calendar value){this.ShowDateTo = MyDate.CalendarToCSharpSQLString(value);}
	public Calendar getRecUpdateDate(){return MyDate.CalendarFromCSharpSQLString(this.RecUpdateDate);}
	public String getRecUpdateDateView(){return MyDate.sQLStringToPersianSrting(this.RecUpdateDate, MyDate.DateToStringFormat.yyyymmdd, MyDate.TimeToStringFormat.None, "");}
	public void setRecUpdateDate(Calendar value){this.RecUpdateDate = MyDate.CalendarToCSharpSQLString(value);}

	public NbAdv() {
    }
	
	public static NbAdv fromBytes(byte[] bts){
		Gson gson = new Gson();
		String json = hutilities.decryptBytesToString(bts);
		return gson.fromJson(json, NbAdv.class);
	}
	public String toString(){
		return "NbAdvId :"+Integer.toString(NbAdvId)+", CCustomerId :"+Integer.toString(CCustomerId)+", AdvContentType :"+Byte.toString(AdvContentType)+", AdvShowType :"+Byte.toString(AdvShowType)+", ExternalId :"+Long.toString(ExternalId)+", PhotoAddress :"+PhotoAddress+", Link :"+Link+", Text1 :"+Text1+", Text2 :"+Text2+", Text3 :"+Text3+", NbFilterId :"+Integer.toString(NbFilterId)+", Status :"+Byte.toString(Status)+", ShowDateFrom :"+ShowDateFrom+", ShowDateTo :"+ShowDateTo+", AdvBoxNo :"+Byte.toString(AdvBoxNo)+", BuyStatus :"+Byte.toString(BuyStatus)+", AdvVersion :"+Integer.toString(AdvVersion)+", PriorityInBox :"+Integer.toString(PriorityInBox)+", RecUpdateDate :"+getRecUpdateDateView();
	}

	public static final int AdvShowType_Photo = 1;
	public static final int AdvShowType_Type1 = 2;
	public static final int AdvShowType_Type2 = 3;
	public static final int AdvContentType_Photo = 1;
	public static final int AdvContentType_Tour = 2;
	public static final int AdvContentType_Product = 3;


}