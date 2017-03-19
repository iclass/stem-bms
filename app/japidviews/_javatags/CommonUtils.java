package japidviews._javatags;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.management.openmbean.OpenDataException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;

import cn.bran.play.JapidPlayAdapter;
import models.WePerson;
import play.Logger;
import play.Play;
import play.i18n.Messages;
import play.mvc.Http.Cookie;
import play.mvc.Http.Request;
import play.mvc.Scope.Session;
import play.templates.JavaExtensions;

/**
 * @author Administrator
 *
 */
/**
 * @author mayan
 *
 */
public class CommonUtils {
	private static final String LOGGED_PERSON_ID = "loggedPersonId";
	private static final String KEEP_LOGIN_ID = "KEEP_LOGIN_ID";

	public static void savePersonToSession(Long personId) {
		if (personId != null) {
			Session.current().put(LOGGED_PERSON_ID, personId);
		} else {
			Session.current().remove(LOGGED_PERSON_ID);
		}
	}

	public static WePerson currentPerson() {
		String personId = currentPersonId();
		if (StringUtils.isNotBlank(personId)) {
			return WePerson.findById(Long.parseLong(personId));
		} else {
			return null;
		}
	}

	public static String getDataFromCookie(String arg) {
		Map<String, Cookie> cookies = play.mvc.Http.Request.current().cookies;
		Cookie cookie = cookies.get(arg);
		if (null != cookie) {
			return cookie.value;
		}
		return null;
	}

	public static boolean checkBackLogin() {
		String backLoginInfo = CommonUtils.getDataFromCookie("backLoginInfo");
		return StringUtils.isBlank(backLoginInfo) ? true : false;
	}

	public static String currentPersonId() {
		String personId = Session.current().get(LOGGED_PERSON_ID);
		if (personId == null) {
			personId = getDataFromCookie(KEEP_LOGIN_ID);
		}
		return personId;
	}

	public static String formatDate(Date date, String format) {
		return JavaExtensions.format(date, format);
	}

	public static String formatDate(Long date, String format) {
		return JavaExtensions.format(new Date(date), format);
	}

	public static Date getDate(String dateStr, String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		try {
			return formatter.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String formatNumber(Number number, String format) {
		return JavaExtensions.format(number, format);
	}

	public static Map<String, Integer> getInterval(Date beginTime, Date endTime) {
		Map<String, Integer> result = new HashMap();
		long seconds = (endTime.getTime() - beginTime.getTime()) / 1000;
		long dCount = seconds / 60 / 60 / 24;
		long hCount = (seconds / 60 / 60) % 24;
		long mCount = (seconds / 60) % (24 * 60);
		long sCount = seconds % (24 * 60 * 60);

		result.put("DAY", (int) dCount);
		result.put("HOUR", (int) hCount);
		result.put("MINUTE", (int) mCount);
		result.put("SECOND", (int) sCount);
		return result;
	}

	public static int getRateNum(long num1, long num2) {
		return num2 == 0 ? 0 : (int) (((float) num1 / num2) * 100);
	}

	public static String dealWithLinkAmp(String url) {
		return url.replaceAll("&amp;", "&");
	}

}
