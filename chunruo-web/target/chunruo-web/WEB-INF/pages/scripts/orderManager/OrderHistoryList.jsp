<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>
Ext.define('OrderHistory', {
	extend: 'Ext.data.Model',
    idProperty: 'historyId',
    fields: [
		{name: 'historyId',		mapping: 'historyId',		type: 'int'},
		{name: 'userId',		mapping: 'userId',			type: 'int'},
		{name: 'name',			mapping: 'name',			type: 'string'},
		{name: 'message',		mapping: 'message',			type: 'string'},
		{name: 'createTime',	mapping: 'createTime',		type: 'string'}
    ]
});
Ext.define('MyExt.orderManager.OrderHistoryList', {
    extend : 'Ext.grid.GridPanel',
    alias: ['widget.orderHistoryList'],
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
        	model: 'OrderHistory'
		});
		
		this.columns = [
	    	{text: '<fmt:message key="history.historyId"/>', dataIndex: 'historyId', width: 65, sortable : true},
			{text: '<fmt:message key="history.name"/>', dataIndex: 'name', width: 120, sortable : true,
				renderer : function(val, metadata, record, rowIndex, columnIndex, store){ 
					metadata.tdAttr = Ext.String.format('data-qtip="{0}"', val); 
					return val;
				}
			},
			{text: '<fmt:message key="history.message"/>', dataIndex: 'message', flex: 1, sortable : true,
				renderer : function(val, metadata, record, rowIndex, columnIndex, store){ 
					metadata.tdAttr = Ext.String.format('data-qtip="{0}"', val); 
					return val;
				}
			},
			{text: '<fmt:message key="history.createTime"/>', dataIndex: 'createTime', width: 130, sortable : true}
        ]; 
       
    	this.callParent();
    },
    
});