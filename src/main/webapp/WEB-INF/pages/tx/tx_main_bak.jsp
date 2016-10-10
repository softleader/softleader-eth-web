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
	
		<div class="ui grid">
				
			<div class="sixteen wide column">
				<h2 class="ui header">
					<i class="big icons">
						<i class="settings icon"></i>
						<i class="inverted corner hashtag icon"></i>
					</i>
					<div class="content">
						Dashboard
					</div>
				</h2>


				<div class="ui padded blue raised segment">
				<form id="dataForm" class="ui form">
					<div class="required field">
						<label>Input</label>
						<div class="ui left icon input">
							<i class="user icon"></i>
							<input type="text" name="message"/>
						</div>
					</div>
					
					<button class="ui fluid primary button disable" id="saveBtn" type="button" disable>送出</button>
				</form>
				</div>
			</div>
			
		</div>

	<!-- custom page end -->
	</div>
</div>

<c:import url="/WEB-INF/pages/layout/footer.jsp" />
</body>

<script>
$(function() {
	var socket = new SockJS('<c:url value="/websocket" />');
	var client = Stomp.over(socket);
	client.debug = function(str) {
	};
	client.connect({}, function(frame) {
		$('#dataForm .button').removeClass('disable');
		$('#dataForm .button').prop('disable', false);
		
		// 按鈕事件註冊
		$('#saveBtn').bind('click', function() {
			client.send("/websocket/hello", {}, toJson($('#dataForm')));
		});
		
		// 頻道訂閱
		client.subscribe('/topic/onblock', function(data){
	      	console.log(JSON.parse(data.body));
	    });
	});
	
});
</script>

</html>
