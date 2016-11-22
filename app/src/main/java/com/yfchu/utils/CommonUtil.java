package com.yfchu.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

public class CommonUtil {

	/***
	 * 获取app的版本号
	 * 
	 * @author weixi liang
	 */
	public static String getAppCurrentVersion(Context mContext) {
		String versionName = "";
		try {
			versionName = mContext.getPackageManager().getPackageInfo(
					mContext.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return versionName;
	} 

	/**
	 * @param mContext
	 * @param px
	 * @return
	 */
	public static int convertPxToDp(Context mContext, int px) {
		WindowManager wm = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		float logicalDensity = metrics.density;
		int dp = Math.round(px / logicalDensity);
		return dp;
	}

	/**
	 * @param mContext
	 * @param dp
	 * @return
	 */
	public static int convertDpToPx(Context mContext, int dp) {
		return Math.round(TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, dp, mContext.getResources()
						.getDisplayMetrics()));
	}
	
	public static float dp2px(Context mContext,float dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				mContext.getResources().getDisplayMetrics());
	}

	/**
	 * 判断网络连接
	 * 
	 * @param context
	 * @return
	 */
	public static boolean hasNetwork(Context context) {
		try {
			ConnectivityManager con = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo workinfo = con.getActiveNetworkInfo();
			if (workinfo == null || !workinfo.isAvailable()) {
				return false;
			}
		} catch (Exception e) {
			// TODO: handle exception
			return true;
		}
		return true;
	}

	/**
	 * 手机号码格式检查
	 * 
	 * @param paramString
	 * @return
	 * @description
	 * @version 1.0
	 * @author weixi liang
	 * 
	 */
	public static boolean isValidMobiNumber(String paramString) {
		String regex = "^1\\d{10}$";
		if (paramString.matches(regex)) {
			return true;
		}
		return false;
	}
	
	/**
	 * 身份证检查
	 * 
	 * @param paramString
	 * @return
	 * @description
	 * @version 1.0
	 * @author yfchu
	 * 
	 */
	public static boolean isValidIdentity(String paramString) {
		String regex = "(\\d{14}[0-9a-zA-Z])|(\\d{17}[0-9a-zA-Z])";
		if (paramString.matches(regex)) {
			return true;
		}
		return false;
	}
	
	/**
     * 读取txt
     * */
	public static String getString(InputStream inputStream) {  
	    InputStreamReader inputStreamReader = null;  
	    try {  
	        inputStreamReader = new InputStreamReader(inputStream, "gbk");  
	    } catch (UnsupportedEncodingException e1) {  
	        e1.printStackTrace();  
	    }  
	    BufferedReader reader = new BufferedReader(inputStreamReader);  
	    StringBuffer sb = new StringBuffer("");  
	    String line;  
	    try {  
	        while ((line = reader.readLine()) != null) {  
	            sb.append(line);  
	            sb.append("\n");  
	        }  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    }  
	    return sb.toString();  
	} 
	
	/**
     * 替换特殊字符
     * */
	public static String ToDBC(String input) {  
		   char[] c = input.toCharArray();  
		   for (int i = 0; i< c.length; i++) {  
		       if (c[i] == 12288) {  
		         c[i] = (char) 32;  
		         continue;  
		       }if (c[i]> 65280&& c[i]< 65375)  
		          c[i] = (char) (c[i] - 65248); 
		  }  
		   return new String(c);  
	}  
	
	/**
     * 替换特殊字符
     * */
	public static String ToSBC(String input) { 
        char c[] = input.toCharArray(); 
        for (int i = 0; i < c.length; i++) { 
            if (c[i] == ' ') { 
                c[i] = '\u3000'; 
            } else if (c[i] < '\177') { 
                c[i] = (char) (c[i] + 65248); 
            } 
        } 
        return new String(c); 
    } 
		
	/**
     * 替换特殊字符
     * */
	public static String StringFilter(String str) throws PatternSyntaxException{  
	    str=str.replaceAll("【","[").replaceAll("】","]").replaceAll("！","!")
	    		.replaceAll("。","。").replaceAll("：",":").replaceAll("；",";").replaceAll("，",",");//替换中文标号  
	    String regEx="[『』]"; // 清除掉特殊字符  
	    Pattern p = Pattern.compile(regEx);  
	    Matcher m = p.matcher(str);  
	    return m.replaceAll("").trim();  
	}  
	
	/**
	 * 控制变量null时返回类型
	 * */
	public static String isOnNull(String s,int type) //1为字符串，2为数字
	{
		if(s == null && type == 1)
			s="";
		if(s == null && type == 2)
			s="0";
		return s; 
	}
	
	/**
     * 数组取最小
     * */
    public static int getMin(int[] arr) 
	{
		int min= 0 ;//初始化为数组中任意一个角标
		for (int x=1;x<arr.length ;x++ )
		{
			if(arr[x]<arr[min])
				min = x;
		}
		return arr[min];
	}
    
    /**
     * 截取字符串
     * */
    public static String getSplit(String[] items,int index,String qu)
    {
    	String item="";
		String[] item_split = null;
        String getItem="";
    	if(qu.equals(""))
    	{
            try {
            	item_split=items[index].toString().split("_");
                getItem=CommonUtil.isOnNull(item_split[0], 1);
    		} catch (Exception e) {
    			item_split=new String[]{};
    		}
    	}
    	else
    	{
            try {
            	item_split=qu.split("_");
                getItem=CommonUtil.isOnNull(item_split[0], 1);
    		} catch (Exception e) {
    			item_split=new String[]{};
    		}
    	}
        if(!getItem.equals(""))
        	item= getItem;
        return item;
    }
    
    /**
     * 短时振动
     * */
    public static void setVibrator(Context mContext)
    {
    	/* 
         * 想设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到 
         * */  
		Vibrator vibrator = (Vibrator)mContext.getSystemService(mContext.VIBRATOR_SERVICE);  
        long [] pattern = {0,50};  
        vibrator.vibrate(pattern,-1);
    }
    
    /**
     * 获得手机dpi
     **/
    public static float getDpi(Context m)
    {
    	float mDesityDpi=0;
    	DisplayMetrics metrics = m.getResources().getDisplayMetrics();
		mDesityDpi = (float)metrics.densityDpi/320;
		if(mDesityDpi<1)
			mDesityDpi=1;
		return mDesityDpi;
    }
}
