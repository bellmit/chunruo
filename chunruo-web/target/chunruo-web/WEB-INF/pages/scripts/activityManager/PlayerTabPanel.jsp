<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('PlayerForm', {
	extend: 'Ext.data.Model',
	idProperty: 'playerId',
    fields: [
    	{name: 'playerId',			mapping: 'playerId',		       type: 'int'},
		{name: 'userId',		    mapping: 'userId',		           type: 'int'},
		{name: 'playerName',	    mapping: 'playerName',             type: 'string'},
		{name: 'playerMobile',	    mapping: 'playerMobile',		   type: 'string'},
		{name: 'address',	 	    mapping: 'address',                type: 'string'},
		{name: 'provinceName',	 	mapping: 'provinceName',           type: 'string'},
		{name: 'cityName',	 	    mapping: 'cityName',               type: 'string'},
		{name: 'areaName',	 	    mapping: 'areaName',               type: 'string'},
		{name: 'content',	 	    mapping: 'content',                type: 'string'},
		{name: 'status',	 	    mapping: 'status',                 type: 'int'},
		{name: 'score',	 	        mapping: 'score',                  type: 'int'},
		{name: 'reason',	     	mapping: 'reason',                 type: 'string'},
		{name: 'level',	 	        mapping: 'level',                  type: 'int'},
		{name: 'number',	 	    mapping: 'number',                 type: 'string'},
		{name: 'image1',	 	    mapping: 'image1',                 type: 'string'},
		{name: 'image2',	 	    mapping: 'image2',                 type: 'string'},
		{name: 'image3',	 	    mapping: 'image3',                 type: 'string'},
		{name: 'image4',	 	    mapping: 'image4',                 type: 'string'},
		{name: 'createTime',	 	mapping: 'createTime',		       type: 'string'},
		{name: 'updateTime',	 	mapping: 'updateTime',		       type: 'string'},
    ]
});
	  
Ext.define('MyExt.activityManager.PlayerTabPanel', {
    extend : 'Ext.panel.Panel',
    requires : ['MyExt.activityManager.PlayerFormPanel'],
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
				xtype: 'playerForm',
				isEditor: false,
				title: '<fmt:message key="player.detail"/>'
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
    	this.tabPanel.down('playerForm').tabPanel = this;
    	this.tabPanel.down('playerForm').loadRecord(Ext.create('PlayerForm'));
    	this.tabPanel.down('playerForm').loadRecord(Ext.create('PlayerForm'));
                                                    
    	Ext.Ajax.request({
       		url: '<c:url value="/activity/getPlayerById.json"/>',
        	method: 'post',
			scope: this,
			params:{playerId: this.record.data.playerId},
         	success: function(response){
         		this.tabPanel.tabPanelMask.hide();
       			var responseObject = Ext.JSON.decode(response.responseText);
       			if (responseObject.success == true){
       			var playerForm = this.tabPanel.down('playerForm');
       				if(responseObject.data != null){
       					this.recordData = responseObject.data;
       					this.recordObject = Ext.create('PlayerForm', this.recordData);
       					this.tabPanel.down('playerForm').loadRecord(this.recordObject);
       					
       				}
       				
       		//		var imagePanel = playerForm.down('imagepanel[name=image1]');
       		//		imagePanel.store.removeAll();
       		//		if(responseObject.image1List != null && responseObject.image1List.length > 0){
       		//			try{
       //						imagePanel.store.removeAll();
       //						for(var i = 0; i < responseObject.image1List.length; i ++){
       //							imagePanel.store.insert(i, {
		//							fileId: responseObject.image1List[i].fileId,
	//								fileName: responseObject.image1List[i].fileName,
	//								fileType: responseObject.image1List[i].fileType,
	//								filePath: responseObject.image1List[i].filePath,
	//								fileState: 200
//								});
  //     						}
 //      					}catch(e){
 //   					}
//       				}
       				
       				var imagePanel = playerForm.down('imagepanel[name=image]');
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