package bo.entity;

import com.google.gson.annotations.SerializedName;

import utils.hutilities;

public class MobileInfoDTO implements IMobileInfoDTO {
	@SerializedName(value="AppId", alternate = {"appId"})
	public Byte AppId;
	@SerializedName(value="Platform", alternate = {"platform"})
	public String Platform;
	@SerializedName(value="OsVersion", alternate = {"osVersion"})
	public Integer OsVersion;
	@SerializedName(value="PhoneSerial", alternate = {"phoneSerial"})
	public String PhoneSerial;
	@SerializedName(value="DeviceModel", alternate = {"deviceModel"})
	public String DeviceModel;
	@SerializedName(value="ManuIdiom", alternate = {"manuIdiom"})
	public String ManuIdiom;
	@SerializedName(value="VersionNumber", alternate = {"versionNumber"})
	public Integer VersionNumber;
	@SerializedName(value="IP", alternate = {"iP"})
	public String IP;
	@SerializedName(value="session", alternate = {"Session"})
	public String Session;
	@SerializedName(value="TTClubNameId", alternate = {"tTClubNameId", "ttClubNameId"})
	public Integer TTClubNameId = 0;
	@SerializedName(value="Uid", alternate = {"uid"})
	public Integer Uid = 0;

	public MobileInfoDTO() {
    }

	public static MobileInfoDTO instance() {
		MobileInfoDTO obj = new MobileInfoDTO();
		obj.AppId = hutilities.AppId;
		obj.OsVersion = hutilities.getOsVersion();
		obj.PhoneSerial = hutilities.getPhoneSerial(12);
		obj.Platform = hutilities.getPlatform();
		obj.IP = hutilities.getIP();
		obj.DeviceModel = hutilities.getDeviceModel(20);
		obj.ManuIdiom = hutilities.getManuIdiom();
		obj.VersionNumber = hutilities.VersionCode;
		obj.Session = hutilities.Session;
		obj.TTClubNameId = 0;//app.session.getMyClubNameId();
		obj.Uid = hutilities.CCustomerId;

		return obj;
	}

	public static class FBTokenUpdateRequest{
		public String token;
		public long ccustomerId;
		public FBTokenUpdateRequest(long _ccustomerId,  String _token) {
			token = _token;
			ccustomerId = _ccustomerId;
		}
	}
}