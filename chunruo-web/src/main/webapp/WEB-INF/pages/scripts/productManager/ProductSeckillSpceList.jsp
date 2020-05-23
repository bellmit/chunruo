<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('SeckillSpce', {
	extend: 'Ext.data.Model',
    fields: [
    	{name: 'objectId',			mapping: 'objectId',		type: 'int'},
    	{name: 'productId',			mapping: 'productId',		type: 'int'},
    	{name: 'productSpecId',		mapping: 'productSpecId',	type: 'int'},
    	{name: 'isSpceProduct',		mapping: 'isSpceProduct',	type: 'bool'},
    	{name: 'productCode',	 	mapping: 'productCode',		type: 'string'},
    	{name: 'productTags',	 	mapping: 'productTags',		type: 'string'},
		{name: 'name',	 			mapping: 'name',			type: 'string'},
		{name: 'stockNumber',	 	mapping: 'stockNumber',		type: 'int'},
		{name: 'priceWholesale',	mapping: 'priceWholesale',	type: 'string'},
		{name: 'priceRecommend',	mapping: 'priceRecommend',	type: 'string'},
		{name: 'priceCost',	 		mapping: 'priceCost',		type: 'string'},
		{name: 'seckillPrice',	 	mapping: 'seckillPrice',	type: 'string'},
		{name: 'seckillProfit',	 	mapping: 'seckillProfit',	type: 'string'},
		{name: 'seckillTotalStock',	mapping: 'seckillTotalStock',	type: 'int'},
		{name: 'seckillLimitNumber',mapping: 'seckillLimitNumber',type: 'int'},
		{name: 'seckillSalesNumber',mapping: 'seckillSalesNumber',	type: 'int'},
		{name: 'seckillLockNumber',	mapping: 'seckillLockNumber',  	type: 'int'}
    ]
});
	
Ext.define('MyExt.productManager.ProductSeckillSpceList', {
    extend : 'Ext.grid.GridPanel',
    requires : [ 'MyExt.productManager.ProductPicker'],
	region: 'center',
	autoScroll: true,   
	closable: true,
	multiSelect: true,
	columnLines: true,
	animCollapse: false,
    enableLocking: true,
    isEditor: false,
    productId: null,
    seckillSort:null,
    selModel: 'cellmodel',
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
	     	model: 'SeckillSpce',
	     	data: []
	    });
		 
		if(!this.isEditor){
			this.tbar = [{
	     		xtype: 'productPicker',
		    	fieldLabel: '<fmt:message key="product.wholesale.name" />',
		    	labelWidth: 60,
		    	width: 500,
				name: 'name',
				editable: false,
	         	allowBlank: false,
	         	anchor: '98%',
	         	typeAhead: true,
	         	listeners: {
	 				scope: this,
	 				itemClick : function(picker, record, item, index, e, eOpts){
	 					picker.setRawValue(record.data.name);
	 					this.productId = record.data.productId;
	 					this.loadData();
	 				}
	 			}
			},{
				xtype:'numberfield',
				labelWidth: 60,
				fieldLabel: '<fmt:message key="fx.channel.sort"/>',
				name: 'seckillSort',
				anchor:'98%',
				value:this.seckillSort
			}];
		}else{
		this.tbar = [{
				xtype:'numberfield',
				labelWidth: 60,
				fieldLabel: '<fmt:message key="fx.channel.sort"/>',
				name: 'seckillSort',
				anchor:'98%'
			}];
		}
		
		this.columns = [
			{text: '<fmt:message key="product.wholesale.wholesaleId"/>', dataIndex: 'objectId', width: 65, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="product.wholesale.productCode"/>', dataIndex: 'productCode', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="product.wholesale.productTags"/>', dataIndex: 'productTags', width: 100, sortable : true,
        		filter: {xtype: 'textfield'},
        	},
        	{text: '<fmt:message key="product.wholesale.seckillPrice"/>', dataIndex: 'seckillPrice', width: 65, sortable : true,
        		filter: {xtype: 'textfield'},
        		editor: 'textfield'
        	},
        	{text: '<fmt:message key="product.wholesale.seckillProfit"/>', dataIndex: 'seckillProfit', width: 65, sortable : true,
        		filter: {xtype: 'textfield'},
        		editor: 'textfield'
        	},
        	{text: '<fmt:message key="product.wholesale.seckillLimitNumber"/>', dataIndex: 'seckillLimitNumber', width: 65, sortable : true,
        		filter: {xtype: 'textfield'},
        		editor: 'textfield'
        	},
        	{text: '<fmt:message key="product.wholesale.seckillTotalStock"/>', dataIndex: 'seckillTotalStock', width: 65, sortable : true,
        		filter: {xtype: 'textfield'},
        		editor: 'textfield'
        	},
        	{text: '<fmt:message key="product.wholesale.seckillSalesNumber"/>', dataIndex: 'seckillSalesNumber', width: 65, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="product.wholesale.seckillLockNumber"/>', dataIndex: 'seckillLockNumber', width: 65, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="product.wholesale.priceWholesale"/>', dataIndex: 'priceWholesale', width: 65, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="product.wholesale.priceRecommend"/>', dataIndex: 'priceRecommend', width: 65, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="product.wholesale.priceCost"/>', dataIndex: 'priceCost', width: 65, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="product.wholesale.quantity"/>', dataIndex: 'stockNumber', width: 60, sortable : true,
        		filter: {xtype: 'textfield'}
        	}
        ];
        this.callParent(arguments);
    },
    
    loadData : function(){
    var sortObj = Ext.ComponentQuery.query('numberfield[name="seckillSort"]')[0];
    
    console.log("seckillSort"+sortObj.value);
    	Ext.Ajax.request({
       		url: '<c:url value="/seckill/getSeckillContByProductId.json"/>',
        	method: 'post',
			scope: this,
			params:{productId: this.productId},
         	success: function(response){
         		this.store.removeAll();
       			var responseObject = Ext.JSON.decode(response.responseText);
       			if (responseObject.success == true){
       				if(responseObject.seckillContList != null && responseObject.seckillContList.length > 0){
       					try{
       						this.store.removeAll();
       						for(var i = 0; i < responseObject.seckillContList.length; i ++){
       							this.store.insert(i, {
       								objectId: responseObject.seckillContList[i].objectId,
									productId: responseObject.seckillContList[i].productId,
									productSpecId: responseObject.seckillContList[i].productSpecId,
									productCode: responseObject.seckillContList[i].productCode,
									productTags: responseObject.seckillContList[i].productTags,
									seckillLimitNumber:responseObject.seckillContList[i].seckillLimitNumber,
									name: responseObject.seckillContList[i].name,
									stockNumber: responseObject.seckillContList[i].stockNumber,
									priceWholesale: responseObject.seckillContList[i].priceWholesale,
									priceRecommend: responseObject.seckillContList[i].priceRecommend,
									priceCost: responseObject.seckillContList[i].priceCost,
									seckillPrice: responseObject.seckillContList[i].seckillPrice,
									seckillProfit: responseObject.seckillContList[i].seckillProfit,
									seckillTotalStock: responseObject.seckillContList[i].seckillTotalStock,
									seckillSalesNumber: responseObject.seckillContList[i].seckillSalesNumber,
									seckillLockNumber: responseObject.seckillContList[i].seckillLockNumber
								});
								sortObj.setValue(responseObject.seckillContList[i].seckillSort);
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
