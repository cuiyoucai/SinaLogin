<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="en" class="no-js">
<head>
<meta charset="utf-8">
<title>创软黑马队爬虫组</title>

<!-- CSS -->
<link rel="stylesheet" href="css/reset.css">
<link rel="stylesheet" href="css/supersized.css">
<link rel="stylesheet" href="css/style.css">
</head>

<body>

	<div class="page-container">
		<h1>Sina模拟登录</h1>
		<form action="new_foundNews" method="post">
			<input type="text" name="userName" class="username" placeholder="用户名">
			<input type="password" name="passWord" class="password" placeholder="密码">
			<button type="submit">登录</button>
			<div class="error">
				<span>+</span>
			</div>
		</form>
	</div>

</body>

</html>