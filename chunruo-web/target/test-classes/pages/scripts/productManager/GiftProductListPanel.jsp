<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('GiftProduct', {
	extend: 'Ext.data.Model',
	idProperty: 'giftProductId',
    fields: [
		{name: 'giftProductId',		mapping: 'giftProductId',		          type: 'int'},
		{name: 'type',		        mapping: 'type',		                  type: 'int'},
		{name: 'couponId',		    mapping: 'couponId',		              type: 'string'},
		{name: 'productId',		    mapping: 'productId',		              type: 'int'},
		{name: 'productSpecId',	    mapping: 'productSpecId',                 type: 'int'},
		{name: 'isSpecProduct',	    mapping: 'isSpecProduct',		          type: 'bool'},
		{name: 'stockNumber',	 	mapping: 'stockNumber',                   type: 'int'},
		{name: 'isEnable',	        mapping: 'isEnable',                      type: 'bool'},
		{name: 'name',	            mapping: 'name',                          type: 'string'},
		{name: 'productName',	    mapping: 'productName',                   type: 'string'},
		{name: 'productTags',	    mapping: 'productTags',                   type: 'string'},
	    {name: 'productCode',	    mapping: 'productCode',                   type: 'string'},
	    {name: 'productSku',	    mapping: 'productSku',                    type: 'string'},
	    {name: 'mainProductIds',	mapping: 'mainProductIds',                type: 'string'},
	    {name: 'adminUserName',	    mapping: 'adminUserName',                 type: 'string'},
		{name: 'createTime',	 	mapping: 'createTime',		              type: 'string'},
		{name: 'updateTime',	 	mapping: 'updateTime',		              type: 'string'}
    ]
});

Ext.define('MyExt.productManager.GiftProductListPanel', {
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
			model: 'GiftProduct',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/giftProduct/list.json"/>',
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
			{text: '<fmt:message key="gift.product.giftProductId"/>', dataIndex: 'giftProductId', width: 50, sortable: true, filter: {xtype: 'textfield'}},
			{text: '<fmt:message key="gift.product.type" />', dataIndex: 'type', width: 100, sortable: true,locked: true, 
            	filter: {
            		xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: Ext.create('Ext.data.Store', {
						autoDestroy: true,
						model: 'InitModel',
						data: [
						     {id: '2', name: '<fmt:message key="gift.product.type2"/>'},
							 {id: '1', name: '<fmt:message key="gift.product.type1"/>'},
						]
					}),
			        queryMode: 'local',
			        typeAhead: true
            	},
            	renderer: this.messageTypeRenderer
            },
			{text: '<fmt:message key="gift.product.giftName"/>',   dataIndex: 'name',   width: 100, sortable : true, filter: {xtype: 'textfield'}}, 
		    {text: '<fmt:message key="gift.product.couponId"/>',   dataIndex: 'couponId',   width: 100, sortable : true, filter: {xtype: 'textfield'}}, 
			{text: '<fmt:message key="gift.product.productName"/>',   dataIndex: 'productName',   width: 360, sortable : true, filter: {xtype: 'textfield'}}, 
			{text: '<fmt:message key="gift.product.productTags"/>',   dataIndex: 'productTags',   width: 100, sortable : true, filter: {xtype: 'textfield'}}, 
			{text: '<fmt:message key="gift.product.stockNumber"/>', dataIndex: 'stockNumber',       width: 80, sortable: true, filter: {xtype: 'textfield'}},
			{text: '<fmt:message key="gift.product.isEnable"/>', dataIndex: 'isEnable', width: 65, sortable : true,
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
			{text: '<fmt:message key="gift.product.productSku"/>',   dataIndex: 'productSku',   width: 80, sortable: true,  filter: {xtype: 'textfield'}},
			{text: '<fmt:message key="gift.product.productCode"/>', dataIndex: 'productCode',       width: 80, sortable: true, filter: {xtype: 'textfield'}},
			{text: '<fmt:message key="gift.product.mainProductIds"/>', dataIndex: 'mainProductIds',       width: 120, sortable: true, filter: {xtype: 'textfield'}},
			{text: '<fmt:message key="gift.product.adminUserName"/>', dataIndex: 'adminUserName',       width: 80, sortable: true, filter: {xtype: 'textfield'}},
			{text: '<fmt:message key="gift.product.createTime"/>',  dataIndex: 'createTime',  width: 150, sortable : true, align: 'center',filter: {xtype: 'textfield'}},
        	{text: '<fmt:message key="gift.product.updateTime"/>',  dataIndex: 'updateTime',  width: 150, sortable : true,filter: {xtype: 'textfield'}},
        ];
        
        this.tbar = [
        <jkd:haveAuthorize access="/discovery/list.json">
        {
	    	text: '<fmt:message key="button.refresh"/>', 
	        iconCls: 'refresh', 	
	       	handler: function(){this.store.reload();}, 
	       	scope: this
	  	}
	  	<c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/giftProduct/saveGiftProduct.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
	  	'-', {
        	text: '<fmt:message key="discovery.save"/>', 
        	iconCls: 'enable', 	
        	handler: this.saveGiftProduct, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/giftProduct/updateIsEnable.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        {
        	text: '<fmt:message key="product.task.stop"/>', 
        	iconCls: 'disabled', 	
        	handler: this.stopGiftProduct, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/giftProduct/updateIsEnable.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        {
        	text: '<fmt:message key="product.task.start"/>', 
        	iconCls: 'enable', 	
        	handler: this.startGiftProduct, 
        	scope: this
        }
        </jkd:haveAuthorize>
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
	    
	    this.east =  Ext.create('MyExt.productManager.GiftProductTabPanel', {
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
	    
	    <jkd:haveAuthorize access="/discovery/list.json">
	   	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.productList.filters)
			});
	    }, this);
	    this.store.load();   
	    </jkd:haveAuthorize>
	    
	    this.productList.on('itemdblclick', function(view, record, item, index, e, eOpts) {
	    	<jkd:haveAuthorize access="/discovery/getDiscoveryById.json">
	    	this.east.transferData(this.east, record, this.body.dom.clientWidth);
	    	this.east.show();
	    	</jkd:haveAuthorize>
	    }, this); 
    },
    
    saveGiftProduct : function(){
    	var giftProductFormPanel = Ext.create('MyExt.productManager.GiftProductFormPanel', {
			id: 'add@giftProductFormPanel' + this.id,
    		viewer: this.viewer,
   	 	});
    	
    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
		      
		    	if(giftProductFormPanel.form.isValid()){
					Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
		             		giftProductFormPanel.form.submit({
		                 		waitMsg: 'Loading...',
		                 		url: '<c:url value="/giftProduct/saveGiftProduct.json"/>',
		               			scope: this,
		               			params:{},
		               			success: function(form, action) {
		                   			var responseObject = Ext.JSON.decode(action.response.responseText);
		                   			if(responseObject.error == false){
		                  				showSuccMsg(responseObject.message);
		                  				this.store.load();
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
      	openWin('<fmt:message key="button.add"/>', giftProductFormPanel, buttons,400, 300);
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
    
    
   	
   	stopGiftProduct : function(){
   	    var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}
		
		for(var i = 0; i < records.length; i++){	
			rowsData.push(records[i].data.giftProductId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="recharge.template.confirm.stop"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/giftProduct/updateIsEnable.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData),isEnable :false},
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
    
     startGiftProduct : function(){
        var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}
		for(var i = 0; i < records.length; i++){	
			rowsData.push(records[i].data.giftProductId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="recharge.template.confirm.start"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/giftProduct/updateIsEnable.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData),isEnable :true },
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