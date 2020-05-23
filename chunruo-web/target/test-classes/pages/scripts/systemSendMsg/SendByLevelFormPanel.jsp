<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.systemSendMsg.SendByLevelFormPanel', {
    extend : 'Ext.Panel',
 	header: false,
 	labelAlign: 'right',
 	labelWidth: 55,
 	bodyPadding: '5 5 0',
    defaultType: 'textfield',
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
				{id: '2', name: '<fmt:message key="user.level2"/>'},
				{id: '6', name: '<fmt:message key="user.level.all"/>'}
				
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
        	border: false,  
        	items: [,{
	        		width: 500,
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
                  		{boxLabel: '<fmt:message key="system.send.msg.objectType1" />', name: '<fmt:message key="system.send.msg.objectType1" />',inputValue:'1',anchor:'100%'},
                  		{boxLabel: '<fmt:message key="system.send.msg.objectType2" />', name: '<fmt:message key="system.send.msg.objectType2" />',inputValue:'2',anchor:'100%'},
                  		{boxLabel: '<fmt:message key="system.send.msg.objectType3" />', name: '<fmt:message key="system.send.msg.objectType3" />',inputValue:'3',anchor:'100%'},
                  		{boxLabel: '<fmt:message key="system.send.msg.objectType4" />', name: '<fmt:message key="system.send.msg.objectType4" />',inputValue:'4',anchor:'100%'},
                  		{boxLabel: '<fmt:message key="system.send.msg.objectType6" />', name: '<fmt:message key="system.send.msg.objectType6" />',inputValue:'6',anchor:'100%'},
                  		{boxLabel: '<fmt:message key="system.send.msg.objectType7" />', name: '<fmt:message key="system.send.msg.objectType7" />',inputValue:'7',anchor:'100%'},
                 		{boxLabel: '<fmt:message key="system.send.msg.objectType8" />', name: '<fmt:message key="system.send.msg.objectType8" />',inputValue:'8',anchor:'100%'},
                 		{boxLabel: '<fmt:message key="system.send.msg.objectType9" />', name: '<fmt:message key="system.send.msg.objectType9" />',inputValue:'9',anchor:'100%'},
             		]
				}]
    	});
    	this.add(form);
    },  
});
