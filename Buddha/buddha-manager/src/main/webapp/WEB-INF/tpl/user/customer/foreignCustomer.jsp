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
<!-- 客商列表 -->
<div id="tabs" class="easyui-tabs" data-options="fit:true,border:false,plain:true">
	<div title="客商列表" style="padding:5px">
		<table id="customers" data-options="url:'${base}/user/queryForeignCustomersByPage?pre=0',
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
					<span id="sfirstName" class="sortable">姓名</span><br/>
					<input id="customerFirstName" style="width:100%;height:15px;" type="text" onkeyup="filter();"/>
				</th>
				<th data-options="field: 'company', width: $(this).width() / 8">
					<span id="scompany" class="sortable">公司名称</span><br/>
					<input id="customerCompany" style="width:100%;height:15px;" type="text" onkeyup="filter();"/>
				</th>
				<th data-options="field: 'country', formatter: formatCountry, width: $(this).width() * 0.07">
					<span id="scountry" class="sortable">国家</span><br/>
					<select id="customerCountry" style="width:100%;height:21px;"
							onchange="filter(this.options[this.options.selectedIndex].value);">
					</select>
				</th>
				<th data-options="field: 'address', width: $(this).width() / 8">
					<span id="saddress" class="sortable">地址</span><br/>
					<input id="customerAddress" style="width:100%;height:15px;" type="text" onkeyup="filter();"/>
				</th>
				<th data-options="field: 'mobilePhone', width: $(this).width() / 8">
					<span id="smobilePhone" class="sortable">手机</span><br/>
					<input id="customerMobilePhone" style="width:100%;height:15px;" type="text" onkeyup="filter();"/>
				</th>
				<th data-options="field: 'tel', width: $(this).width() / 8">
					<span id="stelephone" class="sortable">电话</span><br/>
					<input id="customerTelephone" style="width:100%;height:15px;" type="text" onkeyup="filter();"/>
				</th>
				<th data-options="field: 'email', width: $(this).width() / 8">
					<span id="semail" class="sortable">邮箱</span><br/>
					<input id="customerEmail" style="width:100%;height:15px;" type="text" onkeyup="filter();"/>
				</th>
				<th data-options="field: 'createTime', formatter:formatDatebox, width: $(this).width() / 8">
					<span id="screatedTime" class="sortable">登记时间</span><br/>
					<input id="createdTime" style="width:100%;height:15px;" type="text" onkeyup="filter();"/>
				</th>
			</tr>
			</thead>
		</table>
	</div>
</div>
<!-- 群发邮件 -->
<form id="emailAllForeignCustomers" action="${base}/user/emailAllForeignCustomers" method="post">
	<div id="cidParm1"></div>
</form>
<!-- 导出客商到Excel -->
<form id="exportForeignCustomersToExcel" action="${base}/user/exportForeignCustomersToExcel" method="post">
	<div id="cidParm2"></div>
</form>
<!-- 工具栏 -->
<div id="customerbar">
	<div style="display:inline-block;">
		<div class="easyui-menubutton" menu="#email" iconCls="icon-redo">邮件</div>
	</div>
	<div id="email" style="width:180px;">
		<div id="emailAllCustomers" iconCls="icon-redo">群发所有客商邮件</div>
		<div id="emailSelectedCustomers" iconCls="icon-redo">群发所选客商邮件</div>
	</div>
	<div style="display:inline-block;">
		<div class="easyui-menubutton" menu="#export" iconCls="icon-redo">导出</div>
	</div>
	<div id="export" style="width:180px;">
		<div id="exportAllCustomers" iconCls="icon-redo">所有客商信息到Excel</div>
		<div id="exportSelectedCustomers" iconCls="icon-redo">所选客商信息到Excel</div>
	</div>
</div>

