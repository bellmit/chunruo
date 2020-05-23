<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('ProductForm', {
	extend: 'Ext.data.Model',
	idProperty: 'productId',
    fields: [
    	{name: 'productId',	 		mapping: 'productId',		type: 'string'},
		{name: 'userId',	 		mapping: 'userId',			type: 'string'},
		{name: 'storeId',	 		mapping: 'storeId',			type: 'string'},
		{name: 'name',	 			mapping: 'name',			type: 'string'},
		{name: 'stockNumber',	 	mapping: 'stockNumber',		type: 'string'},
		{name: 'price',	 			mapping: 'price',			type: 'string'},
		{name: 'priceRecommend',	mapping: 'priceRecommend',	type: 'string'},
		{name: 'priceCost',	 		mapping: 'priceCost',		type: 'string'},
		{name: 'priceLowest',	 	mapping: 'priceLowest',		type: 'string'},
		{name: 'categoryFid',	 	mapping: 'categoryFid',		type: 'string'},
		{name: 'categoryId',	 	mapping: 'categoryId',		type: 'string'},
		{name: 'weigth',	 		mapping: 'weigth',			type: 'string'},
		{name: 'productCode',	 	mapping: 'productCode',		type: 'string'},
		{name: 'skuNumber',	 		mapping: 'skuNumber',		type: 'string'},
		{name: 'image',	 			mapping: 'image',			type: 'string'},
		{name: 'postageType',	 	mapping: 'postageType',		type: 'string'},
		{name: 'postage',	 		mapping: 'postage',			type: 'string'},
		{name: 'postageTplId',	 	mapping: 'postageTplId',	type: 'string'},
		{name: 'salesNumber',	 	mapping: 'salesNumber',		type: 'string'},
		{name: 'status',	 		mapping: 'status',			type: 'string'},
		{name: 'isSoldout',	 		mapping: 'isSoldout',		type: 'string'},
		{name: 'productIntros',	 	mapping: 'productIntros',	type: 'string'},
		{name: 'productDesc',	 	mapping: 'productDesc',		type: 'string'},
		{name: 'properties',	 	mapping: 'properties',		type: 'string'},
		{name: 'isRecommend',	 	mapping: 'isRecommend',		type: 'string'},
		{name: 'productType',	 	mapping: 'productType',		type: 'string'},
		{name: 'fxNumber',	 		mapping: 'fxNumber',		type: 'string'},
		{name: 'profit',	 		mapping: 'profit',			type: 'string'},
		{name: 'isPromot',	 		mapping: 'isPromot',		type: 'string'},
		{name: 'promotStartTime',	mapping: 'promotStartTime',	type: 'string'},
		{name: 'promotEndTime',	 	mapping: 'promotEndTime',	type: 'string'},
		{name: 'buyerLimit',	 	mapping: 'buyerLimit',		type: 'string'},
		{name: 'mergeCode',	 		mapping: 'mergeCode',		type: 'string'},
		{name: 'isDirectMail',	 	mapping: 'isDirectMail',	type: 'string'},
		{name: 'createTime',	 	mapping: 'createTime',		type: 'string'},
		{name: 'updateTime',	 	mapping: 'updateTime',		type: 'string'},
		{name: 'productIntro',	 	mapping: 'productIntro',	type: 'string'},
		{name: 'isShowPrice',	 	mapping: 'isShowPrice',		type: 'string'},
		{name: 'isFreePostage',	 	mapping: 'isFreePostage',	type: 'string'},
		{name: 'templateId',	 	mapping: 'templateId',		type: 'string'},
		{name: 'productType',	 	mapping: 'productType',		type: 'int'},
		{name: 'brandId',	 		mapping: 'brandId',			type: 'string'},
		{name: 'seckillId',	 		mapping: 'seckillId',		type: 'string'},
		{name: 'seckillSort',	 	mapping: 'seckillSort',		type: 'string'},
		{name: 'seckillPrice',	    mapping: 'seckillPrice',	type: 'string'},
		{name: 'seckillMinSellPrice',mapping: 'seckillMinSellPrice',	type: 'string'},
		{name: 'seckillProfit',	 	mapping: 'seckillProfit',	type: 'string'},
		{name: 'seckillTotalStock',	mapping: 'seckillTotalStock',type: 'int'},
		{name: 'seckillSalesNumber',mapping: 'seckillSalesNumber',type: 'int'},
		{name: 'seckillLimitNumber',mapping: 'seckillLimitNumber',type: 'int'},
		{name: 'assemblyModelId',	mapping: 'assemblyModelId',	type: 'int'},
    ]
});
	  
