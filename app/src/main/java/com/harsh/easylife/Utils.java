package com.harsh.easylife;

import android.os.Environment;

import java.io.File;

public class Utils {
	public static File appFolder(){
		File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/EasyLife/");
		if (!file.exists()){
			file.mkdir();
		}
		return file;
	}
}
