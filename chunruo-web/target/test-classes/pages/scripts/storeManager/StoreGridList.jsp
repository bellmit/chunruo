<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('Store', {
	extend: 'Ext.data.Model',
	idProperty: 'userId',
    fields: [
    	{name: 'userId',	 mapping: 'userId',		type: 'string'},
		{name: 'nickname',	 mapping: 'nickname',	type: 'string'},
		{name: 'mobile',	 mapping: 'mobile',		type: 'string'},
		{name: 'storeName',	 mapping: 'storeName',	type: 'string'},
		{name: 'inviterCode',	mapping: 'inviterCode',	type: 'string'},
		{name: 'balance',	 	mapping: 'balance',		type: 'string'},
		{name: 'withdrawalAmount',	 mapping: 'withdrawalAmount',	type: 'string'},
		{name: 'sales',	 		mapping: 'sales',		type: 'string'},
		{name: 'income',	 	mapping: 'income',		type: 'string'},
		{name: 'storeMobile',	mapping: 'storeMobile',	type: 'string'},
		{name: 'linkman',	 	mapping: 'linkman',		type: 'string'},
		{name: 'topUserId',	 	mapping: 'topUserId',	type: 'string'},
		{name: 'topStoreName',	mapping: 'topStoreName',	type: 'string'},
		{name: 'bankName',	 	mapping: 'bankName',		type: 'string'},
		{name: 'bankCard',	 	mapping: 'bankCard',		type: 'string'},
		{name: 'bankCardUser',	 mapping: 'bankCardUser',	type: 'string'},
		{name: 'openingBank',	 mapping: 'openingBank',	type: 'string'},
		{name: 'createTime',	 mapping: 'createTime',		type: 'string'},
		{name: 'updateTime',	 mapping: 'updateTime',		type: 'string'}
    ]
});

Ext.define('MyExt.storeManager.StoreGridList', {
    extend : 'Ext.grid.GridPanel',
    requires : ['Ext.ux.grid.GridHeaderFilters','Ext.ux.grid.Exporter'],
   	region: 'center',
	autoScroll: true,  
	header: false, 
	closable: true,
	selType: 'checkboxmodel',
	multiSelect: true,
	columnLines: true,
	animCollapse: false,
    enableLocking: true,
	plugins: ['gridHeaderFilters','gridexporter'],
   	defaults: {  
    	stripeRows: true,
        enableTextSelection: true
    },

	initComponent : function(config) {
		Ext.apply(this, config);
        
        this.store = Ext.create('Ext.data.Store', {
        	pageSize: 50,
	        autoLoad: false,
			autoDestroy: true,
			sortOnLoad: true,
			remoteSort: true,
			model: 'Store',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/store/list.json"/>',
				reader: {
					type : 'json',
                	root: 'data',
                	totalProperty: 'totalCount'
            	}
			},
			sorters: [{
	            property: 'createTime',
	            direction: 'desc'
	        }]
		});
		
		this.columns = [
        	{text: '<fmt:message key="store.userId"/>', dataIndex: 'userId', width: 70, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.nickname"/>', dataIndex: 'nickname', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.mobile"/>', dataIndex: 'mobile', width: 100, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.storeName"/>', dataIndex: 'storeName', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.inviterCode"/>', dataIndex: 'inviterCode', width: 80, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.balance"/>', dataIndex: 'balance', width: 100, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.withdrawalAmount"/>', dataIndex: 'withdrawalAmount', width: 100, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.sales"/>', dataIndex: 'sales', width: 100, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.income"/>', dataIndex: 'income', width: 100, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.storeMobile"/>', dataIndex: 'storeMobile', width: 100, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.linkman"/>', dataIndex: 'linkman', width: 90, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.topUserId"/>', dataIndex: 'topUserId', width: 80, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.topStoreName"/>', dataIndex: 'topStoreName', width: 120, sortable : false,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.bankName"/>', dataIndex: 'bankName', width: 140, sortable : false,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.bankCardUser"/>', dataIndex: 'bankCardUser', width: 90, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.bankCard"/>', dataIndex: 'bankCard', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.openingBank"/>', dataIndex: 'openingBank', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.createTime"/>', dataIndex: 'createTime', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.updateTime"/>', dataIndex: 'updateTime', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	}
        ];
        
        this.bbar = new Ext.PagingToolbar({
        	pageSize: 50,
			store: this.store,
			autoheigth: true,
			displayInfo: true,
			displayMsg: '<fmt:message key="ajax.record"/>',
			emptyMsg: '<fmt:message key="ajax.no.record"/>',
			scope: this,
			items: ['-',{ 
				xtype: 'numberfield', 
				width: 120, 
				labelWidth: 65,
				value: 50, 
				minValue: 1, 
				fieldLabel: '<fmt:message key="ajax.record.size"/>',
                allowBlank: false,
               	scope: this,
                listeners:{
                	scope: this,
               		change: function (field, newValue, oldValue) {
                    	var number = parseInt(newValue);
                        if (isNaN(number) || !number || number < 1) {
                        	number = 50;
                           	Field.setValue(number);
                        }
                       	this.store.pageSize = number;
                       	this.store.load();
                   	}
               	}
        	}]	
		});
    	this.callParent();
    	this.gsm = this.getSelectionModel();
    
    	<jkd:haveAuthorize access="/store/list.json">
    	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.filters),
			});
	    }, this);
	    this.store.load();
	    </jkd:haveAuthorize>
    }
});