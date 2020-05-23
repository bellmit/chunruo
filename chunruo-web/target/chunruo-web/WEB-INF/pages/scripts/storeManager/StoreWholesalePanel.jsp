<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('StoreWholesale', {
	extend: 'Ext.data.Model',
	idProperty: 'storeId',
    fields: [
    	{name: 'storeId',	 	mapping: 'storeId',			type: 'int'},
		{name: 'userId',	 	mapping: 'userId',			type: 'int'},
		{name: 'userName',	 	mapping: 'userName',		type: 'string'},
		{name: 'name',	 		mapping: 'name',			type: 'string'},
		{name: 'editNameCount',	mapping: 'editNameCount',	type: 'int'},
		{name: 'logo',	 		mapping: 'logo',			type: 'string'},
		{name: 'qcode',	 		mapping: 'qcode',			type: 'string'},
		{name: 'linkman',	 	mapping: 'linkman',			type: 'string'},
		{name: 'mobile',	 	mapping: 'mobile',			type: 'string'},
		{name: 'introduce',	 	mapping: 'introduce',		type: 'string'},
		{name: 'isApprove',	 	mapping: 'isApprove',		type: 'string'},
		{name: 'status',	 	mapping: 'status',			type: 'bool'},
		{name: 'serviceTel',	mapping: 'serviceTel',		type: 'string'},
		{name: 'serviceQq',	 	mapping: 'serviceQq',		type: 'string'},
		{name: 'serviceWeixin',	mapping: 'serviceWeixin',	type: 'string'},
		{name: 'openNav',	 	mapping: 'openNav',			type: 'bool'},
		{name: 'navStyleId',	mapping: 'navStyleId',		type: 'int'},
		{name: 'sales',	 		mapping: 'sales',			type: 'string'},
		{name: 'income',	 	mapping: 'income',			type: 'string'},
		{name: 'profitTop',	 	mapping: 'profitTop',		type: 'string'},
		{name: 'balance',	 	mapping: 'balance',			type: 'string'},
		{name: 'unbalance',		mapping: 'unbalance',		type: 'string'},
		{name: 'withdrawalAmount',mapping: 'withdrawalAmount',type: 'string'},
		{name: 'withdrawalType',mapping: 'withdrawalType',	type: 'bool'},
		{name: 'bankId',	 	mapping: 'bankId',			type: 'int'},
		{name: 'bankCard',	 	mapping: 'bankCard',		type: 'string'},
		{name: 'bankCardUser',	mapping: 'bankCardUser',	type: 'string'},
		{name: 'openingBank',	mapping: 'openingBank',		type: 'string'},
		{name: 'isWholesale',	mapping: 'isWholesale',		type: 'bool'},
		{name: 'inviterCode',	mapping: 'inviterCode',		type: 'string'},
		{name: 'storePageId',	mapping: 'storePageId',		type: 'int'},
		{name: 'topUserId',	    mapping: 'topUserId',		type: 'int'},
		{name: 'topStoreName',	mapping: 'topStoreName',	type: 'string'},
		{name: 'createTime',	mapping: 'createTime',		type: 'string'},
		{name: 'updateTime',	mapping: 'updateTime',		type: 'string'}
    ]
});

