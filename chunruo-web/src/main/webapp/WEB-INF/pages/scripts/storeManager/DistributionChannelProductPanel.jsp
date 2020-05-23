<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('PageProduct', {
	extend: 'Ext.data.Model',
	idProperty: 'productId',
    fields: [
		{name: 'productId',			mapping: 'productId',		type: 'int'},
		{name: 'userId',	 		mapping: 'userId',			type: 'int'},
		{name: 'userName',	 		mapping: 'userName',		type: 'string'},
		{name: 'storeId',	 		mapping: 'storeId',			type: 'int'},
		{name: 'storeName',	 		mapping: 'storeName',		type: 'string'},
		{name: 'name',	 			mapping: 'name',			type: 'string'},
		{name: 'quantity',	 		mapping: 'quantity',		type: 'int'},
		{name: 'priceWholesale',	mapping: 'priceWholesale',	type: 'string'},
		{name: 'priceRecommend',	mapping: 'priceRecommend',	type: 'string'},
		{name: 'priceCost',	 		mapping: 'priceCost',		type: 'string'},
		{name: 'categoryFid',	 	mapping: 'categoryFid',		type: 'int'},
		{name: 'categoryFidName',	mapping: 'categoryFidName',	type: 'string'},
		{name: 'categoryId',	 	mapping: 'categoryId',		type: 'int'},
		{name: 'categoryIdName',	mapping: 'categoryIdName',	type: 'string'},
		{name: 'weigth',	 		mapping: 'weigth',			type: 'string'},
		{name: 'productCode',	 	mapping: 'productCode',		type: 'string'},
		{name: 'productSku',	 	mapping: 'productSku',		type: 'string'},
		{name: 'wareHouseId',	 	mapping: 'wareHouseId',		type: 'int'},
		{name: 'wareHouseName',	 	mapping: 'wareHouseName',	type: 'string'},
		{name: 'image',	 			mapping: 'image',			type: 'string'},
		{name: 'salesNumber',	 	mapping: 'salesNumber',		type: 'string'},
		{name: 'status',	 		mapping: 'status',			type: 'bool'},
		{name: 'isSoldout',	 		mapping: 'isSoldout',		type: 'bool'},
		{name: 'productDesc',	 	mapping: 'productDesc',		type: 'string'},
		{name: 'productIntros',	 	mapping: 'productIntros',	type: 'string'},
		{name: 'productType',	 	mapping: 'productType',		type: 'string'},
		{name: 'fxNumber',	 		mapping: 'fxNumber',		type: 'int'},
		{name: 'profit',	 		mapping: 'profit',			type: 'string'},
		{name: 'isPromot',		 	mapping: 'isPromot',		type: 'bool'},
		{name: 'promotStartTime',	mapping: 'promotStartTime',	type: 'string'},
		{name: 'promotEndTime',	 	mapping: 'promotEndTime',	type: 'string'},
		{name: 'buyerLimit',	 	mapping: 'buyerLimit',		type: 'int'},
		{name: 'countryId',	 		mapping: 'countryId',		type: 'int'},
		{name: 'countryName',	 	mapping: 'countryName',		type: 'string'},
		{name: 'createTime',	 	mapping: 'createTime',		type: 'string'},
		{name: 'updateTime',	 	mapping: 'updateTime',		type: 'string'}	
    ]
});

Ext.define('MyExt.storeManager.DistributionChannelProductPanel', {
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
		
    	this.store = Ext.create('Ext.data.Store', {
    		pageSize: 50,
	        autoLoad: false,
			autoDestroy: true,
			sortOnLoad: true,
			remoteSort: true,
			model: 'PageProduct',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/product/wholesaleList.json"/>',
				reader: {
					type : 'json',
                	root: 'data',
                	totalProperty: 'totalCount'
            	}
			},
			sorters: [{
	            property: 'productId',
	            direction: 'desc'
	        }]
		});
		
		this.booleanStore = Ext.create('Ext.data.Store', {
			autoDestroy: true,
			model: 'InitModel',
			data: [
				{id: '1', name: '<fmt:message key="button.yes"/>'},
				{id: '0', name: '<fmt:message key="button.no"/>'},
			]
		});
		
		this.columns = [
		
			{
            	text: '<fmt:message key="product.wholesale.image"/>',
                menuDisabled: true,
                sortable: false,
                xtype: 'actioncolumn',
                align: 'center',
                width: 80,
                higth: 80,
                dataIndex: 'image',
                renderer: function(value, metadata, record) {
        			return Ext.String.format('<img height="60" width="60" src="http://www.jikeduo.com.cn/upload/{0}"></img>', value);
    			}
            }, 
            {text: '<fmt:message key="product.wholesale.wholesaleId"/>', dataIndex: 'productId', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="product.wholesale.name"/>', dataIndex: 'name', width: 320, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	}
        	
         ];
        
        this.tbar = [{}];
        
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
	    
	   
    	
    	
    	this.gsm = this.productList.getSelectionModel();
    	this.items = [this.productList];	
		
		this.callParent(arguments);
	    
	   this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.productList.filters)
			});
	    }, this);
	    this.store.load();   
	    
	  //  this.productList.on('itemdblclick', function(view, record, item, index, e, eOpts) {
	  //  	this.east.transferData(this.east, record, this.body.dom.clientWidth);
	  //  	this.east.show();
	  //  }, this);
    },
    
    addProductWholesale : function(){
  		alert("hello world");
    },
	
	
   	
   	fontRenderer: function(value, meta, record) {
    	meta.style = 'overflow:auto;padding: 3px 6px;text-overflow: ellipsis;white-space: nowrap;white-space:normal;line-height:24px;font-weight:bold;';      
       	return value;     
   	} 
});

