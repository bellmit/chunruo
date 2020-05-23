<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.productManager.ProductGiftFormPanel', {
    extend : 'Ext.form.Panel',
    alias: ['widget.productGiftForm'],
    requires : [
    	'MyExt.productManager.ImagePanel', 
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
        
	    
	    this.items = [{
			xtype: 'hiddenfield', 
			name: 'giftId', 
		},{
			xtype: 'fieldset',
           	anchor: '98%',
           	items: [{
	  			xtype: 'combobox',
				fieldLabel: '<fmt:message key="product.gift.productTags" />',
				name: 'productSpecId',
		        displayField: 'name',
		        valueField: 'id',
		        queryMode: 'local',
		        labelWidth: 70,
		        store: {
					xtype: 'store',
					autoLoad: true,
					autoDestroy: true,
					sortOnLoad: true,
					remoteSort: true,
					model: 'InitModel',
					proxy: {
						type: 'ajax',
						url: '<c:url value="/product/getProductGiftSpecInfo.json"/>',
						reader: {
							type : 'json',
							root: 'data'
						
	  					}
					},
					scope: this
				},
		        anchor: '98%'
	       	},{
	         	xtype: 'combobox',
				labelWidth: 70,
				fieldLabel: '<fmt:message key="product.wholesale.postageTemplate" />',
				name: 'wareHouseId',
		        displayField: 'name',
		        valueField: 'id',
		        store: this.wareHouseStore,
		        editable: false,
		        queryMode: 'local',
		        typeAhead: true,
		        anchor: '98%'
			}]
        },{
			xtype: 'textfield',
         			fieldLabel: '<fmt:message key="product.gift.yearNumber" />',
         			labelWidth: 70,
         			name: 'yearNumber',
         			anchor: '98%'
     			},{
			xtype: 'textfield',
         			fieldLabel: '<fmt:message key="product.gift.productName" />',
         			labelWidth: 70,
         			name: 'productName',
         			anchor: '98%'
     			},{
			xtype: 'imagefieldset',
			title: '<fmt:message key="product.spec.headerimage"/>',
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
        	this.buttons = [{
				text: '<fmt:message key="button.save"/>', 
				style: 'font-size: 14px;background: rgba(22, 155, 213, 1) none repeat scroll 0 0;border-color: rgba(22, 155, 213, 1);',
				scope: this,  
	        	handler: this.saveProductWholesale
	    	}];
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
       	this.down('imagepanel').store.each(function(record) {
       		record.data.input_file = null;
            rowsData.push(record.data);    
      	}, this);
      	
    	if(this.form.isValid()){
			Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
				if(e == 'yes'){
             		this.form.submit({
                 		waitMsg: 'Loading...',
                 		url: '<c:url value="/product/saveGiftProductInfo.json"/>',
               			scope: this,
               			params:{
               				recodeGridJson: Ext.JSON.encode(rowsData)
               			},
               			success: function(form, action) {
                   			var responseObject = Ext.JSON.decode(action.response.responseText);
                   			if(responseObject.error == false){
                  				showSuccMsg(responseObject.message);
                  					productListStore.loadPage(1);
                  					popWin.close();
							}else{
								showFailMsg(responseObject.message, 4);
							}
               			}
   					})
   				}
   			}, this)
 		}
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