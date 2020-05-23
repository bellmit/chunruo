<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>
Ext.define('MyExt.productManager.PurchaseDoubtFormPanel', {
    extend : 'Ext.form.Panel',
    alias: ['widget.purchaseDoubtFormPanel'],
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
				    fieldLabel: '<fmt:message key="product.intro.title"/>',
				    style: 'padding: 0px 4px',   
				    anchor:'99%',
				    name: 'title',
				    value:this.title
				},{
				    xtype: 'numberfield',
				    width: 250,
				    labelWidth: 70,
	       			fieldLabel: '<fmt:message key="product.intro.sort" />',
	       			allowNegative: false, // 不允许负数 
	       			allowDecimals:false,
	           		name: 'sort',
	           		style: 'padding: 0px 4px',
	           		anchor: '99%',
	           		value:this.sort
				},{
				    width: 360,
				    height: 150,
	        		labelWidth: 70,
				    xtype:'textarea',
				    fieldLabel: '<fmt:message key="product.intro.description"/>',
				    style: 'padding: 0px 4px',   
				    anchor:'99%',
				    name: 'description',
				    value:this.description
				}]
        	}]
    	});
    	this.add(form);
    },  
});