Ext.define('MyExt.storeManager.StoreWholesalePanel', {
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
			model: 'StoreWholesale',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/store/list.json?isWholesale=true"/>',
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
		
		this.statusStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: 1, name: '<fmt:message key="button.yes"/>'},
        		{id: 0, name: '<fmt:message key="button.no"/>'}
        	]
        });
        
        this.withdrawalTypeStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: 1, name: '<fmt:message key="store.withdrawalType.1"/>'},
        		{id: 0, name: '<fmt:message key="store.withdrawalType.0"/>'}
        	]
        });
		
		this.columns = [
			{text: '<fmt:message key="store.storeId"/>', dataIndex: 'storeId', width: 70, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.userId"/>', dataIndex: 'userId', width: 70, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.userName"/>', dataIndex: 'userName', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.name"/>', dataIndex: 'name', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.editNameCount"/>', dataIndex: 'editNameCount', width: 80, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.logo"/>', dataIndex: 'logo', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.qcode"/>', dataIndex: 'qcode', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.linkman"/>', dataIndex: 'linkman', width: 100, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.mobile"/>', dataIndex: 'mobile', width: 80, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.introduce"/>', dataIndex: 'introduce', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.isApprove"/>', dataIndex: 'isApprove', width: 80, sortable : true,
        		align: 'center',
        		renderer: this.rendererStuts,
        		filter: {
					xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.statusStore,
			        queryMode: 'local',
			        typeAhead: true
				}
        	},
        	{text: '<fmt:message key="store.status"/>', dataIndex: 'status', width: 80, sortable : true,
        		align: 'center',
        		renderer: this.rendererStuts,
        		filter: {
					xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.statusStore,
			        queryMode: 'local',
			        typeAhead: true
				}
        	},
        	{text: '<fmt:message key="store.serviceTel"/>', dataIndex: 'serviceTel', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.serviceQq"/>', dataIndex: 'serviceQq', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.serviceWeixin"/>', dataIndex: 'serviceWeixin', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.openNav"/>', dataIndex: 'openNav', width: 80, sortable : true,
        		align: 'center',
        		renderer: this.rendererStuts,
        		filter: {
					xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.statusStore,
			        queryMode: 'local',
			        typeAhead: true
				}
        	},
        	{text: '<fmt:message key="store.navStyleId"/>', dataIndex: 'navStyleId', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.sales"/>', dataIndex: 'sales', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.income"/>', dataIndex: 'income', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.profitTop"/>', dataIndex: 'profitTop', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.balance"/>', dataIndex: 'balance', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.unbalance"/>', dataIndex: 'unbalance', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.withdrawalAmount"/>', dataIndex: 'withdrawalAmount', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.withdrawalType"/>', dataIndex: 'withdrawalType', width: 80, sortable : true,
        		align: 'center',
        		renderer: this.rendererWithdrawalType,
        		filter: {
					xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.withdrawalTypeStore,
			        queryMode: 'local',
			        typeAhead: true
				}
        	},
        	{text: '<fmt:message key="store.bankId"/>', dataIndex: 'bankId', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.bankCard"/>', dataIndex: 'bankCard', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.bankCardUser"/>', dataIndex: 'bankCardUser', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.openingBank"/>', dataIndex: 'openingBank', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.isWholesale"/>', dataIndex: 'isWholesale', width: 80, sortable : true,
        		align: 'center',
        		renderer: this.rendererStuts,
        		filter: {
					xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.statusStore,
			        queryMode: 'local',
			        typeAhead: true
				}
        	},
        	{text: '<fmt:message key="store.inviterCode"/>', dataIndex: 'inviterCode', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.storePageId"/>', dataIndex: 'storePageId', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.topUserId"/>', dataIndex: 'topUserId', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="store.topStoreName"/>', dataIndex: 'topStoreName', width: 140, sortable : true,
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
		this.storeListBbar = this.pagingToolbar; 
		
    	this.storeList = Ext.create('Ext.grid.GridPanel', {
	    	id: 'storeList@StorePanel' + this.id,
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
		    bbar: this.storeListBbar,
        	plugins: ['gridHeaderFilters','gridexporter'],
		    viewConfig: {
		        stripeRows: true,
		        enableTextSelection: true
		    }
	    });
	    
	    this.tbar = [{
        	text: '<fmt:message key="store.setNoWholesale"/>', 
        	iconCls: 'add', 	
        	handler: this.setWholesale, 
        	scope: this
        },'->',{
        	text: '<fmt:message key="button.exporter.xls"/>', 
        	iconCls: 'excel', 	
        	handler: function(){
        		this.storeList.saveDocumentAs({
                	type: 'excel',
                    title: 'meta.fiddleHeader',
                    fileName: 'excel.xls'
                });
        	}, 
        	scope: this
        }];
        
        this.east =  Ext.create('MyExt.storeManager.StoreTabPanel', {
        	storeList: this.storeList,
		 	viewer: this.viewer,
		 	layout: 'card',
	        border: false,
	       	region: 'east',
	        width: 550,
	        split: true,
	        header: false,
	        hidden: true
        });
	    
    	this.items = [this.storeList, this.east];	
		this.east.hide();
    	this.callParent(arguments);
    	
    	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.storeList.filters)
			});
	    }, this);
	    this.store.load();
	    
	    this.gsm = this.storeList.getSelectionModel();
	    this.storeList.on('itemdblclick', function(view, record, item, index, e, eOpts) {
	    	this.east.transferData(this.east, record, this.body.dom.clientWidth);
	    	this.east.show();
	    }, this);
    },
    
    setWholesale : function(){
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.storeId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="store.setNoWholesale"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/store/setStoreIsWholesale.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData)},
		          	success: function(response){
          				var responseObject = Ext.JSON.decode(response.responseText);
          				if (responseObject.success == true){
          					showSuccMsg(responseObject.msg);
							this.store.loadPage(1);
		                    this.gsm.deselectAll();
          				}else{
          					showFailMsg(responseObject.message, 4);
          				}
					}
		     	})
	     	}
	 	}, this) 
    },
    
    rendererStuts : function(val){
		if(val == true) {
            return '<b><fmt:message key="button.yes"/></b>';
        }else{
            return '<fmt:message key="button.no"/>';
        }
	},
	
	rendererWithdrawalType : function(val){
		if(val == true) {
            return '<b><fmt:message key="store.withdrawalType.1"/></b>';
        }else{
            return '<fmt:message key="store.withdrawalType.0"/>';
        }
	},
});