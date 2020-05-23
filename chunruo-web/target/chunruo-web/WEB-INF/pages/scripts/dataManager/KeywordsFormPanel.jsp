<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.dataManager.KeywordsFormPanel', {
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
				    fieldLabel: '<fmt:message key="keywords.name"/>',
				    style: 'padding: 0px 4px',
				    name: 'name',
				    anchor:'99%',
				    allowBlank: false,
				    id:'name'
				}]
        	}]
    	});
    	this.add(form);
    }  
});
