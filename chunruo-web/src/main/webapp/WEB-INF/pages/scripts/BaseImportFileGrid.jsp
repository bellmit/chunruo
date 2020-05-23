<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('BaseImportFile', {
	extend: 'Ext.data.Model',
    fields: [
    	{name: 'header_0',	type: 'string'},
    	{name: 'header_1',	type: 'string'},
    	{name: 'header_2',	type: 'string'},
    	{name: 'header_3',	type: 'string'},
		{name: 'header_4',	type: 'string'},
		{name: 'header_5',	type: 'string'},
		{name: 'header_6',	type: 'string'},
		{name: 'header_7',	type: 'string'},
		{name: 'header_8',	type: 'string'},
		{name: 'header_9',	type: 'string'},
		{name: 'header_10',	type: 'string'},
		{name: 'header_11',	type: 'string'},
		{name: 'header_12',	type: 'string'},
		{name: 'header_13',	type: 'string'},
		{name: 'header_14',	type: 'string'},
		{name: 'header_15',	type: 'string'},
		{name: 'header_16',	type: 'string'},
		{name: 'header_17',	type: 'string'},
		{name: 'header_18',	type: 'string'},
		{name: 'header_19',	type: 'string'},
		{name: 'header_20',	type: 'string'},
		{name: 'header_21',	type: 'string'},
		{name: 'header_22',	type: 'string'},
		{name: 'header_23',	type: 'string'},
		{name: 'header_24',	type: 'string'},
		{name: 'header_25',	type: 'string'},
		{name: 'header_26',	type: 'string'},
		{name: 'header_27',	type: 'string'},
		{name: 'header_28',	type: 'string'},
		{name: 'header_29',	type: 'string'},
		{name: 'header_30',	type: 'string'},
		{name: 'header_31',	type: 'string'},
		{name: 'header_32',	type: 'string'},
		{name: 'header_33',	type: 'string'},
		{name: 'header_34',	type: 'string'},
		{name: 'header_35',	type: 'string'},
		{name: 'header_36',	type: 'string'},
		{name: 'header_37',	type: 'string'},
		{name: 'header_38',	type: 'string'},
		{name: 'header_39',	type: 'string'},
		{name: 'header_40',	type: 'string'},
		{name: 'header_41',	type: 'string'},
		{name: 'header_42',	type: 'string'},
		{name: 'header_43',	type: 'string'},
		{name: 'header_44',	type: 'string'},
		{name: 'header_45',	type: 'string'},
		{name: 'header_46',	type: 'string'},
		{name: 'header_47',	type: 'string'},
		{name: 'header_48',	type: 'string'},
		{name: 'header_49',	type: 'string'},
		{name: 'header_50',	type: 'string'}
    ]
});

Ext.define('MyExt.BaseImportFileGrid', {
    extend : 'Ext.grid.GridPanel',
	region: 'center',
	autoScroll: true,  
	header: false, 
	closable: true,
	selType: 'checkboxmodel',
	multiSelect: true,
	columnLines: true,
	animCollapse: false,
    enableLocking: true,
    columns: [],
    keyValueHeaderData: [],
    viewConfig: {
        stripeRows: true,
        enableTextSelection: true
    },
    
	initComponent : function(config) {
		Ext.apply(this, config);
		
    	this.store = Ext.create('Ext.data.Store', {
    		autoDestroy: true,
        	model: 'BaseImportFile',
        	data: []
		});
		
		this.tbar = [{
        	text: '<fmt:message key="button.delete"/>', 
        	iconCls: 'delete', 	
        	handler: this.deleteBaseImportFile, 
        	scope: this
        }];
        
       	this.toolbar = Ext.create('Ext.toolbar.Toolbar');
    	this.bbar = this.toolbar;
    	this.callParent();
    	this.gsm = this.getSelectionModel();
    },
    
    setObject : function(responseObject){
    	var rowsData = [];	
    	if(responseObject.strHeaderList.length > 0){
    		for(var i = 0; i < responseObject.strHeaderList.length; i ++ ){
	    		rowsData.push(Ext.JSON.decode(responseObject.strHeaderList[i]));
	    	}
	    	this.setColumns(rowsData);
	    	
	    	if(responseObject.keyValueHeaderList.length > 0){
	    		this.keyValueHeaderData = [];
	    		for(var i = 0; i < responseObject.keyValueHeaderList.length; i ++ ){
			   		this.keyValueHeaderData.push(Ext.JSON.decode(responseObject.keyValueHeaderList[i]));
			   	}
	    	
	    		if(responseObject.storeStrList.length > 0){
		    		for(var i = 0; i < responseObject.storeStrList.length; i ++ ){
			    		this.store.insert(i, Ext.JSON.decode(responseObject.storeStrList[i]));
			   		}
			   		
			   		var storeLength = responseObject.storeStrList.length;
			   		var strHTML = Ext.String.format('<fmt:message key="ajax.record"/>', 0, storeLength, storeLength);
			   		this.toolbar.add({xtype: 'label', html: strHTML});
		    	}
	    	}
    	}	
    },
    
    deleteBaseImportFile : function(){
		var records = this.gsm.getSelection();
		if(records.length > 0){
			this.store.remove(records);
			var storeLength = this.store.getCount();
			var strHTML = Ext.String.format('<fmt:message key="ajax.record"/>', 0, storeLength, storeLength);
			this.toolbar.down('[xtype=label]').setHtml(strHTML);
		}
	}
});