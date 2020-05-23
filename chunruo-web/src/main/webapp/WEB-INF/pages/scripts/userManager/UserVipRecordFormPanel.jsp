<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.userManager.UserVipRecordFormPanel', {
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
        
        this.rendererPaymentType= Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: '1', name: '<fmt:message key="user.paymentType1"/>'},
        		{id: '0', name: '<fmt:message key="user.paymentType0"/>'}
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
					xtype: 'combobox',
					fieldLabel: '<fmt:message key="user.recordType" />',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.rendererApply,
			        style: 'padding: 0px 4px',
			        allowBlank: false,
			        queryMode: 'local',
			        typeAhead: true,
			        anchor: '99%' , 
			        id: 'recordType',
				},{
				    width: 250,
	        		labelWidth: 70,
					xtype: 'textfield',
					fieldLabel: '<fmt:message key="user.payStatus" />',
			        style: 'padding: 0px 4px',
			        allowBlank: false,
			        queryMode: 'local',
			        typeAhead: true,
			        anchor: '99%' , 
			        value: '<fmt:message key="user.payStatus2"/>',
			        readOnly: true,
				},{
				    width: 250,
	        		labelWidth: 70,
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="user.vipType"/>',
				    style: 'padding: 0px 4px',
				    value: '<fmt:message key="user.level3"/>',
				    name: 'vipType',
				    anchor:'99%',
				    readOnly: true,
				    id: 'vipType'
				},{
				    width: 250,
	        		labelWidth: 70,
				    xtype:'numberfield',
				    fieldLabel: '<fmt:message key="user.cost"/>',
				    style: 'padding: 0px 4px',	  
				    name: 'cost',
				    anchor:'99%',
				    id: 'cost'
				},{
				    width: 250,
	        		labelWidth: 70,
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="user.tradeNo"/>',
				    style: 'padding: 0px 4px',   
				    name: 'tradeNo',
				    anchor:'99%',
				    id: 'tradeNo'
				},{
				    width: 250,
	        		labelWidth: 70,
					xtype: 'combobox',
					fieldLabel: '<fmt:message key="user.paymentType" />',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.rendererPaymentType,
			        style: 'padding: 0px 4px',
			        allowBlank: false,
			        queryMode: 'local',
			        typeAhead: true,
			        anchor: '99%' , 
			        id: 'paymentType',
				}]
        	}]
    	});
    	this.add(form);
    },  
});
