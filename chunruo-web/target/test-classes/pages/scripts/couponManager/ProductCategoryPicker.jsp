<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('XProductCategory', {
	extend: 'Ext.data.TreeModel',
	idProperty: 'categoryId',
   	fields: [
    	{name: 'categoryId',		type: 'int'},
		{name: 'name',	 		    type: 'string'},
    ]
});

Ext.define('MyExt.couponManager.ProductCategoryPicker', {
    extend: 'Ext.form.field.Picker',
    alias: 'widget.productCategoryPicker',
    triggerCls : Ext.baseCSSPrefix + 'form-date-trigger',
    hiddenData: null,
    matchFieldWidth: false,
    categoryId: false,
    
    initComponent : function(config) {
		Ext.apply(this, config);
		
		this.store = Ext.create('Ext.data.Store', {
    		autoLoad: false,
			autoDestroy: true,
			sortOnLoad: true,
			remoteSort: true,
			model: 'XProductCategory',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/coupon/XProductCategoryList.json"/>',
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
	        	query: this.productPicker.down('searchfield').getValue()
			});
	    }, this);
		
		this.on('blur', function(picker, event, eOpts){
			this.productPicker.down('searchfield').setValue();
			this.store.load();
		}, this);
	},
    
    createPicker: function() {
       	this.productPicker = Ext.create('Ext.grid.GridPanel', {
       		height: 300,
            minWidth: 300,
            width: 600,
            floating: true,
			region: 'center',
			header: false,
			collapsible: true,
	        store: this.store,
	        autoScroll: true,  
	        selModel : { 
            selType : 'checkboxmodel', 
            mode : 'SIMPLE', 
            checkOnly : true, 
            }, 
           	lines: true,
           	columnLines: true,
           	viewConfig: {
		        stripeRows: true,
		        enableTextSelection: true
		    },
	        columns: [{
	        	text: '<fmt:message key="product.wholesale.wholesaleId"/>', 
	        	dataIndex: 'categoryId', 
	        	width: 60, 
	        	sortable: true
	        },{
            	text: '<fmt:message key="product.category.name"/>',
            	width: 600,
            	sortable: true,
            	dataIndex: 'name'
        	}],
        	tbar:[{
	        	xtype: 'searchfield',
                width: 250,
                fieldLabel: '<fmt:message key="button.search"/>',
                labelWidth: 30,
                store: this.store
            },{
			    text: '<span style="color:red;"><fmt:message key="button.submit"/></span>', 
			    iconCls: 'add', 	
			    scope: this,
			    handler:this.handle,
			    }]
	    });
	    
	    this.productPicker.on('itemdblclick', function(view, record, item, index, e, eOpts) {
	    	this.picker.hide();
    		e.stopEvent();
    		this.itemClick(record, item, index, e, eOpts);
	    }, this);
        return this.productPicker;
    },
    
     handle: function(){
 		var rowsData=[];
 		var dataSelectTypes=[];
 		var textValues=[];
    	this.gsm = this.productPicker.getSelectionModel();
    	var records=this.gsm.getSelection();
    	for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.categoryId);	
			dataSelectTypes.push(records[i].data.selectType);
			textValues.push(records[i].data.name)
		}
		 
		this.gsm.deselectAll();
		this.setRawValue(textValues);
    	this.objPanel.setValue(rowsData);
    	this.objSelectType.setValue(dataSelectTypes);
    },
    
    itemClick: function(record, item, index, e, eOpts){
        this.fireEvent('itemClick', this, record, item, index, e, eOpts);
    },
    
    setBrandValue(categoryId){
    	this.categoryId = categoryId;
    },
    
    getBrandValue(){
    	return this.categoryId;
    },
    
    booleanRenderer: function(value, meta, record) {    
       	if(value == true) {
            return '<b><fmt:message key="button.yes"/></b>';
        }else{
            return '<fmt:message key="button.no"/>';
        }  
   	}
});
		
		