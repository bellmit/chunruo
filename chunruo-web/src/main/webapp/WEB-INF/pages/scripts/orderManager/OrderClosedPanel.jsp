<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('OrderCancel', {
	extend: 'Ext.data.Model',
	idProperty: 'orderId',
   	fields: [
   		{name: 'orderId',	 	mapping: 'orderId',			type: 'int'},
		{name: 'orderNo',	 	mapping: 'orderNo',			type: 'string'},
		{name: 'isSplitSingle',	mapping: 'isSplitSingle',	type: 'bool'},
		{name: 'isSubOrder',	mapping: 'isSubOrder',		type: 'bool'},
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

Ext.define('MyExt.orderManager.OrderClosedPanel', {
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
			model: 'OrderCancel',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/order/orderCancelList.json"/>',
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
	    
	    <jkd:haveAuthorize access="/order/orderCancelList.json?isExporter=true">
	    this.tbar = ['->',{
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
        }];
        </jkd:haveAuthorize>
        
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
    	
    	<jkd:haveAuthorize access="/order/orderCancelList.json">
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
});