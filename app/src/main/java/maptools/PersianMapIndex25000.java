package maptools;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;

public class PersianMapIndex25000 {
    public Integer X;
    public Integer Y;
    public Integer Part;
    public String Direction;

    public String ToString()
    {
        return X.toString() + Y.toString() + Part.toString() + Direction.toLowerCase();
    }
    public String ToString(String format)
    {
        if (format == "L"){
         return  X.toString() + Y.toString() + " " + (Part == 1?"I":Part == 2?"II":Part == 3?"III":"IV") + " " + Direction.toUpperCase();
        }
        return X.toString() + Y.toString() + Part.toString() + Direction.toLowerCase();
    }

    /// <summary>
    /// topLeft, topRight, botRight, botLeft
    /// </summary>
    /// <returns></returns>
    public List<LatLng> GetBounds()
    {
        List<LatLng> res = new ArrayList<>();
        String cDirection = Direction.toLowerCase();

        double botLeft_Lng = ((this.X - 48) * .5 + 44) + (Part == 1 || Part == 2 ? .25 : 0) + (cDirection.equals("ne") || cDirection.equals("se") ? .125 : 0);
        double botLeft_Lat = ((this.Y - 40) * .5 + 25) + (Part == 1 || Part == 4 ? .25 : 0) + (cDirection.equals("nw") || cDirection.equals("ne") ? .125 : 0);
        LatLng botLeft = new LatLng(botLeft_Lat, botLeft_Lng);

        //add top left
        res.add(new LatLng(botLeft.latitude + .125, botLeft.longitude));
        //add top right
        res.add(new LatLng(botLeft.latitude + .125, botLeft.longitude + .125));
        //add bot right
        res.add(new LatLng(botLeft.latitude, botLeft.longitude + .125));
        //add bot left
        res.add(botLeft);

        return res;
    }

    public static PersianMapIndex25000 FromString(String input)
    {
        PersianMapIndex25000 res = new PersianMapIndex25000();
        if (input.length() < 7)
        {
            return res;
        }
        res.X = Integer.parseInt(input.substring(0, 2));
        res.Y = Integer.parseInt(input.substring(2, (2) + 2));
        res.Part = Integer.parseInt(input.substring(4, (4) + 1));
        res.Direction = input.substring(5, (5) + 2).toLowerCase();

        return res;

    }
    public static PersianMapIndex25000 ExtractMapIndexFromLongString(String input)
    {
        int len = input.length();
        for (int i = 0; i < len; i++)
        {
            if (Character.isDigit(input.charAt(i)))
            {
                if (
                        (len > i + 1 && Character.isDigit(input.charAt(i + 1))) &&
                                (len > i + 2 && Character.isDigit(input.charAt(i + 2))) &&
                                (len > i + 3 && Character.isDigit(input.charAt(i + 3))) &&
                                (len > i + 4 && Character.isDigit(input.charAt(i + 4)))
                )
                {
                    if (
                            (len > i + 6))
                    {
                        String sub = input.substring(i + 5, (i + 5) + 2).toLowerCase();
                        if (sub.equals("ne") || sub.equals("sw") || sub.equals("se") || sub.equals("nw"))
                        {
                            return PersianMapIndex25000.FromString(input.substring(i, (i) + 7));
                        }
                    }
                }
            }
        }
        return new PersianMapIndex25000();
    }
    static final int [][] devideParts =  new int[][]{{3, 2}, {4, 1}};
    static final String [][] devideDirections =  new String[][]{{"SW", "SE"}, {"NW", "NE"}};
    public static PersianMapIndex25000 FromLaglon(LatLng input)
    {
        PersianMapIndex25000 res = new PersianMapIndex25000();
        res.X = (int)(input.longitude*2)-40;
        res.Y = (int)(input.latitude*2)-10;

        if (res.X <= 0 || res.X >= 100 || res.Y <= 0   || res.Y>= 100 ){
            res.X = 0;
            res.Y = 0;
            return res;
        }

        int latDeci = getDecimalPart(input.latitude, 2);
        int lonDeci = getDecimalPart(input.longitude, 2);

        res.Part = devideParts[(int)(latDeci/25f) % 2][(int)(lonDeci/25f)%2];
        res.Direction = devideDirections[(int)(latDeci/12.5) % 2][(int)(lonDeci/12.5)%2];

        return res;
    }
    public static LatLngBounds stringToBounds(String input){
        List<LatLng> pmi = ExtractMapIndexFromLongString(input).GetBounds();
        LatLngBounds res = new LatLngBounds(pmi.get(3), pmi.get(1));
        return res;
    }

    private static int getDecimalPart(double d, int maxLen){
        String doubleAsText = Double.toString(d);
        double number = Double.parseDouble(doubleAsText);
        String []parts = doubleAsText.split("\\.");
        //int decimal = Integer.parseInt(doubleAsText.split("\\.")[0]);
        if (parts.length != 2)
            return 0;
        int fracLen = parts[1].length();
        int fractional = Integer.parseInt(parts[1].substring(0, Math.min(fracLen, maxLen)));
        return fractional;
    }
    public static List<LatLng> stringToBoundsList(String input){
        List<LatLng> pmi = ExtractMapIndexFromLongString(input).GetBounds();
        return pmi;
    }
    public PersianMapIndex25000()
    {
    }
}
