<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('Model', {
	extend: 'Ext.data.Model',
	idProperty: 'id',
    fields: [
    	{name: 'id',			mapping: 'id',				type: 'int'},
    	{name: 'name',			mapping: 'name',			type: 'string'},
		{name: 'imagePath',		mapping: 'imagePath',		type: 'string'},
		{name: 'tagNames',		mapping: 'tagNames',		type: 'string'},
    ]
});

Ext.define('MyExt.productManager.CategoryTabPanel', {
    extend : 'Ext.panel.Panel',
    requires : ['MyExt.productManager.ProductCategoryEditFormPanel'],
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
				xtype: 'productCategoryEditFormPanel',
				title: '<fmt:message key="product.category.info"/>'
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
    	this.tabPanel.down('productCategoryEditFormPanel').loadRecord(Ext.create('Model'));
    	
    	Ext.Ajax.request({
       		url: '<c:url value="/category/getCategoryById.json"/>',
        	method: 'post',
			scope: this,
			params:{categoryId: this.record.data.id},
         	success: function(response){
         		this.tabPanel.tabPanelMask.hide();
       			var responseObject = Ext.JSON.decode(response.responseText);
       			if (responseObject.success == true){
       				if(responseObject.data != null){
       					this.recordData = responseObject.data;
       					this.recordObject = Ext.create('Model', this.recordData);
       					this.tabPanel.down('productCategoryEditFormPanel').loadRecord(this.recordObject);
       				}
       				var productCategoryEditForm = this.tabPanel.down('productCategoryEditFormPanel');
       				var productCategoryEditFormPanel = this.tabPanel.down('productCategoryEditFormPanel').down('[xtype=imagepanel]');
       				var brandListForm = productCategoryEditForm.down('brandList');
       				var brandListFieldset = productCategoryEditForm.down('fieldset[ntype=brandList]');
       				var productCategoryEditFormPanels = this.tabPanel.down('productCategoryEditFormPanel').down('[xtype=imagepanels]');
       				var microImagePanel = this.tabPanel.down('productCategoryEditFormPanel').down('[xtype=adImagePanel]');
       				
					
       				productCategoryEditFormPanel.store.removeAll();
       				if(responseObject.categoryImageList != null && responseObject.categoryImageList.length > 0){
       					try{
       						productCategoryEditFormPanel.store.removeAll();
       						for(var i = 0; i < responseObject.categoryImageList.length; i ++){
       							productCategoryEditFormPanel.store.insert(i, {
									fileId: responseObject.categoryImageList[i].fileId,
									fileName: responseObject.categoryImageList[i].fileName,
									fileType: responseObject.categoryImageList[i].fileType,
									filePath: responseObject.categoryImageList[i].filePath,
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