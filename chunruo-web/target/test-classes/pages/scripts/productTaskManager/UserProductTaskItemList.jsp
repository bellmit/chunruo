<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('UserProductTaskItem', {
	extend: 'Ext.data.Model',
	idProperty: 'itemId',
    fields: [
		{name: 'itemId',	 		mapping: 'itemId',			type: 'int'},
		{name: 'orderId',	 		mapping: 'orderId',			type: 'int'},
		{name: 'userId',		    mapping: 'userId',		    type: 'int'},
		{name: 'orderItemId',	    mapping: 'orderItemId',	    type: 'int'},
		{name: 'productId',	 		mapping: 'productId',		type: 'int'},
		{name: 'taskId',		    mapping: 'taskId',		    type: 'int'},
		{name: 'quantity',		    mapping: 'quantity',		type: 'int'},
		{name: 'status',		    mapping: 'status',		    type: 'string'},
		{name: 'createTime',	    mapping: 'createTime',	    type: 'string'},
		{name: 'updateTime',	    mapping: 'updateTime',	    type: 'string'}
    ]
});

Ext.define('MyExt.productTaskManager.UserProductTaskItemList', {
    extend : 'Ext.grid.GridPanel',
    alias: ['widget.userProductItemList'],
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
			model: 'UserProductTaskItem'
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
	    	{text: '<fmt:message key="user.product.task.item.itemId"/>', dataIndex: 'itemId', width: 70, sortable : true},
	    	{text: '<fmt:message key="user.product.task.item.status"/>', dataIndex: 'status', width: 65, sortable : true,
        		align: 'center',
        		renderer: function(value, meta, record) {    
			       	if(value == 1) {
			            return '<span style="color:green;"><fmt:message key="user.product.task.item.succ"/></span>';
			        }else if(value == 2){
			            return '<span style="color:red;"><b><fmt:message key="user.product.task.item.fail"/></b></span>';
			        }else {
			        	return '<span style="color:red;"><b><fmt:message key="user.product.task.item.wait"/></b></span>';
			        }
			        
			   	},
        		filter: {
					xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			         store: Ext.create('Ext.data.Store', {
						autoDestroy: true,
						model: 'InitModel',
						data: [
						    {id: '2', name: '<fmt:message key="user.product.task.awarded"/>'},
							{id: '1', name: '<fmt:message key="user.product.task.item.fail"/>'},
							{id: '0', name: '<fmt:message key="user.product.task.item.wait"/>'},
						]
					}),
			        queryMode: 'local',
			        typeAhead: true
				}
        	},
	    	{text: '<fmt:message key="user.product.task.item.orderId"/>', dataIndex: 'orderId', width: 70, sortable : true},
	    	{text: '<fmt:message key="user.product.task.item.userId"/>', dataIndex: 'userId', width: 70, sortable : true},
	    	{text: '<fmt:message key="user.product.task.item.orderItemId"/>', dataIndex: 'orderItemId', width: 120, sortable : true},
        	{text: '<fmt:message key="user.product.task.item.productId"/>', dataIndex: 'productId', width: 250, sortable : true},
        	{text: '<fmt:message key="user.product.task.item.taskId"/>', dataIndex: 'taskId', width: 80, sortable : true},
	    	{text: '<fmt:message key="user.product.task.item.quantity"/>', dataIndex: 'quantity', width: 80, sortable : true},
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