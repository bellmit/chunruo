<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>
[
<c:forEach var="productCategory" varStatus="vs" items="${allProductCategoryLists}">
{
	id: 'category@${productCategory.categoryId}',
	categoryId: ${productCategory.categoryId},
	text: '${productCategory.name}',
	leaf: false,
	children:[
	<c:forEach var="childCategory" varStatus="cvs" items="${productCategory.childCategoryList}">
	{
		id: 'category@${childCategory.categoryId}',
		categoryId: ${childCategory.categoryId},
		text: '${childCategory.name}',
		leaf: 'true'
	}<c:if test="${!cvs.last}">,</c:if>
	</c:forEach>
	]
}<c:if test="${!vs.last}">,</c:if>
</c:forEach>
]