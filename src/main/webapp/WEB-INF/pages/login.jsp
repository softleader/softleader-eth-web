<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
	<c:import url="/WEB-INF/pages/layout/meta.jsp" />
	<c:import url="/WEB-INF/pages/layout/javascript.jsp" />
	<c:import url="/WEB-INF/pages/layout/css.jsp" />

<style type="text/css">
	body>.grid {
		height: 90%;
	}
	
	.column {
		max-width: 450px;
	}
</style>
</head>

<body>

<div class="ui middle aligned center aligned grid">
	<div class="row">
		<div class="column">
		
			<div class="ui padded blue raised segment">
				
				<c:if test="${param.error != null}">
					<p><font color="red">${param.error}</font></p>
				</c:if>
				<c:if test="${param.logout != null}">
					<p>您已經成功登出.</p>
				</c:if>
				<c:if test="${linkError != null}">
					<p><font color="red">${linkError}</font></p>
				</c:if>
			
				<h1 class="ui grey header">
					<svg width="100" height="100">
						<image xlink:href="<c:url value="/images/softleader_logo.svg"/>" x="0" y="0" width="100%" height="100%"></image>
					</svg>
					Softleader Eth Web
				</h1>
			
				<form id="listForm" class="ui large form" action="<c:url value="/login" />" method="post">
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
					
					<div class="field">
						<div class="ui left icon input">
							<i class="user icon"></i>
							<input type="text" id="account" name="account" placeholder="帳號" maxlength="50">
						</div>
					</div>
					<div class="field">
						<div class="ui left icon input">
							<i class="lock icon"></i>
							<input type="password" id="password" name="password" placeholder="密碼" maxlength="255">
						</div>
					</div>
					<div class="field">
						<button type="submit" class="ui fluid large blue submit button">登入</button>
					</div>
				</form>
			</div>
			
		</div>	
	</div>
	<div class="row">
		<div class="column">
			<c:import url="/WEB-INF/pages/layout/footer.jsp" />
		</div>
	</div>
</div>

</body>

</html>