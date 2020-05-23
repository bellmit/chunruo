<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>
Ext.define('MyExt.teamTaskManager.TeamTaskRuleForm', {
    extend : 'Ext.form.Panel',
    alias: ['widget.teamTaskRuleForm'],
    requires : ['MyExt.productManager.ImagePanel', 'Ext.ux.form.ImageFieldSet'],
 	header: false,
	closable: true,
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
		if(this.isEditor){
	       	this.buttons = [{
				text: '<fmt:message key="button.save"/>', 
				style: 'font-size: 14px;background: rgba(22, 155, 213, 1) none repeat scroll 0 0;border-color: rgba(22, 155, 213, 1);',
				scope: this,  
	        	handler: this.save
	    	}];
        }
		 this.on('afterrender', function(){
			var imagefieldsets = this.query('imagefieldset');
            for(var i = 0; i < imagefieldsets.length; i ++){
                imagefieldsets[i].on('change', this.onChangeButton, this);
            }
		}, this);
    	this.callParent(arguments);
    	this.levelStore= Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        	    {id: '1', name: '<fmt:message key="team.rule.question.level1"/>'},
        	 	{id: '2', name: '<fmt:message key="team.rule.question.level2"/>'},
        		{id: '3', name: '<fmt:message key="team.rule.question.level3"/>'},
        		
        	]
        });
        
        this.explainIdField = Ext.create('Ext.form.Hidden', {
			xtype: 'hiddenfield', 
			name: 'explainId', 
			allowBlank: true,
			value:this.explainId
		});
		this.levelField = Ext.create('Ext.form.Hidden', {
			xtype: 'hiddenfield', 
			name: 'level', 
			allowBlank: true,
			value:this.level
		});
    	this.addItems(true);
    	
    	
    },
    
    
    addItems : function(isHiddenDelete){
    	var form = Ext.create('Ext.form.Panel', {
    		header: false,
       	 	layout: 'column',  
        	border: false,  
        	items: [{    
            	xtype: 'container',
	            style: 'padding: 10px 10px 10px;',
	            flex: 10,
                layout: 'anchor',
            	items: [this.explainIdField,{
				    xtype: 'numberfield',
				    width: 250,
				    labelWidth: 70,
	       			fieldLabel: '<fmt:message key="product.intro.sort" />',
	       			allowNegative: false, // 不允许负数 
	       			allowDecimals:false,
	           		name: 'sort',
	           		style: 'padding: 0px 4px',
	           		anchor: '99%',
	           		value:this.sort
				},{
	         	xtype: 'combobox',
	         	width: 250,
				labelWidth: 73,
				fieldLabel: '<fmt:message key="team.rule.question.level" />',
				name: 'level',
		        displayField: 'name',
		        valueField: 'id',
		        store: this.levelStore,
		        editable: false,
		        queryMode: 'local',
		        anchor: '99%',
		        value:this.level,
		        hidden: this.isInvite
			    },{
				    width: 500,
	        		labelWidth: 70,
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="team.rule.question.question"/>',
				    style: 'padding: 0px 4px',   
				    anchor:'99%',
				    value: this.question,
				    name: 'question',
				    hidden: !this.isQuestion
				},{
				    width:500,
				    height: 150,
	        		labelWidth: 70,
				    xtype:'textarea',
				    fieldLabel: '<fmt:message key="team.rule.question.content"/>',
				    style: 'padding: 0px 4px',   
				    anchor:'99%',
				    name: 'content',
				    value:this.content
				},{
					xtype: 'imagefieldset',
					title: '<fmt:message key="discovery.creater.headerImage"/>',
					collapsible: false,
					anchor: '98%',
					hidden: !this.isProfit,
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
    
    save : function(){
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
                 		url: '<c:url value="/teamTask/saveOrUpdateRule.json"/>',
               			scope: this,
               			params:{type: 5,level:2,recodeGridJson: Ext.JSON.encode(rowsData)},
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