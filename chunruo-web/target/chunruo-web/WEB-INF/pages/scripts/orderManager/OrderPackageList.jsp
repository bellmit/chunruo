<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('OrderPackage', {
	extend: 'Ext.data.Model',
    idProperty: 'packageId',
    fields: [
		{name: 'packageId',		mapping: 'packageId',		type: 'int'},
		{name: 'expressCode',	mapping: 'expressCode',		type: 'string'},
		{name: 'expressNo',		mapping: 'expressNo',		type: 'string'},
		{name: 'expressCompany',mapping: 'expressCompany',	type: 'string'},
		{name: 'isHandler',		mapping: 'isHandler',		type: 'bool'},
		{name: 'createTime',	mapping: 'createTime',		type: 'string'}
    ]
});

Ext.define('MyExt.orderManager.OrderPackageList', {
    extend : 'Ext.grid.GridPanel',
    alias: ['widget.orderPackageList'],
    requires : [],
	region: 'center',
	autoScroll: true,   
	multiSelect: true,
	selType: 'checkboxmodel',
	columnLines: true,
	animCollapse: false,
    enableLocking: true,
    viewConfig: {
        stripeRows: true,
        enableTextSelection: true
    },
    
	initComponent : function(config) {
		Ext.apply(this, config);
		
		this.store = Ext.create('Ext.data.Store', {
    		autoDestroy: true,
        	model: 'OrderPackage'
		});
		
		this.columns = [
	    	{text: '<fmt:message key="order.item.itemId"/>', dataIndex: 'packageId', width: 65, sortable : true},
	    	{text: '<fmt:message key="order.express.expressNo"/>', dataIndex: 'expressNo', width: 150, sortable : true},
			{text: '<fmt:message key="order.express.expressCode"/>', dataIndex: 'expressCode', width: 90, sortable : true},
			{text: '<fmt:message key="order.express.expressCompany"/>', dataIndex: 'expressCompany', width: 200, sortable : true},
			{text: '<fmt:message key="order.express.isHandler"/>', dataIndex: 'isHandler', width: 80, sortable : true,
				renderer : function(val){
					if(val == true) {
			            return '<b><fmt:message key="button.yes"/></b>';
			        }else{
			            return '<fmt:message key="button.no"/>';
			        }
				}
			},
			{text: '<fmt:message key="order.express.createTime"/>', dataIndex: 'createTime', width: 130, sortable : true}
        ]; 
        
        this.tbar = [
        <jkd:haveAuthorize access="/order/orderOutLibrary.json">
        {
	        text: '<fmt:message key="order.button.out.library"/>',
	        iconCls: 'add',
	        handler : this.orderOutLibrary,
	       	scope: this
	    }
	    <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/order/editOrderPackage.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
	    {
	        text: '<fmt:message key="order.button.edit.package"/>',
	        iconCls: 'add',
	        handler : this.editOrderPackage,
	       	scope: this
	    }
	    </jkd:haveAuthorize>
	    ];
    	this.callParent();
    	this.gsm = this.getSelectionModel();
    },
    
    orderOutLibrary : function(){
    	var orderEidtorExpressPanel = Ext.create('MyExt.orderManager.OrderEidtorExpressPanel', {
			id: 'orderEidtorExpressPanel@' + this.id,
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
							params:{orderId: this.record.data.orderId, expressMap: Ext.JSON.encode(expressMap)},
				          	success: function(response){
		          				var responseObject = Ext.JSON.decode(response.responseText);
		          				if(responseObject.success == true){
		                       		showSuccMsg(responseObject.message);
		                       		this.tabPanel.loadData();
		                       		popWin.close();
								}else{
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
      	openWin(Ext.String.format('<fmt:message key="order.button.out.library.title"/>', this.record.data.orderNo), orderEidtorExpressPanel, buttons, 480, 220);
    },
    
     editOrderPackage : function() {
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}else if(records.length > 1){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.only.one.record"/>');
			return;
		}	

		var orderEidtorExpressPanel = Ext.create('MyExt.orderManager.OrderEidtorExpressPanel', {
			id: 'orderEidtorExpressPanel@' + this.id,
    		viewer: this.viewer,
    		isEdit:true
   	 	});
    	
    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
				var isCheckSucc = true;
				var expressMap = [];
				var expressNo = '';
				var expressCode = '';
		    	orderEidtorExpressPanel.items.each(function(form) {
	        		if(!form.isValid()){
	        			isCheckSucc = false;
	        		}else{
	        			expressCode = form.down('combobox').getValue();
	        		    expressNo = form.down('[name=expressNo]').getValue();
	        		}
				}, this);
				
				if(!isCheckSucc){
					showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
					return;
				}	
				
				Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
					if(e == 'yes'){
				     	Ext.Ajax.request({
				        	url: '<c:url value="/order/editOrderPackage.json"/>',
				         	method: 'post',
							scope: this,
							params:{orderId: this.record.data.orderId, packageId: records[0].data.packageId, expressCode : expressCode,expressNo:expressNo},
				          	success: function(response){
		          				var responseObject = Ext.JSON.decode(response.responseText);
		          				if(responseObject.success == true){
		                       		showSuccMsg(responseObject.message);
		                       		this.tabPanel.loadData();
		                       		popWin.close();
		                    		this.gsm.deselectAll();
								}else{
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
      	openWin(Ext.String.format('<fmt:message key="order.button.out.library.title"/>', this.record.data.orderNo), orderEidtorExpressPanel, buttons, 480, 220);
	},
});