<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('UserRechargeForm', {
	extend: 'Ext.data.Model',
	idProperty: 'recordId',
    fields: [
		{name: 'recordId',	 		mapping: 'recordId',		type: 'int'},
		{name: 'applicant',	 	    mapping: 'applicant',	    type: 'string'},
		{name: 'amount',			mapping: 'amount',			type: 'string'},
		{name: 'userId',	 		mapping: 'userId',			type: 'string'},
		{name: 'status',			mapping: 'status',			type: 'int'},
		{name: 'profitNotice',	 	mapping: 'profitNotice',	type: 'string'},
		{name: 'nickName',	 	    mapping: 'nickName',	    type: 'string'},
		{name: 'reason',		    mapping: 'reason',	        type: 'string'},
		{name: 'attachmentPath',    mapping: 'attachmentPath',	type: 'string'},
		{name: 'completeTime',	    mapping: 'completeTime',    type: 'string'},
		{name: 'createTime',	    mapping: 'createTime',		type: 'string'},
		{name: 'updateTime',	    mapping: 'updateTime',		type: 'string'}
		
	]
});

Ext.define('MyExt.rechargeManager.UserRechargeTabPanel', {
    extend : 'Ext.panel.Panel',
    requires : ['MyExt.rechargeManager.UserRechargeFormPanel'],
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
	        		this.refundList.hide();
	        		this.setWidth(this.clientWidth);
	        	}, 
	        	scope: this
	        },'-',{
	        	iconCls: 'tab_close',
	        	handler: function(){
	        		this.hide();
	        		this.refundList.show();
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
				xtype: 'userRechargeForm',
				title: '<fmt:message key="user.recharge.info"/>'
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
    	this.tabPanel.down('userRechargeForm').loadRecord(Ext.create('UserRechargeForm'));
    //	Ext.ComponentQuery.query('textarea[name="remarks"]')[0].setReadOnly(true);
    	Ext.Ajax.request({
       		url: '<c:url value="/userRecharge/getUserRechargeById.json"/>',
        	method: 'post',
			scope: this,
			params:{recordId: this.record.data.recordId},
         	success: function(response){
         		this.tabPanel.tabPanelMask.hide();
       			var responseObject = Ext.JSON.decode(response.responseText);
       			if (responseObject.success == true){
       				if(responseObject.userRecharge != null){
       					var recordData = Ext.create('UserRechargeForm', responseObject.userRecharge);
       					this.tabPanel.down('userRechargeForm').loadRecord(recordData);
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