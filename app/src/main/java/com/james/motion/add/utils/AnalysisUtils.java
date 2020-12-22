package com.james.motion.add.utils;
import android.content.Context;
import android.content.SharedPreferences;

public class AnalysisUtils {

	/**
	 * 从SharedPreferences中读取登录用户名
	 */
	public static String readLoginUserName(Context context){
		SharedPreferences sp=context.getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
		return sp.getString("loginUserName", "");
	}
}