<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.systemManager.RightPopupFormPanel', {
    extend : 'Ext.form.Panel',
    alias: ['widget.rightPopupForm'],
    requires : ['MyExt.productManager.ImagePanel',
                'Ext.ux.form.ImageFieldSet',
                'MyExt.systemSendMsg.ProductPicker',
                'MyExt.productManager.FxPagePicker',
		        'MyExt.productManager.DiscoveryPicker',
		        'MyExt.productManager.ProductBrandPicker', 
		        'MyExt.productManager.DiscoveryModulePicker',
		        'MyExt.productManager.DiscoveryCreaterPicker',
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
	    
	   this.pushLevelStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: 2, name: '<fmt:message key="user.higher.pushLevel"/>'},
        		{id: 1, name: '<fmt:message key="user.pushLevel1"/>'},
        		{id: '0', name: '<fmt:message key="invites.courtesy.none"/>'},
        	]
          });
          
           this.rendererjumptypeStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: 0, name: '<fmt:message key="home.popup.jump.type0"/>'},
        		{id: 1, name: '<fmt:message key="home.popup.jump.type1"/>'},
        		{id: 2, name: '<fmt:message key="home.popup.jump.type2"/>'},
        		{id: '3', name: '<fmt:message key="link.type.theme"/>'},
				{id: '6', name: '<fmt:message key="link.type.award"/>'},
				{id: '7', name: '<fmt:message key="link.type.mini"/>'},
				{id: '8', name: '<fmt:message key="link.type.web"/>'},
				{id: '9', name: '<fmt:message key="discovery.detail"/>'},
				{id: '10', name: '<fmt:message key="discovery.module.topic"/>'},
				{id: '11', name: '<fmt:message key="discovery.creater"/>'},
				{id: '13', name: '<fmt:message key="product.brand.detail"/>'},
				{id: '14', name: '<fmt:message key="invite.page"/>'},
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
			name: 'popupId', 
		},{
			xtype: 'hiddenfield', 
			name: 'type', 
			value:'1'
		},{
			xtype: 'fieldset',
			title: '<fmt:message key="home.popup.detail"/>',
           	anchor: '98%',
           	items: [
				{
	            xtype: 'container',
	            layout: 'hbox',
	            defaultType: 'textfield',
	            defaults: {labelWidth: 70},
	            anchor: '98%',
	            items: [{
					    fieldLabel: '<fmt:message key="home.popup.isEnable" />',
	           			name: 'isEnable',
	           			labelWidth: 80,
	           			xtype: 'checkbox',
	          			anchor: '98%',
	          			style: 'padding: 10px 50px 10px;',
					    width: 180
				}]
	        },{
			xtype: 'combobox',
	    	fieldLabel: '<fmt:message key="home.popup.jump.type" />',
	    	labelWidth: 70,
			name: 'jumpPageType',
			editable: false,
         	allowBlank: false,
         	anchor: '95%',
         	displayField: 'name',
			valueField: 'id',
         	store: this.rendererjumptypeStore
		   },{
			xtype: 'textfield',
			labelWidth: 70,
			fieldLabel: '<fmt:message key="web.url"/>',
			name: 'webUrl',
			anchor:'95%'
		  },this.productIdField,{
	     		xtype: 'systemProductPicker',
		    	fieldLabel: '<fmt:message key="product.wholesale.pushname" />',
		    	labelWidth: 70,
				name: 'product',
				editable: false,
	         	anchor: '95%',
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
			},this.pageIdField,{
     			xtype: 'fxPagePicker',
	    		fieldLabel: '<fmt:message key="fx.page.link" />',
	    		labelWidth: 70,
				editable: false,
         		anchor: '95%',
         		objPanel: this.pageIdField,
         		objSelectType: this.selectType,
         		name:'fxPage',
         		typeAhead: true,
         		listeners: {
 					scope: this,
 					itemClick : function(picker, record, item, index, e, eOpts){	
 						picker.setRawValue(record.data.pageName);
						this.pageIdField.setValue(record.data.pageId);	
 					}
 				}
			},this.discoveryIdField,{
     			xtype: 'discoveryPicker',
	    		fieldLabel: '<fmt:message key="discovery.detail" />',
	    		labelWidth: 70,
				editable: false,
         		anchor: '95%',
         		objPanel: this.discoveryIdField,
         		objSelectType: this.selectType,
         		name:'discovery',
         		typeAhead: true,
         		listeners: {
 					scope: this,
 					itemClick : function(picker, record, item, index, e, eOpts){	
 						picker.setRawValue(record.data.title);
						this.discoveryIdField.setValue(record.data.discoveryId);	
 					}
 				}
			},this.discoveryModuleIdField,{
     			xtype: 'discoveryModulePicker',
	    		fieldLabel: '<fmt:message key="discovery.module.topic" />',
	    		labelWidth: 70,
				editable: false,
         		anchor: '95%',
         		objPanel: this.discoveryModuleIdField,
         		objSelectType: this.selectType,
         		name:'discoveryModule',
         		typeAhead: true,
         		listeners: {
 					scope: this,
 					itemClick : function(picker, record, item, index, e, eOpts){	
 						picker.setRawValue(record.data.name);
						this.discoveryModuleIdField.setValue(record.data.moduleId);	
 					}
 				}
			},this.discoveryCreaterIdField,{
     			xtype: 'discoveryCreaterPicker',
	    		fieldLabel: '<fmt:message key="discovery.creater" />',
	    		labelWidth: 70,
				editable: false,
         		anchor: '95%',
         		objPanel: this.discoveryCreaterIdField,
         		objSelectType: this.selectType,
         		name:'discoveryCreater',
         		typeAhead: true,
         		listeners: {
 					scope: this,
 					itemClick : function(picker, record, item, index, e, eOpts){	
 						picker.setRawValue(record.data.name);
						this.discoveryCreaterIdField.setValue(record.data.createrId);	
 					}
 				}
			},this.productBrandIdField,{
      			xtype: 'productBrandPicker',
		    	fieldLabel: '<fmt:message key="product.wholesale.brand" />',
		    	labelWidth: 70,
				name: 'brandName',
				editable: false,
	         	anchor: '95%',
	         	objSelectType: this.selectType,
	         	typeAhead: true,
	         	listeners: {
      					scope: this,
      					itemClick : function(picker, record, item, index, e, eOpts){	
      						picker.setRawValue(record.data.name);
  							this.productBrandIdField.setValue(record.data.brandId);	
      					}
      				}
			},{
			    xtype:"combo",
			    name:'pushLevel',
			    displayField:'name',
			    valueField:'id',
			    store:this.pushLevelStore,
			    fieldLabel: '<fmt:message key="home.popup.pushLevel"/>', 
				labelWidth: 70,
				anchor: '95%',
			    allowBlank:false,
			    editable:false
			    },{
	       		xtype: 'container',
  				layout: 'hbox',
  				items: [{
				    xtype:'datefield',
				    fieldLabel: '<fmt:message key="home.popup.beginTime"/>',
				    labelWidth: 70,
				    margin: '5 5',
				    format: 'Y-m-d',
				    readOnly: false,
				    name: 'beginTime',
				    value:Ext.Date.add(new Date(),Ext.Date.DAY,-1),
				    anchor:'98%'
				}]
	       	},{
	       		xtype: 'container',
  				layout: 'hbox',
  				items: [{
				    xtype:'datefield',
				    fieldLabel: '<fmt:message key="home.popup.endTime"/>',
				    labelWidth: 70,
				    margin: '5 5',
				    readOnly: false,
				    format: 'Y-m-d',
				    name: 'endTime',
				    value:new Date(),
				    anchor:'98%'
				}]
	       	}  ,{
			xtype: 'imagefieldset',
			title: '<fmt:message key="home.popup.image"/>',
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
		}]
        }];
        
        if(this.isEditor){
        	this.buttons = [{
				text: '<fmt:message key="button.save"/>', 
				style: 'font-size: 14px;background: rgba(22, 155, 213, 1) none repeat scroll 0 0;border-color: rgba(22, 155, 213, 1);',
				scope: this,  
	        	handler: this.saveHomePopup
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
    
    saveHomePopup : function(){
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
                 		url: '<c:url value="/homePopup/saveHomePopup.json"/>',
               			scope: this,
               			params:{recodeGridJson: Ext.JSON.encode(rowsData)},
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
        openOtherWin('<fmt:message key="image.baseImage.title"/>', imageFileBatchPanel, buttons, 720, 540);
    }
});