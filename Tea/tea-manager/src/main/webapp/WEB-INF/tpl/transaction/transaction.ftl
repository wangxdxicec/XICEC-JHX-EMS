<#if transaction.boothNumber?exists>
${transaction.boothNumber}
</#if>

<#if transaction.company?exists>
${transaction.company}
</#if>
<#if transaction.companye?exists>
${transaction.companye}
</#if>

<#if transaction.address?exists && transaction.addressEn?exists>
地址/Add: ${transaction.address}
${transaction.addressEn}
<#elseif transaction.address?exists>
地址/Add: ${transaction.address}
<#elseif transaction.addressEn?exists>
地址/Add: ${transaction.addressEn}
</#if>
<#if transaction.phone?exists && transaction.phone??>
电话/Tel: ${transaction.phone}
</#if>
<#if transaction.fax?exists && transaction.fax??>
传真/Fax: ${transaction.fax}
</#if>
<#if transaction.website?exists>
网址/Web: ${transaction.website}
</#if>
<#if transaction.email?exists && transaction.email??>
电子邮箱/E-mail: ${transaction.email}
</#if>

<#if transaction.mark?exists && transaction.mark??>
企业简介/Brief Introduction:
${transaction.mark}
</#if>