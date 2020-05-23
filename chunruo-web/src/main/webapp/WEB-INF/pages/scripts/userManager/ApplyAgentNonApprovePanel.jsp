<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('ApplyAgent', {
	extend: 'Ext.data.Model',
	idProperty: 'applyId',
    fields: [
   			{name: 'applyId',	        mapping: 'applyId',	  type: 'int'},
		{name: 'userId',	 		mapping: 'userId',	  type: 'int'},
		{name: 'nickName',	 	    mapping: 'nickName',  type: 'string'},
		{name: 'status',	        mapping: 'status',	  type: 'int'},
		{name: 'linkMan',	        mapping: 'linkMan',	  type: 'string'},
		{name: 'mobile',	        mapping: 'mobile',	  type: 'string'},
		{name: 'identityNo',	    mapping: 'identityNo',type: 'string'},
		{name: 'city',	            mapping: 'city',	  type: 'string'},
		{name: 'profession',	    mapping: 'profession',type: 'int'},
		{name: 'image',	            mapping: 'image',	  type: 'string'},
		{name: 'createTime',	    mapping: 'createTime',type: 'string'},
		{name: 'updateTime',	    mapping: 'updateTime',type: 'string'}
	]
});

Ext.define('MyExt.userManager.ApplyAgentNonApprovePanel', {
    extend : 'Ext.panel.Panel',
    requires : ['Ext.ux.grid.GridHeaderFilters','MyExt.DateSelectorPicker'],
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
			model: 'ApplyAgent',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/applyAgent/list.json?status=2"/>',
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
		
		this.orderPayStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: 0, name: '<fmt:message key="order.payType.wechat"/>'},
        		{id: 1, name: '<fmt:message key="order.payType.alipay"/>'},
        		{id: 2, name: '<fmt:message key="order.payType.huifu"/>'},
        		{id: 3, name: '<fmt:message key="order.payType.yisheng"/>'}
			]
		});
	
		this.buyWayTypeStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: 0, name: '<fmt:message key="order.buyWayType.express"/>'},
        		{id: 1, name: '<fmt:message key="order.buyWayType.expressBySelf"/>'}
			]
		});
	
		this.productTypeStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: 1, name: '<fmt:message key="order.productType.1"/>'},
        		{id: 2, name: '<fmt:message key="order.productType.2"/>'},
        		{id: 3, name: '<fmt:message key="order.productType.3"/>'}
        	]
        });
        
        this.statusStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: 1, name: '<fmt:message key="order.status.1"/>'},
        		{id: 2, name: '<fmt:message key="order.status.2"/>'},
        		{id: 3, name: '<fmt:message key="order.status.3"/>'},
        		{id: 4, name: '<fmt:message key="order.status.4"/>'},
        		{id: 5, name: '<fmt:message key="order.status.5"/>'},
        		{id: 6, name: '<fmt:message key="order.status.6"/>'},
        		{id: 7, name: '<fmt:message key="order.status.7"/>'}
        	]
        });
        
        <!-- 申请状态 0，未审核，1，已审核 -->
        	this.rendererApply= Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        	    {id: '2', name: '<fmt:message key="user.applyAgent.status2"/>'},
        		{id: '1', name: '<fmt:message key="user.applyAgent.status1"/>'},
        		{id: '0', name: '<fmt:message key="user.applyAgent.status0"/>'}
        	]
        });
        
        this.professionStore= Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        	    {id: '1', name: '<fmt:message key="user.applyAgent.profession1"/>'},
        		{id: '2', name: '<fmt:message key="user.applyAgent.profession2"/>'},
        		{id: '3', name: '<fmt:message key="user.applyAgent.profession3"/>'},
        		{id: '4', name: '<fmt:message key="user.applyAgent.profession4"/>'},
        		{id: '5', name: '<fmt:message key="user.applyAgent.profession5"/>'}
        	]
        });
        
    	this.cancelMethodStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: 0, name: '<fmt:message key="order.default"/>'},
        		{id: 1, name: '<fmt:message key="order.timeOut"/>'},
        		{id: 2, name: '<fmt:message key="order.cancelBySeller"/>'},
        		{id: 3, name: '<fmt:message key="order.cancelByBuyer"/>'}
        	]
        });
		
		
		this.columns = [
			{text: '<fmt:message key="user.applyAgent.applyId"/>', dataIndex: 'applyId', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="user.userId"/>', dataIndex: 'userId', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="user.nickname"/>', dataIndex: 'nickName', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="user.applyAgent.status"/>', dataIndex: 'status', width: 140, sortable : true,
        		align: 'center',
        		renderer: this.rendererStuts,
        		filter: {
        		xtype: 'combobox',
        		displayField: 'name',
			    valueField: 'id',
        		store: this.rendererApply,
        		queryMode: 'local',
			    typeAhead: true},
        	},
        	{text: '<fmt:message key="user.applyAgent.linkMan"/>', dataIndex: 'linkMan', width: 140, sortable : true,
                filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        		
        	},
        	{text: '<fmt:message key="user.applyAgent.mobile"/>', dataIndex: 'mobile', width: 140, sortable : true,	
     		    filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        		
        	},
        	{text: '<fmt:message key="user.applyAgent.identityNo"/>', dataIndex: 'identityNo', width: 140, sortable : true,	
     		    filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        		
        	},
        	{text: '<fmt:message key="user.applyAgent.city"/>', dataIndex: 'city', width: 140, sortable : true,	
     		    filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        		
        	},
        	{text: '<fmt:message key="user.applyAgent.profession"/>', dataIndex: 'profession', width: 140, sortable : true,	
     		    align: 'center',
        		renderer: this.profession,
        		filter: {
        		xtype: 'combobox',
        		displayField: 'name',
			    valueField: 'id',
        		store: this.professionStore,
        		queryMode: 'local',
			    typeAhead: true},
        		
        	},
        	{text: '<fmt:message key="user.applyAgent.createTime"/>', dataIndex: 'createTime', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="user.applyAgent.updateTime"/>', dataIndex: 'updateTime', width: 140, sortable : true,
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
		this.applyAgentListBbar = this.pagingToolbar; 
		
    	this.applyAgentList = Ext.create('Ext.grid.GridPanel', {
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
		    bbar: this.applyAgentListBbar,
        	plugins: ['gridHeaderFilters'],
		    viewConfig: {
		        stripeRows: true,
		        enableTextSelection: true
		    }
	    });
	    
	    this.tbar = [<%-- {
        	text: '<fmt:message key="user.applyAgent.approve"/>', 
            iconCls: 'enable', 
        	handler: this.approve, 
        	scope: this
        },{
        	text: '<fmt:message key="user.applyAgent.nonapprove"/>', 
            iconCls: 'delete',
        	handler: this.nonapprove, 
        	scope: this
        }, '->',{
        	text: '<fmt:message key="button.exporter.xls"/>', 
        	iconCls: 'excel', 	
        	handler: function(){
        		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="order.export.confirm"/>', function(e){
					if(e == 'yes'){
						var columns = [];
			    		Ext.Array.each(this.applyAgentList.getColumns(), function(object, index, countriesItSelf) {
			    			if(object.dataIndex){
			    				columns.push({key: object.dataIndex, value: object.text});
			    			}
						});
						
						Ext.Ajax.request({
				        	url: this.applyAgentList.store.proxy.url,
				         	method: 'post',
							scope: this,
							params:{columns: Ext.JSON.encode(columns), filters: Ext.JSON.encode(this.applyAgentList.filters), isExporter: true},
				          	success: function(response){
		          				var responseObject = Ext.JSON.decode(response.responseText);
		                        if(responseObject.success == true){
		                        	var downLoadURL = '<c:url value="/order/downLoadExportFile.msp?filePath="/>' + responseObject.filePath;
		                        	window.open(downLoadURL); 
								}else{
									showFailMsg(responseObject.message, 4);
								}
							}
				     	});
			     	}
			 	}, this); 
        	}, 
        	scope: this
        },'-',{text: '<fmt:message key="order.exporter.xls"/>', 
        	iconCls: 'excel', 	
        	handler: this.exportOrder, 
        	scope: this}--%>];
        
        this.east =  Ext.create('MyExt.userManager.ApplyAgentTabPanel', {
        	applyAgentList: this.applyAgentList,
		 	viewer: this.viewer,
		 	layout: 'card',
	        border: false,
	       	region: 'east',
	        width: 550,
	        split: true,
	        header: false,
	        hidden: true
        });
	    
    	this.items = [this.applyAgentList, this.east];	
		this.east.hide();
    	this.callParent(arguments);
    	
    	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.applyAgentList.filters)
			});
	    }, this);
	    this.store.load();
	    
	    this.gsm = this.applyAgentList.getSelectionModel();
	    this.applyAgentList.on('itemdblclick', function(view, record, item, index, e, eOpts) {
	    	this.east.transferData(this.east, record, this.body.dom.clientWidth);
	    	this.east.show();
	    }, this);
    },
    
    cleanSearch : function(){
    	this.keywordField.setRawValue();
		this.store.loadPage(1);
    },
    
    approve : function(){
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.applyId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="order.cancel.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/applyAgent/approve.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData),tag:1},
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
    
    nonapprove : function(){
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.applyId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="order.cancel.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/applyAgent/approve.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData),tag:2},
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
		if(val == 0) {
            return '<b><fmt:message key="user.applyAgent.status0"/></b>';
        }else if(val == 1){
            return '<fmt:message key="user.applyAgent.status1"/>';
        }else {
           return '<fmt:message key="user.applyAgent.status2"/>';
        }
	},
    
   
	profession : function(val){
		if(val == 1) {
            return '<b><fmt:message key="user.applyAgent.profession1"/></b>';
        }else if(val == 2){
            return '<fmt:message key="user.applyAgent.profession2"/>';
        }else if(val == 3){
           return '<fmt:message key="user.applyAgent.profession3"/>';
        }else if(val == 4){
           return '<fmt:message key="user.applyAgent.profession4"/>';
        }else if(val == 5){
           return '<fmt:message key="user.applyAgent.profession5"/>';
        }
        
	}
	
});