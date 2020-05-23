<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('GiftProductForm', {
	extend: 'Ext.data.Model',
	idProperty: 'giftProductId',
    fields: [
		{name: 'giftProductId',		mapping: 'giftProductId',		          type: 'int'},
		{name: 'productId',		    mapping: 'productId',		              type: 'int'},
		{name: 'productSpecId',	    mapping: 'productSpecId',                 type: 'int'},
		{name: 'isSpecProduct',	    mapping: 'isSpecProduct',		          type: 'bool'},
		{name: 'stockNumber',	 	mapping: 'stockNumber',                   type: 'int'},
		{name: 'isEnable',	        mapping: 'isEnable',                      type: 'bool'},
		{name: 'name',	            mapping: 'name',                          type: 'string'},
		{name: 'productName',	    mapping: 'productName',                   type: 'string'},
		{name: 'productTags',	    mapping: 'productTags',                   type: 'string'},
	    {name: 'productCode',	    mapping: 'productCode',                   type: 'string'},
	    {name: 'productSku',	    mapping: 'productSku',                    type: 'string'},
		{name: 'createTime',	 	mapping: 'createTime',		              type: 'string'},
		{name: 'updateTime',	 	mapping: 'updateTime',		              type: 'string'}
    ]
});

Ext.define('MyExt.productManager.GiftProductTabPanel', {
    extend : 'Ext.panel.Panel',
    requires : ['MyExt.productManager.GiftProductFormPanel'],
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
				xtype: 'giftProductForm',
				isEditor: true,
				title: '<fmt:message key="discovery.detail"/>'
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
    	this.tabPanel.down('giftProductForm').tabPanel = this;
    	this.tabPanel.down('giftProductForm').loadRecord(Ext.create('GiftProductForm'));
    	this.tabPanel.down('giftProductForm').loadRecord(Ext.create('GiftProductForm'));
                                                    
    	Ext.Ajax.request({
       		url: '<c:url value="/giftProduct/getGiftProductById.json"/>',
        	method: 'post',
			scope: this,
			params:{giftProductId: this.record.data.giftProductId},
         	success: function(response){
         		this.tabPanel.tabPanelMask.hide();
       			var responseObject = Ext.JSON.decode(response.responseText);
       			if (responseObject.success == true){
       				if(responseObject.data != null){
       					this.recordData = responseObject.data;
       					this.recordObject = Ext.create('GiftProductForm', this.recordData);
       					this.tabPanel.down('giftProductForm').loadRecord(this.recordObject);
       					
       					var couponIdObj = Ext.ComponentQuery.query('textfield[name="couponId"]')[0];
					    var productNameObj = Ext.ComponentQuery.query('productSpecPicker[name="productName"]')[0];
					    var stockNumberObj = Ext.ComponentQuery.query('textfield[name="stockNumber"]')[0];
							    
							    
					    if(this.recordData.type == "1"){
						   couponIdObj.hide();
						   productNameObj.show();
						   stockNumberObj.show();
					   }else if(this.recordData.type == "2"){
						   couponIdObj.show();
						   productNameObj.hide();
						   stockNumberObj.hide();
						}
       				}

       			   }else{
       				 //showFailMsg(responseObject.message, 4);
       			 }
			}
    	}, this);
    },
    
   
});
