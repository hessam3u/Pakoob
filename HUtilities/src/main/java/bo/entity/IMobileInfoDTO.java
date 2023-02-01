package bo.entity;

import com.google.gson.annotations.SerializedName;

public interface IMobileInfoDTO {
    @SerializedName(value="AppId", alternate = {"appId"})
    public Byte AppId = 0;
    @SerializedName(value="Platform", alternate = {"platform"})
    public String Platform ="";
    @SerializedName(value="OsVersion", alternate = {"osVersion"})
    public Integer OsVersion = 0;
    @SerializedName(value="PhoneSerial", alternate = {"phoneSerial"})
    public String PhoneSerial = "";
    @SerializedName(value="DeviceModel", alternate = {"deviceModel"})
    public String DeviceModel = "";
    @SerializedName(value="ManuIdiom", alternate = {"manuIdiom"})
    public String ManuIdiom = "";
    @SerializedName(value="VersionNumber", alternate = {"versionNumber"})
    public Integer VersionNumber = 0;
    @SerializedName(value="IP", alternate = {"iP"})
    public String IP = "";
    @SerializedName(value="session", alternate = {"Session"})
    public String Session = "";
    @SerializedName(value="TTClubNameId", alternate = {"tTClubNameId", "ttClubNameId"})
    public Integer TTClubNameId = 0;
    @SerializedName(value="Uid", alternate = {"uid"})
    public Integer Uid = 0;
}
