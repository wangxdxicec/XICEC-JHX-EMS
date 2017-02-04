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

        #bg{ display: none; position: absolute; top: 0%; left: 0%; width: 50%; height: 50%; background-color: black; z-index:1001; -moz-opacity: 0.2; opacity:.2; filter: alpha(opacity=50);}
        .loading{display: none; position: absolute; top: 50%; left: 50%; z-index:1002; }
    </style>
</head>
<body class="body">
<div id="historyVisitorInfoTab" class="easyui-tabs" title="信息列表" data-options="fit:true,border:false,plain:true">
    <div title="客商历史数据" style="height:500px;">
        <table id="historyVisitorInfo" data-options="url:'${base}/user/queryHistoryVisitorInfosByPage?inlandOrOutland=1',
            								 loadMsg: '数据加载中......',
									         singleSelect:false,	//只能当行选择：关闭
									         fit:true,
									         fitColumns:true,
									         toolbar:'#toolbar',
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
                <th data-options="field: 'address', width: $(this).width() / 9">
                    地址<br/>
                    <input id="address" style="width:100%;height:15px;" type="text" onkeyup="filter();"/>
                </th>
                <th data-options="field: 'company', width: $(this).width() / 19">
                    公司名<br/>
                    <input id="company" style="width:100%;height:15px;" type="text" onkeyup="filter();"/>
                </th>
                <th data-options="field: 'contact', width: $(this).width() / 9">
                    联系人<br/>
                    <input id="contact" style="width:100%;height:15px;" type="text" onkeyup="filter();"/>
                </th>
                <th data-options="field: 'mobile', width: $(this).width() / 9">
                    手机<br/>
                    <input id="mobile" style="width:100%;height:15px;" type="text" onkeyup="filter();"/>
                </th>
                <th data-options="field: 'telphone', width: $(this).width() / 19">
                    电话<br/>
                    <input id="telphone" style="width:100%;height:15px;" type="text" onkeyup="filter();"/>
                </th>
                <th data-options="field: 'fax', width: $(this).width() / 9">
                    传真<br/>
                    <input id="fax" style="width:100%;height:15px;" type="text" onkeyup="filter();"/>
                </th>
                <th data-options="field: 'email', width: $(this).width() / 9">
                    邮箱<br/>
                    <input id="email" style="width:100%;height:15px;" type="text" onkeyup="filter();"/>
                </th>
                <th data-options="field: 'tel_remark', width: $(this).width() / 9">
                    电话记录<br/>
                    <input id="tel_remark" style="width:100%;height:15px;" type="text" onkeyup="filter();"/>
                </th>
                <th data-options="field: 'type', formatter: formatCustomerType, width: $(this).width() / 9">
                    类别<br/>
                    <select id="type" style="width:100%;height:15px;"
                            onchange="filter(this.options[this.options.selectedIndex].value);">
                    </select>
                </th>
            </tr>
            </thead>
        </table>
    </div>
</div>
<!-- 一键归档，展商信息重复提示对话框 -->
<div id="repeatDiv" class="easyui-dialog" iconCls="icon-search" data-options="width: $(this).width(), height: $(this).height() * 0.86" closed="true">
    <div style="height: 400px;">
        <table id="existExhibitorInfoTable" title="已存在的展商数据" class="easyui-datagrid" fitColumns="true" rownumbers="true" fit="true">
        </table>
    </div>
</div>

<div class="loading"><img src="${base}/resource/load.gif"></div>

<!-- 导入展商账号 -->
<div id="importHistoryVisitoryDlg" data-options="iconCls:'icon-add',modal:true">
    <form id="importHistoryVisitoryForm" name="importHistoryVisitoryForm">
        <table style="width: 320px;margin: 20px auto">
            <tr>
                <td colspan="2" style="width: 90px;text-align: left"><a href="${base}/resource/historyVisitoryTemplate.xlsx">下载导入模版</a><br /></td>
            </tr>
            <tr>
                <td style="width: 70px;text-align: left">导入模版：</td>
                <td style="width: 90px;text-align: left"><input type="file" name="file" id="file" /></td>
            </tr>
            <input type="hidden" value="1" name="inlandOrOutland">
        </table>
    </form>
</div>

