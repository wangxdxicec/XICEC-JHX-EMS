<%@ page import="com.zhenhappy.ems.entity.managerrole.TUserInfo" %>
<?xml version="1.0" encoding="UTF-8"?>
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ include file="/WEB-INF/tpl/user/managerrole/head.jsp" %>

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

		.btn { display: block; position: relative; background: #aaa; padding: 5px; float: left; color: #fff; text-decoration: none; cursor: pointer; }
		.btn * { font-style: normal; background-image: url(btn2.png); background-repeat: no-repeat; display: block; position: relative; }
		.btn i { background-position: top left; position: absolute; margin-bottom: -5px;  top: 0; left: 0; width: 5px; height: 5px; }
		.btn span { background-position: bottom left; left: -5px; padding: 0 0 5px 10px; margin-bottom: -5px; }
		.btn span i { background-position: bottom right; margin-bottom: 0; position: absolute; left: 100%; width: 10px; height: 100%; top: 0; }
		.btn span span { background-position: top right; position: absolute; right: -10px; margin-left: 10px; top: -5px; height: 0; }
		* html .btn span,
		* html .btn i { float: left; width: auto; background-image: none; cursor: pointer; }
		.btn.blue { background: #2ae; }
		.btn.green { background: #9d4; }
		.btn.pink { background: #e1a; }
		.btn:hover { background-color: #a00; }
		.btn:active { background-color: #444; }
		.btn[class] {  background-image: url(shade.png); background-position: bottom; }
		* html .btn { border: 3px double #aaa; }
		* html .btn.blue { border-color: #2ae; }
		* html .btn.green { border-color: #9d4; }
		* html .btn.pink { border-color: #e1a; }
		* html .btn:hover { border-color: #a00; }
		p { clear: both; padding-bottom: 2em; }
		form { margin-top: 2em; }
		form p .btn { margin-right: 1em; }
		textarea { margin: 1em 0;}

		#bg{ display: none; position: absolute; top: 0%; left: 0%; width: 50%; height: 50%; background-color: black; z-index:1001; -moz-opacity: 0.2; opacity:.2; filter: alpha(opacity=50);}
		.loading{display: none; position: absolute; top: 50%; left: 50%; z-index:1002; }
	</style>
</head>
<body class="body">
<!-- 重置列表管理 -->
<%
 HttpSession sesseionValue = request.getSession();
java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM-dd");
java.util.Date current_Time = new java.util.Date();//得到当前系统时间
java.util.Date tea_Fair_Show_Time = formatter.parse((String)sesseionValue.getAttribute("tea_Fair_Show_Begin_Date"));
int dataValiteResult = 0;
int roleValiteResult = 0;
if(current_Time.after(tea_Fair_Show_Time)){
	dataValiteResult = 1;
}

Integer roleId = (Integer)sesseionValue.getAttribute("roleId");
if(roleId.equals(Integer.valueOf(1))){
	roleValiteResult = 1;
}
%>

<% if(/*dataValiteResult== 1 || */roleValiteResult == 1){%>
	<div style="height: auto;" class="easyui-panel" title="归档相关操作">
		<table>
			<tr>
				<td style="width: 240px">一键归档：</td>
				<td>
					<div class="email-footer" align="center">
						<button type="button" class="btn btn-primary" id="oneKeyBackupAllExhibitorInfo">一键归档</button>
					</div>
				</td>
			</tr>
			<tr>
				<td style="width: 240px">重置所有展商为注销状态：</td>
				<td>
					<div class="email-footer" align="center">
						<button type="button" class="btn btn-primary" id="resetExhibitorDefault">重置所有展商为注销状态</button>
					</div>
				</td>

				<td style="width: 240px">重置展商对应参展人员列表：</td>
				<td>
					<div class="email-footer" align="center">
						<button type="button" class="btn btn-primary" id="resetJoinersList">重置展商对应参展人员列表</button>
					</div>
				</td>
			</tr>
		</table>
	</div>
<%}%>

<div id="historyExhibitorInfoTab" title="归档信息列表" class="easyui-tabs" data-options="fit:true,border:false,plain:true" style="height:80%; padding:5px" >
	<table id="historyExhibitorInfo" data-options="url:'${base}/user/queryHistoryExhibitorInfosByPage',
            								 loadMsg: '数据加载中......',
									         singleSelect:false,	//只能当行选择：关闭
									         fit:true,
									         fitColumns:true,
									         idField:'id',
									         remoteSort:true,
									         view: emptyView,
											 emptyMsg: '没有记录',
									         rownumbers: true,
									         pagination:'true',
									         pageSize:'20'">
		<thead>
		<tr>
			<th data-options="field:'ck',checkbox:true"></th>
			<th data-options="field: 'booth_number', width: $(this).width() * 0.07">
				展位号<br/>
				<input id="booth_number" style="width:100%;height:15px;" type="text" onkeyup="filter();"/>
			</th>
			<th data-options="field: 'company_zh', width: $(this).width() * 0.25">
				公司中文名<br/>
				<input id="company_zh" style="width:100%;height:15px;" type="text" onkeyup="filter();"/>
			</th>
			<th data-options="field: 'company_en', width: $(this).width() * 0.26">
				公司英文名<br/>
				<input id="company_en" style="width:100%;height:15px;" type="text" onkeyup="filter();"/>
			</th>
			<th data-options="field: 'main_product_zh', width: $(this).width() * 0.26">
				主营产品中文<br/>
				<input id="main_product_zh" style="width:100%;height:15px;" type="text" onkeyup="filter();"/>
			</th>
			<th data-options="field: 'main_product_en', width: $(this).width() * 0.26">
				主营产品英文<br/>
				<input id="main_product_en" style="width:100%;height:15px;" type="text" onkeyup="filter();"/>
			</th>
			<th data-options="field: 'invoice_head', width: $(this).width() * 0.26">
				发票抬头<br/>
				<input id="invoice_head" style="width:100%;height:15px;" type="text" onkeyup="filter();"/>
			</th>
			<th data-options="field: 'local_tax', width: $(this).width() * 0.26">
				地税编号<br/>
				<input id="local_tax" style="width:100%;height:15px;" type="text" onkeyup="filter();"/>
			</th>
			<th data-options="field: 'website', width: $(this).width() * 0.26">
				网址<br/>
				<input id="website" style="width:100%;height:15px;" type="text" onkeyup="filter();"/>
			</th>
			<th data-options="field: 'joiner_name', width: $(this).width() * 0.26">
				参展人员名单<br/>
				<input id="joiner_name" style="width:100%;height:15px;" type="text" onkeyup="filter();"/>
			</th>
		</tr>
		</thead>
	</table>
</div>
<!-- 一键归档，展商信息重复提示对话框 -->
<div id="repeatDiv" class="easyui-dialog" iconCls="icon-search" data-options="width: $(this).width(), height: $(this).height() * 0.86" closed="true">
	<div style="height: 400px;">
		<table id="existExhibitorInfoTable" title="已存在的展商数据" class="easyui-datagrid" fitColumns="true" rownumbers="true" fit="true">
		</table>
	</div>
</div>

<div class="loading"><img src="${base}/resource/load.gif"></div>
<script>
	var checkedItems = [];
	function filter(){
		var filterParm = "?";
		if(document.getElementById("booth_number").value != ""){
			filterParm += '&booth_number=' + document.getElementById("booth_number").value;
		}
		if(document.getElementById("company_zh").value != ""){
			filterParm += '&company_zh=' + document.getElementById("company_zh").value;
		}
		if(document.getElementById("company_en").value != ""){
			filterParm += '&company_en=' + document.getElementById("company_en").value;
		}
		if(document.getElementById("main_product_zh").value != ""){
			filterParm += '&main_product_zh=' + document.getElementById("main_product_zh").value;
		}
		if(document.getElementById("main_product_en").value != ""){
			filterParm += '&main_product_en=' + document.getElementById("main_product_en").value;
		}
		if(document.getElementById("invoice_head").value != ""){
			filterParm += '&invoice_head=' + document.getElementById("invoice_head").value;
		}
		if(document.getElementById("local_tax").value != ""){
			filterParm += '&local_tax=' + document.getElementById("local_tax").value;
		}
		if(document.getElementById("website").value != ""){
			filterParm += '&website=' + document.getElementById("website").value;
		}
		if(document.getElementById("joiner_name").value != ""){
			filterParm += '&joiner_name=' + document.getElementById("joiner_name").value;
		}
		$('#historyExhibitorInfo').datagrid('options').url = '${base}/user/queryHistoryExhibitorInfosByPage' + filterParm;
		$('#historyExhibitorInfo').datagrid('reload');
	}

	function oneKeyBackupAllExhibitorInfo() {
		$.messager.confirm('确认重置','你确定要一键归档所有的展商信息？',function(r){
			if (r){
				$("#bg,.loading").show();
				$.ajax({
					url: "${base}/user/oneKeyBackupAllExhibitorInfo",
					type: "post",
					dataType: "json",
					traditional: true,
					success: function (data) {
						$("#bg,.loading").hide();
						if (data.resultCode == 0) {
							$.messager.show({
								title: '成功',
								msg: '归档成功',
								timeout: 5000,
								showType: 'slide'
							});
						} else {
							var count = JSON.parse(data.result);
							var isExistData = JSON.parse(data.isExistData);
							$.messager.confirm("数据重复", count, function (data) {
								if (data) {
									$("#repeatDiv").dialog("open").dialog({title: '资料重复',
										buttons: [{text: '不做处理',
											handler: function () {
												$("#repeatDiv").dialog("close");
											}}]});
									$("#repeatDiv").dialog("open");
									$('#existExhibitorInfoTable').datagrid('loadData',isExistData);
									//$("#existExhibitorInfoTable").datagrid("reload");
								}
							});
						}
					}
				});
			}
		});
	}
	$('#oneKeyBackupAllExhibitorInfo').click(function(){
		$.messager.confirm('确认重置','你确定要一键归档所有的展商信息？',function(r){
			if (r){
				$("#bg,.loading").show();
				$.ajax({
					url: "${base}/user/oneKeyBackupAllExhibitorInfo",
					type: "post",
					dataType: "json",
					traditional: true,
					success: function (data) {
						$("#bg,.loading").hide();
						if (data.resultCode == 0) {
							$.messager.show({
								title: '成功',
								msg: '归档成功',
								timeout: 5000,
								showType: 'slide'
							});
						} else {
							var count = JSON.parse(data.result);
							var isExistData = JSON.parse(data.isExistData);
							$.messager.confirm("数据重复", count, function (data) {
								if (data) {
									$("#repeatDiv").dialog("open").dialog({title: '资料重复',
										buttons: [{text: '不做处理',
											handler: function () {
												$("#repeatDiv").dialog("close");
											}}]});
									$("#repeatDiv").dialog("open");
									$('#existExhibitorInfoTable').datagrid('loadData',isExistData);
									//$("#existExhibitorInfoTable").datagrid("reload");
								}
							});
						}
					}
				});
			}
		});
	});

	$('#resetExhibitorDefault').click(function(){
		$.messager.confirm('确认重置','你确定要重置所有展商为注销状态？',function(r){
			if (r){
				$("#bg,.loading").show();
				$.ajax({
					url: "${base}/user/resetExhibitorToDefault",
					type: "post",
					dataType: "json",
					traditional: true,
					success: function (data) {
						$("#bg,.loading").hide();
						if (data.resultCode == 0) {
							$.messager.show({
								title: '成功',
								msg: '重置成功',
								timeout: 5000,
								showType: 'slide'
							});
						} else {
							$.messager.alert('错误', '系统错误');
						}
					}
				});
			}
		});
	});

	$('#resetJoinersList').click(function(){
		$.messager.confirm('确认重置','你确定要重置所有展商对应的参展人员？',function(r){
			if (r){
				$("#bg,.loading").show();
				$.ajax({
					url: "${base}/user/resetJoinerListToDefault",
					type: "post",
					dataType: "json",
					traditional: true,
					success: function (data) {
						$("#bg,.loading").hide();
						if (data.resultCode == 0) {
							$.messager.show({
								title: '成功',
								msg: '重置成功',
								timeout: 5000,
								showType: 'slide'
							});
						} else {
							$.messager.alert('错误', '系统错误');
						}
					}
				});
			}
		});
	});

	$("#existExhibitorInfoTable").datagrid({
		url: '${base}/user/showExistExhibitorInfo',
		singleSelect:false,	//只能当行选择：关闭
		fit:true,
		fitColumns:true,
		rownumbers: true,
		columns: [
			[
				{field: 'ck', checkbox:true },
				{field: 'booth_number', title: '展位号', width: 100},
				{field: 'company_zh', title: '公司中文名', width: 100},
				{field: 'company_en', title: '公司英文名', width: 100},
				{field: 'main_product_zh', title: '主营产品中文', width: 100},
				{field: 'main_product_en', title: '主营产品英文', width: 100},
				{field: 'invoice_head', title: '发票抬头', width: 100},
				{field: 'local_tax', title: '地税编号', width: 100},
				{field: 'website', title: '网址', width: 100},
				{field: 'joiner_name', title: '参展人员名单', width: 100}
			]
		]
	});

	// 归档资料列表渲染
	$('#historyExhibitorInfo').datagrid({
		onSelect:function (rowIndex, rowData){
			var row = $('#historyExhibitorInfo').datagrid('getSelections');
			for (var i = 0; i < row.length; i++) {
				if (findCheckedItem(row[i].eid) == -1) {
					checkedItems.push(row[i].eid);
				}
			}
		},
		onUnselect:function (rowIndex, rowData){
			var k = findCheckedItem(rowData.eid);
			if (k != -1) {
				checkedItems.splice(k, 1);
			}
		},
		onSelectAll:function (rows){
			for (var i = 0; i < rows.length; i++) {
				var k = findCheckedItem(rows[i].eid);
				if (k == -1) {
					checkedItems.push(rows[i].eid);
				}
			}
		},
		onUnselectAll:function (rows){
			for (var i = 0; i < rows.length; i++) {
				var k = findCheckedItem(rows[i].eid);
				if (k != -1) {
					checkedItems.splice(k, 1);
				}
			}
		},
		onDblClickRow: function (index, field, value) {
			if(field.company_zh != ""){
				if (!$("#historyExhibitorInfoTab").tabs("exists", field.company_zh)) {
					$('#historyExhibitorInfoTab').tabs('add', {
						title: field.company_zh,
						content:'<iframe frameborder="0" src="'+ "${base}/user/historyExhibitorDetailInfo?id=" + field.id+'" style="width:100%;height:99%;"></iframe>',
						closable: true
					});
				} else {
					$("#historyExhibitorInfoTab").tabs("select", field.company_zh);
				}
			}else if(field.company_en != ""){
				if (!$("#historyExhibitorInfoTab").tabs("exists", field.company_en)) {
					$('#historyExhibitorInfoTab').tabs('add', {
						title: field.company_en,
						content:'<iframe frameborder="0" src="'+ "${base}/user/historyExhibitorDetailInfo?id=" + field.id+'" style="width:100%;height:99%;"></iframe>',
						closable: true
					});
				} else {
					$("#historyExhibitorInfoTab").tabs("select", field.company_en);
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

	function findCheckedItem(eid) {
		for (var i = 0; i < checkedItems.length; i++) {
			if (checkedItems[i] == eid) return i;
		}
		return -1;
	}
</script>
</body>
</html>