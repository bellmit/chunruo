<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('UserProfitRecord', {
	extend: 'Ext.data.Model',
	idProperty: 'recordId',
    fields: [
   		{name: 'recordId',	 	mapping: 'recordId',		type: 'int'},
		{name: 'userId',	 	mapping: 'userId',			type: 'int'},
		{name: 'storeName',	 	mapping: 'storeName',		type: 'string'},
		{name: 'fromUserId',	mapping: 'fromUserId',		type: 'int'},
		{name: 'fromStoreName',	mapping: 'fromStoreName',	type: 'string'},
		{name: 'orderId',	 	mapping: 'orderId',			type: 'int'},
		{name: 'orderNo',	 	mapping: 'orderNo',			type: 'string'},
		{name: 'orderAmount',	mapping: 'orderAmount',		type: 'string'},
		{name: 'income',	 	mapping: 'income',			type: 'string'},
		{name: 'type',	 		mapping: 'type',			type: 'int'},
		{name: 'status',	 	mapping: 'status',			type: 'int'},
		{name: 'passTime',	 	mapping: 'passTime',		type: 'string'},
		{name: 'createTime',	mapping: 'createTime',		type: 'string'},
		{name: 'updateTime',	mapping: 'updateTime',		type: 'string'}
	]
});

Ext.define('MyExt.orderManager.OrderProfitRecordPanel', {
    extend : 'Ext.panel.Panel',
    requires : ['Ext.ux.grid.GridHeaderFilters','Ext.ux.grid.Exporter'],
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
			model: 'UserProfitRecord',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/userProfitRecord/list.json"/>',
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
        
		this.typeStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: 5, name: '<fmt:message key="store.profit.record.type.5"/>'},
        		{id: 6, name: '<fmt:message key="store.profit.record.type.6"/>'}
        	]
        });
        
        this.statusStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: 1, name: '<fmt:message key="store.profit.record.status.1"/>'},
        		{id: 2, name: '<fmt:message key="store.profit.record.status.2"/>'},
        		{id: 3, name: '<fmt:message key="store.profit.record.status.3"/>'},
        		{id: 4, name: '<fmt:message key="store.profit.record.status.4"/>'}
        	]
        });
		
		this.columns = [
			{text: '<fmt:message key="store.profit.record.recordId"/>', dataIndex: 'recordId', width: 80, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.profit.record.userId"/>', dataIndex: 'userId', width: 70, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.profit.record.storeName"/>', dataIndex: 'storeName', width: 200, sortable : false,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.profit.record.orderId"/>', dataIndex: 'orderId', width: 80, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.profit.record.orderNo"/>', dataIndex: 'orderNo', width: 150, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.profit.record.orderAmount"/>', dataIndex: 'orderAmount', width: 100, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.profit.record.income"/>', dataIndex: 'income', width: 100, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.profit.record.status"/>', dataIndex: 'status', width: 80, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.rendererStatus,
        		filter: {
					xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.statusStore,
			        queryMode: 'local',
			        typeAhead: true
				}
        	},
        	{text: '<fmt:message key="store.profit.record.type"/>', dataIndex: 'type', width: 80, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.rendererTypeStuts,
        		filter: {
					xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.typeStore,
			        queryMode: 'local',
			        typeAhead: true
				}
        	},
        	{text: '<fmt:message key="store.profit.record.fromUserId"/>', dataIndex: 'fromUserId', width: 80, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.profit.record.fromStoreName"/>', dataIndex: 'fromStoreName', width: 200, sortable : false,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.profit.record.passTime"/>', dataIndex: 'passTime', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.profit.record.createTime"/>', dataIndex: 'createTime', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.profit.record.updateTime"/>', dataIndex: 'updateTime', width: 140, sortable : true,
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
		this.orderListBbar = this.pagingToolbar; 
		
    	this.orderList = Ext.create('Ext.grid.GridPanel', {
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
		    bbar: this.orderListBbar,
        	plugins: ['gridHeaderFilters','gridexporter'],
		    viewConfig: {
		        stripeRows: true,
		        enableTextSelection: true
		    }
	    });
        
        this.east =  Ext.create('MyExt.orderManager.OrderTabPanel', {
        	orderList: this.orderList,
		 	viewer: this.viewer,
		 	layout: 'card',
	        border: false,
	       	region: 'east',
	        width: 550,
	        split: true,
	        header: false,
	        hidden: true
        });
	    
    	this.items = [this.orderList, this.east];	
		this.east.hide();
    	this.callParent(arguments);
    	
    	<jkd:haveAuthorize access="/userProfitRecord/list.json">
    	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.orderList.filters)
			});
	    }, this);
	    this.store.load();
	    </jkd:haveAuthorize>
	    
	    this.gsm = this.orderList.getSelectionModel();
	    this.orderList.on('itemdblclick', function(view, record, item, index, e, eOpts) {
	    	<jkd:haveAuthorize access="/order/getOrderById.json">
	    	this.east.transferData(this.east, record, this.body.dom.clientWidth);
	    	this.east.show();
	    	</jkd:haveAuthorize>
	    }, this);
    },
    
    cleanSearch : function(){
    	this.keywordField.setRawValue();
		this.store.loadPage(1);
    },
    
    dealWithInterceptOrder : function(){
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.orderId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="order.dealWithIntercept.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/order/dealWithIntercept.json"/>',
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
	
	rendererTypeStuts : function(val){
		var str =  "";
		if(val == 1){
			str = '<fmt:message key="store.profit.record.type.1"/>';
		}else if(val==3){
			str = '<fmt:message key="store.profit.record.type.3"/>';
		}else if(val==4){
			str = '<fmt:message key="store.profit.record.type.4"/>';
		}else if(val==5){
			str = '<fmt:message key="store.profit.record.type.5"/>';
		}else if(val==6){
			str = '<fmt:message key="store.profit.record.type.6"/>';
		}
		return str;
	},
	
	rendererStatus : function(val){
		var str =  "";
		if(val == 1){
			str = '<fmt:message key="store.profit.record.status.1"/>';
		}else if(val == 2 ){
			str = '<fmt:message key="store.profit.record.status.2"/>';
		}else if(val == 3){
			str = '<fmt:message key="store.profit.record.status.3"/>';
		}else if(val == 4){
			str = '<fmt:message key="store.profit.record.status.4"/>';
		}
		return str;
	}
});