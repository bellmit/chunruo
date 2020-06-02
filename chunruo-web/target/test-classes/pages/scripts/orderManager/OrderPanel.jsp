<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('Order', {
	extend: 'Ext.data.Model',
	idProperty: 'orderId',
    fields: [
   		{name: 'orderId',	 	mapping: 'orderId',			type: 'int'},
		{name: 'orderNo',	 	mapping: 'orderNo',			type: 'string'},
		{name: 'isSplitSingle',	mapping: 'isSplitSingle',	type: 'bool'},
		{name: 'isSubOrder',	mapping: 'isSubOrder',		type: 'bool'},
		{name: 'isGroupProduct',mapping: 'isGroupProduct',	type: 'bool'},
		{name: 'status',	 	mapping: 'status',			type: 'int'},
		{name: 'refundStatus',	mapping: 'refundStatus',    type: 'int'},
		{name: 'storeName',	 	mapping: 'storeName',		type: 'string'},
		{name: 'tradeNo',	 	mapping: 'tradeNo',			type: 'string'},
		{name: 'userId',	 	mapping: 'userId',			type: 'int'},
		{name: 'userName',	 	mapping: 'userName',		type: 'string'},
		{name: 'orderAmount',	mapping: 'orderAmount',		type: 'string'},
		{name: 'payAmount',	 	mapping: 'payAmount',		type: 'string'},
		{name: 'productAmount',	mapping: 'productAmount',	type: 'string'},
		{name: 'postage',	 	mapping: 'postage',			type: 'string'},
		{name: 'tax',	 		mapping: 'tax',				type: 'string'},
		{name: 'paymentType',	mapping: 'paymentType',		type: 'int'},
		{name: 'productNumber',	mapping: 'productNumber',	type: 'string'},
		{name: 'cancelMethod',	mapping: 'cancelMethod',	type: 'int'},
		{name: 'isCheck',	 	mapping: 'isCheck',			type: 'bool'},
		{name: 'isPushErp',	 	mapping: 'isPushErp',		type: 'bool'},
		{name: 'isNeedCheckPayment',mapping: 'isNeedCheckPayment',type: 'bool'},
		{name: 'isDelete',	 	mapping: 'isDelete',		type: 'bool'},
		{name: 'wareHouseId',	mapping: 'wareHouseId',		type: 'int'},
		{name: 'wareHouseName',	mapping: 'wareHouseName',	type: 'string'},
		{name: 'productType',	mapping: 'productType',		type: 'int'},
		{name: 'topUserId',		mapping: 'topUserId',		type: 'int'},
		{name: 'topStoreName',	mapping: 'topStoreName',	type: 'string'},
		{name: 'profitTop',	 	mapping: 'profitTop',		type: 'string'},
		{name: 'profitSub',	 	mapping: 'profitSub',		type: 'string'},
		{name: 'consignee',	 	mapping: 'consignee',		type: 'string'},
		{name: 'consigneePhone',mapping: 'consigneePhone',	type: 'string'},
		{name: 'address',	 	mapping: 'address',			type: 'string'},
		{name: 'provinceId',	mapping: 'provinceId',		type: 'int'},
		{name: 'province',	 	mapping: 'province',		type: 'string'},
		{name: 'cityId',	 	mapping: 'cityId',			type: 'int'},
		{name: 'city',	 		mapping: 'city',			type: 'string'},
		{name: 'areaId',	 	mapping: 'areaId',			type: 'int'},
		{name: 'cityarea',	 	mapping: 'cityarea',		type: 'string'},
		{name: 'identityNo',	mapping: 'identityNo',		type: 'string'},
		{name: 'interuptedStatus',mapping: 'interuptedStatus',	type: 'int'},
		{name: 'isPaymentSucc',mapping: 'isPaymentSucc',	type: 'bool'},
		{name: 'payTime',	 	mapping: 'payTime',			type: 'string'},
		{name: 'sentTime',	 	mapping: 'sentTime',		type: 'string'},
		{name: 'deliveryTime',	mapping: 'deliveryTime',	type: 'string'},
		{name: 'cancelTime',	mapping: 'cancelTime',		type: 'string'},
		{name: 'complateTime',	mapping: 'complateTime',	type: 'string'},
		{name: 'refundTime',	mapping: 'refundTime',		type: 'string'},
		{name: 'buyerMessage',	mapping: 'buyerMessage',	type: 'string'},
		{name: 'remarks',	 	mapping: 'remarks',			type: 'string'},
		{name: 'isShareBuy',    mapping: 'isShareBuy',      type: 'bool'},
		{name: 'isDirectPushErp',mapping: 'isDirectPushErp',type: 'bool'},
		{name: 'isPushCustoms',	mapping: 'isPushCustoms',	type: 'bool'},
		{name: 'remarks',	 	mapping: 'remarks',			type: 'string'},
		{name: 'acceptPayName',	mapping: 'acceptPayName',	type: 'string'},
		{name: 'createTime',	mapping: 'createTime',		type: 'string'},
		{name: 'updateTime',	mapping: 'updateTime',		type: 'string'},
		{name: 'isUserCoupon',	mapping: 'isUserCoupon',	type: 'bool'},
		{name: 'isSeckillProduct',	mapping: 'isSeckillProduct',	type: 'bool'},
		{name: 'preferentialAmount',mapping: 'preferentialAmount',type: 'string'},
		{name: 'isUseAccount',	mapping: 'isUseAccount',	type: 'bool'},
		{name: 'payAccountAmount',mapping: 'payAccountAmount',type: 'string'}
	]
});

