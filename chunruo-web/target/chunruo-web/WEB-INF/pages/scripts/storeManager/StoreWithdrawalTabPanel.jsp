<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('StoreWithdrawalForm', {
	extend: 'Ext.data.Model',
	idProperty: 'recordId',
    fields: [
    	{name: 'recordId',	 	mapping: 'recordId',		type: 'int'},
		{name: 'tradeNo',	 	mapping: 'tradeNo',			type: 'string'},
		{name: 'userId',	 	mapping: 'userId',			type: 'int'},
		{name: 'userName',	 	mapping: 'userName',		type: 'string'},
		{name: 'storeId',	 	mapping: 'storeId',			type: 'int'},
		{name: 'storeName',	 	mapping: 'storeName',		type: 'string'},
		{name: 'bankId',	 	mapping: 'bankId',			type: 'int'},
		{name: 'bankName',	 	mapping: 'bankName',		type: 'string'},
		{name: 'openingBank',	mapping: 'openingBank',		type: 'string'},
		{name: 'bankCard',	 	mapping: 'bankCard',		type: 'string'},
		{name: 'bankCardUser',	mapping: 'bankCardUser',	type: 'string'},
		{name: 'withdrawalType',mapping: 'withdrawalType',	type: 'int'},
		{name: 'status',	 	mapping: 'status',			type: 'int'},
		{name: 'amount',	 	mapping: 'amount',			type: 'string'},
		{name: 'personalTax',	mapping: 'personalTax',	    type: 'string'},
		{name: 'realAmount',	mapping: 'realAmount',	    type: 'string'},
		{name: 'salesRatio',	mapping: 'salesRatio',		type: 'string'},
		{name: 'remarks',	 	mapping: 'remarks',			type: 'string'},
		{name: 'complateTime',	mapping: 'complateTime',	type: 'string'},
		{name: 'createTime',	mapping: 'createTime',		type: 'string'},
		{name: 'updateTime',	mapping: 'updateTime',		type: 'string'}
    ]
});

Ext.define('MyExt.storeManager.StoreWithdrawalTabPanel', {
    extend : 'Ext.panel.Panel',
    requires : ['MyExt.storeManager.StoreWithdrawalFormPanel', 'MyExt.storeManager.StoreWholesaleHistoryList'],
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
	        		this.gridList.hide();
	        		this.setWidth(this.clientWidth);
	        	}, 
	        	scope: this
	        },'-',{
	        	iconCls: 'tab_close',
	        	handler: function(){
	        		this.hide();
	        		this.gridList.show();
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
				xtype: 'storeWithdrawalForm',
				title: '<fmt:message key="store.withdrawal.base"/>'
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
    	this.tabPanel.down('storeWithdrawalForm').loadRecord(Ext.create('StoreWithdrawalForm'));

    	Ext.Ajax.request({
       		url: '<c:url value="/storeWithdrawal/getStoreWithdrawalById.json"/>',
        	method: 'post',
			scope: this,
			params:{recordId: this.record.data.recordId},
         	success: function(response){
         		this.tabPanel.tabPanelMask.hide();
       			var responseObject = Ext.JSON.decode(response.responseText);
       			if (responseObject.success == true){
       				if(responseObject.userWithdrawal != null){
       					var recordData = Ext.create('StoreWithdrawalForm', responseObject.userWithdrawal);
       					this.tabPanel.down('storeWithdrawalForm').tabPanel = this.tabPanel;
       					this.tabPanel.down('storeWithdrawalForm').loadRecord(recordData);
       					this.tabPanel.down('storeWithdrawalForm').record = this.record;
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