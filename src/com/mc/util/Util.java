package com.mc.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.nrs.utils.HttpAssistFile;
import com.xy.fy.util.StaticVarUtil;

public class Util {

	private final static String TAG = "util";
	private final static String infosFloder = "/xuptscore/devInfos";
	private final static String loginMessageTxt = infosFloder + "/login.txt";

	/**
	 * 用来存储设备信息和异常信息 Map<String,String> : mLogInfo
	 * 
	 * @since 2013-3-21下午8:46:15
	 */
	/**
	 * getDeviceInfo:{获取设备参数信息} ──────────────────────────────────
	 * 
	 * @param paramContext
	 * @throws
	 * @since I used to be a programmer like you, then I took an arrow in the
	 *        knee　Ver 1.0
	 *        ──────────────────────────────────────────────────────
	 *        ────────────────────────────────────────────────
	 *        2013-3-24下午12:30:02 Modified By Norris
	 *        ────────────────────────────
	 *        ───────────────────────────────────────
	 *        ───────────────────────────────────
	 */
	public static void saveDeviceInfo(Context paramContext) {

		Map<String, String> mLogInfo = new HashMap<String, String>();
		try {
			// 获得包管理器
			PackageManager mPackageManager = paramContext.getPackageManager();
			// 得到该应用的信息，即主Activity
			PackageInfo mPackageInfo = mPackageManager.getPackageInfo(
					paramContext.getPackageName(),
					PackageManager.GET_ACTIVITIES);
			if (mPackageInfo != null) {
				String versionName = mPackageInfo.versionName == null ? "null"
						: mPackageInfo.versionName;
				String versionCode = mPackageInfo.versionCode + "";
				mLogInfo.put("versionName", versionName);
				mLogInfo.put("versionCode", versionCode);
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		// 反射机制
		Field[] mFields = Build.class.getDeclaredFields();
		// 迭代Build的字段key-value 此处的信息主要是为了在服务器端手机各种版本手机报错的原因
		for (Field field : mFields) {
			try {
				field.setAccessible(true);
				mLogInfo.put(field.getName(), field.get("").toString());
				Log.d(TAG, field.getName() + ":" + field.get(""));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		StringBuffer mStringBuffer = new StringBuffer();
		for (Map.Entry<String, String> entry : mLogInfo.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			mStringBuffer.append(key + "=" + value + "\r\n");
		}

	   String mFileName = ((TelephonyManager) paramContext
				.getSystemService(paramContext.TELEPHONY_SERVICE))
				.getDeviceId()
				+ ".txt";
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			try {
				File mDirectory = new File(
						Environment.getExternalStorageDirectory() + infosFloder);

				Log.v(TAG, mDirectory.toString());
				if (!mDirectory.exists())
					mDirectory.mkdir();
				FileOutputStream mFileOutputStream = new FileOutputStream(
						mDirectory + "/" + mFileName);
				mFileOutputStream.write(mStringBuffer.toString().getBytes());
				mFileOutputStream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static String getTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH/mm/ss");
		System.out.println();
		return sdf.format(new Date());
	}

	/**
	 * 返回是否将 客户端信息上传至服务器 并且将 登录信息 加入到 文件中
	 * 
	 * @param paramContext
	 * @return
	 */
	public static boolean isRecordLoginMessage(Context paramContext) {

		File loginMsgLogFile = new File(Environment.getExternalStorageDirectory()+loginMessageTxt);
		String msg = getAppVersion(paramContext);
		try {
			if (loginMsgLogFile.exists()) {
				writeDevMsgToLog(loginMsgLogFile, msg,true);

				return true;
			} else {
				loginMsgLogFile.createNewFile();
				writeDevMsgToLog(loginMsgLogFile, msg,false);
				
				return false;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;

	}

	private static void writeDevMsgToLog(File loginMsgLogFile, String msg,boolean isAppend)
			throws FileNotFoundException, IOException {
		FileOutputStream fos = new FileOutputStream(loginMsgLogFile,
				isAppend);
		fos.write(msg.getBytes());
		fos.close();
	}

	/**
	 * 上传用户登录信息
	 * @param paramContext
	 */
	public static void uploadLoginMsg(final Context paramContext){
		new Thread(new Runnable() {

			
			public void run() { // TODO Auto-generated method stub
				String mFileName = Environment.getExternalStorageDirectory()
						+ loginMessageTxt;
				
				HttpAssistFile.uploadFile(
						new File(mFileName),
						"loginmsg");
				
			}
		}).start();		
	}
	/**
	 * 上传 客户端手机信息
	 * @param paramContext
	 */
	public static void uploadDevInfos(final Context paramContext) {
		// 上传服务器

		new Thread(new Runnable() {

			public void run() { // TODO Auto-generated method stub
				String mFileName = Environment.getExternalStorageDirectory()
						+ infosFloder + "/" +((TelephonyManager) paramContext
						.getSystemService(paramContext.TELEPHONY_SERVICE))
						.getDeviceId()
						+ ".txt";
				
				HttpAssistFile.uploadFile(
						new File(mFileName),
						"devsdk");
				
			}
		}).start();

	}

	private static String getAppVersion(Context paramContext) {
		try {
			// 获得包管理器
			PackageManager mPackageManager = paramContext.getPackageManager();
			// 得到该应用的信息，即主Activity
			PackageInfo mPackageInfo = mPackageManager.getPackageInfo(
					paramContext.getPackageName(),
					PackageManager.GET_ACTIVITIES);
			StringBuffer sb = new StringBuffer();
			if (mPackageInfo != null) {
				String versionName = mPackageInfo.versionName == null ? "null"
						: mPackageInfo.versionName;
				String versionCode = mPackageInfo.versionCode + "";
				sb.append(getTime() + "\t" + versionCode + "--" + versionName
						+ "\n");
			}
			return sb.toString();
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}

	public static int getAndroidSDKVersion() {
		int version = 0;
		try {
			version = Integer.valueOf(android.os.Build.VERSION.SDK);
		} catch (NumberFormatException e) {
			Log.e("getAndroidSDKVersion", e.toString());
		}
		return version;
	}

	/**
	 * 将图片保存到本地
	 * 
	 * @param bmp
	 * @param username
	 * @return
	 */
	public static boolean saveBitmap2file(Bitmap bmp, String username) {
		CompressFormat format = Bitmap.CompressFormat.JPEG;
		int quality = 100;
		OutputStream stream = null;
		try {
			stream = new FileOutputStream(StaticVarUtil.PATH + "/" + username
					+ ".JPEG");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return bmp.compress(format, quality, stream);
	}

	// 从网络获取图片,并保存找到本地
	public static Bitmap getBitmap(String pictureUrl) {
		URL url = null;
		Bitmap bitmap = null;

		InputStream in = null;
		try {
			if (pictureUrl != null) {
				url = new URL(pictureUrl);
				HttpURLConnection httpURLConnection = (HttpURLConnection) url
						.openConnection();
				httpURLConnection.setConnectTimeout(6000);// 最大延迟6000毫秒
				httpURLConnection.setDoInput(true);// 连接获取数据流
				httpURLConnection.setUseCaches(true);// 用缓存
				in = httpURLConnection.getInputStream();
				bitmap = BitmapFactory.decodeStream(in);
			} else {
				return null;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return bitmap;

	}

	// 将文件转化为图片
	public static Bitmap convertToBitmap(String path, int w, int h) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		// 设置为ture只获取图片大小
		opts.inJustDecodeBounds = true;
		opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
		// 返回为空
		BitmapFactory.decodeFile(path, opts);
		int width = opts.outWidth;
		int height = opts.outHeight;
		float scaleWidth = 0.f, scaleHeight = 0.f;
		if (width > w || height > h) {
			// 缩放
			scaleWidth = ((float) width) / w;
			scaleHeight = ((float) height) / h;
		}
		opts.inJustDecodeBounds = false;
		float scale = Math.max(scaleWidth, scaleHeight);
		opts.inSampleSize = (int) scale;
		WeakReference<Bitmap> weak = new WeakReference<Bitmap>(
				BitmapFactory.decodeFile(path, opts));
		return Bitmap.createScaledBitmap(weak.get(), w, h, true);
	}

	private static boolean haveChar(String str) {
		try {
			int num = Integer.valueOf(str);// 把字符串强制转换为数字
			return false;// 如果是数字，返回True
		} catch (Exception e) {
			return true;// 如果抛出异常，返回False
		}

	}

	/**
	 * check url requestData
	 * 
	 * @param data
	 * @param viewstate
	 * @return
	 */
	public static String checkRankRequestData(String data, String viewstate) {
		// TODO Auto-generated method stub
		String realXh = "";
		String realTime = Passport.jiemi(viewstate,
				String.valueOf(new char[] { 2, 4, 8, 8, 2, 2 }));
		HashMap<String, String> xhAndXnMap = new HashMap<String, String>();
		try {
			realXh = Passport.jiemi(data, realTime);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return realXh;
	}

	private static boolean hasDigit(String content) {

		boolean flag = false;

		Pattern p = Pattern.compile(".*\\d+.*");

		Matcher m = p.matcher(content);

		if (m.matches())

			flag = true;

		return flag;

	}

	public static boolean hasDigitAndNum(String str) {
		if (haveChar(str) & hasDigit(str)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 从listHerf中获取具体 tittle中的herf
	 * 
	 * @param tittle
	 * @return
	 */
	public static String getURL(String tittle) {
		String result = "";
		for (int i = 0; i < StaticVarUtil.listHerf.size(); i++) {
			HashMap<String, String> map = StaticVarUtil.listHerf.get(i);
			if (map.get("tittle").equals(tittle)) {
				result = map.get("herf");
			}
		}
		result = result.replace("%3D", "=");
		result = result.replace("%26", "&");
		result = result.replace("%3f", "?");
		return result;
	}

	/**
	 * 获取软件版本号
	 */
	public static String getVersion(Context context) {
		String version = "";
		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo pt = pm.getPackageInfo(context.getPackageName(), 0);
			version = pt.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return version;
	}
}
