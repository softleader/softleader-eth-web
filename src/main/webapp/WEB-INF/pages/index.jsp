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
				        	# 00000000
				        </div>
				        <div class="label">
				        	Best Block Number
				        </div>
				    </div>
				    <div class="statistic">
				        <div class="value" id="peers">
				        	0
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
		<div class="ui green segment">
			<form class="ui form" id="dataForm">
				<div class="two fields">
					<div class="field">
						<label>Date</label>
						<input type="date" name="date">
					</div>
					<div class="field">
						<label>Rainfall</label>
						<input type="number" name="rainfall">
					</div>
				</div>
			</form>
			<button class="ui primary button" id="insertRainfall">Submit</button>
		</div>
		<div class="ui blue segment">
			<div class="ui list" id="policyList">
				<c:forEach items="${policys}" var="item">
					<div class="item" id="${item.insAddress}">
						<i class="green checkmark box icon"></i>
						<div class="content">
							<a class="header" href="https://morden.ether.camp/account/${item.insAddress}" target="_blank">Address: 0x${item.insAddress}</a>
							<div class="meta">
				        		<span>ApplyDate: ${item.applyDate}</span>
				        		<span>Compensated: ${item.compensated}</span>
							</div>
							<div class="description">
								<span>EffDate: ${item.effDate}</span>
								<span>Rainfall: ${item.rainfall} mm</span>
							</div>
						</div>
					</div>
				</c:forEach>
			</div>
		</div>
	<!-- custom page end -->
	</div>
</div>

<c:import url="/WEB-INF/pages/layout/footer.jsp" />
</body>

<script type="text/template" id="policyItem">
	<div class="item" id="#insAddress#">
		<i class="green checkmark box icon"></i>
		<div class="content">
			<a class="header" href="https://morden.ether.camp/account/#insAddress#" target="_blank">Address: 0x#insAddress#</a>
			<div class="meta">
        		<span>ApplyDate: #applyDate#</span>
        		<span>Compensated: #compensated#</span>
			</div>
			<div class="description">
				<span>EffDate: #effDate#</span>
				<span>Rainfall: #rainfall# mm</span>
			</div>
		</div>
	</div>
</script>

<script>
$(function() {
	socketConnect();
	
	// websocket
	function socketConnect() {
		var socket = new SockJS('<c:url value="/websocket" />');
		var client = Stomp.over(socket);
		
		// 覆寫debug訊息(不顯示)
	 	client.debug = function(str) {
	 	};

		client.connect({}, function(frame) {
			msg.success('Connect success');
			$('#insertRainfall').removeClass('disabled');
			$('#insertRainfall').prop('disabled', false);
			
			// 發送事件綁定
			$("#insertRainfall").bind("click", function() {
				$.ajax({
					url: '<c:url value="/tx/renew/weather/"/>' + $('#dataForm input[name="date"]').val() + '/' + $('#dataForm input[name="rainfall"]').val(),
					method: 'PUT',
					contentType: 'application/json; charset=utf-8',
				}).done(function(data, textStatus, jqXHR) {
					if (data.messagesEmpty) {
						$('#dataForm input').val('');
						msg.success('雨量資料新增成功');
					} else {
						alertMessages(data.messages);
					}
				}).fail(function(jqXHR, textStatus, errorThrown) {
					alertAjaxFail(jqXHR, textStatus, errorThrown);
				}).always(function(data, textStatus, jqXHR) {
				});
			});
			
			// 頻道訂閱:onblock
			client.subscribe('/topic/onInsertRainfall', function(data) {
				$('#dataForm input').val('');
		    });
			
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
			
			// 頻道訂閱:onevent
			client.subscribe('/topic/onevent', function(data) {
				var ethWeatherPolicy = JSON.parse(data.body);
				if ($('#' + ethWeatherPolicy.insAddress).length > 0) {
					$('#' + ethWeatherPolicy.insAddress).transition({
						animation: 'fade left',
						onComplete: function() {
							$(this).remove();
							insertItem(ethWeatherPolicy);
						}
					});
				} else {
					insertItem(ethWeatherPolicy);
				}
		    });
			
			function insertItem(ethWeatherPolicy) {
				var $itemContent = $($('#policyItem').html()
					.replace(/#insAddress#/g, ethWeatherPolicy.insAddress)
					.replace(/#effDate#/g, ethWeatherPolicy.effDate)
					.replace(/#rainfall#/g, ethWeatherPolicy.rainfall)
					.replace(/#applyDate#/g, ethWeatherPolicy.applyDate)
					.replace(/#compensated#/g, ethWeatherPolicy.compensated)
				).hide();
				
				$('#policyList').prepend($itemContent);
				$itemContent.transition('fade left');
			}
		}, function(message) {
			$('#insertRainfall').addClass('disabled');
			$('#insertRainfall').prop('disabled', true);
			client.disconnect();
	    	msg.error('Oops! some trouble with connection', ['Retry connect ASAP'], {
	    		onHidden: function() {
			    	socketConnect();
			    	msg.warn('Reconnecting now');
	    		}
	    	});
		});
	}
	
	// 同步歷時
	var syncedSec = 0;
	var syncInterval = setInterval(function(){
		syncedSec++;
		$('#lastSync').text(syncedSec + 's');
	}, 1000);
	
});
</script>

</html>
