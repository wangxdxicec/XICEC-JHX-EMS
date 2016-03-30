<#if transaction.boothNumber?exists>
${transaction.boothNumber}
${"\r"}
${"\r"}
</#if>
<#if transaction.company?exists>
${transaction.company}
${"\r"}
${"\r"}
</#if>
<#if transaction.companye?exists>
${transaction.companye}
${"\r"}
${"\r"}
</#if>
<#if transaction.address?exists && transaction.addressEn?exists>
地址/Add: ${transaction.address}
${transaction.addressEn}
${"\r"}
<#elseif transaction.address?exists>
地址/Add: ${transaction.address}
${"\r"}
<#elseif transaction.addressEn?exists>
地址/Add: ${transaction.addressEn}
${"\r"}
</#if>
<#if transaction.zipcode?exists>
邮编/Post Code: ${transaction.zipcode}
${"\r"}
</#if>
<#if transaction.phone?exists>
电话/Tel: ${transaction.phone}
${"\r"}
</#if>
<#if transaction.fax?exists>
传真/Fax: ${transaction.fax}
${"\r"}
</#if>
<#if transaction.website?exists>
网址/Web: ${transaction.website}
${"\r"}
</#if>
<#if transaction.email?exists>
电子邮箱/E-mail: ${transaction.email}
${"\r"}
${"\r"}
</#if>
企业简介/Brief Introduction:
${"\r"}
${"\r"}
<#if transaction.mark?exists>
${transaction.mark}
</#if>