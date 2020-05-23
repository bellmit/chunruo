<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('OrderExamine', {
	extend: 'Ext.data.Model',
	idProperty: 'orderId',
    fields: [
   		{name: 'orderId',	 	mapping: 'orderId',			type: 'int'},
		{name: 'orderNo',	 	mapping: 'orderNo',			type: 'string'},
		{name: 'isSplitSingle',	mapping: 'isSplitSingle',	type: 'bool'},
		{name: 'isSubOrder',	mapping: 'isSubOrder',		type: 'bool'},
		{name: 'refundStatus',	mapping: 'refundStatus',    type: 'int'},
		{name: 'isGroupProduct',mapping: 'isGroupProduct',	type: 'bool'},
		{name: 'status',	 	mapping: 'status',			type: 'int'},
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
		{name: 'remarks',	 	mapping: 'remarks',			type: 'string'},
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
		{name: 'errorMsg',	 	mapping: 'errorMsg',		type: 'string'},
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

Ext.define('MyExt.orderManager.OrderExaminePanel', {
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
			model: 'OrderExamine',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/order/orderExamineList.json"/>',
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
        	{text: '<fmt:message key="order.sync.errorMsg"/>', dataIndex: 'errorMsg', width: 160, sortable : true,
        		filter: {xtype: 'textfield'}
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
        	{text: '<fmt:message key="order.tradeNo"/>', dataIndex: 'tradeNo', width: 200, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.userId"/>', dataIndex: 'userId', width: 80, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.userName"/>', dataIndex: 'userName', width: 120, sortable : true,
        		filter: {xtype: 'textfield'}
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
        	{text: '<fmt:message key="order.storeTopId"/>', dataIndex: 'topUserId', width: 80, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.storeTopName"/>', dataIndex: 'topStoreName', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.profitTop"/>', dataIndex: 'profitTop', width: 80, sortable : true,
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
		<jkd:haveAuthorize access="/order/handlerPushErp.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
        	text: '<fmt:message key="order.button.pushERP"/>', 
        	iconCls: 'Packageadd', 	
        	handler: this.orderPushERP, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/order/orderHanderProductCorrection.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
		{
        	text: '<fmt:message key="order.hander.correction"/>', 
        	iconCls: 'Bug', 	
        	handler: this.orderHanderCorrection, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/order/directMailOrderWaitLibrary.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
        	text: '<fmt:message key="order.directMail.waitLibrary"/>', 
        	iconCls: 'app_manager', 	
        	handler: this.orderDirectMailWaitLibrary, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/order/orderDirectMailExport.msp">
		<c:if test="${isHaveAuthorize}">,</c:if>
        {
			text: '<fmt:message key="order.directMail.export"/>',
			iconCls: 'Bricks', 	
			menu: [
				<c:forEach var="warehouse" varStatus="status" items="${allHanderWarehouseLists}" >
				{ 
					text: '${warehouse.value.name}',
					warehouseId: '${warehouse.value.warehouseId}',
					handler: this.orderDirectMailExport,
					scope: this
				}<c:if test="${!vs.last}">,</c:if>
				</c:forEach>
		    ],
			scope: this
		}
		<c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/order/orderRemarks.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
		{
        	text: '<fmt:message key="order.button.remarks"/>', 
        	iconCls: 'app_manager', 	
        	handler: this.orderRemarks, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/order/orderExamineList.json?isExporter=true">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '->',{
        	text: '<fmt:message key="button.exporter.xls"/>', 
        	iconCls: 'excel', 	
        	handler: function(){
        		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="order.directMail.export.confirm"/>', function(e){
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
    	
    	<jkd:haveAuthorize access="/order/orderExamineList.json">
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
    
    orderDirectMailWaitLibrary : function(){
    	var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.orderId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="submit.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/order/directMailOrderWaitLibrary.json"/>',
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
    
    orderHanderCorrection : function(){
    	var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.orderId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="submit.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/order/orderHanderProductCorrection.json"/>',
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
    
    orderDirectMailExport : function(item){
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="order.export.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/order/orderDirectMailExport.msp"/>',
		         	method: 'post',
					scope: this,
					params:{warehouseId: item.warehouseId},
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
    
    orderPushERP : function(){
    	var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.orderId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="submit.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/order/handlerPushErp.json"/>',
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
 			fieldLabel: '<fmt:message key="order.remarks"/>',
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
		}else if(val == 9){
			str = '<b><fmt:message key="order.refund.refundStatus_9"/></b>';
		}else{
		    str = '<b><fmt:message key="order.refund.refundStatus_0"/></b>';
		}
		return str;
	},
});