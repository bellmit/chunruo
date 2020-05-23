<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('InviteImageFormPanel', {
	extend: 'Ext.data.Model',
	idProperty: 'imageId',
    fields: [
    	{name: 'imageId',			mapping: 'imageId',			type: 'int'},
		{name: 'imageContent',		mapping: 'imageContent',	type: 'string'},
		{name: 'typeName',			mapping: 'typeName',		type: 'string'},
		{name: 'imageType',			mapping: 'imageType',		type: 'int'},
		{name: 'createTime',	 	mapping: 'createTime',		type: 'string'},
		{name: 'updateTime',	 	mapping: 'updateTime',		type: 'string'},
    ]
});

Ext.define('MyExt.couponManager.InviteImageTabPanel', {
    extend : 'Ext.panel.Panel',
    requires : ['MyExt.couponManager.InviteImageFormPanel'],
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
				xtype: 'inviteImageFormPanel',
				isEditor: true,
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
    	this.tabPanel.down('inviteImageFormPanel').tabPanel = this;
    	this.tabPanel.down('inviteImageFormPanel').loadRecord(Ext.create('InviteImageFormPanel'));
    	this.tabPanel.down('inviteImageFormPanel').loadRecord(Ext.create('InviteImageFormPanel'));
                                                    
    	Ext.Ajax.request({
       		url: '<c:url value="/inviteImage/getInviteImageByType.json"/>',
        	method: 'post',
			scope: this,
			params:{imageType: this.record.data.imageType},
         	success: function(response){
         		this.tabPanel.tabPanelMask.hide();
       			var responseObject = Ext.JSON.decode(response.responseText);
       			if (responseObject.success == true){
       				if(responseObject.data != null){
       					this.recordData = responseObject.data;
       					this.recordObject = Ext.create('InviteImageFormPanel', this.recordData);
       					this.tabPanel.down('inviteImageFormPanel').loadRecord(this.recordObject);
       				}
       				    var contObj = this.tabPanel.down('inviteImageFormPanel').down('[xtype=textarea]');
       				    if(responseObject.data.imageType == '3' || responseObject.data.imageType == '4'
       				        || responseObject.data.imageType == '5' || responseObject.data.imageType == '6'){
       				       contObj.hide();
       				    }else{
       				    contObj.show();
       				    }
	       				var imagePanel = this.tabPanel.down('inviteImageFormPanel').down('[xtype=imagepanel]');
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
