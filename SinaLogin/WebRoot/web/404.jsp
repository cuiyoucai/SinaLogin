<html xmlns="http://www.w3.org/1999/xhtml">
<head>

<title>用户名密码错误</title>

<meta http-equiv=content-type content="text/html; charset=UTF-8">

<meta http-equiv="refresh" content="3;url=file:../index.jsp">

<meta content="mshtml 6.00.2800.1106" name=generator>

<style type="text/css">
.style1 {
	text-align:center;
	color: #ff0000;
	font-weight: bold;
}
</style>
<script>
	var t = 10;//设定跳转的时间 
	setInterval("refer()", 1000); //启动1秒定时 
	function refer() {
		if (t == 0) {
			location = "index.jsp"; //#设定跳转的链接地址 
		}
		document.getElementById('show').innerHTML = "" + t + "秒后返回登录页面"; // 显示倒计时 
		t--; // 计数器递减 
	}
</script>
<body class="style1">

	<div >
		<img height=312 src="img/404.jpg">
	</div>
	<span id="show"></span> 
	<div>
		copyright © 2015 - 2020 <a href="http://bbs.sxisa.com/">创软俱乐部论坛</a>
		all rights reserved.
	</div>
</body>

</html>
