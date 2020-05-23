<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('ProductTaskForm', {
	extend: 'Ext.data.Model',
	idProperty: 'taskId',
    fields: [
		{name: 'taskId',		    mapping: 'taskId',		              type: 'int'},
		{name: 'taskName',		    mapping: 'taskName',		          type: 'string'},
		{name: 'productId',	        mapping: 'productId',		          type: 'int'},
		{name: 'productName',	    mapping: 'productName',               type: 'string'},
		{name: 'taskNumber',	 	mapping: 'taskNumber',                type: 'int'},
		{name: 'reward',	        mapping: 'reward',                    type: 'string'},
		{name: 'maxGroupNumber',	mapping: 'maxGroupNumber',            type: 'int'},
		{name: 'beginTime',	     	mapping: 'beginTime',                 type: 'string'},
		{name: 'endTime',	        mapping: 'endTime',                   type: 'string'},
		{name: 'isEnable',	        mapping: 'isEnable',                  type: 'bool'},
		{name: 'createTime',	 	mapping: 'createTime',		          type: 'string'},
		{name: 'updateTime',	 	mapping: 'updateTime',		          type: 'string'}
    ]
});

Ext.define('MyExt.productTaskManager.UserProductTaskTabPanel', {
    extend : 'Ext.panel.Panel',
    requires : ['MyExt.productTaskManager.ProductTaskFormPanel','MyExt.productTaskManager.UserProductTaskItemList'],
    header: false,
	closable: true,
	columnLines: true,
	animCollapse: true,
	layout: 'bproduct',
	recordData: null,
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
	            scope: this,
	        	handler: function(){
	        		this.loadData();
	        	}
	        },'->',{
	        	iconCls: 'tab_open',
	        	handler: function(){
	        		this.productList.hide();
	        		this.setWidth(this.clientWidth);
	        	}, 
	        	scope: this
	        },'-',{
	        	iconCls: 'tab_close',
	        	handler: function(){
	        		this.hide();
	        		this.productList.show();
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
				xtype: 'productTaskForm',
				isEditor: true,
				title: '<fmt:message key="product.task.detail"/>'
			},{
				xtype: 'userProductItemList',
				title: '<fmt:message key="user.product.item.list"/>'
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
    	this.tabPanel.down('productTaskForm').tabPanel = this;
    	this.tabPanel.down('productTaskForm').loadRecord(Ext.create('ProductTaskForm'));
    	this.tabPanel.down('userProductItemList').store.removeAll();
                                                    
    	Ext.Ajax.request({
       		url: '<c:url value="/productTask/getUserProductTaskById.json"/>',
        	method: 'post',
			scope: this,
			params:{recordId: this.record.data.recordId},
         	success: function(response){
         		this.tabPanel.tabPanelMask.hide();
       			var responseObject = Ext.JSON.decode(response.responseText);
       			if (responseObject.success == true){
       				if(responseObject.data != null){
       					this.recordData = responseObject.data;
       					this.recordObject = Ext.create('ProductTaskForm', responseObject.productTask);
       					this.tabPanel.down('productTaskForm').loadRecord(this.recordObject);
       					
       					if(responseObject.item != null && responseObject.item.length > 0){
       					for(var i = 0; i < responseObject.item.length; i ++){
       						var itemData = Ext.create('UserProductTaskItem', responseObject.item[i]);
       						this.tabPanel.down('userProductItemList').store.insert(i, itemData);
       					}
       				}
       				}
       				
       			   }else{
       				 //showFailMsg(responseObject.message, 4);
       			 }
			}
    	}, this);
    },
    
   
});
