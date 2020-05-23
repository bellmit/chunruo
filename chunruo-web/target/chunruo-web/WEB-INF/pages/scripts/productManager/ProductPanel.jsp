<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('Product', {
	extend: 'Ext.data.Model',
    fields: [
		{name: 'productId',			mapping: 'productId',		type: 'int'},
		{name: 'name',	 			mapping: 'name',			type: 'string'},
		{name: 'stockNumber',	 	mapping: 'stockNumber',		type: 'int'},
		{name: 'priceWholesale',	mapping: 'priceWholesale',	type: 'string'},
		{name: 'priceRecommend',	mapping: 'priceRecommend',	type: 'string'},
		{name: 'priceCost',	 		mapping: 'priceCost',		type: 'string'},
		{name: 'categoryFid',	 	mapping: 'categoryFid',		type: 'int'},
		{name: 'maxLimitNumber',    mapping: 'maxLimitNumber',  type: 'int'},
		{name: 'categoryFidName',	mapping: 'categoryFidName',	type: 'string'},
		{name: 'categoryId',	 	mapping: 'categoryId',		type: 'int'},
		{name: 'categoryIdName',	mapping: 'categoryIdName',	type: 'string'},
		{name: 'categoryPathName',	mapping: 'categoryPathName',type: 'string'},
		{name: 'weigth',	 		mapping: 'weigth',			type: 'string'},
		{name: 'productCode',	 	mapping: 'productCode',		type: 'string'},
		{name: 'productSku',	 	mapping: 'productSku',		type: 'string'},
		{name: 'wareHouseId',	 	mapping: 'wareHouseId',		type: 'int'},
		{name: 'wareHouseName',	 	mapping: 'wareHouseName',	type: 'string'},
		{name: 'image',	 			mapping: 'image',			type: 'string'},
		{name: 'salesNumber',	 	mapping: 'salesNumber',		type: 'string'},
		{name: 'status',	 		mapping: 'status',			type: 'bool'},
		{name: 'isSoldout',	 		mapping: 'isSoldout',		type: 'bool'},
		{name: 'isShow',	 		mapping: 'isShow',		    type: 'bool'},
		{name: 'isFresh',	 	    mapping: 'isFresh',	type: 'bool'},
		{name: 'isOpenV2Price',	 	mapping: 'isOpenV2Price',	type: 'bool'},
		{name: 'isOpenV3Price',	 	mapping: 'isOpenV3Price',	type: 'bool'},
		{name: 'productDesc',	 	mapping: 'productDesc',		type: 'string'},
		{name: 'usageMethod',	 	mapping: 'usageMethod',		type: 'string'},
		{name: 'productIntros',	 	mapping: 'productIntros',	type: 'string'},
		{name: 'productType',	 	mapping: 'productType',		type: 'string'},
		{name: 'fxNumber',	 		mapping: 'fxNumber',		type: 'int'},
		{name: 'profit',	 		mapping: 'profit',			type: 'string'},
		{name: 'seckillStatus',	 	mapping: 'seckillStatus',	type: 'int'},
		{name: 'seckillName',	 	mapping: 'seckillName',		type: 'string'},
		{name: 'seckillId',		 	mapping: 'seckillId',		type: 'int'},
		{name: 'seckillStartTime',	mapping: 'seckillStartTime',type: 'string'},
		{name: 'seckillEndTime',	mapping: 'seckillEndTime',	type: 'string'},
		{name: 'seckillTotalStock',	mapping: 'seckillTotalStock',type: 'int'},
		{name: 'seckillSalesNumber',mapping: 'seckillSalesNumber',type: 'int'},
		{name: 'buyerLimit',	 	mapping: 'buyerLimit',		type: 'int'},
		{name: 'countryName',	 	mapping: 'countryName',		type: 'string'},
		{name: 'createTime',	 	mapping: 'createTime',		type: 'string'},
		{name: 'updateTime',	 	mapping: 'updateTime',		type: 'string'},
		{name: 'isGuideProduct',	mapping: 'isGuideProduct',	type: 'boolean'},
		{name: 'isShowPrice',	 	mapping: 'isShowPrice',		type: 'boolean'},
		{name: 'isFreePostage',	 	mapping: 'isFreePostage',	type: 'boolean'},
		{name: 'isShowLevelPrice',	mapping: 'isShowLevelPrice',	type: 'boolean'},
		{name: 'templateId',	 	mapping: 'templateId',		type: 'string'}	,
		{name: 'doubtIds',	 	    mapping: 'doubtIds',		type: 'string'},
		{name: 'brandId',	 	    mapping: 'brandId',			type: 'int'},			
		{name: 'brandName',	 	    mapping: 'brandName',		type: 'string'},
		{name: 'seckillName',	 	mapping: 'seckillName',		type: 'string'},
		{name: 'productEffectIntro',mapping: 'productEffectIntro',type: 'string'},
		{name: 'isDelete',	 		mapping: 'isDelete',		type: 'bool'},					
		{name: 'tagName',			mapping: 'tagName',			type: 'string'},
		{name: 'isGroupProduct',	mapping: 'isGroupProduct',	type: 'bool'},
		{name: 'seckillSort',	 	mapping: 'seckillSort',		type: 'string'},
		{name: 'seckillPrice',	    mapping: 'seckillPrice',	type: 'string'},
		{name: 'seckillProfit',	 	mapping: 'seckillProfit',	type: 'string'},
		{name: 'seckillTotalStock',	mapping: 'seckillTotalStock',type: 'int'},
		{name: 'seckillSalesNumber',mapping: 'seckillSalesNumber',type: 'int'},
		{name: 'seckillLimitNumber',mapping: 'seckillLimitNumber',type: 'int'},
		{name: 'adminUserName',		mapping: 'adminUserName',type: 'string'},
		
    ]
});


