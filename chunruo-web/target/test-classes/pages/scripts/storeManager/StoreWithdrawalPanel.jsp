<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('StoreWithdrawal', {
	extend: 'Ext.data.Model',
	idProperty: 'recordId',
    fields: [
    	{name: 'recordId',	 	mapping: 'recordId',		type: 'int'},
		{name: 'tradeNo',	 	mapping: 'tradeNo',			type: 'string'},
		{name: 'userId',	 	mapping: 'userId',			type: 'int'},
		{name: 'userName',	 	mapping: 'userName',		type: 'string'},
		{name: 'status',	 	mapping: 'status',			type: 'int'},
		{name: 'amount',	 	mapping: 'amount',			type: 'string'},
		{name: 'complateTime',	mapping: 'complateTime',	type: 'string'},
		{name: 'createTime',	mapping: 'createTime',		type: 'string'},
		{name: 'updateTime',	mapping: 'updateTime',		type: 'string'}
    ]
});

Ext.define('MyExt.storeManager.StoreWithdrawalPanel', {
   	extend : 'Ext.panel.Panel',
    requires : ['Ext.ux.grid.GridHeaderFilters','Ext.ux.grid.Exporter'],
    header: false,
	closable: true,
	columnLines: true,
	animCollapse: true,
	layout: 'border',
	name: 'pan',
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
			model: 'StoreWithdrawal',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/storeWithdrawal/list.json"/>',
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
		
		this.withdrawalStatusStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: 1, name: '<fmt:message key="store.withdrawal.status.1"/>'},
        		{id: 3, name: '<fmt:message key="store.withdrawal.status.3"/>'},
        		{id: 4, name: '<fmt:message key="store.withdrawal.status.4"/>'}
        	]
        });
        
		
		this.columns = [
        	{text: '<fmt:message key="store.withdrawal.recordId"/>', dataIndex: 'recordId', width: 70, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="store.withdrawal.amount"/>', dataIndex: 'amount', width: 80, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="store.withdrawal.status"/>', dataIndex: 'status', width: 100, sortable : true,
        		align: 'center',
				renderer: this.rendererWithdrawalStatus,
        		filter: {
					xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.withdrawalStatusStore,
			        queryMode: 'local',
			        typeAhead: true
				}
        	},
        	{text: '<fmt:message key="store.withdrawal.userId"/>', dataIndex: 'userId', width: 80, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="store.withdrawal.userName"/>', dataIndex: 'userName', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="store.withdrawal.name"/>', dataIndex: 'name', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="store.withdrawal.tradeNo"/>', dataIndex: 'tradeNo', width: 180, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="store.withdrawal.complateTime"/>', dataIndex: 'complateTime', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="store.withdrawal.createTime"/>', dataIndex: 'createTime', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="store.withdrawal.updateTime"/>', dataIndex: 'updateTime', width: 140, sortable : true,
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
		this.orderListBbar = this.pagingToolbar; 
		
    	this.gridList = Ext.create('Ext.grid.GridPanel', {
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
		    bbar: this.orderListBbar,
        	plugins: ['gridHeaderFilters','gridexporter'],
		    viewConfig: {
		        stripeRows: true,
		        enableTextSelection: true
		    }
	    });
	    
	  
        
        this.east =  Ext.create('MyExt.storeManager.StoreWithdrawalTabPanel', {
        	gridList: this.gridList,
		 	viewer: this.viewer,
		 	layout: 'card',
	        border: false,
	       	region: 'east',
	        width: 550,
	        split: true,
	        header: false,
	        hidden: true
        });
	    
    	this.items = [this.gridList, this.east];	
		this.east.hide();
    	this.callParent(arguments);
    	
    	<jkd:haveAuthorize access="/storeWithdrawal/list.json">
    	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.gridList.filters)
			});
	    }, this);
	    this.store.load();
	    </jkd:haveAuthorize>
	    
	    this.gsm = this.gridList.getSelectionModel();
	   	this.gridList.on('itemdblclick', function(view, record, item, index, e, eOpts) {
	   		<jkd:haveAuthorize access="/storeWithdrawal/getStoreWithdrawalById.json">
	    	this.east.transferData(this.east, record, this.body.dom.clientWidth);
	    	this.east.show();
	    	</jkd:haveAuthorize>
	    }, this);
    },
    
    cancelOrder : function(){
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.orderId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="order.cancel.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/order/cancelOrder.json"/>',
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
    
    rendererWithdrawalStatus : function(val){
		if(val == 1){
			return '<fmt:message key="store.withdrawal.status.1"/>';
		}else if(val == 2 ){
			return '<b><fmt:message key="store.withdrawal.status.2"/></b>';
		}else if(val == 3){
			return '<span style="color:green;"><fmt:message key="store.withdrawal.status.3"/></span>';
		}else if(val == 4){
			return '<span style="color:red;"><fmt:message key="store.withdrawal.status.4"/></span>';
		}
		return val;
	},
	

	rendererWithdrawalType : function(val){
		if(val == 1){
			return '<fmt:message key="store.withdrawal.type.1"/>';
		}else if(val == 2){
			return '<fmt:message key="store.withdrawal.type.2"/>';
		}else if(val == 3){
			return '<fmt:message key="store.withdrawal.type.3"/>';
		}
		return val;
	},
	
		export:function(){
		var exportWithdrawalExcel = Ext.create('MyExt.storeManager.ExportWithdrawalExcel', {id: 'ExportWithdrawalExcel@ExportWithdrawalExcel', title: '<fmt:message key="button.add"/>'});
		var buttons = [{
			text: '<fmt:message key="button.exporter.xls"/>',
			handler: function(){
	            if(exportWithdrawalExcel.form.isValid()){
	            	Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="order.export.confirm"/>', function(e){
						if(e == 'yes'){
				        	var formValues=exportWithdrawalExcel .getForm().getValues();
							var begin = Ext.util.Format.date(Ext.getCmp('beginTime').getValue(), 'Y-m-d H:i:s');
							
							var end = Ext.util.Format.date(Ext.getCmp('endTime').getValue(), 'Y-m-d H:i:s');
							var status = Ext.getCmp('status').value;
							console.log("status"+status);
							window.location.href = "/storeWithdrawal/exportWithdrawalExcel.json?beginTime="+begin+"&endTime="+end+"&status=" + status;
			              	popWin.close();
			        	}
			        }, this)
	        	}
			},
			scope: this
		},{
			text: '<fmt:message key="button.cancel"/>',
			handler : function(){popWin.close();},
			scope: this
		}];
		openWin('<fmt:message key="button.exporter.xls"/>', exportWithdrawalExcel, buttons, 400, 200);
	},
});