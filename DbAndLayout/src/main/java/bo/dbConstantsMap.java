package bo;

import bo.sqlite.AppDatabaseMap;

public class dbConstantsMap {
    public static AppDatabaseMap appDB;
    public static ApiInterfaceFcm apiFcm = ApiClientFcm.getClient(60).create(ApiInterfaceFcm.class);
}
