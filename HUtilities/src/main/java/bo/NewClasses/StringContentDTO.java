package bo.NewClasses;

import com.google.gson.annotations.SerializedName;

public class StringContentDTO {
    @SerializedName(value="data", alternate = {"Data"})
    public String data;

    public static StringContentDTO getInstance(String dd){
        StringContentDTO res =  new StringContentDTO();
        res.data = dd;
        return res;
    }
}
