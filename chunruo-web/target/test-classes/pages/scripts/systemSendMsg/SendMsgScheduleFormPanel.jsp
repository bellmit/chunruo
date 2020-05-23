<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.systemSendMsg.SendMsgScheduleFormPanel', {
    extend : 'Ext.form.Panel',
    alias: ['widget.sendMsgScheduleForm'],
    requires : [
    	'MyExt.productManager.ImagePanel', 
    	'Ext.ux.form.ImageFieldSet'
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
			value:this.productId
		});
		
		this.typeStore = Ext.create('Ext.data.Store', {
			autoDestroy: true,
			model: 'InitModel',
			data: [
			    {id: '5', name: '<fmt:message key="schedule.task.type5"/>'},
			    {id: '4', name: '<fmt:message key="schedule.task.type4"/>'},
				{id: '3', name: '<fmt:message key="schedule.task.type3"/>'},
				{id: '1', name: '<fmt:message key="schedule.task.type1"/>'},
			]
		});
		
		this.items = [{
			xtype: 'hiddenfield', 
			name: 'taskId', 
			value:this.taskId
		},{
				    xtype:'datefield',
				    fieldLabel: '<fmt:message key="schedule.task.beginTime"/>',
				    labelWidth: 60,
				    format: 'Y-m-d H:i:s',
				    readOnly: false,
				    id:'beginTime',
				    name: 'beginTime',
				    value:this.beginTime != '' ? this.beginTime : Ext.Date.add(new Date(),Ext.Date.DAY,-1),
				    anchor:'98%'
				},{
	       				xtype: 'combobox',
	           			fieldLabel: '<fmt:message key="schedule.task.type" />',
	           			labelWidth: 60,
	           			store:this.typeStore,
	           			name: 'type',
	           			valueField : 'id', 
	                    displayField : 'name', 
	           			anchor: '98%',
	           			allowBlank:false,
	           			editable: false,
	           			value:this.type
	       			},{
					xtype: 'numberfield',
			    	fieldLabel: '<fmt:message key="schedule.task.messageId" />',
			    	labelWidth: 60,
					name: 'objectId',
		         	allowBlank: false,
		         	anchor: '98%',
		         	value:this.objectId
				},{
	        		width: 250,
	        		labelWidth: 60,
					xtype: 'checkboxgroup',
					fieldLabel: '<fmt:message key="coupon.sender" />',
			        displayField: 'name',
			        valueField: 'id',
			        allowBlank: false,
			        queryMode: 'local',
			        name:'levels',
			        typeAhead: true,
			        anchor: '98%' , 
			        name: 'level',
			        items: [
                  		{boxLabel: '<fmt:message key="user.level1" />', name: 'vip0',value:this.vip0,inputValue:'1',anchor:'100%'},
                  		{boxLabel: '<fmt:message key="user.level2" />', name: 'vip1',value:this.vip1,inputValue:'2',anchor:'100%'},
                  		{boxLabel: '<fmt:message key="user.level4" />', name: 'vip2',value:this.vip2,inputValue:'4',anchor:'100%'},
                  		{boxLabel: '<fmt:message key="user.level5" />', name: 'vip3',value:this.vip3,inputValue:'5',anchor:'100%'},
             		]
				}];
		
		if(this.isEditor){
	       	this.buttons = [{
				text: '<fmt:message key="button.save"/>', 
				style: 'font-size: 14px;background: rgba(22, 155, 213, 1) none repeat scroll 0 0;border-color: rgba(22, 155, 213, 1);',
				scope: this,  
	        	handler: this.savePurchaseLimit
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
    
    savePurchaseLimit : function(){
    	if(this.form.isValid()){
			Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
				if(e == 'yes'){
             		this.form.submit({
                 		waitMsg: 'Loading...',
                 		url: '<c:url value="/purchaseLimit/savePurchaseLimit.json"/>',
               			scope: this,
               			params:{type : 1},
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
		
		