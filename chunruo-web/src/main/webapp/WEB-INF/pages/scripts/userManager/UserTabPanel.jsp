<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('User', {
	extend: 'Ext.data.Model',
	idProperty: 'userId',
    fields: [
    	{name: 'userId',	 	mapping: 'userId',		type: 'int'},
    	{name: 'storeId',	 	mapping: 'storeId',		type: 'int'},
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

Ext.define('MyExt.userManager.UserTabPanel', {
    extend : 'Ext.panel.Panel',
    requires : ['MyExt.userManager.UserFormPanel'],
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
    	this.tabPanel.down('userForm').loadRecord(Ext.create('User'));
    	
    	this.tabPanel.down('userForm').record = this.record;
    	this.tabPanel.down('userForm').tabPanel = this;
    	
    	Ext.Ajax.request({
       		url: '<c:url value="/user/getUserById.json"/>',
        	method: 'post',
			scope: this,
			params:{userId: this.record.data.userId},
         	success: function(response){
         		Ext.util.Cookies.set("uzen_user_id", this.record.data.userId);
         		this.tabPanel.tabPanelMask.hide();
       			var responseObject = Ext.JSON.decode(response.responseText);
       			if (responseObject.success == true){
       				if(responseObject.user != null){
       					var recordData = Ext.create('User', responseObject.user);
       					this.tabPanel.down('userForm').loadRecord(recordData); 
       				}
       			}
			}
    	})
    },
    
    cleanSearch : function(){
    	this.keywordField.setRawValue();
		this.store.loadPage(1);
    }
});