<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('ProductGiftForm', {
	extend: 'Ext.data.Model',
	idProperty: 'giftId',
    fields: [
    	{name: 'giftId',	        mapping: 'giftId',	         type: 'int'},
		{name: 'productSpecId',	    mapping: 'productSpecId',	 type: 'int'},
		{name: 'headerImage',	    mapping: 'headerImage',      type: 'string'},
		{name: 'productDesc',	    mapping: 'productDesc',	     type: 'string'},
		{name: 'productTags',	    mapping: 'productTags',	     type: 'string'},
		{name: 'productName',	    mapping: 'productName',	     type: 'string'},
		{name: 'yearNumber',	    mapping: 'yearNumber',	     type: 'int'},
		{name: 'createTime',	    mapping: 'createTime',	     type: 'string'},
		{name: 'updateTime',	    mapping: 'updateTime',	     type: 'string'},
    ]
});
	  
Ext.define('MyExt.productManager.ProductQuestionTabPanel', {
    extend : 'Ext.panel.Panel',
    requires : ['MyExt.productManager.ProductAnswerListPanel'],
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
	        items:['->',{
	        	iconCls: 'tab_open',
	        	handler: function(){
	        		this.questionList.hide();
	        		this.setWidth(this.clientWidth);
	        	}, 
	        	scope: this
	        },'-',{
	        	iconCls: 'tab_close',
	        	handler: function(){
	        		this.hide();
	        		this.questionList.show();
	        		this.setWidth(this.initWidth);
	        	}, 
	        	scope: this
	        }]
	    });
	    
	    this.productAnswerListPanel =  Ext.create('MyExt.productManager.ProductAnswerListPanel', {
        	header: false, 
        	closable: false
        });
		this.items = [this.productAnswerListPanel];
    	this.callParent(arguments);
    },
    
    transferData : function(tabPanel, record, clientWidth){
    	this.clientWidth = clientWidth;
    	this.tabPanel = tabPanel;
    	this.productAnswerListPanel.transferData(record);
    }
});