package bo.entity;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import utils.hutilities;

public class PersonalInfoDTO
{
	@SerializedName(value="CCustomerId", alternate = {"cCustomerId"})
	public Integer CCustomerId;
	@SerializedName(value="Name", alternate = {"name"})
	public String Name;
	@SerializedName(value="Family", alternate = {"family"})
	public String Family;
	@SerializedName(value="FatherName", alternate = {"fatherName"})
	public String FatherName;
	@SerializedName(value="NationalCode", alternate = {"nationalCode"})
	public String NationalCode;
	@SerializedName(value="Mobile", alternate = {"mobile"})
	public String Mobile;
	@SerializedName(value="BirthDate", alternate = {"birthDate"})
	public Integer BirthDate;
	@SerializedName(value="Sex", alternate = {"sex"})
	public Byte Sex;
	@SerializedName(value="PhotoAddress", alternate = {"photoAddress"})
	public String PhotoAddress;
	@SerializedName(value="Email", alternate = {"email"})
	public String Email;
	@SerializedName(value="Credit", alternate = {"credit"})
	public Double Credit;
	@SerializedName(value="RegStatus", alternate = {"regStatus"})
	public Byte RegStatus;
	@SerializedName(value="CityId", alternate = {"cityId"})
	public Integer CityId;
	@SerializedName(value="Data", alternate = {"data"})
	public String data;
	@SerializedName(value="Message", alternate = {"message"})
	public String message;
	@SerializedName(value="WhatToDoStr", alternate = {"whatToDoStr"})
	public String whatToDoStr;
	@SerializedName(value="ClubName", alternate = {"clubName"})
	public String ClubName;
	@SerializedName(value="ClubLogo", alternate = {"clubLogo"})
	public String ClubLogo;
	@SerializedName(value="Int4", alternate = {"int4"})
	public Integer Int4;
	@SerializedName(value="CityName", alternate = {"cityName"})
	public String CityName;
	@SerializedName(value="Int1", alternate = {"int1"})
	public Integer Int1;

	public PersonalInfoDTO() {
		BirthDate = 0;
		Sex =1;
		RegStatus = 0;
		CCustomerId = 0;
		CityId = 0;
    }
	public static PersonalInfoDTO fromBytes(byte[] bts){
		Gson gson = new Gson();
		String json = hutilities.decryptBytesToString(bts);
		return gson.fromJson(json, PersonalInfoDTO.class);
	}

	//From TaraModel
	public static final int Int1_User = 0;
	public static final int Int1_ClubAdmin = 1;
	public static final int Int1_StoreAdmin = 2;
	public static final int Int1_HouseAdmin = 4;
}