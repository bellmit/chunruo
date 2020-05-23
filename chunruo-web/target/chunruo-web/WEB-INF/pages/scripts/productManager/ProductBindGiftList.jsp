<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('GiftProduct', {
	extend: 'Ext.data.Model',
    fields: [
    	{name: 'giftProductId',			mapping: 'giftProductId',		type: 'int'},
    	{name: 'productId',				mapping: 'productId',			type: 'int'},
    	{name: 'productSpecId',			mapping: 'productSpecId',		type: 'int'},
    	{name: 'productCode',	 		mapping: 'productCode',			type: 'string'},
		{name: 'name',	 			    mapping: 'name',			    type: 'string'},
		{name: 'productName',			mapping: 'productName',			type: 'string'},
		{name: 'number',		        mapping: 'number',		        type: 'string'},
		{name: 'stockNumber',	 		mapping: 'stockNumber',			type: 'int'},
		{name: 'productTags',	        mapping: 'productTags',	        type: 'string'}
    ]
});

Ext.define('MyExt.productManager.ProductBindGiftList', {
    extend : 'Ext.grid.GridPanel',
    requires : [ 'MyExt.productManager.GiftProductPicker'],
    alias: ['widget.productBindGiftList'],
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
	     	model: 'GiftProduct',
	     	groupField: 'name',
	     	data: []
	    });
		 
		this.tbar = [{
        	text: '<fmt:message key="button.delete"/>', 
        	iconCls: 'delete', 	
        	handler: this.deleteProduct,
        	scope: this
        },'-',{
        	icon: 'add', 
     		xtype: 'giftProductPicker',
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
 					this.loadData(record.data.giftProductId);
 				}
 			}
		}];
		
		this.columns = [
			{text: '<fmt:message key="gift.product.giftProductId"/>', dataIndex: 'giftProductId', width: 65, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="gift.product.productName"/>', dataIndex: 'productName', width: 300, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="product.wholesale.productCode"/>', dataIndex: 'productCode', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="gift.product.productTags"/>', dataIndex: 'productTags', width: 65, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="gift.product.number"/>', dataIndex: 'number', width: 65, sortable : true,
        		filter: {xtype: 'textfield'},
        		editor: 'textfield'
        	},
        	{text: '<fmt:message key="gift.product.stockNumber"/>', dataIndex: 'stockNumber', width: 65, sortable : true,
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
    
    loadData : function(giftProductId){
    	Ext.Ajax.request({
       		url: '<c:url value="/giftProduct/getGiftProductById.json"/>',
        	method: 'post',
			scope: this,
			params:{giftProductId: giftProductId},
         	success: function(response){
       			var responseObject = Ext.JSON.decode(response.responseText);
       			if (responseObject.success == true){
       				if(responseObject.giftProductList != null && responseObject.giftProductList.length > 0){
       					try{
       						this.store.remove(this.store.queryRecords('giftProductId', responseObject.giftProductId));
       						for(var i = 0; i < responseObject.giftProductList.length; i ++){
       							this.store.insert(i, {
       								giftProductId: responseObject.giftProductList[i].giftProductId,
									productId: responseObject.giftProductList[i].productId,
									productSpecId: responseObject.giftProductList[i].productSpecId,
									productCode: responseObject.giftProductList[i].productCode,
									productName: responseObject.giftProductList[i].productName,
									stockNumber: responseObject.giftProductList[i].stockNumber,
									number: responseObject.giftProductList[i].number,
									productTags: responseObject.giftProductList[i].productTags,
									name: responseObject.giftProductList[i].name
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