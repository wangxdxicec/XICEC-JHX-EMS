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
	</style>
</head>
<body>
<!-- 境内法师列表 -->
<div id="tabs" class="easyui-tabs" data-options="fit:true,border:false,plain:true">
	<div title="境内法师列表" style="padding:5px">
		<table id="customers" data-options="url:'${base}/user/queryInlandRabbiCustomersByPage',
         						   loadMsg: '数据加载中......',
						           singleSelect:false,	//只能当行选择：关闭
						           fit:true,
						           fitColumns:true,
								   toolbar:'#customerbar',
						           rownumbers: 'true',
						           pagination:'true',
						           pageSize:'20'">
			<thead>
			<tr>
				<th data-options="field:'ck',checkbox:true"></th>
				<th data-options="field: 'firstName', width: $(this).width() / 8">
					姓名<br/>
					<input id="customerFirstName" style="width:100%;height:15px;" type="text" onkeyup="filter();"/>
				</th>
				<th data-options="field: 'company', width: $(this).width() / 8">
					公司名称<br/>
					<input id="customerCompany" style="width:100%;height:15px;" type="text" onkeyup="filter();"/>
				</th>
				<th data-options="field: 'city', width: $(this).width() / 8">
					城市<br/>
					<input id="customerCity" style="width:100%;height:15px;" type="text" onkeyup="filter();"/>
				</th>
				<th data-options="field: 'address', width: $(this).width() / 8">
					地址<br/>
					<input id="customerAddress" style="width:100%;height:15px;" type="text" onkeyup="filter();"/>
				</th>
				<th data-options="field: 'mobilePhone', width: $(this).width() / 8">
					手机<br/>
					<input id="customerMobilePhone" style="width:100%;height:15px;" type="text" onkeyup="filter();"/>
				</th>
				<th data-options="field: 'tel', width: $(this).width() / 8">
					电话<br/>
					<input id="customerTelephone" style="width:100%;height:15px;" type="text" onkeyup="filter();"/>
				</th>
				<th data-options="field: 'email', width: $(this).width() / 8">
					邮箱<br/>
					<input id="customerEmail" style="width:100%;height:15px;" type="text" onkeyup="filter();"/>
				</th>
				<th data-options="field: 'createTime', formatter:formatDatebox, width: $(this).width() / 8">
					登记时间<br/>
					<input id="createdTime" style="width:100%;height:15px;" type="text" onkeyup="filter();"/>
				</th>
			</tr>
			</thead>
		</table>
	</div>
</div>
<!-- 群发邮件 -->
<form id="emailAllInlandCustomers" action="${base}/user/emailAllInlandCustomers" method="post">
	<div id="cidParm1"></div>
</form>
<!-- 群发短信 -->
<form id="msgSelectInlandCustomers" action="${base}/user/msgAllInlandCustomers" method="post">
	<div id="cidParm2"></div>
</form>
<!-- 导出境内法师到Excel -->
<form id="exportInlandCustomersToExcel" action="${base}/user/exportInlandCustomersToExcel" method="post">
	<div id="cidParm3"></div>
</form>
<!-- 工具栏 -->
<div id="customerbar">
	<div style="display:inline-block;">
		<div class="easyui-menubutton" menu="#email" iconCls="icon-redo">邮件</div>
	</div>
	<div id="email" style="width:180px;">
		<div id="emailAllCustomers" iconCls="icon-redo">群发所有境内法师邮件</div>
		<div id="emailSelectedCustomers" iconCls="icon-redo">群发所选境内法师邮件</div>
	</div>
	<div style="display:inline-block;">
		<div class="easyui-menubutton" menu="#msg" iconCls="icon-redo">短信</div>
	</div>
	<div id="msg" style="width:180px;">
		<div id="msgAllCustomers" iconCls="icon-redo">群发所有境内法师短信</div>
		<div id="msgSelectedCustomers" iconCls="icon-redo">群发所选境内法师短信</div>
	</div>
	<div style="display:inline-block;">
		<div class="easyui-menubutton" menu="#export" iconCls="icon-redo">导出</div>
	</div>
	<div id="export" style="width:180px;">
		<div id="exportAllCustomers" iconCls="icon-redo">所有境内法师信息到Excel</div>
		<div id="exportSelectedCustomers" iconCls="icon-redo">所选境内法师信息到Excel</div>
	</div>
