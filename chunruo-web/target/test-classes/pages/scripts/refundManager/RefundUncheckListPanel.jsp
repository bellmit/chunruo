<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('RefundUncheck', {
	extend: 'Ext.data.Model',
	idProperty: 'refundId',
    fields: [
		{name: 'refundId',	 		mapping: 'refundId',		type: 'int'},
		{name: 'refundNumber',	 	mapping: 'refundNumber',	type: 'string'},
		{name: 'orderId',			mapping: 'orderId',			type: 'string'},
		{name: 'orderNo',			mapping: 'orderNo',			type: 'string'},
		{name: 'storeName',			mapping: 'storeName',		type: 'string'},
		{name: 'orderItemId',		mapping: 'orderItemId',		type: 'string'},
		{name: 'productId',	 		mapping: 'productId',		type: 'string'},
		{name: 'productPrice',	 	mapping: 'productPrice',	type: 'string'},
		{name: 'refundCount',	 	mapping: 'refundCount',		type: 'string'},
		{name: 'refundAmount',	 	mapping: 'refundAmount',	type: 'string'},
		{name: 'refundType',	 	mapping: 'refundType',		type: 'string'},
		{name: 'reasonId',			mapping: 'reasonId',		type: 'string'},
		{name: 'userId',	 		mapping: 'userId',			type: 'string'},
		{name: 'storeId',			mapping: 'storeId',			type: 'string'},
		{name: 'userMobile',		mapping: 'userMobile',		type: 'string'},
		{name: 'refundStatus',	 	mapping: 'refundStatus',	type: 'string'},
		{name: 'refundExplain',		mapping: 'refundExplain',	type: 'string'},
		{name: 'image1',			mapping: 'image1',			type: 'string'},
		{name: 'image2',			mapping: 'image2',			type: 'string'},
		{name: 'image3',			mapping: 'image3',			type: 'string'},
		{name: 'expressNumber',	 	mapping: 'expressNumber',	type: 'string'},
		{name: 'expressCompany',	mapping: 'expressCompany',	type: 'string'},
		{name: 'isReceive',	 		mapping: 'isReceive',		type: 'bool'},
		{name: 'refusalReason',		mapping: 'refusalReason',	type: 'string'},
		{name: 'createTime',		mapping: 'createTime',		type: 'string'},
		{name: 'updateTime',		mapping: 'updateTime',		type: 'string'},
		{name: 'expressImage1',	 	mapping: 'expressImage1',	type: 'string'},
		{name: 'expressExplain',	mapping: 'expressExplain',	type: 'string'},
		{name: 'expressImage2',		mapping: 'expressImage2',	type: 'string'},
		{name: 'expressImage3',		mapping: 'expressImage3',	type: 'string'},
		{name: 'remarkReasonId',    mapping: 'remarkReasonId',	type: 'int'},
		{name: 'completedTime',		mapping: 'completedTime',	type: 'string'}
	]
});

