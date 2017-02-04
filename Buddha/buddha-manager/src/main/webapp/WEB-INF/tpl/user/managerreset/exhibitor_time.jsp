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
			<td style="width: 200px">展会开展时间周期：</td>
			<td>
				<input id="buddha_Fair_Show_Date" style="width:80%;" type="text"/>
			</td>

			<td style="width: 200px">展会信息办理截止时间：</td>
			<td>
				<input id="exhibitor_Info_Submit_Deadline" style="width:80%;" type="text"/>
			</td>
		</tr>
		<tr>
			<td style="width: 200px">展会开展时间：</td>
			<td>
				<input id="buddha_Fair_Show_Date_Begin" style="width:80%;" type="text"/>
			</td>
		</tr>
	</table>
	<div class="email-footer" align="center" style="margin-left: 50%;margin-top: 30px">
		<button type="button" class="btn btn-primary" id="saveData">确认修改</button>
	</div>
</div>
<div style="height: auto;" class="easyui-panel" title="国内客商归类">
	<table>
		<tr>
			<td style="width: 240px">国内客商归类：</td>
			<td>
				<div class="email-footer" align="center">
					<button type="button" class="btn btn-primary" id="classOneKey">一键归类</button>
				</div>
			</td>
		</tr>
	</table>
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
						document.getElementById("buddha_Fair_Show_Date").value = map.buddha_Fair_Show_Date;
						document.getElementById("exhibitor_Info_Submit_Deadline").value = map.exhibitor_Info_Submit_Deadline;
						document.getElementById("buddha_Fair_Show_Date_Begin").value = map.buddha_Fair_Show_Date_Begin;
					}
				}
			}
		});
	}

	$('#classOneKey').click(function(){
		$.messager.confirm('确认归类','你确定要一键归类客商类别？',function(r){
			if (r){
				$("#bg,.loading").show();
				$.ajax({
					url: "${base}/user/classVisitorByOneKey",
					type: "post",
					dataType: "json",
					traditional: true,
					success: function (data) {
						$("#bg,.loading").hide();
						if (data.resultCode == 1) {
							$.messager.alert('错误', '一键归类客商类别失败');
						} else if (data.resultCode > 1) {
							$.messager.alert('错误', '服务器错误');
						} else {
							$.messager.show({
								title: '成功',
								msg: '一键归类客商类别成功',
								timeout: 2000,
								showType: 'slide'
							});
						}
					}
				});
			}
		});
	});

	$(document).ready(function () {
		$("#saveData").click(function () {
			$.messager.confirm('确认修改','你确定要修改时间参数？',function(r){
				if (r){
					$.ajax({
						url: "${base}/user/modifyExhibitorTime",
						type: "post",
						dataType: "json",
						traditional: true,
						data: {"buddha_Fair_Show_Date": $("#buddha_Fair_Show_Date").val(),
							"exhibitor_Info_Submit_Deadline": $("#exhibitor_Info_Submit_Deadline").val(),
							"buddha_Fair_Show_Date_Begin": $("#buddha_Fair_Show_Date_Begin").val()},
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