<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.systemManager.StartImageFormPanel', {
    extend : 'Ext.form.Panel',
    alias: ['widget.startImageForm'],
    requires : ['MyExt.productManager.ImagePanel', 'Ext.ux.form.ImageFieldSet'],
 	header: false,
 	buttonAlign: 'center',
 	labelAlign: 'right',
 	labelWidth: 40,
 	bodyPadding: '5 5 0',
    defaultType: 'textfield',
    fontStyle: '<span style="font-size:14px;font-weight:bold;">{0}</span>',
    autoScroll: true,
    viewConfig: {
        stripeRows: true,
        enableTextSelection: true
    },
    
	initComponent : function(config) {
		Ext.apply(this, config);
	    
	     this.rendererStutsStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: 1, name: '<fmt:message key="start.image.phoneType1"/>'},
        		{id: 0, name: '<fmt:message key="start.image.phoneType0"/>'}
        	]
        });
        
	    this.items = [{
			xtype: 'hiddenfield', 
			name: 'templateId', 
			value:this.templateId
		},{
			xtype: 'hiddenfield', 
			name: 'id', 
		},{
			xtype: 'combobox',
	    	fieldLabel: '<fmt:message key="start.image.phoneType" />',
	    	labelWidth: 70,
			name: 'phoneType',
         	allowBlank: false,
         	anchor: '95%',
         	displayField: 'name',
			valueField: 'id',
         	store: this.rendererStutsStore
		},{
			xtype: 'textfield',
	    	fieldLabel: '<fmt:message key="start.image.height" />',
	    	labelWidth: 70,
			name: 'height',
         	allowBlank: false,
         	anchor: '95%'
		},{
			xtype: 'textfield',
	    	fieldLabel: '<fmt:message key="start.image.width" />',
	    	labelWidth: 70,
			name: 'width',
         	allowBlank: false,
         	anchor: '95%'
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
        
        this.buttons = [{
			text: '<fmt:message key="button.save"/>', 
			style: 'font-size: 14px;background: rgba(22, 155, 213, 1) none repeat scroll 0 0;border-color: rgba(22, 155, 213, 1);',
			scope: this,  
	        handler: function(){
	        	var rowsData = [];
                this.down('imagepanel').store.each(function(record) {
                    record.data.input_file = null;
                    rowsData.push(record.data);    
                }, this);

	        	var formValues = this.form.getValues();
	        	var id = formValues["id"];
	        	var height = formValues["height"];
	        	var width = formValues["width"];
	        	var productId = formValues["productId"];
	        	var templateId = formValues["templateId"];
	        	var phoneType = formValues["phoneType"];
	        	if(this.form.isValid()){
					Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
			                this.form.submit({
			                    waitMsg: 'Loading...',
			                    url: '<c:url value="/startImage/saveOrUpdateStartImage.json"/>',
			                    scope: this,
			                    params:{id : id, productId : productId, height : height, width : width, templateId:templateId,recodeGridJson: Ext.JSON.encode(rowsData),phoneType : phoneType},
			                    success: function(form, action) {
			                        var responseObject = Ext.JSON.decode(action.response.responseText);
			                        if(responseObject.success == true){
			                       		showSuccMsg(responseObject.message);
		                       		    popWin.close();
		                       		    this.store.loadPage(1);
									}else{
										showFailMsg(responseObject.message, 4);
									}
			                    }
			        		})
			        	}
			        }, this)
	        	}
	        }
	    },{
			text: '<fmt:message key="button.cancel"/>',
			style: 'font-size: 14px;background: rgba(22, 155, 213, 1) none repeat scroll 0 0;border-color: rgba(22, 155, 213, 1);',
			handler : function(){popWin.close();},
			scope: this
		}];
	    
	    this.on('afterrender', function(){
			var imagefieldsets = this.query('imagefieldset');
            for(var i = 0; i < imagefieldsets.length; i ++){
                imagefieldsets[i].on('change', this.onChangeButton, this);
            }
		}, this);
    	this.callParent();
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