Ext.define('MyExt.refundManager.RefundUncheckListPanel', {
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
		
		var now = new Date();
   		var expiry = new Date(now.getTime() + 10 * 60 * 1000);
   		Ext.util.Cookies.set('isCheck','1',expiry);
   		
		this.store = Ext.create('Ext.data.Store', {
			id:'uncheckRefundStore',
        	pageSize: 50,
	        autoLoad: false,
			autoDestroy: true,
			sortOnLoad: true,
			remoteSort: true,
			model: 'RefundUncheck',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/refund/list.json?status=1"/>',
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
      	this.refundTypeStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: 1, name: '<fmt:message key="order.refund.refundType.1"/>'},
        		{id: 2, name: '<fmt:message key="order.refund.refundType.2"/>'},
        		{id: 3, name: '<fmt:message key="order.refund.refundType.3"/>'}
        	]
        });
       
      	this.refundStatusStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: 1, name: '<fmt:message key="order.refund.refundStatus_1"/>'},
        		{id: 2, name: '<fmt:message key="order.refund.refundStatus_2"/>'},
        		{id: 3, name: '<fmt:message key="order.refund.refundStatus_3"/>'},
        		{id: 4, name: '<fmt:message key="order.refund.refundStatus_4"/>'},
        		{id: 5, name: '<fmt:message key="order.refund.refundStatus_5"/>'},
        		{id: 6, name: '<fmt:message key="order.refund.refundStatus_6"/>'},
        	]
        });
        
		this.columns = [
			{text: '<fmt:message key="order.refund.refundId"/>', dataIndex: 'refundId', width: 70, locked: true, sortable : true,
        		align: 'center',filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.orderNo"/>', dataIndex: 'orderNo', width: 160,  sortable : true,locked: true,
        		align: 'center',filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.storeId"/>', dataIndex: 'storeId', width: 100,  sortable : true,locked: true,
        		align: 'center',filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.storeName"/>', dataIndex: 'storeName', width: 100,  sortable : true,locked: true,
        		align: 'center',filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.refund.refundNumber"/>', dataIndex: 'refundNumber', width: 160,  sortable : true,locked: true,
        		align: 'center',filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.refund.refundAmount"/>', dataIndex: 'refundAmount', width: 100, locked: true, sortable : true,
        		align: 'center',filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.refund.refundType"/>', dataIndex: 'refundType', width: 100, locked: true, sortable : true,
        		align: 'center',
				renderer: this.rendererRefundType,
				filter: {
					xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.refundTypeStore,
			        queryMode: 'local',
			        typeAhead: true
				}
        	},
        	{text: '<fmt:message key="order.refund.refundStatus"/>', dataIndex: 'refundStatus', width: 100,  sortable : true,
        		align: 'center',
        		renderer: this.rendererStatus,
        		filter: {
					xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.refundStatusStore,
			        queryMode: 'local',
			        typeAhead: true
				}
        	},
        	{text: '<fmt:message key="order.refund.isReceive"/>', dataIndex: 'isReceive', width: 100, sortable : true,
        		align: 'center',filter: {xtype: 'textfield'},renderer: this.rendererReceipt
        	},
        	{text: '<fmt:message key="order.refund.createdAt"/>', dataIndex: 'createTime', width: 160, sortable : true,
        		align: 'center',filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.refund.updatedAt"/>', dataIndex: 'updateTime', width: 160,  sortable : true,
        		align: 'center',filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.refund.completedAt"/>', dataIndex: 'completedTime', width: 160,  sortable : true,
        		align: 'center',filter: {xtype: 'textfield'}
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
		this.refundUncheckListBbar = this.pagingToolbar; 
		
    	this.refundUncheckList = Ext.create('Ext.grid.GridPanel', {
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
		    bbar: this.refundUncheckListBbar,
        	plugins: ['gridHeaderFilters'],
		    viewConfig: {
		        stripeRows: true,
		        enableTextSelection: true
		    }
	    });
        
        this.east =  Ext.create('MyExt.refundManager.RefundTabPanel', {
        	refundList: this.refundUncheckList,
		 	viewer: this.viewer,
		 	layout: 'card',
	        border: false,
	       	region: 'east',
	        width: 550,
	        split: true,
	        header: false,
	        hidden: true
        });
	    
    	this.items = [this.refundUncheckList, this.east];	
		this.east.hide();
    	this.callParent(arguments);
    	
    	<jkd:haveAuthorize access="/refund/list.json">
    	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.refundUncheckList.filters)
			});
	    }, this);
	    this.store.load();
	    </jkd:haveAuthorize>
	    
	    this.gsm = this.refundUncheckList.getSelectionModel();
	    this.refundUncheckList.on('itemdblclick', function(view, record, item, index, e, eOpts) {
	    	<jkd:haveAuthorize access="/refund/getRefundById.json">
	    	this.east.transferData(this.east, record, this.body.dom.clientWidth);
	    	this.east.show();
	    	</jkd:haveAuthorize>
	    }, this);
    },
    
	rendererStatus : function(val){
		var str =  "";
		if(val == 1){
			str = '<b><fmt:message key="order.refund.refundStatus_1"/></b>';
		}else if(val == 2 ){
			str = '<b><fmt:message key="order.refund.refundStatus_2"/></b>';
		}else if(val == 3){
			str = '<b><fmt:message key="order.refund.refundStatus_3"/></b>';
		}else if(val == 4){
			str = '<b><fmt:message key="order.refund.refundStatus_4"/></b>';
		}else if(val == 5){
			str = '<b><fmt:message key="order.refund.refundStatus_5"/></b>';
		}else if(val == 6){
			str = '<b><fmt:message key="order.refund.refundStatus_6"/></b>';
		}
		return str;
	},
	
	rendererReceipt : function(val){
		var str =  "";
		if(val){
			str = '<b><fmt:message key="order.refund.receipt"/></b>';
		}
		return str;
	},
	
	rendererRefundType : function(val){
		var str =  "";
		if(val == '1'){
			str = '<b><fmt:message key="order.refund.refundType.1"/></b>';
		}else if(val == '2'){
			str = '<b><fmt:message key="order.refund.refundType.2"/></b>';
		}else{
		    str = '<b><fmt:message key="order.refund.refundType.3"/></b>';
		}
		return str;
	}
});