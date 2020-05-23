<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>
[
<c:forEach var="map" varStatus="vs" items="${allPostageTplMaps}">
{
	id: 'tpl@${map.key.warehouseId}',
	warehouseId: ${map.key.warehouseId},
	warehouseName: '${map.key.name}',
	templateId: ${map.key.warehouseId},
	productType: '${map.key.productType}',
	text: '${map.key.name}',
	freePostageAmount: '',
	expanded: <c:choose><c:when test="${map.key.expanded == true}">true</c:when><c:otherwise>false</c:otherwise></c:choose>,
	leaf: false,
	children:[
	<c:forEach var="postageTpl" varStatus="cvs" items="${map.value}">
	{
		id: 'childMenu@${postageTpl.templateId}',
		warehouseId: ${map.key.warehouseId},
		warehouseName: '${map.key.name}',
		templateId: ${postageTpl.templateId},
		isFreeTemplate: ${postageTpl.isFreeTemplate},
		text: '${postageTpl.name}',
		freePostageAmount: '${postageTpl.freePostageAmount}',
		leaf: 'true'
	}<c:if test="${!cvs.last}">,</c:if>
	</c:forEach>
	]
}<c:if test="${!vs.last}">,</c:if>
</c:forEach>
]