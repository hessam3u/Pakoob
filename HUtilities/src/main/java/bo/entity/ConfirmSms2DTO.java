package bo.entity;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import utils.hutilities;

public class ConfirmSms2DTO
{
	@SerializedName(value="info", alternate = {"Info"})
	public MobileInfoDTO info;
	@SerializedName(value="confirmCode", alternate = {"ConfirmCode"})
	public String confirmCode;
	@SerializedName(value="TempDeviceId", alternate = {"tempDeviceId"})
	public String TempDeviceId;
	@SerializedName(value="Mobile", alternate = {"mobile"})
	public String Mobile;
	@SerializedName(value="ConfirmStatus", alternate = {"confirmStatus"})
	public Byte ConfirmStatus;
	@SerializedName(value="ConfirmType", alternate = {"confirmType"})
	public Byte ConfirmType;
	@SerializedName(value="Message", alternate = {"message"})
	public String message;
	@SerializedName(value="whatToDoStr", alternate = {"WhatToDoStr"})
	public String whatToDoStr;
	@SerializedName(value="FirebaseToken", alternate = {"firebaseToken"})
	public String FirebaseToken;

	public ConfirmSms2DTO() {
    }
	public static ConfirmSms2DTO fromBytes(byte[] bts){
		Gson gson = new Gson();
		String json = hutilities.decryptBytesToString(bts);
		return gson.fromJson(json, ConfirmSms2DTO.class);
	}
}