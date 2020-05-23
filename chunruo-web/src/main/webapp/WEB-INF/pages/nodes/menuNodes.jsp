<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>
[
<c:forEach var="rootTreeNode" varStatus="vs" items="${allMenuTreeMaps}">
{
	id: 'menu@${rootTreeNode.menuId}',
	menuId: ${rootTreeNode.menuId},
	text: '${rootTreeNode.name}',
	namePath: '${rootTreeNode.name}',
	iconCls: 'Bricks',
	leaf: <c:choose><c:when test="${rootTreeNode.menuId == -1}">true</c:when><c:otherwise>false</c:otherwise></c:choose>,
	children:[
	<c:forEach var="menuTreeNode" varStatus="cvs" items="${rootTreeNode.childrenNode}">
	{
		id: 'childMenu@${menuTreeNode.menuId}',
		menuId: ${menuTreeNode.menuId},
		text: '${menuTreeNode.name}',
		namePath: '${menuTreeNode.namePath}',
		<c:choose>
			<c:when test="${fn:length(menuTreeNode.childrenNode) > 0}">
				expanded: true,
				iconCls: 'Brickadd',
				leaf: 'false',
				children:[
				<c:forEach var="lastTreeNode" varStatus="lvs" items="${menuTreeNode.childrenNode}">
				{
					id: 'childMenu@${lastTreeNode.menuId}',
					menuId: ${lastTreeNode.menuId},
					text: '${lastTreeNode.name}',
					namePath: '${lastTreeNode.namePath}',
					iconCls: 'Brick',
					leaf: 'true'
				}<c:if test="${!lvs.last}">,</c:if>
				</c:forEach>
				]
			</c:when>
			<c:otherwise>
				iconCls: 'Brick',
				leaf: 'true'
			</c:otherwise>
		</c:choose>
	}<c:if test="${!cvs.last}">,</c:if>
	</c:forEach>
	]
}<c:if test="${!vs.last}">,</c:if>
</c:forEach>
]