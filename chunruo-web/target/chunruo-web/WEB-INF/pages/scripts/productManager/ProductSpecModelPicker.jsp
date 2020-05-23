<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('ProductSpecModel', {
	extend: 'Ext.data.TreeModel',
	idProperty: 'specModelId',
   	fields: [
    	{name: 'specModelId',	type: 'int'},
		{name: 'name',	 		type: 'string'},
		{name: 'sort',	 		type: 'int'},
		{name: 'createTime',	type: 'string'},
		{name: 'updateTime',	type: 'string'},
    ]
});

Ext.define('MyExt.productManager.ProductSpecModelPicker', {
    extend: 'Ext.form.field.Picker',
    alias: 'widget.productSpecModelPicker',
    triggerCls : Ext.baseCSSPrefix + 'form-date-trigger',
    hiddenData: null,
    matchFieldWidth: false,
    specModelId: false,
    
    initComponent : function(config) {
		Ext.apply(this, config);
		this.store = Ext.create('Ext.data.Store', {
    		autoLoad: false,
			autoDestroy: true,
			sortOnLoad: true,
			remoteSort: true,
			model: 'ProductSpecModel',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/productSpce/specModelList.json"/>',
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
	        	query: this.productSpecModelPicker.down('searchfield').getValue()
			});
	    }, this);
		
		this.on('blur', function(picker, event, eOpts){
			this.productSpecModelPicker.down('searchfield').setValue();
			this.store.load();
		}, this);
	},
    
    createPicker: function() {
       	this.productSpecModelPicker = Ext.create('Ext.grid.GridPanel', {
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
	        	text: '<fmt:message key="product.category.categoryId"/>', 
	        	dataIndex: 'specModelId', 
	        	width: 60, 
	        	sortable: true
	        },{
            	text: '<fmt:message key="product.category.name"/>',
            	width: 230,
            	sortable: true,
            	dataIndex: 'name'
        	}],
        	tbar:[{
	        	xtype: 'searchfield',
                width: 230,
                fieldLabel: '<fmt:message key="button.search"/>',
                labelWidth: 30,
                store: this.store
            },'-',{
	        	text: '<fmt:message key="button.add"/>', 
	        	iconCls: 'add',	
	        	handler: function(){
	        		var name = this.productSpecModelPicker.down('searchfield').getValue();
	        		if(name == null
	        			|| name == ''
	        			|| name.length == 0){
						showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
						return;
					}
	        		
	        		Ext.Ajax.request({
			        	url: '<c:url value="/productSpce/addSpecModel.json"/>',
			         	method: 'post',
						scope: this,
						params:{name: name},
			          	success: function(response){
	          				var responseObject = Ext.JSON.decode(response.responseText);
	                        if(responseObject.success == true){
	                       		showSuccMsg(responseObject.message);
	                        	this.store.loadPage(1);
							}else{
								showFailMsg(responseObject.message, 4);
							}
						}
			     	});
	        	}, 
	        	scope: this
	        }]
	    });
	    
	    this.productSpecModelPicker.on('itemdblclick', function(view, record, item, index, e, eOpts) {
	    	this.picker.hide();
    		e.stopEvent();
    		this.itemClick(record, item, index, e, eOpts);
	    }, this);
        return this.productSpecModelPicker;
    },
    
    itemClick: function(record, item, index, e, eOpts){
        this.fireEvent('itemClick', this, record, item, index, e, eOpts);
    },
    
    setSpecModelValue(specModelId){
    	this.specModelId = specModelId;
    },
    
    getSpecModelValue(){
    	return this.specModelId;
    },
    
    booleanRenderer: function(value, meta, record) {    
       	if(value == true) {
            return '<b><fmt:message key="button.yes"/></b>';
        }else{
            return '<fmt:message key="button.no"/>';
        }  
   	}
});
