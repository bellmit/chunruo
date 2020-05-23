<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('Menu', {
	extend: 'Ext.data.Model',
	idProperty: 'menuId',
    fields: [
    	{name: 'menuId',	mapping: 'menuId',		type: 'int'},
		{name: 'name',		mapping: 'name',		type: 'string'},
		{name: 'ctrl',		mapping: 'ctrl',		type: 'string'},
		{name: 'sequence',	mapping: 'sequence',	type: 'int'},
		{name: 'parentId',	mapping: 'parentId',	type: 'int'},
		{name: 'status',	mapping: 'status',		type: 'bool'},
		{name: 'icon',		mapping: 'icon',		type: 'string'},
		{name: 'desc',		mapping: 'desc',		type: 'string'}
    ]
});

Ext.define('MyExt.systemManager.MenuManagerPanel', {
    extend : 'Ext.panel.Panel',
	closable: true,
	columnLines: true,
	animCollapse: true,
	layout: 'border',
    viewConfig: {
        stripeRows: true,
        enableTextSelection: true
    },
    
	initComponent : function(config) {
		Ext.apply(this, config);
		
		this.store = Ext.create('Ext.data.TreeStore', {
        	proxy: {
            	type: 'ajax',
            	url: '<c:url value="/menuSys/queryMenuNodes.js"/>'
        	},
        	root: {
            	text: 'ROOT',
            	expanded: true
        	}
    	});
    	
		this.menuTreePanel = Ext.create('Ext.TreePanel', {
			region: 'west',
			requires : [],
			autoScroll: true,
			rootVisible: false, 
    		border: true,
    		enableDD: false,
			animate: true,
			containerScroll: true,
			layout: {type: 'table'},
			store: this.store,
	        width: 300,
	        split: true,
	        header: false,
	        tbar:[
	        <jkd:haveAuthorize access="/menuSys/saveMenu.json">
	        {
		       	iconCls: 'add',
		       	type: 'root',
				text: '<fmt:message key="system.menu.add"/>',
				handler: function(){this.addMenu(0)},
		    	scope: this
	        }
	        <c:set var="isHaveAuthorize" value="true" />
			</jkd:haveAuthorize>
			<jkd:haveAuthorize access="/menuSys/queryMenuNodes.js">
			<c:if test="${isHaveAuthorize}">,</c:if>
	        {
		       	iconCls: 'refresh',
		       	type: 'root',
				text: '<fmt:message key="system.menu.refresh"/>',
				handler: function(){this.store.reload()},
		    	scope: this
		    }
		    </jkd:haveAuthorize>
		    ]
        });
        
        this.menu = Ext.create('Ext.menu.Menu', {
        	items:[
        	<jkd:haveAuthorize access="/menuSys/saveMenu.json">
        	{
		       	iconCls: 'add',
				text: '<fmt:message key="system.menu.add"/>',
				handler: function(){this.addMenu(1)},
		    	scope: this
		    }
		    <c:set var="isHaveAuthorize" value="true" />
			</jkd:haveAuthorize>
			<jkd:haveAuthorize access="/menuSys/deleteMenuById.json">
			<c:if test="${isHaveAuthorize}">,</c:if>
		    {
		       	iconCls: 'delete',
				text: '<fmt:message key="system.menu.delete"/>',
				handler: this.deleteMenu,
		    	scope: this
		    }
		    </jkd:haveAuthorize>
		    ]
    	});
        
        this.menuForm =  Ext.create('Ext.form.Panel', {
		 	header: false,
		 	region: 'center',
		 	labelWidth: 45,
		 	bodyPadding: '15 5 20',
		    defaultType: 'textfield',
		    layout: 'form',
		    autoScroll: true,
		    menuPanelMask: new Ext.LoadMask(this, {msg:"Please wait..."}),
		    viewConfig: {
		        stripeRows: true,
		        enableTextSelection: true
		    },
		    items: [{
			    xtype: 'hiddenfield',
			    name: 'menuId',
			    anchor:'97%'
			},{
			    xtype: 'hiddenfield',
			    name: 'parentId',
			    anchor:'97%'
			},{
			    xtype:'textfield',
			    fieldLabel: '<fmt:message key="system.menu.name"/>',
			    allowBlank: false,
			    name: 'name',
			    anchor:'97%'
			},{
				xtype: 'radiogroup',
	            fieldLabel: '<fmt:message key="system.menu.status"/>',
	            allowBlank: false,
	            columns: [100, 100],
            	vertical: true,
	            items: [
	                {boxLabel: '<fmt:message key="button.yes"/>', name: 'status', inputValue: true},
	                {boxLabel: '<fmt:message key="button.no"/>', name: 'status', inputValue: false, checked: true}
	            ]
	        },{
			    xtype:'textfield',
			    fieldLabel: '<fmt:message key="system.menu.ctrl"/>',
			    name: 'ctrl',
			    anchor:'97%'
			},{
			    xtype:'textfield',
			    fieldLabel: '<fmt:message key="system.menu.icon"/>',
			    name: 'icon',
			    anchor:'97%'
			},{
			    xtype:'numberfield',
			    fieldLabel: '<fmt:message key="system.menu.sequence"/>',
			    allowBlank: false,
			    name: 'sequence',
			    value: 1,
			    anchor: '50%'
			},{
			    xtype:'textfield',
			    fieldLabel: '<fmt:message key="system.menu.desc"/>',
			    name: 'desc',
			    anchor:'97%'
			},{
				xtype: 'radiogroup',
	            fieldLabel: '',
	            columns: [100, 100],
            	vertical: true,
            	labelAlign: 'left',
	            items: [{
			    	xtype:'button',
			    	text: '<fmt:message key="button.refresh.cache"/>',
			    	handler: this.refreshMenu, 
        			scope: this
				},{
					xtype:'button',
			    	bodyPadding: '15 5 20 50',
			    	text: '<fmt:message key="button.save"/>',
			    	handler: this.saveMenu, 
        			scope: this
	            }]
	        }]
        });
		
    	this.items = [this.menuTreePanel, this.menuForm];	
    	this.callParent(arguments);
    	
    	<jkd:haveAuthorize access="/menuSys/getMenuById.json">
    	this.menuTreePanel.on('itemclick', this.onClick, this);
    	</jkd:haveAuthorize>
    	
    	this.menuTreePanel.on('itemcontextmenu', function(view, record, item, index, e){
    		e.stopEvent();
			this.menuTreePanel.getSelectionModel().select(record);
	    	this.menu.showAt(e.getXY());		
		}, this);
    },
    
    onClick : function(treePanel, record, item, index, e){
    	this.menuForm.menuPanelMask.show();
    	this.menuForm.loadRecord(Ext.create('Menu'));
    	Ext.Ajax.request({
       		url: '<c:url value="/menuSys/getMenuById.json"/>',
        	method: 'post',
			scope: this,
			params:{menuId: record.raw.menuId},
         	success: function(response){
         		this.menuForm.menuPanelMask.hide();
         		var responseObject = Ext.JSON.decode(response.responseText);
         		if (responseObject.success == true && responseObject.menu != null){
         			var recordData = Ext.create('Menu', responseObject.menu);
         			this.menuForm.loadRecord(recordData);
         			this.menuForm.down('radiofield[inputValue=false]').setValue(!recordData.data.status);
         			this.menuForm.down('radiofield[inputValue=true]').setValue(recordData.data.status);
    			}
    		}
    	});
    },
    
    addMenu : function(type){
    	this.menuForm.menuPanelMask.show();
		this.menuForm.loadRecord(Ext.create('Menu'));
		this.menuForm.down('radiofield[inputValue=false]').setValue(true);
        this.menuForm.down('radiofield[inputValue=true]').setValue(false);
        if(type == 1){
        	var parentMenu = this.menuTreePanel.getSelectionModel().getLastSelected();
    		this.menuForm.down('hiddenfield[name=parentId]').setValue(parentMenu.raw.menuId);
        }
        
        var taskConfig = { 
        	scope: this,
    		run : function() {  
	        	this.menuForm.menuPanelMask.hide();
    		},  
    		interval: 3000,
    		repeat: 1 
		} 
		var task = new Ext.util.TaskRunner();
  		task.start(taskConfig);
	},
	
	refreshMenu : function(){
		this.menuForm.menuPanelMask.show();
		var menuId = this.menuForm.down('hiddenfield[name=menuId]').getValue();
		this.menuForm.loadRecord(Ext.create('Menu'));
		if(menuId != null && menuId.length > 0){
			Ext.Ajax.request({
	       		url: '<c:url value="/menuSys/getMenuById.json"/>',
	        	method: 'post',
				scope: this,
				params:{menuId: menuId},
	         	success: function(response){
	         		this.menuForm.menuPanelMask.hide();
	         		var responseObject = Ext.JSON.decode(response.responseText);
	         		if (responseObject.success == true && responseObject.menu != null){
	         			var recordData = Ext.create('Menu', responseObject.menu);
	         			this.menuForm.loadRecord(recordData);
	         			this.menuForm.down('radiofield[inputValue=false]').setValue(!recordData.data.status);
         				this.menuForm.down('radiofield[inputValue=true]').setValue(recordData.data.status);
         				this.menuTreePanel.setRootNode();
	    			}
	    		}
	    	});
		}else{
			this.menuForm.menuPanelMask.hide();
		}
	},
	
	saveMenu : function(){
		if(this.menuForm.form.isValid()){
	        Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
				if(e == 'yes'){
					this.menuForm.menuPanelMask.show();
	                this.menuForm.form.submit({
	                    waitMsg: 'Loading...',
	                   	url: '<c:url value="/menuSys/saveMenu.json"/>',
	                    scope: this,
	                    success: function(form, action) {
	                    	this.menuForm.menuPanelMask.hide();
	                        var responseObject = Ext.JSON.decode(action.response.responseText);
	                        if(responseObject.error == false){
	                       		showSuccMsg(responseObject.message);
	                        	this.menuTreePanel.setRootNode();
							}else{
								showFailMsg(responseObject.message, 4);
							}
	                    }
	        		})
	        	}
	        }, this)
		}
	},
	
	deleteMenu : function(){
		var menu = this.menuTreePanel.getSelectionModel().getLastSelected();
		Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="delete.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/menuSys/deleteMenuById.json"/>',
		         	method: 'post',
					scope: this,
					params:{menuId: menu.raw.menuId},
		          	success: function(response){
          				var responseObject = Ext.JSON.decode(response.responseText);
                        if(responseObject.success == true){
                       		showSuccMsg(responseObject.message);
                        	this.menuTreePanel.setRootNode();
						}else{
							showFailMsg(responseObject.message, 4);
						}
					}
		     	})
        	}
        }, this)
	}
});