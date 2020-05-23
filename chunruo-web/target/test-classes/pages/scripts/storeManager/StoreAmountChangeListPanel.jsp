<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('Record', {
	extend: 'Ext.data.Model',
	idProperty: 'id',
    fields: [
    	{name: 'id',				mapping: 'id',				type: 'int'},
    	{name: 'storeName',			mapping: 'storeName',		type: 'string'},
		{name: 'type',				mapping: 'type',			type: 'int'},
		{name: 'beforeAmount',		mapping: 'beforeAmount',	type: 'string'},
		{name: 'changeAmount',		mapping: 'changeAmount',	type: 'string'},
		{name: 'afterAmount',		mapping: 'afterAmount',		type: 'string'},
		{name: 'createTime',		mapping: 'createTime',		type: 'string'},
		{name: 'updateTime',		mapping: 'updateTime',		type: 'string'}
    ]
});

Ext.define('MyExt.storeManager.StoreAmountChangeListPanel', {
    extend : 'Ext.panel.Panel',
    requires : ['Ext.ux.grid.GridHeaderFilters','Ext.ux.grid.Exporter'],
    header: false,
	closable: true,
	columnLines: true,
	animCollapse: true,
	layout: 'border',
   	defaults: {
    	split: true,    
        collapsible: true,
        collapseDirection: 'left'
    },

	initComponent : function(config) {
		Ext.apply(this, config);
        
        this.store = Ext.create('Ext.data.Store', {
        	pageSize: 50,
	        autoLoad: false,
			autoDestroy: true,
			sortOnLoad: true,
			remoteSort: true,
			model: 'Record',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/store/storeAmountChangeList.json"/>',
				reader: {
					type : 'json',
                	root: 'data',
                	totalProperty: 'totalCount'
            	}
			},
			sorters: [{
	            property: 'id',
	            direction: 'desc'
	        }]
		});
		
		this.changeAmountTypeStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: 2, name: '<fmt:message key="storeAmountChange.type_check"/>'},
        		{id: 1, name: '<fmt:message key="storeAmountChange.type_drawal"/>'}
			]
		});
		
		this.columns = [
			{text: '<fmt:message key="storeAmountChange.id"/>', dataIndex: 'id',   hidden:true 
	    	},
	    	{text: '<fmt:message key="storeAmountChange.storeName"/>', dataIndex: 'storeName', width: 260,  sortable : true,
				filter: {xtype: 'textfield'}
	    	},
	    	{text: '<fmt:message key="storeAmountChange.userId"/>', dataIndex: 'userId', width: 90,  sortable : true,
				filter: {xtype: 'textfield'}
	    	},
	    	{text: '<fmt:message key="storeAmountChange.type"/>', dataIndex: 'type', width: 90, sortable : true,
        		align: 'center',
        		renderer: this.rendererChangeAmountType,
        		filter: {
					xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.changeAmountTypeStore,
			        queryMode: 'local',
			        typeAhead: true
				}
        	},
	    	{text: '<fmt:message key="storeAmountChange.beforeAmount"/>', dataIndex: 'beforeAmount', width: 260,  sortable : true,
				filter: {xtype: 'textfield'}
	    	},
	    	{text: '<fmt:message key="storeAmountChange.changeAmount"/>', dataIndex: 'changeAmount', width: 260,  sortable : true,
				filter: {xtype: 'textfield'}
	    	},
	    	{text: '<fmt:message key="storeAmountChange.afterAmount"/>', dataIndex: 'afterAmount', width: 260,  sortable : true,
				filter: {xtype: 'textfield'}
	    	},
	    	{text: '<fmt:message key="sellers.updateTime"/>', dataIndex: 'updateTime', width: 260, sortable : true,
				filter: {xtype: 'textfield'}
			},
			{text: '<fmt:message key="sellers.createTime"/>', dataIndex: 'createTime', width: 270, sortable : true,
				filter: {xtype: 'textfield'}
			}
        ];
        
        this.pagingToolbar = new Ext.PagingToolbar({
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
		this.recordListBbar = this.pagingToolbar; 
		
    	this.recordList = Ext.create('Ext.grid.GridPanel', {
	    	id: 'recordList@recordPanel' + this.id,
			region: 'center',
			header: false,
			autoScroll: true,   
			closable: true,
			selType: 'checkboxmodel',
			multiSelect: true,
			columnLines: true,
			animCollapse: false,
		    enableLocking: true,
		    columns: this.columns,
		    store: this.store,
		    bbar: this.recordListBbar,
        	plugins: ['gridHeaderFilters','gridexporter'],
		    viewConfig: {
		        stripeRows: true,
		        enableTextSelection: true
		    }
	    });
	    
    	this.items = [this.recordList];	
    	this.callParent(arguments);
    	
    	this.gsm = this.recordList.getSelectionModel();
    	<jkd:haveAuthorize access="/store/storeAmountChangeList.json">
    	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.recordList.filters)
			});
	    }, this);
	    this.store.load();
	    </jkd:haveAuthorize>
    },
    
    rendererChangeAmountType : function(val){
		var str =  "";
		if(val == 2){
			str = '<fmt:message key="storeAmountChange.type_check"/>';
		}else if(val==1){
			str = '<fmt:message key="storeAmountChange.type_drawal"/>';
		}
		return str;
	},
    
    cleanSearch : function(){
		this.store.loadPage(1);
    }
     
});