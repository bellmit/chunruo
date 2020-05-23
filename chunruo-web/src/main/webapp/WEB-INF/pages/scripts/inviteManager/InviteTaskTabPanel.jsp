<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('InviteTaskForm', {
	extend: 'Ext.data.Model',
	idProperty: 'taskId',
    fields: [
		{name: 'taskId',		   mapping: 'taskId',		            type: 'int'},
		{name: 'number',	       mapping: 'number',                   type: 'int'},
		{name: 'type',	     	   mapping: 'type',                     type: 'int'},
		{name: 'couponIds',	       mapping: 'couponIds',                type: 'string'},
		{name: 'imagePath',	       mapping: 'imagePath',                type: 'string'},
		{name: 'amount',	       mapping: 'amount',                   type: 'string'},
		{name: 'inviteDesc',	   mapping: 'inviteDesc',               type: 'string'},
		{name: 'createTime',	   mapping: 'createTime',		        type: 'string'},
		{name: 'updateTime',	   mapping: 'updateTime',		        type: 'string'}
    ]
});

Ext.define('MyExt.inviteManager.InviteTaskTabPanel', {
    extend : 'Ext.panel.Panel',
    requires : ['MyExt.inviteManager.InviteTaskFormPanel'],
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
				xtype: 'inviteTaskForm',
				isEditor: true,
				title: '<fmt:message key="invite.taks.detail"/>'
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
    	this.tabPanel.down('inviteTaskForm').tabPanel = this;
    	this.tabPanel.down('inviteTaskForm').loadRecord(Ext.create('InviteTaskForm'));
                                                    
    	Ext.Ajax.request({
       		url: '<c:url value="/inviteTask/getInviteTaskById.json"/>',
        	method: 'post',
			scope: this,
			params:{taskId: this.record.data.taskId},
         	success: function(response){
         		this.tabPanel.tabPanelMask.hide();
       			var responseObject = Ext.JSON.decode(response.responseText);
       			if (responseObject.success == true){
       				if(responseObject.data != null){
       					this.recordData = responseObject.data;
       					this.recordObject = Ext.create('InviteTaskForm', this.recordData);
       					this.tabPanel.down('inviteTaskForm').loadRecord(this.recordObject);
       					
       					var imagePanels = this.tabPanel.down('inviteTaskForm').down('[xtype=imagepanel]');
	       				imagePanels.store.removeAll();
	       				if(responseObject.imageList != null && responseObject.imageList.length > 0){
	       					try{
	       						imagePanels.store.removeAll();
	       						for(var i = 0; i < responseObject.imageList.length; i ++){
	       							imagePanels.store.insert(i, {
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
       				}
       			   }else{
       				 //showFailMsg(responseObject.message, 4);
       			 }
			}
    	}, this);
    },
    
   
});
