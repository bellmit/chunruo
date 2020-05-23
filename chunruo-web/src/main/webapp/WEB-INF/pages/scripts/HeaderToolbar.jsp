<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MenuModel', {
	extend: 'Ext.data.Model',
	fields: [
	   {name: 'menuId', type: 'int'},
	   {name: 'name', 	type: 'string'},
	   {name: 'ctrl', 	type: 'string'},
	   {name: 'icon', 	type: 'string'}
	]
});

Ext.define('MyExt.HeaderToolbar', {
   	extend : 'Ext.Panel',
   	region: 'center',
    closable: false,
    autoScroll: false,
    layout: 'border',
    fontStyle: '<span style="font-size:17px;font-weight:bold;margin:0 12px;color:#666666;">{0}</span>',
    fontMenuStyle: '<span style="font-size:14px;color:#666666;">{0}</span>',
    viewConfig: {
    	stripeRows : true,
    	enableTextSelection : true
    },

    initComponent : function(config) {
    	Ext.apply(this, config);
    	
	   	this.toolbar = Ext.create('Ext.Toolbar', { 
	   		width: 550,
	   		height: 50,
	   		scope: this,
	   		region: 'center',
			xtype: 'button',
			dock: 'bottom', 
			anchor: '100%', 
			layout: { type: 'hbox', pack: 'center' },
			style: {background: '#eeeeee none repeat scroll 0 0', 'border-color': '#eeeeee'},
	        defaults: {
                scale: 'medium',
                columns: 40,
                rowspan: 10
            },
	        items:[]
	    });
	    
		this.toolbarMenu = Ext.create('Ext.Toolbar', { 
	   		scope: this,
	   		border: false,
	   		region: 'center',
			xtype: 'button',
			dock: 'bottom', 
			height: 50,
			layout: { type: 'hbox', pack: 'left'},
			style: {background: '#eeeeee none repeat scroll 0 0', 'border-color': '#eeeeee'},
            defaults: {
                scale: 'large',
                columns: 2,
                rowspan: 3
            },
	        items:[{
	    		header: false,
				width: 200,
				height: 38,
				region: 'west',
	   			xtype: 'cycle',
	            iconCls: 'layout-hide',
	            showText: true,
	            scope: this,
	            type: 'defualt',
	        	changeHandler: this.changeHandler,
	        	style: {border: '1px solid #b0b0b0','font-size': '20px'},
	        	menu: {
                    id: 'reading-menu' + this.id,
                    width: 200,
                    items: [{
                    	id: 'headerToolber@Menu' + this.id,
                        baseCls: 'xd-menu-item-link xd-menu-icon-separator',
						text: Ext.String.format(this.fontStyle, '<fmt:message key="webapp.control.panel"/>'),
						iconCls: 'adds',
						checked: true,
						height: 44,
						scope: this
                    }
                    <c:forEach var="menuTreeNode" varStatus="status" items="${headerMenuTreeMaps}" >
					,{
						baseCls: 'xd-menu-item-link xd-menu-icon-separator',
						text: Ext.String.format(this.fontStyle, '${menuTreeNode.name}'),
						height: 44,
						iconCls: '${menuTreeNode.icon}',
						store: Ext.create('Ext.data.Store', {
					        autoDestroy: true,
					        model: 'MenuModel',
					        data: [
					        <c:forEach var="menu" varStatus="vs" items="${menuTreeNode.childrenNode}">
							{
								id:  'headerToolbar@${menu.menuId}',
								menuId: '${menu.menuId}',
								name: '${menu.name}',
								ctrl: '${menu.ctrl}',
								icon: '${menu.icon}',
								childData: [
								<c:forEach var="childMenu" varStatus="cvs" items="${menu.childrenNode}">
								{
									id:  'headerToolbar@${childMenu.menuId}',
									menuId: '${childMenu.menuId}',
									name: '${childMenu.name}',
									ctrl: '${childMenu.ctrl}',
									icon: '${childMenu.icon}',
								}<c:if test="${!cvs.last}">,</c:if>
								</c:forEach>
								]
							}<c:if test="${!vs.last}">,</c:if>
							</c:forEach>
					    	]
					    }),
						scope: this
					}
					</c:forEach>
					]
                }
			}]
	    });
	    
    	this.items = [this.toolbarMenu, this.toolbar];
    	this.callParent(arguments);
    	this.on('resize', function(panel, width, height, oldWidth, oldHeight, eOpts){
    		this.toolbar.setWidth(width - this.toolbarMenu.getWidth());
    	}, this)
    },
    
    changeHandler : function(cycle, activeItem){
   
   		this.toolbar.items.each(function(t){
   			if(t.type != null && t.type == 'dynamic'){
   				this.toolbar.remove(t, true);
   			}
   		}, this);
   		
 	 	if(activeItem.store != null && activeItem.store.getCount() > 0){
 	 		activeItem.store.each(function(record) {
 	 			if(record.data.childData.length > 0){
 	 				var menu = Ext.create('Ext.menu.Menu', {
 	 					id: 'dynamicMenu@' + record.data.id,
 	 					style: {overflow: 'visible'}
 	 				});
 	 		
 	 				for(var i = 0; i < record.data.childData.length; i ++){
 	 					var childData = record.data.childData[i];
 	 					menu.add({
 	 						id: 'dynamics@' + childData.id,
			 	 			menuId: childData.menuId,
			 	 			name: childData.name,
			 	 			text: Ext.String.format(this.fontMenuStyle, childData.name),
				        	ctrl: childData.ctrl,
				        	iconCls: childData.icon,	
				        	handler: this.onButtonClick,
				        	type: 'dynamic',
				        	scope: this,
				        	defaults: {
					        	scale: 'medium',
					            columns: 40,
					            rowspan: 10
					        }
 	 					});
 	 				}
 	 				
	 	 			this.toolbar.add({
	 	 				id: 'dynamic@' + record.data.id,
	 	 				menuId: record.data.menuId,
	 	 				name: record.data.name,
	 	 				text: Ext.String.format(this.fontStyle, record.data.name),
		        		ctrl: record.data.ctrl,
		        		iconCls: record.data.icon,
		        		type: 'dynamic',
		        		overCls: 'my-button',	
		        		menu: menu
	 	 			});
 	 			}else{
 	 				this.toolbar.add({
	 	 				id: 'dynamic@' + record.data.id,
	 	 				menuId: record.data.menuId,
	 	 				name: record.data.name,
	 	 				text: Ext.String.format(this.fontStyle, record.data.name),
		        		ctrl: record.data.ctrl,
		        		iconCls: record.data.icon,	
		        		handler: this.onButtonClick,
		        		type: 'dynamic',
		        		scope: this,
		        		defaults: {
			                scale: 'medium',
			                columns: 40,
			                rowspan: 10
			            }
	 	 			});
 	 			}
 	 			
			}, this);
 	    }
    },
    
    onButtonClick : function(item){
    	var newTabId = item.id + '_' + item.ctrl;
	    if(!Ext.getCmp(newTabId)){
	    	this.viewer.mainPanel.mask.show();
	    }
    
    	this.viewer.mainPanel.openConfigTab({
    		id: newTabId,
    		menuId: item.menuId,
    		style:{'line-height': '22px'},
    		viewer: this.viewer,  
    		title: item.name, 
    		xtype: item.ctrl,
    		xHeight: this.viewer.mainPanel.getHeight(),
    		xWidth: this.viewer.mainPanel.getWidth(),
    		listeners:{
            	scope: this,
            	afterrender: function (panel , eOpts) {
                	this.viewer.mainPanel.mask.hide();
             	}
            }
    	});
    }
});
