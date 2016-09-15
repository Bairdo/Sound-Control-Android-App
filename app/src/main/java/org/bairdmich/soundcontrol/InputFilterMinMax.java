package org.bairdmich.soundcontrol;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * Created by Michael on 8/12/2015.
 */
public class InputFilterMinMax implements InputFilter {

    private int min;
    private int max;

    public  InputFilterMinMax(int min, int max){
        if (min > max){
            throw new IllegalArgumentException("min " + min + " cannot be greate than max " + max);
        }
        this.min = min;
        this.max = max;

    }


    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            // Remove the string out of destination that is to be replaced
            String newVal = dest.toString().substring(0, dstart) + dest.toString().substring(dend, dest.toString().length());
            // Add the new string in
            newVal = newVal.substring(0, dstart) + source.toString() + newVal.substring(dstart, newVal.length());
            int input = Integer.parseInt(newVal);
            if (isInRange(input))
                return null;
        } catch (NumberFormatException nfe) { }
        return "";
   }

    private boolean isInRange(int input){

        if (input >= min && input <= max){
            return true;
        }

        return false;
    }
}
