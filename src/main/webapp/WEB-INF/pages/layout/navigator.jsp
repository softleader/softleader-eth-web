<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<div class="ui fixed main menu">
	<div class="ui container">
		<div class="item" style="padding-top: 0px; padding-bottom: 0px;">
			<a class="ui logo icon" href="<c:url value="/"/>">
				<svg width="50" height="50">
					<image xlink:href="<c:url value="/images/softleader_logo.svg"/>" x="0" y="0" width="100%" height="100%"></image>
				</svg>
			</a>
			<a href="<c:url value="/"/>"><b>Softleader Eth Web</b></a>
		</div>
		<sec:authentication property="principal" var="userDetail" />
		<div class="ui simple dropdown right item">
			<i class="setting icon"></i>
			<div class="menu">
				<a id="logoutLink" class="item" href="#">
					<i class="power icon"></i>Log out
				</a>
			</div>
		</div>
	</div>
</div>

<form hidden id="logout" action="<c:url value="/logout" />" method="post">
	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
</form>

<script>

$(function() {
	// 自動根據超連結列顯示title
	var path = window.location.pathname;
	var selectedItem = $('#nav .item[href="' + path + '"]');
	selectedItem.addClass('active');
	$('#pageTitile').text(selectedItem.text());
	
	// 登出link
	$('#logoutLink').bind('click', function() {
		$('#logout').submit();
	});
})
	
</script>