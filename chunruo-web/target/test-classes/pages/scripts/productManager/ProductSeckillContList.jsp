<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('SeckillCont', {
	extend: 'Ext.data.Model',
    fields: [
    	{name: 'objectId',			mapping: 'objectId',		type: 'int'},
    	{name: 'productId',			mapping: 'productId',		type: 'int'},
    	{name: 'productSpecId',		mapping: 'productSpecId',	type: 'int'},
    	{name: 'isSpceProduct',		mapping: 'isSpceProduct',	type: 'bool'},
    	{name: 'productCode',	 	mapping: 'productCode',		type: 'string'},
    	{name: 'productTags',	 	mapping: 'productTags',		type: 'string'},
		{name: 'name',	 			mapping: 'name',			type: 'string'},
		{name: 'stockNumber',	 	mapping: 'stockNumber',		type: 'int'},
		{name: 'seckillLimitNumber',mapping: 'seckillLimitNumber',type: 'int'},
		{name: 'priceWholesale',	mapping: 'priceWholesale',	type: 'string'},
		{name: 'priceRecommend',	mapping: 'priceRecommend',	type: 'string'},
		{name: 'priceCost',	 		mapping: 'priceCost',		type: 'string'},
		{name: 'seckillPrice',	 	mapping: 'seckillPrice',	type: 'string'},
		{name: 'seckillProfit',	 	mapping: 'seckillProfit',	type: 'string'},
		{name: 'seckillTotalStock',	mapping: 'seckillTotalStock',type: 'int'},
		{name: 'seckillSalesNumber',mapping: 'seckillSalesNumber',type: 'int'},
		{name: 'seckillLockNumber',mapping: 'seckillLockNumber',  type: 'int'}
    ]
});

