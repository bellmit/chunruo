<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.userManager.UserTopEditPanel', {
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
        	    {id: '5', name: '<fmt:message key="user.level5"/>'},
        	    {id: '4', name: '<fmt:message key="user.level4"/>'},
        	    {id: '3', name: '<fmt:message key="user.level3"/>'},
        		{id: '2', name: '<fmt:message key="user.level2"/>'},
        		{id: '1', name: '<fmt:message key="user.level1"/>'},
        		{id: '0', name: '<fmt:message key="user.level0"/>'},
        	]});
        	
        	this.rendererPushLevelStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: 3, name: '<fmt:message key="user.pushLevel3"/>'},
        		{id: 2, name: '<fmt:message key="user.pushLevel2"/>'},
        		{id: 1, name: '<fmt:message key="user.pushLevel1"/>'},
        		{id: 0, name: '<fmt:message key="user.pushLevel0"/>'}
        	]
        });
        	
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
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="user.origin.top.mobile"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'topMobile',
				    anchor:'97%',
				    value: this.topMobile
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="user.new.top.mobile"/>',
				    labelWidth: 85,
				    readOnly: false,
				    name: 'newMobile',
				    anchor:'97%'
				}]
        	}]
    	});
    	this.add(form);
    },  
});
