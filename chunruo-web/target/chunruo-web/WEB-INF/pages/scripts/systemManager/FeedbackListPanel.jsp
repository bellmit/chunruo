<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('Feedback', {
	extend: 'Ext.data.Model',
	idProperty: 'id',
    fields: [
    	{name: 'feedbackId',		mapping: 'feedbackId',		type: 'int'},
    	{name: 'mobile',			mapping: 'mobile',			type: 'string'},
		{name: 'userName',			mapping: 'userName',		type: 'string'},
		{name: 'content',			mapping: 'content',			type: 'string'},
		{name: 'createTime',		mapping: 'createTime',		type: 'string'},
		{name: 'updateTime',		mapping: 'updateTime',		type: 'string'}
    ]
});

Ext.define('MyExt.systemManager.FeedbackListPanel', {
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
			model: 'Feedback',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/feedback/list.json"/>',
				reader: {
					type : 'json',
                	root: 'data',
                	totalProperty: 'totalCount'
            	}
			},
			sorters: [{
	            property: 'feedbackId',
	            direction: 'desc'
	        }]
		});
		
		this.columns = [
			{text: '<fmt:message key="sellers.id"/>', dataIndex: 'feedbackId',   hidden:true 
	    	},
	    	{text: '<fmt:message key="order.customs.mobile"/>', dataIndex: 'mobile', width: 260,  sortable : true,
				filter: {xtype: 'textfield'}
	    	},
	    	{text: '<fmt:message key="store.userName"/>', dataIndex: 'userName', width: 260,  sortable : true,
				filter: {xtype: 'textfield'}
	    	},
	    	{text: '<fmt:message key="feedback.content"/>', dataIndex: 'content', width: 800,  sortable : true,
				filter: {xtype: 'textfield'}
	    	},
	    	{text: '<fmt:message key="sellers.updateTime"/>', dataIndex: 'updateTime', width: 260, sortable : true,
				filter: {xtype: 'textfield'}
			},
			{text: '<fmt:message key="sellers.createTime"/>', dataIndex: 'createTime', width: 270, sortable : true,
				filter: {xtype: 'textfield'}
			}
        ];
        
        this.keywordField = new Ext.create('Ext.form.TextField', {
			width: 200,
			emptyText:'<fmt:message key="app.user.search" />',
        	scope: this
        });
        
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
		this.listBar = this.pagingToolbar; 
		
    	this.list = Ext.create('Ext.grid.GridPanel', {
	    	id: 'sellerInfoList@SellerInfoPanel' + this.id,
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
		    bbar: this.listBar,
        	plugins: ['gridHeaderFilters','gridexporter'],
		    viewConfig: {
		        stripeRows: true,
		        enableTextSelection: true
		    }
	    });
	    
    	this.items = [this.list];	
    	this.callParent(arguments);
    	
    	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.list.filters),
				keyword: this.keywordField.getRawValue()
			});
	    }, this);
	    this.store.load();
	    
	    this.gsm = this.list.getSelectionModel();
	    this.list.on('headerfilterchange', function(e) {
	        alert('sfs');
		});
    },
    
    cleanSearch : function(){
    	this.keywordField.setRawValue();
		this.store.loadPage(1);
    }
});