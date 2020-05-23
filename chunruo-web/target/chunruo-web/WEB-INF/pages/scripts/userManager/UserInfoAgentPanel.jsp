<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('UserInfoAgent', {
	extend: 'Ext.data.Model',
	idProperty: 'userId',
    fields: [
    	{name: 'userId',	 	mapping: 'userId',		type: 'int'},
		{name: 'nickname',	 	mapping: 'nickname',	type: 'string'},
		{name: 'status',	 	mapping: 'status',		type: 'bool'},
		{name: 'unionId',	 	mapping: 'unionId',		type: 'string'},
		{name: 'openId',	 	mapping: 'openId',		type: 'string'},
		{name: 'countryCode',	mapping: 'countryCode',	type: 'string'},
		{name: 'mobile',	 	mapping: 'mobile',		type: 'string'},
		{name: 'topUserId',   	mapping: 'topUserId',	type: 'int'},
		{name: 'topStoreName',	mapping: 'topStoreName',type: 'string'},
		{name: 'isAgent',	 	mapping: 'isAgent',		type: 'bool'},
		{name: 'isSpecialDealer',mapping: 'isSpecialDealer',type: 'bool'},
		{name: 'headerImage',	mapping: 'headerImage',	type: 'string'},
		{name: 'sex',	 		mapping: 'sex',			type: 'int'},
		{name: 'provinceId',	mapping: 'provinceId',	type: 'string'},
		{name: 'provinceName',	mapping: 'provinceName',type: 'string'},
		{name: 'cityId',	 	mapping: 'cityId',		type: 'string'},
		{name: 'cityName',	 	mapping: 'cityName',	type: 'string'},
		{name: 'areaId',	 	mapping: 'areaId',		type: 'string'},
		{name: 'areaName',	 	mapping: 'areaName',	type: 'string'},
		{name: 'realName',	 	mapping: 'realName',	type: 'string'},
		{name: 'identityNo',	mapping: 'identityNo',	type: 'string'},
		{name: 'introduce',	 	mapping: 'introduce',	type: 'string'},
		{name: 'registerIp',	mapping: 'registerIp',	type: 'string'},
		{name: 'lastIp',	 	mapping: 'lastIp',		type: 'string'},
		{name: 'loginCount',	mapping: 'loginCount',	type: 'int'},
		{name: 'createTime',	mapping: 'createTime',	type: 'string'},
		{name: 'updateTime',	mapping: 'updateTime',	type: 'string'}
    ]
});

Ext.define('MyExt.userManager.UserInfoAgentPanel', {
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
			model: 'UserInfoAgent',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/user/agentList.json"/>',
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
        	{text: '<fmt:message key="userInfo.userId"/>', dataIndex: 'userId', width: 70, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="userInfo.nickname"/>', dataIndex: 'nickname', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="userInfo.countryCode"/>', dataIndex: 'countryCode', width: 70, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="userInfo.mobile"/>', dataIndex: 'mobile', width: 120, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="userInfo.status"/>', dataIndex: 'status', width: 70, sortable : true,
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
        	{text: '<fmt:message key="userInfo.sex"/>', dataIndex: 'sex', width: 70, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="userInfo.unionId"/>', dataIndex: 'unionId', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="userInfo.openId"/>', dataIndex: 'openId', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="user.topUserId"/>', dataIndex: 'topUserId', width: 100, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="userInfo.topStoreName"/>', dataIndex: 'topStoreName', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="userInfo.isSpecialDealer"/>', dataIndex: 'isSpecialDealer', width: 80, sortable : true,
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
        	{text: '<fmt:message key="userInfo.headerImage"/>', dataIndex: 'headerImage', width: 200, sortable : true},
        	{text: '<fmt:message key="userInfo.provinceName"/>', dataIndex: 'provinceName', width: 80, sortable : true},
        	{text: '<fmt:message key="userInfo.cityName"/>', dataIndex: 'cityName', width: 80, sortable : true},
        	{text: '<fmt:message key="userInfo.areaName"/>', dataIndex: 'areaName', width: 80, sortable : true},
        	{text: '<fmt:message key="userInfo.realName"/>', dataIndex: 'realName', width: 100, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="userInfo.identityNo"/>', dataIndex: 'identityNo', width: 180, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="userInfo.introduce"/>', dataIndex: 'introduce', width: 200, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="userInfo.registerIp"/>', dataIndex: 'registerIp', width: 140, sortable : true},
        	{text: '<fmt:message key="userInfo.lastIp"/>', dataIndex: 'lastIp', width: 140, sortable : true},
        	{text: '<fmt:message key="userInfo.loginCount"/>', dataIndex: 'loginCount', width: 70, sortable : true},
        	{text: '<fmt:message key="userInfo.createTime"/>', dataIndex: 'createTime', width: 140, sortable : true},
        	{text: '<fmt:message key="userInfo.updateTime"/>', dataIndex: 'updateTime', width: 140, sortable : true}
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
		this.userListBbar = this.pagingToolbar; 
		
    	this.userList = Ext.create('Ext.grid.GridPanel', {
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
        
        this.east =  Ext.create('MyExt.userManager.UserInfoTabPanel', {
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
	        	filters: Ext.JSON.encode(this.userList.filters)
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
    },
    
    rendererStuts : function(val){
		if(val == true) {
            return '<b><fmt:message key="button.yes"/></b>';
        }else{
            return '<fmt:message key="button.no"/>';
        }
	}
});