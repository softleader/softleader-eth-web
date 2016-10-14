<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="utils" uri="http://www.softleader.com.tw/tags/utils"%>

<script type="text/javascript" src='<c:url value="/resources/jquery/jquery-2.2.4.min.js" />'></script>
<script type="text/javascript" src='<c:url value="/resources/semantic/semantic.min.js" />'></script>
<script type="text/javascript" src='<c:url value="/resources/semantic/modules/calendar.js" />'></script>
<script type="text/javascript" src='<c:url value="/resources/datatables/jquery.dataTables.min.js" />'></script>
<script type="text/javascript" src='<c:url value="/resources/datatables/dataTables.semanticui.min.js" />'></script>
<script type="text/javascript" src='<c:url value="/resources/json/form2js.js" />'></script>
<script type="text/javascript" src='<c:url value="/resources/jquery/jquery.fileDownload.js" />'></script>
<script type="text/javascript" src='<c:url value="/resources/toastr-2.1.1/build/toastr.min.js" />'></script>

<script type="text/javascript" src='<c:url value="/resources/sockjs-client-1.1.0/dist/sockjs-1.1.0.min.js" />'></script>
<script type="text/javascript" src='<c:url value="/resources/stomp-websocket-2.3.4/lib/stomp.min.js" />'></script>

<div hidden="true" id="msgTemplate">
	<div class="ui basic modal" id="alertMessages">
		<div class="content">
			<div class="ui error message">
				<i class="close icon"></i>
				<div class="header"></div>
				<ul class="list">
				</ul>
			</div>
		</div>
	</div>
	
	<div class="ui small basic modal" name="confirmbox" id="confirmbox">
		<div class="ui icon header">
			<i class="warning sign icon"></i>
		</div>
		<div class="content">
		</div>
		<div class="actions">
			<div class="ui red basic cancel inverted button">
				<i class="remove icon"></i> No
			</div>
			<div class="ui green ok inverted button">
				<i class="checkmark icon"></i> Yes
			</div>
		</div>
	</div>
</div>

