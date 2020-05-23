<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>
[
<c:forEach var="rootTreeNode" varStatus="vs" items="${allMenuTreeMaps}">
{
	id: 'menu@${rootTreeNode.menuId}',
	menuId: ${rootTreeNode.menuId},
	text: '${rootTreeNode.name}',
	enableType: '${rootTreeNode.enableType}',
	iconCls: 'Bricks',
	expanded: true, 
	leaf: false,
	children:[
	<c:forEach var="menuTreeNode" varStatus="cvs" items="${rootTreeNode.childrenNode}">
	{
		id: 'childMenu@${menuTreeNode.menuId}',
		menuId: ${menuTreeNode.menuId},
		text: '${menuTreeNode.name}',
		namePath: '${menuTreeNode.namePath}',
		enableType: '${menuTreeNode.enableType}',
		<c:choose>
			<c:when test="${rootTreeNode.menuId == -1}">
				checked: ${menuTreeNode.status},
				iconCls: 'app_manager',
				leaf: 'true'
			</c:when>
			<c:otherwise>
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
							enableType: '${lastTreeNode.enableType}',
							<c:choose>
								<c:when test="${fn:length(lastTreeNode.childrenNode) > 0}">
									expanded: true,
									iconCls: 'Brick',
									leaf: 'false',
									children:[
									<c:forEach var="resourceTreeNode" varStatus="lvs" items="${lastTreeNode.childrenNode}">
									{
										id: 'childMenu@${resourceTreeNode.menuId}',
										menuId: ${resourceTreeNode.menuId},
										text: '${resourceTreeNode.name}',
										namePath: '${resourceTreeNode.namePath}',
										enableType: '${resourceTreeNode.enableType}',
										<c:choose>
											<c:when test="${fn:length(resourceTreeNode.childrenNode) > 0}">
												expanded: true,
												leaf: 'false',
												children:[
												<c:forEach var="lastResourceTreeNode" varStatus="lvs" items="${resourceTreeNode.childrenNode}">
												{
													id: 'childMenu@${lastResourceTreeNode.menuId}',
													menuId: ${lastResourceTreeNode.menuId},
													text: '${lastResourceTreeNode.name}',
													namePath: '${lastResourceTreeNode.namePath}',
													enableType: '${lastResourceTreeNode.enableType}',
													checked: ${lastResourceTreeNode.status},
													iconCls: 'app_manager',
													leaf: 'true'
												}<c:if test="${!lvs.last}">,</c:if>
												</c:forEach>
												]
											</c:when>
											<c:otherwise>
												<c:choose>
													<c:when test="${!resourceTreeNode.isResource}">
														iconCls: 'Brick',
														leaf: 'true'
													</c:when>
													<c:otherwise>
														iconCls: 'app_manager',
														checked: ${resourceTreeNode.status},
														leaf: 'true'
													</c:otherwise>
												</c:choose>
											</c:otherwise>
										</c:choose>
									}<c:if test="${!lvs.last}">,</c:if>
									</c:forEach>
									]
								</c:when>
								<c:otherwise>
									<c:choose>
										<c:when test="${!lastTreeNode.isResource}">
											iconCls: 'Brick',
											leaf: 'true'
										</c:when>
										<c:otherwise>
											iconCls: 'app_manager',
											checked: ${lastTreeNode.status},
											leaf: 'true'
										</c:otherwise>
									</c:choose>
								</c:otherwise>
							</c:choose>
						}<c:if test="${!lvs.last}">,</c:if>
						</c:forEach>
						]
					</c:when>
					<c:otherwise>
						iconCls: 'Brickadd',
						leaf: 'true'
					</c:otherwise>
				</c:choose>
			</c:otherwise>
		</c:choose>
	}<c:if test="${!cvs.last}">,</c:if>
	</c:forEach>
	]
}<c:if test="${!vs.last}">,</c:if>
</c:forEach>
]