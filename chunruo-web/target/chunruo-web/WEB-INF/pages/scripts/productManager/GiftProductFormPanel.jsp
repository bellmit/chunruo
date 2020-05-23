<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.productManager.GiftProductFormPanel', {
    extend : 'Ext.form.Panel',
    alias: ['widget.giftProductForm'],
    requires : ['MyExt.productManager.ImagePanel', 'Ext.ux.form.ImageFieldSet','MyExt.productManager.ProductSpecPicker'],
 	header: false,
 	buttonAlign: 'center',
 	labelAlign: 'right',
 	labelWidth: 40,
 	bodyPadding: '5 5 0',
    defaultType: 'textfield',
    fontStyle: '<span style="font-size:14px;font-weight:bold;">{0}</span>',
    autoScroll: true,
    isEditor: false,
    viewConfig: {
        stripeRows: true,
        enableTextSelection: true
    },
    
	initComponent : function(config) {
		Ext.apply(this, config);
		
		this.discoveryCreaterStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		<c:forEach var="map" varStatus="cvs" items="${allDiscoveryCreaterMaps}">
        		{id: '${map.value.createrId}', name: '${map.value.name}'}<c:if test="${!cvs.last}">,</c:if>
        		</c:forEach>
        	]
        });
        
        this.discoveryModuleStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		<c:forEach var="map" varStatus="cvs" items="${allDiscoveryModuleMaps}">
        		{id: '${map.value.moduleId}', name: '${map.value.name}'}<c:if test="${!cvs.last}">,</c:if>
        		</c:forEach>
        	]
        });
        
        this.typeStore = Ext.create('Ext.data.Store', {
			autoDestroy: true,
			model: 'InitModel',
			data: [
				{id: '2', name: '<fmt:message key="gift.product.type2"/>'},
				{id: '1', name: '<fmt:message key="gift.product.type1"/>'},
			]
		});
        
        this.productIdField = Ext.create('Ext.form.Hidden', {
			xtype: 'hiddenfield', 
			name: 'productId', 
			allowBlank: true,
		});
		this.productSpecIdField = Ext.create('Ext.form.Hidden', {
			xtype: 'hiddenfield', 
			name: 'productSpecId', 
			allowBlank: true,
		});
		
		this.items = [{
			xtype: 'hiddenfield', 
			name: 'giftProductId', 
		},{
			xtype: 'fieldset',
			title: '<fmt:message key="discovery.creater.detail"/>',
           	anchor: '98%',
           	items: [{
	       				xtype: 'combobox',
	           			fieldLabel: '<fmt:message key="gift.product.type" />',
	           			labelWidth: 60,
	           			store:this.typeStore,
	           			name: 'type',
	           			valueField : 'id', 
	                    displayField : 'name', 
	           			anchor: '98%',
	           			allowBlank:false,
	           			editable: false,
	           			hehai:this.isEditor,
					    listeners:{
							select :function(a,b,c){
							    var type = b.data.id;
							    if(a.hehai){
							    var couponIdObj = Ext.ComponentQuery.query('textfield[name="couponId"]')[0];
							    var productNameObj = Ext.ComponentQuery.query('productSpecPicker[name="productName"]')[0];
							  	var stockNumberObj = Ext.ComponentQuery.query('textfield[name="stockNumber"]')[0];
							  
							    }else{
							    var couponIdObj = Ext.ComponentQuery.query('textfield[name="couponId"]')[1];
							    var productNameObj = Ext.ComponentQuery.query('productSpecPicker[name="productName"]')[1];
							  	var stockNumberObj = Ext.ComponentQuery.query('textfield[name="stockNumber"]')[1];
							    }
							    if(type == "1"){
								   couponIdObj.hide();
								   productNameObj.show();
								   stockNumberObj.show();
								}else if(type == "2"){
								   couponIdObj.show();
								   productNameObj.hide();
								   stockNumberObj.hide();
								}
							}
				  		}
	       			},{
					xtype: 'textfield',
			    	fieldLabel: '<fmt:message key="gift.product.giftName" />',
			    	labelWidth: 60,
					name: 'name',
		         	allowBlank: false,
		         	anchor: '98%'
				    },{
					xtype: 'textfield',
			    	fieldLabel: '<fmt:message key="gift.product.couponId" />',
			    	labelWidth: 60,
					name: 'couponId',
		         	anchor: '98%'
				   },{
					xtype: 'textfield',
			    	fieldLabel: '<fmt:message key="gift.product.stockNumber" />',
			    	labelWidth: 60,
					name: 'stockNumber',
		         	anchor: '98%'
					},this.productIdField,this.productSpecIdField,{
		     		xtype: 'productSpecPicker',
			    	fieldLabel: '<fmt:message key="discovery.product" />',
			    	labelWidth: 60,
					name: 'productName',
					editable: false,
		         	anchor: '98%',
		         	objSelectType: this.selectType,
		         	typeAhead: true,
		         	listeners: {
		 				scope: this,
		 				itemClick : function(picker, record, item, index, e, eOpts){	
		 					picker.setRawValue(record.data.name + '->'+record.data.productTags );
							this.productIdField.setValue(record.data.productId);
						    this.productSpecIdField.setValue(record.data.productSpecId);	
		 				}
		 			}
				}]
		}];
		
		if(this.isEditor){
	       	this.buttons = [{
				text: '<fmt:message key="button.save"/>', 
				style: 'font-size: 14px;background: rgba(22, 155, 213, 1) none repeat scroll 0 0;border-color: rgba(22, 155, 213, 1);',
				scope: this,  
	        	handler: this.saveDiscovery
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
    
    saveDiscovery : function(){
    	
    	if(this.form.isValid()){
			Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
				if(e == 'yes'){
             		this.form.submit({
                 		waitMsg: 'Loading...',
                 		url: '<c:url value="/giftProduct/saveGiftProduct.json"/>',
               			scope: this,
               			success: function(form, action) {
                   			var responseObject = Ext.JSON.decode(action.response.responseText);
                   			if(responseObject.error == false){
                  				showSuccMsg(responseObject.message);
                  				this.tabPanel.loadData();
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
        openOtherWin('<fmt:message key="image.baseOneImage.title"/>', imageFileBatchPanel, buttons, 720, 540);
	},
});		
		
		