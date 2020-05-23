<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>


Ext.define('PurchaseLimit', {
	extend: 'Ext.data.Model',
	idProperty: 'limitId',
    fields: [
		{name: 'limitId',	 		mapping: 'limitId',		type: 'int'},
		{name: 'type',	 		    mapping: 'type',		type: 'int'},
		{name: 'limitNumber',	 	mapping: 'limitNumber',	type: 'int'},
		{name: 'productId',			mapping: 'productId',	type: 'int'},
		{name: 'hours',			    mapping: 'hours',	    type: 'int'},
		{name: 'v1Number',			mapping: 'v1Number',	type: 'int'},
		{name: 'v2Number',			mapping: 'v2Number',	type: 'int'},
		{name: 'isEnable',	        mapping: 'isEnable',    type: 'bool'},
		{name: 'adminUserName',		mapping: 'adminUserName',type: 'string'},
		{name: 'createTime',		mapping: 'createTime',	type: 'string'},
		{name: 'updateTime',		mapping: 'updateTime',	type: 'string'}
	
	]
});

Ext.define('MyExt.couponManager.SubscriberLimitListPanel', {
   	extend : 'Ext.panel.Panel',
    requires : ['Ext.ux.grid.GridHeaderFilters','Ext.ux.grid.Exporter'],
    header: false,
	closable: true,
	columnLines: true,
	animCollapse: true,
	layout: 'border',
   	defaults: {  
    	split: true,    
        collapsible: false
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
			model: 'PurchaseLimit',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/purchaseLimit/list.json?type=1"/>',
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
		
		this.columns = [
			{text: '<fmt:message key="purchase.limit.limitid"/>', dataIndex: 'limitId', width: 70, locked: true, sortable : true,
        		align: 'center'
        	},
        	{text: '<fmt:message key="product.task.isEnable"/>', dataIndex: 'isEnable', width: 65, sortable : true,locked: true, 
        		align: 'center',
        		renderer: function(value, meta, record) {    
			       	if(value == false) {
			            return '<span style="color:green;"><fmt:message key="button.no"/></span>';
			        }else{
			            return '<span style="color:red;"><b><fmt:message key="button.yes"/></b></span>';
			        }  
			   	},
        		filter: {
					xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			         store: Ext.create('Ext.data.Store', {
						autoDestroy: true,
						model: 'InitModel',
						data: [
							{id: '1', name: '<fmt:message key="button.yes"/>'},
							{id: '0', name: '<fmt:message key="button.no"/>'},
						]
					}),
			        queryMode: 'local',
			        typeAhead: true
				}
        	},
        	{text: '<fmt:message key="purchase.limit.limitNumber"/>', dataIndex: 'limitNumber', width: 300,  sortable : true,locked: true,
        		align: 'center'
        	},
        	{text: '<fmt:message key="purchase.limit.adminUserName"/>', dataIndex: 'adminUserName', width: 300,  sortable : true,locked: true,
        		align: 'center'
        	},
        	{text: '<fmt:message key="system.roolingNotice.createTime"/>', dataIndex: 'createTime', width: 180, locked: true, sortable : true,
        		align: 'center'
        	},
        	{text: '<fmt:message key="system.roolingNotice.updateTime"/>', dataIndex: 'updateTime', width: 180, locked: true, sortable : true,
        		align: 'center'
			}
			 ];
        
        this.tbar = [{
	    	text: '<fmt:message key="button.refresh"/>', 
	        iconCls: 'refresh', 	
	       	handler: function(){this.store.reload();}, 
	       	scope: this
	  	},'-', {
        	text: '<fmt:message key="discovery.save"/>', 
        	iconCls: 'add', 	
        	handler: this.savePurchaseLimit, 
        	scope: this
        },{
        	text: '<fmt:message key="discovery.delete"/>', 
        	iconCls: 'delete', 	
        	handler: this.deletePurchaseLimit, 
        	scope: this
        }, {
        	text: '<fmt:message key="product.task.stop"/>', 
        	iconCls: 'disabled', 	
        	handler: this.stopPurchaseLimit, 
        	scope: this
        }, {
        	text: '<fmt:message key="product.task.start"/>', 
        	iconCls: 'enable', 	
        	handler: this.startPurchaseLimit, 
        	scope: this
        }];
        
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
		
		this.productList = Ext.create('Ext.grid.GridPanel', {
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
		    bbar: this.pagingToolbar,
		    plugins: ['gridHeaderFilters','gridexporter'],
		    viewConfig: {
		        stripeRows: true,
		        enableTextSelection: true
		    }
	    });     
	    
	    this.east =  Ext.create('MyExt.couponManager.DiscoveryTabPanel', {
        	productList: this.productList,
		 	viewer: this.viewer,
		 	layout: 'card',
	        border: false,
	       	region: 'east',
	        width: 550,
	        split: true,
	        header: false,
	        hidden: true
        });
    	
    	
    	this.gsm = this.productList.getSelectionModel();
    	this.items = [this.productList, this.east];	
		this.east.hide();
		this.callParent(arguments);
	    
	   	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.productList.filters)
			});
	    }, this);
	    this.store.load();   
	    
	    this.productList.on('itemdblclick', function(view, record, item, index, e, eOpts) {
	    	
	    }, this); 
    },
    
    savePurchaseLimit : function(){
       var records = this.gsm.getSelection();
       if(records.length > 1 ){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="user.approve.records"/>');
			return;
		}
		
		var limitId = '';
		var limitNumber = '';
		if(records.length == 1){
			limitId = records[0].data.limitId;
			limitNumber = records[0].data.limitNumber;
		}
    	var subscriberFormPanel = Ext.create('MyExt.couponManager.SubscriberFormPanel', {
			id: 'add@subscriberFormPanel' + this.id,
    		viewer: this.viewer,
    		limitNumber:limitNumber,
    		limitId:limitId
   	 	});
    	
    	
    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
		    	if(subscriberFormPanel.form.isValid()){
					Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
		             		subscriberFormPanel.form.submit({
		                 		waitMsg: 'Loading...',
		                 		url: '<c:url value="/purchaseLimit/savePurchaseLimit.json"/>',
		               			scope: this,
		               			params:{type:1},
		               			success: function(form, action) {
		                   			var responseObject = Ext.JSON.decode(action.response.responseText);
		                   			if(responseObject.error == false){
		                  				showSuccMsg(responseObject.message);
		                  				this.store.loadPage(1);
                        				this.gsm.deselectAll();
		                  				popWin.close();
									}else{
										showFailMsg(responseObject.message, 4);
									}
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
      	openWin('<fmt:message key="button.add"/>', subscriberFormPanel, buttons,200, 200);
    },    
    
    deleteDiscovery : function() {
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	

		for(var i = 0; i < records.length; i++){	
			rowsData.push(records[i].data.discoveryId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm.agree"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/discovery/deleteDiscovery.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData)},
		          	success: function(response){
          				var responseObject = Ext.JSON.decode(response.responseText);
                        if(responseObject.success == true){
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
    
    syncProductMaterialImage : function(){
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}else if(records.length > 1){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.only.one.record"/>');
			return;
		}	
		
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.discoveryId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="discovery.sync.product.material.image"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/discovery/syncProductMaterialImage.json"/>',
		         	method: 'post',
					scope: this,
					params:{discoveryId: records[0].data.discoveryId},
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
   	
  	stopPurchaseLimit : function(){
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}else if(records.length > 1){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.only.one.record"/>');
			return;
		}	
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="product.task.confirm.stop"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/purchaseLimit/updateIsEnable.json"/>',
		         	method: 'post',
					scope: this,
					params:{limitId: records[0].data.limitId,isEnable :false},
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
   	
   deletePurchaseLimit : function() {
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	

		for(var i = 0; i < records.length; i++){	
			rowsData.push(records[i].data.limitId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="limit.confirm.delete"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/purchaseLimit/deletePurchaseLimit.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData)},
		          	success: function(response){
          				var responseObject = Ext.JSON.decode(response.responseText);
                        if(responseObject.success == true){
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

     startPurchaseLimit : function(){
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}else if(records.length > 1){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.only.one.record"/>');
			return;
		}	
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="product.task.confirm.start"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/purchaseLimit/updateIsEnable.json"/>',
		         	method: 'post',
					scope: this,
					params:{limitId: records[0].data.limitId,isEnable :true },
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
   	
   	fontRenderer: function(value, meta, record) {
    	meta.style = 'overflow:auto;padding: 3px 6px;text-overflow: ellipsis;white-space: nowrap;white-space:normal;line-height:24px;font-weight:bold;';      
       	return value;     
   	},
   	
   	couponTypeRenderer : function(val){
   	    if(val == 1) {
            return '<b><fmt:message key="coupon.couponType1"/></b>';
         }else if(val == 2){
            return '<b><fmt:message key="coupon.couponType2"/></b>';
         }
   	},
   	
   	receiveTypeRenderer: function(val){
   	    if(val == 0) {
            return '<b><fmt:message key="coupon.receiveType0"/></b>';
         }else if(val == 1){
            return '<b><fmt:message key="coupon.receiveType1"/></b>';
         }else if(val == 2){
            return '<b><fmt:message key="coupon.receiveType2"/></b>';
         }
   	},
   	
   	attributeRenderer : function(val){
   	    if(val == 0) {
            return '<b><fmt:message key="order.evaluate.status0"/></b>';
         }else if(val == 1){
            return '<b><fmt:message key="order.evaluate.status1"/></b>';
         }else if(val == 2){
            return '<b><fmt:message key="order.evaluate.status2"/></b>';
         }else if(val == 3){
            return '<b><fmt:message key="order.evaluate.status3"/></b>';
         }
   	},
	
	rendererStuts : function(val){
		if(val == true) {
            return '<b><fmt:message key="button.yes"/></b>';
        }else{
            return '<fmt:message key="button.no"/>';
        }
	},
});