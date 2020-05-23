<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('SmsSendRecord', {
	extend: 'Ext.data.Model',
	idProperty: 'recordId',
    fields: [
    	{name: 'recordId',	 mapping: 'recordId',	type: 'string'},
		{name: 'mobile',	 mapping: 'mobile',		type: 'string'},
		{name: 'content',	 mapping: 'content',	type: 'string'},
		{name: 'status',	 mapping: 'status',		type: 'bool'},
		{name: 'createTime',	 mapping: 'createTime',	type: 'string'},
		{name: 'updateTime',	 mapping: 'updateTime',	type: 'string'}
    ]
});

Ext.define('MyExt.userManager.SmsSendRecordListPanel', {
    extend : 'Ext.grid.GridPanel',
    requires : ['Ext.ux.grid.GridHeaderFilters'],
	region: 'center',
	autoScroll: true,   
	closable: true,
	selType: 'checkboxmodel',
	multiSelect: true,
	columnLines: true,
	animCollapse: false,
    enableLocking: true,
    plugins: ['gridHeaderFilters'],
    viewConfig: {
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
			model: 'SmsSendRecord',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/user/smsSendRecordList.json"/>',
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
		
		this.rendererStutsStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: 1, name: '<fmt:message key="button.yes"/>'},
        		{id: 0, name: '<fmt:message key="button.no"/>'}
        	]
        });
		
		this.columns = [
	    	{text: '<fmt:message key="user.smsSendRecord.recordId"/>', dataIndex: 'recordId', width: 70, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="user.smsSendRecord.status"/>', dataIndex: 'status', width: 80, sortable : true,
        		align: 'center',
				renderer: this.rendererStuts,
        		filter: {
					xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.rendererStutsStore,
			        queryMode: 'local',
			        typeAhead: true
				}
        	},
        	{text: '<fmt:message key="user.smsSendRecord.mobile"/>', dataIndex: 'mobile', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="user.smsSendRecord.content"/>', dataIndex: 'content', flex: 1, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="user.smsSendRecord.createTime"/>', dataIndex: 'createTime', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="user.smsSendRecord.updateTime"/>', dataIndex: 'updateTime', width: 140, sortable : true,
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
  		<jkd:haveAuthorize access="/user/smsSendRecordList.json">
  		this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.filters)
			});
	    }, this);
    	this.store.load();
    	</jkd:haveAuthorize>
    },
    
    rendererStuts : function(val){
		if(val == true) {
            return '<b><fmt:message key="button.yes"/></b>';
        }else{
            return '<fmt:message key="button.no"/>';
        }
	}
});