<!-- 工具栏 -->
<div id="toolbar">
    <div style="display:inline-block;">
        <div class="easyui-menubutton" menu="#import" iconCls="icon-redo">导入</div>
    </div>
    <div id="import" style="width:180px;">
        <div id="importHistoryVisitor" iconCls="icon-redo">导入历史客商信息</div>
    </div>
    <div style="display:inline-block;">
        <div class="easyui-menubutton" menu="#export" iconCls="icon-redo">导出</div>
    </div>
    <div id="export" style="width:180px;">
        <div id="exportAllHistoryVisitoryInfos" iconCls="icon-redo">导出所有资料</div>
        <div id="exportSelectedHistoryVisitoryInfos" iconCls="icon-redo">导出所选资料</div>
        <div id="exportHistoryVisitoryInfosByType" iconCls="icon-redo">按类别导出</div>
    </div>
</div>

<!-- 导出历史客商资料到Excel -->
<form id="exportHistoryVisitoryToExcel" action="${base}/user/exportHistoryVisitoryToExcel" method="post">
    <div id="cidParm1"></div>
</form>
<form id="exportHistoryVisitoryByTypeToExcel" action="${base}/user/exportHistoryVisitoryByTypeToExcel" method="post">
    <div id="cidParm2"></div>
</form>

<!-- 类别选择表单 -->
<div id="customerSelectTypeDlg" data-options="iconCls:'icon-edit',modal:true">
    <center>
        <form id="customerSelectTypeForm" name="customerSelectTypeForm">
            <div id="typeParm"></div>
        </form>
    </center>
</div>

