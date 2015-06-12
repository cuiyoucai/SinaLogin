package com.zbh.login;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.annotation.Resource;

import org.apache.commons.codec.binary.Base64;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class SinaLogin {
	@Resource(name = "SinaService")
	public static void main(String[] args) throws Exception {
		System.err.println("开始登陆，获取user");
		// 设置微博用户名以及密码
		String user = requestAccessTicket("13544185508", "qqq2918859");
		if (user != "false") {
			System.err.println("获取成功");
			System.err.println("开始获取cookies");
			String cookies = sendGetRequest(user, null);
			System.err.println("cookies获取成功:" + cookies);
			System.err.println("开始发现热门微博");
			getFoundcontent(cookies);
			System.err.println("发现热门微博成功");
		} else
			System.err.println("user获取失败，请检查用户名或者密码是否正确!");
	}

	public static String sendGetRequest(String url, String cookies)
			throws MalformedURLException, IOException {
		HttpURLConnection conn = (HttpURLConnection) new URL(url)
				.openConnection();
		conn.setRequestProperty("Cookie", cookies);
		conn.setRequestProperty("Referer",
				"http://login.sina.com.cn/signup/signin.php?entry=sso");
		conn.setRequestProperty(
				"User-Agent",
				"Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:34.0) Gecko/20100101 Firefox/34.0");
		conn.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		BufferedReader read = new BufferedReader(new InputStreamReader(
				conn.getInputStream(), "gbk"));
		String line = null;
		StringBuilder ret = new StringBuilder();
		while ((line = read.readLine()) != null) {
			ret.append(line).append("\n");
		}
		StringBuilder ck = new StringBuilder();
		try {
			for (String s : conn.getHeaderFields().get("Set-Cookie")) {
				ck.append(s.split(";")[0]).append(";");
			}

		} catch (Exception e) {
		}
		return ck.toString();
	}

	@SuppressWarnings("deprecation")
	public static String requestAccessTicket(String username, String password)
			throws MalformedURLException, IOException {
		username = Base64.encodeBase64String(username.replace("@", "%40")
				.getBytes());
		HttpURLConnection conn = (HttpURLConnection) new URL(
				"https://login.sina.com.cn/sso/login.php?client=ssologin.js(v1.4.15)")
				.openConnection();
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Referer",
				"http://login.sina.com.cn/signup/signin.php?entry=sso");
		conn.setRequestProperty(
				"User-Agent",
				"Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:34.0) Gecko/20100101 Firefox/34.0");
		conn.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		DataOutputStream out = new DataOutputStream(conn.getOutputStream());
		out.writeBytes(String
				.format("entry=sso&gateway=1&from=null&savestate=30&useticket=0&pagerefer=&vsnf=1&su=%s&service=sso&sp=%s&sr=1280*800&encoding=UTF-8&cdult=3&domain=sina.com.cn&prelt=0&returntype=TEXT",
						URLEncoder.encode(username), password));
		out.flush();
		out.close();
		BufferedReader read = new BufferedReader(new InputStreamReader(
				conn.getInputStream(), "gbk"));
		String line = null;
		StringBuilder ret = new StringBuilder();
		while ((line = read.readLine()) != null) {
			ret.append(line).append("\n");
		}
		String res = null;
		try {
			res = ret.substring(ret.indexOf("https:"),
					ret.indexOf(",\"https:") - 1).replace("\\", "");
		} catch (Exception e) {
			res = "false";
		}
		return res;
	}

	// 发现热门微博网页源代码
	public static void getFoundcontent(String cookies)
			throws MalformedURLException, IOException {
		StringBuilder ret = new StringBuilder();
		for (int i = 1; i <= 3; i++) {
			String url = "http://d.weibo.com/?feed_filter=102803_ctg1_99993_-_ctg1_99993&feed_sort=102803_ctg1_99993_-_ctg1_99993&current_page=3&since_id=&page="
					+ i + "#feedtop";
			HttpURLConnection conn = (HttpURLConnection) new URL(url)
					.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Cookie", cookies);
			conn.setRequestProperty("Referer",
					"http://weibo.com/u/5608730432/home");
			conn.setRequestProperty(
					"User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.90 Safari/537.36");
			BufferedReader read = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "utf-8"));
			String line = null;
			while ((line = read.readLine()) != null) {
				ret.append(line).append("\n\r");
			}
		}
		String value = ret.toString();
		String dealvalue = dealData(value);
		String finalvalue = finalDeal(dealvalue);
		writeFile("C:\\Users\\Administrator\\Desktop\\weibo.txt", finalvalue,
				"utf-8");
	}

	/**
	 * 对获得的源代码进行预处理
	 * 
	 * @param text
	 * @return
	 */
	public static String dealData(String text) {
		String string = null;
		Document doc = Jsoup.parse(text);
		Elements element = doc.select("script");
		string = element
				.toString()
				.replace("\\n", "")
				.replace("\\r", "")
				.replace("\\t", "")
				.replace("\\", "")
				.replace("<script>FM.view({\"ns\":\"pl.content.homeFeed.index",
						"")
				+ " ";
		return string;
	}

	/**
	 * 再处理获得标题,正文,时间,转发次和评论次
	 */
	public static String finalDeal(String text)
			throws UnsupportedEncodingException, IOException {
		String string = null;
		StringBuilder sb = new StringBuilder();
		Document doc = Jsoup.parse(text);
		writeFile("text.html", doc.toString(), "GB2312");
		// 获取标题
		Elements title = doc.select("a[nick-name]");
		// 获取正文
		Elements content = doc.select("div[node-type=feed_list_content]");
		// 获取时间
		Elements time = doc.select("a[node-type=feed_list_item_date]");
		// 获取转发次
		Elements forwordCount = doc.select("span[node-type=forward_btn_text]");
		// 获取评论次
		Elements discuss = doc.select("a[action-type=fl_comment]");
		for (int i = 0; i < title.size(); i++) {
			String title_ = title.get(i).text();
			String content_ = content.get(i).text();
			String time_ = time.get(i).text();
			String forwordCount_ = forwordCount.get(i).text()
					.replace("转发 ", "");
			String discuss_ = discuss.get(i).text().replace("评论 ", "");
			System.out.println(title_ + " " + content_ + " " + time_ + " "
					+ forwordCount_ + " " + discuss_ + " ");
			// ----- here need to insert to mysql ------
			string = title_ + " " + content_ + " " + time_ + " "
					+ forwordCount_ + " " + discuss_ + " " + "\n\r";
			sb.append(string);
		}
		string = sb.toString();
		return string;
	}

	/**
	 * 写入文件
	 */
	public static void writeFile(String filepath, String value, String encoding)
			throws UnsupportedEncodingException, IOException {
		File file = new File(filepath);
		FileOutputStream fos = null;
		fos = new FileOutputStream(file, false);
		fos.write(value.getBytes(encoding));
		fos.close();
	}
}