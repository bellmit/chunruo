<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>
Ext.define('MyExt.productManager.ProductWarehouseAddFormPanel', {
    extend : 'Ext.Panel',
 	header: false,
	closable: true,
	columnLines: true,
	animCollapse: true,
	collapsible: true,
    scroll: 'both',
    autoScroll: true,
   	items:[],
	viewConfig: {	
		stripeRows: true,
		enableTextSelection: true
	},
		    
	initComponent : function(config) {
		Ext.apply(this, config);
		
		
        	this.rendererApply= Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: '2', name: '<fmt:message key="user.recordType2"/>'},
        		{id: '1', name: '<fmt:message key="user.recordType1"/>'}
        	]
        }
        );
        
        this.renderer= Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: '2', name: '<fmt:message key="user.payStatus2"/>'},
        		{id: '1', name: '<fmt:message key="user.payStatus1"/>'}
        	]
        }
        );
        
        this.rendererType= Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: '1', name: '<fmt:message key="product.wareHouse.warehouseType1"/>'},
        		{id: '2', name: '<fmt:message key="product.wareHouse.warehouseType2"/>'}
        	]
        }
        );
        
         this.rendererAttribute= Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: '1', name: '<fmt:message key="product.wareHouse.productType1"/>'},
        		{id: '2', name: '<fmt:message key="product.wareHouse.productType2"/>'},
        		{id: '3', name: '<fmt:message key="product.wareHouse.productType3"/>'},
        		{id: '4', name: '<fmt:message key="product.wareHouse.productType4"/>'}
        		
        	]
        }
        );
        
        
    	this.callParent(arguments);
    	this.addItems(true);
    },
    
    
    
    
    addItems : function(isHiddenDelete){
    	var form = Ext.create('Ext.form.Panel', {
    		header: false,
       	 	layout: 'column',  
        	border: false,  
        	items: [{    
            	xtype: 'container',
	            style: 'padding: 10px 10px 10px;',
	            flex: 10,
                layout: 'anchor',
            	items: [{
				    width: 250,
	        		labelWidth: 70,
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="product.wareHouse.name"/>',
				    style: 'padding: 0px 4px',   
				    anchor:'99%',
				    id: 'name',  
				    value: this.name,
				},{
				    width: 250,
	        		labelWidth: 70,
					xtype: 'combobox',
					fieldLabel: '<fmt:message key="product.wareHouse.warehouseType" />',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.rendererType,
			        style: 'padding: 0px 4px',
			        allowBlank: false,
			        queryMode: 'local',
			        typeAhead: true,
			        anchor: '99%' , 
			        id: 'warehouseType',
			        value: this.warehouseType,
			        editable: false,
				},{
				    width: 250,
	        		labelWidth: 70,
					xtype: 'combobox',
					fieldLabel: '<fmt:message key="product.wareHouse.productType" />',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.rendererAttribute,
			        style: 'padding: 0px 4px',
			        allowBlank: false,
			        queryMode: 'local',
			        typeAhead: true,
			        anchor: '99%' , 
			        id: 'productType',
			        value: this.productType,
			        editable: false,
			        readOnly:this.read
				}]
        	}]
    	});
    	this.add(form);
    },  
});