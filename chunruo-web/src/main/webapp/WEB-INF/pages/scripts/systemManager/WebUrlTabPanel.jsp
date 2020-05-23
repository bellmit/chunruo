<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('WebUrlForm', {
	extend: 'Ext.data.Model',
	idProperty: 'configId',
    fields: [
		{name: 'configId',		    mapping: 'configId',			type: 'int'},
		{name: 'name',	     	    mapping: 'name',             	type: 'string'},
		{name: 'isEnable',	     	mapping: 'isEnable',          	type: 'bool'},
		{name: 'url',	            mapping: 'url',                 type: 'string'},
		{name: 'methodName',	    mapping: 'methodName',          type: 'string'},
		{name: 'createTime',	 	mapping: 'createTime',			type: 'string'},
		{name: 'updateTime',	 	mapping: 'updateTime',		  	type: 'string'}
     ]
});

Ext.define('MyExt.systemManager.WebUrlTabPanel', {
    extend : 'Ext.panel.Panel',
    requires : ['MyExt.systemManager.WebUrlFormPanel'],
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
				xtype: 'webUrlForm',
				isEditor: true,
				title: '<fmt:message key="web.url.detail"/>'
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
    	this.tabPanel.down('webUrlForm').tabPanel = this;
    	this.tabPanel.down('webUrlForm').loadRecord(Ext.create('WebUrlForm'));
    	this.tabPanel.down('webUrlForm').loadRecord(Ext.create('WebUrlForm'));
                                                    
    	Ext.Ajax.request({
       		url: '<c:url value="/webUrl/getWebUrlById.json"/>',
        	method: 'post',
			scope: this,
			params:{configId: this.record.data.configId},
         	success: function(response){
         		this.tabPanel.tabPanelMask.hide();
       			var responseObject = Ext.JSON.decode(response.responseText);
       			if (responseObject.success == true){
       				if(responseObject.data != null){
       					this.recordData = responseObject.data;
       					this.recordObject = Ext.create('WebUrlForm', this.recordData);
       					this.tabPanel.down('webUrlForm').loadRecord(this.recordObject);
       				}
       			   }else{
       				 //showFailMsg(responseObject.message, 4);
       			 }
			}
    	}, this);
    },
    
   
});
