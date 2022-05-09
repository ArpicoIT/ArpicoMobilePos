package com.arpico.groupit.marksys.arpicomobilepos.Common;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Created by Sampath-IT on 2017-07-20.
 */

public class ValidationHelper {
    private SharedPreferences sp, config;
    private ComHelper comHelper;

    private SharedPreferences.Editor ed;

    // Regular Expression
    // you can change the expression based on your need
    private static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String PHONE_REGEX = "\\d{3}\\d{7}";

    // Error Messages
    private static final String REQUIRED_MSG = "Required";
    private static final String EMAIL_MSG = "Invalid Email";
    private static final String PHONE_MSG = "##########";
    boolean valid = false;
    Context mContext;

    public ValidationHelper(Context mContext, SharedPreferences sp, SharedPreferences config) {
        this.mContext = mContext;
        this.sp = sp;
        this.config = config;

        comHelper = new ComHelper(mContext, sp);
    }


    // call this method when you need to check email validation
    public static boolean isEmailAddress(EditText editText, boolean required) {
        return isValid(editText, EMAIL_REGEX, EMAIL_MSG, required);
    }

    // call this method when you need to check phone number validation
    public static boolean isPhoneNumber(EditText editText, boolean required) {
        return isValid(editText, PHONE_REGEX, PHONE_MSG, required);
    }

    public boolean isValidDate(String date) {
        String[] m_date = date.split("-");
        String newDate = m_date[1] + "/" + m_date[2] + "/" + m_date[0];

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        // declare and initialize testDate variable, this is what will hold
        // our converted string

        Date testDate = null;

        // we will now try to parse the string into date form
        try {
            testDate = sdf.parse(newDate);
        }

        // if the format of the string provided doesn't match the format we
        // declared in SimpleDateFormat() we will get an exception

        catch (ParseException e) {
            comHelper.alert("the date you provided is in an invalid date format.");
            return false;
        }

        // dateformat.parse will accept any date as long as it's in the format
        // you defined, it simply rolls dates over, for example, december 32
        // becomes jan 1 and december 0 becomes november 30
        // This statement will make sure that once the string
        // has been checked for proper formatting that the date is still the
        // date that was entered, if it's not, we assume that the date is invalid

        if (!sdf.format(testDate).equals(newDate)) {
            comHelper.alert("The date that you provided is invalid.");
            return false;
        }

        // if we make it to here without getting an error it is assumed that
        // the date was a valid one and that it's in the proper format

        return true;

    } // end isValidDate

    // return true if the input field is valid, based on the parameter passed
    public static boolean isValid(EditText editText, String regex, String errMsg, boolean required) {

        String text = editText.getText().toString().trim();
        // clearing the error, if it was previously set by some other values
        editText.setError(null);

        // text required and editText is blank, so return false
        if (required && !hasText(editText)) return false;

        // pattern doesn't match so returning false
        if (required && !Pattern.matches(regex, text)) {
            editText.setError(errMsg);
            return false;
        }

        return true;
    }

    // check the input field has any text or not
    // return true if it contains text otherwise false
    public static boolean hasText(EditText editText) {

        String text = editText.getText().toString().trim();
        editText.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            editText.setError(REQUIRED_MSG);
            return false;
        }

        return true;
    }

    public boolean setEmptyEditTextError(EditText editText, String name, boolean validate) {
        if (validate) {
            if (editText.getText().toString().isEmpty()) {
                editText.requestFocus();
                if (name.contains("Please Select")) {
                    editText.setError(name);
                } else {
                    editText.setError("Please Enter " + name);
                }
                valid = true;
            }
        } else {
            editText.setError(null);
            valid = false;
        }

        return valid;
    }

    public boolean setEmptySpinnerError(Spinner spinner, boolean validate) {
        if (validate) {
            if (spinner.getSelectedItemPosition() == 0) {
                spinner.requestFocus();
                comHelper.alert(spinner.getSelectedItem().toString());
                valid = true;
            }
        }
        return valid;
    }

    public void setEnableDissableText(EditText editText, String setText, boolean status) {
        if (status) {
            editText.setText("");
        } else {
            editText.setText(setText);
        }

        editText.setFocusable(status);
        editText.setFocusableInTouchMode(status);
        editText.setClickable(status);
    }
    
    public boolean isNullOrEmpty(String string) {
        return TextUtils.isEmpty(string);
    }

    public boolean isNumeric(String string) {
        return TextUtils.isDigitsOnly(string);
    }


}
