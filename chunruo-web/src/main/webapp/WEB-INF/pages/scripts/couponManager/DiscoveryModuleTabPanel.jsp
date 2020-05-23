<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('DiscoveryModuleForm', {
	extend: 'Ext.data.Model',
	idProperty: 'moduleId',
    fields: [
		{name: 'moduleId',		    mapping: 'moduleId',		              type: 'int'},
		{name: 'name',	     	    mapping: 'name',                          type: 'string'},
		{name: 'isEnable',	     	mapping: 'isEnable',                      type: 'bool'},
		{name: 'isRecommend',	    mapping: 'isRecommend',                   type: 'bool'},
		{name: 'backgroundImage',	mapping: 'backgroundImage',               type: 'string'},
		{name: 'introduce',	     	mapping: 'introduce',           type: 'string'},
		{name: 'createTime',	 	mapping: 'createTime',		              type: 'string'},
		{name: 'updateTime',	 	mapping: 'updateTime',		              type: 'string'}
    ]
});

Ext.define('MyExt.couponManager.DiscoveryModuleTabPanel', {
    extend : 'Ext.panel.Panel',
    requires : ['MyExt.couponManager.DiscoveryModuleFormPanel'],
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
				xtype: 'discoveryModuleForm',
				isEditor: true,
				title: '<fmt:message key="discovery.module.detail"/>'
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
    	this.tabPanel.down('discoveryModuleForm').tabPanel = this;
    	this.tabPanel.down('discoveryModuleForm').loadRecord(Ext.create('DiscoveryModuleForm'));
    	this.tabPanel.down('discoveryModuleForm').loadRecord(Ext.create('DiscoveryModuleForm'));
                                                    
    	Ext.Ajax.request({
       		url: '<c:url value="/discovery/getModuleById.json"/>',
        	method: 'post',
			scope: this,
			params:{moduleId: this.record.data.moduleId},
         	success: function(response){
         		this.tabPanel.tabPanelMask.hide();
       			var responseObject = Ext.JSON.decode(response.responseText);
       			if (responseObject.success == true){
       				if(responseObject.data != null){
       					this.recordData = responseObject.data;
       					this.recordObject = Ext.create('DiscoveryModuleForm', this.recordData);
       					this.tabPanel.down('discoveryModuleForm').loadRecord(this.recordObject);
       					
       					var imagePanels = this.tabPanel.down('discoveryModuleForm').down('[xtype=imagepanel]');
	       				imagePanels.store.removeAll();
	       				if(responseObject.backgroundImageList != null && responseObject.backgroundImageList.length > 0){
	       					try{
	       						imagePanels.store.removeAll();
	       						for(var i = 0; i < responseObject.backgroundImageList.length; i ++){
	       							imagePanels.store.insert(i, {
	       						     	fileId: responseObject.backgroundImageList[i].fileId,
										fileName: responseObject.backgroundImageList[i].fileName,
										fileType: responseObject.backgroundImageList[i].fileType,
										filePath: responseObject.backgroundImageList[i].filePath,
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
    	}, this);
    },
    
   
});
