<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>
[
<c:forEach var="map" varStatus="vs" items="${productCategoryMap}">
{
	id: '${map.key.categoryId}',
	parentId: '${map.key.parentId}',
	name: '${map.key.name}',
	imagePath: '${map.key.imagePath}',
	text: '${map.key.name}',
	description: '${map.key.description}',
	status: '${map.key.status}',
	sort: '${map.key.sort}',
	level: '${map.key.level}',
	profit:'${map.key.profit}',
	imagePath:'${map.key.imagePath}',
	description:'${map.key.description}',
	tagNames:'${map.key.tagNames}',
	createTime:'${map.key.createTime}',
	updateTime:'${map.key.updateTime}',
	expanded: <c:choose><c:when test="${map.key.expanded == true}">true</c:when><c:otherwise>false</c:otherwise></c:choose>,
	leaf: false,
	children:[
	<c:forEach var="childCategory" varStatus="cvs" items="${map.value}">
	{
		id: '${childCategory.categoryId}',
		parentId: '${childCategory.parentId}',
		name: '${childCategory.name}',
		imagePath: '${childCategory.imagePath}',
		text: '${childCategory.name}',
		description: '${childCategory.description}',
		status: '${childCategory.status}',
		sort: '${childCategory.sort}',
		level: '${childCategory.level}',
		profit:'${childCategory.profit}',
		imagePath:'${childCategory.imagePath}',
		description:'${childCategory.description}',
		tagNames:'${childCategory.tagNames}',
		createTime:'${childCategory.createTime}',
		updateTime:'${childCategory.updateTime}',
		leaf: 'true'
	}<c:if test="${!cvs.last}">,</c:if>
	</c:forEach>
	]
}<c:if test="${!vs.last}">,</c:if>
</c:forEach>
]