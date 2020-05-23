<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.couponManager.DiscoveryFormPanel', {
    extend : 'Ext.form.Panel',
    alias: ['widget.discoveryForm'],
    requires : [
    	'MyExt.productManager.ImagePanel',
    	'MyExt.userManager.ImagePanel', 
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
				{id: '1', name: '<fmt:message key="discovery.type1"/>'},
				{id: '2', name: '<fmt:message key="discovery.type2"/>'},
				{id: '3', name: '<fmt:message key="discovery.type3"/>'},
			]
		});
		
		 this.shareTypeStore = Ext.create('Ext.data.Store', {
			autoDestroy: true,
			model: 'InitModel',
			data: [
				{id: '1', name: '<fmt:message key="discovery.share.type1"/>'},
				{id: '2', name: '<fmt:message key="discovery.share.type2"/>'}
			]
		});
		
		this.productIdField = Ext.create('Ext.form.Hidden', {
			xtype: 'hiddenfield', 
			name: 'productIds', 
			allowBlank: true,
		});
       
		
		this.items = [{
			xtype: 'hiddenfield', 
			name: 'discoveryId', 
		},{
			xtype: 'fieldset',
			title: '<fmt:message key="discovery.creater.detail"/>',
           	anchor: '98%',
           	items: [{
				xtype: 'combobox',
				labelWidth: 60,
				fieldLabel: '<fmt:message key="discovery.creater" />',
				name: 'createrId',
		        displayField: 'name',
		        valueField: 'id',
		        store: this.discoveryCreaterStore,
		        editable: false,
		        allowBlank: false,
		        queryMode: 'local',
		        typeAhead: true,
		        anchor: '98%',
			    allowBlank:false
			},{
				xtype: 'combobox',
				labelWidth: 60,
				fieldLabel: '<fmt:message key="discovery.module" />',
				name: 'moduleId',
		        displayField: 'name',
		        valueField: 'id',
		        store: this.discoveryModuleStore,
		        editable: false,
		        queryMode: 'local',
		        typeAhead: true,
		        anchor: '98%',
			    allowBlank:false
			},{
				xtype: 'combobox',
				labelWidth: 60,
				fieldLabel: '<fmt:message key="discovery.type" />',
				name: 'type',
		        displayField: 'name',
		        valueField: 'id',
		        store: this.typeStore,
		        editable: false,
		        queryMode: 'local',
		        typeAhead: true,
		        anchor: '98%',
			    allowBlank:false,
			    hehai:this.isEditor,
			    listeners:{
					select :function(a,b,c){
					    var type = b.data.id;
					    if(a.hehai){
					    	var contentObj = Ext.ComponentQuery.query('textarea[name="content"]')[0];
					    	var productIdsObj = Ext.ComponentQuery.query('couponProductPicker[name="productName"]')[0];
					    	var imageObj = Ext.ComponentQuery.query('imagefieldset[name="image"]')[0];
					    	var videoObj = Ext.ComponentQuery.query('imagefieldset[name="video"]')[0];
					    	var videoWidthObj = Ext.ComponentQuery.query('textfield[name="videoWidth"]')[0];
					    	var videoHeightObj = Ext.ComponentQuery.query('textfield[name="videoHeight"]')[0];
					    }else{
					    	var contentObj = Ext.ComponentQuery.query('textarea[name="content"]')[1];
							var productIdsObj = Ext.ComponentQuery.query('couponProductPicker[name="productName"]')[1];
					    	var imageObj = Ext.ComponentQuery.query('imagefieldset[name="image"]')[1];
					    	var videoObj = Ext.ComponentQuery.query('imagefieldset[name="video"]')[1];
					    	var videoWidthObj = Ext.ComponentQuery.query('textfield[name="videoWidth"]')[1];
					    	var videoHeightObj = Ext.ComponentQuery.query('textfield[name="videoHeight"]')[1];
					    }
					    if(type == "1"){
						   contentObj.show();
						   productIdsObj.hide();
						   imageObj.hide();
						   videoObj.hide();
						   videoWidthObj.hide();
						   videoHeightObj.hide();
						}else if(type == "2"){
						   contentObj.show();
						   productIdsObj.show();
						   imageObj.show();
						   videoObj.hide();
						   videoWidthObj.hide();
						   videoHeightObj.hide();
						}else if(type == "3"){
						    contentObj.show();
						    productIdsObj.show();
						    imageObj.show();
						    videoObj.show();
						    videoWidthObj.show();
						    videoHeightObj.show();
						}
					}
		  		}
			},{
				xtype: 'textfield',
	    		fieldLabel: '<fmt:message key="discovery.title" />',
	    		labelWidth: 60,
				name: 'title',
	     		allowBlank: false,
	     		anchor: '98%'
		    },{
				xtype: 'textarea',
	    		fieldLabel: '<fmt:message key="discovery.content" />',
	    		labelWidth: 60,
				name: 'content',
				allowBlank: false,
				height: 100,
	     		anchor: '98%'
			},this.productIdField,{
	     		xtype: 'couponProductPicker',
		    	fieldLabel: '<fmt:message key="discovery.productIds" />',
		    	labelWidth: 60,
				name: 'productName',
				editable: false,
	         	anchor: '98%',
	         	objPanel: this.productIdField,
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
		    	fieldLabel: '<fmt:message key="discovery.downLoadCount" />',
		    	labelWidth: 60,
				name: 'downLoadCount',
	         	allowBlank: false,
	         	anchor: '98%'
			},{
				xtype: 'textfield',
		    	fieldLabel: '<fmt:message key="discovery.viewNumber" />',
		    	labelWidth: 60,
				name: 'viewNumber',
	         	allowBlank: false,
	         	anchor: '98%'
			},{
				xtype: 'textfield',
		    	fieldLabel: '<fmt:message key="discovery.likeNumber" />',
		    	labelWidth: 60,
				name: 'likeNumber',
	         	allowBlank: false,
	         	anchor: '98%'
			},{
				xtype: 'textfield',
		    	fieldLabel: '<fmt:message key="discovery.shareNumber" />',
		    	labelWidth: 60,
				name: 'shareNumber',
	         	allowBlank: false,
	         	anchor: '98%'
			},{
				xtype: 'textfield',
		    	fieldLabel: '<fmt:message key="discovery.video.width" />',
		    	labelWidth: 60,
				name: 'videoWidth',
	         	anchor: '98%'
			},{
				xtype: 'textfield',
		    	fieldLabel: '<fmt:message key="discovery.video.height" />',
		    	labelWidth: 60,
				name: 'videoHeight',
	         	anchor: '98%'
			},{
				xtype: 'combobox',
				labelWidth: 60,
				fieldLabel: '<fmt:message key="discovery.share.type" />',
				name: 'shareType',
		        displayField: 'name',
		        valueField: 'id',
		        store: this.shareTypeStore,
		        editable: false,
		        queryMode: 'local',
		        typeAhead: true,
		        anchor: '98%',
			    allowBlank:false
   			},{
				xtype: 'imagefieldset',
				name:'image',
				title: '<fmt:message key="discovery.imagePath"/>',
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
				name:'video',
				title: '<fmt:message key="discovery.video"/>',
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
	        	handler: this.saveDiscovery
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
    
    saveDiscovery : function(){
       var type = this.getForm().findField('type').getValue();
        
    	var rowsData = [];    
    	if(type != 1 ){
    	this.down('imagepanel').store.each(function(record) {
       		record.data.input_file = null;
            rowsData.push(record.data);    
      	}, this);
    	}
    	
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
                 		url: '<c:url value="/discovery/saveDiscovery.json"/>',
               			scope: this,
               			params:{recodeGridJson: Ext.JSON.encode(rowsData),videoJson: Ext.JSON.encode(rowsDatas)},
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
		
		