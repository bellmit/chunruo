<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('XProductCategory', {
	extend: 'Ext.data.TreeModel',
	idProperty: 'categoryId',
   	fields: [
    	{name: 'categoryId',	type: 'int'},
		{name: 'name',	 		type: 'string'},
		{name: 'pathName',	 	type: 'string'},
		{name: 'description',	type: 'string'},
		{name: 'parentId',	 	type: 'string'},
		{name: 'imagePath',	 	type: 'string'},
		{name: 'status',	 	type: 'string'},
		{name: 'sort',	 		type: 'string'},
		{name: 'level',	 		type: 'string'},
		{name: 'profit',	 	type: 'string'},
		{name: 'createTime',	type: 'string'},
		{name: 'updateTime',	type: 'string'},
    ]
});

Ext.define('MyExt.productManager.ProductCategoryPicker', {
    extend: 'Ext.form.field.Picker',
    alias: 'widget.productCategoryPicker',
    triggerCls : Ext.baseCSSPrefix + 'form-date-trigger',
    hiddenData: null,
    matchFieldWidth: false,
    
    createPicker: function() {
    
	    this.treeStore = Ext.create('Ext.data.TreeStore', {
			model: 'XProductCategory',
			folderSort: true,
			proxy: {
				type: 'ajax',
				url: '<c:url value="/category/categoryTreeList.json"/>'
			}
       	});
       	
       	this.categoryPicker = Ext.create('Ext.tree.Panel', {
       		height: 300,
            minWidth: 350,
            width: 400,
            floating: true,
			region: 'center',
			header: false,
			collapsible: true,
	        useArrows: true,
	        rootVisible: false,
	        store: this.treeStore,
	        multiSelect: true,
	        autoScroll: true,   
	        selType: 'checkboxmodel',
            scroll: 'both',
           	lines: true,
        	rowLines: true,
           	columnLines: true,
	        columns: [
	        	{text: '<fmt:message key="product.category.categoryId"/>', dataIndex: 'categoryId', width: 70, locked: true, sortable : true,multiSelect: true,},
            	{
	           		xtype: 'treecolumn',
	            	text: '<fmt:message key="product.category.name"/>',
	            	width: 260,
	            	sortable: true,
	            	dataIndex: 'name',
	            	locked: true,
	            	multiSelect: true,
	            	checked:'true',
	            	name:'haha'
	        	},
	       		{text: '<fmt:message key="product.category.status"/>', dataIndex: 'status', width: 65, locked: true, sortable : true,
	       			align: 'center',
	       			renderer: this.booleanRenderer,
	        		filter: {
						xtype: 'combobox',
				        displayField: 'name',
				        valueField: 'id',
				        store: this.booleanStore,
				        queryMode: 'local',
				        typeAhead: true
					}
	       		}
        	],
        	tbar:[{
            	hidden: this.isHiddenSubmit,
			    text: '<span style="color:red;"><fmt:message key="button.submit"/></span>', 
			    iconCls: 'add', 	
			    scope: this,
			    handler:this.handle,
			}],
        	viewConfig: {
		        stripeRows: true,
		        enableTextSelection: true
		    },
	    });
	    
	    this.categoryPicker.on('itemdblclick', function(view, record, item, index, e, eOpts) {
	    	this.picker.hide();
    		e.stopEvent();
    		if(record.data.leaf){
    			this.setRawValue(record.data.pathName);
    			this.objPanel.setValue(record.data.categoryId);		
    		}
	    }, this);
        return this.categoryPicker;
    },
    
    handle: function(){
 		var rowsData=[];
 		var textValues=[];
    	this.gsm = this.categoryPicker.getSelectionModel();
    	var records=this.gsm.getSelection();
    	for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.categoryId);	
			textValues.push(records[i].data.pathName)
		}
		
		this.gsm.deselectAll();
      	this.setRawValue(textValues);
      	this.objPanel.setValue(rowsData);
    },
    
    booleanRenderer: function(value, meta, record) {    
       	if(value == true) {
            return '<b><fmt:message key="button.yes"/></b>';
        }else{
            return '<fmt:message key="button.no"/>';
        }  
   	}
});
