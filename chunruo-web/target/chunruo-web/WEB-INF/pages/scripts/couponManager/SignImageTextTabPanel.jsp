<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('SignImageTextForm', {
	extend: 'Ext.data.Model',
	idProperty: 'textId',
    fields: [
    	{name: 'textId',			mapping: 'textId',		       type: 'int'},
		{name: 'oneDate',		    mapping: 'oneDate',		       type: 'string'},
		{name: 'imagePath',	        mapping: 'imagePath',          type: 'string'},
		{name: 'content',	        mapping: 'content',			   type: 'string'},
		{name: 'createTime',	 	mapping: 'createTime',		   type: 'string'},
		{name: 'updateTime',	 	mapping: 'updateTime',		   type: 'string'},
    ]
});
	  
Ext.define('MyExt.couponManager.SignImageTextTabPanel', {
    extend : 'Ext.panel.Panel',
    requires : ['MyExt.couponManager.SignImageTextFormPanel'],
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
				xtype: 'signImageTextForm',
				isEditor: true,
				title: '<fmt:message key="image.text.detail"/>'
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
    	this.tabPanel.down('signImageTextForm').tabPanel = this;
    	this.tabPanel.down('signImageTextForm').loadRecord(Ext.create('SignImageTextForm'));
    	this.tabPanel.down('signImageTextForm').loadRecord(Ext.create('SignImageTextForm'));
                                                    
    	Ext.Ajax.request({
       		url: '<c:url value="/sign/getImageTextById.json"/>',
        	method: 'post',
			scope: this,
			params:{textId: this.record.data.textId},
         	success: function(response){
         		this.tabPanel.tabPanelMask.hide();
       			var responseObject = Ext.JSON.decode(response.responseText);
       			if (responseObject.success == true){
       			var signImageTextForm = this.tabPanel.down('signImageTextForm');
       				if(responseObject.data != null){
       					this.recordData = responseObject.data;
       					this.recordObject = Ext.create('SignImageTextForm', this.recordData);
       					this.tabPanel.down('signImageTextForm').loadRecord(this.recordObject);
       					
       				}
       				
       				var imagePanel = signImageTextForm.down('[xtype=imagepanel]');
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