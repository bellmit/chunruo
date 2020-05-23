<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('EvaluateForm', {
	extend: 'Ext.data.Model',
	idProperty: 'evaluateId',
    fields: [
    	{name: 'evaluateId',		mapping: 'evaluateId',		       type: 'int'},
		{name: 'userId',		    mapping: 'userId',		           type: 'int'},
		{name: 'orderId',	        mapping: 'orderId',                type: 'int'},
		{name: 'itemId',	        mapping: 'itemId',		           type: 'int'},
		{name: 'productId',	 	    mapping: 'productId',              type: 'int'},
		{name: 'status',	 	    mapping: 'status',                 type: 'int'},
		{name: 'score',	 	        mapping: 'score',                  type: 'int'},
		{name: 'content',	     	mapping: 'content',                type: 'string'},
		{name: 'productName',	    mapping: 'orderItems.productName', type: 'string'},
		{name: 'productImagePath',	mapping: 'orderItems.productImagePath', type: 'string'},
		{name: 'imagePath',	 	    mapping: 'imagePath',              type: 'string'},
		{name: 'evaluateRate',	 	mapping: 'evaluateRate',           type: 'string'},
		{name: 'createTime',	 	mapping: 'createTime',		       type: 'string'},
		{name: 'updateTime',	 	mapping: 'updateTime',		       type: 'string'}
    ]
});
	  
Ext.define('MyExt.evaluateManager.EvaluateTabPanel', {
    extend : 'Ext.panel.Panel',
    requires : ['MyExt.evaluateManager.EvaluateFormPanel'],
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
				xtype: 'evaluateForm',
				isEditor: false,
				title: '<fmt:message key="evaluate.detail"/>'
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
    	this.tabPanel.down('evaluateForm').tabPanel = this;
    	this.tabPanel.down('evaluateForm').loadRecord(Ext.create('EvaluateForm'));
    	this.tabPanel.down('evaluateForm').loadRecord(Ext.create('EvaluateForm'));
                                                    
    	Ext.Ajax.request({
       		url: '<c:url value="/evaluate/getEvaluateById.json"/>',
        	method: 'post',
			scope: this,
			params:{evaluateId: this.record.data.evaluateId},
         	success: function(response){
         		this.tabPanel.tabPanelMask.hide();
       			var responseObject = Ext.JSON.decode(response.responseText);
       			if (responseObject.success == true){
       			var evaluateForm = this.tabPanel.down('evaluateForm');
       				if(responseObject.data != null){
       					this.recordData = responseObject.data;
       					this.recordObject = Ext.create('EvaluateForm', this.recordData);
       					this.tabPanel.down('evaluateForm').loadRecord(this.recordObject);
       					
       				}
       				
       				var imagePanel = evaluateForm.down('imagepanel[name=image]');
       				imagePanel.store.removeAll();
       				if(responseObject.imageList != null && responseObject.imageList.length > 0){
       					try{
       						imagePanel.store.removeAll();
       						for(var i = 0; i < responseObject.imageList.length; i ++){
       							imagePanel.store.insert(i, {
									fileId: responseObject.imageList[i].fileId,
									fileName: responseObject.imageList[i].fileName,
									fileType: responseObject.imageList[i].fileType,
									filePath: responseObject.imageList[i].filePath,
									fileState: 200
								});
       						}
       					}catch(e){
    					}
       				}
       				
       				
       			}else{
       				//showFailMsg(responseObject.message, 4);
       			}
			}
    	}, this);
    }
});