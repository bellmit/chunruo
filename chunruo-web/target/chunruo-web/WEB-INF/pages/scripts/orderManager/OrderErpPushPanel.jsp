<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('OrderErpPush', {
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
		{name: 'isDelete',	 	mapping: 'isDelete',		type: 'bool'},
		{name: 'wareHouseId',	mapping: 'wareHouseId',		type: 'int'},
		{name: 'wareHouseName',	mapping: 'wareHouseName',	type: 'string'},
		{name: 'productType',	mapping: 'productType',		type: 'int'},
		{name: 'topUserId',		mapping: 'topUserId',		type: 'int'},
		{name: 'topStoreName',	mapping: 'topStoreName',	type: 'string'},
		{name: 'profitTop',	 	mapping: 'profitTop',		type: 'string'},
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
		{name: 'buyerMessage',	mapping: 'buyerMessage',	type: 'string'},
		{name: 'remarks',	 	mapping: 'remarks',			type: 'string'},
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

Ext.define('MyExt.orderManager.OrderErpPushPanel', {
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
			model: 'OrderErpPush',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/order/orderErpPushList.json"/>',
				reader: {
					type : 'json',
                	root: 'data',
                	totalProperty: 'totalCount'
            	}
			},
			sorters: [{
	            property: 'updateTime',
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
        	{text: '<fmt:message key="order.storeName"/>', dataIndex: 'storeName', width: 160, locked: true, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.wareHouseName"/>', dataIndex: 'wareHouseId', width: 120, sortable : true,
        		align: 'center',
				renderer: function(val){
			    	<c:forEach var="warehouse" varStatus="status" items="${allProductWarehouseLists}" >
			    	if(val == ${warehouse.value.warehouseId}){
			    		return '${warehouse.value.name}';
			    	}
					</c:forEach>
					return val;
			    },
        		filter: {
					xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.productWarehouseStore,
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
        	{text: '<fmt:message key="order.isUseAccount"/>', dataIndex: 'isUseAccount', width: 70, sortable : true,
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
        	{text: '<fmt:message key="order.payAccountAmount"/>', dataIndex: 'payAccountAmount', width: 90, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.isUserCoupon"/>', dataIndex: 'isUserCoupon', width: 70, sortable : true,
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
        	{text: '<fmt:message key="order.preferentialAmount"/>', dataIndex: 'preferentialAmount', width: 90, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.isSubOrder"/>', dataIndex: 'isSubOrder', width: 70, sortable : true,
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
        	{text: '<fmt:message key="order.isPushErp"/>', dataIndex: 'isPushErp', width: 90, sortable : true,
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
        	{text: '<fmt:message key="order.profitTop"/>', dataIndex: 'profitTop', width: 80, sortable : true,
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
        	{text: '<fmt:message key="order.isDirectPushErp"/>', dataIndex: 'isDirectPushErp', width: 100, sortable : true,
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
        	{text: '<fmt:message key="order.postage"/>', dataIndex: 'postage', width: 80, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.tax"/>', dataIndex: 'tax', width: 80, sortable : true,
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
        	{text: '<fmt:message key="order.paymentType"/>', dataIndex: 'paymentType', width: 90, sortable : true,
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
        	{text: '<fmt:message key="order.isSeckill"/>', dataIndex: 'isSeckillProduct', width: 90, sortable : true,
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
        	{text: '<fmt:message key="product.wholesale.isGroup"/>', dataIndex: 'isGroupProduct', width: 80, sortable : true,
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
        	{text: '<fmt:message key="order.acceptPayName"/>', dataIndex: 'acceptPayName', width: 100, sortable : false,
        		align: 'center',
				filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.productNumber"/>', dataIndex: 'productNumber', width: 80, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.cancelMethod"/>', dataIndex: 'cancelMethod', width: 100, sortable : true,
        		align: 'center',
        		renderer: this.rendererCancelMethod,
        		filter: {
					xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.cancelMethodStore,
			        queryMode: 'local',
			        typeAhead: true
				}
        	},
        	{text: '<fmt:message key="order.productType"/>', dataIndex: 'productType', width: 80, sortable : true,
        		align: 'center',
        		renderer: this.rendererProductType,
        		filter: {
					xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.productTypeStore,
			        queryMode: 'local',
			        typeAhead: true
				}
        	},
        	{text: '<fmt:message key="order.storeTopId"/>', dataIndex: 'topUserId', width: 80, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.storeTopName"/>', dataIndex: 'topStoreName', width: 140, sortable : true,
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
        	{text: '<fmt:message key="order.interuptedStatus"/>', dataIndex: 'interuptedStatus', width: 140, sortable : true,
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
        	text: '<fmt:message key="order.dealWithIntercept"/>', 
        	iconCls: 'Arrowinlonger', 	
        	handler: this.dealWithInterceptOrder, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/order/expressNumberImport.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
        	text: '<fmt:message key="order.button.expressCode.import"/>', 
        	iconCls: 'Boxworld', 	
        	handler: this.expressCodeImport, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/order/orderErpPushList.json?isExporter=true">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '->',{
        	text: '<fmt:message key="button.exporter.xls"/>', 
        	iconCls: 'excel', 	
        	handler: function(){
        		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="order.export.confirm"/>', function(e){
					if(e == 'yes'){
						var columns = [];
			    		Ext.Array.each(this.orderList.getColumns(), function(object, index, countriesItSelf) {
			    			if(object.dataIndex){
			    				columns.push({key: object.dataIndex, value: object.text});
			    			}
						});
						
						Ext.Ajax.request({
				        	url: this.orderList.store.proxy.url,
				         	method: 'post',
							scope: this,
							params:{columns: Ext.JSON.encode(columns), filters: Ext.JSON.encode(this.orderList.filters), isExporter: true},
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
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/userSys/editUser.json,/userSys/saveAdminUser.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
            text: '<fmt:message key="order.exporter.xls"/>', 
        	iconCls: 'excel', 	
        	handler: this.exportOrder, 
        	scope: this
        }
        </jkd:haveAuthorize>
        ];
        
        this.east =  Ext.create('MyExt.orderManager.OrderTabPanel', {
        	orderList: this.orderList,
		 	viewer: this.viewer,
		 	layout: 'card',
	        border: false,
	       	region: 'east',
	        width: 550,
	        split: true,
	        header: false,
	        hidden: true
        });
	    
    	this.items = [this.orderList, this.east];	
		this.east.hide();
    	this.callParent(arguments);
    	
    	<jkd:haveAuthorize access="/order/orderErpPushList.json">
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
	    
	    this.orderList.on('rowcontextmenu', function(view, record, tr, rowIndex, e, eOpts) {
	    	e.preventDefault();
	    	
	    	<jkd:haveAuthorize access="/order/orderOutLibrary.json">
            Ext.create('Ext.menu.Menu', {
               	items: [{  
               		text: '<fmt:message key="order.button.out.library"/>',
               		iconCls: 'Build', 	
               		record: record,
		        	handler: this.orderOutLibrary, 
		        	scope: this  
               	}]  
          	}).showAt(e.getXY());  
          	</jkd:haveAuthorize>
	    }, this);
    },
    
    orderOutLibrary : function(item){
    	var orderEidtorExpressPanel = Ext.create('MyExt.orderManager.OrderEidtorExpressPanel', {
			id: 'orderInteruptedFormPanel@' + this.id,
    		viewer: this.viewer
   	 	});
    	
    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
				var isCheckSucc = true;
				var expressMap = [];
		    	orderEidtorExpressPanel.items.each(function(form) {
	        		if(!form.isValid()){
	        			isCheckSucc = false;
	        		}else{
	        			var expressCode = form.down('combobox').getValue();
	        			var expressNo = form.down('[name=expressNo]').getValue();
	        			expressMap.push({key: expressNo, value: expressCode});
	        		}
				}, this);
				
				if(!isCheckSucc){
					showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
					return;
				}	
				
				Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
					if(e == 'yes'){
				     	Ext.Ajax.request({
				        	url: '<c:url value="/order/orderOutLibrary.json"/>',
				         	method: 'post',
							scope: this,
							params:{orderId: item.record.data.orderId, expressMap: Ext.JSON.encode(expressMap)},
				          	success: function(response){
		          				var responseObject = Ext.JSON.decode(response.responseText);
		          				if(responseObject.success == true){
		                        	popWin.close();
		                       		showSuccMsg(responseObject.message);
		                        	this.store.loadPage(1);
								}else{
									this.show();
									showFailMsg(responseObject.message, 4);
								}
							}
				     	})
				     }
				}, this)
			},
			scope: this
		},{
			text: '<fmt:message key="button.cancel"/>',
			handler : function(){popWin.close();},
			scope: this
		}];
      	openWin(Ext.String.format('<fmt:message key="order.button.out.library.title"/>', item.record.data.orderNo), orderEidtorExpressPanel, buttons, 480, 220);
    },
    
    orderCloseStatus : function(){
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.orderId);	
		}
		
		var textAreaMsg = Ext.create('Ext.form.TextArea', {
 			fieldLabel: '<fmt:message key="order.close.confirm"/>',
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
				        	url: '<c:url value="/order/orderClose.json"/>',
				         	method: 'post',
							scope: this,
							params:{idListGridJson: Ext.JSON.encode(rowsData), message: textAreaMsg.getValue()},
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
							var status = 6;
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
    
    expressCodeImport : function(){
    	var formPanel = Ext.create('Ext.form.Panel', {
		    width: 400,
		    header: false,
		    labelHidder: true,
		    items: [{
		        xtype: 'filefield',
		        name: 'file',
		        msgTarget: 'side',
		        allowBlank: false,
		        anchor: '100%',
		        buttonText: '<fmt:message key="order.import.select.file"/>'
		    }]
		});
		
		var buttons = [{
			text: '<fmt:message key="button.confirm"/>',
			handler : function(){
	            if(formPanel.isValid()){
	            	formPanel.submit({
	                    url: '<c:url value="/import/baseImportFile.json"/>',
	                    waitMsg: '<fmt:message key="ajax.loading"/>',
	                    scope: this,
	                    success: function(form, action) {
	                    	var responseObject = Ext.JSON.decode(action.response.responseText);
			                if(responseObject.error == false || responseObject.error == 'false'){
	                        	popFormWin.close();
								var testPanel = Ext.create('MyExt.BaseImportFileGrid');
								testPanel.setObject(responseObject);
								
								var importButtons = [{
									text: '<fmt:message key="button.confirm"/>',
									handler : function(){
										var rowsData = [];		
										if(testPanel.store.getCount() == 0){
											showFailMsg('<fmt:message key="errors.noRecord"/>', 4);
											return;
										}	
										for(var i = 0; i < testPanel.store.getCount(); i++){	
											rowsData.push(testPanel.store.getAt(i).data);			
										}
		
							            Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.import.confirm"/>', function(e){
											if(e == 'yes'){
									        	Ext.Ajax.request({
										        	url: '<c:url value="/order/expressNumberImport.json"/>',
										         	method: 'post',
													scope: this,
													params:{dataGridJson: Ext.JSON.encode(rowsData), headerGridJson: Ext.JSON.encode(testPanel.keyValueHeaderData)},
										          	success: function(xresponse){
												    	var xresponseObject = Ext.JSON.decode(xresponse.responseText);
								          				if (xresponseObject.success == true){
								          					showSuccMsg(xresponseObject.message);
								          					popWin.close();
								          					this.store.loadPage(1);
								          				}else{
								          					showFailMsg(xresponseObject.message, 4);
								          				}
													}
										     	})
									        }
							 			}, this)
									},
									scope: this
								},{
									text: '<fmt:message key="button.cancel"/>',
									handler : function(){popWin.close();},
									scope: this
								}];
								openWin('<fmt:message key="order.expressNumber.import"/>', testPanel, importButtons, 750, 450);
	                        }else{
								showFailMsg(responseObject.message, 4);
							}
	                    }
	                });
	            }
			},
			scope: this
		},{
			text: '<fmt:message key="button.cancel"/>',
			handler : function(){popFormWin.close();},
			scope: this
		}];
		openFormWin('<fmt:message key="order.expressNumber.import"/>', formPanel, buttons, 420, 100);
    },
    
    dealWithInterceptOrder : function(){
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
		        	url: '<c:url value="/order/dealWithIntercept.json"/>',
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
			str = '<fmt:message key="order.status.1"/>';
		}else if(val == 2 ){
			str = '<fmt:message key="order.status.2"/>';
		}else if(val == 3){
			str = '<fmt:message key="order.status.3"/>';
		}else if(val == 4){
			str = '<fmt:message key="order.status.4"/>';
		}else if(val == 5){
			str = '<fmt:message key="order.status.5"/>';
		}else if(val == 6){
			str = '<fmt:message key="order.status.6"/>';
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
});