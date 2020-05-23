<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('User', {
	extend: 'Ext.data.Model',
	idProperty: 'userId',
    fields: [
    	{name: 'userId',	    mapping: 'userId',	    type: 'int'},
		{name: 'nickname',	    mapping: 'nickname',	type: 'string'},
		{name: 'password',	    mapping: 'password',	type: 'string'},
		{name: 'mobile',	    mapping: 'mobile',	    type: 'string'},
		{name: 'registerIp',	mapping: 'registerIp',	type: 'string'},
		{name: 'lastIp',	    mapping: 'lastIp',	    type: 'string'},
		{name: 'loginCount',	mapping: 'loginCount',	type: 'string'},
		{name: 'status',	    mapping: 'status',	    type: 'string'},
		{name: 'introduce',	    mapping: 'introduce',	type: 'string'},
		{name: 'sex',	        mapping: 'sex',	        type: 'string'},
		{name: 'province',	    mapping: 'province',	type: 'string'},
		{name: 'city',	        mapping: 'city',	    type: 'string'},
		{name: 'areaCode',	    mapping: 'areaCode',	type: 'string'},
		{name: 'realName',	    mapping: 'realName',	type: 'string'},
		{name: 'identityNo',	mapping: 'identityNo',	type: 'string'},
		{name: 'topUserId',	    mapping: 'topUserId',	type: 'string'},
		{name: 'createTime',	mapping: 'createTime',	type: 'string'},
		{name: 'updateTime',	mapping: 'updateTime',	type: 'string'}
    ]
});

Ext.define('MyExt.userManager.UserWholesaleListPanel', {
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
			model: 'User',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/user/list.json?isWholesale=true"/>',
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
        		{id: '0', name: '<fmt:message key="order.status.0"/>'},
        		{id: '1', name: '<fmt:message key="order.status.1"/>'},
        		{id: '2', name: '<fmt:message key="order.status.2"/>'},
        		{id: '3', name: '<fmt:message key="order.status.3"/>'}
        	]
        });
		
		this.columns = [
        	{text: '<fmt:message key="user.userId"/>', dataIndex: 'userId', width: 70, locked: true, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.nickname"/>', dataIndex: 'nickname', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.password"/>', dataIndex: 'password', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.mobile"/>', dataIndex: 'mobile', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.registerIp"/>', dataIndex: 'registerIp', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.lastIp"/>', dataIndex: 'lastIp', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.loginCount"/>', dataIndex: 'loginCount', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.status"/>', dataIndex: 'status', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.introduce"/>', dataIndex: 'introduce', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.sex"/>', dataIndex: 'sex', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.provinceName"/>', dataIndex: 'province', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.cityName"/>', dataIndex: 'city', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.areaName"/>', dataIndex: 'areaCode', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.realName"/>', dataIndex: 'realName', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.identityNo"/>', dataIndex: 'identityNo', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.topUserId"/>', dataIndex: 'topUserId', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.createTime"/>', dataIndex: 'createTime', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.updateTime"/>', dataIndex: 'updateTime', width: 140, sortable : true,
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
		this.userListBbar = this.pagingToolbar; 
		
    	this.userList = Ext.create('Ext.grid.GridPanel', {
	    	id: 'userList@UserPanel' + this.id,
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
		    bbar: this.userListBbar,
        	plugins: ['gridHeaderFilters','gridexporter'],
		    viewConfig: {
		        stripeRows: true,
		        enableTextSelection: true
		    }
	    });
	    
	    this.tbar = [{
        	text: '<fmt:message key="user.cancel"/>', 
        	iconCls: 'delete', 	
        	handler: this.cancelUser, 
        	scope: this
        },'->',{
        	text: '<fmt:message key="button.exporter.xls"/>', 
        	iconCls: 'excel', 	
        	handler: function(){
        		this.userList.saveDocumentAs({
                	type: 'excel',
                    title: 'meta.fiddleHeader',
                    fileName: 'excel.xls'
                });
        	}, 
        	scope: this
        }];
        
        this.east =  Ext.create('MyExt.userManager.UserTabPanel', {
        	userList: this.userList,
		 	viewer: this.viewer,
		 	layout: 'card',
	        border: false,
	       	region: 'east',
	        width: 550,
	        split: true,
	        header: false,
	        hidden: true
        });
	    
    	this.items = [this.userList, this.east];	
		this.east.hide();
    	this.callParent(arguments);
    	
    	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.userList.filters),
				keyword: this.keywordField.getRawValue()
			});
	    }, this);
	    this.store.load();
	    
	    this.gsm = this.userList.getSelectionModel();
	    this.userList.on('itemdblclick', function(view, record, item, index, e, eOpts) {
	    	this.east.transferData(this.east, record, this.body.dom.clientWidth);
	    	this.east.show();
	    }, this);
	    
	    this.userList.on('headerfilterchange', function(e) {
	        alert('sfs');
		});
    },
    
    cleanSearch : function(){
    	this.keywordField.setRawValue();
		this.store.loadPage(1);
    },
    
    cancelUser : function(){
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.UserId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="order.cancel.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/user/cancelUser.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData)},
		          	success: function(response){
          				var responseObject = Ext.JSON.decode(response.responseText);
          				if (responseObject.success == true){
          					showSuccMsg(responseObject.message);
							this.store.loadPage(1);
		                    this.gsm.deselectAll();
          				}else{
          					showFailMsg(responseObject.message, 4);
          				}
					}
		     	})
	     	}
	 	}, this) 
    }
});