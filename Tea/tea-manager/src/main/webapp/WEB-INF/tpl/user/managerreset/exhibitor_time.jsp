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
<body  onload="queryExhibitorTimeData()">
<div style="height: auto;" class="easyui-panel" title="时间参数">
	<table align="center">
		<tr>
			<td style="width: 250px">展会开展时间周期（中文）：</td>
			<td>
				<input id="tea_Fair_Show_Date_Zh" style="width:80%;" type="text"/>
			</td>

			<td style="width: 250px">展会开展时间周期（英文）：</td>
			<td>
				<input id="tea_Fair_Show_Date_En" style="width:80%;" type="text"/>
			</td>
		</tr>
		<tr>
			<td style="width: 250px">展会信息办理截止时间（中文）：</td>
			<td>
				<input id="exhibitor_Info_Submit_Deadline_Zh" style="width:80%;" type="text"/>
			</td>

			<td style="width: 250px">展会信息办理截止时间（英文）：</td>
			<td>
				<input id="exhibitor_Info_Submit_Deadline_En" style="width:80%;" type="text"/>
			</td>
		</tr>
		<tr>
			<td style="width: 200px">展会办展开始日期：</td>
			<td>
				<input id="tea_Fair_Show_Begin_Date" style="width:80%;" type="text"/>
			</td>

			<td style="width: 200px">展会办展年份：</td>
			<td>
				<input id="tea_Fair_Show_Year" style="width:80%;" type="text"/>
			</td>
		</tr>
		<tr>
			<td style="width: 200px">展商信息截止时间：</td>
			<td>
				<input id="tea_Fair_Data_End_Html" style="width:80%;" type="text"/>
			</td>
		</tr>
		<tr>
			<td style="width: 200px">参展人员信息提交截止时间（中文）：</td>
			<td>
				<input id="tea_Fair_Contact_Submit_Deadline_Zh" style="width:80%;" type="text"/>
			</td>

			<td style="width: 200px">参展人员信息提交截止时间（英文）：</td>
			<td>
				<input id="tea_Fair_Contact_Submit_Deadline_En" style="width:80%;" type="text"/>
			</td>
		</tr>
		<tr>
			<td style="width: 200px">发票信息提交截止时间（中文）：</td>
			<td>
				<input id="tea_Fair_Invoice_Submit_Deadline_Zh" style="width:80%;" type="text"/>
			</td>

			<td style="width: 200px">发票人员信息提交截止时间（英文）：</td>
			<td>
				<input id="tea_Fair_Invoice_Submit_Deadline_En" style="width:80%;" type="text"/>
			</td>
		</tr>
	</table>
	<div class="email-footer" align="center" style="margin-left: 50%;margin-top: 30px">
		<button type="button" class="btn btn-primary" id="saveData">确认修改</button>
	</div>
</div>
<script>
	function queryExhibitorTimeData() {
		$.ajax({
			url: "${base}/user/queryAllExhibitorTime",
			type: "post",
			dataType: "json",
			traditional: true,
			success: function (data) {
				if(data.rows.length>0) {
					for(var i = 0; i < data.rows.length; i++){
						var map = data.rows[i];
						document.getElementById("tea_Fair_Show_Date_Zh").value = map.tea_Fair_Show_Date_Zh;
						document.getElementById("tea_Fair_Show_Date_En").value = map.tea_Fair_Show_Date_En;
						document.getElementById("exhibitor_Info_Submit_Deadline_Zh").value = map.exhibitor_Info_Submit_Deadline_Zh;
						document.getElementById("exhibitor_Info_Submit_Deadline_En").value = map.exhibitor_Info_Submit_Deadline_En;
						document.getElementById("tea_Fair_Show_Year").value = map.tea_Fair_Show_Year;
						document.getElementById("tea_Fair_Show_Begin_Date").value = map.tea_Fair_Show_Begin_Date;
						document.getElementById("tea_Fair_Data_End_Html").value = map.tea_Fair_Data_End_Html;
						document.getElementById("tea_Fair_Contact_Submit_Deadline_Zh").value = map.tea_Fair_Contact_Submit_Deadline_Zh;
						document.getElementById("tea_Fair_Contact_Submit_Deadline_En").value = map.tea_Fair_Contact_Submit_Deadline_En;
						document.getElementById("tea_Fair_Invoice_Submit_Deadline_Zh").value = map.tea_Fair_Invoice_Submit_Deadline_Zh;
						document.getElementById("tea_Fair_Invoice_Submit_Deadline_En").value = map.tea_Fair_Invoice_Submit_Deadline_En;
					}
				}
			}
		});
	}

	$(document).ready(function () {
		$("#saveData").click(function () {
			$.messager.confirm('确认修改','你确定要修改时间参数？',function(r){
				if (r){
					$.ajax({
						url: "${base}/user/modifyExhibitorTime",
						type: "post",
						dataType: "json",
						traditional: true,
						data: {"tea_Fair_Show_Date_Zh": $("#tea_Fair_Show_Date_Zh").val(),
							"tea_Fair_Show_Date_En": $("#tea_Fair_Show_Date_En").val(),
							"exhibitor_Info_Submit_Deadline_Zh": $("#exhibitor_Info_Submit_Deadline_Zh").val(),
							"exhibitor_Info_Submit_Deadline_En": $("#exhibitor_Info_Submit_Deadline_En").val(),
							"tea_Fair_Show_Year": $("#tea_Fair_Show_Year").val(),
							"tea_Fair_Show_Begin_Date": $("#tea_Fair_Show_Begin_Date").val(),
							"tea_Fair_Data_End_Html": $("#tea_Fair_Data_End_Html").val(),
							"tea_Fair_Contact_Submit_Deadline_Zh": $("#tea_Fair_Contact_Submit_Deadline_Zh").val(),
							"tea_Fair_Contact_Submit_Deadline_En": $("#tea_Fair_Contact_Submit_Deadline_En").val(),
							"tea_Fair_Invoice_Submit_Deadline_Zh": $("#tea_Fair_Invoice_Submit_Deadline_Zh").val(),
							"tea_Fair_Invoice_Submit_Deadline_En": $("#tea_Fair_Invoice_Submit_Deadline_En").val()},
						success: function (data) {
							if (data.resultCode == 1) {
								$.messager.alert('错误', '更新时间参数失败错误');
							} else if (data.resultCode > 1) {
								$.messager.alert('错误', '服务器错误');
							} else {
								queryExhibitorTimeData();
								$.messager.show({
									title: '成功',
									msg: '修改时间参数成功',
									timeout: 2000,
									showType: 'slide'
								});
							}
						}
					});
				}
			});
		});
	});
</script>
</body>
</html>