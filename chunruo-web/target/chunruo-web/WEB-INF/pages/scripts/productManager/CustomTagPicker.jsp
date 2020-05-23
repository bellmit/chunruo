<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('XTag', {
	extend: 'Ext.data.TreeModel',
	idProperty: 'tagId',
   	fields: [
    	{name: 'tagId',		type: 'int'},
		{name: 'name',	    type: 'string'},
    ]
});

Ext.define('MyExt.productManager.CustomTagPicker', {
    extend: 'Ext.form.field.Picker',
    alias: 'widget.customTagPicker',
    triggerCls : Ext.baseCSSPrefix + 'form-date-trigger',
    hiddenData: null,
    matchFieldWidth: false,
    productId: false,
    
    initComponent : function(config) {
		Ext.apply(this, config);
		
		this.store = Ext.create('Ext.data.Store', {
    		autoLoad: false,
			autoDestroy: true,
			sortOnLoad: true,
			remoteSort: true,
			model: 'XTag',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/tagModel/XTagList.json"/>',
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
	        	query: this.customTagPicker.down('searchfield').getValue()
			});
	    }, this);
		
		this.on('blur', function(picker, event, eOpts){
			this.customTagPicker.down('searchfield').setValue();
			this.store.load();
		}, this);
	},
    
    createPicker: function() {
       	this.customTagPicker = Ext.create('Ext.grid.GridPanel', {
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
	        	text: '<fmt:message key="product.tag.tagId"/>', 
	        	dataIndex: 'tagId', 
	        	width: 60, 
	        	sortable: true
	        },{
            	text: '<fmt:message key="product.tag.name"/>',
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
	    
	    this.customTagPicker.on('itemdblclick', function(view, record, item, index, e, eOpts) {
	    	this.picker.hide();
    		e.stopEvent();
    		this.itemClick(record, item, index, e, eOpts);
	    }, this);
        return this.customTagPicker;
    },
    
     handle: function(){
 		var rowsData=[];
 		var dataSelectTypes=[];
 		var textValues=[];
    	this.gsm = this.customTagPicker.getSelectionModel();
    	var records=this.gsm.getSelection();
    	for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.tagId);	
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
    
    setBrandValue(tagId){
    	this.tagId = tagId;
    },
    
    getBrandValue(){
    	return this.tagId;
    },
    
    booleanRenderer: function(value, meta, record) {    
       	if(value == true) {
            return '<b><fmt:message key="button.yes"/></b>';
        }else{
            return '<fmt:message key="button.no"/>';
        }  
   	}
});
		
		