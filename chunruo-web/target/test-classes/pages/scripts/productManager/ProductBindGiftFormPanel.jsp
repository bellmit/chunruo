<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.productManager.ProductBindGiftFormPanel', {
    extend : 'Ext.form.Panel',
    alias: ['widget.productBindGiftForm'],
    requires : [
    	'MyExt.productManager.ProductCategoryPicker', 
    	'MyExt.productManager.CustomTagPicker', 
    	'MyExt.productManager.ProductBrandPicker', 
    	'MyExt.productManager.ImagePanel', 
    	'MyExt.productManager.ProductSpecForm',
    	'MyExt.productManager.ProductBindGiftList',
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
        
	   
	    
	    this.items = [{
			xtype: 'hiddenfield', 
			name: 'productId'
		},{
        	xtype: 'fieldset',
        	ntype: 'giftProductListFieldset',
			title: '<fmt:message key="gift.product.info"/>',
           	anchor: '100%',
           	items: [{
				xtype: 'productBindGiftList',
				
				header: false
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
    	var productGroupRowsData = this.down('productBindGiftList').getProductGroupRowsData();

      	
    	if(this.form.isValid()){
			Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
				if(e == 'yes'){
             		this.form.submit({
                 		waitMsg: 'Loading...',
                 		url: '<c:url value="/giftProduct/saveGiftProductRelation.json"/>',
               			scope: this,
               			params:{
               				recordGridJson: Ext.JSON.encode(productGroupRowsData)
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