Ext.define('MyExt.productManager.ProductTabPanel', {
    extend : 'Ext.panel.Panel',
    requires : ['MyExt.productManager.ProductFormPanel', 'MyExt.productManager.ProductMaterialFormPanel'],
    header: false,
	closable: true,
	columnLines: true,
	animCollapse: true,
	layout: 'border',
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
	        items:[
	        <jkd:haveAuthorize access="/product/getProductById.json">
	        {
	        	text: '<fmt:message key="button.refresh"/>', 
	            iconCls: 'refresh', 	
	        	handler: function(){
	        		this.loadData();
	        	}, 
	        	scope: this
	        }
	        <c:set var="isHaveAuthorize" value="true" />
			</jkd:haveAuthorize>
			<jkd:haveAuthorize access="/product/saveProductDesc.json">
			<c:if test="${isHaveAuthorize}">,</c:if>
	        '-',{
	        	text: '<fmt:message key="product.wholesale.productDesc.edit"/>', 
	        	iconCls: 'Arrownwneswse',
	        	handler: this.productInfoHtmlEditor, 
	        	scope: this
	        }
	        <c:set var="isHaveAuthorize" value="true" />
	        </jkd:haveAuthorize>
	        <c:if test="${isHaveAuthorize}">,</c:if>
	        '->',{
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
				xtype: 'productForm',
				isEditor: true,
				title: '<fmt:message key="product.detail"/>'
			},{
				xtype: 'productMaterialForm',
				title: '<fmt:message key="product.wholesale.material"/>'
			},{
				xtype: 'component',
				name: 'htmlContent',
		    	title: '<fmt:message key="product.wholesale.productDesc"/>',
		    	html: '',
		    	cls: 'x-rich-media',
	   	 		autoScroll: true,   
	    		padding: 5,
	    		style: {
	        		color: '#FFFFFF',
	        		backgroundColor:'#FFFFFF'
	    		}
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
    	this.tabPanel.down('productForm').tabPanel = this.tabPanel;
    	this.tabPanel.down('productForm').loadRecord(Ext.create('ProductForm'));
    	this.tabPanel.down('productMaterialForm').loadRecord(Ext.create('ProductForm'));
    	Ext.Ajax.request({
       		url: '<c:url value="/product/getProductById.json"/>',
        	method: 'post',
			scope: this,
			params:{productId: this.record.data.productId},
         	success: function(response){
         		this.tabPanel.tabPanelMask.hide();
       			var responseObject = Ext.JSON.decode(response.responseText);
       			if (responseObject.success == true){
       				var productForm = this.tabPanel.down('productForm');
       				var productSpecForm = productForm.down('productSpecForm');
       				var productGroupListForm = productForm.down('productGroupList');
       				var productAggregatedListForm = productForm.down('productAggregatedList');
       				var productAggregatedListFieldset = productForm.down('fieldset[ntype=productAggregatedList]');
       				var productMaterialForm = this.tabPanel.down('productMaterialForm');
       				if(responseObject.data != null){
       					this.recordData = responseObject.data;
       					this.recordObject = Ext.create('ProductForm', this.recordData);
       					productForm.loadRecord(this.recordObject);
       					productMaterialForm.loadRecord(this.recordObject);
       					this.tabPanel.down('[name=htmlContent]').update(this.recordData.productDesc);
       				}
       			
       	
       	
       				
       				var priceCostField = productForm.down('textfield[name=priceCost]');
       				var priceRecommendField = productForm.down('textfield[name=priceRecommend]');
       				var weigthField = productForm.down('textfield[name=weigth]');
       				if(responseObject.data.productType == 3 || responseObject.data.productType == 4){
       					priceCostField.setFieldLabel('<fmt:message key="product.wholesale.priceCost.dollar" />');
       					priceRecommendField.setFieldLabel('<fmt:message key="product.wholesale.priceRecommend.dollar" />');
       					weigthField.setFieldLabel('<fmt:message key="product.wholesale.weigth.pound" />');
       				}else{
       					priceCostField.setFieldLabel('<fmt:message key="product.wholesale.priceCost" />');
       					priceRecommendField.setFieldLabel('<fmt:message key="product.wholesale.priceRecommend" />');
       					weigthField.setFieldLabel('<fmt:message key="product.wholesale.weigth" />');
       				}
       				

       			
       				var imagePanel = productForm.down('[xtype=imagepanel]');
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
       				
       				var materialImagePanel = productMaterialForm.down('[xtype=imagepanel]');
       				materialImagePanel.store.removeAll();
       				if(responseObject.materialImageList != null && responseObject.materialImageList.length > 0){
       					try{
       						materialImagePanel.store.removeAll();
       						for(var i = 0; i < responseObject.materialImageList.length; i ++){
       							materialImagePanel.store.insert(i, {
									fileId: responseObject.materialImageList[i].fileId,
									fileName: responseObject.materialImageList[i].fileName,
									fileType: responseObject.materialImageList[i].fileType,
									filePath: responseObject.materialImageList[i].filePath,
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
       				
				    
       				productSpecForm.productSpecList.setHidden(true);
    				productSpecForm.productSpecList.store.removeAll();
    				productGroupListForm.store.removeAll();
    				productForm.down('checkboxfieldset').setHidden(true);
    				productForm.down('[ntype=productGroupListFieldset]').setHidden(true);
    				if(responseObject.isGroupProduct != null && responseObject.isGroupProduct == true){
    					productForm.down('[ntype=productGroupListFieldset]').setHidden(false);
    					productForm.down('checkbox[name=isOpenV2Price]').setHidden(true);
    					productForm.down('checkbox[name=isOpenV3Price]').setHidden(true);
    					productForm.down('checkbox[name=isShowLevelPrice]').setHidden(true);
    					if(responseObject.productGroupList != null && responseObject.productGroupList.length > 0){
				       		try{
					       		for(var i = 0; i < responseObject.productGroupList.length; i ++){
					       			productGroupListForm.store.add(Ext.create('ProductGroup', responseObject.productGroupList[i]));
					       		}
				       		}catch(e){
				 			}
				       	}
    				}else{
    					productForm.down('checkboxfieldset').setHidden(false);
    					productForm.down('checkbox[name=isOpenV2Price]').setHidden(false);
    					productForm.down('checkbox[name=isOpenV3Price]').setHidden(false);
    					productForm.down('checkbox[name=isShowLevelPrice]').setHidden(false);
    					if(responseObject.data.isSpceProduct != null && responseObject.data.isSpceProduct == true){
       						productForm.down('[name=isSpceProduct]').setValue(true);
       						productForm.down('checkboxfieldset').down('checkbox').setValue(true);
       						productForm.down('checkboxfieldset').down('checkbox').setDisabled(true);
						}else {
							productForm.down('[name=isSpceProduct]').setValue(false);
							productForm.down('checkboxfieldset').down('checkbox').setValue(false);
							productForm.down('checkboxfieldset').down('checkbox').setDisabled(true);
						}
    				}
    				
    				productAggregatedListFieldset.setHidden(false);
    				productAggregatedListFieldset.collapse();
    				productAggregatedListForm.store.removeAll();
			       	if(responseObject.aggrProductList != null && responseObject.aggrProductList.length > 0){
			       		try{
				       		for(var i = 0; i < responseObject.aggrProductList.length; i ++){
				       			productAggregatedListFieldset.expand();
	       						var productAggregated = Ext.create('ProductAggregated', responseObject.aggrProductList[i]);
	       						productAggregatedListForm.store.insert(i, productAggregated);
	       					}
			       		}catch(e){
			 			}
			       	}
					
					if(responseObject.data.isSpceProduct != null && responseObject.data.isSpceProduct == true){
	       				var dynamicItemPanels = productSpecForm.dynamicItemPanel.query('[ttype=productSpecFieldSet]');
				       	if(dynamicItemPanels != null && dynamicItemPanels.length > 0){
				       		try{
				       			for(var i = 0; i < dynamicItemPanels.length; i ++){
				       				productSpecForm.dynamicItemPanel.remove(dynamicItemPanels[i], false);
									dynamicItemPanels[i].hide();
									dynamicItemPanels[i].destroy();
				       			}
				       		}catch(e){
				 			}
				       	}
			       	
				   		productSpecForm.productSpecAddButton.setHidden(true);
				       	if(responseObject.isPrimarySpec == true){
				       		try{
					       		var primaryProductSpecFieldSet = productSpecForm.addDynamicItemPanel(true);
					       		var primaryProductSpecDataView = primaryProductSpecFieldSet.down('[ttype=productSpecDataView]');
					       		primaryProductSpecFieldSet.down('productSpecModelPicker').setValue(responseObject.primarySpecModelName);
					       		primaryProductSpecFieldSet.down('productSpecModelPicker').setSpecModelValue(responseObject.primarySpecModelId);
					       		productSpecForm.productSpecAddButton.setHidden(true);
					       		
					    		for(var i = 0; i < responseObject.primarySpecTypeList.length; i ++){
					    			primaryProductSpecDataView.store.add(Ext.create('InitModel', {
					    				name: responseObject.primarySpecTypeList[i].specTypeName, 
					    				strId: responseObject.primarySpecTypeList[i].specTypeId, 
					    				specModelId: responseObject.primarySpecTypeList[i].specModelId, 
					    				filePath: responseObject.primarySpecTypeList[i].imagePath
					    			}));
					    		}
					       		primaryProductSpecDataView.isSecondarySpec = false;
								primaryProductSpecDataView.refresh();
					       		
					       		if(responseObject.isSecondarySpec == true){
					       			var secondaryProductSpecFieldSet = productSpecForm.addDynamicItemPanel(true);
					       			var secondaryProductSpecDataView = secondaryProductSpecFieldSet.down('[ttype=productSpecDataView]');
					       			secondaryProductSpecFieldSet.down('productSpecModelPicker').setValue(responseObject.secondarySpecModelName);
					       			secondaryProductSpecFieldSet.down('productSpecModelPicker').setSpecModelValue(responseObject.secondarySpecModelId);
					       			
					       			for(var i = 0; i < responseObject.secondarySpecTypeList.length; i ++){
					    				secondaryProductSpecDataView.store.add(Ext.create('InitModel', {
					    					name: responseObject.secondarySpecTypeList[i].specTypeName, 
					    					strId: responseObject.secondarySpecTypeList[i].specTypeId, 
					    					specModelId: responseObject.secondarySpecTypeList[i].specModelId
					    				}));
					    			}
					       			secondaryProductSpecDataView.isSecondarySpec = true;
									secondaryProductSpecDataView.refresh();
					       		}
					       	}catch(e){
				 			}
				       	}else{
				       		productSpecForm.productSpecAddButton.setHidden(false);
				       	}
				       
				       	if(responseObject.productSpecList != null && responseObject.productSpecList.length > 0){
				       		try{
					       		productSpecForm.productSpecList.setHidden(false);
					       		for(var i = 0; i < responseObject.productSpecList.length; i ++){
					       			productSpecForm.productSpecList.store.add(Ext.create('ProductSpecModel', responseObject.productSpecList[i]));
					       		}
					       		
					       		productSpecForm.productSpceTpl.productType = responseObject.productType;
					       		productSpecForm.productSpceTpl.isPrimarySpec = responseObject.isPrimarySpec;
					       		productSpecForm.productSpceTpl.primarySpecName = responseObject.primarySpecModelName;
					       		productSpecForm.productSpceTpl.isSecondarySpec = responseObject.isSecondarySpec;
					       		productSpecForm.productSpceTpl.secondarySpecName = responseObject.secondarySpecModelName;
					       		productSpecForm.productSpecList.refresh();
				       		}catch(e){
				 			}
				       	}
				    }
       			}
			}
    	}, this);
    },
    
    productInfoHtmlEditor : function(){
    	var productEdit = Ext.create('MyExt.productManager.ProductEdit', {
	    	style:{'line-height': '22px'},
    		viewer: this.viewer,
    		recordData: this.recordData,
    		closable: true
    	});
    	
    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
				Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
					if(e == 'yes'){
						var htmlContent = productEdit.ueditor.getContent();
	                	productEdit.form.submit({
	                    	waitMsg: 'Loading...',
	                    	url: '<c:url value="/product/saveProductDesc.json"/>',
	                    	scope: this,
	                    	params:{productId: productEdit.recordData.productId, productDesc: htmlContent},
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
		},{
			text: '<fmt:message key="button.cancel"/>',
			handler : function(){popWin.close();},
			scope: this
		}];
      	openWin(this.recordData.name, productEdit, buttons, 820, 620);
    }
});