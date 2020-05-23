<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('RefundForm', {
	extend: 'Ext.data.Model',
	idProperty: 'refundId',
    fields: [
		{name: 'refundId',	 		mapping: 'refundId',		type: 'int'},
		{name: 'refundNumber',	 	mapping: 'refundNumber',	type: 'string'},
		{name: 'orderId',			mapping: 'orderId',			type: 'string'},
		{name: 'orderNo',			mapping: 'orderNo',			type: 'string'},
		{name: 'orderItemId',		mapping: 'orderItemId',		type: 'string'},
		{name: 'parentOrderId',	 	mapping: 'parentOrderId',	type: 'string'},
		{name: 'productId',	 		mapping: 'productId',		type: 'string'},
		{name: 'productPrice',	 	mapping: 'productPrice',	type: 'string'},
		{name: 'refundCount',	 	mapping: 'refundCount',		type: 'string'},
		{name: 'refundAmount',	 	mapping: 'refundAmount',	type: 'string'},
		{name: 'refundType',	 	mapping: 'refundType',		type: 'string'},
		{name: 'paymentType',	 	mapping: 'paymentType',		type: 'string'},
		{name: 'reasonId',			mapping: 'reasonId',		type: 'string'},
		{name: 'userId',	 		mapping: 'userId',			type: 'string'},
		{name: 'storeId',			mapping: 'storeId',			type: 'string'},
		{name: 'topUserId',	 	    mapping: 'topUserId',		type: 'string'},
		{name: 'refundStatus',	 	mapping: 'refundStatus',	type: 'string'},
		{name: 'refundExplain',		mapping: 'refundExplain',	type: 'string'},
		{name: 'image1',			mapping: 'image1',	type: 'string'},
		{name: 'image2',			mapping: 'image2',	type: 'string'},
		{name: 'image3',			mapping: 'image3',	type: 'string'},
		{name: 'expressNumber',	 	mapping: 'expressNumber',	type: 'string'},
		{name: 'expressCompany',	mapping: 'expressCompany',	type: 'string'},
		{name: 'isReceive',	 		mapping: 'isReceive',		type: 'string'},
		{name: 'refusalReason',		mapping: 'refusalReason',	type: 'string'},
		{name: 'createdAt',			mapping: 'createdAt',		type: 'string'},
		{name: 'updatedAt',			mapping: 'updatedAt',		type: 'string'},
		{name: 'completedAt',		mapping: 'completedAt',		type: 'string'},
		{name: 'userName',			mapping: 'userName',		type: 'string'},
		{name: 'userMobile',		mapping: 'userMobile',		type: 'string'},
		{name: 'storeName',			mapping: 'storeName',		type: 'string'},
		{name: 'reason',			mapping: 'reason',			type: 'string'},
		{name: 'expressImage1',	 	mapping: 'expressImage1',	type: 'string'},
		{name: 'expressExplain',	mapping: 'expressExplain',	type: 'string'},
		{name: 'expressImage2',		mapping: 'expressImage2',	type: 'string'},
		{name: 'expressImage3',		mapping: 'expressImage3',	type: 'string'},
		{name: 'productName',		mapping: 'productName',		type: 'string'},
		{name: 'remarkReason',		mapping: 'remarkReason',	type: 'string'}
		
	]
});

