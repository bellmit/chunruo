<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.productTaskManager.ProductTaskFormPanel', {
    extend : 'Ext.form.Panel',
    alias: ['widget.productTaskForm'],
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
		});
		
		this.items = [{
			xtype: 'hiddenfield', 
			name: 'taskId', 
		},{
			xtype: 'fieldset',
			title: '<fmt:message key="product.task.detail"/>',
           	anchor: '98%',
           	items: [{
				xtype: 'textfield',
		    	fieldLabel: '<fmt:message key="product.task.taskName" />',
		    	labelWidth: 80,
				name: 'taskName',
	         	allowBlank: false,
	         	anchor: '98%'
			},this.productIdField,{
	     		xtype: 'couponProductPicker',
		    	fieldLabel: '<fmt:message key="product.task.productName" />',
		    	labelWidth: 80,
				name: 'productName',
				editable: false,
	         	anchor: '98%',
	         	objSelectType: this.selectType,
	         	typeAhead: true,
	         	listeners: {
	 				scope: this,
	 				itemClick : function(picker, record, item, index, e, eOpts){	
	 					picker.setRawValue(record.data.name);
						this.productIdField.setValue(record.data.productId);	
	 				}
	 			}
			},{
				xtype: 'textfield',
		    	fieldLabel: '<fmt:message key="product.task.taskNumber" />',
		    	labelWidth: 80,
				name: 'taskNumber',
	         	allowBlank: false,
	         	anchor: '98%'
			},{
				xtype: 'textfield',
		    	fieldLabel: '<fmt:message key="product.task.reward" />',
		    	labelWidth: 80,
				name: 'reward',
	         	allowBlank: false,
	         	anchor: '98%'
			 },{
				xtype: 'textfield',
		    	fieldLabel: '<fmt:message key="product.task.maxGroupNumber" />',
		    	labelWidth: 80,
				name: 'maxGroupNumber',
	         	allowBlank: false,
	         	anchor: '98%'
			},{
			    xtype:'datefield',
			    fieldLabel: '<fmt:message key="product.task.beginTime"/>',
			    labelWidth: 80,
			    format: 'Y-m-d H:i:s',
			    readOnly: false,
			    name: 'beginTime',
			    value:Ext.Date.add(new Date(),Ext.Date.DAY,-1),
			    anchor:'98%'
			},{
			    xtype:'datefield',
			    fieldLabel: '<fmt:message key="product.task.endTime"/>',
			    labelWidth: 80,
			    format: 'Y-m-d H:i:s',
			    readOnly: false,
			    name: 'endTime',
			    value:Ext.Date.add(new Date(),Ext.Date.DAY,-1),
			    anchor:'98%'
			}]
		}];
		
		if(this.isEditor){
	       	this.buttons = [{
				text: '<fmt:message key="button.save"/>', 
				style: 'font-size: 14px;background: rgba(22, 155, 213, 1) none repeat scroll 0 0;border-color: rgba(22, 155, 213, 1);',
				scope: this,  
	        	handler: this.saveProductTask
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
    
    saveProductTask : function(){
    	if(this.form.isValid()){
			Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
				if(e == 'yes'){
             		this.form.submit({
                 		waitMsg: 'Loading...',
                 		url: '<c:url value="/productTask/saveProductTask.json"/>',
               			scope: this,
               			params:{},
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
		
		