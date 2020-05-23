<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.userManager.SignFormPanel', {
    extend : 'Ext.form.Panel',
    alias: ['widget.signFormPanel'],
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
        	items: [
        	{xtype: 'hiddenfield', name: 'signId' , value : this.signId, allowBlank: true},{    
            	xtype: 'container',
	            style: 'padding: 10px 10px 10px;',
	            flex: 10,
                layout: 'anchor',
            	items: [
            	{
	        		labelWidth: 70,
				    xtype:'numberfield',
				    fieldLabel: '<fmt:message key="sign.signIntegral"/>',
				    style: 'padding: 0px 4px',   
				    anchor:'99%',
				    name: 'signIntegral',
				    value:this.signIntegral,
				    hidden:this.isAgree
				}
            	]
        	}]
    	});
    	this.add(form);
    },  
});
