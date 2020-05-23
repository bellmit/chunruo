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
				},{
	        		width: 250,
	        		labelWidth: 70,
					xtype: 'combobox',
					fieldLabel: '<fmt:message key="user.pushLevel" />',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.rendererPushLevelStore,
			        style: 'padding: 0px 4px',
			        editable: false,
			        allowBlank: false,
			        queryMode: 'local',
			        typeAhead: true,
			        anchor: '99%' ,
			        value: this.pushLevel,
			        name: 'pushLevel'
				},{
				    width:250,
				    xtype:'datefield',
				    fieldLabel: '<fmt:message key="user.expireTime"/>',
				    labelWidth: 70,
				    readOnly: false,
				    format: 'Y-m-d',
				    name: 'expireEndDate',
				    id:'endTime',		    
				    value:Ext.Date.add(new Date(),Ext.Date.YEAR,1),
				    anchor:'99%',
				    style: 'padding: 0px 4px',
				    value:this.expireEndDate
				},{
				    width:250,
				    xtype:'datefield',
				    fieldLabel: '<fmt:message key="user.expireTime.v1"/>',
				    labelWidth: 70,
				    readOnly: true,
				    format: 'Y-m-d',
				    name: 'v1ExpireEndDate',
				    id:'v1Expire',		    
				    value:Ext.Date.add(new Date(),Ext.Date.YEAR,1),
				    anchor:'99%',
				    style: 'padding: 0px 4px',
				    value:this.v1ExpireEndDate
				},{
				    width:250,
				    xtype:'datefield',
				    fieldLabel: '<fmt:message key="user.expireTime.v2"/>',
				    labelWidth: 70,
				    format: 'Y-m-d',
				    name: 'v2ExpireEndDate',
				    id:'v2Expire',		    
				    value:Ext.Date.add(new Date(),Ext.Date.YEAR,1),
				    anchor:'99%',
				    style: 'padding: 0px 4px',
				    value:this.v2ExpireEndDate
				},{
				    width:250,
				    xtype:'datefield',
				    fieldLabel: '<fmt:message key="user.expireTime.v3"/>',
				    labelWidth: 70,
				    format: 'Y-m-d',
				    name: 'v3ExpireEndDate',
				    id:'v3Expire',		    
				    value:Ext.Date.add(new Date(),Ext.Date.YEAR,1),
				    anchor:'99%',
				    style: 'padding: 0px 4px',
				    value:this.v3ExpireEndDate
				}]
        	}]
    	});
    	this.add(form);
    },  
});
