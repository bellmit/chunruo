<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('OrderSyncStatusRecord', {
	extend: 'Ext.data.Model',
	idProperty: 'recordId',
    fields: [
    	{name: 'recordId',	 	mapping: 'recordId',	type: 'string'},
    	{name: 'storeName',	 	mapping: 'storeName',	type: 'string'},
		{name: 'orderNumber',	mapping: 'orderNumber',	type: 'string'},
		{name: 'orderStatus',	mapping: 'orderStatus',	type: 'int'},
		{name: 'syncNumber',	mapping: 'syncNumber',	type: 'int'},
		{name: 'batchNumber',	mapping: 'batchNumber',	type: 'string'},
		{name: 'isSyncSucc',	mapping: 'isSyncSucc',	type: 'bool'},
		{name: 'expressNumber',	 mapping: 'expressNumber',	type: 'string'},
		{name: 'logisticCode',	mapping: 'logisticCode',	type: 'string'},
		{name: 'logisticName',	mapping: 'logisticName',	type: 'string'},
		{name: 'errorMsg',	 	mapping: 'errorMsg',	type: 'string'},
		{name: 'createTime',	mapping: 'createTime',	type: 'string'},
		{name: 'updateTime',	mapping: 'updateTime',	type: 'string'}
    ]
});

Ext.define('MyExt.orderManager.OrderSyncStatusRecordPanel', {
    extend : 'Ext.grid.GridPanel',
    requires : ['Ext.ux.grid.GridHeaderFilters','Ext.ux.grid.Exporter'],
	region: 'center',
	autoScroll: true,   
	closable: true,
	selType: 'checkboxmodel',
	multiSelect: true,
	columnLines: true,
	animCollapse: false,
    enableLocking: true,
    plugins: ['gridHeaderFilters','gridexporter'],
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
			model: 'OrderSyncStatusRecord',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/orderSync/syncStatusList.json"/>',
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
        
        this.rendererOrderStatusStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: 1, name: '<fmt:message key="order.sync.orderStatus.1"/>'},
        		{id: 2, name: '<fmt:message key="order.sync.orderStatus.2"/>'}
        	]
        });
		
		this.columns = [
	    	{text: '<fmt:message key="order.sync.recordId"/>', dataIndex: 'recordId', width: 80, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="order.sync.storeName"/>', dataIndex: 'storeName', width: 180, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="order.sync.orderNumber"/>', dataIndex: 'orderNumber', width: 160, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="order.sync.orderStatus"/>', dataIndex: 'orderStatus', width: 70, sortable : true,
        		renderer: this.rendererOrderStatus,
        		filter: {
					xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.rendererOrderStatusStore,
			        queryMode: 'local',
			        typeAhead: true
				}
        	},
        	{text: '<fmt:message key="order.sync.syncNumber"/>', dataIndex: 'syncNumber', width: 65, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="order.sync.isSyncSucc"/>', dataIndex: 'isSyncSucc', width: 90, sortable : true,
        		filter: {xtype: 'textfield'},
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
        	{text: '<fmt:message key="order.sync.expressNumber"/>', dataIndex: 'expressNumber', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="order.sync.logisticCode"/>', dataIndex: 'logisticCode', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="order.sync.logisticName"/>', dataIndex: 'logisticName', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="order.sync.errorMsg"/>', dataIndex: 'errorMsg', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="order.sync.createTime"/>', dataIndex: 'createTime', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="order.sync.updateTime"/>', dataIndex: 'updateTime', width: 140, sortable : true,
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
        this.bbar = this.pagingToolbar;      
    	this.callParent();
  
  		this.gsm = this.getSelectionModel();
  		<jkd:haveAuthorize access="/orderSync/syncStatusList.json">
  		this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.filters)
			});
	    }, this);
    	this.store.load();
    	</jkd:haveAuthorize>
    },
    
    cancelOrder : function(){
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.orderId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="order.cancel.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/order/cancelOrder.json"/>',
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
    
    rendererStuts : function(val){
		if(val == true) {
            return '<b><fmt:message key="button.yes"/></b>';
        }else{
            return '<fmt:message key="button.no"/>';
        }
	},
	
	rendererOrderStatus : function(val){
		if(val == 1) {
            return '<span style="color:green;"><fmt:message key="order.sync.orderStatus.1"/></span>';
        }else if(val == 2){
            return '<span style="color:red;"><fmt:message key="order.sync.orderStatus.2"/></span>';
        }
        return val;
	}
});