package com.lte.lte;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedManagerUtil {
    private SharedPreferences.Editor editor;
    private SharedPreferences sp;
    private static SharedManagerUtil mSpUtil;

    private Context mContext;


    public static final String PROPERTY_KEY_USER_ID = "user_id";
    public static final String PROPERTY_KEY_AUTO_LOGIN = "auto_login"; // 자동 로그인
    public static final String PROPERTY_KEY_USER_PW = "user_pw";

    public static final String PROPERTY_KEY_SERVER_IP = "";

    public static final String SP_FILE_NAME = "SP";

    public static final boolean PROPERTY_INIT_BOOL_FALSE = false;
    public static final String PROPERTY_INIT_NULL_STRING = "";


    public static SharedManagerUtil getInstance(Context context) {
        if (mSpUtil == null && context != null) {
            mSpUtil = new SharedManagerUtil(context, SP_FILE_NAME);
        }
        return mSpUtil;
    }

    private SharedManagerUtil(Context context, String file) {
        mContext = context;

        sp = mContext.getSharedPreferences(file, Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    public void setUserID(String value) {
        editor.putString(PROPERTY_KEY_USER_ID, value);
        editor.commit();
    }

    public String getUserID() {
        return sp.getString(PROPERTY_KEY_USER_ID, PROPERTY_INIT_NULL_STRING);
    }

    public void setUserPW(String value) {
        editor.putString(PROPERTY_KEY_USER_PW, value);
        editor.commit();
    }

    public String getUserPW() {
        return sp.getString(PROPERTY_KEY_USER_PW, PROPERTY_INIT_NULL_STRING);
    }

    public void setAutoLogin(boolean value) {
        editor.putBoolean(PROPERTY_KEY_AUTO_LOGIN, value);
        editor.commit();
    }

    public Boolean getAutoLogin() {
        return sp.getBoolean(PROPERTY_KEY_AUTO_LOGIN, PROPERTY_INIT_BOOL_FALSE);
    }

    public void setServerIP(String value) {
        editor.putString(PROPERTY_KEY_SERVER_IP, value);
        editor.commit();
    }
    public String getServeriP() {
        return sp.getString(PROPERTY_KEY_SERVER_IP, PROPERTY_INIT_NULL_STRING);
    }
}
