<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('CouponForm', {
	extend: 'Ext.data.Model',
	idProperty: 'userCouponId',
    fields: [
    	{name: 'userCouponId',		mapping: 'userCouponId',		type: 'int'},
		{name: 'couponId',			mapping: 'couponId',			type: 'int'},
		{name: 'userId',			mapping: 'userId',				type: 'int'},
		{name: 'couponNo',	 		mapping: 'couponNo',			type: 'string'},
		{name: 'couponTaskId',	 	mapping: 'couponTaskId',		type: 'string'},
		{name: 'couponStatus',	 	mapping: 'couponStatus',		type: 'string'},
		{name: 'receiveTime',		mapping: 'receiveTime',			type: 'string'},
		{name: 'effectiveTime',		mapping: 'effectiveTime',		type: 'string'},
		{name: 'timeOutStr',		mapping: 'timeOutStr',			type: 'string'},
		{name: 'createTime',		mapping: 'createTime',			type: 'string'},
		{name: 'updateTime',	 	mapping: 'updateTime',			type: 'string'},
		{name: 'useRangeType',	    mapping: 'useRangeType',	    type: 'string'},
    ]
});
	  
Ext.define('MyExt.couponManager.CouponTabPanel', {
    extend : 'Ext.panel.Panel',
    requires : ['MyExt.couponManager.CouponFormPanel'],
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
				xtype: 'couponForm',
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
    	this.tabPanel.down('couponForm').tabPanel = this;
    	this.tabPanel.down('couponForm').loadRecord(Ext.create('CouponForm'));
    	this.tabPanel.down('couponForm').loadRecord(Ext.create('CouponForm'));
                                                    
    	Ext.Ajax.request({
       		url: '<c:url value="/coupon/getCouponById.json"/>',
        	method: 'post',
			scope: this,
			params:{couponId: this.record.data.couponId},
         	success: function(response){
         		this.tabPanel.tabPanelMask.hide();
       			var responseObject = Ext.JSON.decode(response.responseText);
       			if (responseObject.success == true){
       				if(responseObject.data != null){
       					this.recordData = responseObject.data;
       					this.recordObject = Ext.create('CouponForm', this.recordData);
       					this.tabPanel.down('couponForm').loadRecord(this.recordObject);
       					

       				}
       			}else{
       				//showFailMsg(responseObject.message, 4);
       			}
			}
    	}, this);
    }
});