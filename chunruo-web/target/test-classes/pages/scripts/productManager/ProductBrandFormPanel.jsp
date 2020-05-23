<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.productManager.ProductBrandFormPanel', {
    extend : 'Ext.form.Panel',
    alias: ['widget.productBrandForm'],
    requires : ['MyExt.productManager.ImagePanel','MyExt.productManager.FirstCategoryPicker', 'MyExt.productManager.BackGroundImagePanel','MyExt.productManager.AdImagePanel','MyExt.userManager.ImagePanel','Ext.ux.form.ImageFieldSet'],
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
		
		this.isHotStore= Ext.create('Ext.data.Store', {
			autoDestroy: true,
			model: 'InitModel',
			data: [
        		{id: 1, name: '<fmt:message key="button.yes"/>'},
        		{id: 0, name: '<fmt:message key="button.no"/>'}
        	]
		});
		
		this.productCountryStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: '0', name: '<fmt:message key="product.wholesale.country.no"/>'}
        		<c:forEach var="map" varStatus="cvs" items="${allProductCountryMaps}">
        		,{id: '${map.value.countryId}', name: '${map.value.countryName}'}
        		</c:forEach>
        	]
    	});
    	
    	this.categoryIdField = Ext.create('Ext.form.Hidden', {
			xtype: 'hiddenfield', 
			name: 'categoryId', 
			allowBlank: true,
		});
		
		this.items = [{
			xtype: 'hiddenfield', 
			name: 'brandId', 
		},{
			xtype: 'fieldset',
			title: '<fmt:message key="productBrand.detail"/>',
           	anchor: '95%',
           	items: [{
					xtype: 'textfield',
			    	fieldLabel: '<fmt:message key="productBrand.name" />',
			    	labelWidth: 70,
					name: 'name',
		         	allowBlank: false,
		         	anchor: '95%'
				},{
					xtype: 'textfield',
			    	fieldLabel: '<fmt:message key="productBrand.shortName" />',
			    	labelWidth: 70,
					name: 'shortName',
		         	allowBlank: false,
		         	anchor: '95%'
				},
				{
					xtype: 'textfield',
			    	fieldLabel: '<fmt:message key="productBrand.initial" />',
			    	labelWidth: 70,
					name: 'initial',
		         	allowBlank: false,
		         	anchor: '95%'
				},{
						xtype: 'combobox',
						labelWidth: 70,
						fieldLabel: '<fmt:message key="product.brand.countryName" />',
						name: 'countryId',
				        displayField: 'name',
				        valueField: 'id',
				        store: this.productCountryStore,
				        editable: false,
				        allowBlank:false,
				        queryMode: 'local',
				        typeAhead: true,
				        anchor: '95%'
	       			},
				{
      				xtype: 'checkbox',
          			fieldLabel: '<fmt:message key="productBrand.isHot" />',
          			labelWidth: 70,
          			valueField : 'id', 
                    displayField : 'name', 
                    name: 'isHot',
          			anchor: '95%',
          			editable: false,
          			store:this.isHotStore,
       			},{
					xtype: 'numberfield',
			    	fieldLabel: '<fmt:message key="productBrand.sort" />',
			    	labelWidth: 70,
					name: 'sort',
		         	anchor: '95%'
				},this.categoryIdField,
	       	   {
     			xtype: 'firstCategoryPicker',
	    		fieldLabel: '<fmt:message key="productBrand.category" />',
	    		labelWidth: 70,
				editable: false,
         		anchor: '95%',
         		objPanel: this.categoryIdField,
         		objSelectType: this.selectType,
         		name:'category',
         		typeAhead: true,
         		listeners: {
 					scope: this,
 					itemClick : function(picker, record, item, index, e, eOpts){	
 						picker.setRawValue(record.data.name);
						this.categoryIdField.setValue(record.data.categoryId);	
 					}
 				}
			},{
					xtype: 'textfield',
			    	fieldLabel: '<fmt:message key="productBrand.intro" />',
			    	labelWidth: 70,
					name: 'intro',
		         	allowBlank: false,
		         	anchor: '95%'
				},{
					xtype: 'textarea',
			    	fieldLabel: '<fmt:message key="productBrand.brandDesc" />',
			    	labelWidth: 70,
					name: 'brandDesc',
		         	allowBlank: false,
		         	anchor: '95%'
				},
				{
					xtype: 'imagefieldset',
					title: '<fmt:message key="productBrand.image"/>',
					collapsible: false,
					anchor: '95%',
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
					title: '<fmt:message key="productBrand.country.image"/>',
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
				},{
					xtype: 'imagefieldset',
					title: '<fmt:message key="productBrand.ad.image"/>',
					collapsible: false,
					anchor: '98%',
					items: [{
						xtype: 'adImagePanel',
						combineErrors: true,
						msgTarget: 'under',
						hideLabel: true,
						height: this.clientHeight,
						viewHeight: this.clientHeight
					}]
				},{
					xtype: 'imagefieldset',
					title: '<fmt:message key="productBrand.background.image"/>',
					collapsible: false,
					anchor: '98%',
					items: [{
						xtype: 'backGroundImagePanel',
						combineErrors: true,
						msgTarget: 'under',
						hideLabel: true,
						height: this.clientHeight,
						viewHeight: this.clientHeight
					}]
				},
				{
				    xtype: 'container',
		            layout: 'hbox',
		            style: 'padding: 1px 2px 1px;',
		            name:'baseTag',
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
				        	form.hide(); 
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
				}  
		    ]
		}];
		
		if(this.isEditor){
	       	this.buttons = [{
				text: '<fmt:message key="button.save"/>', 
				style: 'font-size: 14px;background: rgba(22, 155, 213, 1) none repeat scroll 0 0;border-color: rgba(22, 155, 213, 1);',
				scope: this,  
	        	handler: this.saveBrand
	    	}];
        }
       
	    this.on('afterrender', function(){
			var imagefieldsets = this.query('imagefieldset');
            for(var i = 0; i < imagefieldsets.length; i ++){
                if(i == 1){
                  imagefieldsets[i].on('change', this.onChangeButtons, this);
                }else if(i == 0){
                  imagefieldsets[i].on('change', this.onChangeButton, this); 
                }else if(i == 2 ){
                  imagefieldsets[i].on('change', this.onChangeButtonAd, this); 
                }else if(i == 3 ){
                  imagefieldsets[i].on('change', this.onChangeButtonBack, this); 
                }
                
            }
		}, this);
    	this.callParent();
    },
    
    saveBrand : function(){
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
      	
      	var adRowsData = [];    
       	this.down('adImagePanel').store.each(function(record) {
       		record.data.input_file = null;
            adRowsData.push(record.data);    
      	}, this);
      	
      	var backRowsData = [];    
       	this.down('backGroundImagePanel').store.each(function(record) {
       		record.data.input_file = null;
            backRowsData.push(record.data);    
      	}, this);
      	
    	if(this.form.isValid()){
			Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
				if(e == 'yes'){
             		this.form.submit({
                 		waitMsg: 'Loading...',
                 		url: '<c:url value="/brand/saveBrand.json"/>',
               			scope: this,
               			params:{ recodeGridJson: Ext.JSON.encode(rowsData),
               			         countryImageJson: Ext.JSON.encode(rowsDatas),
               			         adImageJson: Ext.JSON.encode(adRowsData),
               			         backImageJson: Ext.JSON.encode(backRowsData)
               			       },
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
    
    
     onChangeButtonBack : function(fieldset, fieldName){
    	var backGroundImagePanel = this.down('backGroundImagePanel');
        var imageFileBatchPanel = Ext.create('MyExt.productManager.ImageBatchPanel', {
            isLoader: false,
            header: false
        });
        
        imageFileBatchPanel.store.removeAll([true]);
		backGroundImagePanel.store.each(function(record) {
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
               
                backGroundImagePanel.store.removeAll();
				imageFileBatchPanel.store.each(function(record) {
					backGroundImagePanel.store.insert(backGroundImagePanel.store.getCount(), {
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
    
     onChangeButtonAd : function(fieldset, fieldName){
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
        openOtherWin('<fmt:message key="image.baseOneImage.title"/>', imageFileBatchPanel, buttons, 720, 540);
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
	            style: 'padding: 8px 12px 8px;',
	            name:'baseTag',
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
    },
});		
		
		