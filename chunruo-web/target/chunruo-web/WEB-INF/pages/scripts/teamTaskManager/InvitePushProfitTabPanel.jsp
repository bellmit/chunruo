<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('TeamTaskRuleForm', {
	extend: 'Ext.data.Model',
	idProperty: 'explainId',
    fields: [
		{name: 'explainId',	    mapping: 'explainId',	    type: 'int'},
		{name: 'level',	 		mapping: 'level',		    type: 'int'},
		{name: 'type',	        mapping: 'type',	        type: 'int'},
		{name: 'imagePath',	    mapping: 'imagePath',       type: 'string'},
		{name: 'content',	    mapping: 'content',         type: 'string'},
		{name: 'question',	    mapping: 'question',	    type: 'string'},
		{name: 'sort',	        mapping: 'sort',	        type: 'int'},
		{name: 'createTime',	mapping: 'createTime',	    type: 'string'},
		{name: 'updateTime',	mapping: 'updateTime',	    type: 'string'},
    ]
});

Ext.define('MyExt.teamTaskManager.InvitePushProfitTabPanel', {
    extend : 'Ext.panel.Panel',
    requires : ['MyExt.teamTaskManager.TeamTaskRuleForm'],
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
				xtype: 'teamTaskRuleForm',
				isEditor: true,
				isProfit:true,
				isQuestion:true,
				isInvite:true,
				explainId:this.explainId,
				level:2,
				title: '<fmt:message key="discovery.creater.detail"/>'
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
    	this.tabPanel.down('teamTaskRuleForm').tabPanel = this;
    	this.tabPanel.down('teamTaskRuleForm').loadRecord(Ext.create('TeamTaskRuleForm'));
    	this.tabPanel.down('teamTaskRuleForm').loadRecord(Ext.create('TeamTaskRuleForm'));
                                                    
    	Ext.Ajax.request({
       		url: '<c:url value="/teamTask/getTeamTaskRuleById.json"/>',
        	method: 'post',
			scope: this,
			params:{explainId: this.record.data.explainId},
         	success: function(response){
         		this.tabPanel.tabPanelMask.hide();
       			var responseObject = Ext.JSON.decode(response.responseText);
       			if (responseObject.success == true){
       				if(responseObject.data != null){
       					this.recordData = responseObject.data;
       					this.recordObject = Ext.create('TeamTaskRuleForm', this.recordData);
       					this.tabPanel.down('teamTaskRuleForm').loadRecord(this.recordObject);
       				}

	       				var imagePanel = this.tabPanel.down('teamTaskRuleForm').down('[xtype=imagepanel]');
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
       			   }else{
       				 //showFailMsg(responseObject.message, 4);
       			 }
			}
    	}, this);
    },
    
   
});
