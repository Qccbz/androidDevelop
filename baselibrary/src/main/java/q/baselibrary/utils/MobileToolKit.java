package q.baselibrary.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.Surface;
import cn.intwork.um3.core.Core;
import cn.intwork.um3.data.DataManager;
import cn.intwork.um3.data.MyApp;
import cn.intwork.um3.data.MyApp.TaxType;
import cn.intwork.um3.data.circle.AreaBean;
import cn.intwork.um3.data.enterprise.EnterpriseInfoBean;
import cn.intwork.um3.data.enterprise.StaffInfoBean;
import cn.intwork.um3.protocol.Protocol_Logout;
import cn.intwork.um3.service.UMService;
import cn.intwork.um3.ui.ActivateMainActivity;
import cn.intwork.um3.ui.MainActivity;
import cn.intwork.um3.ui.enterprise.ConfigEnterpriseActivity1;
import cn.intwork.umlx.ui.todo.WarningService;
import cn.intwork.umlxe.R;
import cn.intwork.version_enterprise.activity.AddressbookVMain;
import cn.intwork.version_enterprise.activity.MoreApp_EnterpriseVersion;
import cn.intwork.version_enterprise.db.dao.StaffInforBeanDao;
import cn.intwork.version_enterprise.toolkit.TabInfoUtil;

/**
 * 跟手机有关的工具类
 * 
 * @author www
 * 
 */
public class MobileToolKit {

	/**
	 * 获取剩余内存大小，获取出的大小给LRU使用
	 * 
	 * @author jinghq
	 * @category 获取剩余内存大小
	 * 
	 * */
	public static int getRemainMemorySize(Context context) {
		int memClass = ((ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
		return 1024 * 1024 * memClass / 8;
	}

	/**
	 * 检查文件夹是否存在
	 * 
	 * @param file
	 * @param isCreate
	 * @return
	 */
	public static boolean checkDir(File file, boolean isCreate) {
		return file.exists() && file.isDirectory() ? true : (isCreate ? file
				.mkdirs() : false);
	}

	/** @category SDCard是否可用 */
	public static boolean isSDCardEnable() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED) ? true : false;
	}

	/** @category 检查GPS设备是否可用 */
	public static boolean isGpsEnable(Context context) {
		LocationManager locationManager = ((LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE));
		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	/**
	 * @category 是用系统发送短信
	 * @see 多号码使用；进行间隔
	 * @param context
	 *            上下文对象
	 * @param number
	 *            电话号码，多号码用；进行间隔
	 * @param text
	 *            短信内容
	 * @author jinghq
	 * */
	public static void SendSMSBySystem(Context context, String number,
			String text) {
		Uri uri = Uri.parse("smsto:" + number);
		Intent it = new Intent(Intent.ACTION_SENDTO, uri);
		it.putExtra("sms_body", text);
		context.startActivity(it);
	}

	/**
	 * @category 使用系统发送电子邮件
	 * @param context
	 *            上下文对象
	 * @param uri
	 *            邮箱地址
	 * @author jinghq
	 * */
	public static void SendEmail(Context context, String uri) {
		Intent data = new Intent(Intent.ACTION_SENDTO);
		data.setData(Uri.parse("mailto:" + uri));
		data.putExtra(Intent.EXTRA_SUBJECT, "");
		data.putExtra(Intent.EXTRA_TEXT, "");
		context.startActivity(data);
	}

	/**
	 * @category 取得四位的一个随机数
	 * */
	public static int getRandomForPackId() {
		return (new Random().nextInt(9000) + 1000);
	}

	/**
	 * 获得IMSI
	 */
	public static String getIMSI(Context context) {
		if (context == null)
			return "";

		TelephonyManager mTelephonyMgr = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = mTelephonyMgr.getSubscriberId();
		return imsi == null ? "" : imsi;
	}

	/**
	 * 获得IMEI
	 */
	public static String getIMEI(Context context) {
		if (context == null)
			return "";
		TelephonyManager mTelephonyMgr = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = mTelephonyMgr.getDeviceId();
		return imei == null ? "" : imei;
	}

	/**
	 * 获取当前设置的电话号码 注意：这个方法有时候是获取不到的，有些卡本身就没有写入电话号码
	 */
	public static String getNativePhoneNumber(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getLine1Number();
	}

	/**
	 * Role:Telecom service providers获取手机服务商信息 <BR>
	 * 需要加入权限<uses-permission
	 * android:name="android.permission.READ_PHONE_STATE"/> <BR>
	 * 
	 */
	public static String getProvidersName(Context context) {
		String ProvidersName = null;
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		// 返回唯一的用户ID;就是这张卡的编号神马的
		String IMSI = telephonyManager.getSubscriberId();
		// IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
		QLog.i(IMSI);
		if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
			ProvidersName = "中国移动";
		} else if (IMSI.startsWith("46001")) {
			ProvidersName = "中国联通";
		} else if (IMSI.startsWith("46003")) {
			ProvidersName = "中国电信";
		}
		return ProvidersName;
	}

