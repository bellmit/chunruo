<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('User', {
	extend: 'Ext.data.Model',
	idProperty: 'userId',
    fields: [
    	{name: 'userId',	 	mapping: 'userId',		type: 'int'},
		{name: 'nickname',	 	mapping: 'nickname',	type: 'string'},
		{name: 'password',	 	mapping: 'password',	type: 'string'},
		{name: 'mobile',	 	mapping: 'mobile',		type: 'string'},
		{name: 'registerIp',	mapping: 'registerIp',	type: 'string'},
		{name: 'lastIp',	 	mapping: 'lastIp',		type: 'string'},
		{name: 'loginCount',	mapping: 'loginCount',	type: 'string'},
		{name: 'status',	 	mapping: 'status',		type: 'string'},
		{name: 'introduce',	 	mapping: 'introduce',	type: 'string'},
		{name: 'avatar',	 	mapping: 'avatar',		type: 'string'},
		{name: 'sex',	 		mapping: 'sex',			type: 'string'},
		{name: 'province',		mapping: 'province',	type: 'string'},
		{name: 'city',	 		mapping: 'city',		type: 'string'},
		{name: 'areaCode',		mapping: 'areaCode',	type: 'string'},
		{name: 'realName',		mapping: 'realName',	type: 'string'},
		{name: 'identityNo',	mapping: 'identityNo',	type: 'string'},
		{name: 'topUserId',	    mapping: 'topUserId',	type: 'string'},
		{name: 'createTime',	mapping: 'createTime',	type: 'string'},
		{name: 'updateTime',	mapping: 'updateTime',	type: 'string'}
    ]
});

Ext.define('MyExt.userManager.ApplyAgentTabPanel', {
    extend : 'Ext.panel.Panel',
    requires : ['MyExt.userManager.ApplyAgentFormPanel','MyExt.userManager.UserFormPanel'],
    header: false,
	closable: true,
	columnLines: true,
	animCollapse: true,
	layout: 'border',
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
	        	handler: function(){
	        		this.loadData();
	        	}, 
	        	scope: this
	        },'->',{
	        	iconCls: 'tab_open',
	        	handler: function(){
	        		this.userList.hide();
	        		this.setWidth(this.clientWidth);
	        	}, 
	        	scope: this
	        },'-',{
	        	iconCls: 'tab_close',
	        	handler: function(){
	        		this.hide();
	        		this.userList.show();
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
				xtype: 'applyAgentForm',
				title: '<fmt:message key="user.applyAgent.info"/>'
			},{
				xtype: 'userForm',
				title: '<fmt:message key="user.info"/>'
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
    	this.tabPanel.down('applyAgentForm').tabPanel = this;
    	this.tabPanel.down('userForm').loadRecord(Ext.create('User'));
        this.tabPanel.down('applyAgentForm').loadRecord(Ext.create('ApplyAgent'));
        
        this.tabPanel.down('userForm').record = this.record;
    	this.tabPanel.down('userForm').tabPanel = this;
    	
    	Ext.Ajax.request({
       		url: '<c:url value="/applyAgent/getApplyAgentById.json"/>',
        	method: 'post',
			scope: this,
			params:{userId: this.record.data.userId,applyId: this.record.data.applyId},
         	success: function(response){
         		this.tabPanel.tabPanelMask.hide();
       			var responseObject = Ext.JSON.decode(response.responseText);
       			if (responseObject.success == true){
       				if(responseObject.applyAgent != null){
       					var recordData = Ext.create('User', responseObject.userInfo);
       					this.tabPanel.down('userForm').loadRecord(recordData);   
       					
       					var srecordData = Ext.create('ApplyAgent', responseObject.applyAgent);
       					this.tabPanel.down('applyAgentForm').loadRecord(srecordData);  	
       									
       				var imagePanel = this.tabPanel.down('applyAgentForm').down('[xtype=imagepanel]');
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
       				}
       				
       				
       				
       			}else{
       				//showFailMsg(responseObject.message, 4);
       			}
			}
    	})
    },
    
    cleanSearch : function(){
    	this.keywordField.setRawValue();
		this.store.loadPage(1);
    }
});