Ext.define('MyExt.orderManager.OrderPanel', {
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
			model: 'Order',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/order/list.json"/>',
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
		
		this.refundStatusStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        	    {id: 0, name: '<fmt:message key="order.refund.refundStatus_0"/>'},
        		{id: 1, name: '<fmt:message key="order.refund.refundStatus_1"/>'},
        		{id: 2, name: '<fmt:message key="order.refund.refundStatus_2"/>'},
        		{id: 3, name: '<fmt:message key="order.refund.refundStatus_3"/>'},
        		{id: 4, name: '<fmt:message key="order.refund.refundStatus_4"/>'},
        		{id: 5, name: '<fmt:message key="order.refund.refundStatus_5"/>'},
        		{id: 6, name: '<fmt:message key="order.refund.refundStatus_6"/>'},
        		{id: 9, name: '<fmt:message key="order.refund.refundStatus_9"/>'}
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
		
		this.productWarehouseStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
				<c:forEach var="warehouse" varStatus="status" items="${allProductWarehouseLists}" >
				{id: ${warehouse.value.warehouseId}, name: '${warehouse.value.name}'}<c:if test="${!vs.last}">,</c:if>
				</c:forEach>
			]
		});
		
		this.columns = [
			{text: '<fmt:message key="order.orderId"/>', dataIndex: 'orderId', width: 70, locked: true, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.orderNo"/>', dataIndex: 'orderNo', width: 160, locked: true, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.storeName"/>', dataIndex: 'userName', width: 160, locked: true, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.status"/>', dataIndex: 'status', width: 80, locked: true, sortable : true,
        		align: 'center',
        		renderer: this.rendererStatus,
        		filter: {
					xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.statusStore,
			        queryMode: 'local',
			        typeAhead: true
				}
        	},
        	{text: '<fmt:message key="order.refund.status"/>', dataIndex: 'refundStatus', width: 80, locked: true, sortable : true,
        		align: 'center',
        		renderer: this.rendererRefundStatus,
        		filter: {
					xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.refundStatusStore,
			        queryMode: 'local',
			        typeAhead: true
				}
        	},
        	{text: '<fmt:message key="order.orderAmount"/>', dataIndex: 'orderAmount', width: 90, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.payAmount"/>', dataIndex: 'payAmount', width: 90, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.productAmount"/>', dataIndex: 'productAmount', width: 90, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.tradeNo"/>', dataIndex: 'tradeNo', width: 200, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.userId"/>', dataIndex: 'userId', width: 80, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.userName"/>', dataIndex: 'userName', width: 120, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.postage"/>', dataIndex: 'postage', width: 80, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.isPaymentSucc"/>', dataIndex: 'isPaymentSucc', width: 100, sortable : true,
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
        	{text: '<fmt:message key="order.productNumber"/>', dataIndex: 'productNumber', width: 80, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.storeTopId"/>', dataIndex: 'topUserId', width: 80, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.consignee"/>', dataIndex: 'consignee', width: 100, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.consigneePhone"/>', dataIndex: 'consigneePhone', width: 100, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.address"/>', dataIndex: 'address', width: 240, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.province"/>', dataIndex: 'province', width: 80, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.city"/>', dataIndex: 'city', width: 80, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.cityarea"/>', dataIndex: 'cityarea', width: 80, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.payTime"/>', dataIndex: 'payTime', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.sentTime"/>', dataIndex: 'sentTime', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.deliveryTime"/>', dataIndex: 'deliveryTime', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.cancelTime"/>', dataIndex: 'cancelTime', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.complateTime"/>', dataIndex: 'complateTime', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.buyerMessage"/>', dataIndex: 'buyerMessage', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.remarks"/>', dataIndex: 'remarks', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.createTime"/>', dataIndex: 'createTime', width: 140, sortable : true,
        		filter: {
	        		xtype: 'dateSelectorPicker',
					editable: false
		        }
        	},
        	{text: '<fmt:message key="order.updateTime"/>', dataIndex: 'updateTime', width: 140, sortable : true,
        		filter: {
	        		xtype: 'dateSelectorPicker',
					editable: false
		        }
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
	    
	    this.tbar = [
	    <jkd:haveAuthorize access="/order/dealWithIntercept.json">
	    {
        	text: '<fmt:message key="order.sent"/>', 
        	iconCls: 'add', 	
        	handler: this.sent, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
        ];
        
        this.east =  Ext.create('MyExt.orderManager.OrderTabPanel', {
        	orderList: this.orderList,
		 	viewer: this.viewer,
		 	layout: 'card',
	        border: false,
	       	region: 'east',
	        width: this.xWidth/2,
	        minWidth: this.xWidth/2,
	        split: true,
	        header: false,
	        hidden: true
        });
	    
    	this.items = [this.orderList, this.east];	
		this.east.hide();
    	this.callParent(arguments);
    	
    	<jkd:haveAuthorize access="/order/list.json">
    	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.orderList.filters)
			});
	    }, this);
	    this.store.load();
	    </jkd:haveAuthorize>
	    
	    this.gsm = this.orderList.getSelectionModel();
	    this.orderList.on('itemdblclick', function(view, record, item, index, e, eOpts) {
	    	<jkd:haveAuthorize access="/order/getOrderById.json">
	    	this.east.transferData(this.east, record, this.body.dom.clientWidth);
	    	this.east.show();
	    	</jkd:haveAuthorize>
	    }, this);
    },
    
    cleanSearch : function(){
    	this.keywordField.setRawValue();
		this.store.loadPage(1);
    },
    
    rendererStuts : function(val){
		if(val == true) {
            return '<b><fmt:message key="button.yes"/></b>';
        }else{
            return '<fmt:message key="button.no"/>';
        }
	},
	
	rendererRefundStatus : function(val){
		var str =  "";
		if(val == 1){
			str = '<b><fmt:message key="order.refund.refundStatus_1"/></b>';
		}else if(val == 2 ){
			str = '<b><fmt:message key="order.refund.refundStatus_2"/></b>';
		}else if(val == 3){
			str = '<b><fmt:message key="order.refund.refundStatus_3"/></b>';
		}else if(val == 4){
			str = '<b><fmt:message key="order.refund.refundStatus_4"/></b>';
		}else if(val == 5){
			str = '<b><fmt:message key="order.refund.refundStatus_5"/></b>';
		}else if(val == 6){
			str = '<b><fmt:message key="order.refund.refundStatus_6"/></b>';
		}else{
		    str = '<b><fmt:message key="order.refund.refundStatus_0"/></b>';
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
	},
	
	rendererBuyWayType : function(val){ 
		var str =  "";
		if(val == 0){
			str = '<fmt:message key="order.buyWayType.express"/>';
		}else if(val == 1){
			str = '<fmt:message key="order.buyWayType.expressBySelf"/>';
		}
		return str;
	},
	
	rendererProductType : function(val){
		var str =  "";
		if(val == 1){
			str = '<fmt:message key="order.productType.1"/>';
		}else if(val==2){
			str = '<fmt:message key="order.productType.2"/>';
		}else if(val==3){
			str = '<fmt:message key="order.productType.3"/>';
		}
		return str;
	},
	
	rendererStatus : function(val){
		var str =  "";
		if(val == 1){
			str = '<b><fmt:message key="order.status.1"/></b>';
		}else if(val == 2 ){
			str = '<b><fmt:message key="order.status.2"/></b>';
		}else if(val == 3){
			str = '<b><fmt:message key="order.status.3"/></b>';
		}else if(val == 4){
			str = '<b><fmt:message key="order.status.4"/></b>';
		}else if(val == 5){
			str = '<b><fmt:message key="order.status.5"/></b>';
		}else if(val == 6){
			str = '<b><fmt:message key="order.status.6"/></b>';
		}else if(val == 7){
			str = '<b><fmt:message key="order.status.7"/></b>';
		}
		return str;
	},
	
	rendererCancelMethod : function(val){
		var str =  "";
		if(val == 0){
			str = '<fmt:message key="order.default"/>';
		}else if(val == 1){
			str = '<fmt:message key="order.timeOut"/>';
		}else if(val == 2){
			str = '<fmt:message key="order.cancelBySeller"/>';
		}else if(val == 3){
			str = '<fmt:message key="order.cancelByBuyer"/>';
		}
		return str;
	},
	
	exportOrder : function(){
		var orderExcelPanel = Ext.create('MyExt.orderManager.OrderExportExcel', {id: 'OrderExportExcel@OrderExportExcel', title: '<fmt:message key="button.add"/>'});
		var buttons = [{
			text: '<fmt:message key="order.exporter.xls"/>',
			handler: function(){
	            if(orderExcelPanel.form.isValid()){
	            	Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="order.export.confirm"/>', function(e){
						if(e == 'yes'){
						var formValues=orderExcelPanel.getForm().getValues();
							var begin = Ext.util.Format.date(Ext.getCmp('beginTime').getValue(), 'Y-m-d H:i:s');
							var status = 1;
							var end = Ext.util.Format.date(Ext.getCmp('endTime').getValue(), 'Y-m-d H:i:s');
							var phone = Ext.getCmp('phone').getValue();
							window.location.href = "/order/exportOrderExcel.json?beginTime="+begin+"&endTime="+end+"&phone=" + phone+"&status=" + status;
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
		openWin('<fmt:message key="button.exporter.xls"/>', orderExcelPanel, buttons, 400, 200);
	},
	
	exportTeam : function(){
		var orderExcelPanel = Ext.create('MyExt.orderManager.OrderExportExcel', {id: 'OrderExportExcel@OrderExportExcel', title: '<fmt:message key="button.add"/>'});
		var buttons = [{
			text: '<fmt:message key="order.team.xls"/>',
			handler: function(){
	            if(orderExcelPanel.form.isValid()){
	            	Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="order.export.confirm"/>', function(e){
						if(e == 'yes'){
						var formValues=orderExcelPanel.getForm().getValues();
							var begin = Ext.util.Format.date(Ext.getCmp('beginTime').getValue(), 'Y-m-d H:i:s');
							
							var end = Ext.util.Format.date(Ext.getCmp('endTime').getValue(), 'Y-m-d H:i:s');
							var phone = Ext.getCmp('phone').getValue();
							var status = 5;
							window.location.href = "/order/exportOrderExcel.json?beginTime="+begin+"&endTime="+end+"&phone=" + phone+"&status=" + status;
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
		openWin('<fmt:message key="button.exporter.xls"/>', orderExcelPanel, buttons, 400, 200);
	},

	exportSaleOrder : function(){
		var excelPanel = Ext.create('MyExt.orderManager.ExportExcel', {id: 'ExportExcel@ExportExcel', title: '<fmt:message key="button.add"/>'});
		var buttons = [{
			text: '<fmt:message key="button.exporter.xls"/>',
			handler: function(){
	            if(excelPanel.form.isValid()){
	            	Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="order.export.confirm"/>', function(e){
						if(e == 'yes'){
							var columns = [];
							var begin = Ext.util.Format.date(Ext.getCmp('beginTime').getValue(), 'Y-m-d H:i:s');
							var end = Ext.util.Format.date(Ext.getCmp('endTime').getValue(), 'Y-m-d H:i:s');
					
				    		Ext.Array.each(this.orderList.getColumns(), function(object, index, countriesItSelf) {
				    			if(object.dataIndex){
				    				columns.push({key: object.dataIndex, value: object.text});
				    			}
							});
							
							Ext.Ajax.request({
					        	url: this.orderList.store.proxy.url,
					         	method: 'post',
								scope: this,
								params:{
									columns: Ext.JSON.encode(columns), 
									filters: Ext.JSON.encode(this.orderList.filters), 
									beginTime: begin, 
									endTime: end, 
									isExporter: true, 
									isSaleExporter: true
								},
					          	success: function(response){
			          				var responseObject = Ext.JSON.decode(response.responseText);
			          				if(responseObject.success == true 
			          						&& responseObject.filePath != null 
			          						&& responseObject.filePath != ''){
			                        	window.location.href  = '<c:url value="/order/downLoadExportFile.msp?filePath="/>' + responseObject.filePath;
			                        	popWin.close();
			                        }else{
										showFailMsg('<fmt:message key="order.nomessage"/>', 4);
									}
								}
					     	});
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
		openWin('<fmt:message key="button.exporter.xls"/>', excelPanel, buttons, 400, 175);
	},
	
	 orderRemarks : function(){
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		
		if(records.length > 1){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.only.one.record"/>');
			return;
		}
		
		var textAreaMsg = Ext.create('Ext.form.TextArea', {
 			fieldLabel: '<fmt:message key="order.remarks.confirm"/>',
 			labelAlign: 'top',
    		allowBlank: false,
       		anchor: '100%'
 		});	
	 	
	 	var buttons = [{ 	
			text: '<fmt:message key="button.save"/>', 
			scope: this,  
	        handler: function(){
				if(textAreaMsg.getValue() == null || textAreaMsg.getValue().length == 0){ 
					showWarnMsg('<fmt:message key="order.close.confirm"/>', 8);
					return;
				}
				
		     	Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
					if(e == 'yes'){
				     	Ext.Ajax.request({
				        	url: '<c:url value="/order/orderRemarks.json"/>',
				         	method: 'post',
							scope: this,
							params:{orderId: records[0].data.orderId,remarks: textAreaMsg.getValue()},
				          	success: function(response){
		          				var responseObject = Ext.JSON.decode(response.responseText);
		          				if(responseObject.error == false){
		                        	popFormWin.close();
		                       		showSuccMsg(responseObject.message);
		                        	this.store.loadPage(1);
		                    		this.gsm.deselectAll();
								}else{
									this.show();
									showFailMsg(responseObject.message, 4);
								}
							}
				     	})
				     }
				}, this)
	      	}
		},{
			text: '<fmt:message key="button.cancel"/>',
			handler : function(){popFormWin.close();},
			scope: this
		}];  
 		openFormWin('<fmt:message key="ajax.waitTitle"/>', [textAreaMsg], buttons, 300, 170);
    },
    
    orderSettlement : function(){
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}else if(records.length > 1){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.only.one.record"/>');
			return;
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="order.settlement.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/order/orderSettlement.json"/>',
		         	method: 'post',
					scope: this,
					params:{orderId: records[0].data.orderId},
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
    
    sent : function(){
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.orderId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="order.dealWithIntercept.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/order/sentOrder.json"/>',
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
    
    downLoadIdCard : function(){
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.orderId);	
		}
		
		var columns = [];
		Ext.Array.each(this.orderList.getColumns(), function(object, index, countriesItSelf) {
				    			if(object.dataIndex){
				    				columns.push({key: object.dataIndex, value: object.text});
				    			}
							});
							
		
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="order.idcard.confirm"/>', function(e){
			if(e == 'yes'){
		     	Ext.Ajax.request({
					        	url: '<c:url value="/order/createIdCardZip.json"/>',
					         	method: 'post',
								scope: this,
								params:{
								    idListGridJson: Ext.JSON.encode(rowsData),
									columns: Ext.JSON.encode(columns)
								},
					          	success: function(response){
			          				var responseObject = Ext.JSON.decode(response.responseText);
			          				if(responseObject.success == true 
			          						&& responseObject.filePath != null 
			          						&& responseObject.filePath != ''){
			                        	window.location.href  = '<c:url value="/order/downLoadIdCard.json?filePath="/>' + responseObject.filePath;
			                        	popWin.close();
			                        }else{
										showFailMsg('<fmt:message key="order.nomessage"/>', 4);
									}
								}
					     	});
	     	}
	 	}, this) 
    },
    
    exporter : function(){
		var excelPanel = Ext.create('MyExt.orderManager.ExportExcel', {id: 'ExportExcel@ExportExcel', title: '<fmt:message key="button.add"/>'});
		var buttons = [{
			text: '<fmt:message key="button.exporter.xls"/>',
			handler: function(){
	            if(excelPanel.form.isValid()){
	            	Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="order.export.confirm"/>', function(e){
						if(e == 'yes'){
							var columns = [];
							var begin = Ext.util.Format.date(Ext.getCmp('beginTime').getValue(), 'Y-m-d H:i:s');
							var end = Ext.util.Format.date(Ext.getCmp('endTime').getValue(), 'Y-m-d H:i:s');
					
				    		Ext.Array.each(this.orderList.getColumns(), function(object, index, countriesItSelf) {
				    			if(object.dataIndex){
				    				columns.push({key: object.dataIndex, value: object.text});
				    			}
							});
							
							Ext.Ajax.request({
					        	url: this.orderList.store.proxy.url,
					         	method: 'post',
								scope: this,
								params:{
									columns: Ext.JSON.encode(columns), 
									filters: Ext.JSON.encode(this.orderList.filters), 
									beginTime: begin, 
									endTime: end, 
									isExporter: true
								},
					          	success: function(response){
			          				var responseObject = Ext.JSON.decode(response.responseText);
			          				if(responseObject.success == true 
			          						&& responseObject.filePath != null 
			          						&& responseObject.filePath != ''){
			                        	window.location.href  = '<c:url value="/order/downLoadExportFile.msp?filePath="/>' + responseObject.filePath;
			                        	popWin.close();
			                        }else{
										showFailMsg('<fmt:message key="order.nomessage"/>', 4);
									}
								}
					     	});
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
		openWin('<fmt:message key="button.exporter.xls"/>', excelPanel, buttons, 400, 175);
	},
});