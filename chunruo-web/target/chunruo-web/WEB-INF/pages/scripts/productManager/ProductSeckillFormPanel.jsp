<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('ProductSeckillForm', {
	extend: 'Ext.data.Model',
	idProperty: 'seckillId',
    fields: [
		{name: 'seckillId',			mapping: 'seckillId',		type: 'int'},
		{name: 'seckillName',		mapping: 'seckillName',		type: 'string'},
		{name: 'statusName',	    mapping: 'statusName',		type: 'string'},
		{name: 'startTime',			mapping: 'startTime',		type: 'string'},
		{name: 'endTime',	        mapping: 'endTime',			type: 'string'},
		{name: 'status',			mapping: 'status',			type: 'bool'},
		{name: 'createTime',	 	mapping: 'createTime',		type: 'string'},
		{name: 'updateTime',	 	mapping: 'updateTime',		type: 'string'},
    ]
});

Ext.define('MyExt.productManager.ProductSeckillFormPanel', {
    extend : 'Ext.form.Panel',
 	header: false,
 	labelAlign: 'right',
 	labelWidth: 55,
 	bodyPadding: '5 5 0',
    defaultType: 'textfield',
	
	initComponent : function(config) {
		Ext.apply(this, config);
		
		this.items = [
		{xtype: 'hiddenfield', name: 'seckillId', allowBlank: true},
		{
		    width: 250,
       		labelWidth: 80,
		    xtype: 'textfield',
		    fieldLabel: '<fmt:message key="productSeckill.seckillName"/>',
		    name: 'seckillName',
		    anchor:'99%',
		    allowBlank: false
		},{
          	xtype: 'datefield',
     		fieldLabel: '<fmt:message key="productSeckill.startTime" />',
     		width: 250,
       		labelWidth: 80,
         	name: 'startTime',
         	editable: true,
         	format: 'Y-m-d H:i',
         	anchor: '99%',
		    allowBlank: false,
		    scope: this,
		    minValue: new Date(),
            listeners:{ 
              	scope: this, 
	        	select:function(dateField, date){  
	            	var endDate = this.down('datefield[name=endTime]'); 
	            	if(endDate.getValue() != null && dateField.getValue() > endDate.getValue()){  
	                	dateField.setValue(endDate.getValue());                   
	            	}  
	        	}  
	    	} 
		},{
          	xtype: 'datefield',
     		fieldLabel: '<fmt:message key="productSeckill.endTime" />',
     		width: 250,
       		labelWidth: 80,
         	name: 'endTime',
         	editable: true,
         	format: 'Y-m-d H:i',
         	anchor: '99%',
		    allowBlank: false,
		    minValue: new Date(),
		    listeners:{ 
              	scope: this, 
	        	select:function(dateField, date){  
	            	var startDate = this.down('datefield[name=startTime]'); 
	            	if(startDate.getValue() != null && startDate.getValue() > dateField.getValue()){  
	                	dateField.setValue(startDate.getValue());               
	            	}  
	        	}  
	    	} 
		}];
		
		this.reader = new Ext.data.JsonReader({
			totalProperty: 'total',
			model: 'ProductSeckillForm',
			root: 'data'
		}); 
    	this.callParent(arguments);
    }
});