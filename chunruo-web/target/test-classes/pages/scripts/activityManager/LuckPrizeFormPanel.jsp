<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.activityManager.LuckPrizeFormPanel', {
    extend : 'Ext.form.Panel',
 	header: false,
	closable: true,
	requires : ['MyExt.productManager.ImagePanel', 'Ext.ux.form.ImageFieldSet'],
	columnLines: true,
	animCollapse: true,
	collapsible: true,
    scroll: 'both',
    autoScroll: true,
   	items:[],
	viewConfig: {	
		stripeRows: true,
		enableTextSelection: true
	},
	
	initComponent : function(config) {
		Ext.apply(this, config);
		
		this.on('afterrender', function(){
			var imagefieldsets = this.query('imagefieldset');
            for(var i = 0; i < imagefieldsets.length; i ++){
                imagefieldsets[i].on('change', this.onChangeButton, this);
            }
		}, this);
    	this.callParent(arguments);
    	this.addItems(true);
    },
    
    addItems : function(isHiddenDelete){
    	var form = Ext.create('Ext.form.Panel', {
    		header: false,
       	 	layout: 'column',  
        	border: false,  
        	items: [{
			xtype: 'hiddenfield', 
			name: 'luckId', 
			value:this.luckId
		},{    
            	xtype: 'container',
	            style: 'padding: 10px 10px 10px;',
	            flex: 10,
                layout: 'anchor',
                baseCls: 'my-panel-no-border',
            	items: [{
						fieldLabel: '<fmt:message key="activity.luck.isThanks" />',
	           			name: 'isThanks',
	           			labelWidth: 100,
	           			xtype: 'checkbox',
	          			anchor: '99%'
					},{
			           xtype: 'textfield',
			           labelWidth: 70,
			           fieldLabel: '<fmt:message key="activity.luck.name"/>',
			           anchor:'99%',
			           name: 'name',
			           value: this.name
		           },{
			           xtype: 'numberfield',
			           labelWidth: 70,
			           fieldLabel: '<fmt:message key="activity.luck.weight"/>',
			           anchor:'99%',
			           name: 'weight',
			           value: this.weight
		           },{
			           xtype: 'numberfield',
			           labelWidth: 70,
			           fieldLabel: '<fmt:message key="activity.luck.number"/>',
			           anchor:'99%',
			           name: 'number',
			           value: this.number
		           },{
					xtype: 'imagefieldset',
					title: '<fmt:message key="acitivity.image"/>',
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
        	}]
    	});
    	this.add(form);
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