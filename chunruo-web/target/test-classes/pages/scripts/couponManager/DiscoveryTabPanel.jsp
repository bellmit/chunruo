<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('DiscoveryForm', {
	extend: 'Ext.data.Model',
	idProperty: 'discoveryId',
    fields: [
		{name: 'discoveryId',		mapping: 'discoveryId',		              type: 'int'},
		{name: 'createrId',		    mapping: 'createrId',		              type: 'int'},
		{name: 'moduleId',	        mapping: 'moduleId',                      type: 'int'},
		{name: 'productId',	        mapping: 'productId',		              type: 'int'},
		{name: 'title',	 	        mapping: 'title',                         type: 'string'},
		{name: 'content',	 	    mapping: 'content',                       type: 'string'},
		{name: 'downLoadCount',	    mapping: 'downLoadCount',                 type: 'string'},
		{name: 'imagePath',	     	mapping: 'imagePath',                     type: 'string'},
		{name: 'name',	     	    mapping: 'name',                          type: 'string'},
		{name: 'createTime',	 	mapping: 'createTime',		              type: 'string'},
		{name: 'updateTime',	 	mapping: 'updateTime',		              type: 'string'}
    ]
});

Ext.define('MyExt.couponManager.DiscoveryTabPanel', {
    extend: 'Ext.panel.Panel',
    requires: [ 'MyExt.couponManager.DiscoveryFormPanel'],
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
				xtype: 'discoveryForm',
				isEditor: true,
				title: '<fmt:message key="discovery.detail"/>'
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
    	this.tabPanel.down('discoveryForm').tabPanel = this;
    	this.tabPanel.down('discoveryForm').loadRecord(Ext.create('DiscoveryForm'));
    	this.tabPanel.down('discoveryForm').loadRecord(Ext.create('DiscoveryForm'));
                                                    
    	Ext.Ajax.request({
       		url: '<c:url value="/discovery/getDiscoveryById.json"/>',
        	method: 'post',
			scope: this,
			params:{discoveryId: this.record.data.discoveryId},
         	success: function(response){
         		this.tabPanel.tabPanelMask.hide();
       			var responseObject = Ext.JSON.decode(response.responseText);
       			if (responseObject.success == true){
       				if(responseObject.data != null){
       					this.recordData = responseObject.data;
       					this.recordObject = Ext.create('DiscoveryForm', this.recordData);
       					this.tabPanel.down('discoveryForm').loadRecord(this.recordObject);
       					
       					var contentObj = Ext.ComponentQuery.query('textarea[name="content"]')[0];
					    var productIdsObj = Ext.ComponentQuery.query('couponProductPicker[name="productName"]')[0];
					    var imageObj = Ext.ComponentQuery.query('imagefieldset[name="image"]')[0];
						var videoObj = Ext.ComponentQuery.query('imagefieldset[name="video"]')[0];
						var videoWidthObj = Ext.ComponentQuery.query('textfield[name="videoWidth"]')[0];
					    var videoHeightObj = Ext.ComponentQuery.query('textfield[name="videoHeight"]')[0];
							    
					   if(this.recordData.type == "1"){
						   contentObj.show();
						   productIdsObj.hide();
						   imageObj.hide();
						   videoObj.hide();
						   videoWidthObj.hide();
						   videoHeightObj.hide();
						}else if(this.recordData.type == "2"){
						   contentObj.show();
						   productIdsObj.show();
						   imageObj.show();
						   videoObj.hide();
						   videoWidthObj.hide();
						   videoHeightObj.hide();
						}else if(this.recordData.type == "3"){
						    contentObj.show();
						    productIdsObj.show();
						    imageObj.show();
						    videoObj.show();
						    videoWidthObj.show();
						    videoHeightObj.show();
						}
       				}

	       				var imagePanel = this.tabPanel.down('discoveryForm').down('[xtype=imagepanel]');
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
	       				
	       				var videoPanel = this.tabPanel.down('discoveryForm').down('[xtype=imagepanels]');
	       				videoPanel.store.removeAll();
	       				if(responseObject.videoList != null && responseObject.videoList.length > 0){
	       					try{
	       						videoPanel.store.removeAll();
	       						for(var i = 0; i < responseObject.videoList.length; i ++){
	       							videoPanel.store.insert(i, {
	       						     	fileId: responseObject.videoList[i].fileId,
										fileName: responseObject.videoList[i].fileName,
										fileType: responseObject.videoList[i].fileType,
										filePath: responseObject.videoList[i].filePath,
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
    
   
});
