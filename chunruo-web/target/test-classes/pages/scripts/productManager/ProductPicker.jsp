<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('ProductCont', {
	extend: 'Ext.data.TreeModel',
	idProperty: 'productId',
   	fields: [
    	{name: 'productId',		type: 'int'},
		{name: 'name',	 		type: 'string'},
    ]
});

Ext.define('MyExt.productManager.ProductPicker', {
    extend: 'Ext.form.field.Picker',
    alias: 'widget.productPicker',
    triggerCls : Ext.baseCSSPrefix + 'form-date-trigger',
    requires : ['Ext.ux.grid.GridHeaderFilters'],
    hiddenData: null,
    matchFieldWidth: false,
    productId: false,
    
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
				url: '<c:url value="/product/wholesaleList.json?isNotGroup=true"/>',
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
	        	{text: '<fmt:message key="product.wholesale.wholesaleId"/>', dataIndex: 'productId', width: 65, sortable : true,
        			filter: {xtype: 'textfield'}
        		},
        		{text: '<fmt:message key="product.wholesale.name"/>', dataIndex: 'name', width: 390, sortable : true,
        			filter: {xtype: 'textfield'}
        		},
        		{text: '<fmt:message key="product.wholesale.productCode"/>', dataIndex: 'productCode', width: 130, sortable : true,
        			filter: {xtype: 'textfield'}
        		}
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
    
    setBrandValue(productId){
    	this.productId = productId;
    },
    
    getBrandValue(){
    	return this.productId;
    },
    
    booleanRenderer: function(value, meta, record) {    
       	if(value == true) {
            return '<b><fmt:message key="button.yes"/></b>';
        }else{
            return '<fmt:message key="button.no"/>';
        }  
   	}
});
		
		