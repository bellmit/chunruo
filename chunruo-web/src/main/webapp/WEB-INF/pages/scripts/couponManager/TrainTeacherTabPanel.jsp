<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('TrainTeacherForm', {
	extend: 'Ext.data.Model',
	idProperty: 'teacherId',
    fields: [
		{name: 'teacherId',		     mapping: 'teacherId',		              type: 'int'},
		{name: 'nickName',		     mapping: 'nickName',		              type: 'string'},
		{name: 'wechatNumber',		 mapping: 'wechatNumber',		          type: 'string'},
		{name: 'headerImage',		 mapping: 'headerImage',		          type: 'string'},
		{name: 'qrCode',	         mapping: 'qrCode',                       type: 'string'},
		{name: 'createTime',	 	 mapping: 'createTime',		              type: 'string'},
		{name: 'updateTime',	 	 mapping: 'updateTime',		              type: 'string'}
    ]
});

Ext.define('MyExt.couponManager.TrainTeacherTabPanel', {
    extend : 'Ext.panel.Panel',
    requires : ['MyExt.couponManager.TrainTeacherFormPanel'],
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
				xtype: 'trainTeacherForm',
				isEditor: true,
				title: '<fmt:message key="invites.courtesy.detail"/>'
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
    	this.tabPanel.down('trainTeacherForm').tabPanel = this;
    	this.tabPanel.down('trainTeacherForm').loadRecord(Ext.create('TrainTeacherForm'));
    	this.tabPanel.down('trainTeacherForm').loadRecord(Ext.create('TrainTeacherForm'));
                                                    
    	Ext.Ajax.request({
       		url: '<c:url value="/trainTeacher/getTrainTeacherById.json"/>',
        	method: 'post',
			scope: this,
			params:{teacherId: this.record.data.teacherId},
         	success: function(response){
         		this.tabPanel.tabPanelMask.hide();
       			var responseObject = Ext.JSON.decode(response.responseText);
       			if (responseObject.success == true){
       				if(responseObject.data != null){
       					this.recordData = responseObject.data;
       					this.recordObject = Ext.create('TrainTeacherForm', this.recordData);
       					this.tabPanel.down('trainTeacherForm').loadRecord(this.recordObject);
       				}

	       				var headerImagePanel = this.tabPanel.down('trainTeacherForm').down('[xtype=imagepanel]');
	       				headerImagePanel.store.removeAll();
	       				if(responseObject.headerImageList != null && responseObject.headerImageList.length > 0){
	       					try{
	       						headerImagePanel.store.removeAll();
	       						for(var i = 0; i < responseObject.headerImageList.length; i ++){
	       							headerImagePanel.store.insert(i, {
	       						     	fileId: responseObject.headerImageList[i].fileId,
										fileName: responseObject.headerImageList[i].fileName,
										fileType: responseObject.headerImageList[i].fileType,
										filePath: responseObject.headerImageList[i].filePath,
										fileState: 200
									});
	       						}
	       					}catch(e){
	    					}
	       				}
	       				
	       				var imagePathPanel = this.tabPanel.down('trainTeacherForm').down('[xtype=imagepanels]');
	       				imagePathPanel.store.removeAll();
	       				if(responseObject.imagePathList != null && responseObject.imagePathList.length > 0){
	       					try{
	       						imagePathPanel.store.removeAll();
	       						for(var i = 0; i < responseObject.imagePathList.length; i ++){
	       							imagePathPanel.store.insert(i, {
	       						     	fileId: responseObject.imagePathList[i].fileId,
										fileName: responseObject.imagePathList[i].fileName,
										fileType: responseObject.imagePathList[i].fileType,
										filePath: responseObject.imagePathList[i].filePath,
										fileState: 200
									});
	       						}
	       					}catch(e){
	    					}
	       				}
	       				
       			   }else{
       				 //showFailMsg(responseObject.message, 4);
       			 }
			}
    	}, this);
    },
    
   
});
