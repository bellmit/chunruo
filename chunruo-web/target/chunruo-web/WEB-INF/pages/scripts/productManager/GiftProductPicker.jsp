<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('ProductCont', {
	extend: 'Ext.data.TreeModel',
	idProperty: 'giftProductId',
   	fields: [
    	{name: 'giftProductId',		type: 'int'},
		{name: 'name',	 		    type: 'string'},
		{name: 'productName',	 	type: 'string'},
		{name: 'productTags',	 	type: 'string'},
    ]
});

Ext.define('MyExt.productManager.GiftProductPicker', {
    extend: 'Ext.form.field.Picker',
    alias: 'widget.giftProductPicker',
    triggerCls : Ext.baseCSSPrefix + 'form-date-trigger',
    requires : ['Ext.ux.grid.GridHeaderFilters'],
    hiddenData: null,
    matchFieldWidth: false,
    giftProductId: false,
    
    initComponent : function(config) {
		Ext.apply(this, config);
		
		this.store = Ext.create('Ext.data.Store', {
    		pageSize: 50,
	        autoLoad: false,
			autoDestroy: true,
			sortOnLoad: true,
			remoteSort: true,
			model: 'ProductCont',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/giftProduct/list.json?isSetGift=true"/>',
				reader: {
					type : 'json',
                	root: 'data',
                	totalProperty: 'totalCount'
            	}
			},
			sorters: [{
	            property: 'giftProductId',
	            direction: 'desc'
	        }]
		});
		
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
       		height: 300,
            minWidth: 300,
            width: 600,
            floating: true,
			region: 'center',
			header: false,
			collapsible: true,
	        store: this.store,
	        autoScroll: true,  
           	lines: true,
           	columnLines: true,
           	bbar: this.pagingToolbar,
        	plugins: ['gridHeaderFilters'],
           	viewConfig: {
		        stripeRows: true,
		        enableTextSelection: true
		    },
	        columns: [
	        	{text: '<fmt:message key="product.wholesale.wholesaleId"/>', dataIndex: 'giftProductId', width: 65, sortable : true,
        			filter: {xtype: 'textfield'}
        		},
        		{text: '<fmt:message key="gift.product.giftName"/>', dataIndex: 'name', width: 200, sortable : true,
        			filter: {xtype: 'textfield'}
        		},
        		{text: '<fmt:message key="gift.product.productName"/>', dataIndex: 'productName', width: 390, sortable : true,
        			filter: {xtype: 'textfield'}
        		},
        		{text: '<fmt:message key="gift.product.productTags"/>', dataIndex: 'productTags', width: 100, sortable : true,
        			filter: {xtype: 'textfield'}
        		},
        	]
	    });
		this.callParent();
		
		this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.productList.filters)
			});
	    }, this);
	    this.store.load();
		
		this.on('blur', function(picker, event, eOpts){
			//this.productPicker.down('searchfield').setValue();
			this.store.load();
		}, this);
	},
    
    createPicker: function() {
	    this.productList.on('itemdblclick', function(view, record, item, index, e, eOpts) {
	    	this.picker.hide();
    		e.stopEvent();
    		this.itemClick(record, item, index, e, eOpts);
	    }, this);
        return this.productList;
    },
    
    itemClick: function(record, item, index, e, eOpts){
        this.fireEvent('itemClick', this, record, item, index, e, eOpts);
    },
    
    setBrandValue(giftProductId){
    	this.giftProductId = giftProductId;
    },
    
    getBrandValue(){
    	return this.giftProductId;
    },
    
    booleanRenderer: function(value, meta, record) {    
       	if(value == true) {
            return '<b><fmt:message key="button.yes"/></b>';
        }else{
            return '<fmt:message key="button.no"/>';
        }  
   	}
});
		
		