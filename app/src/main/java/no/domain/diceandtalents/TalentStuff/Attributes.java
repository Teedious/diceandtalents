package no.domain.diceandtalents.TalentStuff;

/**
 * Created by Florian on 12.12.2017.
 */

public class Attributes
{
    private static int[] values = {13,12,14,13,12,14,11,14};
    public static final String[] ATTR_NUM_STR = {
            "MU",
            "KL",
            "IN",
            "CH",
            "FF",
            "GE",
            "KO",
            "KK"
    };
    public static final int ATTR_MU=0;
    public static final int ATTR_KL=1;
    public static final int ATTR_IN=2;
    public static final int ATTR_CH=3;
    public static final int ATTR_FF=4;
    public static final int ATTR_GE=5;
    public static final int ATTR_KO=6;
    public static final int ATTR_KK=7;

    public static int[] getValues()
    {
        return values.clone();
    }

    public static boolean setValues(int[] values)
    {
        for (int i:values)
        {
           if(i<0)return false;
        }
        Attributes.values = values;
        return true;
    }
}
