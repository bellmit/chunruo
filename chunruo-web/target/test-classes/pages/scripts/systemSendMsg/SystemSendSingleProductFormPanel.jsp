<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('SystemSendSingleProductForm', {
	extend: 'Ext.data.Model',
    fields: [
    	{name: 'id', 			type: 'int'},
    	{name: 'content', 		type: 'string'},
    	{name: 'messageType', 	type: 'string'},
    	{name: 'objectId', 		type: 'string'},
    	{name: 'createTime', 	type: 'string'},
    	{name: 'title', 		type: 'string'},
    	{name: 'imageUrl', 		type: 'string'},
    	{name: 'objectType', 	type: 'string'},
    	{name: 'level', 	type: 'string'},
    	{name: 'productId', 	type: 'string'},
	],
    idProperty: 'id'
});

Ext.define('MyExt.systemSendMsg.SystemSendSingleProductFormPanel', {
    extend : 'Ext.form.Panel',
 	header: false,
 	labelAlign: 'right',
 	labelWidth: 55,
 	bodyPadding: '5 5 0',
    defaultType: 'textfield',
    autoScroll: true,
	requires : [ 
		'MyExt.productManager.ImagePanel', 
		'Ext.ux.form.ImageFieldSet',
		'MyExt.systemSendMsg.ProductPicker'
	],
	initComponent : function(config) {
		Ext.apply(this, config);	 
        
         this.messageTypeStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: '1', name: '<fmt:message key="system.sendmsg.type.notice"/>'},
				{id: '2', name: '<fmt:message key="system.sendmsg.type.activity"/>'}
        	]
        });
        
        this.objectTypeStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
				{id: '1', name: '<fmt:message key="user.level1"/>'},
				{id: '2', name: '<fmt:message key="user.level2"/>'}
				
        	]
        });
        
        this.productIdField = Ext.create('Ext.form.Hidden', {
			xtype: 'hiddenfield', 
			name: 'productId', 
			allowBlank: true,
		});
		
		
        
      	this.items = [this.productIdField,{
     		xtype: 'systemProductPicker',
	    	fieldLabel: '<fmt:message key="product.wholesale.pushname" />',
			name: 'name',
			editable: false,
         	allowBlank: false,
         	anchor: '100%',
         	objPanel: this.productIdField,
         	objSelectType: this.selectType,
         	typeAhead: true,
         	padding :'5 5 0' ,
         	margin: '5 5',
         	listeners: {
 				scope: this,
 				itemClick : function(picker, record, item, index, e, eOpts){	
 					picker.setRawValue(record.data.name);
					this.productIdField.setValue(record.data.productId);	
 				}
 			}
		},{
				fieldLabel: '<fmt:message key="system.sendmsg.isDelaySend" />',
	   			name: 'isDelaySend',
	   			labelWidth: 100,
	   			xtype: 'checkbox',
	   			padding :'5 5 0' ,
	   			margin: '5 5',
	  			anchor: '100%',
		   },{
      		xtype: 'container',
      		padding :'5 5 0' ,
      		name : 'activityContent',
			layout: 'hbox',
			items: [{
				margin: '5 5',
				width :350,
        		xtype: 'textfield',
				anchor: '100%',
				maxLength : 15,
        		fieldLabel: '<fmt:message key="system.sendmsg.title"/>',
        		name: 'title'
			}]
        },{
      		xtype: 'container',
      		padding :'5 5 0' ,
      		name : 'activityContent',
			layout: 'hbox',
			items: [{
        		xtype: 'textarea',
        		margin: '5 5',
        		width :600,
				anchor: '100%',
				maxLength : 30,
        		fieldLabel: '<fmt:message key="system.sendmsg.content"/>',
        		name: 'content'
			}]
        },{
	        		width: 250,
					xtype: 'checkboxgroup',
					fieldLabel: '<fmt:message key="coupon.sender" />',
			        displayField: 'name',
			        valueField: 'id',
			        style: 'padding: 0px 4px',
			        allowBlank: false,
			        queryMode: 'local',
			        typeAhead: true,
			         margin: '5 5',
			        padding :'5 5 0' ,
			        anchor: '100%' , 
			        name: 'level',
			        items: [
                  		{boxLabel: '<fmt:message key="system.send.msg.objectType1" />', name: '<fmt:message key="system.send.msg.objectType1" />',inputValue:'1',anchor:'100%'},
                  		{boxLabel: '<fmt:message key="system.send.msg.objectType2" />', name: '<fmt:message key="system.send.msg.objectType2" />',inputValue:'2',anchor:'100%'},
                  		{boxLabel: '<fmt:message key="system.send.msg.objectType3" />', name: '<fmt:message key="system.send.msg.objectType3" />',inputValue:'3',anchor:'100%'},
                  		{boxLabel: '<fmt:message key="system.send.msg.objectType4" />', name: '<fmt:message key="system.send.msg.objectType4" />',inputValue:'4',anchor:'100%'},
                  		{boxLabel: '<fmt:message key="system.send.msg.objectType6" />', name: '<fmt:message key="system.send.msg.objectType6" />',inputValue:'6',anchor:'100%'},
                  		{boxLabel: '<fmt:message key="system.send.msg.objectType7" />', name: '<fmt:message key="system.send.msg.objectType7" />',inputValue:'7',anchor:'100%'},
                 		{boxLabel: '<fmt:message key="system.send.msg.objectType8" />', name: '<fmt:message key="system.send.msg.objectType8" />',inputValue:'8',anchor:'100%'},
                 		{boxLabel: '<fmt:message key="system.send.msg.objectType9" />', name: '<fmt:message key="system.send.msg.objectType9" />',inputValue:'9',anchor:'100%'},
             		]
				} ,{
				xtype: 'imagefieldset',
				title: '<fmt:message key="system.sendmsg.image"/>',
				collapsible: false,
				anchor: '100%',
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
			model: 'SystemSendSingleProductForm',
			root: 'data'
		}); 
		
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
	    	return;
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
			    	return;
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
