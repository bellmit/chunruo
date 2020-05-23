<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('OrderPaymentNotifyRecord', {
	extend: 'Ext.data.Model',
	idProperty: 'recordId',
    fields: [
    	{name: 'recordId',	 	mapping: 'recordId',	type: 'int'},
		{name: 'orderNo',	 	mapping: 'orderNo',		type: 'string'},
		{name: 'tradeNo',	 	mapping: 'tradeNo',		type: 'string'},
		{name: 'price',	 		mapping: 'price',		type: 'string'},
		{name: 'status',	 	mapping: 'status',		type: 'bool'},
		{name: 'paymentType',	 mapping: 'paymentType',	type: 'int'},
		{name: 'weChatConfigId', mapping: 'weChatConfigId',	type: 'string'},
		{name: 'syncNumber',	 mapping: 'syncNumber',	type: 'string'},
		{name: 'createTime',	 mapping: 'createTime',	type: 'string'},
		{name: 'updateTime',	 mapping: 'updateTime',	type: 'string'}
    ]
});

Ext.define('MyExt.orderManager.OrderPaymentNotifyRecordList', {
    extend : 'Ext.grid.GridPanel',
    requires : ['Ext.ux.grid.GridHeaderFilters','Ext.ux.grid.Exporter'],
	region: 'center',
	autoScroll: true,   
	closable: true,
	selType: 'checkboxmodel',
	multiSelect: true,
	columnLines: true,
	animCollapse: false,
    enableLocking: true,
    plugins: ['gridHeaderFilters','gridexporter'],
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
			model: 'OrderPaymentNotifyRecord',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/orderPaymentNotify/list.json"/>',
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
        		{id: 2, name: '<fmt:message key="order.payType.huifu"/>'}
			]
		});
		
		this.columns = [
	    	{text: '<fmt:message key="order.payment.notify.recordId"/>', dataIndex: 'recordId', width: 70, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.payment.notify.orderNo"/>', dataIndex: 'orderNo', width: 180, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="order.payment.notify.tradeNo"/>', dataIndex: 'tradeNo', width: 200, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.payment.notify.price"/>', dataIndex: 'price', width: 90, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.payment.notify.status"/>', dataIndex: 'status', width: 70, sortable : true,
        		filter: {xtype: 'textfield'},
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
        	{text: '<fmt:message key="order.payment.notify.paymentType"/>', dataIndex: 'paymentType', width: 80, sortable : true,
        		align: 'center',
        		renderer: this.rendererOrderPay,
        		filter: {
					xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.orderPayStore,
			        queryMode: 'local',
			        typeAhead: true
				}
        	},
        	{text: '<fmt:message key="order.payment.notify.weChatConfigId"/>', dataIndex: 'weChatConfigId', width: 70, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.payment.notify.syncNumber"/>', dataIndex: 'syncNumber', width: 70, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.payment.notify.createTime"/>', dataIndex: 'createTime', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.payment.notify.updateTime"/>', dataIndex: 'updateTime', width: 140, sortable : true,
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
	    
	    this.tbar = [{
        	text: '<fmt:message key="order.customs.handler.push"/>', 
        	iconCls: 'Packageadd', 	
        	handler: this.orderPushCustom, 
        	scope: this
        },'-',{
        	text: '<fmt:message key="order.customs.handler.close"/>', 
        	iconCls: 'delete', 	
        	handler: this.orderCloseStatus, 
        	scope: this
        },'->',{
        	text: '<fmt:message key="button.exporter.xls"/>', 
        	iconCls: 'excel', 	
        	handler: function(){
        		this.orderList.saveDocumentAs({
                	type: 'excel',
                    title: 'meta.fiddleHeader',
                    fileName: 'excel.xls'
                });
        	}, 
        	scope: this
        }];
        this.bbar = this.pagingToolbar;      
    	this.callParent();
  
  		this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.filters)
			});
	    }, this);
    	this.store.load();
    	this.gsm = this.getSelectionModel();
    },
    
    orderPushCustom : function(){
    	var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.recordId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="submit.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/customs/handlerPushCustom.json"/>',
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
	 	}, this);
    },
    
    orderCloseStatus : function(){
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.recordId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="submit.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/customs/orderCustomsClose.json"/>',
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
	 	}, this);
    },
    
    rendererOrderPay : function(val){ 
		var str =  "";
		if(val == 0){
			str = '<fmt:message key="order.payType.wechat"/>';
		}else if(val == 1){
			str = '<fmt:message key="order.payType.alipay"/>';
		}else if(val == 2){
			str = '<fmt:message key="order.payType.huifu"/>';
		}else if(val == 3){
			str = '<fmt:message key="order.payType.yisheng"/>';
		}
		return str;
	},
    
    rendererStuts : function(val){
		if(val == true) {
            return '<b><fmt:message key="button.yes"/></b>';
        }else{
            return '<fmt:message key="button.no"/>';
        }
	}
});