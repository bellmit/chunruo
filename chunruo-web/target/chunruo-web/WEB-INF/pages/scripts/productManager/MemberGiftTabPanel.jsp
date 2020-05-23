<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('Model', {
	extend: 'Ext.data.Model',
	idProperty: 'giftId',
    fields: [
    	{name: 'giftId',		    mapping: 'giftId',		    type: 'int'},
		{name: 'templateId',		mapping: 'templateId',		type: 'int'},
		{name: 'name',		        mapping: 'name',		    type: 'string'},
		{name: 'imagePath',	 		mapping: 'imagePath',		type: 'string'},
		{name: 'price',		        mapping: 'price',           type: 'string'},
		{name: 'productCode',	    mapping: 'productCode',		type: 'string'},
		{name: 'productSku',	    mapping: 'productSku',	    type: 'string'},
		{name: 'stockNumber',	 	mapping: 'stockNumber',		type: 'int'},
		{name: 'wareHouseId',	 	mapping: 'wareHouseId',		type: 'int'},
		{name: 'couponIds',		    mapping: 'couponIds',       type: 'string'},
		{name: 'updateTime',	 	mapping: 'updateTime',		type: 'string'},
		{name: 'createTime',	 	mapping: 'createTime',		type: 'string'}
    ]
});

Ext.define('MyExt.productManager.MemberGiftTabPanel', {
    extend : 'Ext.panel.Panel',
    requires : ['MyExt.productManager.MemberGiftFormPanel'],
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
				xtype: 'memberGiftForm',
				title: '<fmt:message key="member.gift.info"/>'
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
    	this.tabPanel.down('memberGiftForm').loadRecord(Ext.create('Model'));
    	
    	Ext.Ajax.request({
       		url: '<c:url value="/memberYears/getMemberGiftByGiftId.json"/>',
        	method: 'post',
			scope: this,
			params:{giftId: this.record.data.giftId},
         	success: function(response){
         		this.tabPanel.tabPanelMask.hide();
       			var responseObject = Ext.JSON.decode(response.responseText);
       			if (responseObject.success == true){
       				if(responseObject.data != null){
       					this.recordData = responseObject.data;
       					this.recordObject = Ext.create('Model', this.recordData);
       					this.tabPanel.down('memberGiftForm').loadRecord(this.recordObject);
       				}
       			
       				var memberGiftForm = this.tabPanel.down('memberGiftForm').down('[xtype=imagepanel]');
       				memberGiftForm.store.removeAll();
       				if(responseObject.imageList != null && responseObject.imageList.length > 0){
       					try{
       						memberGiftForm.store.removeAll();
       						for(var i = 0; i < responseObject.imageList.length; i ++){
       							memberGiftForm.store.insert(i, {
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
       				
       				var memberGiftForms = this.tabPanel.down('memberGiftForm').down('[xtype=imagepanels]');
       				memberGiftForms.store.removeAll();
       				if(responseObject.detailImageList != null && responseObject.detailImageList.length > 0){
       					try{
       						memberGiftForms.store.removeAll();
       						for(var i = 0; i < responseObject.detailImageList.length; i ++){
       							memberGiftForms.store.insert(i, {
									fileId: responseObject.detailImageList[i].fileId,
									fileName: responseObject.detailImageList[i].fileName,
									fileType: responseObject.detailImageList[i].fileType,
									filePath: responseObject.detailImageList[i].filePath,
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