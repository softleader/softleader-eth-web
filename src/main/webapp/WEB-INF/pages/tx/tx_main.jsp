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
	
	<div class="ui main container">
	<!-- custom page -->
	
		<div class="ui two column grid">
				<div class="column">
					<h2 class="ui header">
						<i class="big icons">
							<i class="settings icon"></i>
						</i>
						<div class="content">
							Dashboard
						</div>
					</h2>
				</div>
				<div class="column">
					<div class="ui tiny statistics">
					    <div class="statistic">
					        <div class="value" id="blockNum">
					        </div>
					        <div class="label">
					        	Best Block Number
					        </div>
					    </div>
					    <div class="statistic">
					        <div class="value" id="peers">
					        </div>
					        <div class="label">
					        	Peers
					        </div>
					    </div>
					    <div class="statistic">
					        <div class="value" id="lastSync">
					        </div>
					        <div class="label">
					        	Last Sync
					        </div>
					    </div>
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
	
	// 覆寫debug訊息
	client.debug = function(str) {
	};
	
	// websocket
	client.connect({}, function(frame) {
		$('#dataForm .button').removeClass('disable');
		$('#dataForm .button').prop('disable', false);
		
		// 頻道訂閱:onblock
		client.subscribe('/topic/onblock', function(data) {
			var blockInfo = JSON.parse(data.body);
			if (blockInfo.number < blockInfo.lastKnownBlockNumber) {
				$('#blockNum').text('#' + blockInfo.number + '/' + blockInfo.lastKnownBlockNumber);
			} else {
				$('#blockNum').text('#' + blockInfo.number);
			}
			$('#peers').text(blockInfo.peerCnt);
			$('#lastSync').text('0s');
			syncedSec = 0;
	    });
	});
	
	// 同步歷時
	var syncedSec = 0;
	var syncInterval = setInterval(function(){
		syncedSec++;
		$('#lastSync').text(syncedSec + 's');
	}, 1000);
	
});
</script>

</html>
