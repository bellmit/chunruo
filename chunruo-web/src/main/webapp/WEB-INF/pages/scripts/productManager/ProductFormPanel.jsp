<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.productManager.ProductFormPanel', {
    extend : 'Ext.form.Panel',
    alias: ['widget.productForm'],
    requires : [
    	'MyExt.productManager.ProductCategoryPicker', 
    	'MyExt.productManager.CustomTagPicker', 
    	'MyExt.productManager.ProductBrandPicker', 
    	'MyExt.productManager.ImagePanel', 
    	'MyExt.productManager.ProductSpecForm',
    	'MyExt.productManager.ProductGroupList',
    	'MyExt.productManager.ProductAggregatedList',
    	'Ext.ux.form.ImageFieldSet', 
    	'Ext.ux.form.CheckboxFieldSet'
    ],
 	header: false,
 	buttonAlign: 'center',
 	labelAlign: 'right',
 	labelWidth: 40,
 	bodyPadding: '5 5 0',
    defaultType: 'textfield',
    fontStyle: '<span style="font-size:14px;font-weight:bold;">{0}</span>',
    autoScroll: true,
    isEditor: false,
    isGroupProduct: false,
    viewConfig: {
        stripeRows: true,
        enableTextSelection: true
    },
    
	initComponent : function(config) {
		this.tabPanelMask = new Ext.LoadMask(this, {msg:"Please wait..."});
		Ext.apply(this, config);
        
	    this.wareHouseStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		<c:forEach var="map" varStatus="cvs" items="${allProductWarehouseLists}">
        		{id: '${map.value.warehouseId}', name: '${map.value.name}'}<c:if test="${!cvs.last}">,</c:if>
        		</c:forEach>
        	]
        });
        
	    this.productIntroStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		<c:forEach var="map" varStatus="cvs" items="${allProductIntroMaps}">
        		{id: '${map.value.introId}', name: '${map.value.title}'}<c:if test="${!cvs.last}">,</c:if>
        		</c:forEach>
        	]
        });
	    
	     this.doubtIdsStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		<c:forEach var="map" varStatus="cvs" items="${allPurchaseDoubtMaps}">
        		{id: '${map.value.doubtId}', name: '${map.value.title}'}<c:if test="${!cvs.last}">,</c:if>
        		</c:forEach>
        	]
        });
	    
	    
	    this.productCountryStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: '0', name: '<fmt:message key="product.wholesale.country.no"/>'}
        		<c:forEach var="map" varStatus="cvs" items="${allProductCountryMaps}">
        		,{id: '${map.value.countryId}', name: '${map.value.countryName}'}
        		</c:forEach>
        	]
    	});
    	
    	 this.postageTemplateStore = Ext.create('Ext.data.Store', {
	    	fields:['fileId', 'fileName'],  
	    	proxy: {
				type: 'ajax',
				url: '<c:url value="/postageTpl/getPostageTemplate.json"/>',
				reader: {
					type : 'json',
                	root: 'data'
            	}
			}
	    	
        });
    	
	    this.categoryIdField = Ext.create('Ext.form.Hidden', {
			xtype: 'hiddenfield', 
			name: 'categoryIds', 
			allowBlank: true,
			multiSelect: true
		});
		
	   	this.productBrandIdField = Ext.create('Ext.form.Hidden', {
			xtype: 'hiddenfield', 
			name: 'brandId', 
			allowBlank: true
		});
		
		this.tagIdField = Ext.create('Ext.form.Hidden', {
			xtype: 'hiddenfield', 
			name: 'tagIds', 
			allowBlank: true
		});
	    
	    this.items = [{
			xtype: 'hiddenfield', 
			name: 'productId'
		},{
			xtype: 'hiddenfield', 
			name: 'seckillId'
		},{
			xtype: 'hiddenfield', 
			name: 'seckillSort'
		},{
			xtype: 'hiddenfield', 
			name: 'seckillPrice'
		},{
			xtype: 'hiddenfield', 
			name: 'seckillMinSellPrice'
		},{
			xtype: 'hiddenfield', 
			name: 'seckillProfit'
		},{
			xtype: 'hiddenfield', 
			name: 'seckillTotalStock'
		},{
			xtype: 'hiddenfield', 
			name: 'seckillSalesNumber'
		},{
			xtype: 'hiddenfield', 
			name: 'seckillLimitNumber'
		},{
			xtype: 'hiddenfield', 
			name: 'isSpceProduct'
		},this.categoryIdField,this.productBrandIdField,{
			xtype: 'fieldset',
			title: '<fmt:message key="product.wholesale.promot"/>',
           	anchor: '98%',
           	items: [{
				xtype: 'textfield',
		    	fieldLabel: '<fmt:message key="product.wholesale.name" />',
		    	labelWidth: 70,
				name: 'name',
	         	allowBlank: false,
	         	anchor: '95%'
			},{
				xtype: 'productCategoryPicker',
		    	fieldLabel: '<fmt:message key="product.wholesale.category" />',
		    	labelWidth: 70,
		    	objPanel: this.categoryIdField,
				name: 'categoryPathName',
				editable: false,
	         	allowBlank: false,
	         	anchor: '95%',
	         	multiSelect: true
			},{
	            xtype: 'container',
	            layout: 'hbox',
	            bodyPadding: 70,
	            defaultType: 'textfield',
	            anchor: '95%',
	            items: [{
					fieldLabel: '<fmt:message key="product.wholesale.isFresh" />',
           			name: 'isFresh',
           			labelWidth: 70,
           			xtype: 'checkbox',
          			anchor: '95%',
				    width: 180
				}]
	        },{
	            xtype: 'container',
	            layout: 'hbox',
	            bodyPadding: 70,
	            defaultType: 'textfield',
	            anchor: '95%',
	            items: [{
					fieldLabel: '<fmt:message key="product.wholesale.isFreePostage" />',
           			name: 'isFreePostage',
           			labelWidth: 70,
           			xtype: 'checkbox',
          			anchor: '95%',
				    width: 180
				}]
	        },{
	            xtype: 'container',
	            layout: 'hbox',
	            bodyPadding: 10,
	            defaultType: 'textfield',
	            defaults: {labelWidth: 60},
	            anchor: '100%',
	            items: [{
	                xtype: 'container',
	                flex: 1,
	                layout: 'anchor',
	                items: [{
	  			xtype: 'combobox',
				fieldLabel: '<fmt:message key="product.wholesale.postageTemplate"/>',
				name: 'templateId',
		        displayField: 'name',
		        labelWidth: 70,
		        valueField: 'id',
		        queryMode: 'local',
		        store: {
					xtype: 'store',
					autoLoad: true,
					autoDestroy: true,
					sortOnLoad: true,
					remoteSort: true,
					model: 'InitModel',
					proxy: {
						type: 'ajax',
						url: '<c:url value="/postageTpl/getPostageTemplate.json"/>',
						reader: {
							type : 'json',
							root: 'data'
						
	  					}
					},
					scope: this
				},
		        anchor: '98%'
	       	}]
				}]
	        },{
	            xtype: 'checkboxfieldset',
	            hidden: this.isGroupProduct,
	            title: '<fmt:message key="product.wholesale.isSpceProduct" />',
	            style: 'font: 300 15px/15px helvetica,arial,verdana,sans-serif;margin-top: 5px;padding: 5px 5px 10px 5px;',
	            anchor: '100%',
	            listeners:{
		        	scope: this,
					change :function(checkbox, newValue, oldValue, eOpts){
						this.down('[name=isSpceProduct]').setValue(newValue);
						if(newValue == true){
							this.down('[ttype=baseProduct]').hide();
							this.down('[ttype=spceProduct]').show();
						}else {
							this.down('[ttype=baseProduct]').show();
							this.down('[ttype=spceProduct]').hide();
						}
					}
		  		},
	            items: [{
					xtype: 'container',
					ttype: 'baseProduct',
		           	layout: 'hbox',
		           	style: 'background: #f5f5f5 none repeat scroll 0 0;',
		           	items: [{
		                xtype: 'container',
		                flex: 1,
		                layout: 'anchor',
		                items: [{
		                	xtype: 'textfield',
		       				fieldLabel: '<fmt:message key="product.wholesale.priceCost" />',
		       				labelWidth: 60,
		           			name: 'priceCost',
		           			anchor: '98%'
		       			},{
		                	xtype: 'textfield',
		       				fieldLabel: '<fmt:message key="product.wholesale.priceRecommend" />',
		       				labelWidth: 60,
		           			name: 'priceRecommend',
		           			anchor: '98%'
		       			}]
		            },{
		                xtype: 'container',
		                flex: 1,
		                layout: 'anchor',
		                items: [{
		                	xtype: 'textfield',
		       				fieldLabel: '<fmt:message key="product.wholesale.salesNumber" />',
		       				labelWidth: 60,
		           			name: 'salesNumber',
		           			anchor: '98%'
		       			},{
		                	xtype: 'textfield',
		       				fieldLabel: '<fmt:message key="product.wholesale.weigth" />',
		       				labelWidth: 60,
		           			name: 'weigth',
		           			anchor: '98%'
		       			},{
							xtype: 'textfield',
		           			fieldLabel: '<fmt:message key="product.wholesale.quantity" />',
		           			labelWidth: 60,
		           			name: 'stockNumber',
		           			anchor: '98%'
		       			}]
					}]
				},{
				    xtype: 'container',
				    ttype: 'spceProduct',
		            title: '<fmt:message key="product.wholesale.specInfo"/>',
		            style: 'font: 300 15px/15px helvetica,arial,verdana,sans-serif;margin-top: 5px;padding: 5px 5px 10px 5px;',
		            anchor: '100%',
		            hidden: true,
					items:[{
						xtype: 'productSpecForm',
			    		fieldLabel: '<fmt:message key="product.productDesc" />',
			    		labelWidth: 60,
						style: 'background: #f5f5f5 none repeat scroll 0 0;',
		         		scope: this,
		         		anchor: '100%'
		         	}]
				}]
	        }]
        },{
			xtype: 'imagefieldset',
			title: '<fmt:message key="product.wholesale.image"/>',
			collapsible: false,
			anchor: '98%',
			items: [{
				xtype: 'imagepanel',
				combineErrors: true,
				msgTarget: 'under',
				hideLabel: true,
				height: this.clientHeight,
				viewHeight: this.clientHeight
			}]
		}];
        
        if(this.isEditor){
        	<jkd:haveAuthorize access="/product/saveProduct.json">
        	this.buttons = [{
				text: '<fmt:message key="button.save"/>', 
				style: 'font-size: 14px;background: rgba(22, 155, 213, 1) none repeat scroll 0 0;border-color: rgba(22, 155, 213, 1);',
				scope: this,  
	        	handler: this.saveProductWholesale
	    	}];
	    	</jkd:haveAuthorize>
        }
        
	    this.on('afterrender', function(){
			var imagefieldsets = this.query('imagefieldset');
            for(var i = 0; i < imagefieldsets.length; i ++){
                imagefieldsets[i].on('change', this.onChangeButton, this);
            }
		}, this);
    	this.callParent();
    },
    
    saveProductWholesale : function(isNewAddProduct, productListStore){
    	var rowsData = [];    
    	var primarySpecRowsData = this.down('productSpecForm').getPrimarySpecRowsData();
    	var secondarySpecRowsData = this.down('productSpecForm').getSecondarySpecRowsData();
    	var productSpecRowsData = this.down('productSpecForm').getProductSpecRowsData();
       	this.down('imagepanel').store.each(function(record) {
       		record.data.input_file = null;
            rowsData.push(record.data);    
      	}, this);
      	
    	if(this.form.isValid()){
			Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
				if(e == 'yes'){
             		this.form.submit({
                 		waitMsg: 'Loading...',
                 		url: '<c:url value="/product/saveProduct.json"/>',
               			scope: this,
               			params:{
               				isGroupProduct: this.isGroupProduct,
               				recodeGridJson: Ext.JSON.encode(rowsData),
               				primarySpecGridJson: Ext.JSON.encode(primarySpecRowsData),
               				secondarySpecGridJson: Ext.JSON.encode(secondarySpecRowsData),
               				productSpecGridJson: Ext.JSON.encode(productSpecRowsData)
               			},
               			success: function(form, action) {
                   			var responseObject = Ext.JSON.decode(action.response.responseText);
                   			if(responseObject.error == false){
                  				showSuccMsg(responseObject.message);
                  				if(isNewAddProduct != null && isNewAddProduct == true){
                  					productListStore.loadPage(1);
                  					popWin.close();
                  				}else{
                  					this.tabPanel.loadData();
                  				}
							}else{
								showFailMsg(responseObject.message, 4);
							}
               			}
   					})
   				}
   			}, this)
 		}
    },
    
    selectWareHouseCombobox : function(combo, record, index){
    	var templateCombobox  = this.down('combobox[name=templateId]');
    	var priceCostField = this.down('textfield[name=priceCost]');
		var priceWholesaleField = this.down('textfield[name=priceWholesale]');
		var v2PriceField = this.down('textfield[name=v2Price]');
		var v3PriceField = this.down('textfield[name=v3Price]');
		var priceRecommendField = this.down('textfield[name=priceRecommend]');
		var weigthField = this.down('textfield[name=weigth]');
		var productSpecForm = this.down('productSpecForm');
    	templateCombobox.store.removeAll();
    	templateCombobox.setValue();
    	Ext.Ajax.request({
        	url: '<c:url value="/product/getPostageTemplate.json"/>',
         	method: 'post',
			scope: this,
			params:{wareHouseId: record.data.id},
          	success: function(response){
        		var responseObject = Ext.JSON.decode(response.responseText);
        		if (responseObject.success == true){
        			if(responseObject.postageTemplateList != null && responseObject.postageTemplateList.length > 0){
       					try{
       						templateCombobox.store.removeAll();
       						for(var i = 0; i < responseObject.postageTemplateList.length; i ++){
       							templateCombobox.store.insert(i, {
									id: responseObject.postageTemplateList[i].templateId,
									name: responseObject.postageTemplateList[i].name
								});
       						}
       					}catch(e){
    					}
       				}
       				productSpecForm.productSpceTpl.productType = responseObject.productType;
       				if(responseObject.productType == 3 || responseObject.productType == 4){
					priceCostField.setFieldLabel('<fmt:message key="product.wholesale.priceCost.dollar" />');
					priceWholesaleField.setFieldLabel('<fmt:message key="product.wholesale.priceWholesale.dollar" />');
					v2PriceField.setFieldLabel('<fmt:message key="product.wholesale.v2Price.dollar" />');
					v3PriceField.setFieldLabel('<fmt:message key="product.wholesale.v3Price.dollar" />');
					priceRecommendField.setFieldLabel('<fmt:message key="product.wholesale.priceRecommend.dollar" />');
					weigthField.setFieldLabel('<fmt:message key="product.wholesale.weigth.pound" />');
					}else{
       				priceCostField.setFieldLabel('<fmt:message key="product.wholesale.priceCost" />');
       				priceWholesaleField.setFieldLabel('<fmt:message key="product.wholesale.priceWholesale" />');
       				v2PriceField.setFieldLabel('<fmt:message key="product.wholesale.v2Price" />');
       				v3PriceField.setFieldLabel('<fmt:message key="product.wholesale.v3Price" />');
       				priceRecommendField.setFieldLabel('<fmt:message key="product.wholesale.priceRecommend" />');
       				weigthField.setFieldLabel('<fmt:message key="product.wholesale.weigth" />');
       				}
       				productSpecForm.productSpecList.refresh();
        		}else{
	        		showFailMsg(responseObject.message, 4);	
	        	}
			}
     	})
    },
    
    onChangeButton : function(fieldset, fieldName){
    	var imagePanel = this.down('imagepanel');
        var imageFileBatchPanel = Ext.create('MyExt.productManager.ImageBatchPanel', {
            isLoader: false,
            header: false
        });
        
        imageFileBatchPanel.store.removeAll([true]);
		imagePanel.store.each(function(record) {
			imageFileBatchPanel.store.insert(imageFileBatchPanel.store.getCount(), {
	       		fileId: record.data.fileId,
				fileName: record.data.fileName,
				fileType: record.data.fileType,
				filePath: record.data.filePath,
				fileState: imageFileBatchPanel.fileList.FILE_STATUS.COMPLETE
	    	});
		}, this);
		
		var buttons = [{
            text: '<fmt:message key="button.insert"/>',
            handler: function(){
                var rowsData = [];    
                var isError = false;
                imageFileBatchPanel.store.each(function(record) {
                    if(record.data.fileState == imageFileBatchPanel.fileList.FILE_STATUS.QUEUED
                            || record.data.fileState == imageFileBatchPanel.fileList.FILE_STATUS.ERROR){
                        isError = true;
                        return;
                    }
                    record.data.input_file = null;
                    rowsData.push(record.data);    
                }, this);
        
                if(imageFileBatchPanel.isLoader == true){
                    showWarnMsg('<fmt:message key="ajax.loading"/>');
                    return;
                }else if(isError == true){
                    showWarnMsg('<fmt:message key="image.noupload.error"/>');
                    return;
                }
               
                imagePanel.store.removeAll();
				imageFileBatchPanel.store.each(function(record) {
					imagePanel.store.insert(imagePanel.store.getCount(), {
			       		fileId: record.data.fileId,
						fileName: record.data.fileName,
						fileType: record.data.fileType,
						filePath: record.data.filePath,
						fileState: imageFileBatchPanel.fileList.FILE_STATUS.COMPLETE
			    	});
				}, this);
				popOtherWin.close();
            },
            scope: this
        },{
            text: '<fmt:message key="button.cancel"/>',
            handler : function(){popOtherWin.close();},
            scope: this
        }];
        openOtherWin('<fmt:message key="image.baseImage.title"/>', imageFileBatchPanel, buttons, 720, 540);
    }
});