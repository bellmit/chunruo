<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>


Ext.define('RechargeTemplateForm', {
	extend: 'Ext.data.Model',
	 idProperty: 'templateId',
    fields: [
    	{name: 'templateId',		mapping: 'templateId',		 type: 'int'},
        {name: 'amount',			mapping: 'amount',		     type: 'string'},
		{name: 'giftAmount',		mapping: 'giftAmount',		 type: 'string'},
		{name: 'giftUserLevel',		mapping: 'giftUserLevel',	 type: 'int'},
		{name: 'giftUserLevelTime',	mapping: 'giftUserLevelTime',type: 'string'},
		{name: 'productId',			mapping: 'productId',		 type: 'int'},
		{name: 'type',			    mapping: 'type',		     type: 'int'},
		{name: 'couponId',			mapping: 'couponId',		 type: 'string'},
		{name: 'giftName',			mapping: 'giftName',		 type: 'string'},
		{name: 'imageUrl',			mapping: 'imageUrl',		 type: 'string'},
		{name: 'isRecommend',		mapping: 'isRecommend',		 type: 'bool'},
		{name: 'userLevel',			mapping: 'userLevel',		 type: 'int'},
		{name: 'createTime',		mapping: 'createTime',		 type: 'string'},
		{name: 'updateTime',		mapping: 'updateTime',		 type: 'int'}

	],
});

Ext.define('MyExt.couponManager.RechargeTemplateTabPanel', {
    extend : 'Ext.panel.Panel',
    requires : ['MyExt.couponManager.RechargeTemplateFormPanel'],
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
				xtype: 'rechargeTemplateForm',
				isEditor: true,
				title: '<fmt:message key="recharge.template.detail"/>'
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
    	this.tabPanel.down('rechargeTemplateForm').tabPanel = this;
    	this.tabPanel.down('rechargeTemplateForm').loadRecord(Ext.create('RechargeTemplateForm'));
    	this.tabPanel.down('rechargeTemplateForm').loadRecord(Ext.create('RechargeTemplateForm'));
                                                    
    	Ext.Ajax.request({
       		url: '<c:url value="/rechargeTemplate/getRechargeTemplateById.json"/>',
        	method: 'post',
			scope: this,
			params:{templateId: this.record.data.templateId},
         	success: function(response){
         		this.tabPanel.tabPanelMask.hide();
       			var responseObject = Ext.JSON.decode(response.responseText);
       			if (responseObject.success == true){
       				if(responseObject.data != null){
       					this.recordData = responseObject.data;
       					this.recordObject = Ext.create('RechargeTemplateForm', this.recordData);
       					this.tabPanel.down('rechargeTemplateForm').loadRecord(this.recordObject);
       					
       					var couponObj = Ext.ComponentQuery.query('textfield[name="couponId"]')[0];
						var giftNameObj = Ext.ComponentQuery.query('textfield[name="giftName"]')[0];
						var giftAmountObj = Ext.ComponentQuery.query('textfield[name="giftAmount"]')[0];
						var giftUserLevelObj = Ext.ComponentQuery.query('combobox[name="giftUserLevel"]')[0];
					    var giftUserLevelTimeObj = Ext.ComponentQuery.query('textfield[name="giftUserLevelTime"]')[0];
						var productIdObj = Ext.ComponentQuery.query('textfield[name="productId"]')[0];
						
					    if(this.recordData.type == "1"){
						    couponObj.show();
						    giftNameObj.show();
						    productIdObj.hide();
							giftAmountObj.hide();
							giftUserLevelObj.hide();
							giftUserLevelTimeObj.hide();
						}else if(this.recordData.type == "2"){
						    couponObj.hide();
						    giftNameObj.show();
						    productIdObj.hide();
							giftAmountObj.show();
							giftUserLevelObj.hide();
							giftUserLevelTimeObj.hide();
						}else if(this.recordData.type == "3"){
						    couponObj.hide();
						    giftNameObj.show();
						    productIdObj.hide();
							giftAmountObj.hide();
							giftUserLevelObj.show();
							giftUserLevelTimeObj.show();
						}else if(this.recordData.type == "4"){
						    couponObj.hide();
						    giftNameObj.show();
						    productIdObj.show();
							giftAmountObj.hide();
							giftUserLevelObj.hide();
							giftUserLevelTimeObj.hide();
						}else {
							couponObj.hide();
							giftNameObj.hide();
							productIdObj.hide();
							giftAmountObj.hide();
							giftUserLevelObj.hide();
							giftUserLevelTimeObj.hide();
						}
						
						var imagePanel = this.tabPanel.down('rechargeTemplateForm').down('[xtype=imagepanel]');
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
       				}
       				
       			   }else{
       				 //showFailMsg(responseObject.message, 4);
       			 }
			}
    	}, this);
    },
    
   
});
