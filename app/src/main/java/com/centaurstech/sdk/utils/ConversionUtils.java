package com.centaurstech.sdk.utils;


import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import com.centaurstech.qiwu.common.User;
import com.centaurstech.qiwu.utils.UIUtils;
import com.centaurstech.qiwu.utils.VerifyUtils;
import com.centaurstech.sdk.R;
import com.centaurstech.sdk.activity.BaseActivity;
import com.centaurstech.sdk.activity.LoginActivity;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import static com.centaurstech.qiwu.QiWu.getContext;

/**
 * @author Leon(黄长亮)
 * @describe 转换工具类
 * @date 2018/7/23
 */

public class ConversionUtils {


    /***
     * 保留两位小数
     * @param second
     * @return
     */
    public static String stringToNumber2(String second) {
        if (TextUtils.isEmpty(second)) {
            return "";
        }
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(Double.valueOf(second));
    }

    /**
     * 获取对应天气
     *
     * @param weather
     * @return
     */
    public static int weather(int weather) {
        switch (weather) {
            case 0:
            case 2:
            case 38:
                return R.mipmap.weather_0;
            case 1:
            case 3:
                return R.mipmap.weather_1;
            case 4:
                return R.mipmap.weather_4;
            case 5:
                return R.mipmap.weather_5;
            case 6:
                return R.mipmap.weather_6;
            case 7:
                return R.mipmap.weather_7;
            case 8:
                return R.mipmap.weather_8;
            case 9:
                return R.mipmap.weather_9;
            case 10:
            case 11:
            case 13:
            case 19:
                return R.mipmap.weather_10;
            case 12:
                return R.mipmap.weather_12;
            case 14:
                return R.mipmap.weather_14;
            case 15:
                return R.mipmap.weather_15;
            case 16:
            case 17:
            case 18:
                return R.mipmap.weather_16;
            case 20:
                return R.mipmap.weather_20;
            case 21:
            case 22:
                return R.mipmap.weather_21;
            case 23:
            case 24:
            case 25:
            case 37:
                return R.mipmap.weather_23;
            case 26:
            case 27:
            case 28:
            case 29:
                return R.mipmap.weather_26;
            case 30:
                return R.mipmap.weather_30;
            case 31:
                return R.mipmap.weather_31;
            case 32:
            case 33:
                return R.mipmap.weather_32;
            case 34:
            case 35:
            case 36:
                return R.mipmap.weather_34;
            case 99:
                return R.mipmap.weather_99;
            default:
                return 0;
        }
    }

    /**
     * 提供精确加法计算的add方法
     *
     * @param value1 被加数
     * @param value2 加数
     * @return 两个参数的和
     */
    public static double add(double value1, double value2) {
        BigDecimal b1 = new BigDecimal(Double.valueOf(value1));
        BigDecimal b2 = new BigDecimal(Double.valueOf(value2));
        return b1.add(b2).doubleValue();
    }

    /***
     * 两个double相加
     * @param price1
     * @param price2
     * @return
     */
    public static String getSummation(String price1, String price2) {
        try {
            if (TextUtils.isEmpty(price1) || TextUtils.isEmpty(price2)) {
                return "0";
            }
            BigDecimal bd1 = new BigDecimal(price1);
            BigDecimal bd2 = new BigDecimal(price2);
            return bd1.add(bd2).setScale(2, BigDecimal.ROUND_DOWN).toString();
        } catch (Exception e) {

        }
        return "0";
    }

    public static String getDuration(long duration) {
        long hour = duration / 60;
        long minute = duration % 60;
        return (hour == 0 ? "" : hour + "h") + (minute == 0 ? "" : minute + "min");
    }

    /**
     * 距离转换
     *
     * @param distance
     * @return
     */
    public static String distance2(float distance) {
        if (distance > 1000) {
            DecimalFormat df = new DecimalFormat("#.0");
            distance = distance / 1000;
            return df.format(distance) + UIUtils.getString(R.string.kilometre);
        }
        return (int) distance + "m";
    }

    public static ScreenWidthLevel getScreenWidthLevel() {
        if (getScreensWidth() >= 1080) {
            return ScreenWidthLevel.MAX;
        } else if (getScreensWidth() >= 720) {
            return ScreenWidthLevel.MIDDLE;
        } else {
            return ScreenWidthLevel.MIX;
        }
    }

    /**
     * 获取屏幕宽度（像素）
     *
     * @return
     */
    public static int getScreensWidth() {
        return getContext().getResources().getDisplayMetrics().widthPixels; // 屏幕宽度（像素）;
    }

    public enum ScreenWidthLevel {
        MAX, MIDDLE, MIX
    }

    public static String getStringNumber2(String number) {
        if (TextUtils.isEmpty(number)) {
            return "";
        }
        NumberFormat nf = new DecimalFormat("#.##");
        return nf.format(new BigDecimal(number));
    }

    public static void doStartApplicationWithPackageName(String packagename, BaseActivity mActivity) {

        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        PackageInfo packageinfo = null;
        try {
            packageinfo = mActivity.getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            return;
        }

        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);

