<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.userManager.ApplyAgentFormPanel', {
    extend : 'Ext.form.Panel',
    alias: ['widget.applyAgentForm'],
    requires : ['MyExt.productManager.ImagePanel', 
    	'Ext.ux.form.ImageFieldSet', ],
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
		
	     this.professionStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: 1, name: '<fmt:message key="user.applyAgent.profession1"/>'},
        		{id: 2, name: '<fmt:message key="user.applyAgent.profession2"/>'},
        		{id: 3, name: '<fmt:message key="user.applyAgent.profession3"/>'},
        		{id: 4, name: '<fmt:message key="user.applyAgent.profession4"/>'},
        		{id: 5, name: '<fmt:message key="user.applyAgent.profession5"/>'}
        	]
        });
        
        
	    this.items = [{
            xtype: 'hidden',
            name: 'storeId',
            allowBlank: true
        },{
        	xtype: 'fieldset',
            title: Ext.String.format(this.fontStyle, '<fmt:message key="user.applyAgent.info"/>'),
            bodyPadding: '5 5 0',
            anchor: '99%',
            layout: 'hbox',
            items:[{
                xtype: 'container',
                flex: 1,
                layout: 'anchor',
                items: [{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="user.applyAgent.applyId"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'applyId',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="user.userId"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'userId',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="user.nickname"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'nickName',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="user.applyAgent.status"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'status',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="user.applyAgent.identityNo"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'identityNo',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="user.applyAgent.city"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'city',
				    anchor:'97%'
				}]
            },{
                xtype: 'container',
                flex: 1,
                layout: 'anchor',
                items: [,{
				    xtype:'combobox',
				    fieldLabel: '<fmt:message key="user.applyAgent.profession"/>',
				    labelWidth: 85,
				    displayField: 'name',
			        valueField: 'id',
				    readOnly: true,
				    name: 'profession',
				    queryMode: 'local',
				    typeAhead: true,
				    allowBlank:false,
				    store: this.professionStore,
				    anchor:'97%',
				   },{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="user.applyAgent.linkMan"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'linkMan',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="user.applyAgent.mobile"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'mobile',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="user.applyAgent.createTime"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'createTime',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="user.applyAgent.updateTime"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'updateTime',
				    anchor:'97%'
				}]
           
			   }]
               },{
	                xtype: 'container',
	                flex: 1,
	                layout: 'anchor',
	                items: [{
			         xtype: 'imagefieldset',
			         title: '<fmt:message key="user.applyAgent.image"/>',
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