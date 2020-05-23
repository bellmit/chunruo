<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.couponManager.DiscoveryCreaterFormPanel', {
    extend : 'Ext.form.Panel',
    alias: ['widget.discoveryCreaterForm'],
    requires : ['MyExt.productManager.ImagePanel','MyExt.userManager.ImagePanel', 'Ext.ux.form.ImageFieldSet'],
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
		
		
		
		this.items = [{
			xtype: 'hiddenfield', 
			name: 'createrId', 
		},{
			xtype: 'fieldset',
			title: '<fmt:message key="discovery.creater.detail"/>',
           	anchor: '98%',
           	items: [{
					    fieldLabel: '<fmt:message key="discovery.creater.isEnable" />',
	           			name: 'isEnable',
	           			labelWidth: 70,
	           			xtype: 'checkbox',
	          			anchor: '98%',
				  },{
					    fieldLabel: '<fmt:message key="discovery.creater.isRecommend" />',
	           			name: 'isRecommend',
	           			labelWidth: 70,
	           			xtype: 'checkbox',
	          			anchor: '98%',
				  },{
					xtype: 'textfield',
			    	fieldLabel: '<fmt:message key="discovery.creater.name" />',
			    	labelWidth: 70,
					name: 'name',
		         	allowBlank: false,
		         	anchor: '98%'
				},{
					xtype: 'textarea',
			    	fieldLabel: '<fmt:message key="discovery.module.introduce" />',
			    	labelWidth: 70,
					name: 'introduce',
		         	allowBlank: false,
		         	anchor: '95%',
		         	height:200
				},{
					xtype: 'imagefieldset',
					title: '<fmt:message key="discovery.creater.headerImage"/>',
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
				},{
					xtype: 'imagefieldset',
					title: '<fmt:message key="discovery.creater.backgroundImage"/>',
					collapsible: false,
					anchor: '98%',
					items: [{
						xtype: 'imagepanels',
						combineErrors: true,
						msgTarget: 'under',
						hideLabel: true,
						height: this.clientHeight,
						viewHeight: this.clientHeight
					}]
				}]
		}];
		
		if(this.isEditor){
	       	this.buttons = [{
				text: '<fmt:message key="button.save"/>', 
				style: 'font-size: 14px;background: rgba(22, 155, 213, 1) none repeat scroll 0 0;border-color: rgba(22, 155, 213, 1);',
				scope: this,  
	        	handler: this.saveCreater
	    	}];
        }
       
	    this.on('afterrender', function(){
			var imagefieldsets = this.query('imagefieldset');
            for(var i = 0; i < imagefieldsets.length; i ++){
                if(i == 1){
                  imagefieldsets[i].on('change', this.onChangeButtons, this);
                }else{
                  imagefieldsets[i].on('change', this.onChangeButton, this); 
                }
                
            }
		}, this);
    	this.callParent();
    },
    
    saveCreater : function(){
    	var rowsData = [];    
       	this.down('imagepanel').store.each(function(record) {
       		record.data.input_file = null;
            rowsData.push(record.data);    
      	}, this);
      	
        var rowsDatas = [];    
       	this.down('imagepanels').store.each(function(record) {
       		record.data.input_file = null;
            rowsDatas.push(record.data);    
      	}, this);
      	
    	if(this.form.isValid()){
			Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
				if(e == 'yes'){
             		this.form.submit({
                 		waitMsg: 'Loading...',
                 		url: '<c:url value="/discovery/saveCreater.json"/>',
               			scope: this,
               			params:{recodeGridJson: Ext.JSON.encode(rowsData),bgRecodeGridJson: Ext.JSON.encode(rowsDatas)},
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
    
     onChangeButtons : function(fieldset, fieldName){
    	var imagePanels = this.down('imagepanels');
        var imageFileBatchPanel = Ext.create('MyExt.productManager.ImageBatchPanel', {
            isLoader: false,
            header: false
        });
        
        imageFileBatchPanel.store.removeAll([true]);
		imagePanels.store.each(function(record) {
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
               
                imagePanels.store.removeAll();
				imageFileBatchPanel.store.each(function(record) {
					imagePanels.store.insert(imagePanels.store.getCount(), {
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
		
		