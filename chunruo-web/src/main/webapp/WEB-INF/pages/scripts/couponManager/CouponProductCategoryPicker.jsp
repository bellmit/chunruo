<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('XCouponProductCategory', {
	extend: 'Ext.data.TreeModel',
	idProperty: 'id',
   	fields: [
    	{name: 'id',	type: 'int'},
		{name: 'name',	 		type: 'string'},
		{name: 'pathName',	 	type: 'string'},
		{name: 'description',	type: 'string'},
		{name: 'parentId',	 	type: 'string'},
		{name: 'imagePath',	 	type: 'string'},
		{name: 'status',	 	type: 'string'},
		{name: 'sort',	 		type: 'string'},
		{name: 'level',	 		type: 'string'},
		{name: 'profit',	 	type: 'string'},
		{name: 'selectType',	 	type: 'int'},
		{name: 'att',	 	type: 'int'},
		{name: 'createTime',	type: 'string'},
		{name: 'updateTime',	type: 'string'},
    ]
});

Ext.define('MyExt.couponManager.CouponProductCategoryPicker', {
    extend: 'Ext.form.field.Picker',
    alias: 'widget.couponProductCategoryPicker',
    triggerCls : Ext.baseCSSPrefix + 'form-date-trigger',
    hiddenData: null,
    matchFieldWidth: false,
    
    createPicker: function() {
    
	    this.treeStore = Ext.create('Ext.data.TreeStore', {
			model: 'XCouponProductCategory',
			folderSort: false,
			proxy: {
				type: 'ajax',
				url: '<c:url value="/coupon/categoryTreeList.json"/>'
			}
       	});
       	
       	this.categoryPicker = Ext.create('Ext.tree.Panel', {
       		height: 400,
            minWidth: 350,
            width: 600,
            floating: true,
			region: 'center',
			header: false,
			collapsible: true,
	        useArrows: true,
	        rootVisible: false,
	        store: this.treeStore,
	        multiSelect: true,
	        autoScroll: true,   
	        selModel : { 
            selType : 'checkboxmodel', 
            mode : 'SIMPLE', 
            checkOnly : true, 
            }, 
		    listeners: { 
		    //禁止选中方法
		    beforeselect: function(grid, record, index, eOpts) { 
		      if (record.get('id') == 0) { 
		        return false; 
		      } 
		    } 
		  },
           scroll: 'both',
           lines: true,
           rowLines: true,
           columnLines: true,
	       columns:[
	        	{text: '<fmt:message key="product.category.id"/>', dataIndex: 'id', width: 70, locked: true, sortable : true,multiSelect: true,},
            	{
	           		xtype: 'treecolumn',
	            	text: '<fmt:message key="product.category.name"/>',
	            	width: 450,
	            	sortable: true,
	            	dataIndex: 'name',
	            	locked: true,
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
       tbar:[
			    {
			    text: '<span style="color:red;"><fmt:message key="button.submit"/></span>', 
			    iconCls: 'add', 	
			    scope: this,
			    handler:this.handle,
			    }
	        ],
       viewConfig: {
		        stripeRows: true,
		        enableTextSelection: true
		    },
	    });
	    

	    
	    this.categoryPicker.on('click', function(view, record, item, index, e, eOpts) {
	    	this.picker.hide();
    		e.stopEvent();
    		if(record.data.leaf){
    			this.setRawValue(record.data.pathName);
    			this.objPanel.setValue(record.data.id);		
    		}

	    }, this);
        return this.categoryPicker;
    },
    
    handle: function(){
 		var rowsData=[];
 		var dataSelectTypes=[];
 		var textValues=[];
		this.gsm = this.categoryPicker.getSelectionModel();
		var records = this.gsm.getSelection();
	   	for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.id);	
		  	dataSelectTypes.push(records[i].data.selectType);
		  	textValues.push(records[i].data.name)
	   	}
	     
	    this.gsm.deselectAll();
	    this.setRawValue(textValues);
	    this.objPanel.setValue(rowsData);
		this.objSelectType.setValue(dataSelectTypes);
    },
    
    booleanRenderer: function(value, meta, record) {    
       	if(value == true) {
            return '<b><fmt:message key="button.yes"/></b>';
        }else{
            return '<fmt:message key="button.no"/>';
        }  
   	}
});
