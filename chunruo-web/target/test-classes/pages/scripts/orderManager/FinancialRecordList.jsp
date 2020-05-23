<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('FinancialRecord', {
	extend: 'Ext.data.Model',
	idProperty: 'storeId',
    fields: [
    	{name: 'storeId',	 mapping: 'storeId',	type: 'int'},
		{name: 'userId',	 mapping: 'userId',	type: 'int'},
		{name: 'name',	 mapping: 'name',	type: 'string'},
		{name: 'editNameCount',	 mapping: 'editNameCount',	type: 'string'},
		{name: 'logo',	 mapping: 'logo',	type: 'string'},
		{name: 'qcode',	 mapping: 'qcode',	type: 'string'},
		{name: 'saleCategoryFid',	 mapping: 'saleCategoryFid',	type: 'string'},
		{name: 'saleCategoryId',	 mapping: 'saleCategoryId',	type: 'string'},
		{name: 'linkman',	 mapping: 'linkman',	type: 'string'},
		{name: 'mobile',	 mapping: 'mobile',	type: 'string'},
		{name: 'introduce',	 mapping: 'introduce',	type: 'string'},
		{name: 'isApprove',	 mapping: 'isApprove',	type: 'string'},
		{name: 'status',	 mapping: 'status',	type: 'string'},
		{name: 'publicDisplay',	 mapping: 'publicDisplay',	type: 'string'},
		{name: 'serviceTel',	 mapping: 'serviceTel',	type: 'string'},
		{name: 'serviceQq',	 mapping: 'serviceQq',	type: 'string'},
		{name: 'serviceWeixin',	 mapping: 'serviceWeixin',	type: 'string'},
		{name: 'openNav',	 mapping: 'openNav',	type: 'string'},
		{name: 'navStyleId',	 mapping: 'navStyleId',	type: 'string'},
		{name: 'useNavPages',	 mapping: 'useNavPages',	type: 'string'},
		{name: 'sales',	 mapping: 'sales',	type: 'string'},
		{name: 'income',	 mapping: 'income',	type: 'string'},
		{name: 'balance',	 mapping: 'balance',	type: 'string'},
		{name: 'unbalance',	 mapping: 'unbalance',	type: 'string'},
		{name: 'orders',	 mapping: 'orders',	type: 'string'},
		{name: 'storePayIncome',	 mapping: 'storePayIncome',	type: 'string'},
		{name: 'withdrawalAmount',	 mapping: 'withdrawalAmount',	type: 'string'},
		{name: 'withdrawalType',	 mapping: 'withdrawalType',	type: 'string'},
		{name: 'bankId',	 mapping: 'bankId',	type: 'string'},
		{name: 'bankCard',	 mapping: 'bankCard',	type: 'string'},
		{name: 'bankCardUser',	 mapping: 'bankCardUser',	type: 'string'},
		{name: 'openingBank',	 mapping: 'openingBank',	type: 'string'},
		{name: 'collect',	 mapping: 'collect',	type: 'string'},
		{name: 'isOfficialShop',	 mapping: 'isOfficialShop',	type: 'string'},
		{name: 'isWholesale',	 mapping: 'isWholesale',	type: 'string'},
		{name: 'inviterCode',	 mapping: 'inviterCode',	type: 'string'},
		{name: 'editWholesale',	 mapping: 'editWholesale',	type: 'string'},
		{name: 'storePageId',	 mapping: 'storePageId',	type: 'string'},
		{name: 'createTime',	 mapping: 'createTime',	type: 'string'},
		{name: 'updateTime',	 mapping: 'updateTime',	type: 'string'}
    ]
});