<script>
    var checkedItems = [];
    var customerType = [];

    //导出所有历史客商信息到Excel
    $('#exportAllHistoryVisitoryInfos').click(function(){
        cidParm1.innerHTML = "";
        var node = "<input type='hidden' name='cids' value='-1'/>";
        cidParm1.innerHTML += node;
        var node1 = "<input type='hidden' name='inOrOut' value='1'/>";
        cidParm1.innerHTML += node1;
        document.getElementById("exportHistoryVisitoryToExcel").submit();
        $.messager.alert('提示', '导出所有历史客商成功');
    });

    //导出所选历史客商信息到Excel
    $('#exportSelectedHistoryVisitoryInfos').click(function(){
        cidParm1.innerHTML = "";
        if(checkedItems.length > 0){
            for (var i = 0; i < checkedItems.length; i++) {
                var node = "<input type='hidden' name='cids' value='"+checkedItems[i]+"'/>";
                cidParm1.innerHTML += node;
            }
            var node1 = "<input type='hidden' name='inOrOut' value='1'/>";
            cidParm1.innerHTML += node1;
            document.getElementById("exportHistoryVisitoryToExcel").submit();
            $.messager.alert('提示', '导出所选历史客商成功');
        }else{
            $.messager.alert('提示', '请至少选择一项历史客商再导出');
        }
    });

    //按类别导出
    $("#exportHistoryVisitoryInfosByType").click(function () {
        $("#customerSelectTypeDlg").dialog("open");
    });
    //类别选择框
    $('#customerSelectTypeDlg').dialog({
        title: '选择客商种类',
        width: 300,
        height: 350,
        closed: true,
        cache: false,
        modal: true,
        buttons: [
            {
                text: '取消',
                handler: function () {
                    $("#customerSelectTypeDlg").dialog("close");
                }
            }
        ]
    });

    $('#importHistoryVisitor').click(function(){
        $("#importHistoryVisitoryDlg").dialog("open");
    });

    // 导入展商账号弹出框
    $('#importHistoryVisitoryDlg').dialog({
        title: '导入国内历史客商资料',
        width: 350,
        height: 340,
        closed: true,
        cache: false,
        modal: true,
        buttons: [
            {
                text: '确认导入',
                iconCls: 'icon-ok',
                handler: function () {
                    var file = document.getElementById("file").value;
                    if(file == null || file == ""){
                        $.messager.alert('提示', '请选择所要上传的文件！');
                    } else {
                        var index = file.lastIndexOf(".");
                        if(index < 0) {
                            $.messager.alert('提示', '上传的文件格式不正确，请选择97-2003Excel文件(*.xls)！');
                        } else {
                            $("#importHistoryVisitoryDlg").dialog("close");
                            $("#bg,.loading").show();
                            $.ajaxFileUpload({
                                url:'importHistoryVisitoryInfos',
                                dataType: 'text/html',
                                data:$("#importHistoryVisitoryForm").serializeJson(),
                                fileElementId:'file',
                                success: function (data){
                                    $("#bg,.loading").hide();
                                    $("#importHistoryVisitoryDlg").dialog("close");
                                    filter();
                                    var report = data;
                                    report = report.replace(new RegExp('","', 'g'), '<br/>');
                                    report = report.substring(2, report.length-2);
                                    $.messager.alert('成功', report);
                                    $.messager.show({
                                        title: '成功',
                                        msg: '导入历史客商资料成功',
                                        timeout: 3000,
                                        showType: 'slide'
                                    });
                                },
                                error: function (data){
                                    $.messager.alert('错误', data);
                                }
                            });
                        }
                    }
                }
            },
            {
                text: '取消',
                handler: function () {
                    $("#importHistoryVisitoryDlg").dialog("close");
                }
            }
        ]
    });

    $(document).ready(function () {
        //加载国内客商类别
        $.ajax({
            type:"POST",
            dataType:"json",
            url:"${base}/user/queryCustomerType",
            success : function(result) {
                if(result){
                    customerType = result;
                    $("#type").html('');
                    $("#type").append('<option value="">全部</option>');

                    typeParm.innerHTML = "";
                    for(var i=0,a;a=customerType[i++];){
                        $("#type").append('<option value="'+a.id+'">'+a.typename+'</option>');

                        var node = "<input type='button' class='easyui-linkbutton' id='"+a.id+"' value='"+a.typename+"' onclick='getHistoryVisitoryListByType("+a.id+")'/>";
                        typeParm.innerHTML += node;
                    }
                }
            }
        });

        // 归档资料列表渲染
        $('#historyVisitorInfo').datagrid({
            onSelect:function (rowIndex, rowData){
                var row = $('#historyVisitorInfo').datagrid('getSelections');
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
                    if (!$("#historyVisitorInfoTab").tabs("exists", field.company)) {
                        $('#historyVisitorInfoTab').tabs('add', {
                            title: field.company,
                            content:'<iframe frameborder="0" src="'+ "${base}/user/historyVisitorDetailInfo?id=" + field.id+'" style="width:100%;height:99%;"></iframe>',
                            closable: true
                        });
                    } else {
                        $("#historyVisitorInfoTab").tabs("select", field.company);
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
    });

    function getHistoryVisitoryListByType(typeId) {
        $("#customerSelectTypeDlg").dialog("close");
        cidParm2.innerHTML = "";
        var node = "<input type='hidden' name='typeId' value='"+typeId+"'/>";
        cidParm2.innerHTML += node;
        var node1 = "<input type='hidden' name='inOrOut' value='1'/>";
        cidParm2.innerHTML += node1;
        document.getElementById("exportHistoryVisitoryByTypeToExcel").submit();
        $.messager.alert('提示', '按类别导出历史客商成功');
    }

    function findCheckedItem(id) {
        for (var i = 0; i < checkedItems.length; i++) {
            if (checkedItems[i] == id) return i;
        }
        return -1;
    }

    function filter(){
        var filterParm = "?";
        if(document.getElementById("address").value != ""){
            filterParm += '&address=' + document.getElementById("address").value;
        }
        if(document.getElementById("company").value != ""){
            filterParm += '&company=' + document.getElementById("company").value;
        }
        if(document.getElementById("contact").value != ""){
            filterParm += '&contact=' + document.getElementById("contact").value;
        }
        if(document.getElementById("mobile").value != ""){
            filterParm += '&mobile=' + document.getElementById("mobile").value;
        }
        if(document.getElementById("telphone").value != ""){
            filterParm += '&telphone=' + document.getElementById("telphone").value;
        }
        if(document.getElementById("fax").value != ""){
            filterParm += '&fax=' + document.getElementById("fax").value;
        }
        if(document.getElementById("email").value != ""){
            filterParm += '&email=' + document.getElementById("email").value;
        }
        if(document.getElementById("tel_remark").value != ""){
            filterParm += '&tel_remark=' + document.getElementById("tel_remark").value;
        }
        if(document.getElementById("type").value != ""){
            filterParm += '&type=' + document.getElementById("type").value;
        }
        filterParm += '&inlandOrOutland=1';
        $('#historyVisitorInfo').datagrid('options').url = '${base}/user/queryHistoryVisitorInfosByPage' + filterParm;
        $('#historyVisitorInfo').datagrid('reload');
    }

    function formatCustomerType(val, row) {
        if (val != null) {
            return customerType[val - 1].typename;
        } else {
            return null;
        }
    }
</script>
</body>
</html>