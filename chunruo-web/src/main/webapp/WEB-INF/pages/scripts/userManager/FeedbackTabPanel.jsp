<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('FeedbackForm', {
	extend: 'Ext.data.Model',
	idProperty: 'feedbackId',
    fields: [
		{name: 'feedbackId',	 	mapping: 'feedbackId',	          type: 'int'},
    	{name: 'userId',	 		mapping: 'userId',	              type: 'int'},
    	{name: 'userName',	 		mapping: 'userName',	       type: 'string'},
    	{name: 'mobile',	 		mapping: 'mobile',		       type: 'string'},
		{name: 'ftype',	 			mapping: 'ftype',		       type: 'string'},
		{name: 'content',	 		mapping: 'content',	           type: 'string'},
		{name: 'uuid',	 		    mapping: 'uuid',	           type: 'string'},
		{name: 'userIp',	 	    mapping: 'userIp',		       type: 'string'},
		{name: 'isReply',	 	    mapping: 'isReply',            type: 'string'},
		{name: 'isPushUser',	    mapping: 'isPushUser',		   type: 'string'},
		{name: 'replyMsg',	 		mapping: 'replyMsg',	       type: 'string'},
		{name: 'createTime',	    mapping: 'createTime',		   type: 'string'},
		{name: 'updateTime',	 	mapping: 'updateTime',	       type: 'string'},
		
	]
});

Ext.define('MyExt.userManager.FeedbackTabPanel', {
    extend : 'Ext.panel.Panel',
    requires : ['MyExt.userManager.FeedbackFormPanel'],
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
				xtype: 'feedbackForm',
				title: '<fmt:message key="user.feedback.info"/>'
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
    	this.tabPanel.down('feedbackForm').loadRecord(Ext.create('FeedbackForm'));
    	this.tabPanel.down('feedbackForm').record = this.record;
    	this.tabPanel.down('feedbackForm').tabPanel = this;
    	Ext.Ajax.request({
       		url: '<c:url value="/feedback/getFeedbackById.json"/>',
        	method: 'post',
			scope: this,
			params:{feedbackId: this.record.data.feedbackId},
         	success: function(response){
         	//	Ext.util.Cookies.set("uzen_feedback_id", this.record.data.feedbackId);
         		this.tabPanel.tabPanelMask.hide();
       			var responseObject = Ext.JSON.decode(response.responseText);
       			if (responseObject.success == true){
       				if(responseObject.feedback != null){
       					var recordData = Ext.create('FeedbackForm', responseObject.feedback);
       					this.tabPanel.down('feedbackForm').loadRecord(recordData); 
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