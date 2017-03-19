package utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import models.json.URLResult;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import play.Logger;
import play.Play;
import play.Play.Mode;
import utils.HTTPClientUtils.Builder;
import cn.bran.play.JapidPlayAdapter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Request;
import com.ning.http.client.RequestBuilder;
import com.ning.http.client.Response;
import com.ning.http.multipart.FilePart;
import com.ning.http.multipart.StringPart;

public class FileUtil {

	public static String AUDIO_SERVER = Play.configuration.getProperty("audioSever_url");

	private static final String FILE_SERVER_BASEURL = "http://www.iclass.cn:9001";

	public static String BUCKT_NAME = Play.configuration.getProperty("bucketName");
	public static String SOURCE = Play.configuration.getProperty("source");

	public static String FILE_SERVER = Play.configuration.getProperty("fileSever_Baseurl");

	private static final String RESOURCE_SERVER_URL = Play.configuration.getProperty("fileSever_url");

	private class Result {
		public boolean succ;
		public String html;
		public Integer duration;
	}

	public class androidAudioResult {
		public boolean succ;
		public String html;
		public Long pageId;
	}

	public static List<String> cutAudio(String splitFileName, String splitTime) {
		Builder build = new Builder();
		build.addParam("audioName", splitFileName);
		build.addParam("audioType", "mp3");
		build.addParam("bucketName", BUCKT_NAME);
		build.addParam("times", splitTime);
		String splitUrlResult = HTTPClientUtils.postMethod(FILE_SERVER + "/application/cutAudio", build.end());
		if (StringUtils.isBlank(splitUrlResult)) {
			return Collections.EMPTY_LIST;
		}
		List<String> cutResult = new Gson().fromJson(splitUrlResult, new TypeToken<List<String>>() {
		}.getType());
		return cutResult;
	}

	public static URLResult uploadAudio(File file) {
		final AsyncHttpClient client = new AsyncHttpClient();
		try {
			final Response response = client.preparePost(AUDIO_SERVER).addBodyPart(new FilePart("qqfile", file))
					.addBodyPart(new StringPart("bucketName", BUCKT_NAME)).addBodyPart(new StringPart("source", SOURCE))
					.execute().get();
			final String responseBody = response.getResponseBody("utf8");
			System.err.println(responseBody);
			try {
				return new Gson().fromJson(responseBody, URLResult.class);
			} catch (Exception e) {
				Logger.error("[EndServer has not started.]");
			}
		} catch (Exception e) {
			Logger.error(e, e.getMessage());
		} finally {
			client.close();
		}
		return null;
	}

	public static URLResult uploadResource(File file) {
		final AsyncHttpClient client = new AsyncHttpClient();
		try {
			final Response response = client.preparePost(RESOURCE_SERVER_URL).addBodyPart(new FilePart("qqfile", file))
					.addBodyPart(new StringPart("bucketName", BUCKT_NAME)).addBodyPart(new StringPart("source", SOURCE))
					.execute().get();
			final String responseBody = response.getResponseBody("utf8");
			System.err.println(responseBody);
			try {
				return new Gson().fromJson(responseBody, URLResult.class);
			} catch (Exception e) {
				Logger.error("[EndServer has not started.]");
			}
		} catch (Exception e) {
			Logger.error(e, e.getMessage());
		} finally {
			client.close();
		}
		return null;
	}

	public static androidAudioResult uploadAndroidAudio(File file) {
		final AsyncHttpClient client = new AsyncHttpClient();
		try {
			final Response response = client.preparePost(AUDIO_SERVER).addBodyPart(new FilePart("qqfile", file))
					.addBodyPart(new StringPart("bucketName", BUCKT_NAME)).addBodyPart(new StringPart("source", SOURCE))
					.execute().get();
			final String responseBody = response.getResponseBody("utf8");
			try {
				androidAudioResult result = new Gson().fromJson(responseBody, androidAudioResult.class);
				result.pageId = Long.valueOf(file.getName().substring(0, file.getName().lastIndexOf(".")));
				return result;
			} catch (Exception e) {
				Logger.error("[EndServer has not started.]");
			}
		} catch (Exception e) {
			Logger.error(e, e.getMessage());
		} finally {
			client.close();
		}
		return null;
	}

	public static String uploadFile(File file) {
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		Request request = new RequestBuilder("POST").setBody(file).setFollowRedirects(true).setUrl(FILE_SERVER_BASEURL)
				.build();
		try {
			return asyncHttpClient.executeRequest(request).get().getResponseBody();
		} catch (InterruptedException e) {
			Logger.error(e, e.getMessage());
		} catch (ExecutionException e) {
			Logger.error(e, e.getMessage());
		} catch (IOException e) {
			Logger.error(e, e.getMessage());
		} finally {
			if (!asyncHttpClient.isClosed()) {
				asyncHttpClient.close();
			}
		}
		return null;
	}