Ext.define('MyExt.productManager.ProductSeckillContList', {
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
    features: [{
    	ftype: 'groupingsummary',
        groupHeaderTpl: '{name}',
        showSummaryRow: false
   	}],
    viewConfig: {
        stripeRows: true,
        enableTextSelection: true
    },

	initComponent : function(config) {
		Ext.apply(this, config);
		
        this.store = Ext.create('Ext.data.Store', {
	        autoLoad: false,
			autoDestroy: true,
			sortOnLoad: true,
			remoteSort: true,
			model: 'SeckillCont',
			groupField: 'name',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/seckill/getProductListBySeckillId.json"/>',
				reader: {
					type : 'json',
                	root: 'data',
                	totalProperty: 'totalCount'
            	}
			}
		});
		
		this.columns = [
			{text: '<fmt:message key="product.wholesale.wholesaleId"/>', dataIndex: 'objectId', width: 65, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="product.wholesale.productCode"/>', dataIndex: 'productCode', width: 150, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="product.wholesale.productTags"/>', dataIndex: 'productTags', width: 150, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="product.wholesale.seckillPrice"/>', dataIndex: 'seckillPrice', width: 65, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="product.wholesale.seckillProfit"/>', dataIndex: 'seckillProfit', width: 65, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="product.wholesale.seckillLimitNumber"/>', dataIndex: 'seckillLimitNumber', width: 65, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="product.wholesale.seckillTotalStock"/>', dataIndex: 'seckillTotalStock', width: 65, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="product.wholesale.seckillSalesNumber"/>', dataIndex: 'seckillSalesNumber', width: 65, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="product.wholesale.seckillLockNumber"/>', dataIndex: 'seckillLockNumber', width: 65, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="product.wholesale.quantity"/>', dataIndex: 'stockNumber', width: 65, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="product.wholesale.priceWholesale"/>', dataIndex: 'priceWholesale', width: 65, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="product.wholesale.priceRecommend"/>', dataIndex: 'priceRecommend', width: 65, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="product.wholesale.priceCost"/>', dataIndex: 'priceCost', width: 65, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	}
        ];
	    
	    this.tbar = [
	    <jkd:haveAuthorize access="/seckill/getProductListBySeckillId.json">
	    {
	    	text: '<fmt:message key="button.refresh"/>', 
	        iconCls: 'refresh', 	
	       	handler: function(){this.store.reload();}, 
	       	scope: this
	  	}
	  	<c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/seckill/saveSeckillProduct.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
	  	'-',{
        	text: '<fmt:message key="button.add"/>', 
        	iconCls: 'add', 	
        	handler: this.addProduct,
        	hidden: true,
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/seckill/delectSeckillProductById.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
        	text: '<fmt:message key="button.delete"/>', 
        	iconCls: 'delete', 	
        	handler: this.deleteProduct,
        	hidden: true,
        	scope: this
        }
        </jkd:haveAuthorize>
        ];
    	this.callParent();
    	
    	this.gsm = this.getSelectionModel();
    	<jkd:haveAuthorize access="/seckill/getProductListBySeckillId.json">
    	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	seckillId: this.record.data.seckillId
			});
	    }, this);
	    </jkd:haveAuthorize>
    	
    	<jkd:haveAuthorize access="/seckill/saveSeckillProduct.json">
    	this.on('itemdblclick', this.onDbClick, this);
    	</jkd:haveAuthorize>
    },
    
    onDbClick : function(view, record, item, index, e, eOpts){
		var	seckillId = this.record.data.seckillId;
    	var productSeckillSpceList = Ext.create('MyExt.productManager.ProductSeckillSpceList', {
			id: 'edit@ProductSeckillSpceList' + this.id,
			isEditor: true,
			header: false,
    		productId: record.data.productId,
    		seckillSort:0
   	 	});
   	 	productSeckillSpceList.loadData();
    	
    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
				var seckillContListData = [];
				productSeckillSpceList.store.each(function(record) {
		    		seckillContListData.push(record.data);
				}, this);
    	
    	        var sortObj = Ext.ComponentQuery.query('numberfield[name="seckillSort"]')[0];
    	        var seckillSort = sortObj.getValue() ;
				Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
					if(e == 'yes'){
	             		Ext.Ajax.request({
	                 		waitMsg: 'Loading...',
	                 		url: '<c:url value="/seckill/saveSeckillProduct.json"/>',
	               			scope: this,
	               			params:{seckillId: seckillId, seckillSort: seckillSort,seckillContList: Ext.JSON.encode(seckillContListData)},
	               			success: function(response) {
	                   			var responseObject = Ext.JSON.decode(response.responseText);
	                   			if(responseObject.success == true){
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
			},
			scope: this
		},{
			text: '<fmt:message key="button.cancel"/>',
			handler : function(){popWin.close();},
			scope: this
		}];
      	openWin(Ext.String.format('<fmt:message key="button.left"/>{0}<fmt:message key="button.right"/>', record.data.name), productSeckillSpceList, buttons, 800, 400);
    },
    
    addProduct : function(){
		var	seckillId = this.record.data.seckillId;
    	var productSeckillSpceList = Ext.create('MyExt.productManager.ProductSeckillSpceList', {
			id: 'add@ProductSeckillSpceList' + this.id,
			header: false,
    		viewer: this.viewer
   	 	});
    	
    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
				var seckillContListData = [];
				productSeckillSpceList.store.each(function(record) {
		    		seckillContListData.push(record.data);
				}, this);
				
				Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
					if(e == 'yes'){
	             		Ext.Ajax.request({
	                 		waitMsg: 'Loading...',
	                 		url: '<c:url value="/seckill/saveSeckillProduct.json"/>',
	               			scope: this,
	               			params:{seckillId: seckillId, seckillContList: Ext.JSON.encode(seckillContListData)},
	               			success: function(response) {
	                   			var responseObject = Ext.JSON.decode(response.responseText);
	                   			if(responseObject.success == true){
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
			},
			scope: this
		},{
			text: '<fmt:message key="button.cancel"/>',
			handler : function(){popWin.close();},
			scope: this
		}];
      	openWin('<fmt:message key="button.add"/>', productSeckillSpceList, buttons, 800, 400);
    },
    
    deleteProduct : function(){
    	var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.productId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="delete.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/seckill/delectSeckillProductById.json"/>',
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
	
    transferData : function(record){
    	this.record = record;
    	this.store.load();
    	this.down('[iconCls=add]').show();
    	this.down('[iconCls=delete]').show();
    },
    
    fontRenderer: function(value, meta, record) {
    	meta.style = 'overflow:auto;padding: 3px 6px;text-overflow: ellipsis;white-space: nowrap;white-space:normal;line-height:24px;font-weight:bold;';      
       	return value;     
   	} 
});