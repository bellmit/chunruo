<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('ProductWarehouseTemplateForm', {
	extend: 'Ext.data.Model',
	idProperty: 'templateId',
    fields: [
		{name: 'templateId',		mapping: 'templateId',		type: 'int'},
		{name: 'name',		        mapping: 'name',		    type: 'string'}
    ]
});

Ext.define('MyExt.productManager.ProductWarehouseTemplateFormPanel', {
    extend : 'Ext.form.Panel',
 	header: false,
 	requires : ['MyExt.productManager.ImagePanel', 'Ext.ux.form.ImageFieldSet'],
 	labelAlign: 'right',
 	labelWidth: 55,
 	bodyPadding: '5 5 0',
    defaultType: 'textfield',
	
	initComponent : function(config) {
		Ext.apply(this, config);
		
		this.items = [
		{xtype: 'hiddenfield', name: 'templateId', allowBlank: true,value:this.templateId},
		{
			xtype: 'textfield',
	    	fieldLabel: '<fmt:message key="product.warehouse.template.name" />',
	    	labelWidth: 80,
			name: 'name',
         	allowBlank: true,
         	value:this.name,
         	anchor: '99%'
		},{
			xtype: 'imagefieldset',
			title: '<fmt:message key="start.image"/>',
			collapsible: false,
			anchor: '97%',
			items: [{
				xtype: 'imagepanel',
				combineErrors: true,
				msgTarget: 'under',
				hideLabel: true,
				height: this.clientHeight,
				viewHeight: this.clientHeight
			}]
		}];
		
		this.reader = new Ext.data.JsonReader({
			totalProperty: 'total',
			model: 'ProductWarehouseTemplateForm',
			root: 'data'
		}); 
		
		this.on('afterrender', function(){
			var imagefieldsets = this.query('imagefieldset');
	           for(var i = 0; i < imagefieldsets.length; i ++){
	               imagefieldsets[i].on('change', this.onChangeButton, this);
	           }
		}, this);
    	this.callParent(arguments);
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