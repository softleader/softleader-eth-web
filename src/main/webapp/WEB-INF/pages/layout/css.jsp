<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<link href='<c:url value="/resources/semantic/semantic.min.css" />' rel="stylesheet">
<link href='<c:url value="/resources/datatables/dataTables.semanticui.min.css" />' rel="stylesheet">
<link href='<c:url value="/resources/semantic/modules/calendar.css" />' rel="stylesheet">

<style>
.ui.main.container {
	padding-top: 6em;
	padding-bottom: 6em;
}

.message[name="msgbox"] {
	position: fixed;
	top: 5%;
	display: none;
	z-index: 99999;
	width: 70%;
    left: 15%;
}

.sl-float-right {
	float: right!important;
	padding-top: 10px;
}

.sl-float-left {
	float: left!important;
	padding-top: 10px;
}

.disabled {
	cursor: not-allowed;
}

.ui.button.item {
	text-align: left;
}
</style>