	/**
	 * 获取号码所属的运营商
	 * 
	 * @param number
	 * @return 0移动 1联通 2 电信 -1未匹配到
	 */
	public static int getNumberType(String number) {
		if ((number == null) || (number.length() == 0)) {
			return -1;
		}
		Pattern p = Pattern.compile(PATTERN_CMCMOBILENUM);
		Matcher m = p.matcher(number);
		if (m.find()) {
			return 0;
		}

		p = Pattern.compile(PATTERN_CUTMOBILENUM);
		m = p.matcher(number);
		if (m.find()) {
			return 1;
		}
		p = Pattern.compile(PATTERN_CTCMOBILENUM);
		m = p.matcher(number);
		if (m.find()) {
			return 2;
		}
		return -1;
	}

	/**
	 * 监测网络状态同时获得当前网络名称，返回”“就是没有网络
	 * 
	 * @param context
	 * @return
	 */
	public static String getCurrrentConnectionName(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkinfo = cm.getActiveNetworkInfo();
		if (networkinfo == null || !networkinfo.isAvailable())
			return "";

		return networkinfo.getTypeName();
	}

	/**
	 * 是否有手机或者wifi网络连接
	 * 
	 * @param context
	 * @return
	 */
	public static boolean ishaveLocalConnection(Context context) {
		ConnectivityManager connectManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		// mobile
		NetworkInfo mobileNet = connectManager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if ((mobileNet != null)
				&& (mobileNet.getState() == NetworkInfo.State.CONNECTED)) {
			return true;
		}
		// wifi
		NetworkInfo wifiNet = connectManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if ((wifiNet != null)
				&& (wifiNet.getState() == NetworkInfo.State.CONNECTED)) {
			return true;
		}
		return false;
	}

