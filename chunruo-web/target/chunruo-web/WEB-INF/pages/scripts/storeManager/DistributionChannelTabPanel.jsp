<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>
Ext.define('Channel', {
	extend: 'Ext.data.Model',
	idProperty: 'channelId',
    fields: [
    	{name: 'channelId',	 		mapping: 'channelId',			type: 'int'},
		{name: 'channelName',	 	mapping: 'channelName',			type: 'int'},
		{name: 'status',	 		mapping: 'status',				type: 'int'},
		{name: 'sort',	 			mapping: 'sort',				type: 'string'},
		{name: 'createTime',	 	mapping: 'createTime',			type: 'string',	
			convert:function(value){ 
				if(value) {
					var createTime = Ext.Date.format(new Date(value),"Y-m-d H:i:s");
					return createTime; 
				}   
			}
		},
		{name: 'updateTime',	 	mapping: 'updateTime',			type: 'string',	
			convert:function(value){ 
				if(value) {
					var createTime = Ext.Date.format(new Date(value),"Y-m-d H:i:s");
					return createTime; 
				}   
			}}
    ]
});

Ext.define('MyExt.storeManager.DistributionChannelTabPanel', {
    extend : 'Ext.panel.Panel',
    requires : ['MyExt.storeManager.DistributionChannelFormPanel','MyExt.storeManager.DistributionChannePagesList'],
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
	        		
	        		this.setWidth(this.clientWidth);
	        	}, 
	        	scope: this
	        },'-',{
	        	iconCls: 'tab_close',
	        	handler: function(){
	        		this.hide();
	        		
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
				xtype: 'channelForm',
				title: '<fmt:message key="fx.channel.info"/>'
			},{
				xtype: 'channelPageList',
				title: '<fmt:message key="fx.channel.page"/>'
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
    	this.tabPanel.down('channelForm').loadRecord(Ext.create('Channel'));
    	this.tabPanel.down('channelPageList').store.removeAll();
    	
    	
    	Ext.Ajax.request({
       		url: '<c:url value="/channel/getChannelById.json"/>',
        	method: 'post',
			scope: this,
			params:{channelId: this.record.data.channelId},
         	success: function(response){
         		this.tabPanel.tabPanelMask.hide();
       			var responseObject = Ext.JSON.decode(response.responseText);
       			if (responseObject.success == true){
       				if(responseObject.channel != null){
       					var recordData = Ext.create('Channel', responseObject.channel);
       					this.tabPanel.down('channelForm').loadRecord(recordData);
       				}
       				
       				if(responseObject.channelPageList != null && responseObject.channelPageList.length > 0){
       					for(var i = 0; i < responseObject.channelPageList.length; i ++){
       						var channelPageData = Ext.create('ChannelPage', responseObject.channelPageList[i]);
       						this.tabPanel.down('channelPageList').store.insert(i, channelPageData);
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