Ext.define('MyExt.orderManager.FinancialRecordList', {
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
			model: 'FinancialRecord',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/store/list.json?statusId=6"/>',
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
        		{id: '0', name: '<fmt:message key="order.status.0"/>'},
        		{id: '1', name: '<fmt:message key="order.status.1"/>'},
        		{id: '2', name: '<fmt:message key="order.status.2"/>'},
        		{id: '3', name: '<fmt:message key="order.status.3"/>'}
        	]
        });
		
		this.columns = [
        	{text: '<fmt:message key="store.storeId"/>', dataIndex: 'storeId', width: 70, locked: true, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="store.name"/>', dataIndex: 'name', width: 140, locked: true, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="store.userId"/>', dataIndex: 'userId', width: 140, locked: true, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="store.editNameCount"/>', dataIndex: 'editNameCount', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="store.logo"/>', dataIndex: 'logo', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="store.qcode"/>', dataIndex: 'qcode', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="store.saleCategoryFid"/>', dataIndex: 'saleCategoryFid', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="store.saleCategoryId"/>', dataIndex: 'saleCategoryId', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="store.linkman"/>', dataIndex: 'linkman', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="store.mobile"/>', dataIndex: 'mobile', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="store.introduce"/>', dataIndex: 'introduce', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="store.isApprove"/>', dataIndex: 'isApprove', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="store.status"/>', dataIndex: 'status', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="store.publicDisplay"/>', dataIndex: 'publicDisplay', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="store.serviceTel"/>', dataIndex: 'serviceTel', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="store.serviceQq"/>', dataIndex: 'serviceQq', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="store.serviceWeixin"/>', dataIndex: 'serviceWeixin', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="store.openNav"/>', dataIndex: 'openNav', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="store.navStyleId"/>', dataIndex: 'navStyleId', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="store.useNavPages"/>', dataIndex: 'useNavPages', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="store.sales"/>', dataIndex: 'sales', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="store.income"/>', dataIndex: 'income', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="store.balance"/>', dataIndex: 'balance', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="store.unbalance"/>', dataIndex: 'unbalance', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="store.orders"/>', dataIndex: 'orders', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="store.storePayIncome"/>', dataIndex: 'storePayIncome', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="store.withdrawalAmount"/>', dataIndex: 'withdrawalAmount', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="store.withdrawalType"/>', dataIndex: 'withdrawalType', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="store.bankId"/>', dataIndex: 'bankId', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="store.bankCard"/>', dataIndex: 'bankCard', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="store.bankCardUser"/>', dataIndex: 'bankCardUser', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="store.openingBank"/>', dataIndex: 'openingBank', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="store.collect"/>', dataIndex: 'collect', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="store.isOfficialShop"/>', dataIndex: 'isOfficialShop', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="store.isWholesale"/>', dataIndex: 'isWholesale', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="store.inviterCode"/>', dataIndex: 'inviterCode', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="store.editWholesale"/>', dataIndex: 'editWholesale', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="store.storePageId"/>', dataIndex: 'storePageId', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="store.createTime"/>', dataIndex: 'createTime', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="store.updateTime"/>', dataIndex: 'updateTime', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	}
        ];
        
        this.keywordField = new Ext.create('Ext.form.TextField', {
			width: 200,
			emptyText:'<fmt:message key="app.user.search" />',
        	scope: this
        });
        
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
	    	id: 'orderList@OrderPanel' + this.id,
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
	    
	    this.tbar = [{
        	text: '<fmt:message key="order.cancel"/>', 
        	iconCls: 'delete', 	
        	handler: this.cancelOrder, 
        	scope: this
        },'->',{
        	text: '<fmt:message key="button.exporter.xls"/>', 
        	iconCls: 'excel', 	
        	handler: function(){
        		this.orderList.saveDocumentAs({
                	type: 'excel',
                    title: 'meta.fiddleHeader',
                    fileName: 'excel.xls'
                });
        	}, 
        	scope: this
        }];
        
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
    	
    	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.orderList.filters),
				keyword: this.keywordField.getRawValue()
			});
	    }, this);
	    this.store.load();
	    
	    this.gsm = this.orderList.getSelectionModel();
	    this.orderList.on('itemdblclick', function(view, record, item, index, e, eOpts) {
	    	this.east.transferData(this.east, record, this.body.dom.clientWidth);
	    	this.east.show();
	    }, this);
	    
	    this.orderList.on('headerfilterchange', function(e) {
	        alert('sfs');
		});
    },
    
    cleanSearch : function(){
    	this.keywordField.setRawValue();
		this.store.loadPage(1);
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
    }
});