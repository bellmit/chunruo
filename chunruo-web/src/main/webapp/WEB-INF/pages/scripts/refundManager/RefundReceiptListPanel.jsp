<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('RefundReceipt', {
	extend: 'Ext.data.Model',
	idProperty: 'refundId',
    fields: [
		{name: 'refundId',	 		mapping: 'refundId',		type: 'int'},
		{name: 'refundNumber',	 	mapping: 'refundNumber',	type: 'string'},
		{name: 'orderId',			mapping: 'orderId',			type: 'string'},
		{name: 'orderNo',			mapping: 'orderNo',			type: 'string'},
		{name: 'orderItemId',		mapping: 'orderItemId',		type: 'string'},
		{name: 'productId',	 		mapping: 'productId',		type: 'string'},
		{name: 'productPrice',	 	mapping: 'productPrice',	type: 'string'},
		{name: 'refundCount',	 	mapping: 'refundCount',		type: 'string'},
		{name: 'refundAmount',	 	mapping: 'refundAmount',	type: 'string'},
		{name: 'refundType',	 	mapping: 'refundType',		type: 'string'},
		{name: 'reasonId',			mapping: 'reasonId',		type: 'string'},
		{name: 'userId',	 		mapping: 'userId',			type: 'string'},
		{name: 'storeId',			mapping: 'storeId',			type: 'string'},
		{name: 'userMobile',		mapping: 'userMobile',		type: 'string'},
		{name: 'refundStatus',	 	mapping: 'refundStatus',	type: 'string'},
		{name: 'refundExplain',		mapping: 'refundExplain',	type: 'string'},
		{name: 'image1',			mapping: 'image1',			type: 'string'},
		{name: 'image2',			mapping: 'image2',			type: 'string'},
		{name: 'image3',			mapping: 'image3',			type: 'string'},
		{name: 'expressNumber',	 	mapping: 'expressNumber',	type: 'string'},
		{name: 'expressCompany',	mapping: 'expressCompany',	type: 'string'},
		{name: 'isReceive',	 		mapping: 'isReceive',		type: 'bool'},
		{name: 'refusalReason',		mapping: 'refusalReason',	type: 'string'},
		{name: 'createTime',		mapping: 'createTime',		type: 'string'},
		{name: 'updateTime',		mapping: 'updateTime',		type: 'string'},
		{name: 'expressImage1',	 	mapping: 'expressImage1',	type: 'string'},
		{name: 'expressExplain',	mapping: 'expressExplain',	type: 'string'},
		{name: 'expressImage2',		mapping: 'expressImage2',	type: 'string'},
		{name: 'expressImage3',		mapping: 'expressImage3',	type: 'string'},
		{name: 'remarkReasonId',    mapping: 'remarkReasonId',	type: 'int'},
		{name: 'completedTime',		mapping: 'completedTime',	type: 'string'}
	]
});

