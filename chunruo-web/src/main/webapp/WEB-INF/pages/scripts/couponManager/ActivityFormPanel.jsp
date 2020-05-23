<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.couponManager.ActivityFormPanel', {
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
                baseCls: 'my-panel-no-border',
            	items: [{
            		xtype: 'datefield',
       				fieldLabel: '<fmt:message key="activity.startTime" />',
       				width: 250,
	        		labelWidth: 70,
           			name: 'startTime',
           			editable: true,
           			format: 'Y-m-d H:i:s',
           			anchor: '99%',
           			style: 'padding: 5px 5px 5px;',
           			id:'startTime',
				    allowBlank: false
				},{
				    width: 250,
	        		labelWidth: 70,
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="activity.voteTime"/>',
				    style: 'padding: 0px 4px',
				    name: 'voteTime',
				    anchor:'99%',
				    allowBlank: false,
				    style: 'padding: 5px 5px 5px;',
				    id:'voteTime'
				}]
        	}]
    	});
    	this.add(form);
    }  
});