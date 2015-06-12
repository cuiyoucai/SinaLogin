<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page isELIgnored="false"%>
<link rel="stylesheet" href="css/MainTable.css">

<body>
	<table class="bordered">
		<tr>
			<th colspan="6">微博数据抓取</th>
		</tr>
		<tbody id="tablelsw">
			<tr>
				<th>id</th>
				<th>title</th>
				<th>content</th>
				<th>time</th>
				<th>forword</th>
				<th>discuss</th>
			</tr>
			<c:forEach items="${list}" var="obj">
				<tr>
					<td>${obj.id}</td>
					<td>${obj.title}</td>
					<td>${obj.content}</td>
					<td>${obj.time}</td>
					<td>${obj.forwordCount}</td>
					<td>${obj.discuss}</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</body>