package bo.NewClasses;

import com.google.gson.annotations.SerializedName;

//Must Override resList and fromBytes
public class ListItemsResult {
    @SerializedName(value="message", alternate = {"Message"})
    public String message;
    @SerializedName(value="OtherCommand", alternate = {"otherCommand"})
    public String OtherCommand;
    @SerializedName(value="isOk")
    public Boolean isOk;
    @SerializedName(value="status")
    public Integer status;
}
