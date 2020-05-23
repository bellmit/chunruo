<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('XDiscovery', {
	extend: 'Ext.data.TreeModel',
	idProperty: 'discoveryId',
   	fields: [
    	{name: 'discoveryId',		type: 'int'},
		{name: 'title',	            type: 'string'},
    ]
});

Ext.define('MyExt.productManager.DiscoveryPicker', {
    extend: 'Ext.form.field.Picker',
    alias: 'widget.discoveryPicker',
    triggerCls : Ext.baseCSSPrefix + 'form-date-trigger',
    hiddenData: null,
    matchFieldWidth: false,
    pageId: false,
    isHiddenSubmit: false,
    
    initComponent : function(config) {
		Ext.apply(this, config);
		
		this.store = Ext.create('Ext.data.Store', {
    		autoLoad: false,
			autoDestroy: true,
			sortOnLoad: true,
			remoteSort: true,
			model: 'XDiscovery',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/category/XDiscoveryList.json"/>',
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
	        	text: '<fmt:message key="discovery.discoveryId"/>', 
	        	dataIndex: 'discoveryId', 
	        	width: 60, 
	        	sortable: true
	        },{
            	text: '<fmt:message key="discovery.title"/>',
            	width: 600,
            	sortable: true,
            	dataIndex: 'title'
        	}],
        	tbar:[{
	        	xtype: 'searchfield',
                width: 250,
                fieldLabel: '<fmt:message key="button.search"/>',
                labelWidth: 30,
                store: this.store
            },{
            	hidden: this.isHiddenSubmit,
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
 		var textValues=[];
    	this.gsm = this.productPicker.getSelectionModel();
    	var records=this.gsm.getSelection();
    	for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.discoveryId);	
			textValues.push(records[i].data.title)
		}
		
		this.gsm.deselectAll();
      	this.setRawValue(textValues);
      	this.objPanel.setValue(rowsData);
    },
    
    itemClick: function(record, item, index, e, eOpts){
        this.fireEvent('itemClick', this, record, item, index, e, eOpts);
    },
    
    setBrandValue(discoveryId){
    	this.discoveryId = discoveryId;
    },
    
    getBrandValue(){
    	return this.discoveryId;
    },
    
    booleanRenderer: function(value, meta, record) {    
       	if(value == true) {
            return '<b><fmt:message key="button.yes"/></b>';
        }else{
            return '<fmt:message key="button.no"/>';
        }  
   	}
});
		
		