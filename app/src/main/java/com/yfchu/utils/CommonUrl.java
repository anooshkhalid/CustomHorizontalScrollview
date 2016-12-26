package com.yfchu.utils;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class CommonUrl {

	/**
	 * 点击滚动ScrollView
	 * */
	public static final int SCROLL_ROLL = 0x01;

	/**
	 * 发送宽度数据
	 * */
	public static final int SETDATA = 0x02;

	/**
	 * 快速滑动标记
	 */
	public final static int FASTMOVE = 0x03;
}