<script>
	var checkedItems = [];
	var country = [];
	var sortForeign = "createTime";
	var orderForeign = "desc";
	//----------------------------------------------------工具栏函数开始--------------------------------------------------------//
	//群发所有客商邮件
	$('#emailAllCustomers').click(function(){
		$.messager.confirm('确认删除','你确定要群发所有客商邮件吗?',function(r){
			if (r){
				$.ajax({
					url: "${base}/user/emailAllForeignCustomers",
					type: "post",
					data: {"cids": "-1", "rabbi": "0"},
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
	//群发所选客商邮件
	$('#emailSelectedCustomers').click(function(){
		if(checkedItems.length > 0){
			$.messager.confirm('确认删除','你确定要群发所选客商邮件吗?',function(r){
				if (r){
					$.ajax({
						url: "${base}/user/emailAllForeignCustomers",
						type: "post",
						data: {"cids": checkedItems, "rabbi": "0"},
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
									msg: '群发所选客商邮件成功',
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
	//导出所有客商信息到Excel
	$('#exportAllCustomers').click(function(){
		cidParm2.innerHTML = "";
		var node = "<input type='hidden' name='cids' value='-1'/>";
		cidParm2.innerHTML += node;
		var node1 = "<input type='hidden' name='rabbi' value='0'/>";
		cidParm2.innerHTML += node1;
		document.getElementById("exportForeignCustomersToExcel").submit();
		$.messager.alert('提示', '导出所有客商成功');
	});
	//导出所选客商信息到Excel
	$('#exportSelectedCustomers').click(function(){
//     	alert(checkedItems);
		cidParm2.innerHTML = "";
//     	alert(cidParm1.innerHTML);
		if(checkedItems.length > 0){
			for (var i = 0; i < checkedItems.length; i++) {
				var node = "<input type='hidden' name='cids' value='"+checkedItems[i]+"'/>";
				cidParm2.innerHTML += node;
			}
			var node1 = "<input type='hidden' name='rabbi' value='0'/>";
			cidParm2.innerHTML += node1;
			document.getElementById("exportForeignCustomersToExcel").submit();
			$.messager.alert('提示', '导出所选客商成功');
		}else{
			$.messager.alert('提示', '请至少选择一项客商再导出');
		}
	});
	//----------------------------------------------------工具栏函数结束--------------------------------------------------------//
	//----------------------------------------------------自定义函数开始--------------------------------------------------------//
	function formatCountry(val, row) {
		if (val != null) {
			if(val == 44) return country[0].chineseName;
			if(val > 0 && val <= 43){
				return country[val].chineseName;
			}else if(val > 43 && val <= 240){
				return country[val - 1].chineseName;
			}else{
				return null;
			}
		} else {
			return null;
		}
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

	function filter(countryId){
		$('#customers').datagrid('reload', {
			firstName : document.getElementById("customerFirstName").value,
			company : document.getElementById("customerCompany").value,
			country : document.getElementById("customerCountry").value,
			address : document.getElementById("customerAddress").value,
			mobilePhone : document.getElementById("customerMobilePhone").value,
			telephone : document.getElementById("customerTelephone").value,
			email : document.getElementById("customerEmail").value,
			createdTime : document.getElementById("createdTime").value,
			sort : sortForeign,
			order : orderForeign
		});
	}

	function filter() {
		var filterParm = "?";
		if(document.getElementById("customerFirstName").value != ""){
			filterParm += '&firstName=' + encodeURI(document.getElementById("customerFirstName").value);
		}
		if(document.getElementById("customerCompany").value != ""){
			filterParm += '&company=' + encodeURI(document.getElementById("customerCompany").value);
		}
		if(document.getElementById("customerCountry").value != ""){
			filterParm += '&country=' + encodeURI(document.getElementById("customerCountry").value);
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
		filterParm += '&inlandOrForeign=2';
		filterParm += '&isRabbicFlag=0';
		$('#customers').datagrid('options').url = '${base}/user/queryCustomersByPage' + filterParm;
		$('#customers').datagrid('reload');
	}
//----------------------------------------------------自定义函数结束--------------------------------------------------------//
    $(document).ready(function () {
		//加载国家列表
		$.ajax({
			type:"POST",
			dataType:"json",
			url:"${base}/user/queryAllCountry",
			success : function(result) {
				if(result){
					country = result;
					$("#customerCountry").html('');
					$("#customerCountry").append('<option value="">全部</option>');
					for(var i=0,a;a=country[i++];){
						if(a.id != 44) $("#customerCountry").append('<option value="'+a.id+'">'+a.countryValue+a.chineseName+'</option>');
					}
				}
			}
		});

    	// 国外客商列表渲染
        $('#customers').datagrid({
       		onSelect:function (rowIndex, rowData){
	        	var row = $('#customers').datagrid('getSelections');
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
		$("#customers").data().datagrid.dc.view.find("div.datagrid-header td .sortable").click(function(){
			sortForeign = $(this).prop("id").substr(1, $(this).prop("id").length);
			if(orderForeign == "asc"){
				$('#customers').datagrid('reload', {
					firstName : document.getElementById("customerFirstName").value,
					company : document.getElementById("customerCompany").value,
					country : document.getElementById("customerCountry").value,
					address : document.getElementById("customerAddress").value,
					mobilePhone : document.getElementById("customerMobilePhone").value,
					telephone : document.getElementById("customerTelephone").value,
					email : document.getElementById("customerEmail").value,
					createdTime : document.getElementById("createdTime").value,
					sort : sortForeign,
					order : orderForeign
				});
				$(this).html($(this).html().split(" ▲")[0].split(" ▼")[0] + " ▲");
				orderForeign = "desc";
			}else if(orderForeign == "desc"){
				$('#customers').datagrid('reload', {
					firstName : document.getElementById("customerFirstName").value,
					company : document.getElementById("customerCompany").value,
					country : document.getElementById("customerCountry").value,
					address : document.getElementById("customerAddress").value,
					mobilePhone : document.getElementById("customerMobilePhone").value,
					telephone : document.getElementById("customerTelephone").value,
					email : document.getElementById("customerEmail").value,
					createdTime : document.getElementById("createdTime").value,
					sort : sortForeign,
					order : orderForeign
				});
				$(this).html($(this).html().split(" ▲")[0].split(" ▼")[0] + " ▼");
				orderForeign = "null";
			}else{
				$('#customers').datagrid('reload', {
					firstName : document.getElementById("customerFirstName").value,
					company : document.getElementById("customerCompany").value,
					country : document.getElementById("customerCountry").value,
					address : document.getElementById("customerAddress").value,
					mobilePhone : document.getElementById("customerMobilePhone").value,
					telephone : document.getElementById("customerTelephone").value,
					email : document.getElementById("customerEmail").value,
					createdTime : document.getElementById("createdTime").value,
					sort : null,
					order : null
				});
				$(this).html($(this).html().split(" ▲")[0].split(" ▼")[0]);
				orderForeign = "asc";
			}
		});

		$('#customers').datagrid('reload', {
			firstName : document.getElementById("customerFirstName").value,
			company : document.getElementById("customerCompany").value,
			country : document.getElementById("customerCountry").value,
			address : document.getElementById("customerAddress").value,
			mobilePhone : document.getElementById("customerMobilePhone").value,
			telephone : document.getElementById("customerTelephone").value,
			email : document.getElementById("customerEmail").value,
			createdTime : document.getElementById("createdTime").value,
			sort : sortForeign,
			order : orderForeign
		});
    });
</script>
</body>
</html>