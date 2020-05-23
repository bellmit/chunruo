<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('VoterForm', {
	extend: 'Ext.data.Model',
	idProperty: 'voterId',
    fields: [
    	{name: 'voterId',			mapping: 'voterId',		   type: 'int'},
		{name: 'userId',		    mapping: 'userId',		       type: 'int'},
		{name: 'name',	            mapping: 'name',          type: 'string'},
		{name: 'mobile',	        mapping: 'mobile',		       type: 'string'},
		{name: 'address',	 	    mapping: 'address',            type: 'string'},
		{name: 'createTime',	 	mapping: 'createTime',		   type: 'string'},
		{name: 'updateTime',	 	mapping: 'updateTime',		   type: 'string'},
    ]
});
	  
Ext.define('MyExt.activityManager.VoterTabPanel', {
    extend : 'Ext.panel.Panel',
    requires : ['MyExt.activityManager.VoterFormPanel'],
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
				xtype: 'voterForm',
				isEditor: false,
				title: '<fmt:message key="voter.detail"/>'
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
    	this.tabPanel.down('voterForm').tabPanel = this;
    	this.tabPanel.down('voterForm').loadRecord(Ext.create('VoterForm'));
    	this.tabPanel.down('voterForm').loadRecord(Ext.create('VoterForm'));
                                                    
    	Ext.Ajax.request({
       		url: '<c:url value="/activity/getVoterById.json"/>',
        	method: 'post',
			scope: this,
			params:{voterId: this.record.data.voterId},
         	success: function(response){
         		this.tabPanel.tabPanelMask.hide();
       			var responseObject = Ext.JSON.decode(response.responseText);
       			if (responseObject.success == true){
       			var voterForm = this.tabPanel.down('voterForm');
       				if(responseObject.data != null){
       					this.recordData = responseObject.data;
       					this.recordObject = Ext.create('VoterForm', this.recordData);
       					this.tabPanel.down('voterForm').loadRecord(this.recordObject);
       					
       				}
       				
       			}else{
       				//showFailMsg(responseObject.message, 4);
       			}
			}
    	}, this);
    }
});