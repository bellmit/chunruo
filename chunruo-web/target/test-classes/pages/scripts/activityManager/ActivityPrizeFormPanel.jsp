<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.activityManager.ActivityPrizeFormPanel', {
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
		
		this.levelStore = Ext.create('Ext.data.Store', {
			autoDestroy: true,
			model: 'InitModel',
			data: [
				{id: '1', name: '<fmt:message key="activity.prize.level1"/>'},
				{id: '2', name: '<fmt:message key="activity.prize.level2"/>'},
				{id: '3', name: '<fmt:message key="activity.prize.level3"/>'},
				{id: '4', name: '<fmt:message key="activity.prize.level4"/>'},
			]
		});
		
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
			name: 'prizeId', 
			value:this.prizeId
		   },{
				xtype: 'textfield',
		    	fieldLabel: '<fmt:message key="activity.prize.content" />',
		    	labelWidth: 70,
				name: 'content',
	         	allowBlank: false,
	         	anchor: '99%',
	         	value: this.content
			},{    
            	xtype: 'container',
	            flex: 10,
                layout: 'anchor',
                baseCls: 'my-panel-no-border',
            	items: [{
	         	xtype: 'combobox',
				labelWidth: 70,
				fieldLabel: '<fmt:message key="activity.prize.level" />',
				name: 'level',
		        displayField: 'name',
		        valueField: 'id',
		        store: this.levelStore,
		        editable: false,
		        queryMode: 'local',
		        typeAhead: true,
		        anchor: '99%',
		        value:this.level
			 },{
					xtype: 'imagefieldset',
					title: '<fmt:message key="productBrand.image"/>',
					collapsible: false,
					labelWidth: 70,
					anchor: '99%',
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