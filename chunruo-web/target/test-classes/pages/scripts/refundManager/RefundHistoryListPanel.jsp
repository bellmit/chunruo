<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('RefundHistory', {
	extend: 'Ext.data.Model',
    idProperty: 'historyId',
    fields: [
		{name: 'historyId',		mapping: 'historyId',		type: 'int'},
		{name: 'refundId',		mapping: 'refundId',		type: 'int'},
		{name: 'storeName',		mapping: 'storeName',		type: 'string'},
		{name: 'title',		    mapping: 'title',			type: 'string'},
		{name: 'content',		mapping: 'content',			type: 'string'},
		{name: 'adminName',		mapping: 'adminName',		type: 'string'},
		{name: 'createTime',	mapping: 'createTime',		type: 'string'}
    ]
});


Ext.define('MyExt.refundManager.RefundHistoryListPanel', {
    extend : 'Ext.grid.GridPanel',
    alias: ['widget.refundHistoryList'],
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
        	model: 'RefundHistory'
		});
		
	this.columns = [
	    	{text: '<fmt:message key="refund.history.historyId"/>', dataIndex: 'historyId', width: 65, sortable : true},
//			{text: '<fmt:message key="refund.history.storeName"/>', dataIndex: 'storeName', width: 120, sortable : true},
			{text: '<fmt:message key="refund.history.title"/>', dataIndex: 'title', width: 120, sortable : true},
			{text: '<fmt:message key="refund.history.content"/>', dataIndex: 'content', flex: 1, sortable : true,
				renderer : function(val, metadata, record, rowIndex, columnIndex, store){ 
					metadata.tdAttr = Ext.String.format('data-qtip="{0}"', val); 
					return val;
				}
			},
			{text: '<fmt:message key="refund.history.adminName"/>', dataIndex: 'adminName', width: 80, sortable : true},
			{text: '<fmt:message key="refund.history.createTime"/>', dataIndex: 'createTime', width: 130, sortable : true}
        ]; 
    	this.callParent();
    }
});