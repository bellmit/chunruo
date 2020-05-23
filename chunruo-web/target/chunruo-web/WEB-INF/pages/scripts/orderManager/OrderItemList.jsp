<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('OrderItem', {
	extend: 'Ext.data.Model',
	idProperty: 'itemId',
    fields: [
		{name: 'itemId',	 		mapping: 'itemId',			type: 'int'},
		{name: 'orderId',	 		mapping: 'orderId',			type: 'int'},
		{name: 'subOrderId',		mapping: 'subOrderId',		type: 'int'},
		{name: 'sort',	 			mapping: 'sort',			type: 'int'},
		{name: 'productId',	 		mapping: 'productId',		type: 'int'},
		{name: 'productCode',		mapping: 'productCode',		type: 'string'},
		{name: 'productName',		mapping: 'productName',		type: 'string'},
		{name: 'groupProductName',	mapping: 'groupProductName',type: 'string'},
		{name: 'productSku',		mapping: 'productSku',		type: 'string'},
		{name: 'productTags',		mapping: 'productTags',		type: 'string'},
		{name: 'wareHouseName',		mapping: 'wareHouseName',	type: 'string'},
		{name: 'quantity',	 		mapping: 'quantity',		type: 'int'},
		{name: 'amount',	 		mapping: 'amount',			type: 'string'},
		{name: 'discountAmount',	mapping: 'discountAmount',	type: 'string'},
		{name: 'price',	 			mapping: 'price',			type: 'string'},
		{name: 'weight',			mapping: 'weight',			type: 'string'},
		{name: 'profit',	 		mapping: 'profit',			type: 'string'},
		{name: 'topProfit',	 		mapping: 'topProfit',			type: 'string'},
		{name: 'priceCost',	 		mapping: 'priceCost',		type: 'string'},
		{name: 'priceWholesale',	mapping: 'priceWholesale',	type: 'string'},
		{name: 'isGroupProduct',	mapping: 'isGroupProduct',	type: 'bool'},
		{name: 'groupProductId',	mapping: 'groupProductId',	type: 'int'},
		{name: 'groupQuantity',		mapping: 'groupQuantity',	type: 'int'},
		{name: 'saleTimes',			mapping: 'saleTimes',		type: 'int'},
		{name: 'isMainGroupItem',	mapping: 'isMainGroupItem',	type: 'bool'},
		{name: 'groupUniqueBatch',	mapping: 'groupUniqueBatch',type: 'string'},
		{name: 'isSeckillProduct',	mapping: 'isSeckillProduct',type: 'bool'},
		{name: 'isGiftProduct',	    mapping: 'isGiftProduct',	type: 'bool'},
		{name: 'createTime',	mapping: 'createTime',	type: 'string'},
		{name: 'updateTime',	mapping: 'updateTime',	type: 'string'}
    ]
});

Ext.define('MyExt.orderManager.OrderItemList', {
    extend : 'Ext.grid.GridPanel',
    alias: ['widget.orderItemList'],
    requires : [],
	region: 'center',
	autoScroll: true,   
	multiSelect: true,
	columnLines: true,
	animCollapse: false,
    enableLocking: true,
    viewConfig: {
        stripeRows: true,
        enableTextSelection: true
    },
    
	initComponent : function(config) {
		Ext.apply(this, config);
		
    	this.store = Ext.create('Ext.data.Store', {
			autoDestroy: true,
			model: 'OrderItem'
		});
		
		this.rendererStutsStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: 1, name: '<fmt:message key="button.yes"/>'},
        		{id: 0, name: '<fmt:message key="button.no"/>'}
        	]
        });
		
		this.columns = [
	    	{text: '<fmt:message key="order.item.itemId"/>', dataIndex: 'itemId', width: 70, sortable : true},
	    	{text: '<fmt:message key="order.orderAmount"/>', dataIndex: 'amount', width: 70, sortable : true},
        	{text: '<fmt:message key="order.item.productName"/>', dataIndex: 'productName', width: 250, sortable : true,
        		renderer : function(val, metadata, record, rowIndex, columnIndex, store){ 
					metadata.tdAttr = Ext.String.format('data-qtip="{0}"', val); 
					return val;
				}
        	},
	    	{text: '<fmt:message key="order.item.price"/>', dataIndex: 'price', width: 90, sortable : true},
	    	{text: '<fmt:message key="order.item.quantity"/>', dataIndex: 'quantity', width: 80, sortable : true},
	    	{text: '<fmt:message key="order.item.priceCost"/>', dataIndex: 'priceCost', width: 90, sortable : true},
	    	{text: '<fmt:message key="order.item.topProfit"/>', dataIndex: 'topProfit', width: 90, sortable : true},
        	{text: '<fmt:message key="order.item.productId"/>', dataIndex: 'productId', width: 70, sortable : true},
        	{text: '<fmt:message key="order.item.weight"/>', dataIndex: 'weight', width: 70, sortable : true},
        	{text: '<fmt:message key="order.item.createTime"/>', dataIndex: 'createTime', width: 140, sortable : true},
        	{text: '<fmt:message key="order.item.updateTime"/>', dataIndex: 'updateTime', width: 140, sortable : true}
        ]; 
    	this.callParent();
    },
    
    rendererStuts : function(val){
		if(val == true) {
            return '<b><fmt:message key="button.yes"/></b>';
        }else{
            return '<fmt:message key="button.no"/>';
        }
	}
});