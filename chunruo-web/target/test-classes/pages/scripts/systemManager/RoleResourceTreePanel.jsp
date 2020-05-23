<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('RoleResource', {
	extend: 'Ext.data.TreeModel',
	idProperty: 'categoryId',
   	fields: [
    	{name: 'menuId',	type: 'int'},
		{name: 'text',	 	type: 'string'},
		{name: 'enableType',type: 'int'},
		{name: 'namePath',	type: 'string'}
    ]
});

Ext.define('MyExt.systemManager.RoleResourceTreePanel', {
	extend : 'Ext.panel.Panel',
	closable: false,
	columnLines: true,
	animCollapse: true,
	layout: 'border',
   	defaults: {  
    	split: true,    
        collapsible: false
    },
    
	initComponent : function(config) {
		this.treePanelMask = new Ext.LoadMask(this, {msg:"Please wait..."});
		Ext.apply(this, config);
		
		this.store = Ext.create('Ext.data.TreeStore', {
			model: 'RoleResource',
			autoLoad: true,
			proxy: {
				type: 'ajax',
				url: '<c:url value="/resourceSys/queryResourceMenuNodes.js"/>'
			}
       	});
		
       	this.selectResourcePanel = Ext.create('Ext.grid.GridPanel', {
			width: 300,
			region: 'center',
	        header: false,
	        autoScroll: true,
		    columns: [{
		    	text: '<fmt:message key="resource.name" />', 
		    	dataIndex: 'namePath', 
		    	flex: 1,  
		    	sortable : true
		    }],
		    buttonAlign: 'center',
		    buttons: [{
				text: '<fmt:message key="button.save"/>', 
				scope: this,  
	        	handler: this.saveRoleResource
	    	}],
		    store: Ext.create('Ext.data.Store', {
	     		autoDestroy: true,
	     		model: 'RoleResource',
	     		data: []
	    	})
	    });  
       	
       	this.resourceTreePanal = Ext.create('Ext.tree.Panel', {
	    	region: 'west',
		    header: false,
		    checkPropagation: 'both',
		    store: this.store,
		    rootVisible: false,
		    useArrows: true,
		    title: 'Check Tree',
		    width: 500,
		    bufferedRenderer: false,
		    animate: true
		});
		
       	this.resourceTreePanal.on('checkchange', function(node, checked, e, eOpts){
    		this.getResourceCheckData();
    	}, this);
       	
       	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	roleId: this.roleId
			});
	    }, this);
	    
	    this.store.on('load', function(store, records, successful, eOpts){
	    	this.treePanelMask.hide();
	    	this.getResourceCheckData();
	    }, this);
	    
	    this.items = [this.resourceTreePanal, this.selectResourcePanel];	
    	this.callParent();
    },
    
    saveRoleResource : function(){
    	Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
			if(e == 'yes'){
				var resourceIdList = [];
    			this.selectResourcePanel.store.each(function(record) {
	   				resourceIdList.push(record.data.menuId);    
	    		}, this);
	    
             	Ext.Ajax.request({
		        	url: '<c:url value="/roleSys/saveResourceToRole.json"/>',
		         	method: 'post',
					scope: this,
					params:{roleId: this.roleId, idListGridJson: Ext.JSON.encode(resourceIdList)},
		          	success: function(response){
          				var responseObject = Ext.JSON.decode(response.responseText);
          				if (responseObject.success == true){
          					showSuccMsg(responseObject.message);
							this.store.loadPage(1);
          				}else{
          					showFailMsg(responseObject.message, 4);
          				}
					}
		     	})
   			}
   		}, this)
    },
    
    getResourceCheckData : function(){
    	this.selectResourcePanel.store.removeAll(false);
		Ext.each(this.resourceTreePanal.getChecked(), function (node) {
			this.selectResourcePanel.store.add(node.data);
		}, this);
    },
    
    transferData : function(roleId, title){
    	this.treePanelMask.show();
    	this.roleId = roleId;
    	this.setTitle(title);
    	this.selectResourcePanel.store.removeAll(false);
    	this.resourceTreePanal.getRootNode().removeAll(false);
    	this.store.clearData();
    	this.store.reload();
    }
})