Ext.define('MyExt.productManager.ProductPanel', {
   	extend : 'Ext.panel.Panel',
    requires : ['Ext.ux.grid.GridHeaderFilters'],
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
			model: 'Product',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/product/wholesaleList.json"/>',
				reader: {
					type : 'json',
                	root: 'data',
                	totalProperty: 'totalCount'
            	}
			}
		});
		
		this.templateStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
				<c:forEach var="template" varStatus="status" items="${allNoFreePostageTemplateMaps}" >
				{id: ${template.value.templateId}, name: '${template.value.name}'}<c:if test="${!vs.last}">,</c:if>
				</c:forEach>
			]
		});	
		
		this.brandStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
				<c:forEach var="template" varStatus="status" items="${allProductBrandMaps}" >
				{id: ${template.value.brandId}, name: '${template.value.name}'}<c:if test="${!vs.last}">,</c:if>
				</c:forEach>
			]
		});	
		
		this.booleanStore = Ext.create('Ext.data.Store', {
			autoDestroy: true,
			model: 'InitModel',
			data: [
				{id: '1', name: '<fmt:message key="button.yes"/>'},
				{id: '0', name: '<fmt:message key="button.no"/>'},
			]
		});
		
		this.brandStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
				<c:forEach var="brand" varStatus="status" items="${allProductBrandMaps}" >
				{id: ${brand.value.brandId}, name: "${brand.value.name}"}<c:if test="${!vs.last}">,</c:if>
				</c:forEach>
			]
		});
		
		this.columns = [
			{
            	text: '<fmt:message key="product.wholesale.image"/>',
                menuDisabled: true,
                sortable: false,
                xtype: 'actioncolumn',
                align: 'center',
                width: 35,
                higth: 35,
                locked: true,
                dataIndex: 'image',
                renderer: function(value, metadata, record) {
        			return Ext.String.format('<img height="30" width="30" src="{0}"></img>', value);
    			}
            }, 
			{text: '<fmt:message key="product.wholesale.wholesaleId"/>', dataIndex: 'productId', locked: true, width: 65, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="product.wholesale.name"/>', dataIndex: 'name', locked: true, width: 210, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="product.wholesale.status"/>', dataIndex: 'status', locked: true, width: 65, sortable : true,
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
        	{text: '<fmt:message key="product.wholesale.isSoldout"/>', dataIndex: 'isSoldout', locked: true, width: 65, sortable : true,
        		align: 'center',
        		renderer: function(value, meta, record) {    
			       	if(value == true) {
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
							{id: '0', name: '<fmt:message key="button.yes"/>'},
							{id: '1', name: '<fmt:message key="button.no"/>'},
						]
					}),
			        queryMode: 'local',
			        typeAhead: true
				}
        	},
        	{text: '<fmt:message key="product.wholesale.isFresh"/>', dataIndex: 'isFresh', width: 65, sortable : true,
        		align: 'center',
        		renderer: this.isFreePostageRenderer,
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
        	{text: '<fmt:message key="product.wholesale.isFreePostage"/>', dataIndex: 'isFreePostage', width: 65, sortable : true,
        		align: 'center',
        		renderer: this.isFreePostageRenderer,
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
        	{text: '<fmt:message key="product.wholesale.postageTemplate"/>', dataIndex: 'templateId', width: 160, sortable : true,
        		align: 'center',
				renderer: function(val){
			    	<c:forEach var="template" varStatus="status" items="${allNoFreePostageTemplateMaps}" >
			    	if(val == ${template.value.templateId}){
			    		return '${template.value.name}';
			    	}
					</c:forEach>
					return val;
			    },
        		filter: {
					xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.templateStore,
			        queryMode: 'local',
			        typeAhead: true
				}
        	},
        	{text: '<fmt:message key="product.wholesale.priceRecommend"/>', dataIndex: 'priceRecommend', width: 65, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="product.wholesale.priceCost"/>', dataIndex: 'priceCost', width: 65, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="product.wholesale.quantity"/>', dataIndex: 'stockNumber', width: 65, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="product.wholesale.weigth"/>', dataIndex: 'weigth', width: 100, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="product.wholesale.salesNumber"/>', dataIndex: 'salesNumber', width: 65, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="product.wholesale.categoryFid"/>', dataIndex: 'categoryFid', width: 65, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="product.wholesale.categoryFidName"/>', dataIndex: 'categoryFidName', width: 120, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="product.wholesale.categoryIdName"/>', dataIndex: 'categoryIdName', width: 120, sortable : false,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="product.wholesale.categoryId"/>', dataIndex: 'categoryId', width: 65, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	
        	{text: '<fmt:message key="product.wholesale.createTime"/>', dataIndex: 'createTime', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="product.wholesale.updateTime"/>', dataIndex: 'updateTime', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	}
        ];
        
        this.tbar = [
        <jkd:haveAuthorize access="/product/saveProduct.json">
        {
        	text: '<fmt:message key="button.add"/>', 
        	iconCls: 'add', 	
        	handler: this.addProductWholesale,
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		
		<jkd:haveAuthorize access="/product/updateFxStatus.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
        	text: '<fmt:message key="product.wholesale.fx.enable"/>', 
        	iconCls: 'enable', 	
        	handler: this.fxEnableProductWholesale, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/product/updateFxStatus.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        {
        	text: '<fmt:message key="product.wholesale.fx.disable"/>', 
        	iconCls: 'Cancel', 	
        	handler: this.fxDisableProductWholesale, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/product/updateSoldoutStatus.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
        	text: '<fmt:message key="product.wholesale.soldout.enable"/>', 
        	iconCls: 'Packageadd', 	
        	handler: this.soldoutEnableProductWholesale, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/product/updateSoldoutStatus.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        {
        	text: '<fmt:message key="product.wholesale.soldout.disable"/>', 
        	iconCls: 'disabled', 	
        	handler: this.soldoutDisableProductWholesale, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/product/deleteProduct.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
        	text: '<fmt:message key="product.wholesale.delete"/>', 
        	iconCls: 'delete', 	
        	handler: this.deleteProduct, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/user/getUserStandard.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        {
        	text: '<fmt:message key="product.stock.notice"/>', 
        	iconCls: 'add', 	
        	handler: this.userSaleStandard, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
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
        	plugins: ['gridHeaderFilters'],
		    viewConfig: {
		        stripeRows: true,
		        enableTextSelection: true
		    }
	    });     
	    
	    this.east =  Ext.create('MyExt.productManager.ProductTabPanel', {
        	productList: this.productList,
        	viewer: this.viewer,
		 	layout: 'card',
	        border: false,
	       	region: 'east',
	        width: this.xWidth/2,
	        minWidth: 900,
	        split: true,
	        header: false,
	        hidden: true
        });
    	
    	this.items = [this.productList, this.east];	
		this.east.hide();
		this.callParent(arguments);
	    
	    <jkd:haveAuthorize access="/product/wholesaleList.json">
	   	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.productList.filters)
			});
	    }, this);
	    this.store.load();  
	    </jkd:haveAuthorize> 
	    
	    this.gsm = this.productList.getSelectionModel();
	    this.productList.on('itemdblclick', function(view, record, item, index, e, eOpts) {
	    	<jkd:haveAuthorize access="/product/getProductById.json">
	    	this.east.transferData(this.east, record, this.body.dom.clientWidth);
	    	this.east.show();
	    	</jkd:haveAuthorize> 
	    }, this);
    },
    
    addProductWholesale : function(){
    	var productFormPanel = Ext.create('MyExt.productManager.ProductFormPanel', {
			id: 'add@productFormPanel' + this.id,
    		viewer: this.viewer
   	 	});
   	 	productFormPanel.down('checkboxfieldset').down('checkbox').setValue(true);
    			  
    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
				productFormPanel.saveProductWholesale(true, this.store);
			},
			scope: this
		},{
			text: '<fmt:message key="button.cancel"/>',
			handler : function(){popWin.close();},
			scope: this
		}];
      	openWin('<fmt:message key="product.wholesale.add"/>', productFormPanel, buttons, 400, 400);
    },
    
    addGroupProductWholesale : function(){
    	var productFormPanel = Ext.create('MyExt.productManager.ProductFormPanel', {
			id: 'add@productGroupFormPanel' + this.id,
			isGroupProduct: true,
    		viewer: this.viewer
   	 	});
    			  
    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
				productFormPanel.saveProductWholesale(true, this.store);
			},
			scope: this
		},{
			text: '<fmt:message key="button.cancel"/>',
			handler : function(){popWin.close();},
			scope: this
		}];
      	openWin('<fmt:message key="product.addGroupProduct"/>', productFormPanel, buttons, 900, 800);
    },
    
    fxEnableProductWholesale : function() {
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.productId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/product/updateFxStatus.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData), isEnabled: true},
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
	
	fxDisableProductWholesale : function() {
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.productId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/product/updateFxStatus.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData), isEnabled: false},
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
	
	
	enableProductShowStatus : function() {
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.productId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/product/updateShowStatus.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData), isEnabled: true},
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
	
	disableProductShowStatus : function() {
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.productId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/product/updateShowStatus.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData), isEnabled: false},
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
	
	soldoutEnableProductWholesale : function() {
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.productId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/product/updateSoldoutStatus.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData), isEnabled: false},
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
	
	soldoutDisableProductWholesale : function() {
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.productId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/product/updateSoldoutStatus.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData), isEnabled: true},
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
	
	deleteProduct : function() {
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.productId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/product/deleteProduct.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData), isDelete: true},
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

	
	userSaleStandard:function(){	
		var userSaleStandardForm = Ext.create('MyExt.userManager.UserSaleStandardForm', {id: 'userSaleStandardForm@userSaleStandardForm', title: '<fmt:message key="button.add"/>'});
		Ext.Ajax.request({
       		url: '<c:url value="/user/getUserStandard.json"/>',
        	method: 'post',
         	success: function(response){
       			var responseObject = Ext.JSON.decode(response.responseText);
       			if (responseObject.success == true){
       				var salesNum = Ext.getCmp('salesNum');
					var hours = Ext.getCmp('hours');
					salesNum.setValue(responseObject.data.salesNum);
					hours.setValue(responseObject.data.hours);
       			}
			}
    	});
		
		var buttons = [
		<jkd:haveAuthorize access="/user/userSaleStandard.json">
		{
			text: '<fmt:message key="product.stock.notice"/>',
			handler: function(){
	            if(userSaleStandardForm.form.isValid()){
	            	Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="button.user.edit.confirm"/>', function(e){
						if(e == 'yes'){
							var salesNum = Ext.getCmp('salesNum').getValue();
							var hours = Ext.getCmp('hours').getValue();
							Ext.Ajax.request({
					        	url: '<c:url value="/user/userSaleStandard.json"/>',
					         	method: 'post',
								scope: this,
								params:{salesNum: salesNum, hours: hours},
					          	success: function(xresponse){
							    	var xresponseObject = Ext.JSON.decode(xresponse.responseText);
			          				if (xresponseObject.success == true){
			          					showSuccMsg(xresponseObject.message);
			          					popWin.close();
			          					this.loadData();
			          				}else{
			          					showFailMsg(xresponseObject.message, 4);
			          				}
								}
					     	})
			        	}
			        }, this)
	        	}
			},
			scope: this
		},
		</jkd:haveAuthorize>
		{
			text: '<fmt:message key="button.cancel"/>',
			handler : function(){popWin.close();},
			scope: this
		}];
		openWin('<fmt:message key="product.stock.notice"/>', userSaleStandardForm, buttons, 400,150);
	}, 
	
	setPackage : function() {
		var productId ;		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		if(records.length > 1){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.only.one.record"/>');
			return;
		}
			
		for(var i = 0; i < records.length; i++){			
			productId = records[i].data.productId;	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/product/setPackage.json"/>',
		         	method: 'post',
					scope: this,
					params:{productId: productId},
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
	
	importProduct : function(){
    	var importProductFormPanel = Ext.create('MyExt.productManager.ImportProductFormPanel', {
			id: 'importProductFormPanel@' + this.id,
    		viewer: this.viewer,
    		isSave: true,
    		isCheck:0
   	 	});
    	
    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){ 
		    	if(importProductFormPanel.form.isValid()){
					Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
		             		importProductFormPanel.form.submit({
		                 		waitMsg: 'Loading...',
		                 		url: '<c:url value="/product/importProduct.json"/>',
		               			scope: this,
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
      	openWin('<fmt:message key="coupon.add"/>', importProductFormPanel, buttons, 800, 120);
    },
	
	booleanRenderer: function(value, meta, record) {    
       	if(value) {
            return '<b><fmt:message key="button.yes"/></b>';
        }else{
            return '<fmt:message key="button.no"/>';
        }  
   	},
   	
   	isFreePostageRenderer:function(value, meta, record) {    
       	if(value) {
            return '<b><fmt:message key="button.yes"/></b>';
        }else{
            return '<fmt:message key="button.no"/>';
        }  
   	},
   	
   	fontRenderer: function(value, meta, record) {
    	meta.style = 'overflow:auto;padding: 3px 6px;text-overflow: ellipsis;white-space: nowrap;white-space:normal;line-height:24px;font-weight:bold;';      
       	return value;     
   	} 
});