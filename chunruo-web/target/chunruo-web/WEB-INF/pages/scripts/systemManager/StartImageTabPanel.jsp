<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('Model', {
	extend: 'Ext.data.Model',
	idProperty: 'id',
    fields: [
    	{name: 'id',				mapping: 'id',				type: 'int'},
    	{name: 'productId',			mapping: 'productId',		type: 'int'},
		{name: 'productName',		mapping: 'productName',		type: 'string'},
		{name: 'imagePath',	 		mapping: 'imagePath',		type: 'string'},
		{name: 'width',	 			mapping: 'width',			type: 'int'},
		{name: 'height',	 		mapping: 'height',			type: 'int'},
		{name: 'updateTime',	 	mapping: 'updateTime',		type: 'string'},
		{name: 'createTime',	 	mapping: 'createTime',		type: 'string'}
    ]
});

Ext.define('MyExt.systemManager.StartImageTabPanel', {
    extend : 'Ext.panel.Panel',
    requires : ['MyExt.systemManager.StartImageFormPanel'],
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
	        	handler: function(){
	        		this.loadData();
	        	}, 
	        	scope: this
	        },'->',{
	        	iconCls: 'tab_open',
	        	handler: function(){
	        		this.setWidth(this.clientWidth);
	        	}, 
	        	scope: this
	        },'-',{
	        	iconCls: 'tab_close',
	        	handler: function(){
	        		this.hide();
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
				xtype: 'startImageForm',
				title: '<fmt:message key="start.image.info"/>'
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
    	this.tabPanel.down('startImageForm').loadRecord(Ext.create('Model'));
    	
    	Ext.Ajax.request({
       		url: '<c:url value="/startImage/getStartImageById.json"/>',
        	method: 'post',
			scope: this,
			params:{id: this.record.data.id},
         	success: function(response){
         		this.tabPanel.tabPanelMask.hide();
       			var responseObject = Ext.JSON.decode(response.responseText);
       			if (responseObject.success == true){
       				if(responseObject.data != null){
       					this.recordData = responseObject.data;
       					this.recordObject = Ext.create('Model', this.recordData);
       					this.tabPanel.down('startImageForm').loadRecord(this.recordObject);
       				}
       			
       				var startImageForm = this.tabPanel.down('startImageForm').down('[xtype=imagepanel]');
       				startImageForm.store.removeAll();
       				if(responseObject.startImageList != null && responseObject.startImageList.length > 0){
       					try{
       						startImageForm.store.removeAll();
       						for(var i = 0; i < responseObject.startImageList.length; i ++){
       							startImageForm.store.insert(i, {
									fileId: responseObject.startImageList[i].fileId,
									fileName: responseObject.startImageList[i].fileName,
									fileType: responseObject.startImageList[i].fileType,
									filePath: responseObject.startImageList[i].filePath,
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
    },
    
    cleanSearch : function(){
    	this.keywordField.setRawValue();
		this.store.loadPage(1);
    }
});