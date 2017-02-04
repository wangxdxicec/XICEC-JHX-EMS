<?xml version="1.0" encoding="UTF-8"?>
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ include file="/WEB-INF/tpl/user/managerrole/head.jsp" %>

<!DOCTYPE html>
<html>
<head>
	<title>金泓信展商管理后台</title>
	<style>
		body {
			margin: 0px;
			padding: 0px;
			width: 100%;
			height: 100%;
		}

		input {
			width: 200px;
			height: 20px;
		}

		#bg{ display: none; position: absolute; top: 0%; left: 0%; width: 50%; height: 50%; background-color: black; z-index:1001; -moz-opacity: 0.2; opacity:.2; filter: alpha(opacity=50);}
		.loading{display: none; position: absolute; top: 50%; left: 50%; z-index:1002; }
	</style>
</head>
<body>
<!-- 客商列表 -->
<div id="tabs" class="easyui-tabs" data-options="fit:true,border:false,plain:true">
	<div title="参观团列表" style="padding:5px">
		<table id="visitorGroup" data-options="url:'${base}/user/queryVisitorGroupByPage',
         						   loadMsg: '数据加载中......',
						           singleSelect:false,	//只能当行选择：关闭
						           fit:true,
						           fitColumns:true,
						           idField:'id',
						           remoteSort:true,
						           rownumbers: 'true',
						           pagination:'true',
						           pageSize:'20'">
			<thead>
			<tr>
				<th data-options="field:'ck',checkbox:true"></th>
				<th data-options="field: 'group_name', width: $(this).width() / 7">
					参观团名称<br/>
					<input id="groupName" style="width:100%;height:15px;" type="text" onkeyup="filter();"/>
				</th>
				<th data-options="field: 'group_header_name', width: $(this).width() / 7">
					团长姓名<br/>
					<input id="groupHeaderName" style="width:100%;height:15px;" type="text" onkeyup="filter();"/>
				</th>
				<th data-options="field: 'group_header_telphone', width: $(this).width() / 7">
					团长手机<br/>
					<input id="groupHeaderTelphone" style="width:100%;height:15px;" type="text" onkeyup="filter();"/>
				</th>
				<th data-options="field: 'group_header_position', width: $(this).width() / 7">
					团长职位<br/>
					<input id="groupHeaderPosition" style="width:100%;height:15px;" type="text" onkeyup="filter();"/>
				</th>
				<th data-options="field: 'group_header_email', width: $(this).width() / 7">
					团长邮箱<br/>
					<input id="groupHeaderEmail" style="width:100%;height:15px;" type="text" onkeyup="filter();"/>
				</th>
				<th data-options="field: 'group_header_address', width: $(this).width() / 7">
					团长地址<br/>
					<input id="groupHeaderAddress" style="width:100%;height:15px;" type="text" onkeyup="filter();"/>
				</th>
				<th data-options="field: 'group_header_create_time', formatter:formatDatebox, width: $(this).width() / 7">
					注册时间<br/>
					<input id="groupHeaderCreateTime" style="width:100%;height:15px;" type="text" onkeyup="filter();"/>
				</th>
				<th data-options="field: 'group_header_update_time', formatter:formatDatebox, width: $(this).width() / 8">
					修改时间<br/>
					<input id="groupHeaderUpdateTime" style="width:100%;height:15px;" type="text" onkeyup="filter();"/>
				</th>
			</tr>
			</thead>
		</table>
	</div>
</div>