	public static void deleteFile(File file) {
		try {
			file.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void zipByLinux(String fileName) throws IOException, InterruptedException {
		String tmpPath = System.getProperty("user.dir") + "/tmp/";
		Process proc = Runtime.getRuntime().exec(
				"7zr u -uq0 -r -mx=0 -mmt -ms=200m " + fileName + ".7z " + fileName + "/", null, new File(tmpPath));
		if (proc != null) {
			Logger.info("压缩状态号：[" + proc.waitFor() + "]");
			proc.destroy();
		}
	}

	// 存储文件
	public static synchronized void writeHtml(String filePath, String info) {
		PrintWriter pw = null;
		try {
			File writeFile = Play.getFile("/tmp/" + filePath);
			boolean isExit = writeFile.exists();
			if (isExit != true) {
				writeFile.createNewFile();
			} else {
				writeFile.delete();
				writeFile.createNewFile();
			}
			pw = new PrintWriter(new FileOutputStream(writeFile));
			pw.println(info);
			pw.close();
		} catch (Exception ex) {
			Logger.error(ex.getMessage());
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}

	public static String readHtml(String fileURL) {
		// 从utl中读取html存为str
		String str = "";
		try {
			URL url = new URL(fileURL);
			InputStream is = url.openStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "gb2312"));
			while (br.ready()) {
				str += br.readLine() + "\n";
			}
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return str;
	}

	public static String httpReadHtml(String fileURL) {
		HttpClient httpClient = new HttpClient();
		GetMethod getMethod = new GetMethod(fileURL);
		String html = "";
		try {
			int statusCode = httpClient.executeMethod(getMethod);
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + getMethod.getStatusLine());

			}
			// 读取内容
			byte[] responseBody = getMethod.getResponseBody();
			html = new String(responseBody);

		} catch (Exception e) {
			System.err.println("页面无法访问");
		} finally {
			getMethod.releaseConnection();
		}
		return html;
	}

	public static void createFile(String fileURL, String desPath) {
		if (StringUtils.isBlank(fileURL) || !fileURL.contains("http")) {
			return;
		}
		String fileName = getFileName(fileURL);
		File file = new File(desPath + "/" + fileName);
		File srcFile = new File(fileURL);
		try {
			FileUtils.copyFile(srcFile, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void downloadFile(String fileURL, String filePath) {
		URL theURL = null;
		URLConnection con = null;
		try {
			theURL = new URL(fileURL);
			con = theURL.openConnection();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if (con != null) {
			String urlPath = con.getURL().getFile();
			String fileFullName = urlPath.substring(urlPath.lastIndexOf("/") + 1);
			String path = filePath + "/" + fileFullName;
			if (fileFullName != null) {
				byte[] buffer = new byte[4 * 1024];
				int read;
				File fileFolder = new File(filePath);
				if (!fileFolder.exists()) {
					fileFolder.mkdirs();
				}
				InputStream in = null;
				FileOutputStream os = null;
				try {
					in = con.getInputStream();
					os = new FileOutputStream(path);
					while ((read = in.read(buffer)) > 0) {
						os.write(buffer, 0, read);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						if (os != null) {
							os.close();
						}
						if (in != null) {
							in.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}
		}

	}

	public static String getFileName(String fileURL) {
		return fileURL.substring(fileURL.lastIndexOf("/") + 1);
	}

	public static void copyFolder(String srcURL, String desURL) {
		File[] file = (new File(srcURL)).listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return !(pathname.getName().contains("html") || pathname.getName().contains("json"));
			}
		});
		for (int i = 0; i < file.length; i++) {
			if (file[i].isFile()) {
				// 复制文件
				try {
					if (file[i].getName().contains("html")) {
						// continue;
					}
					FileUtils.copyFile(file[i], new File(desURL + file[i].getName()));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (file[i].isDirectory()) {
				// 复制目录
				String sourceDir = srcURL + File.separator + file[i].getName();
				String targetDir = desURL + File.separator + file[i].getName();
				copyDirectiory(sourceDir, targetDir);
			}
		}
	}

	public static void copyDirectiory(String sourceDir, String targetDir) {
		// 新建目标目录
		File targetFolder = Play.getFile(targetDir);
		if (!targetFolder.exists() && !targetFolder.isDirectory()) {
			targetFolder.mkdirs();
		}
		// 获取源文件夹当前下的文件或目录
		File[] file = (new File(sourceDir)).listFiles();
		for (int i = 0; i < file.length; i++) {
			if (file[i].isFile()) {
				if (file[i].getName().contains("html")) {
					continue;
				}
				// 源文件
				File sourceFile = file[i];
				// 目标文件
				File targetFile = new File(new File(targetDir).getAbsolutePath() + File.separator + file[i].getName());
				try {
					FileUtils.copyFile(sourceFile, targetFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (file[i].isDirectory()) {
				// 准备复制的源文件夹
				String dir1 = sourceDir + "/" + file[i].getName();
				// 准备复制的目标文件夹
				String dir2 = targetDir + "/" + file[i].getName();
				copyDirectiory(dir1, dir2);
			}
		}
	}

	public static File downloadFilePrallerByStream(String[] data) {
		// long time1 = System.currentTimeMillis();
		File fileFolder = new File(data[1].substring(0, data[1].lastIndexOf("/")));
		if (!fileFolder.exists()) {
			fileFolder.mkdirs();
		}
		File outfile = new File(data[1]);
		if (!outfile.exists()) {
			try {
				outfile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			URL url = new URL(data[0]);
			try (InputStream in = new BufferedInputStream(url.openStream());
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					OutputStream fos = new BufferedOutputStream(new FileOutputStream(outfile));) {
				byte[] buf = new byte[1024];
				int n = 0;
				while (-1 != (n = in.read(buf))) {
					out.write(buf, 0, n);
				}

				byte[] response = out.toByteArray();

				fos.write(response);
				// long time2 = System.currentTimeMillis();
				// File testFile = new File(data[1]);
				// System.err.println("文件名："+data[1]+"
				// 文件大小:"+testFile.length()+"---下载结束，耗时:"+(time2-time1));
				in.close();
				out.close();
				fos.close();
				return outfile;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}