        // 通过getPackageManager()的queryIntentActivities方法遍历
        List<ResolveInfo> resolveinfoList = mActivity.getPackageManager()
                .queryIntentActivities(resolveIntent, 0);

        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            // packagename = 参数packname
            String packageName = resolveinfo.activityInfo.packageName;
            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
            String className = resolveinfo.activityInfo.name;
            // LAUNCHER Intent
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            // 设置ComponentName参数1:packagename参数2:MainActivity路径
            ComponentName cn = new ComponentName(packageName, className);
            intent.setComponent(cn);
            mActivity.startActivity(intent);
        }
    }

    /***
     * 两个double相加
     * @param price1
     * @param price2
     * @return
     */
    public static String addition2Price(String price1, String price2) {
        if (TextUtils.isEmpty(price1)) {
            price1 = "0.0";
        }
        if (TextUtils.isEmpty(price2)) {
            price2 = "0";
        }
        BigDecimal bigDecimal1 = new BigDecimal(price1);
        BigDecimal bigDecimal2 = new BigDecimal(price2);
        String price = bigDecimal1.add(bigDecimal2).toString();
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(Double.valueOf(price));
    }

    public static boolean checkLogin(final BaseActivity baseActivity) {
        return checkLogin(baseActivity, 999);
    }

    public static boolean checkLogin(final BaseActivity baseActivity, final int requestCode) {
        if (User.isLogin()) {
            return true;
        } else {
            baseActivity.launchActivity(LoginActivity.class, requestCode);
            return false;
        }
    }

    /**
     * 复制文字到剪切板
     *
     * @param text
     */
    public static void copyText(CharSequence text) {
        ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setText(text);
        ToastUtils.show(R.string.already_copy);
    }

    public static String getMoney(double money) {
        if (money == 0) {
            return "----";
        } else {
            return "" + money;
        }
    }

    /**
     * EditText设置文本数据
     *
     * @param et
     * @param charSequence
     */
    public static void setText(EditText et, CharSequence charSequence) {
        if (TextUtils.isEmpty(charSequence)) {
            et.setText("");
        } else {
            et.setText(charSequence);
            et.setSelection(et.getText().toString().length());
        }
    }

    public static void numberAndLetter(final EditText editText, final int length) {
        editText.addTextChangedListener(new TextWatcher() {
            public String mContactPhone = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mContactPhone = editText.getText().toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    if (VerifyUtils.numberAndLetter(s)) {
                        if (s.length() > length) {
                            setText(editText, s.toString().substring(0, length));
                        }
                    } else {
                        setText(editText, mContactPhone);
                    }
                }
            }
        });
    }

    public static void email(final EditText editText, final int length) {
        editText.addTextChangedListener(new TextWatcher() {
            public String mContactPhone = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mContactPhone = editText.getText().toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    if (VerifyUtils.email(s)) {
                        if (s.length() > length) {
                            setText(editText, s.toString().substring(0, length));
                        }
                    } else {
                      setText(editText, mContactPhone);
                    }
                }
            }
        });
    }

    public static void setNameLimit(final EditText editText) {
        setNameLimit(editText, 12);
    }

    public static void setNameLimit(final EditText editText, final int length) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (getStringLength(String.valueOf(s)) > length) {
                   setText(editText, s.toString().substring(0, s.length() - 1));
                }
            }
        });
    }


    public static void setNicknameLimit(final EditText editText, final int length) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (getStringLength(String.valueOf(s)) > length) {
                   setText(editText, s.toString().substring(0, s.length() - 1));
                }
            }
        });
    }

    public static void setLetterLimit(final EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            public String mLetter = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mLetter = editText.getText().toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    if (VerifyUtils.letter(s)) {
                        if (s.length() > 18) {
                           setText(editText, s.toString().substring(0, 18));
                        }
                    } else {
                        setText(editText, mLetter);
                    }
                }
            }
        });
    }

    /**
     * 获取字符串长度
     *
     * @param s
     * @return
     */
    public static int getStringLength(String s) {
        if (s == null)
            return 0;
        char[] c = s.toCharArray();
        int len = 0;
        for (int i = 0; i < c.length; i++) {
            len++;
            if (!isLetter(c[i])) {
                len++;
            }
        }
        return len;
    }

    public static boolean isLetter(char c) {
        int k = 0x80;
        return c / k == 0;
    }

    public static void setPhoneTextLimit(final EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            public String mContactPhone = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mContactPhone = editText.getText().toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    if (VerifyUtils.number(s)) {
                        if (s.length() > 11) {
                           setText(editText, s.toString().substring(0, 11));
                        }
                    } else {
                        setText(editText, mContactPhone);
                    }
                }
            }
        });
    }

    public static String encryptIDCode(String idCode) {
        if (idCode.length() > 8) {
            return idCode.substring(0, idCode.length() - 8) + "********";
        } else {
            return idCode;
        }
    }

    public static String encryptIDCode4(String idCode) {
        if (idCode.length() > 4) {
            return idCode.substring(0, idCode.length() - 4) + "****";
        } else {
            return idCode;
        }
    }

    /**
     * 距离转换
     *
     * @param distance
     * @return
     */
    public static String distance(float distance) {
        DecimalFormat df = new DecimalFormat("#.0");
        if (distance > 1000) {
            return df.format((distance / 1000)) + UIUtils.getString(R.string.kilometre);
        }
        return (int) distance + "m";
    }

    /**
     * 时间转换
     *
     * @param second 秒
     * @return
     */
    public static String time(long second) {
        if (second < 0) return 0 + UIUtils.getString(R.string.second);
        long days = second / 86400;//转换天数
        second = second % 86400;//剩余秒数
        long hours = second / 3600;//转换小时数
        second = second % 3600;//剩余秒数
        long minutes = second / 60;//转换分钟
        second = second % 60;//剩余秒数
        if (days > 0) {
            return (days * 24 + hours) + UIUtils.getString(R.string.hour) + minutes + "分";
        } else {
            if (hours > 0) {
                if (minutes > 0) {
                    return hours + UIUtils.getString(R.string.hour) + minutes + "分";
                } else {
                    return hours + UIUtils.getString(R.string.hour);
                }
            } else {
                return minutes + "分";
            }
        }
    }

}
