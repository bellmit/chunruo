<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.imManager.BdOperatorFormPanel', {
    extend : 'Ext.form.Panel',
    alias: ['widget.bdOperatorForm'],
    requires : ['MyExt.productManager.ImagePanel', 'Ext.ux.form.ImageFieldSet'],
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
        		{id: 1, name: '<fmt:message key="user.pushLevel1"/>'}
        	]
          });
	    
	  
	    this.items = [{
			xtype: 'fieldset',
			title: '<fmt:message key="update.bd.operator"/>',
           	anchor: '98%',
           	items: [
				{
	            xtype: 'container',
	            layout: 'hbox',
	            defaultType: 'textfield',
	            defaults: {labelWidth: 70},
	            anchor: '98%',
	            items: [{
						xtype: 'combobox',
						labelWidth: 70,
						fieldLabel: '<fmt:message key="bd.operator.operatorName" />',
						name: 'operatorUserId',
				        displayField: 'name',
				        valueField: 'id',
				        store: {
					xtype: 'store',
					autoLoad: true,
					autoDestroy: true,
					sortOnLoad: true,
					remoteSort: true,
					model: 'InitModel',
					proxy: {
						type: 'ajax',
						url: '<c:url value="/bdOperator/getImoperator.json"/>',
						reader: {
							type : 'json',
							root: 'data'
						
	  					}
					},
					scope: this
				},
				        editable: false,
				        queryMode: 'local',
				        typeAhead: true,
				        anchor: '98%',
					    allowBlank:false,
						listeners:{  
							scope: this,
					      	select:function(combo, record, index){
					         	this.selectBdUserCombobox(combo, record, index);
					      	}
					   	}
	       			}
	            ]
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
    
     selectBdUserCombobox : function(combo, record, index){
    	var bdUserCombobox  = this.down('combobox[name=bdUserId]');
    	bdUserCombobox.store.removeAll();
    	bdUserCombobox.setValue();
    	Ext.Ajax.request({
        	url: '<c:url value="/bdOperator/getBdUser.json"/>',
         	method: 'post',
			scope: this,
			params:{userId: record.data.id},
          	success: function(response){
        		var responseObject = Ext.JSON.decode(response.responseText);
        		if (responseObject.success == true){
        			if(responseObject.userInfoList != null && responseObject.userInfoList.length > 0){
       					try{
       						bdUserCombobox.store.removeAll();
       						for(var i = 0; i < responseObject.userInfoList.length; i ++){
       							bdUserCombobox.store.insert(i, {
									id: responseObject.userInfoList[i].userId,
									name: responseObject.userInfoList[i].nickname
								});
       						}
       					}catch(e){
    					}
       				}
        		}else{
	        		showFailMsg(responseObject.message, 4);	
	        	}
			}
     	})
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