Ext.define('MyExt.refundManager.RefundTabPanel', {
    extend : 'Ext.panel.Panel',
    requires : ['MyExt.refundManager.RefundFormPanel','MyExt.orderManager.OrderItemList','MyExt.refundManager.RefundHistoryListPanel'],
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
	        items:[
	        <jkd:haveAuthorize access="/refund/getRefundById.json">
	        {
	        	text: '<fmt:message key="button.refresh"/>', 
	            iconCls: 'refresh', 	
	        	handler: function(){
	        		this.loadData();
	        	}, 
	        	scope: this
	        },
	       	</jkd:haveAuthorize>
	        '->',{
	        	iconCls: 'tab_open',
	        	handler: function(){
	        		this.refundList.hide();
	        		this.setWidth(this.clientWidth);
	        	}, 
	        	scope: this
	        },'-',{
	        	iconCls: 'tab_close',
	        	handler: function(){
	        		this.hide();
	        		this.refundList.show();
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
				xtype: 'refundForm',
				title: '<fmt:message key="order.refund.info"/>'
			},{
				xtype: 'orderItemList',
				title: '<fmt:message key="order.item.list"/>'
			},{
				xtype: 'refundHistoryList',
				title: '<fmt:message key="refund.history"/>'
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
    	this.tabPanel.down('refundForm').loadRecord(Ext.create('RefundForm'));
    	this.tabPanel.down('orderItemList').store.removeAll();
    	this.tabPanel.down('refundHistoryList').store.removeAll();
    	Ext.ComponentQuery.query('textarea[name="remarks"]')[0].setReadOnly(true);
    	Ext.Ajax.request({
       		url: '<c:url value="/refund/getRefundById.json"/>',
        	method: 'post',
			scope: this,
			params:{refundId: this.record.data.refundId},
         	success: function(response){
         	var baseUrl = "http://admin.chunruo.net/";
         		this.tabPanel.tabPanelMask.hide();
       			var responseObject = Ext.JSON.decode(response.responseText);
       			if (responseObject.success == true){
       				if(responseObject.refund != null){
       					var recordData = Ext.create('RefundForm', responseObject.refund);
       					this.tabPanel.down('refundForm').loadRecord(recordData);
       					
       					
	       				var reasonCombobox = this.tabPanel.down('refundForm').down('combobox[name=remarkReasonId]');
       					try{
       					reasonCombobox.setValue(responseObject.refund.remarkReasonId);
       					}catch(e){
    					}
       				
       					//
       					var image1List = document.getElementsByName('image1')
       					if(image1List.length > 0){
	       					for(var i = 0; i < image1List.length; i++){			
								image1List[i].src = responseObject.refund.image1;	
							}
						}
						var image2List = document.getElementsByName('image2')
						if(image2List.length > 0){
	       					for(var i = 0; i < image2List.length; i++){			
								image2List[i].src = responseObject.refund.image2;	
							}
						}
						var image3List = document.getElementsByName('image3')
						if(image3List.length > 0){
	       					for(var i = 0; i < image3List.length; i++){			
								image3List[i].src = responseObject.refund.image3;	
							}
						}
						var expressImage1List = document.getElementsByName('expressImage1')
						if(expressImage1List.length > 0){
	       					for(var i = 0; i < expressImage1List.length; i++){			
								expressImage1List[i].src = responseObject.refund.expressImage1;	
							}
						}
						var expressImage2List = document.getElementsByName('expressImage2')
						if(expressImage2List.length > 0){
	       					for(var i = 0; i < expressImage2List.length; i++){			
								expressImage2List[i].src = responseObject.refund.expressImage2;	
							}
						}
						var expressImage3List = document.getElementsByName('expressImage3')
						if(expressImage3List.length > 0){
	       					for(var i = 0; i < expressImage3List.length; i++){			
								expressImage3List[i].src = responseObject.refund.expressImage3;	
							}
						}

       					}
       				if(responseObject.orderItem != null && responseObject.orderItem.length > 0){
       					for(var i = 0; i < responseObject.orderItem.length; i ++){
       						var orderItemData = Ext.create('OrderItem', responseObject.orderItem[i]);
       						this.tabPanel.down('orderItemList').store.insert(i, orderItemData);
       					}
       				}
       				
       				if(responseObject.history != null && responseObject.history.length > 0){
       					for(var i = 0; i < responseObject.history.length; i ++){
       						var historyData = Ext.create('RefundHistory', responseObject.history[i]);
       						this.tabPanel.down('refundHistoryList').store.insert(i, historyData);
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