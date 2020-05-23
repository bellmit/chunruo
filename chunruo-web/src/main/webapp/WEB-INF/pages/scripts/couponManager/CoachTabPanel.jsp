<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('CoachForm', {
	extend: 'Ext.data.Model',
	idProperty: 'coachId',
    fields: [
    	{name: 'coachId',			mapping: 'coachId',		       type: 'int'},
		{name: 'userId',		    mapping: 'userId',		       type: 'int'},
		{name: 'isCompleteTask',	mapping: 'isCompleteTask',     type: 'bool'},
		{name: 'name',	        	mapping: 'name',			   type: 'string'},
		{name: 'identityNo',	 	mapping: 'identityNo',         type: 'string'},
		{name: 'identityFront',	 	mapping: 'identityFront',      type: 'string'},
		{name: 'coachImage',	 	mapping: 'coachImage',         type: 'string'},
		{name: 'status',	 	    mapping: 'status',             type: 'int'},
		{name: 'reason',	 	    mapping: 'reason',             type: 'string'},
		{name: 'subUserIds',	 	mapping: 'subUserIds',         type: 'string'},
		{name: 'createTime',	 	mapping: 'createTime',		   type: 'string'},
		{name: 'updateTime',	 	mapping: 'updateTime',		   type: 'string'},
    ]
});
	  
Ext.define('MyExt.couponManager.CoachTabPanel', {
    extend : 'Ext.panel.Panel',
    requires : ['MyExt.couponManager.CoachFormPanel'],
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
				xtype: 'coachForm',
				isEditor: false,
				title: '<fmt:message key="coupon.detail"/>'
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
    	this.tabPanel.down('coachForm').tabPanel = this;
    	this.tabPanel.down('coachForm').loadRecord(Ext.create('CoachForm'));
    	this.tabPanel.down('coachForm').loadRecord(Ext.create('CoachForm'));
                                                    
    	Ext.Ajax.request({
       		url: '<c:url value="/coach/getCoachById.json"/>',
        	method: 'post',
			scope: this,
			params:{coachId: this.record.data.coachId},
         	success: function(response){
         		this.tabPanel.tabPanelMask.hide();
       			var responseObject = Ext.JSON.decode(response.responseText);
       			if (responseObject.success == true){
       			var coachForm = this.tabPanel.down('coachForm');
       				if(responseObject.data != null){
       					this.recordData = responseObject.data;
       					this.recordObject = Ext.create('CoachForm', this.recordData);
       					this.tabPanel.down('coachForm').loadRecord(this.recordObject);
       					
       				}
       				
       				
       				var imagePanel = coachForm.down('imagepanel[name=image]');
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
    }
});