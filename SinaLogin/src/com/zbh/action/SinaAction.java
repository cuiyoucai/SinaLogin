package com.zbh.action;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionSupport;
import com.zbh.model.Sina;
import com.zbh.service.SinaService;

@Controller("SinaAction")
public class SinaAction extends ActionSupport {
	private static Logger logger = Logger.getLogger(SinaAction.class);

	private static final long serialVersionUID = 1L;
	@Resource(name = "SinaService")
	private SinaService sinaService;

	public String foundNews() throws MalformedURLException, IOException,
			ServletException {
		HttpServletRequest request = ServletActionContext.getRequest();
		String username = request.getParameter("userName");
		String password = request.getParameter("passWord");
		// String keyWord = request.getParameter("keyWord");
		// String pageCount = request.getParameter("pageCount");
		String user = requestAccessTicket(username, password);
		if (user != "false") {
			logger.info("获取成功");
			logger.info("开始获取user");
			String cookies = sendGetRequest(user, null);
			logger.info("cookies获取成功:" + cookies);
			logger.info("开始发现热门微博");
			Document doc = Jsoup.parse(getFoundcontent(cookies));
			// 获取标题
			Elements title = doc.select("a[nick-name]");
			// 获取正文
			Elements content = doc.select("div[node-type=feed_list_content]");
			// 获取时间
			Elements time = doc.select("a[node-type=feed_list_item_date]");
			// 获取转发次
			Elements forwordCount = doc
					.select("span[node-type=forward_btn_text]");
			// 获取评论次
			Elements discuss = doc.select("a[action-type=fl_comment]");
			for (int i = 0; i < title.size(); i++) {
				String title_ = title.get(i).text();
				String content_ = content.get(i).text();
				String time_ = time.get(i).text();
				String forwordCount_ = forwordCount.get(i).text()
						.replace("转发 ", "");
				String discuss_ = discuss.get(i).text().replace("评论 ", "");
				logger.info(title_ + " " + content_ + " " + time_ + " "
						+ forwordCount_ + " " + discuss_ + " ");
				// ----- here need to insert to mysql ------
				Sina sina = new Sina();
				sina.setTitle(title_);
				sina.setContent(content_);
				sina.setTime(time_);
				sina.setForwordCount(forwordCount_);
				sina.setDiscuss(discuss_);
				sinaService.insert(sina);
			}
			logger.info("发现热门微博完毕");
			// here to select
			List<Sina> list = sinaService.select();
			request.setAttribute("list", list);
			return "success";
		} else {
			logger.error("user获取失败，请检查用户名或者密码是否正确!");
			return "false";
		}
	}

	/**
	 * 把cookies放进请求里
	 * 
	 * @param url
	 * @param cookies
	 */
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

	/**
	 * 登录获取后的cookies
	 * 
	 * @param username
	 * @param password
	 * @return cookies
	 */
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

	// 获取热门微博网页的源代码
	public static String getFoundcontent(String cookies)
			throws MalformedURLException, IOException {
		StringBuilder ret = new StringBuilder();
		// int count = Integer.parseInt(pageCount);
		for (int i = 1; i <= 7; i++) {
			String url = "http://d.weibo.com/102803?feed_filter=102803_ctg1_9999_-_ctg1_9999&feed_sort=102803_ctg1_9999_-_ctg1_9999&current_page=15&since_id=&page="
					+ i + "#feedtop";
			HttpURLConnection conn = (HttpURLConnection) new URL(url)
					.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Cookie", cookies);
			// conn.setRequestProperty("Referer",
			// "http://weibo.com/u/5608730432/home");
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
		return dealvalue;
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
}
