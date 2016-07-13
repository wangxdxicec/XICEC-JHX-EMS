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
<body class="body">
<!-- 重置列表管理 -->
<div style="height: auto;" class="easyui-panel" title="归档相关操作">
	<table>
		<tr>
			<td style="width: 240px">重置展商列表为初始状态：</td>
			<td>
				<div class="email-footer" align="center">
					<button type="button" class="btn btn-primary" id="resetExhibitorDefault">重置展商列表为初始状态</button>
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
<div class="loading"><img src="${base}/resource/load.gif"></div>
<script>
	$('#resetExhibitorDefault').click(function(){
		$.messager.confirm('确认重置','你确定要重置展商列表为初始状态？',function(r){
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
</script>
</body>
</html>