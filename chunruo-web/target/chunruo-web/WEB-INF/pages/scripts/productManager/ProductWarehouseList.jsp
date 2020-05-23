<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('ProductWarehouse', {
	extend: 'Ext.data.Model',
	idProperty: 'warehouseId',
     fields: [
    	{name: 'warehouseId',	mapping: 'warehouseId',	type: 'string'},
		{name: 'name',	 		mapping: 'name',		type: 'string'},
		{name: 'tplArea',	 	mapping: 'tplArea',		type: 'string'},
		{name: 'warehouseType',	mapping: 'warehouseType',	type: 'int'},
		{name: 'productType',	mapping: 'productType',	type: 'int'},
		{name: 'isPushCustoms',	mapping: 'isPushCustoms',	type: 'bool'},
		{name: 'isDirectPushErp',	mapping: 'isDirectPushErp',	type: 'bool'},
		{name: 'createTime',	mapping: 'createTime',	type: 'string'},
		{name: 'updateTime',	mapping: 'updateTime',	type: 'string'},
    ]
});

Ext.define('MyExt.productManager.ProductWarehouseList', {
    extend : 'Ext.panel.Panel',
    requires : ['Ext.ux.grid.GridHeaderFilters','MyExt.DateSelectorPicker'],
    header: false,
	closable: true,
	columnLines: true,
	animCollapse: true,
	layout: 'border',
   	defaults: {  
    	split: true,    
        collapsible: true,
        collapseDirection: 'left'
    },

	initComponent : function(config) {
		Ext.apply(this, config);
		
		this.store = Ext.create('Ext.data.Store', {
        	pageSize: 50,
	        autoLoad: false,
			autoDestroy: true,
			sortOnLoad: true,
			remoteSort: true,
			model: 'ProductWarehouse',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/productWarehouse/getProductWarehouseListByTemplateId.json"/>',
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
        
	    this.rendererStore= Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: '1', name: '<fmt:message key="button.yes"/>'},
        		{id: '0', name: '<fmt:message key="button.no"/>'}
        	]
        });
	        
       	this.rendererProductTypeStore= Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: '1', name: '<fmt:message key="product.wareHouse.productType1"/>'},
        		{id: '2', name: '<fmt:message key="product.wareHouse.productType2"/>'},
        		{id: '3', name: '<fmt:message key="product.wareHouse.productType3"/>'},
        		{id: '4', name: '<fmt:message key="product.wareHouse.productType4"/>'},
        	]
        });
		
		this.rendererwarehouseTypeStore= Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: '1', name: '<fmt:message key="product.wareHouse.warehouseType1"/>'},
        		{id: '2', name: '<fmt:message key="product.wareHouse.warehouseType2"/>'},
        		
        	]
        });
		
		this.columns = [
			{text: '<fmt:message key="product.warehouse.warehouseId"/>', dataIndex: 'warehouseId', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="product.warehouse.name"/>', dataIndex: 'name', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="product.warehouse.tplArea"/>', dataIndex: 'tplArea', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="product.wareHouse.warehouseType"/>', dataIndex: 'warehouseType', width: 140, sortable : true,
        		renderer: this.rendererWarehouseTypeStuts,
        		filter: {
        			xtype: 'combobox',
        			displayField: 'name',
			    	valueField: 'id',
        			store: this.rendererwarehouseTypeStore,
        			queryMode: 'local',
			    	typeAhead: true 
			    }
        	},
        	{text: '<fmt:message key="product.wareHouse.productType"/>', dataIndex: 'productType', width: 140, sortable : true,
        		renderer: this.rendererProductTypeStuts,
        		filter: {
        			xtype: 'combobox',
        			displayField: 'name',
			    	valueField: 'id',
        			store: this.rendererProductTypeStore,
        			queryMode: 'local',
			    	typeAhead: true 
			    }
        	},  	
        	{text: '<fmt:message key="product.warehouse.isPushCustoms"/>', dataIndex: 'isPushCustoms', width: 140, sortable : true,
        		align: 'center',
        		renderer: this.rendererStuts,
        		filter: {
        			xtype: 'combobox',
        			displayField: 'name',
			    	valueField: 'id',
        			store: this.rendererStore,
        			queryMode: 'local',
			    	typeAhead: true 
			    }
        	},
        	{text: '<fmt:message key="product.warehouse.isDirectPushErp"/>', dataIndex: 'isDirectPushErp', width: 140, sortable : true,
        		align: 'center',
        		renderer: this.rendererStuts,
        		filter: {
        			xtype: 'combobox',
        			displayField: 'name',
			    	valueField: 'id',
        			store: this.rendererStore,
        			queryMode: 'local',
			    	typeAhead: true
			    }
        	},
        	{text: '<fmt:message key="product.warehouse.createTime"/>', dataIndex: 'createTime', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="product.warehouse.updateTime"/>', dataIndex: 'updateTime', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	}
        ];
        
        this.pagingToolbar = new Ext.PagingToolbar({
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
		this.productWarehouseListBbar = this.pagingToolbar; 
		
    	this.productWarehouseList = Ext.create('Ext.grid.GridPanel', {
			region: 'center',
			header: false,
			autoScroll: true,   
			closable: true,
			selType: 'checkboxmodel',
			multiSelect: true,
			columnLines: true,
			animCollapse: false,
		    enableLocking: true,
		    columns: this.columns,
		    store: this.store,
		    bbar: this.productWarehouseListBbar,
        	plugins: ['gridHeaderFilters'],
		    viewConfig: {
		        stripeRows: true,
		        enableTextSelection: true
		    }
	    });
	    
	    this.tbar = [
	    <jkd:haveAuthorize access="/productWarehouse/saveOrUpdateWarehouse.json">
	    {
        	text: '<fmt:message key="product.wareHouse.add"/>', 
            iconCls: 'add', 
        	handler: this.adds, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/productWarehouse/saveOrUpdateWarehouse.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        {
        	text: '<fmt:message key="product.wareHouse.edit"/>', 
            iconCls: 'Chartpieadd',
        	handler: this.edit, 
        	scope: this
        }
        </jkd:haveAuthorize>
        ];

    	this.items = [this.productWarehouseList];	
    	this.callParent(arguments);
    	
    	this.gsm = this.productWarehouseList.getSelectionModel();
    	<jkd:haveAuthorize access="/productWarehouse/getProductWarehouseListByTemplateId.json">
    	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.productWarehouseList.filters),
	        	templateId: this.record.data.templateId
			});
	    }, this);
	    </jkd:haveAuthorize>
    },
    
    cleanSearch : function(){
    	this.keywordField.setRawValue();
		this.store.loadPage(1);
    },
    
    rendererStuts : function(val){
		if(val == true) {
            return '<b><fmt:message key="button.yes"/></b>';
        }else{
            return '<fmt:message key="button.no"/>';
        }
	},
    
    approves : function(){
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.applyId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="user.applyAgent.approveConfirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/applyAgent/approve.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData),tag:1},
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
    
    adds : function(){
        var rowsData = [];
		var records = this.gsm.getSelection();
		var productWarehouseAddFormPanel = Ext.create('MyExt.productManager.ProductWarehouseAddFormPanel', {
			id: 'productWarehouseAddFormPanel@' + this.id,
    		viewer: this.viewer,	
    		edit: false,
   	 	});

    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
				var isCheckSucc = true;
				var name='';
				var productType='';
				var warehouseType='';

		    	productWarehouseAddFormPanel.items.each(function(form) {
	        		if(!form.isValid()){
	        			isCheckSucc = false;
	        		}else{ 
	        			name = Ext.getCmp('name').getValue();
	        			productType = Ext.getCmp('productType').value;
	        			warehouseType = Ext.getCmp('warehouseType').value;
	        		}
				}, this);
				
				if(!isCheckSucc){
					showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
					return;
				}	
				
				Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
					if(e == 'yes'){
				     	Ext.Ajax.request({
				        	url: '<c:url value="/productWarehouse/saveOrUpdateWarehouse.json"/>',
				         	method: 'post',
							scope: this,
							params:{name : name,productType : productType,warehouseType : warehouseType,status : 1,templateId: this.record.data.templateId},
				          	success: function(response){
		          				var responseObject = Ext.JSON.decode(response.responseText);
		          				if(responseObject.success == true){
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
			},
			scope: this
		},{
			text: '<fmt:message key="button.cancel"/>',
			handler : function(){popWin.close();},
			scope: this
		}];
      	openWin(Ext.String.format('<fmt:message key="product.wareHouse.add"/>'), productWarehouseAddFormPanel, buttons, 300, 180);
    },
    
    edit : function(){
        var rowsData = [];
		var records = this.gsm.getSelection();
		if(records.length == 0 ){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}
		if(records.length > 1 ){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="user.approve.records"/>');
			return;
		}
		
		var warehouseId = records[0].data.warehouseId;
		var sname = records[0].data.name;
		var swarehouseType = records[0].data.warehouseType;
		var sproductType = records[0].data.productType;
		var productWarehouseAddFormPanel = Ext.create('MyExt.productManager.ProductWarehouseAddFormPanel', {
			id: 'productWarehouseAddFormPanel@' + this.id,
    		viewer: this.viewer,
    		warehouseType:swarehouseType,
    		productType:sproductType,	
    		name:sname,
    		read: true,
   	 	});

    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
				var isCheckSucc = true;
				var name='';
	            var productType='';
	            var warehouseType='';
	            
		    	productWarehouseAddFormPanel.items.each(function(form) {
	        		if(!form.isValid()){
	        			isCheckSucc = false;
	        		}else{
	        			 name= Ext.getCmp('name').getValue();
	        			 productType= Ext.getCmp('productType').value;
	        			 warehouseType= Ext.getCmp('warehouseType').value;
	        		}
				}, this);
				
				if(!isCheckSucc){
					showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
					return;
				}	
				
				Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
					if(e == 'yes'){
				     	Ext.Ajax.request({
				        	url: '<c:url value="/productWarehouse/saveOrUpdateWarehouse.json"/>',
				         	method: 'post',
							scope: this,
							params:{warehouseId : warehouseId,name : name,productType : productType,warehouseType : warehouseType,status : 2,templateId: this.record.data.templateId},
				          	success: function(response){
		          				var responseObject = Ext.JSON.decode(response.responseText);
		          				if(responseObject.success == true){
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
			},
			scope: this
		},{
			text: '<fmt:message key="button.cancel"/>',
			handler : function(){popWin.close();},
			scope: this
		}];
      	openWin(Ext.String.format('<fmt:message key="product.wareHouse.edit"/>'), productWarehouseAddFormPanel, buttons, 300, 180);
    },
    
   	delete : function(){
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.warehouseId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="product.confirm.delete"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/productWarehouse/delete.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData)},
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
    
  	rendererProductTypeStuts : function(val){
		if(val == 1) {
            return '<b><fmt:message key="product.wareHouse.productType1"/></b>';
        }else if(val == 2){
            return '<fmt:message key="product.wareHouse.productType2"/>';
        }else if(val == 3){
            return '<fmt:message key="product.wareHouse.productType3"/>';
        }else {
           return '<fmt:message key="product.wareHouse.productType4"/>';
        }
	},
	
	rendererStuts : function(val){
		if(val == true) {
            return '<b><fmt:message key="button.yes"/></b>';
        }else{
            return '<b><fmt:message key="button.no"/></b>';
        }
	},
    
   	rendererWarehouseTypeStuts : function(val){
		if(val == 1) {
            return '<b><fmt:message key="product.wareHouse.warehouseType1"/></b>';
        }else{
            return '<fmt:message key="product.wareHouse.warehouseType2"/>';
        }
	},
	
	transferData : function( record){
    	this.record = record;
    	this.store.load();
    }
});