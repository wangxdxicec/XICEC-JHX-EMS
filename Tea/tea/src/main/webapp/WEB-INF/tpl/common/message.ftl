<#if alert?exists>
<script>
    alert("${alert}");
        <#if redirectweixin?exists>
        window.location.href = "${redirectweixin}";
        <#elseif redirect?exists>
        window.location.href = "${redirect}";
        <#else>
        window.location.href = window.location.href;
        </#if>
</script>
</#if>