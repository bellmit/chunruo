<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('OrderCustomsRecordList', {
	extend: 'Ext.data.Model',
	idProperty: 'recordId',
    fields: [
    	{name: 'recordId',	 mapping: 'recordId',	type: 'string'},
		{name: 'orderNo',	 mapping: 'orderNo',	type: 'string'},
		{name: 'tradeNo',	 mapping: 'tradeNo',	type: 'string'},
		{name: 'idCardName',	 mapping: 'idCardName',	type: 'string'},
		{name: 'idCardNo',	 mapping: 'idCardNo',	type: 'string'},
		{name: 'payAmount',	 mapping: 'payAmount',	type: 'string'},
		{name: 'orderTax',	 mapping: 'orderTax',	type: 'string'},
		{name: 'postage',	 mapping: 'postage',	type: 'string'},
		{name: 'paymentType',	 mapping: 'paymentType',	type: 'int'},
		{name: 'weChatConfigName',	 mapping: 'weChatConfigName',	type: 'string'},
		{name: 'isPushCustomSucc',	 mapping: 'isPushCustomSucc',	type: 'bool'},
		{name: 'syncNumber',	 mapping: 'syncNumber',	type: 'int'},
		{name: 'errorMsg',	 mapping: 'errorMsg',	type: 'string'},
		{name: 'createTime',	 mapping: 'createTime',	type: 'string'},
		{name: 'updateTime',	 mapping: 'updateTime',	type: 'string'}
    ]
});

Ext.define('MyExt.orderManager.OrderSyncCustomsListPanel', {
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
			model: 'OrderCustomsRecordList',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/customs/list.json"/>',
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
		
		this.orderPayStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: 0, name: '<fmt:message key="order.payType.wechat"/>'},
        		{id: 1, name: '<fmt:message key="order.payType.alipay"/>'},
        		{id: 2, name: '<fmt:message key="order.payType.huifu"/>'}
			]
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
	    	{text: '<fmt:message key="order.customsRecord.recordId"/>', dataIndex: 'recordId', width: 70, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="order.customsRecord.isPushCustomSucc"/>', dataIndex: 'isPushCustomSucc', width: 80, sortable : true,
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
        	{text: '<fmt:message key="order.customsRecord.orderNo"/>', dataIndex: 'orderNo', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="order.customsRecord.tradeNo"/>', dataIndex: 'tradeNo', width: 200, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="order.customsRecord.idCardName"/>', dataIndex: 'idCardName', width: 100, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="order.customsRecord.idCardNo"/>', dataIndex: 'idCardNo', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="order.customsRecord.errorMsg"/>', dataIndex: 'errorMsg', width: 180, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="order.customsRecord.payAmount"/>', dataIndex: 'payAmount', width: 70, sortable : true,
        		filter: {xtype: 'textfield'},
        		align: 'right',
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="order.customsRecord.orderTax"/>', dataIndex: 'orderTax', width: 70, sortable : true,
        		filter: {xtype: 'textfield'},
        		align: 'right',
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="order.customsRecord.postage"/>', dataIndex: 'postage', width: 70, sortable : true,
        		filter: {xtype: 'textfield'},
        		align: 'right',
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="order.customsRecord.paymentType"/>', dataIndex: 'paymentType', width: 100, sortable : true,
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
        	{text: '<fmt:message key="order.customsRecord.weChatConfigName"/>', dataIndex: 'weChatConfigName', width: 100, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="order.customsRecord.syncNumber"/>', dataIndex: 'syncNumber', width: 80, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="order.customsRecord.createTime"/>', dataIndex: 'createTime', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="order.customsRecord.updateTime"/>', dataIndex: 'updateTime', width: 140, sortable : true,
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
	    
	 	this.orderListBbar = this.pagingToolbar; 
		
    	this.orderList = Ext.create('Ext.grid.GridPanel', {
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
        	plugins: ['gridHeaderFilters'],
		    viewConfig: {
		        stripeRows: true,
		        enableTextSelection: true
		    }
	    });
	    
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
	    
	    <jkd:haveAuthorize access="/customs/handlerPushCustom.json">
	    this.tbar = [{
        	text: '<fmt:message key="order.customsRecord.handler.push"/>', 
        	iconCls: 'Packageadd', 	
        	handler: this.orderPushCustom, 
        	scope: this
        }];  
        </jkd:haveAuthorize>
    	this.callParent();
  
  		this.gsm = this.getSelectionModel();
  		<jkd:haveAuthorize access="/customs/list.json">
  		this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.filters)
			});
	    }, this);
    	this.store.load();
    	</jkd:haveAuthorize>
    },
    
    orderPushCustom : function(){
    	var rowsData = [];
		var records = this.gsm.getSelection();
		if(records.length == 0 ){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}else if(records.length > 1 ){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="user.approve.records"/>');
			return;
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="submit.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/customs/handlerPushCustom.json"/>',
		         	method: 'post',
					scope: this,
					params:{recordId: records[0].data.recordId},
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
    
    rendererStuts : function(val){
		if(val == true) {
            return '<b><fmt:message key="button.yes"/></b>';
        }else{
            return '<fmt:message key="button.no"/>';
        }
	},

   	rendererPushCustomStatus : function(val){
		var str =  "";
		if(val == 0){
			str = '<b><fmt:message key="order.customs.pushCustomStatus.0"/></b>';
		}else if(val == 1){
			str = '<span style="color:blue;"><b><fmt:message key="order.customs.pushCustomStatus.1"/></b></span>';
		}else if(val == 2){
			str = '<span style="color:green;"><b><fmt:message key="order.customs.pushCustomStatus.2"/></b></span>';
		}else if(val == 3){
			str = '<span style="color:red;"><b><fmt:message key="order.customs.pushCustomStatus.3"/></b></span>';
		}
		return str;
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
	}
});