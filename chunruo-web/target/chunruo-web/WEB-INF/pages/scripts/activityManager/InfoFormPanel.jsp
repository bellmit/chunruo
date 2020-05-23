<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.activityManager.InfoFormPanel', {
    extend : 'Ext.form.Panel',
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
			xtype: 'hiddenfield', 
			name: 'infoId', 
			value:this.infoId
		},{    
            	xtype: 'container',
	            style: 'padding: 10px 10px 10px;',
	            flex: 10,
                layout: 'anchor',
                baseCls: 'my-panel-no-border',
            	items: [{
			           xtype: 'numberfield',
			           labelWidth: 70,
			           fieldLabel: '<fmt:message key="product.category.sort"/>',
			           anchor:'99%',
			           name: 'sort',
			           value: this.sort
		           },{
	        		labelWidth: 70,
				    xtype:'textarea',
				    fieldLabel: '<fmt:message key="activity.info.content"/>',
				    name: 'content',
				    anchor:'99%',
				    allowBlank: false,
				    height: 180,
				    value: this.content
				}]
        	}]
    	});
    	this.add(form);
    }  
});