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

Ext.define('MyExt.productTaskManager.ProductTaskTabPanel', {
    extend : 'Ext.panel.Panel',
    requires : ['MyExt.productTaskManager.ProductTaskFormPanel'],
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
    	this.tabPanel.down('productTaskForm').loadRecord(Ext.create('ProductTaskForm'));
                                                    
    	Ext.Ajax.request({
       		url: '<c:url value="/productTask/getProductTaskById.json"/>',
        	method: 'post',
			scope: this,
			params:{taskId: this.record.data.taskId},
         	success: function(response){
         		this.tabPanel.tabPanelMask.hide();
       			var responseObject = Ext.JSON.decode(response.responseText);
       			if (responseObject.success == true){
       				if(responseObject.data != null){
       					this.recordData = responseObject.data;
       					this.recordObject = Ext.create('ProductTaskForm', this.recordData);
       					this.tabPanel.down('productTaskForm').loadRecord(this.recordObject);
       				}
       				
       			   }else{
       				 //showFailMsg(responseObject.message, 4);
       			 }
			}
    	}, this);
    },
    
   
});
