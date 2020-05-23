<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>
Ext.define('Profit', {
	extend: 'Ext.data.Model',
    fields: [
		{name: 'productCategoryId',	type: 'int'},
		{name: 'catName',			type: 'string'},
		{name: 'catRate',			type: 'string'},
		{name: 'hsCode',			type: 'string'}
    ]
});
Ext.define('MyExt.productManager.ProductCategoryFormPanel', {
    extend : 'Ext.form.Panel',
    alias: ['widget.productCategoryFormPanel'],
    requires : [ 'MyExt.productManager.ImagePanel',
                 'MyExt.userManager.ImagePanel', 
                 'MyExt.productManager.AdImagePanel',
                 'MyExt.couponManager.ProductPicker',
                 'MyExt.productManager.ProductBrandPicker', 
                 'MyExt.productManager.DiscoveryPicker',
                 'MyExt.productManager.DiscoveryModulePicker',
                 'MyExt.productManager.DiscoveryCreaterPicker',
                 'MyExt.productManager.FxPagePicker',
                 'Ext.ux.form.ImageFieldSet',
                 'MyExt.productManager.BrandList'],
 	header: false,
 	closable: false,
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
        this.linkTypeStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: '0', name: '<fmt:message key="link.type.none"/>'},
				{id: '2', name: '<fmt:message key="link.type.product"/>'},
				{id: '3', name: '<fmt:message key="link.type.theme"/>'},
				{id: '6', name: '<fmt:message key="link.type.award"/>'},
				{id: '7', name: '<fmt:message key="link.type.mini"/>'},
				{id: '8', name: '<fmt:message key="link.type.web"/>'},
				{id: '9', name: '<fmt:message key="discovery.detail"/>'},
				{id: '10', name: '<fmt:message key="discovery.module.topic"/>'},
				{id: '11', name: '<fmt:message key="discovery.creater"/>'},
				{id: '13', name: '<fmt:message key="product.brand.detail"/>'},
        	]
        });
        
        this.statusStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: 1, name: '<fmt:message key="product.category.status1"/>'},
        		{id: 0, name: '<fmt:message key="product.category.status0"/>'}
        	]
        });
        
        this.productIdField = Ext.create('Ext.form.Hidden', {
			xtype: 'hiddenfield', 
			name: 'productId', 
			allowBlank: true,
		});
		
		this.productBrandIdField = Ext.create('Ext.form.Hidden', {
			xtype: 'hiddenfield', 
			name: 'brandId', 
			allowBlank: true
		});
		
		this.pageIdField = Ext.create('Ext.form.Hidden', {
			xtype: 'hiddenfield', 
			name: 'pageId', 
			allowBlank: true,
		});
		
		this.discoveryIdField = Ext.create('Ext.form.Hidden', {
			xtype: 'hiddenfield', 
			name: 'discoveryId', 
			allowBlank: true,
		});
		
		this.discoveryModuleIdField = Ext.create('Ext.form.Hidden', {
			xtype: 'hiddenfield', 
			name: 'moduleId', 
			allowBlank: true,
		});
		
		this.discoveryCreaterIdField = Ext.create('Ext.form.Hidden', {
			xtype: 'hiddenfield', 
			name: 'createrId', 
			allowBlank: true,
		});
        
	    this.items = [{
			xtype: 'hiddenfield', 
			name: 'categoryId', 
			value: this.id
		},{
			xtype: 'hiddenfield', 
			name: 'level', 
			value: this.level
		},{
			xtype: 'textfield',
			labelWidth: 60,
			fieldLabel: '<fmt:message key="product.category.belong"/>',
			anchor:'97%',
			hidden: !(this.level==1),
			value: this.text,
		},{
			xtype: 'textfield',
			labelWidth: 60,
			fieldLabel: '<fmt:message key="product.category.name"/>',
			name: 'name',
			anchor:'97%'
		},{
			xtype: 'numberfield',
			labelWidth: 60,
			fieldLabel: '<fmt:message key="product.category.sort"/>',
			anchor:'97%',
			name: 'sort'
		},{
			fieldLabel: '<fmt:message key="product.category.status" />',
           	name: 'status',
           	labelWidth: 60,
           	xtype: 'combobox',
          	anchor: '97%',
          	displayField: 'name',
		    valueField: 'id',
		    store: this.statusStore,
		    editable: false,
		    queryMode: 'local',
		    typeAhead: true,
		    value: 0
		},{
			xtype: 'imagefieldset',
			title: '<fmt:message key="product.category.image"/>',
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
	        	if(this.form.isValid()){
					Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
			                this.form.submit({
			                    waitMsg: 'Loading...',
			                    url: '<c:url value="/category/saveCategory.json"/>',
			                    scope: this,
			                    params:{recodeGridJson: Ext.JSON.encode(rowsData)         
			                    },
			                    success: function(form, action) {
			                        var responseObject = Ext.JSON.decode(action.response.responseText);
			                        if(responseObject.error == false){
			                       		showSuccMsg(responseObject.message);
                                     //   this.tabPanel.loadData();
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
    
    onChangeButtonMicro : function(fieldset, fieldName){
    	var adImagePanel = this.down('adImagePanel');
        var imageFileBatchPanel = Ext.create('MyExt.productManager.ImageBatchPanel', {
            isLoader: false,
            header: false
        });
        
        imageFileBatchPanel.store.removeAll([true]);
		adImagePanel.store.each(function(record) {
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
               
                adImagePanel.store.removeAll();
				imageFileBatchPanel.store.each(function(record) {
					adImagePanel.store.insert(adImagePanel.store.getCount(), {
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
        openOtherWin('<fmt:message key="image.baseImage.title"/>', imageFileBatchPanel, buttons, 720, 540);
    },
    
    addItems : function(isHiddenDelete){
    	var container = Ext.create('Ext.container.Container', {
    		header: false,
       	 	layout: 'column',  
        	border: false,  
        	items: [{    
           		xtype: 'container',
	            layout: 'hbox',
	            defaultType: 'textfield', 
	            name: 'baseTag',
            	items: [{
					xtype: 'textfield',
			    	fieldLabel: '<fmt:message key="product.tag.name" />',
			    	labelWidth: 70,
					name: 'tagNames',
		         	anchor: '95%'
	           	},{    
					hideLabel: true,  
                	xtype: 'button',
			        iconCls: 'delete',
			        scope: this,
			       	handler: function(){
			        	this.remove(container, false);
			        	container.hide(); 
			        } 
	        	},{    
					hideLabel: true,  
                	xtype: 'button',
			        iconCls: 'add',
			        scope: this,
			        handler: function(){
			        	this.addItems(false);
			       	} 
	        	}]
        	}]
    	});
    	this.add(container);
    }
});