<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.couponManager.SendByLevelFormPanel', {
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
        
        this.objectTypeStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
				{id: '1', name: '<fmt:message key="user.level1"/>'},
				{id: '2', name: '<fmt:message key="user.level2"/>'}
				
        	]
        });
        
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
					xtype: 'checkboxgroup',
					fieldLabel: '<fmt:message key="coupon.sender" />',
			        displayField: 'name',
			        valueField: 'id',
			        style: 'padding: 0px 4px',
			        allowBlank: false,
			        queryMode: 'local',
			        typeAhead: true,
			        anchor: '99%' , 
			        name: 'level',
			        items: [
                  		{boxLabel: '<fmt:message key="user.level1" />', name: '<fmt:message key="user.level1" />',inputValue:'1',anchor:'100%'},
                  		{boxLabel: '<fmt:message key="user.level2" />', name: '<fmt:message key="user.level2" />',inputValue:'2',anchor:'100%'},
                  		{boxLabel: '<fmt:message key="user.level4" />', name: '<fmt:message key="user.level4" />',inputValue:'4',anchor:'100%'},
                  		{boxLabel: '<fmt:message key="user.level5" />', name: '<fmt:message key="user.level5" />',inputValue:'5',anchor:'100%'},
             		]
				}]
        	}]
    	});
    	this.add(form);
    },  
});
