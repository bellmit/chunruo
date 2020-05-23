<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('StartImageTemplateForm', {
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

Ext.define('MyExt.systemManager.StartImageTemplateFormPanel', {
    extend : 'Ext.form.Panel',
 	header: false,
 	labelAlign: 'right',
 	labelWidth: 55,
 	bodyPadding: '5 5 0',
    defaultType: 'textfield',
	
	initComponent : function(config) {
		Ext.apply(this, config);
		
		this.items = [
		{xtype: 'hiddenfield', name: 'templateId', allowBlank: true,value:this.templateId},
		{
          	xtype: 'datefield',
     		fieldLabel: '<fmt:message key="productSeckill.startTime" />',
     		width: 250,
       		labelWidth: 80,
         	name: 'beginTime',
         	format: 'Y-m-d H:i:s',
         	editable: true,
         	anchor: '99%',
		    allowBlank: false,
		    scope: this,
		    value:this.beginTime,
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
       		format: 'Y-m-d H:i:s',
         	name: 'endTime',
         	editable: true,
         	anchor: '99%',
		    allowBlank: false,
		    value:this.endTime,
		    listeners:{ 
              	scope: this, 
	        	select:function(dateField, date){  
	            	var startDate = this.down('datefield[name=startTime]'); 
	            	if(startDate.getValue() != null && startDate.getValue() > dateField.getValue()){  
	                	dateField.setValue(startDate.getValue());               
	            	}  
	        	}  
	    	} 
		},{
			fieldLabel: '<fmt:message key="home.popup.isInvitePage" />',
   			name: 'isInvitePage',
   			labelWidth: 80,
   			xtype: 'checkbox',
   			value:this.isInvitePage,
  			anchor: '99%',
		},{
			xtype: 'textfield',
	    	fieldLabel: '<fmt:message key="start.image.wholsale.id" />',
	    	labelWidth: 80,
			name: 'productId',
         	allowBlank: true,
         	value:this.productId,
         	anchor: '99%'
		}];
		
		this.reader = new Ext.data.JsonReader({
			totalProperty: 'total',
			model: 'StartImageTemplateForm',
			root: 'data'
		}); 
    	this.callParent(arguments);
    }
});