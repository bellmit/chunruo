<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.userManager.UserEditPanel', {
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
        		{id: '2', name: '<fmt:message key="user.level2"/>'},
        		{id: '1', name: '<fmt:message key="user.level1"/>'},
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
	        		width: 250,
	        		labelWidth: 70,
					xtype: 'combobox',
					fieldLabel: '<fmt:message key="user.level" />',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.rendererApply,
			        style: 'padding: 0px 4px',
			        editable: false,
			        allowBlank: false,
			        queryMode: 'local',
			        typeAhead: true,
			        anchor: '99%' ,
			        value: this.level,
			        name: 'level'
				}]
        	}]
    	});
    	this.add(form);
    },  
});
