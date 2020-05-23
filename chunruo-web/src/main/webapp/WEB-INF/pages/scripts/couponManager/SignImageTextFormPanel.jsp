<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.couponManager.SignImageTextFormPanel', {
    extend : 'Ext.form.Panel',
    alias: ['widget.signImageTextForm'],
    requires : [ 'MyExt.productManager.ImagePanel', 'Ext.ux.form.ImageFieldSet'],
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
	    
	    
		this.isCompleteTaskStore= Ext.create('Ext.data.Store', {
			autoDestroy: true,
			model: 'InitModel',
			data: [
        		{id: 1, name: '<fmt:message key="button.yes"/>'},
        		{id: 0, name: '<fmt:message key="button.no"/>'}
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
			name: 'attributeContent', 
			allowBlank: true,
		});
		
		this.selectType = Ext.create('Ext.form.Hidden', {
			xtype: 'hiddenfield', 
			name: 'selectType', 
			allowBlank: true,
			id: 'selectType'
		});
		
		rendererStuts =function(val){
		if(val == true) {
            return '<b><fmt:message key="button.yes"/></b>';
        }else{
            return '<fmt:message key="button.no"/>';
        }
	},
		
	    this.items = [{
			xtype: 'fieldset',
			title: '<fmt:message key="image.text.detail"/>',
           	anchor: '98%',
           	items: [{
			         xtype: 'hiddenfield', 
			         name: 'textId', 
		        },{
				    xtype:'datefield',
				    fieldLabel: '<fmt:message key="image.text.oneDate"/>',
				    labelWidth: 70,
				    format: 'Y-m-d H:i:s',
				    readOnly: false,
				    name: 'oneDate',
				    anchor:'99%'
				},{
				xtype: 'textarea',
		    	fieldLabel: '<fmt:message key="image.text.content" />',
		    	labelWidth: 70,
				name: 'content',
	         	anchor: '95%',
	         	height:100
				},
				{
					xtype: 'imagefieldset',
					title: '<fmt:message key="image.text.imagePath"/>',
					collapsible: false,
					anchor: '98%',
					items: [{
						xtype: 'imagepanel',
						combineErrors: true,
						msgTarget: 'under',
						hideLabel: true,
						height: this.clientHeight,
						viewHeight: this.clientHeight,
						name:'imagePath'
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
       if(this.isEditor){
		this.buttons = [{
				text: '<fmt:message key="button.save"/>', 
				style: 'font-size: 14px;background: rgba(22, 155, 213, 1) none repeat scroll 0 0;border-color: rgba(22, 155, 213, 1);',
				scope: this,  
	        	handler: this.updateImageText
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
    
    
   updateImageText : function(){
   var rowsData=[];
   this.down('imagepanel[name=imagePath]').store.each(function(record) {
       		record.data.input_file = null;
            rowsData.push(record.data);    
      	}, this);
    	if(this.form.isValid()){
			Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
				if(e == 'yes'){
				var textId = this.getForm().findField('textId').getValue();
             		this.form.submit({
                 		waitMsg: 'Loading...',
                 		url: '<c:url value="/sign/saveImageText.json"/>',
               			scope: this,
               			method : 'post', 
               			params:{recodeGridJson: Ext.JSON.encode(rowsData),textId : textId},
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
    
    agree : function(){
    	var agreePanel = Ext.create('MyExt.couponManager.CoachAgreeFormPanel', {
    				id: 'coachAgreeFormPanel@CoachAgreeFormPanel',
     				title: '<fmt:message key="button.add"/>'});
		var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
	            if(agreePanel.form.isValid()){
	            	Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
							var coachId = this.getForm().findField('coachId').getValue();
						 	agreePanel.form.submit({
			                    waitMsg: 'Loading...',
			                    url: '<c:url value="/coach/checkCoach.json"/>',
			                    scope: this,
			                    params:{coachId: coachId},
			                    success: function(form, action) {
			                        var responseObject = Ext.JSON.decode(action.response.responseText);
			                       	if (responseObject.success == true){
		          						showSuccMsg(responseObject.msg);
		          						Ext.StoreMgr.get('waitStore').reload();
		          						popWin.close();
		          					}else{
		          						showFailMsg(responseObject.msg, 4);
		          					}
			                    },
			                    failure: function(form, action) {
				                    var responseObject = Ext.JSON.decode(action.response.responseText);
				                    showFailMsg(responseObject.msg, 4);
			                    }
			        		})
			        		
					
						
			        	}
			        }, this)
	        	}
			},
			scope: this
		},{
			text: '<fmt:message key="button.cancel"/>',
			handler : function(){popWin.close();},
			scope: this
		}];
		openWin('<fmt:message key="button.check"/>', agreePanel, buttons, 260, 160);
    
    },
    
    unagree : function(){
    	var unagreePanel = Ext.create('MyExt.couponManager.CoachUnagreeFormPanel', {
 //   				id: 'coachUnagreeFormPanel@CoachUnagreeFormPanel',
     				title: '<fmt:message key="button.add"/>'});
		var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
	            if(unagreePanel.form.isValid()){
	            	Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
							var coachId = this.getForm().findField('coachId').getValue();
						  	unagreePanel.form.submit({
			                    waitMsg: 'Loading...',
			                    url: '<c:url value="/coach/checkCoach.json"/>',
			                    scope: this,
			                    params:{coachId: coachId},
			                    success: function(form, action) {
			                        var responseObject = Ext.JSON.decode(action.response.responseText);
			                       	if (responseObject.success == true){
		          						showSuccMsg(responseObject.msg);
		          						Ext.StoreMgr.get('waitStore').reload();
		          						popWin.close();
		          					}else{
		          						showFailMsg(responseObject.msg, 4);
		          					}
			                    },
			                    failure: function(form, action) {
				                    var responseObject = Ext.JSON.decode(action.response.responseText);
				                    showFailMsg(responseObject.msg, 4);
			                    }
			        		})
			        		
			        	}
			        }, this)
	        	}
			},
			scope: this
		},{
			text: '<fmt:message key="button.cancel"/>',
			handler : function(){popWin.close();},
			scope: this
		}];
		openWin('<fmt:message key="button.check"/>', unagreePanel, buttons, 400, 200);
    
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