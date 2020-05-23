<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('RightPopupForm', {
	extend: 'Ext.data.Model',
	idProperty: 'popupId',
    fields: [
    	{name: 'popupId',			mapping: 'popupId',		    type: 'int'},
		{name: 'pushLevel',		    mapping: 'pushLevel',		type: 'int'},
		{name: 'isInvitePage',		mapping: 'isInvitePage',    type: 'bool'},
		{name: 'imageUrl',	 		mapping: 'imageUrl',		type: 'string'},
		{name: 'beginTime',	 	    mapping: 'beginTime',		type: 'string'},
		{name: 'endTime',	 	    mapping: 'endTime',		    type: 'string'},
		{name: 'isEnable',			mapping: 'isEnable',		type: 'bool'},
		{name: 'createTime',	 	mapping: 'createTime',		type: 'string'},
		{name: 'updateTime',	 	mapping: 'updateTime',		type: 'string'},
    ]
});
	  
Ext.define('MyExt.systemManager.RightPopupTabPanel', {
    extend : 'Ext.panel.Panel',
    requires : ['MyExt.systemManager.RightPopupFormPanel'],
    header: false,
	closable: true,
	columnLines: true,
	animCollapse: true,
	layout: 'bproduct',
	recordData: null,
    viewConfig: {
        stripeRows: true,
        enableTextSelection: true
    },

	initComponent : function(config) {
		this.tabPanelMask = new Ext.LoadMask(this, {msg:"Please wait..."});
		Ext.apply(this, config);
		this.initWidth = this.width;
	    
	    this.tbar = Ext.create('Ext.Toolbar', { 
	   		scope: this,
	        items:[{
	        	text: '<fmt:message key="button.refresh"/>', 
	            iconCls: 'refresh', 	
	            scope: this,
	        	handler: function(){
	        		this.loadData();
	        	}
	        },'->',{
	        	iconCls: 'tab_open',
	        	handler: function(){
	        		this.productList.hide();
	        		this.setWidth(this.clientWidth);
	        	}, 
	        	scope: this
	        },'-',{
	        	iconCls: 'tab_close',
	        	handler: function(){
	        		this.hide();
	        		this.productList.show();
	        		this.setWidth(this.initWidth);
	        	}, 
	        	scope: this
	        }]
	    });
	    
	    this.tabPanel = Ext.create('Ext.TabPanel', { 
	    	activeTab : 0,
    		enableTabScroll : true,		
			layoutOnTabChange : true,
			tabWidth : 120,
			items:[{
				xtype: 'rightPopupForm',
				isEditor: true,
				title: '<fmt:message key="home.popup.detail"/>'
			}]
		});
		this.items = [this.tabPanel];
    	this.callParent(arguments);
    },
    
    transferData : function(tabPanel, record, clientWidth){
    	this.clientWidth = clientWidth;
    	this.tabPanel = tabPanel;
    	this.record = record;
    	this.loadData();
    },
    
    loadData : function(){
    	this.tabPanel.tabPanelMask.show();
    	this.tabPanel.down('rightPopupForm').tabPanel = this;
    	this.tabPanel.down('rightPopupForm').loadRecord(Ext.create('RightPopupForm'));
    	this.tabPanel.down('rightPopupForm').loadRecord(Ext.create('RightPopupForm'));
                                                    
    	Ext.Ajax.request({
       		url: '<c:url value="/homePopup/getHomePopupById.json"/>',
        	method: 'post',
			scope: this,
			params:{popupId: this.record.data.popupId},
         	success: function(response){
         		this.tabPanel.tabPanelMask.hide();
       			var responseObject = Ext.JSON.decode(response.responseText);
       			if (responseObject.success == true){
       				if(responseObject.data != null){
       					this.recordData = responseObject.data;
       					this.recordObject = Ext.create('RightPopupForm', this.recordData);
       					this.tabPanel.down('rightPopupForm').loadRecord(this.recordObject);
       					
       				}
       				var rightPopupForm = this.tabPanel.down('rightPopupForm');
       				
       				var imagePanel = rightPopupForm.down('[xtype=imagepanel]');
	       				imagePanel.store.removeAll();
	       				if(responseObject.imageList != null && responseObject.imageList.length > 0){
	       					try{
	       						imagePanel.store.removeAll();
	       						for(var i = 0; i < responseObject.imageList.length; i ++){
	       							imagePanel.store.insert(i, {
	       						     	fileId: responseObject.imageList[i].fileId,
										fileName: responseObject.imageList[i].fileName,
										fileType: responseObject.imageList[i].fileType,
										filePath: responseObject.imageList[i].filePath,
										fileState: 200
									});
	       						}
	       					}catch(e){
	    					}
	       				}
	       				
	       				
	       			var productPickerObj = rightPopupForm.down('systemProductPicker[name=product]');
       				var productIdObj = rightPopupForm.down('hiddenfield[name=productId]');
       				if(responseObject.data.jumpPageType == '2'){
       				    productPickerObj.setRawValue(responseObject.name);
       				    productIdObj.setValue(responseObject.data.content);
       				}else{
       				   productPickerObj.setRawValue('');
       				   productIdObj.setValue('');
       				}
       				
       				var fxPagePickerObj = rightPopupForm.down('fxPagePicker[name=fxPage]');
       				var fxPageIdObj = rightPopupForm.down('hiddenfield[name=pageId]');
       				if(responseObject.data.jumpPageType == '3'){
       				    fxPagePickerObj.setRawValue(responseObject.name);
       				    fxPageIdObj.setValue(responseObject.data.content);
       				}else{
	       				fxPagePickerObj.setRawValue('');
	       				fxPageIdObj.setValue('');
       				}
       				
       				var discoveryPickerObj = rightPopupForm.down('discoveryPicker[name=discovery]');
       				var discoveryIdObj = rightPopupForm.down('hiddenfield[name=discoveryId]');
       				if(responseObject.data.jumpPageType == '9'){
       				    discoveryPickerObj.setRawValue(responseObject.name);
       				    discoveryIdObj.setValue(responseObject.data.content);
       				}else{
	       				discoveryPickerObj.setRawValue('');
	       				discoveryIdObj.setValue('');
       				}
       				
       				var discoveryModulePickerObj = rightPopupForm.down('discoveryModulePicker[name=discoveryModule]');
       				var discoveryModuleIdObj = rightPopupForm.down('hiddenfield[name=moduleId]');
       				if(responseObject.data.jumpPageType == '10'){
       				    discoveryModulePickerObj.setRawValue(responseObject.name);
       				    discoveryModuleIdObj.setValue(responseObject.data.content);
       				}else{
	       				discoveryModulePickerObj.setRawValue('');
	       				discoveryModuleIdObj.setValue('');
       				}
       				
       				var discoveryCreaterPickerObj = rightPopupForm.down('discoveryCreaterPicker[name=discoveryCreater]');
       				var discoveryCreaterIdObj = rightPopupForm.down('hiddenfield[name=createrId]');
       				if(responseObject.data.jumpPageType == '10'){
       				    discoveryCreaterPickerObj.setRawValue(responseObject.name);
       				    discoveryCreaterIdObj.setValue(responseObject.data.content);
       				}else{
	       				discoveryCreaterPickerObj.setRawValue('');
	       				discoveryCreaterIdObj.setValue('');
       				}
       				
       				var brandPickerObj = rightPopupForm.down('productBrandPicker[name=brandName]');
       				var brandIdObj = rightPopupForm.down('hiddenfield[name=brandId]');
       				if(responseObject.data.jumpPageType == '13'){
       				    brandPickerObj.setRawValue(responseObject.name);
       				    brandIdObj.setValue(responseObject.data.content);
       				}else{
	       				brandPickerObj.setRawValue('');
	       				brandIdObj.setValue('');
       				}
       				
       				var webUrlObj = rightPopupForm.down('textfield[name=webUrl]');
       				if(responseObject.data.jumpPageType == '8'){
       				    webUrlObj.setValue(responseObject.data.content);
       				}else{
       				    webUrlObj.setValue('');
       				}
       			}else{
       				//showFailMsg(responseObject.message, 4);
       			}
			}
    	}, this);
    }
});