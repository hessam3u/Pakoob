package utils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class SHEncryptionAlgorithm
{
    static int[] TwoN = new int[] { 1, 2, 4, 8, 16, 32, 64, 128 };
    public static String DefaultHashTable5 = "abcdefGhijklmnopqrstuvwxyzAZSTQR";
    public static String DefaultHashTable6 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ01234556789+/";
    public static String Key = "abcdefGhijklmnopqrstuvwxyzAZSTQR";
    public static int baseNumber = 6;
    public static char EmptyChar = 'g';

    public static String EncryptFromBytesToString(byte[] plainarray, String Key)
    {
        String res = "";
        int bits = plainarray.length * 8;

        int Counter = 0;
        byte newChar = 0;
        for (int i = 0; i < bits; i++)
        {
            int index = i / 8;
            int bitId = i % 8;
            int bitValue = ((plainarray[index] & TwoN[bitId]) );
            if (bitValue > 0)
            {
                newChar |= (byte)(TwoN[Counter]);
            }

            Counter = ++Counter % baseNumber; // baseNumber
            if (Counter == 0)
            {
                res += Key.charAt(newChar);
                newChar = 0;
            }
        }
        if (Counter > 0)
        {
            res += Key.charAt(newChar);
            res += EmptyChar;
            res += (baseNumber - Counter);
        }
        return res;
    }
    public static byte[] DecryptFromStringToBytes(String cypherText, String Key)
    {
        int resByteCount = 0;
        int cypherLength = cypherText.length();
        if (cypherText.length() > 2 && cypherText.charAt(cypherText.length() - 2) == EmptyChar)
        {
            resByteCount = (baseNumber * (cypherText.length() - 2) - (int)(cypherText.charAt(cypherText.length() - 1) - '0')) / 8;
        }
        else
        {
            resByteCount = (baseNumber * cypherText.length()) / 8;
        }
        byte[] res = new byte[resByteCount];
        int[] cypherIndexes = new int[cypherLength];
        for (int i = 0; i < cypherLength; i++)
        {
            cypherIndexes[i] = GetCharIndexInKey(cypherText.charAt(i), Key);
        }

        int bits = resByteCount * 8;
        int Counter = 0;
        int resIndexCounter = 0;
        byte newChar = 0;
        for (int i = 0; i < bits; i++)
        {
            int index = i / baseNumber;
            int bitId = i % baseNumber;
            int bitValue = ((cypherIndexes[index] & TwoN[bitId]) );
            if (bitValue > 0)
            {
                newChar |= (byte)(TwoN[Counter]);
            }

            Counter = ++Counter % 8; // jaye baseNumber va 8 avaz mishe
            if (Counter == 0)
            {
                res[resIndexCounter] = (byte)newChar;
                newChar = 0;
                resIndexCounter++;
            }
        }
        return res;
    }

public enum Encoding{
        Unicode, ASCII
}
		//region Encrypt String
    /// <summary>
    /// Default value for Encoding : Unicode
    /// </summary>
    /// <param name="plainText"></param>
    /// <returns></returns>
    public static String EncryptString(String plainText, String Key)
    {
        return EncryptString(plainText, Key, Encoding.Unicode);
    }
    public static String EncryptString(String plainText, String Key, Encoding enc)
    {
        return EncryptFromBytesToString(plainText.getBytes(StandardCharsets.UTF_16LE), Key);
    }
    public static String DecryptString(String cypherText, String Key, Encoding enc) throws UnsupportedEncodingException {
        return new String(DecryptFromStringToBytes(cypherText, Key), "UTF-16LE");
//        return enc.GetString(DecryptFromStringToBytes(cypherText, Key));
    }
    public static String DecryptString(String cypherText, String Key) throws UnsupportedEncodingException {
        return DecryptString(cypherText, Key, Encoding.Unicode);
    }
		//endregion


		//region Encrypt - Integer and Decimal
    public static String EncryptInteger(Long Number, String Key)
    {
        return EncryptFromBytesToString(Number.toString().getBytes(StandardCharsets.US_ASCII), Key);
        //return EncryptFromBytesToString(Encoding.ASCII.GetBytes(Number.ToString()), Key);
    }
    public static long DecryptInteger(String value, String Key) throws UnsupportedEncodingException {
        String longStr= new String(DecryptFromStringToBytes(value, Key), "US-ASCII");
        return Long.getLong(longStr);
        //return (long)(Encoding.ASCII.GetString(DecryptFromStringToBytes(value, Key)));
    }
    public static String EncryptDecimal(Double Number, String Key)
    {
        return EncryptFromBytesToString(Number.toString().getBytes(StandardCharsets.US_ASCII), Key);
        //return EncryptFromBytesToString(Encoding.ASCII.GetBytes(Number.ToString()), Key);
    }
    public static double DecryptDecimal(String value, String Key) throws UnsupportedEncodingException {
        String longStr= new String(DecryptFromStringToBytes(value, Key), "US-ASCII");
        return Double.valueOf(longStr);
        //return (double)(Encoding.ASCII.GetString(DecryptFromStringToBytes(value, Key)));
    }
		//endregion


		//region Utils
    private static int GetCharIndexInKey(char ch, String Key)
    {
        return Key.indexOf(ch);
    }
		//endregion

    public static String HashLong(long x)
    {
        return SHEncryptionAlgorithm.EncryptInteger(x, SHEncryptionAlgorithm.DefaultHashTable6);
    }
}