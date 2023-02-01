package bo.entity;

import java.util.Calendar;

public class DownloadRequest {

    public MobileInfoDTO mobileInfoDTO;
    public int NbMapId;
    public String dDate;
    public byte NbBuyType;
    public static DownloadRequest getInstance(int _NbMapId, byte NbBuyType){
        DownloadRequest res = new DownloadRequest();
        res.mobileInfoDTO = MobileInfoDTO.instance();
        res.NbMapId = _NbMapId;
        res.dDate = Calendar.getInstance().toString();
        res.NbBuyType = NbBuyType;
        return res;
    }
    public DownloadRequest(){}
}
