<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('ResourceMenu', {
	extend: 'Ext.data.TreeModel',
	idProperty: 'categoryId',
   	fields: [
    	{name: 'menuId',	type: 'int'},
		{name: 'text',	 	type: 'string'},
		{name: 'namePath',	type: 'string'}
    ]
});

Ext.define('MyExt.systemManager.ResourceMenuPicker', {
    extend: 'Ext.form.field.Picker',
    alias: 'widget.resourceMenuPicker',
    triggerCls : Ext.baseCSSPrefix + 'form-date-trigger',
    hiddenData: null,
    matchFieldWidth: false,
    
    createPicker: function() {
    
	    this.treeStore = Ext.create('Ext.data.TreeStore', {
			model: 'ResourceMenu',
			folderSort: true,
			proxy: {
				type: 'ajax',
				url: '<c:url value="/menuSys/queryMenuNodes.js?isResRequest=1"/>'
			}
       	});
       	
       	this.categoryPicker = Ext.create('Ext.tree.Panel', {
       		height: 300,
            minWidth: 350,
            width: 450,
            floating: true,
			region: 'center',
			header: false,
			collapsible: true,
	        useArrows: true,
	        rootVisible: false,
	        store: this.treeStore,
	        multiSelect: true,
	        autoScroll: true,   
            scroll: 'both',
           	lines: true,
        	rowLines: true,
           	columnLines: true,
	        columns: [{
        		text: '<fmt:message key="product.category.categoryId"/>', 
        		dataIndex: 'menuId', 
        		width: 70, 
        		locked: true, 
        		sortable: true,
        		multiSelect: true
        	},{
           		xtype: 'treecolumn',
            	text: '<fmt:message key="product.category.name"/>',
            	width: 260,
            	sortable: true,
            	dataIndex: 'text',
            	locked: true,
            	multiSelect: true,
            	checked:'true',
            	name:'haha'
        	}],
        	viewConfig: {
		        stripeRows: true,
		        enableTextSelection: true
		    }
	    });
	    
	    this.categoryPicker.on('itemdblclick', function(view, record, item, index, e, eOpts) {
    		if(record.data.leaf){
    			this.picker.hide();
    			e.stopEvent();
    			
    			this.setRawValue(record.data.namePath);
    			this.objPanel.setValue(record.data.menuId);		
    		}
	    }, this);
        return this.categoryPicker;
    },
    
    booleanRenderer: function(value, meta, record) {    
       	if(value == true) {
            return '<b><fmt:message key="button.yes"/></b>';
        }else{
            return '<fmt:message key="button.no"/>';
        }  
   	}
});
