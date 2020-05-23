<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('ProductGroup', {
	extend: 'Ext.data.Model',
    fields: [
    	{name: 'objectId',				mapping: 'objectId',			type: 'string'},
    	{name: 'groupId',				mapping: 'groupId',				type: 'int'},
    	{name: 'productGroupId',		mapping: 'productGroupId',		type: 'int'},
    	{name: 'productId',				mapping: 'productId',			type: 'int'},
    	{name: 'productSpecId',			mapping: 'productSpecId',		type: 'int'},
    	{name: 'productCode',	 		mapping: 'productCode',			type: 'string'},
		{name: 'name',	 				mapping: 'name',				type: 'string'},
		{name: 'priceCost',				mapping: 'priceCost',			type: 'string'},
		{name: 'priceWholesale',		mapping: 'priceWholesale',		type: 'string'},
		{name: 'priceRecommend',		mapping: 'priceRecommend',		type: 'string'},
		{name: 'priceCost',	 			mapping: 'priceCost',			type: 'string'},
		{name: 'stockNumber',	 		mapping: 'stockNumber',			type: 'int'},
		{name: 'groupPriceRecommend',	mapping: 'groupPriceRecommend',	type: 'string'},
		{name: 'groupPriceWholesale',	mapping: 'groupPriceWholesale',	type: 'string'},
		{name: 'saleTimes',				mapping: 'saleTimes',			type: 'int'},
		{name: 'productTags',			mapping: 'productTags',			type: 'string'}
    ]
});

Ext.define('MyExt.productManager.ProductGroupList', {
    extend : 'Ext.grid.GridPanel',
    requires : [ 'MyExt.productManager.ProductPicker'],
    alias: ['widget.productGroupList'],
	region: 'center',
	autoScroll: true,   
	closable: true,
	selType: 'checkboxmodel',
	multiSelect: true,
	columnLines: true,
	animCollapse: false,
    enableLocking: true,
    features: [{
    	ftype: 'groupingsummary',
        groupHeaderTpl: '{name}',
        showSummaryRow: false
   	}],
   	plugins: {
    	ptype: 'cellediting',
        clicksToEdit: 1
    },
    viewConfig: {
        stripeRows: true,
        enableTextSelection: true
    },
    
	initComponent : function(config) {
		Ext.apply(this, config);	
		
		this.store = Ext.create('Ext.data.Store', {
	    	autoDestroy: true,
	     	model: 'ProductGroup',
	     	groupField: 'name',
	     	data: []
	    });
		 
		this.tbar = [{
        	text: '<fmt:message key="button.delete"/>', 
        	iconCls: 'delete', 	
        	handler: this.deleteProduct,
        	scope: this
        },'-',{
        	iconCls: 'add',
     		xtype: 'productPicker',
	    	fieldLabel: '<fmt:message key="product.wholesale.name" />',
	    	labelWidth: 60,
	    	width: 400,
			editable: false,
         	anchor: '98%',
         	typeAhead: true,
         	listeners: {
 				scope: this,
 				itemClick : function(picker, record, item, index, e, eOpts){
 					picker.setRawValue(record.data.name);
 					this.loadData(record.data.productId);
 				}
 			}
		}];
		
		this.columns = [
			{text: '<fmt:message key="product.wholesale.wholesaleId"/>', dataIndex: 'objectId', width: 65, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="product.wholesale.productCode"/>', dataIndex: 'productCode', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="product.wholesale.groupPriceCost"/>', dataIndex: 'groupPriceCost', width: 65, sortable : true,
        		filter: {xtype: 'textfield'},
        		editor: 'textfield'
        	},
        	{text: '<fmt:message key="product.wholesale.groupPriceWholesale"/>', dataIndex: 'groupPriceWholesale', width: 65, sortable : true,
        		filter: {xtype: 'textfield'},
        		editor: 'textfield'
        	},
        	{text: '<fmt:message key="product.wholesale.groupPriceRecommend"/>', dataIndex: 'groupPriceRecommend', width: 65, sortable : true,
        		filter: {xtype: 'textfield'},
        		editor: 'textfield'
        	},
        	{text: '<fmt:message key="product.wholesale.saleTimes"/>', dataIndex: 'saleTimes', width: 65, sortable : true,
        		filter: {xtype: 'textfield'},
        		editor: 'textfield'
        	},
        	{text: '<fmt:message key="product.wholesale.groupProductTags"/>', dataIndex: 'productTags', width: 130, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="product.wholesale.priceCost"/>', dataIndex: 'priceCost', width: 65, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="product.wholesale.priceWholesale"/>', dataIndex: 'priceWholesale', width: 65, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="product.wholesale.quantity"/>', dataIndex: 'stockNumber', width: 60, sortable : true,
        		filter: {xtype: 'textfield'}
        	}
        ];
        
        this.callParent(arguments);
        this.gsm = this.getSelectionModel();
    },
    
    getProductGroupRowsData : function(){
    	var productGroupRowsData = [];
    	this.store.each(function(record) {
	   		productGroupRowsData.push(record.data);    
	    }, this);
	    return productGroupRowsData;
    },
    
    deleteProduct : function(){
    	var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){
			if(!Ext.Array.contains(rowsData, records[i].data.productId)){
				rowsData.push(records[i].data.productId);
			}				
		}
		
		Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="delete.confirm"/>', function(e){
			if(e == 'yes'){
            	for(var i = 0; i < rowsData.length; i ++){
            		this.store.remove(this.store.queryRecords('productId', rowsData[i]));
            	}
  			}
  		}, this)
    },
    
    loadData : function(productId){
    	Ext.Ajax.request({
       		url: '<c:url value="/group/addGroupContByProductId.json"/>',
        	method: 'post',
			scope: this,
			params:{productId: productId, productGroupId: this.productGroupId},
         	success: function(response){
       			var responseObject = Ext.JSON.decode(response.responseText);
       			if (responseObject.success == true){
       				if(responseObject.productGroupList != null && responseObject.productGroupList.length > 0){
       					try{
       						this.store.remove(this.store.queryRecords('productId', responseObject.productId));
       						for(var i = 0; i < responseObject.productGroupList.length; i ++){
       							this.store.insert(i, {
       								objectId: responseObject.productGroupList[i].objectId,
									productId: responseObject.productGroupList[i].productId,
									productSpecId: responseObject.productGroupList[i].productSpecId,
									productCode: responseObject.productGroupList[i].productCode,
									name: responseObject.productGroupList[i].name,
									stockNumber: responseObject.productGroupList[i].stockNumber,
									priceCost: responseObject.productGroupList[i].priceCost,
									priceWholesale: responseObject.productGroupList[i].priceWholesale,
									priceRecommend: responseObject.productGroupList[i].priceRecommend,
									groupPriceCost: responseObject.productGroupList[i].groupPriceCost,
									groupPriceRecommend: responseObject.productGroupList[i].groupPriceRecommend,
									groupPriceWholesale: responseObject.productGroupList[i].groupPriceWholesale,
									saleTimes: responseObject.productGroupList[i].saleTimes,
									productTags: responseObject.productGroupList[i].productTags
								});
       						}
       					}catch(e){
    					}
       				}
       			}else{
					showFailMsg(responseObject.message, 4);
				}
       		}
       	})
    }
});