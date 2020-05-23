<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('ProductCategoryForm', {
	extend: 'Ext.data.Model',
	idProperty: 'categoryId',
    fields: [
		{name: 'categoryId',	mapping: 'categoryId',	type: 'int'},
		{name: 'name',	 		mapping: 'name',		type: 'string'},
		{name: 'description',	mapping: 'description',	type: 'string'},
		{name: 'parentId',	 	mapping: 'parentId',	type: 'string'},
		{name: 'imagePath',	 	mapping: 'imagePath',	type: 'string'},
		{name: 'status',	 	mapping: 'status',		type: 'string'},
		{name: 'sort',	 		mapping: 'sort',		type: 'string'},
		{name: 'level',	 		mapping: 'level',		type: 'string'},
		{name: 'profit',	 	mapping: 'profit',		type: 'string'},
		{name: 'tagNames',	 	mapping: 'tagNames',	type: 'string'},
		{name: 'createTime',	mapping: 'createTime',	type: 'string'},
		{name: 'updateTime',	mapping: 'updateTime',	type: 'string'}
    ]
});

Ext.define('MyExt.productManager.ProductCategoryTabPanel', {
    extend : 'Ext.panel.Panel',
    requires : ['MyExt.productManager.ProductCategoryFormPanel'],
    header: false,
	closable: false,
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
	        		//this.productList.show();
	        		this.setWidth(this.initWidth);
	        	}, 
	        	scope: this
	        }]
	    });
	    
		this.items = [{
			xtype: 'productCategoryFormPanel',
			title: '<fmt:message key="order.info"/>'
		}];
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
    	this.tabPanel.down('productWholesaleForm').loadRecord(Ext.create('ProductCategoryForm'));
    	
    	Ext.Ajax.request({
       		url: '<c:url value="/product/getProductWholesaleById.json"/>',
        	method: 'post',
			scope: this,
			params:{productId: this.record.data.productId},
         	success: function(response){
         		this.tabPanel.tabPanelMask.hide();
       			var responseObject = Ext.JSON.decode(response.responseText);
       			if (responseObject.success == true){
       				if(responseObject.data != null){
       					this.recordData = responseObject.data;
       					this.recordObject = Ext.create('ProductForm', this.recordData);
       					this.tabPanel.down('productForm').loadRecord(this.recordObject);
       					this.tabPanel.down('[name=htmlContent]').update(this.recordData.productDesc);
       				}
       				var productCategoryForm = this.tabPanel.down('productCategoryFormPanel');
       				
					if(responseObject.data.level == 1){
    					productForm.down('checkbox[name=isRecommend]').setHidden(true);
					}
    					
       			
       				if(responseObject.imageList != null && responseObject.imageList.length > 0){
       					try{
       						var imagePanel = this.tabPanel.down('productForm').down('imagepanel');
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
       			
       				if(responseObject.productCategory != null && responseObject.productCategory.productCategoryId != null){
		    			this.tabPanel.down('productForm').down('productCategoryTabPanel').store.insert(0, {
							productCategoryId: responseObject.productCategory.productCategoryId,
							catName: responseObject.productCategory.catName,
							vaTrate: responseObject.productCategory.vaTrate,
							customRate: responseObject.productCategory.customRate,
							customAmountRate: responseObject.productCategory.customAmountRate
						});
       				}
       			}else{
       				//showFailMsg(responseObject.message, 4);
       			}
			}
    	})
    },
    
    productInfoHtmlEditor : function(){
    	var productEdit = Ext.create('MyExt.productManager.ProductEdit', {
	    	style:{'line-height': '22px'},
    		viewer: this.viewer,
    		recordData: this.recordData,
    		closable: true
    	});
    	
    	var buttons = [
    	<jkd:haveAuthorize access="/category/editProductCategory.json">
    	{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
				Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
					if(e == 'yes'){
						var htmlContent = productEdit.ueditor.getContent();
	                	productEdit.form.submit({
	                    	waitMsg: 'Loading...',
	                    	url: '<c:url value="/product/saveProductWholesaleInfo.json"/>',
	                    	scope: this,
	                    	params:{productId: productEdit.recordData.productId, content: htmlContent},
	                    	success: function(form, action) {
	                        	var responseObject = Ext.JSON.decode(action.response.responseText);
	                        	if(responseObject.error == false){
	                       			showSuccMsg(responseObject.message);
	                        		this.loadData();
									popWin.close();
								}else{
									showFailMsg(responseObject.message, 4);
								}
	                    	}
	        			})
	        		}
	        	}, this)
			},
			scope: this
		},
		</jkd:haveAuthorize>
		{
			text: '<fmt:message key="button.cancel"/>',
			handler : function(){popWin.close();},
			scope: this
		}];
      	openWin(this.recordData.name, productEdit, buttons, 820, 620);
    },
    
    cleanSearch : function(){
    	this.keywordField.setRawValue();
		this.store.loadPage(1);
    }
});