<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('Order', {
	extend: 'Ext.data.Model',
	idProperty: 'orderId',
    fields: [
    	{name: 'orderId',	 	mapping: 'orderId',		type: 'int'},
		{name: 'orderNumber',	mapping: 'orderNumber',	type: 'string'},
		{name: 'oldOrderNumber',mapping: 'oldOrderNumber',	type: 'string'},
		{name: 'shopName',	 	mapping: 'shopName',	type: 'string'},
		{name: 'appStoreId',	mapping: 'appStoreId',	type: 'int'},
		{name: 'orderDate',	 	mapping: 'orderDate',	type: 'string'},
		{name: 'buyerNick',	 	mapping: 'buyerNick',	type: 'string'},
		{name: 'totalAmount',	mapping: 'totalAmount',	type: 'string'},
		{name: 'postAmount',	mapping: 'postAmount',	type: 'string'},
		{name: 'discount',	 	mapping: 'discount',	type: 'string'},
		{name: 'paymentType',	mapping: 'paymentType',	type: 'string'},
		{name: 'consignee',	 	mapping: 'consignee',	type: 'string'},
		{name: 'idType',	 	mapping: 'idType',		type: 'string'},
		{name: 'idCode',	 	mapping: 'idCode',		type: 'string'},
		{name: 'payNo',	 		mapping: 'payNo',		type: 'string'},
		{name: 'payTime',	 	mapping: 'payTime',		type: 'string'},
		{name: 'payment',	 	mapping: 'payment',		type: 'string'},
		{name: 'province',	 	mapping: 'province',	type: 'string'},
		{name: 'city',	 		mapping: 'city',		type: 'string'},
		{name: 'cityarea',	 	mapping: 'cityarea',	type: 'string'},
		{name: 'address',	 	mapping: 'address',		type: 'string'},
		{name: 'mobilePhone',	mapping: 'mobilePhone',	type: 'string'},
		{name: 'sellerMemo',	mapping: 'sellerMemo',	type: 'string'},
		{name: 'buyerMessage',	mapping: 'buyerMessage',type: 'string'},
		{name: 'logisticCode',	mapping: 'logisticCode',type: 'string'},
		{name: 'status',	 	mapping: 'status',		type: 'string'},
		{name: 'expressNumber',	mapping: 'expressNumber',	type: 'string'},
		{name: 'logisticName',	mapping: 'logisticName',	type: 'string'},
		{name: 'createTime',	mapping: 'createTime',	type: 'string'},
		{name: 'updateTime',	mapping: 'updateTime',	type: 'string'}
    ]
});

Ext.define('MyExt.orderManager.OrderTabPanel', {
    extend : 'Ext.panel.Panel',
    requires : ['MyExt.orderManager.OrderFormPanel','MyExt.orderManager.OrderItemList','MyExt.orderManager.OrderHistoryList','MyExt.orderManager.OrderPackageList'],
    header: false,
	closable: true,
	columnLines: true,
	animCollapse: true,
	layout: 'border',
    viewConfig: {
        stripeRows: true,
        enableTextSelection: true
    },
    
	initComponent : function(config) {
		this.tabPanelMask = new Ext.LoadMask(this, {msg:"Please wait..."});
		Ext.apply(this, config);
		this.initWidth = this.width;
	    
	    this.tbar = Ext.create('Ext.Toolbar', { 
	   		scope: this,
	        items:[{
	        	text: '<fmt:message key="button.refresh"/>', 
	            iconCls: 'refresh', 	
	        	handler: function(){
	        		this.loadData();
	        	}, 
	        	scope: this
	        },'->',{
	        	iconCls: 'tab_open',
	        	handler: function(){
	        		this.orderList.hide();
	        		this.setWidth(this.clientWidth);
	        	}, 
	        	scope: this
	        },'-',{
	        	iconCls: 'tab_close',
	        	handler: function(){
	        		this.hide();
	        		this.orderList.show();
	        		this.setWidth(this.initWidth);
	        	}, 
	        	scope: this
	        }]
	    });
	    
	    this.tabPanel = Ext.create('Ext.TabPanel', { 
	    	activeTab : 0,
    		enableTabScroll : true,		
			layoutOnTabChange : true,
			tabWidth : 120,
			items:[{
				xtype: 'orderForm',
				title: '<fmt:message key="order.info"/>'
			},{
				xtype: 'orderItemList',
				title: '<fmt:message key="order.item.list"/>'
			},{
				xtype: 'orderHistoryList',
				title: '<fmt:message key="order.history"/>'
			}]
		});
		this.items = [this.tabPanel];
    	this.callParent(arguments);
    },
    
    transferData : function(tabPanel, record, clientWidth){
    	this.clientWidth = clientWidth;
    	this.tabPanel = tabPanel;
    	this.record = record;
    	this.loadData();
    },
    
    loadData : function(){
    	this.tabPanel.tabPanelMask.show();
    	this.tabPanel.down('orderForm').loadRecord(Ext.create('Order'));
    	this.tabPanel.down('orderItemList').store.removeAll();
    	this.tabPanel.down('orderHistoryList').store.removeAll();
    	this.tabPanel.down('orderHistoryList').record = this.record;
    	this.tabPanel.down('orderHistoryList').tabPanel = this;
    	
    	Ext.Ajax.request({
       		url: '<c:url value="/order/getOrderById.json"/>',
        	method: 'post',
			scope: this,
			params:{orderId: this.record.data.orderId},
         	success: function(response){
         		this.tabPanel.tabPanelMask.hide();
       			var responseObject = Ext.JSON.decode(response.responseText);
       			if (responseObject.success == true){
       				if(responseObject.order != null){
       					var recordData = Ext.create('Order', responseObject.order);
       					this.tabPanel.down('orderForm').loadRecord(recordData);
       				}
       				
       				if(responseObject.orderItem != null && responseObject.orderItem.length > 0){
       					for(var i = 0; i < responseObject.orderItem.length; i ++){
       						var orderItemData = Ext.create('OrderItem', responseObject.orderItem[i]);
       						this.tabPanel.down('orderItemList').store.insert(i, orderItemData);
       					}
       				}
       				
       				if(responseObject.orderHistoryList != null && responseObject.orderHistoryList.length > 0){
       					for(var i = 0; i < responseObject.orderHistoryList.length; i ++){
       						var orderHistoryData = Ext.create('OrderHistory', responseObject.orderHistoryList[i]);
       						this.tabPanel.down('orderHistoryList').store.insert(i, orderHistoryData);
       					}
       				}
       				
       				
       			}else{
       				//showFailMsg(responseObject.message, 4);
       			}
			}
    	})
    },
    
    cleanSearch : function(){
    	this.keywordField.setRawValue();
		this.store.loadPage(1);
    }
});