	/**
	 * 检查是否存在网络连接
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkConnected(Context context) {
		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return networkInfo != null && networkInfo.isConnected() ? true : false;
	}

	/**
	 * 发送短信
	 */
	public static void sendSmsDirectly(String number, String body) {
		if (number == null || body == null) {
			return;
		}

		QLog.i("发送短信到:" + number + ">>" + body);

		SmsManager sms = SmsManager.getDefault();
		try {
			sms.sendTextMessage(number, null, body, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * exit Applicaiton
	 */
	public static void exitApp() {

		if (MyApp.myApp != null) {
			// 登出协议
			if (MyApp.myApp.logout != null) {
				MyApp.myApp.logout.logout();
			} else {
				new Protocol_Logout().logout();
			}
			// 将第一次加载标记恢复
			if (MyApp.myApp.sp != null) {
				Editor e = MyApp.myApp.sp.edit();
				e.putBoolean("firstRun", true);
				e.putBoolean(MainActivity.LOAD, true);
				e.commit();
			}
			saveAllMessageId();// 保存messageId
			MyApp.myApp.saveConfigOthers(MyApp.myApp);
			if (MyApp.myApp.timer != null) {
				MyApp.myApp.timer.cancel();
			}
		}
		MyApp.isLogin = false;// 已退出
		AddressbookVMain.isHasShownPwdTip = false;

		if (UMService.umService != null) {
			UMService.umService.dismissTempNotification();
			UMService.umService.dissmissNotification();
			UMService.umService.stopHeartbeatTask();
			UMService.umService.stopForeground(true);
			UMService.umService.stopSelf();

		}
		if (WarningService.warnService != null) {
			WarningService.warnService.dismissTempNotification();
			WarningService.warnService.stopSelf();
		}

		System.exit(0);
		System.gc();
	}

	/**
	 * 企业退出
	 * 
	 * @param exitTips
	 *            非正常退出时需要提示的内容 正常退出可填空
	 * @param 是否是体验帐号下退出
	 */
	public static void exitEnterprise(Context context, String exitTips) {
		if (MyApp.myApp != null) {
			int curOrgid = MyApp.myApp.getOrgid();
			List<EnterpriseInfoBean> l = MyApp.db.findAllByWhere(
					EnterpriseInfoBean.class, "orgId=" + curOrgid);
			if (l.size() > 0) {
				EnterpriseInfoBean e = l.get(0);
				if (e != null) {
					e.setAutoLogin(false);
					MyApp.db.update(e);
				}
			}
			if (ConfigEnterpriseActivity1.createOrgidMoreTag == curOrgid) {
				ConfigEnterpriseActivity1.createOrgidMoreTag = -1;
				ConfigEnterpriseActivity1.createOrgidAddressTag = -1;
			}
			if (MyApp.myApp.taxOrgId == curOrgid) {
				MyApp.myApp.taxType = TaxType.unlogin;
			}
			saveAllMessageId();// 保存messageId
			MyApp.myApp.setLong(MyApp.LastUmid, 0);
			MyApp.myApp.setLong(MyApp.LastOrg, 0);
			MyApp.myApp.isEnterprise = false;
			MyApp.myApp.company = null;
			MyApp.myApp.isEnterpriseAdmin = false;
			MyApp.myApp.EnterpriseType = 0;
			MyApp.myApp.isLoginSuccess = false;
			MyApp.myApp.isAutoLogin = false;
			MyApp.myApp.myName = "";
			// 发送登出协议
			if (MyApp.myApp.logout != null) {
				MyApp.myApp.logout.logout();
			} else {
				new Protocol_Logout().logout();
			}

			String curOrgName = MyApp.myApp.getOrgName();
			if (StringToolKit.notBlank(exitTips)) {
				UIToolKit.ServiceToastShort(MyApp.myApp, exitTips);
			} else {
				if (StringToolKit.notBlank(curOrgName)) {
					UIToolKit.ServiceToastShort(MyApp.myApp, "退出“" + curOrgName
							+ "”成功");
				} else {
					UIToolKit.ServiceToastShort(MyApp.myApp, "退出成功");
				}
			}
		}
		AddressbookVMain.isHasShownPwdTip = false;
		MoreApp_EnterpriseVersion.isEditApplicationMode = false;
		TabInfoUtil.clearData();// 清空tab数据
		MyApp.isLogin = false;// 已退出
		Core.getInstance().stopReconnect();// 停止重连

		if (WarningService.warnService != null) {
			WarningService.warnService.dismissTempNotification();
			WarningService.warnService.stopSelf();
		}

		if (UMService.umService != null) {
			UMService.umService.dismissTempNotification();
			UMService.umService.dissmissNotification();
			UMService.umService.stopHeartbeatTask();
		}

		Intent intent = new Intent(context, ActivateMainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(ActivateMainActivity.IS_QUIT_LOGIN, true);
		context.startActivity(intent);

		if (MainActivity.act != null) {
			MainActivity.act.finish();
			MainActivity.act = null;
		}
	}

	/**
	 * 保存所有的消息id
	 */
	public static void saveAllMessageId() {
		if (MyApp.myApp != null) {
			SharedPreferences sp = MyApp.myApp.getSharedPreferences(
					"UM2config", 0);
			Editor editor = sp.edit();
			editor.putInt("messageId", MyApp.myApp.messageId);
			editor.putInt("roommsgId", MyApp.myApp.packid);
			editor.putInt("enterpriseMessageId",
					MyApp.myApp.enterpriseMessageId);
			editor.commit();
		}
	}

	/**
	 * 重启本应用程序
	 * 
	 * @author jinghq
	 * @category 重启软件
	 * @param Activity
	 *            act
	 * */
	public static void restart(Activity act) {
		Context base = act.getBaseContext();
		String packageName = base.getPackageName();
		Intent i = base.getPackageManager().getLaunchIntentForPackage(
				packageName);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		act.startActivity(i);
	}

	/**
	 * 创建快捷方式并保存此标志，如果用户手动删除就不再创建
	 */
	public static void createShortcut_old() {
		if (!MyApp.myApp.isActivated) {
			if (!hasShortcut()) {
				// Intent shortcut = new
				// Intent("com.android.launcher.action.INSTALL_SHORTCUT");
				//
				// //快捷方式的名称
				// shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,
				// context.getString(R.string.app_name));
				// shortcut.putExtra("duplicate", false); //不允许重复创建
				//
				// //指定当前的Activity为快捷方式启动的对象: 如 com.everest.video.VideoPlayer
				// //注意: ComponentName的第二个参数必须加上点号(.)，否则快捷方式无法启动相应程序
				// ComponentName comp = new ComponentName("cn.intwork.um2",
				// "cn.intwork.um2.ui.ActivateMainActivity");
				// shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new
				// Intent(Intent.ACTION_MAIN).setComponent(comp));
				//
				// //快捷方式的图标
				// ShortcutIconResource iconRes =
				// Intent.ShortcutIconResource.fromContext(context,
				// R.drawable.default_img);
				// shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
				// iconRes);
				//
				// context.sendBroadcast(shortcut);

				Intent intent2 = new Intent(Intent.ACTION_MAIN);
				intent2.addCategory(Intent.CATEGORY_LAUNCHER);
				intent2.setComponent(new ComponentName("cn.intwork.um3",
						"cn.intwork.um3.ui.ActivateMainActivity"));

				Intent intent = new Intent(
						"com.android.launcher.action.INSTALL_SHORTCUT");
				intent.putExtra(Intent.EXTRA_SHORTCUT_NAME,
						MyApp.myApp.getString(R.string.app_name));
				intent.putExtra("duplicate", false);
				intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent2);
				intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
						ShortcutIconResource.fromContext(MyApp.myApp,
								R.drawable.ic_launcher));

				MyApp.myApp.sendBroadcast(intent);
				// MyApp.myApp.saveConfig(MyApp.myApp);
			}
		}
	}

	/**
	 * @author ytwk_zhou
	 * @see 新的创建快捷方式方法，如果用户手动删除就不再创建
	 */
	public static void createShortcut(Activity act) {
		if (!MyApp.myApp.isActivated) {
			if (!hasShortcut()) {
				Intent shortcut = new Intent(
						"com.android.launcher.action.INSTALL_SHORTCUT");

				// 快捷方式的名称

				shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,
						MyApp.myApp.getString(R.string.app_name));

				shortcut.putExtra("duplicate", false); // 不允许重复创建

				Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);
				shortcutIntent.addCategory(Intent.CATEGORY_LAUNCHER);
				shortcutIntent.setClass(act.getApplicationContext(),
						act.getClass());
				shortcutIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
						| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

				shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);

				// 快捷方式的图标

				ShortcutIconResource iconRes = ShortcutIconResource
						.fromContext(MyApp.myApp, R.drawable.ic_launcher);

				shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);

				MyApp.myApp.sendBroadcast(shortcut);
				sharePreferenceSaveCreateShortCut();
			}
		}
	}

	/**
	 * 检查是否已生成快捷方式 判断方法可能不准确
	 */
	public static boolean hasShortcut() {
		boolean ishasshortcut = false;
		ishasshortcut = isHasShortCutBySharePreference();
		if (ishasshortcut)
			return ishasshortcut;
		String url = "";
		if (Build.VERSION.SDK_INT < 8) {
			url = "content://com.android.launcher.settings/favorites?notify=true";
		} else {
			url = "content://com.android.launcher2.settings/favorites?notify=true";
		}
		ContentResolver resolver = MyApp.myApp.getContentResolver();
		// Cursor cursor = resolver
		// .query(Uri.parse(url),
		// new String[]{"title"},
		// null,null,null);
		Cursor cursor = resolver
				.query(Uri.parse(url),
						new String[] { "title" },
						"title=?",
						new String[] { MyApp.myApp.getString(R.string.app_name) },
						null);
		QLog.O("cursor==null:" + (cursor == null));
		if (cursor != null && cursor.getCount() > 0) {
			ishasshortcut = true;
			// 测试代码
			// cursor.moveToFirst();
			// for(int i=0;i<cursor.getCount();i++){
			// QLog.O("title:"+cursor.getString(0));
			// cursor.moveToNext();
			// }

			cursor.close();
		}
		QLog.i("==ishasshortcut===" + ishasshortcut);
		return ishasshortcut;
	}

	private static void sharePreferenceSaveCreateShortCut() {
		QLog.O("sharePreferenceSaveCreateShortCut >>>>>>>>>>>>>>>>>>>>");
		SharedPreferences sp = MyApp.myApp.getSharedPreferences("UM2config", 0);
		Editor editor = sp.edit();
		editor.putBoolean("isHasCreateShortcut", true);
		editor.commit();
	}

	/** 是否创建过快捷方式 SharePreference 记载 **/
	private static boolean isHasShortCutBySharePreference() {
		boolean hasShortCut = false;
		SharedPreferences sp = MyApp.myApp.getSharedPreferences("UM2config", 0);
		hasShortCut = sp.getBoolean("isHasCreateShortcut", false);
		QLog.O("isHasShortCutBySharePreference hasShortCut:" + hasShortCut
				+ " 000000000000000");
		return hasShortCut;
	}

	public static List<Object> DomReadXML(Context context) {
		List<Object> list = new ArrayList<Object>();
		AreaBean ab = null;
		String re = "";
		DocumentBuilderFactory docBuilderFactory = null;
		DocumentBuilder docBuilder = null;
		Document doc = null;
		try {
			docBuilderFactory = DocumentBuilderFactory.newInstance();
			docBuilder = docBuilderFactory.newDocumentBuilder();

			// xml file 放到 assets目录中的
			// java.io.InputStream inStream1 =
			// context.getClass().getClassLoader()
			// .getResourceAsStream("area1.xml");
			InputStream inStream1 = readInputStreamByName("area1.xml", context);
			doc = docBuilder.parse(inStream1);
			// root element
			Element root = doc.getDocumentElement();
			// Do something here
			// get a NodeList by tagname
			NodeList nodeList = root.getElementsByTagName("Cell");
			for (int i = 1; i < nodeList.getLength(); i++) {
				if (i > 3) {
					Node nd = nodeList.item(i);
					re = nd.getFirstChild().getFirstChild().getNodeValue();
					// System.out.println("re:"+re);
					switch (i % 4) {
					case 0:
						ab = new AreaBean();
						ab.setSerialno(Integer.parseInt(re));
						// System.out.println("-------------------------------------------");
						break;
					case 1:
						ab.setSname(re);
						break;
					case 2:
						ab.setFserialno(Integer.parseInt(re));
						break;
					case 3:
						ab.setLeveltype(Integer.parseInt(re));
						list.add(ab);
						break;
					}
				}
			}
			// re = getAttributes().getNamedItem(“number”).getNodeValue();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			doc = null;
			docBuilder = null;
			docBuilderFactory = null;
		}
		return list;
	}

	public static InputStream readInputStreamByName(String name, Context context) {
		// 创建一个输入流
		AssetManager assets = context.getAssets();
		InputStream is = null;
		try {
			// 使用asset管理器打开文件
			is = assets.open(name);
		} catch (IOException e) {
			// 捕获IOE
			e.printStackTrace();
		}
		// 如果输入流为空，则读取失败返回空
		return is == null ? null : is;
	}

	// / <summary>
	// / 匹配移动手机号
	// / </summary>
	public static String PATTERN_CMCMOBILENUM = "^1(3[4-9]|5[012789]|8[78])\\d{8}$";
	// / <summary>
	// / 匹配电信手机号
	// / </summary>
	public static String PATTERN_CTCMOBILENUM = "^18[09]\\d{8}$";
	// / <summary>
	// / 匹配联通手机号
	// / </summary>
	public static String PATTERN_CUTMOBILENUM = "^1(3[0-2]|5[56]|8[56])\\d{8}$";
	// / <summary>
	// / 匹配CDMA手机号
	// / </summary>
	public static String PATTERN_CDMAMOBILENUM = "^1[35]3\\d{8}$";

	/** 通过反射获取系统的硬件信息 **/
	public static String getMobileInfo() {
		StringBuffer sb = new StringBuffer();
		// 通过反射获取系统的硬件信息
		try {

			Field[] fields = Build.class.getDeclaredFields();
			for (Field field : fields) {
				// 反射 ,获取私有的信息
				field.setAccessible(true);
				String name = field.getName();
				String value = field.get(null).toString();
				sb.append(name + "=" + value);
				sb.append("\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	/**
	 * 获取手机型号
	 * 
	 * @param con
	 * @return
	 */
	public static String getMobileModel(Context con) {
		// android.os.Build.MODEL; // 手机型号
		// android.os.Build.BRAND;//手机品牌
		return "Android-" + Build.MODEL;
		// String brand = android.os.Build.BRAND;
		// String model = android.os.Build.MODEL;
		// return model.contains(brand) ? model : brand + " " + model;
	}
	
	/**
	 * 获取手机信息,登录时附加
	 * 
	 * @param con
	 * @return
	 */
	public static String getMobileInfo(Context con) {

		try {
			StringBuilder builder = new StringBuilder("手机型号:");
			builder.append(Build.MODEL).append(",Android版本:")// 手机型号
					.append(Build.VERSION.RELEASE).append(",IMEI码:")// 手机系统
					.append(getIMEI(con));// 手机IMEI码
			return builder.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 获取版本号
	 * 
	 * @return 当前应用的版本号
	 */
	public static String getVersion(Context context) {
		try {
			PackageManager m = context.getPackageManager();
			PackageInfo info = m.getPackageInfo(context.getPackageName(), 0);
			return info.versionName + "\n";
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 判断当前登录用户是否是该组织的管理员
	 * 
	 * @return ture表示是该组织的管理员
	 */
	public static boolean isAdmain() {

		StaffInfoBean s = StaffInforBeanDao.queryOneByPhone(DataManager
				.getInstance().mySelf().key(), MyApp.myApp.getOrgid());
		return s != null ? (s.getType() == 0 ? true : false) : false;
	}

	/**
	 * 获取手机mac地址
	 * 
	 * @param Activity
	 *            mActivity
	 * @return String
	 */
	public static String getMacAddress() {

		WifiManager wifi = (WifiManager) MyApp.myApp
				.getSystemService(Context.WIFI_SERVICE);
		if (wifi != null) {
			WifiInfo info = wifi.getConnectionInfo();
			return info == null ? "" : info.getMacAddress();
		}
		return "";
	}

	public static void setCameraDisplayOrientation(Activity activity,
			int cameraId, Camera camera) {
		Camera.CameraInfo info = new Camera.CameraInfo();
		Camera.getCameraInfo(cameraId, info);
		int rotation = activity.getWindowManager().getDefaultDisplay()
				.getRotation();
		int degrees = 0;
		switch (rotation) {
		case Surface.ROTATION_0:
			degrees = 0;
			break;
		case Surface.ROTATION_90:
			degrees = 90;
			break;
		case Surface.ROTATION_180:
			degrees = 180;
			break;
		case Surface.ROTATION_270:
			degrees = 270;
			break;
		}

		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360; // compensate the mirror
		} else { // back-facing
			result = (info.orientation - degrees + 360) % 360;
		}
		camera.setDisplayOrientation(result);
	}
	
	public static Bitmap rotateBitmap(Bitmap bm, String sourcepath) {

		int rotate = 0;
		try {
			File imageFile = new File(sourcepath);
			ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());

			int orientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);

			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_270:
				rotate = 270;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				rotate = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_90:
				rotate = 90;
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return bm;
		}

		if (rotate == 0) {
			return bm;
		}

		try {
			Matrix matrix = new Matrix();
			matrix.postRotate(rotate);
			return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(),
					matrix, true);
		} catch (Exception e) {
			e.printStackTrace();
			return bm;
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			return bm;
		}
	}
	
	@SuppressLint("InlinedApi")
	public static String[] getMultiSDCards(Context context) {

		int num = 0;
		String[] sdcards = null;
		// 获取sdcard的路径：外置和内置
		try {
			StorageManager sm = (StorageManager) context
					.getSystemService(Context.STORAGE_SERVICE);
			String[] strs = (String[]) sm.getClass()
					.getMethod("getVolumePaths", null).invoke(sm, null);
			int len = strs.length;
			if (len > 0) {
				sdcards = new String[len];
				for (int i = 0; i < len; i++) {
					sdcards[i] = "";
					String status = (String) sm.getClass()
							.getMethod("getVolumeState", String.class)
							.invoke(sm, strs[i]);
					if (status.equals(Environment.MEDIA_MOUNTED)) {
						num++;
						sdcards[i] = strs[i];
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (num > 1) {// 有多个sdcard时返回值
				return sdcards;
			}
		}
		return null;
	}

}
