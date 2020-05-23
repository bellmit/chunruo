<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('XProductBrand', {
	extend: 'Ext.data.TreeModel',
	idProperty: 'brandId',
   	fields: [
    	{name: 'brandId',		type: 'int'},
		{name: 'name',	 		type: 'string'},
		{name: 'isHot',	 		type: 'bool'},
		{name: 'initial',		type: 'string'},
		{name: 'image',			type: 'string'},
		{name: 'createTime',	type: 'string'},
		{name: 'updateTime',	type: 'string'},
    ]
});

Ext.define('MyExt.productManager.ProductBrandPicker', {
    extend: 'Ext.form.field.Picker',
    alias: 'widget.productBrandPicker',
    triggerCls : Ext.baseCSSPrefix + 'form-date-trigger',
    hiddenData: null,
    matchFieldWidth: false,
    brandId: false,
    
    initComponent : function(config) {
		Ext.apply(this, config);
		
		this.store = Ext.create('Ext.data.Store', {
    		autoLoad: false,
			autoDestroy: true,
			sortOnLoad: true,
			remoteSort: true,
			model: 'XProductBrand',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/brand/XBrandList.json"/>',
				reader: {
					type : 'json',
                	root: 'data',
                	totalProperty: 'totalCount'
            	}
			}
		});
		this.callParent();
		
		this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	query: this.productBrandPicker.down('searchfield').getValue()
			});
	    }, this);
		
		this.on('blur', function(picker, event, eOpts){
			this.productBrandPicker.down('searchfield').setValue();
			this.store.load();
		}, this);
	},
    
    createPicker: function() {
       	this.productBrandPicker = Ext.create('Ext.grid.GridPanel', {
       		height: 150,
            minWidth: 250,
            width: 300,
            floating: true,
			region: 'center',
			header: false,
			collapsible: true,
	        store: this.store,
	        autoScroll: true,  
           	lines: true,
           	columnLines: true,
           	viewConfig: {
		        stripeRows: true,
		        enableTextSelection: true
		    },
	        columns: [{
	        	text: '<fmt:message key="productBrand.brandId"/>', 
	        	dataIndex: 'brandId', 
	        	width: 60, 
	        	sortable: true
	        },{
            	text: '<fmt:message key="productBrand.name"/>',
            	width: 200,
            	sortable: true,
            	dataIndex: 'name'
        	}],
        	tbar:[{
	        	xtype: 'searchfield',
                width: 250,
                fieldLabel: '<fmt:message key="button.search"/>',
                labelWidth: 30,
                store: this.store
            }]
	    });
	    
	    this.productBrandPicker.on('itemdblclick', function(view, record, item, index, e, eOpts) {
	    	this.picker.hide();
    		e.stopEvent();
    		this.itemClick(record, item, index, e, eOpts);
	    }, this);
        return this.productBrandPicker;
    },
    
    itemClick: function(record, item, index, e, eOpts){
        this.fireEvent('itemClick', this, record, item, index, e, eOpts);
    },
    
    setBrandValue(brandId){
    	this.brandId = brandId;
    },
    
    getBrandValue(){
    	return this.brandId;
    },
    
    booleanRenderer: function(value, meta, record) {    
       	if(value == true) {
            return '<b><fmt:message key="button.yes"/></b>';
        }else{
            return '<fmt:message key="button.no"/>';
        }  
   	}
});
		
		