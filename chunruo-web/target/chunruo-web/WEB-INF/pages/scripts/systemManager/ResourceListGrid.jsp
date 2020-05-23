<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('Resource', {
	extend: 'Ext.data.Model',
    fields: [
    	{name: 'resourceId',  type: 'int'},
    	{name: 'name',        type: 'string'},
    	{name: 'linkPath', 	  type: 'string'},
    	{name: 'menuPath', 	  type: 'string'},
    	{name: 'isEnable', 	  type: 'bool'},
    	{name: 'createTime',  type: 'string'},
    	{name: 'updateTime',  type: 'string'}
    ],
    idProperty: 'resourceId'
});

Ext.define('MyExt.systemManager.ResourceListGrid', {
	extend : 'Ext.grid.GridPanel',
    requires : ['Ext.ux.grid.GridHeaderFilters'],
	region: 'center',
	autoScroll: true,   
	closable: true,
	selType: 'checkboxmodel',
	multiSelect: true,
	columnLines: true,
	animCollapse: false,
    enableLocking: true,
    plugins: ['gridHeaderFilters'],
    viewConfig: {
        stripeRows: true,
        enableTextSelection: true
    },
    
	initComponent : function(config) {
		Ext.apply(this, config);
		
		this.store = Ext.create('Ext.data.Store', {
        	pageSize: 50,
	        autoLoad: false,
			autoDestroy: true,
			sortOnLoad: true,
			remoteSort: true,
			model: 'Resource',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/resourceSys/list.json"/>',
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
		
		this.rendererStutsStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: 1, name: '<fmt:message key="button.yes"/>'},
        		{id: 0, name: '<fmt:message key="button.no"/>'}
        	]
        });
		
	    this.columns = [
	    	{text: '<fmt:message key="resource.resourceId" />', dataIndex: 'resourceId', width: 70, align: 'center', sortable: true,
	    		filter: {xtype: 'textfield'}
	    	},
            {text: '<fmt:message key="resource.name" />', dataIndex: 'name', flex: 1, sortable : true,
            	filter: {xtype: 'textfield'}
            },
            {text: '<fmt:message key="resource.linkPath" />', dataIndex: 'linkPath', width: 400, sortable: true,
            	filter: {xtype: 'textfield'}
            },
            {text: '<fmt:message key="resource.menuPath" />', dataIndex: 'menuPath', width: 250, sortable: true,
            	filter: {xtype: 'textfield'}
            },
            {text: '<fmt:message key="resource.isEnable" />', dataIndex: 'isEnable', width: 80, align: 'center', sortable: true, 
            	renderer: this.rendererStuts,
            	filter: {
					xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.rendererStutsStore,
			        queryMode: 'local',
			        typeAhead: true
				}
			},  
            {text: '<fmt:message key="resource.createTime" />', dataIndex: 'createTime', width: 140, sortable: true}
        ];
        
        this.tbar = [
        <jkd:haveAuthorize access="/resourceSys/addResource.json">
        {
        	text: '<fmt:message key="button.add"/>', 
        	iconCls: 'add', 
        	handler: this.addResource, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/resourceSys/deleteResource.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
        	text: '<fmt:message key="button.delete"/>', 
        	iconCls: 'delete',	
        	handler: this.deleteResource, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/resourceSys/updateResourceStatus.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
        	text: '<fmt:message key="button.enable"/>', 
        	iconCls: 'enable',	
        	handler: this.enableResource, 
        	scope: this
        },{
        	text: '<fmt:message key="button.disable"/>', 
        	iconCls: 'disabled',	
        	handler: this.disableResource, 
        	scope: this
        }
        </jkd:haveAuthorize>
        ];
        
        this.bbar = new Ext.PagingToolbar({
        	pageSize: 50,
			store: this.store,
			autoheigth: true,
			displayInfo: true,
			displayMsg: '<fmt:message key="ajax.record"/>',
			emptyMsg: '<fmt:message key="ajax.no.record"/>',
			scope: this,
			items: ['-',{ 
				xtype: 'numberfield', 
				width: 120, 
				labelWidth: 65,
				value: 50, 
				minValue: 1, 
				fieldLabel: '<fmt:message key="ajax.record.size"/>',
                allowBlank: false,
               	scope: this,
                listeners:{
                	scope: this,
               		change: function (field, newValue, oldValue) {
                    	var number = parseInt(newValue);
                        if (isNaN(number) || !number || number < 1) {
                        	number = 50;
                           	Field.setValue(number);
                        }
                       	this.store.pageSize = number;
                       	this.store.load();
                   	}
               	}
        	}]	
		});
    	this.callParent();
    	
    	<jkd:haveAuthorize access="/resourceSys/list.json">
    	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.filters)
			});
	    }, this);
    	this.store.load();
    	</jkd:haveAuthorize>
    	
    	this.gsm = this.getSelectionModel();
    	<jkd:haveAuthorize access="/resourceSys/editResource.json">
    	this.on('itemdblclick', this.onDbClick, this);
    	</jkd:haveAuthorize>
    },
    
    onDbClick : function(view, record, item, index, e, eOpts) {
    	var resourceFormPanel = Ext.create('MyExt.systemManager.ResourceFormPanel', {id: 'modifyResource@systemManager', title: '<fmt:message key="resource.edit.title"/>'});
    	resourceFormPanel.load({   
    		waitMsg: '<fmt:message key="ajax.waitMsg"/>',   
    		waitTitle: '<fmt:message key="ajax.waitTitle"/>',
    		url: '<c:url value="/resourceSys/editResource.json"/>', 
    		params: {resourceId: record.data.resourceId}, 
    		failure : function (form, action) {
     			showMsg('<fmt:message key="ajax.waitTitle"/>', '<fmt:message key="ajax.load.failure"/>', 4);   
    		}   
   		});
   		
    	var buttons = [
    	<jkd:haveAuthorize access="/resourceSys/updateResource.json">
    	{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
				if(resourceFormPanel.form.isValid()){
					Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
			                resourceFormPanel.form.submit({
			                    waitMsg: 'Loading...',
			                    url: '<c:url value="/resourceSys/updateResource.json"/>',
			                    scope: this,
			                    success: function(form, action) {
			                        var responseObject = Ext.JSON.decode(action.response.responseText);
			                        if(responseObject.error == false){
			                       		showSuccMsg(responseObject.message);
			                        	this.store.loadPage(1);
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
		openWin('<fmt:message key="resource.edit.title"/>', resourceFormPanel, buttons, 600, 200);
    },

	addResource : function(){
		var resourceFormPanel = Ext.create('MyExt.systemManager.ResourceFormPanel', {id: 'addGroup@systemManager', title: '<fmt:message key="resource.add.title"/>'});
		var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
	            if(resourceFormPanel.form.isValid()){
	            	Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
			                resourceFormPanel.form.submit({
			                    waitMsg: 'Loading...',
			                    url: '<c:url value="/resourceSys/addResource.json"/>',
			                    scope: this,
			                    success: function(form, action) {
			                        var responseObject = Ext.JSON.decode(action.response.responseText);
			                        if(responseObject.error == false){
			                       		showSuccMsg(responseObject.message);
			                        	this.store.loadPage(1);
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
		},{
			text: '<fmt:message key="button.cancel"/>',
			handler : function(){popWin.close();},
			scope: this
		}]
		openWin('<fmt:message key="resource.add.title"/>', resourceFormPanel, buttons, 600, 200);
	},
	
	deleteResource : function() {
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.resourceId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="delete.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/resourceSys/deleteResource.json"/>',
		         	method: 'post',
					scope: this,
					params:{resourceGridJson: Ext.JSON.encode(rowsData)},
		          	success: function(response){
          				var responseObject = Ext.JSON.decode(response.responseText);
          				if (responseObject.success == true){
          					showSuccMsg(responseObject.message);
							this.store.loadPage(1);
		                    this.gsm.deselectAll();
          				}else{
          					showFailMsg(responseObject.message, 4);
          				}
					}
		     	})
	     	}
	 	}, this)  
	},
	
	enableResource : function() {
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.resourceId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/resourceSys/updateResourceStatus.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData), isEnable: true},
		          	success: function(response){
          				var responseObject = Ext.JSON.decode(response.responseText);
          				if (responseObject.success == true){
          					showSuccMsg(responseObject.message);
							this.store.loadPage(1);
		                    this.gsm.deselectAll();
          				}else{
          					showFailMsg(responseObject.message, 4);
          				}
					}
		     	})
	     	}
	 	}, this)  
	},
	
	disableResource : function() {
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.resourceId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/resourceSys/updateResourceStatus.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData), isEnable: false},
		          	success: function(response){
          				var responseObject = Ext.JSON.decode(response.responseText);
          				if (responseObject.success == true){
          					showSuccMsg(responseObject.message);
							this.store.loadPage(1);
		                    this.gsm.deselectAll();
          				}else{
          					showFailMsg(responseObject.message, 4);
          				}
					}
		     	})
	     	}
	 	}, this)  
	},
	
	rendererStuts : function(val){
		if(val == true) {
            return '<span style="color:green;"><b><fmt:message key="button.yes"/></b></span>';
        }else{
            return '<fmt:message key="button.no"/>';
        }
	}
})
