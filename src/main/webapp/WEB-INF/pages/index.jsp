<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec"%>
<!DOCTYPE html>
<html>
<head>
	<c:import url="/WEB-INF/pages/layout/meta.jsp" />
	<c:import url="/WEB-INF/pages/layout/javascript.jsp" />
	<c:import url="/WEB-INF/pages/layout/css.jsp" />
</head>

<body>
<div class="wrapper">
	<c:import url="/WEB-INF/pages/layout/navigator.jsp" />
	
	<div class="ui main text container">
	<!-- custom page -->
		Hello there,
		
		<p>
		This is your login principal:<br>
		<sec:authentication property="principal"/>
		</p>

	<!-- custom page end -->
	</div>
</div>

<c:import url="/WEB-INF/pages/layout/footer.jsp" />
</body>

</html>