<div class="loading"><img src="${base}/resource/load.gif"></div>
<script>
	var checkedItems = [];
	//----------------------------------------------------自定义函数开始--------------------------------------------------------//
	function filter(){
		var filterParm = "?";
		if(document.getElementById("groupName").value != ""){
			filterParm += '&group_name=' + encodeURI(document.getElementById("groupName").value);
		}
		if(document.getElementById("groupHeaderName").value != ""){
			filterParm += '&group_header_name=' + encodeURI(document.getElementById("groupHeaderName").value);
		}
		if(document.getElementById("groupHeaderTelphone").value != ""){
			filterParm += '&group_header_telphone=' + encodeURI(document.getElementById("groupHeaderTelphone").value);
		}
		if(document.getElementById("groupHeaderPosition").value != ""){
			filterParm += '&group_header_position=' + encodeURI(document.getElementById("groupHeaderPosition").value);
		}
		if(document.getElementById("groupHeaderEmail").value != ""){
			filterParm += '&group_header_email=' + encodeURI(document.getElementById("groupHeaderEmail").value);
		}
		if(document.getElementById("groupHeaderAddress").value != ""){
			filterParm += '&group_header_address=' + encodeURI(document.getElementById("groupHeaderAddress").value);
		}
		if(document.getElementById("groupHeaderCreateTime").value != ""){
			filterParm += '&group_header_create_time=' + encodeURI(document.getElementById("groupHeaderCreateTime").value);
		}
		if(document.getElementById("groupHeaderUpdateTime").value != ""){
			filterParm += '&group_header_update_time=' + document.getElementById("groupHeaderUpdateTime").value;
		}
		$('#visitorGroup').datagrid('options').url = '${base}/user/queryVisitorGroupByPage' + filterParm;
		$('#visitorGroup').datagrid('reload');
	}

	//日期时间格式转换
	function formatDatebox(value) {
		if (value == null || value == '') {
			return '';
		}
		var dt;
		if (value instanceof Date) {
			dt = value;
		}
		else {
			dt = new Date(value);
			if (isNaN(dt)) {
				value = value.replace(/\/Date\((-?\d+)\)\//, '$1'); //标红的这段是关键代码，将那个长字符串的日期值转换成正常的JS日期格式
				dt = new Date();
				dt.setTime(value);
			}
		}

		return dt.format("yyyy-MM-dd h:m");   //这里用到一个javascript的Date类型的拓展方法，这个是自己添加的拓展方法，在后面的步骤3定义
	}

	Date.prototype.format = function (format)
	{
		var o = {
			"M+": this.getMonth() + 1, //month
			"d+": this.getDate(),    //day
			"h+": this.getHours(),   //hour
			"m+": this.getMinutes(), //minute
			"s+": this.getSeconds(), //second
			"q+": Math.floor((this.getMonth() + 3) / 3),  //quarter
			"S": this.getMilliseconds() //millisecond
		}
		if (/(y+)/.test(format)) format = format.replace(RegExp.$1,
				(this.getFullYear() + "").substr(4 - RegExp.$1.length));
		for (var k in o) if (new RegExp("(" + k + ")").test(format))
			format = format.replace(RegExp.$1,
					RegExp.$1.length == 1 ? o[k] :
							("00" + o[k]).substr(("" + o[k]).length));
		return format;
	}

	//----------------------------------------------------自定义函数结束--------------------------------------------------------//
	$(document).ready(function () {
		$('#visitorGroup').datagrid({
			onSelect:function (rowIndex, rowData){
				var row = $('#visitorGroup').datagrid('getSelections');
				for (var i = 0; i < row.length; i++) {
					if (findCheckedItem(row[i].id) == -1) {
						checkedItems.push(row[i].id);
					}
				}
			},
			onUnselect:function (rowIndex, rowData){
				var k = findCheckedItem(rowData.id);
				if (k != -1) {
					checkedItems.splice(k, 1);
				}
			},
			onSelectAll:function (rows){
				for (var i = 0; i < rows.length; i++) {
					var k = findCheckedItem(rows[i].id);
					if (k == -1) {
						checkedItems.push(rows[i].id);
					}
				}
			},
			onUnselectAll:function (rows){
				for (var i = 0; i < rows.length; i++) {
					var k = findCheckedItem(rows[i].id);
					if (k != -1) {
						checkedItems.splice(k, 1);
					}
				}
			},
			onDblClickRow: function (index, field, value) {
				if(field.company != ""){
					if (!$("#tabs").tabs("exists", field.group_name)) {
						$('#tabs').tabs('add', {
							title: field.group_name,
							content:'<iframe frameborder="0" src="'+ "${base}/user/directToVisitorGroupInfo?id=" + field.id+'" style="width:100%;height:99%;"></iframe>',
							closable: true
						});
					} else {
						$("#tabs").tabs("select", field.group_name);
					}
				}
			}
		}).datagrid('getPager').pagination({
			pageSize: 20,//每页显示的记录条数，默认为10
			pageList: [10,20,30,40,50],//可以设置每页记录条数的列表
			beforePageText: '第',//页数文本框前显示的汉字
			afterPageText: '页    共 {pages} 页',
			displayMsg: '当前显示 {from} - {to} 条记录   共 {total} 条记录'
		});

		function findCheckedItem(id) {
			for (var i = 0; i < checkedItems.length; i++) {
				if (checkedItems[i] == id) return i;
			}
			return -1;
		}
	});
</script>
</body>
</html>