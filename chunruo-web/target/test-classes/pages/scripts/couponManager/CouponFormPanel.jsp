<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.couponManager.CouponFormPanel', {
    extend : 'Ext.form.Panel',
    alias: ['widget.couponForm'],
    requires : [
    	'MyExt.couponManager.CouponProductCategoryPicker',
    	'MyExt.couponManager.ProductCategoryPicker', 
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
	    
	    this.attributeStore = Ext.create('Ext.data.Store', {
			autoDestroy: true,
			model: 'InitModel',
			data: [
			    {id: '3', name: '<fmt:message key="coupon.attribute3"/>'},
				{id: '2', name: '<fmt:message key="coupon.attribute2"/>'},
				{id: '1', name: '<fmt:message key="coupon.attribute1"/>'},
			]
		});
		
		this.useRangeTypeStore = Ext.create('Ext.data.Store', {
			autoDestroy: true,
			model: 'InitModel',
			data: [
			    {id: '1', name: '<fmt:message key="coupon.useRangeType1"/>'},
				{id: '2', name: '<fmt:message key="coupon.useRangeType2"/>'},
			]
		});
	    this.receiveTypeStore= Ext.create('Ext.data.Store', {
			autoDestroy: true,
			model: 'InitModel',
			data: [
				{id: '1', name: '<fmt:message key="coupon.receiveType1"/>'},
			]
		});
	    
	    this.couponTypeStore = Ext.create('Ext.data.Store', {
			autoDestroy: true,
			model: 'InitModel',
			data: [
				{id: '2', name: '<fmt:message key="coupon.couponType2"/>'},
				{id: '1', name: '<fmt:message key="coupon.couponType1"/>'},
			]
		});
	    
	    this.categoryIdField = Ext.create('Ext.form.Hidden', {
			xtype: 'hiddenfield', 
			name: 'categoryId', 
			allowBlank: true,
		});
		
		this.productIdField = Ext.create('Ext.form.Hidden', {
			xtype: 'hiddenfield', 
			name: 'productId', 
			allowBlank: true,
		});
		
		this.selectType = Ext.create('Ext.form.Hidden', {
			xtype: 'hiddenfield', 
			name: 'selectType', 
			allowBlank: true,
			id: 'selectType'
		});
		
	    this.items = [{
			xtype: 'fieldset',
			title: '<fmt:message key="coupon.detail"/>',
           	anchor: '98%',
           	items: [{
				xtype: 'textfield',
		    	fieldLabel: '<fmt:message key="coupon.couponName" />',
		    	labelWidth: 70,
				name: 'couponName',
	         	allowBlank: false,
	         	anchor: '95%'
			},{
			    xtype:"combo",
			    name:'useRangeType',
			    displayField:'name',
			    valueField:'id',
			    store:this.useRangeTypeStore,
			    triggerAction:'all',
			    fieldLabel: '<fmt:message key="coupon.useRangeType"/>', 
				labelWidth: 70,
				anchor: '95%',
			    selectOnFocus:true,
			    forceSelection: true,
			    allowBlank:false,
			    editable:false,
			    emptyText:'<fmt:message key="coupon.useRangeType.emptyBlank"/>',
			},{
			    xtype:"combo",
			    name:'attribute',
			    displayField:'name',
			    valueField:'id',
			    store:this.attributeStore,
			    triggerAction:'all',
			    fieldLabel: '<fmt:message key="coupon.attribute"/>', 
				labelWidth: 70,
				anchor: '95%',
			    selectOnFocus:true,
			    forceSelection: true,
			    allowBlank:false,
			    editable:false,
			    emptyText:'<fmt:message key="coupon.attribute.emptyBlank"/>'
			},this.productIdField,{
     			xtype: 'couponProductPicker',
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
			},this.categoryIdField,{
     			xtype: 'productCategoryPicker',
	    		fieldLabel: '<fmt:message key="product.category.nameType" />',
	    		labelWidth: 70,
				name: 'productCategory',
				editable: false,
         		anchor: '95%',
         		objPanel: this.categoryIdField,
         		objSelectType: this.selectType,
         		typeAhead: true,
         		listeners: {
 					scope: this,
 					itemClick : function(picker, record, item, index, e, eOpts){	
 						picker.setRawValue(record.data.name);
						this.categoryIdField.setValue(record.data.categoryId);	
 					}
 				}
			},{
				xtype: 'container',
	           	layout: 'hbox',
	           	items: [{
	                xtype: 'container',
	                flex: 1,
	                layout: 'anchor',
	                items: [{
	                	xtype: 'numberfield',
	       				fieldLabel: '<fmt:message key="coupon.totalCount" />',
	       				allowNegative: false, 
	       				allowDecimals:false,
	       				labelWidth: 70,
	           			name: 'totalCount',
	           			anchor: '98%'
	       			},{
	                	xtype: 'datefield',
	       				fieldLabel: '<fmt:message key="coupon.receiveBeginTime" />',
	       				labelWidth: 70,
	           			name: 'receiveBeginTime',
	           			editable: true,
	           			format: 'Y-m-d H:i:s',
	           			anchor: '98%'
	       			},{
	                	xtype: 'datefield',
	       				fieldLabel: '<fmt:message key="coupon.receiveEndTime" />',
	       				labelWidth: 70,
	       				editable: true,
	           			name: 'receiveEndTime',
	           		    format: 'Y-m-d H:i:s',
	           			anchor: '98%'
	       			},{
	       				xtype: 'numberfield',
	           			fieldLabel: '<fmt:message key="coupon.effectiveTime" />',
	           			labelWidth: 70,
	           			name: 'effectiveTime',
	           			readOnly: false,
	           			allowBlank:false,
	           			anchor: '98%',
	           			
	       			},{
	       				xtype: 'textarea',
	           			fieldLabel: '<fmt:message key="coupon.remark" />',
	           			labelWidth: 70,
	           			name: 'remark',
	           			readOnly: false,
	           			allowBlank:false,
	           			anchor: '98%',
	           			
	       			}]
	            },{
	                xtype: 'container',
	                flex: 1,
	                layout: 'anchor',
	                items: [{
	       				xtype: 'combobox',
	           			fieldLabel: '<fmt:message key="coupon.couponType" />',
	           			labelWidth: 60,
	           			store:this.couponTypeStore,
	           			name: 'couponType',
	           			valueField : 'id', 
	                    displayField : 'name', 
	           			anchor: '98%',
	           			allowBlank:false,
	           			editable: false,
	       			},{
	       				xtype: 'numberfield',
	           			fieldLabel: '<fmt:message key="coupon.fullAmount" />',
	           			labelWidth: 60,
	           			name: 'fullAmount',
	           			anchor: '98%'
	       			},{
	       				xtype: 'numberfield',
	           			fieldLabel: '<fmt:message key="coupon.giveAmount" />',
	           			labelWidth: 60,
	           			name: 'giveAmount',
	           			allowBlank: false,
	           			anchor: '98%'
	       			},{
	       				xtype: 'combobox',
	           			fieldLabel: '<fmt:message key="coupon.receiveType" />',
	           			labelWidth: 60,
	           			valueField : 'id', 
	                    displayField : 'name', 
	                    name: 'receiveType',
	           			anchor: '98%',
	           			allowBlank:false,
	           			editable: false,
	           			store:this.receiveTypeStore,
	       			}]
				}]
			}]
        }];
        
        if(this.isEditor){
        	this.buttons = [{
				text: '<fmt:message key="button.save"/>', 
				style: 'font-size: 14px;background: rgba(22, 155, 213, 1) none repeat scroll 0 0;border-color: rgba(22, 155, 213, 1);',
				scope: this,  
	        	handler: this.saveProductWholesale
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
    
    saveProductWholesale : function(){
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
                 		url: '<c:url value="/product/saveProductWholesale.json"/>',
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