<script>
	//所有ajax都加上_csrf資訊
	$(function() {
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		if (header && token) {
			$(document).ajaxSend(function(e, xhr, options) {
				xhr.setRequestHeader(header, token);
			});
		}
		
		if ('${errors}') {
			alertMessages('${utils:toListString(errors)}'.split(','), '頁面載入異常，請稍後重試');
		}
	});

	/** 顯示AjaxResponse訊息的popup */
	function alertMessages(msgs, title, form) {
		var $target = $('#alertMessages').clone();
		if (title) {
			$target.find('.message').find('.header').text(title);
		} else {
			$target.find('.message').find('.header').text('');
		}
		$target.find('.message').find('.list').empty();

		msgs.forEach(function(msg) {
			var $input = $((form ? form + ' ' : '') + 'input[name="' + msg.name + '"]' + ',' + (form ? form + ' ' : '') + 'textarea[name="' + msg.name + '"]');
			var $fieldDiv = $input.parent().parent();
			var columnName = $fieldDiv.find('label').text();
			columnName = columnName ? columnName : msg.name;
			columnName = columnName ? columnName : '系統';
			columnName = columnName[0] != '＊' ? columnName : columnName.substring(1);
			$target.find('.message').find('.list').append($('<li></li>').text('【' + columnName + '】 ' + (msg.value ? msg.value : msg)));
			$fieldDiv.addClass('error');
			$input.bind('change', function() {
				$fieldDiv.removeClass('error');
			})
		});
		
		$target.modal({
			allowMultiple: true,
			closable: true,
			silent: true,
			onHidden: function() {
				$target.remove();
			}
		});
		// 雙擊後消失
		$target.bind('dblclick', function() {
			closeMsg($target);
		});
		// 點X後消失
		$target.find('.close.icon').bind('click', function() {
			closeMsg($target);
		});
		$target.modal('show');
	}

	/** MsgBox功能 */
	var msg = {
		info: function(message, messages, settings) {
			showMsg('info', message, messages, settings);
		},
		success: function(message, messages, settings) {
			showMsg('success', message, messages, settings);
		},
		warn: function(message, messages, settings) {
			showMsg('warning', message, messages, settings);
		},
		error: function(message, messages, settings) {
			showMsg('error', message, messages, settings);
		},
		confirm: function(message, messages, settings) {
			showConfirm($('#confirmbox').clone(), message, messages, callback);
		}
	};

	/** 顯示訊息 */
	function showMsg(type, message, messages, settings) {
		toastr.options = {
			  closeButton: false,
			  debug: false,
			  newestOnTop: true,
			  progressBar: true,
			  positionClass: "toast-top-right",
			  preventDuplicates: false,
			  showDuration: "300",
			  hideDuration: "1000",
			  timeOut: "5000",
			  extendedTimeOut: "1000",
			  showEasing: "swing",
			  hideEasing: "linear",
			  showMethod: "fadeIn",
			  hideMethod: "fadeOut",
			  onclick: null
			};
		$.extend(toastr.options, settings); // 套用客製化設定
		if (messages && messages.length) {
			toastr[type](messages.map(m => '·' + m).join('<br>'), message);
		} else {
			toastr[type](message);
		}
	}

	/** 顯示訊息 */
	function showConfirm($target, message, messages, callback) {
		$target.attr('id', '');
		// 主要訊息
		$target.find('.header').append($('<p></p>').text(message));
		// 列表訊息
		if (messages && messages.length > 0) {
			var $list = $('<ul class="list"></ul>');
			messages.forEach(function(msg) {
				$list.append($('<li></li>').text(msg));
			})
			$target.find('.content').append($list);
		}

		$target.modal({
			allowMultiple: true,
			closable: false,
			silent: true,
			onDeny: function() {
				callback(false)
			},
			onApprove: function() {
				callback(true)
			},
			onHidden: function() {
				$target.remove()
			}
		});

		// 初始化
		$('body').append($target);
		$target.modal('show');
	}

	/** 5秒後關閉MsgBox */
	function closeMsgInterval($target) {
		setInterval(function() {
			closeMsg($target);
		}, 5000);
	}

	/** 關閉MsgBox */
	function closeMsg($target) {
		if ($target.hasClass('modal')) {
			$target.modal('hide');
		} else if ($target.transition('is visible')) {
			$target.transition('fade down', 400, function() {
				$target.remove();
			});
		}
	}

	/** 共用的list ajax查詢object */
	function defaultListAjax(selector, settings) {
		var defaultSettings = {
			data: function(oData) { // 送回server端前的資訊處理
				enblock();
				var data = toObject(selector);
				data.page = oData.start / oData.length; // 目前第幾頁
				data.size = oData.length // 一頁長度
				if (oData.order.length > 0) {
					var order = oData.order[0];
					data.sort = oData.columns[order.column].name + ',' + order.dir; // 排序方式
					data.order = JSON.stringify([[order.column, order.dir]]); // 為了做到記憶查詢
				}
				return data;
			},
			dataFilter: function(data) { // server產出資料的資訊處理
				deblock();
				var json = $.parseJSON(data);
				if (!json.messagesEmpty) {
					alertMessages(json.messages, '');
				}
				if (json.totalElements == null) {
					json.totalElements = 0;
				}
				json.recordsTotal = json.totalElements; // 總比數
				json.recordsFiltered = json.totalElements; // 無用, 總之給總比數
				return JSON.stringify(json);
			}
		}
		$.extend(defaultSettings, settings); // 套用客製化設定
		return defaultSettings;
	}

	/** 顯示Ajax方法fail時的訊息 */
	function alertAjaxFail(jqXHR, textStatus, errorThrown) {
		console.log(jqXHR);
		console.log(textStatus);
		console.log(errorThrown);
		msg.error(textStatus, [errorThrown]);
	}

	/** 處理畫面block */
	function enblock() {
		$('form').addClass('loading');
		$('.form').addClass('loading');
		$('button').not('.dropdown, .item').addClass('loading');
		$('.button').not('.dropdown, .item').addClass('loading');
	}
	function deblock() {
		$('form').removeClass('loading');
		$('.form').removeClass('loading');
		$('button').removeClass('loading');
		$('.button').removeClass('loading');
	}

	/** 將form轉為物件以供查詢 */
	function toObject(selector, mapping) {
		var obj = form2js($(selector).attr('id'), '.', true, null, true);
		if (mapping) {
			mapping(obj)
		}
		return obj;
	}

	/** 將form轉為json */
	function toJson(selector, mapping) {
		return JSON.stringify(toObject(selector, mapping));
	}

	/** 移除顏色並記憶 */
	function removeColor($selector) {
		var toRemove = ['primary', 'secondary', 'basic', 'compact', 'yellow', 'orange', 'green', 'teal', 'blue', 'purple', 'pink', 'red', 'black'];
		var removed = [];
		toRemove.forEach(function(css) {
			if ($selector.hasClass(css)) {
				$selector.removeClass(css);
				removed.push(css);
			}
		});
	}

	/** 預設的Option產生器 */
	function defaultOptionFunction(option) {
		return '<div class="item dynamic" data-value="' + option.value + '">' + option.label + '</div>';
	}

	/** 動態產生Dropdown的項目 */
	function dinamicDropdown(selector, url, settings, optionFunction, noDefaultOption) {
		var noDefaultOption = noDefaultOption == null ? false : noDefaultOption;
		$.ajax({
			url: url,
			method: 'GET',
		}).done(function(data, textStatus, jqXHR) {
			optionFunction = optionFunction ? optionFunction : defaultOptionFunction;
			setDropdownOptions(selector, data, settings, optionFunction, noDefaultOption);
		});
	}

	/** 清空Dropdown的項目 */
	function emptyDynamicDropdownOption(selector) {
		$(selector).dropdown('hide');
		$(selector).find('div.dynamic').remove();
		$(selector).dropdown('restore defaults');
		$(selector).find('input').val('');
	}

	/** 設定Dropdown的項目 */
	function setDropdownOptions(selector, options, settings, optionFunction) {
		// 暫存預設選項(若是從input.value中帶入的話)
		var $input = $(selector).find('input');
		var defaultValue = $input.val();

		// 將動態產生的項目都刪掉
		emptyDynamicDropdownOption(selector);
		var html = $(selector + ' .menu').html();

		var defaultText = $(selector + ' .default.text').text();
		if (options && options.length > 0) {
			$(selector + ' .default.text').text(defaultText ? defaultText : '--請選擇--');
			html += generateOption(options, optionFunction);
		} else if (options && options.data && options.data.length > 0) {
			$(selector + ' .default.text').text(defaultText ? defaultText : '--請選擇--');
			html += generateOption(options.data, optionFunction);
		} else {
			$(selector + ' .default.text').text('--無選項--');
		}

		// 將選項塞入
		$(selector + ' .menu').html(html);

		// 若有其他設定檔則套入
		if (settings) {
			$(selector).dropdown(settings);
		} else {
			$(selector).dropdown();
		}

		var anyMatch = $(html).filter(function(idx, option) {return $(option).attr('data-value') == defaultValue});
		if (defaultValue && anyMatch.length) {
			$(selector).dropdown('set exactly', defaultValue.split(','));
		} else {
			$(selector).dropdown('restore defaults');
			$input.val('');
		}
	}

	/** 產生Option */
	function generateOption(datas, optionFunction) {
		var html = '';
		// 產生選項
		datas.forEach(function(option) {
			html += optionFunction(option);
		});
		return html;
	}

	/** 清空form */
	function resetForm(selector, gridSelector) {
		$(selector).find('input').each(function(idx, input) {
			var $inputBox = $(input).parent();
			if (!$inputBox.hasClass('disabled') && !$inputBox.hasClass('not-reset')) {
				if ($inputBox.hasClass('input')) {
					$(input).val('');
				} else if ($inputBox.hasClass('dropdown')) {
					$inputBox.dropdown('clear');
				}
			}
		});
		$(gridSelector).dataTable().api().order([0, 'desc']);
	}

	/** 轉導頁面 */
	function toPage(url) {
		window.location = '<c:url value="/"/>' + url;
	}

	/** 刷黑loading */
	function enLoading(selector) {
		var $dimmer = $('<div id="loadingDimmer" class="ui active dimmer"><div class="ui text loader">Loading</div></div>');
		$(selector).append($dimmer);
	}

	/** 解除loading */
	function deLoading(selector) {
		$(selector + ' #loadingDimmer').remove();
	}

	/** 按Enter自動跳往下一個input或送出 */
	function autoFacus() {
		$('#listForm').unbind('keypress');
		$('#listForm').bind('keypress', function(e) {
			if (e.keyCode == 13) {
				$('#searchBtn').click();
			}
		});

		var $fields = $('#dataForm .input:not(:hidden):not(.disabled), #dataForm .dropdown:not(:hidden):not(.disabled)');
		$fields.unbind('keypress');

		var fields = [];
		$fields.each(function(idx, field) {
			fields.push(this);
		});

		$fields.bind('keypress', function(e) {
			if (e.keyCode == 13) {
				var idx = fields.indexOf(this);
				if ($(fields[idx + 1]).hasClass('dropdown')) {
					$(fields[idx + 1]).dropdown('show');
				} else {
					$(fields[idx + 1]).find('input').focus();
				}
			}
		});

		$fields.last().bind('keypress', function(e) {
			if (e.keyCode == 13) {
				$('#saveBtn').click();
			}
		});
	}

	/** 根據傳進來的enum(LabelValueModel判斷顏色) */
	function guessRoleColor(role) {
		switch (role) {
		case 'DEV':
			return 'red';
		case 'USER':
			return 'green';
		case 'MGR':
			return 'violet';
		default:
			return 'grey';
		}
	}

	$(window).load(function() {
		autoFacus();
	});
</script>