</div>
<script>
	var checkedItems = [];

	//----------------------------------------------------工具栏函数开始--------------------------------------------------------//
	//群发所有境内法师邮件
	$('#emailAllCustomers').click(function(){
		$.messager.confirm('确认删除','你确定要群发所有境内法师邮件吗?',function(r){
			if (r){
				$.ajax({
					url: "${base}/user/emailAllInlandCustomers",
					type: "post",
					data: {"cids": "-1", "rabbi": "1"},
					dataType: "json",
					beforeSend:function(XMLHttpRequest){
						$.messager.show({
							title: '处理中...',
							msg: '正在群发邮件，请稍等...',
							timeout: 10000,
							showType: 'slide'
						});
					},
					success: function (data) {
						if (data.resultCode == 0) {
							$.messager.show({
								title: '成功',
								msg: '群发邮件成功',
								timeout: 5000,
								showType: 'slide'
							});
							$("#customers").datagrid("reload");
						} else {
							$.messager.alert('错误', '系统错误');
						}
					}
				});
			}
		});
	});
	//群发所选境内法师邮件
	$('#emailSelectedCustomers').click(function(){
		if(checkedItems.length > 0){
			$.messager.confirm('确认删除','你确定要群发所选境内法师邮件吗?',function(r){
				if (r){
					$.ajax({
						url: "${base}/user/emailAllInlandCustomers",
						type: "post",
						data: {"cids": checkedItems, "rabbi": "1"},
						dataType: "json",
						traditional: true,
						beforeSend:function(XMLHttpRequest){
							$.messager.show({
								title: '处理中...',
								msg: '正在群发邮件，请稍等...',
								timeout: 10000,
								showType: 'slide'
							});
						},
						success: function (data) {
							if (data.resultCode == 0) {
								$.messager.show({
									title: '成功',
									msg: '群发所选境内法师邮件成功',
									timeout: 5000,
									showType: 'slide'
								});
								$("#customers").datagrid("reload");
							} else {
								$.messager.alert('错误', '系统错误');
							}
						}
					});
				}
			});
		}else{
			$.messager.alert('提示', '请至少选择一项客商再操作');
		}
	});
	//群发所有境内法师短信
	$('#msgAllCustomers').click(function(){
		$.messager.confirm('确认删除','你确定要群发所有境内法师短信吗?',function(r){
			if (r){
				$.ajax({
					url: "${base}/user/msgAllInlandCustomers",
					type: "post",
					data: {"cids": "-1", "rabbi": "1"},
					dataType: "json",
					beforeSend:function(XMLHttpRequest){
						$.messager.show({
							title: '处理中...',
							msg: '正在群发短信，请稍等...',
							timeout: 10000,
							showType: 'slide'
						});
					},
					success: function (data) {
						if (data.resultCode == 0) {
							$.messager.show({
								title: '成功',
								msg: '群发所有境内法师短信成功',
								timeout: 5000,
								showType: 'slide'
							});
							$("#customers").datagrid("reload");
						} else {
							$.messager.alert('错误', '系统错误');
						}
					}
				});
			}
		});
	});
	//群发所选境内法师短信
	$('#msgSelectedCustomers').click(function(){
		if(checkedItems.length > 0){
			$.messager.confirm('确认删除','你确定要群发所选境内法师短信吗?',function(r){
				if (r){
					$.ajax({
						url: "${base}/user/msgAllInlandCustomers",
						type: "post",
						data: {"cids": checkedItems, "rabbi": "1"},
						dataType: "json",
						traditional: true,
						beforeSend:function(XMLHttpRequest){
							$.messager.show({
								title: '处理中...',
								msg: '正在群发短信，请稍等...',
								timeout: 10000,
								showType: 'slide'
							});
						},
						success: function (data) {
							if (data.resultCode == 0) {
								$.messager.show({
									title: '成功',
									msg: '群发所选境内法师短信成功',
									timeout: 5000,
									showType: 'slide'
								});
								$("#customers").datagrid("reload");
							} else {
								$.messager.alert('错误', '系统错误');
							}
						}
					});
				}
			});
		}else{
			$.messager.alert('提示', '请至少选择一项境内法师再操作');
		}
	});
	//导出所有境内法师信息到Excel
	$('#exportAllCustomers').click(function(){
		cidParm3.innerHTML = "";
		var node = "<input type='hidden' name='cids' value='-1'/>";
		cidParm3.innerHTML += node;
		var node1 = "<input type='hidden' name='rabbi' value='1'/>";
		cidParm3.innerHTML += node1;
		document.getElementById("exportInlandCustomersToExcel").submit();
		$.messager.alert('提示', '导出所有境内法师成功');
	});
	//导出所选境内法师信息到Excel
	$('#exportSelectedCustomers').click(function(){
		cidParm3.innerHTML = "";
		if(checkedItems.length > 0){
			for (var i = 0; i < checkedItems.length; i++) {
				var node = "<input type='hidden' name='cids' value='"+checkedItems[i]+"'/>";
				cidParm3.innerHTML += node;
			}
			var node1 = "<input type='hidden' name='rabbi' value='1'/>";
			cidParm3.innerHTML += node1;
			document.getElementById("exportInlandCustomersToExcel").submit();
			$.messager.alert('提示', '导出所选境内法师成功');
		}else{
			$.messager.alert('提示', '请至少选择一项境内法师再导出');
		}
	});
	//----------------------------------------------------工具栏函数结束--------------------------------------------------------//
	//----------------------------------------------------自定义函数开始--------------------------------------------------------//
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

	function filter(){
		var filterParm = "?";
		if(document.getElementById("customerFirstName").value != ""){
			filterParm += '&firstName=' + encodeURI(document.getElementById("customerFirstName").value);
		}
		if(document.getElementById("customerCompany").value != ""){
			filterParm += '&company=' + encodeURI(document.getElementById("customerCompany").value);
		}
		if(document.getElementById("customerCity").value != ""){
			filterParm += '&city=' + encodeURI(document.getElementById("customerCity").value);
		}
		if(document.getElementById("customerAddress").value != ""){
			filterParm += '&address=' + encodeURI(document.getElementById("customerAddress").value);
		}
		if(document.getElementById("customerMobilePhone").value != ""){
			filterParm += '&mobile=' + encodeURI(document.getElementById("customerMobilePhone").value);
		}
		if(document.getElementById("customerTelephone").value != ""){
			filterParm += '&tel=' + encodeURI(document.getElementById("customerTelephone").value);
		}
		if(document.getElementById("customerEmail").value != ""){
			filterParm += '&email=' + encodeURI(document.getElementById("customerEmail").value);
		}
		if(document.getElementById("createdTime").value != ""){
			filterParm += '&createTime=' + encodeURI(document.getElementById("createdTime").value);
		}
		filterParm += '&inlandOrForeign=1';
		filterParm += '&isRabbicFlag=1';
		$('#customers').datagrid('options').url = '${base}/user/queryCustomersByPage' + filterParm;
		$('#customers').datagrid('reload');
	}
	//----------------------------------------------------自定义函数结束--------------------------------------------------------//

	$(document).ready(function () {
		// 国内客商列表渲染
		$('#customers').datagrid({
			onSelect:function (rowIndex, rowData){
				var row = $('#customers').datagrid('getSelections');
				for (var i = 0; i < row.length; i++) {
					if (findCheckedItem(row[i].id) == -1) {
						checkedItems.push(row[i].id);
					}
				}
// 					alert(checkedItems);
			},
			onUnselect:function (rowIndex, rowData){
				var k = findCheckedItem(rowData.id);
				if (k != -1) {
					checkedItems.splice(k, 1);
				}
// 					alert(checkedItems);
			},
			onSelectAll:function (rows){
				for (var i = 0; i < rows.length; i++) {
					var k = findCheckedItem(rows[i].id);
					if (k == -1) {
						checkedItems.push(rows[i].id);
					}
				}
// 					alert(checkedItems);
			},
			onUnselectAll:function (rows){
				for (var i = 0; i < rows.length; i++) {
					var k = findCheckedItem(rows[i].id);
					if (k != -1) {
						checkedItems.splice(k, 1);
					}
				}
// 					alert(checkedItems);
			},
			rowStyler:function(index,row){
				if ((row.sendEmailFlag == '1') && (row.sendMsgFlag != '1')){
					return 'color:red;font-weight:bold;';
				}
				if((row.sendEmailFlag != '1') && (row.sendMsgFlag == '1')) {
					return 'color:green;font-weight:bold;';
				}
				if((row.sendEmailFlag == '1') && (row.sendMsgFlag == '1')) {
					return 'color:orange;font-weight:bold;';
				}
			},
			onDblClickRow: function (index, field, value) {
				if(field.company != ""){
					if (!$("#tabs").tabs("exists", field.company)) {
						$('#tabs').tabs('add', {
							title: field.company,
							content:'<iframe frameborder="0" src="'+ "${base}/user/directToCustomerInfo?id=" + field.id+'" style="width:100%;height:99%;"></iframe>',
							closable: true
						});
					} else {
						$("#tabs").tabs("select", field.company);
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