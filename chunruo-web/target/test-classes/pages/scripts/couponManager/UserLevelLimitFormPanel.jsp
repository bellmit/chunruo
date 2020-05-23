<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.couponManager.UserLevelLimitFormPanel', {
    extend : 'Ext.form.Panel',
    alias: ['widget.userLevelLimitForm'],
    requires : [
    	'MyExt.productManager.ImagePanel', 
    	'Ext.ux.form.ImageFieldSet',
    	'MyExt.couponManager.ProductPicker'
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
    viewConfig: {
        stripeRows: true,
        enableTextSelection: true
    },
    
	initComponent : function(config) {
		Ext.apply(this, config);
		
        this.productIdField = Ext.create('Ext.form.Hidden', {
			xtype: 'hiddenfield', 
			name: 'productId', 
			allowBlank: true,
			value:this.productId
		});
		
		this.items = [{
			xtype: 'hiddenfield', 
			name: 'limitId', 
			value:this.limitId
		},this.productIdField,{
     		xtype: 'couponProductPicker',
	    	fieldLabel: '<fmt:message key="product.task.productName" />',
	    	labelWidth: 80,
			name: 'productName',
			editable: false,
         	anchor: '98%',
         	objSelectType: this.selectType,
         	typeAhead: true,
         	value:this.productName,
         	listeners: {
 				scope: this,
 				itemClick : function(picker, record, item, index, e, eOpts){	
 					picker.setRawValue(record.data.name);
					this.productIdField.setValue(record.data.productId);	
 				}
 			}
		},{
			xtype: 'fieldset',
			title: '<fmt:message key="purchase.limit.save"/>',
           	anchor: '98%',
           	items: [{
				xtype: 'numberfield',
		    	fieldLabel: '<fmt:message key="purchase.limit.v1Number" />',
		    	labelWidth: 80,
				name: 'v1Number',
	         	allowBlank: false,
	         	anchor: '98%',
	         	value:this.v1Number
			},{
				xtype: 'numberfield',
		    	fieldLabel: '<fmt:message key="purchase.limit.v2Number" />',
		    	labelWidth: 80,
				name: 'v2Number',
	         	allowBlank: false,
	         	anchor: '98%',
	         	value:this.v2Number
			}]
		}];
		
		if(this.isEditor){
	       	this.buttons = [{
				text: '<fmt:message key="button.save"/>', 
				style: 'font-size: 14px;background: rgba(22, 155, 213, 1) none repeat scroll 0 0;border-color: rgba(22, 155, 213, 1);',
				scope: this,  
	        	handler: this.savePurchaseLimit
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
    
    savePurchaseLimit : function(){
    	if(this.form.isValid()){
			Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
				if(e == 'yes'){
             		this.form.submit({
                 		waitMsg: 'Loading...',
                 		url: '<c:url value="/purchaseLimit/savePurchaseLimit.json"/>',
               			scope: this,
               			params:{type : 1},
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
		
		