Ext.define('MyExt.refundManager.RefundReceiptListPanel', {
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
		
		var now = new Date();
   		var expiry = new Date(now.getTime() + 10 * 60 * 1000);
   		Ext.util.Cookies.set('isCheck','0',expiry);
   		
		this.store = Ext.create('Ext.data.Store', {
        	pageSize: 50,
	        autoLoad: false,
			autoDestroy: true,
			sortOnLoad: true,
			remoteSort: true,
			model: 'RefundReceipt',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/refund/list.json?status=4"/>',
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
      
		this.refundTypeStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: 1, name: '<fmt:message key="order.refund.refundType.1"/>'},
        		{id: 2, name: '<fmt:message key="order.refund.refundType.2"/>'},
        		{id: 3, name: '<fmt:message key="order.refund.refundType.3"/>'}
        	]
        });
       
        
        this.refundStatusStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: 1, name: '<fmt:message key="order.refund.refundStatus_1"/>'},
        		{id: 2, name: '<fmt:message key="order.refund.refundStatus_2"/>'},
        		{id: 3, name: '<fmt:message key="order.refund.refundStatus_3"/>'},
        		{id: 4, name: '<fmt:message key="order.refund.refundStatus_4"/>'},
        		{id: 5, name: '<fmt:message key="order.refund.refundStatus_5"/>'},
        		{id: 6, name: '<fmt:message key="order.refund.refundStatus_6"/>'},
        	]
        });
        
		this.columns = [
			{text: '<fmt:message key="order.refund.refundId"/>', dataIndex: 'refundId', width: 70, locked: true, sortable : true,
        		align: 'center',filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.orderNo"/>', dataIndex: 'orderNo', width: 160,  sortable : true,locked: true,
        		align: 'center',filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.storeId"/>', dataIndex: 'storeId', width: 100,  sortable : true,locked: true,
        		align: 'center',filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.storeName"/>', dataIndex: 'storeName', width: 100,  sortable : true,locked: true,
        		align: 'center',filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.refund.refundNumber"/>', dataIndex: 'refundNumber', width: 160,  sortable : true,locked: true,
        		align: 'center',filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.refund.refundAmount"/>', dataIndex: 'refundAmount', width: 100, locked: true, sortable : true,
        		align: 'center',filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.refund.refundType"/>', dataIndex: 'refundType', width: 100, locked: true, sortable : true,
        		align: 'center',
				renderer: this.rendererRefundType,
				filter: {
					xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.refundTypeStore,
			        queryMode: 'local',
			        typeAhead: true
				}
        	},
        	{text: '<fmt:message key="order.refund.refundStatus"/>', dataIndex: 'refundStatus', width: 100,  sortable : true,
        		align: 'center',
        		renderer: this.rendererStatus,
        		filter: {
					xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.refundStatusStore,
			        queryMode: 'local',
			        typeAhead: true
				}
        	},
        	{text: '<fmt:message key="order.refund.isReceive"/>', dataIndex: 'isReceive', width: 100, sortable : true,
        		align: 'center',filter: {xtype: 'textfield'},renderer: this.rendererReceipt
        	},
        	{text: '<fmt:message key="order.refund.createdAt"/>', dataIndex: 'createTime', width: 160, sortable : true,
        		align: 'center',filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.refund.updatedAt"/>', dataIndex: 'updateTime', width: 160,  sortable : true,
        		align: 'center',filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.refund.completedAt"/>', dataIndex: 'completedTime', width: 160,  sortable : true,
        		align: 'center',filter: {xtype: 'textfield'}
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
		this.refundReceiptListBbar = this.pagingToolbar; 
		
    	this.refundReceiptList = Ext.create('Ext.grid.GridPanel', {
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
		    bbar: this.refundReceiptListBbar,
        	plugins: ['gridHeaderFilters'],
		    viewConfig: {
		        stripeRows: true,
		        enableTextSelection: true
		    }
	    });
	    
	    this.tbar = [
	    <jkd:haveAuthorize access="/refund/refundReceipt.json">
	    {
        	text: '<fmt:message key="refund.receipt"/>', 
        	iconCls: 'add', 	
        	handler: this.refundReceipt, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/refund/checkRefund.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
        	text: '<fmt:message key="refund.unreceipt"/>', 
        	iconCls: 'delete', 	
        	handler: this.refundUnreceipt, 
        	scope: this
        }
        </jkd:haveAuthorize>
        ];
        
        this.east =  Ext.create('MyExt.refundManager.RefundTabPanel', {
        	refundList: this.refundReceiptList,
		 	viewer: this.viewer,
		 	layout: 'card',
	        border: false,
	       	region: 'east',
	        width: 550,
	        split: true,
	        header: false,
	        hidden: true
        });
	    
    	this.items = [this.refundReceiptList, this.east];	
		this.east.hide();
    	this.callParent(arguments);
    	
    	<jkd:haveAuthorize access="/refund/list.json">
    	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.refundReceiptList.filters)
			});
	    }, this);
	    this.store.load();
	    </jkd:haveAuthorize>
	    
	    this.gsm = this.refundReceiptList.getSelectionModel();
	    this.refundReceiptList.on('itemdblclick', function(view, record, item, index, e, eOpts) {
	    	<jkd:haveAuthorize access="/refund/getRefundById.json">
	    	this.east.transferData(this.east, record, this.body.dom.clientWidth);
	    	this.east.show();
	    	</jkd:haveAuthorize>
	    }, this);
    },
	
	refundUnreceipt : function(){
		var records = this.gsm.getSelection();
		if(records.length != 1){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		
		var refundId = records[0].data.refundId;	
    	var refundUnreceipt = Ext.create('MyExt.refundManager.RefundUnagreeFormPanel', {title: '<fmt:message key="button.add"/>'});
		var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
	            if(refundUnreceipt.form.isValid()){
	            	Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
						  	refundUnreceipt.form.submit({
			                    waitMsg: 'Loading...',
			                    url: '<c:url value="/refund/checkRefund.json"/>',
			                    scope: this,
			                    params:{refundId: refundId},
			                    success: function(form, action) {
			                        var responseObject = Ext.JSON.decode(action.response.responseText);
			                       	if (responseObject.success == true){
		          						showSuccMsg(responseObject.msg);
		          						this.store.loadPage(1);
		                    			this.gsm.deselectAll();
		          						popWin.close();
		          					}else{
		          						showFailMsg(responseObject.msg, 4);
		          					}
			                    },
			                    failure: function(form, action) {
				                    var responseObject = Ext.JSON.decode(action.response.responseText);
				                    showFailMsg(responseObject.msg, 4);
			                    }
			        		})
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
		openWin('<fmt:message key="button.check"/>', refundUnreceipt, buttons, 500, 200);
    },
    
    refundReceipt : function(){
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.refundId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="refund.receipt.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/refund/refundReceipt.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData)},
		          	success: function(response){
          				var responseObject = Ext.JSON.decode(response.responseText);
          				if (responseObject.success == true){
          					showSuccMsg(responseObject.msg);
							this.store.loadPage(1);
		                    this.gsm.deselectAll();
          				}else{
          					showFailMsg(responseObject.msg, 4);
          				}
					}
		     	})
	     	}
	 	}, this) 
    },
    
	rendererStatus : function(val){
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
		}
		return str;
	},
	
	rendererReceipt : function(val){
		var str =  "";
		if(val){
			str = '<b><fmt:message key="order.refund.receipt"/></b>';
		}
		return str;

	},
	rendererRefundType : function(val){
		var str =  "";
		if(val == '1'){
			str = '<b><fmt:message key="order.refund.refundType.1"/></b>';
		}else if(val == '2'){
			str = '<b><fmt:message key="order.refund.refundType.2"/></b>';
		}else{
		    str = '<b><fmt:message key="order.refund.refundType.3"/></b>';
		}
		return str;

	}
});