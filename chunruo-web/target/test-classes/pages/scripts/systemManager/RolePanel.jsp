<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('Role', {
	extend: 'Ext.data.Model',
    fields: [
    	{name: 'id',  		  type: 'int'},
    	{name: 'name',        type: 'string'},
    	{name: 'description', type: 'string'},
    	{name: 'createTime',  type: 'string'},
    	{name: 'updateTime',  type: 'string'}
    ],
    idProperty: 'id'
});

Ext.define('MyExt.systemManager.RolePanel', {
    extend : 'Ext.panel.Panel',
    header: false,
	closable: true,
	columnLines: true,
	animCollapse: true,
	layout: 'border',
   	defaults: {  
    	split: true,    
        collapsible: false
    },
    
	initComponent : function(config) {
		Ext.apply(this, config);
		 
    	this.store = Ext.create('Ext.data.Store', {
        	pageSize: 5000,
	        autoLoad: false,
			autoDestroy: true,
			sortOnLoad: true,
			remoteSort: true,
			model: 'Role',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/roleSys/list.json"/>',
				reader: {
					type : 'json',
                	root: 'data',
                	totalProperty: 'totalCount'
            	}
			},
			sorters: [{
	            property: 'createTime',
	            direction: 'desc'
	        }]
		});
		
		this.roleList = Ext.create('Ext.grid.GridPanel', {
			region: 'west',
			header: false,
			width: 330,
			autoScroll: true,   
			closable: true,
			multiSelect: true,
			columnLines: true,
			animCollapse: true,
		    enableLocking: true,
		    selType: 'checkboxmodel',
		    columns: [
				{text: '<fmt:message key="role.roleId" />', dataIndex: 'id', width: 70, sortable: true},
		    	{text: '<fmt:message key="role.name" />', dataIndex: 'name', width: 230,  sortable : true}
	        ],
		    store: this.store,
		    tbar:[
		    <jkd:haveAuthorize access="/roleSys/editRole.json">
        	{
        		text: '<fmt:message key="template.save.update"/>', 
        		iconCls: 'add', 
        		handler: this.modifyRole, 
        		scope: this
        	}
			</jkd:haveAuthorize>
		    ]
	    });    
	    
	    this.east =  Ext.create('MyExt.systemManager.RoleResourceTreePanel', {
        	region: 'center',
	        autoScroll: true
        });
    	
    	this.items = [this.roleList, this.east];	
		this.east.hide();
		this.callParent(arguments);
	    
	    this.gsm = this.roleList.getSelectionModel();
	    <jkd:haveAuthorize access="/roleSys/list.json">
    	this.store.load();
    	</jkd:haveAuthorize>
    	
	    this.roleList.on('itemdblclick', function(view, record, item, index, e, eOpts) {
	    	<jkd:haveAuthorize access="/resourceSys/queryResourceMenuNodes.js">
	    	var title = '<fmt:message key="button.left"/>' + record.data.name + '<fmt:message key="button.right"/>';
	    	this.east.transferData(record.data.id, title);
	    	this.east.show();
	    	</jkd:haveAuthorize>
	    }, this);
    },
    
	modifyRole : function(){
	 	var records = this.gsm.getSelection();
       	if(records.length > 1){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="user.approve.records"/>');
			return;
		}
		
		var roleFormPanel = Ext.create('MyExt.systemManager.RoleFormPanel', {id: 'modifyRole@systemManager', title: '<fmt:message key="role.edit.title"/>'});
		if(records.length > 0){
	    	roleFormPanel.load({   
	    		waitMsg: '<fmt:message key="ajax.waitMsg"/>',   
	    		waitTitle: '<fmt:message key="ajax.waitTitle"/>',
	    		url: '<c:url value="/roleSys/editRole.json"/>', 
	    		params: {roleId: records[0].data.id}, 
	    		failure : function (form, action) {
	     			showMsg('<fmt:message key="ajax.waitTitle"/>', '<fmt:message key="ajax.load.failure"/>', 4);   
	    		}   
	   		});
	   	}
	   		
		var buttons = [
		<jkd:haveAuthorize access="/roleSys/saveRole.json">
		{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
	            if(roleFormPanel.form.isValid()){
	            	Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
			                roleFormPanel.form.submit({
			                    waitMsg: 'Loading...',
			                    url: '<c:url value="/roleSys/saveRole.json"/>',
			                    scope: this,
			                    success: function(form, action) {
			                        var responseObject = Ext.JSON.decode(action.response.responseText);
			                        if(responseObject.error == false){
			                       		showSuccMsg(responseObject.message);
			                        	this.store.loadPage(1);
		                    			this.gsm.deselectAll();
										popWin.close();
									}else{
										showFailMsg(responseObject.message, 4);
									}
			                    }
			        		})
			        	}
	 				}, this)
	        	}
			},
			scope: this
		},
		</jkd:haveAuthorize>
		{
			text: '<fmt:message key="button.cancel"/>',
			handler : function(){popWin.close();},
			scope: this
		}]
		openWin('<fmt:message key="role.edit.title"/>', roleFormPanel, buttons, 400, 100);
	}
});