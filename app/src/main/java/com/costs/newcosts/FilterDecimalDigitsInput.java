package com.costs.newcosts;


import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilterDecimalDigitsInput implements InputFilter {

    private Pattern mPattern;

    public FilterDecimalDigitsInput() {
        mPattern = Pattern.compile("[0-9]*+((\\.[0-9]?)?)||(\\.)?");
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        if (dest.length() > 16)
            return "";

        Matcher matcher = mPattern.matcher(dest);
        if (!matcher.matches())
            return "";
        return null;
    }
}
