<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MemberYearsTemplateForm', {
	extend: 'Ext.data.Model',
	idProperty: 'templateId',
    fields: [
		{name: 'templateId',	mapping: 'templateId',	type: 'int'},
		{name: 'status',	 	mapping: 'status',		type: 'bool'},
		{name: 'isDelete',	    mapping: 'isDelete',	type: 'bool'},
		{name: 'yearsNumber',   mapping: 'yearsNumber', type: 'int'},
		{name: 'yearsName',	    mapping: 'yearsName',   type: 'string'},
		{name: 'price',		    mapping: 'price',	    type: 'string'},
		{name: 'sort',	        mapping: 'sort',	    type: 'int'},
		{name: 'profit',		mapping: 'profit',	    type: 'string'},
		{name: 'createTime',	mapping: 'createTime',	type: 'string'},
		{name: 'updateTime',	mapping: 'updateTime',	type: 'string'}
    ]
});

Ext.define('MyExt.productManager.MemberYearsTemplateFormPanel', {
    extend : 'Ext.form.Panel',
 	header: false,
 	labelAlign: 'right',
 	labelWidth: 55,
 	bodyPadding: '5 5 0',
    defaultType: 'textfield',
	
	initComponent : function(config) {
		Ext.apply(this, config);
		
		this.rendererApply= Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        	    {id: '5', name: '<fmt:message key="user.level5"/>'},
        	    {id: '4', name: '<fmt:message key="user.level4"/>'},
        		{id: '2', name: '<fmt:message key="user.level2"/>'},
        	]});
		
		this.items = [
		{xtype: 'hiddenfield', name: 'templateId', allowBlank: true,value:this.templateId},
		{
			fieldLabel: '<fmt:message key="member.years.template.yearsNumber" />',
   			name: 'yearsNumber',
   			labelWidth: 80,
   			xtype: 'textfield',
   			value:this.yearsNumber,
   			allowBlank: false,
  			anchor: '99%',
		},{
       		labelWidth: 80,
			xtype: 'combobox',
			fieldLabel: '<fmt:message key="member.years.template.level" />',
	        displayField: 'name',
	        valueField: 'id',
	        store: this.rendererApply,
	        editable: false,
	        allowBlank: false,
	        queryMode: 'local',
	        typeAhead: true,
	        anchor: '99%' ,
	        value: this.level,
	        name: 'level'
		},{
			xtype: 'textfield',
	    	fieldLabel: '<fmt:message key="member.years.template.yearsName" />',
	    	labelWidth: 80,
			name: 'yearsName',
         	allowBlank: false,
         	value:this.yearsName,
         	anchor: '99%'
		},{
			xtype: 'textfield',
	    	fieldLabel: '<fmt:message key="member.years.template.price" />',
	    	labelWidth: 80,
			name: 'price',
         	allowBlank: false,
         	value:this.price,
         	anchor: '99%'
		},{
			xtype: 'textfield',
	    	fieldLabel: '<fmt:message key="member.years.template.profit" />',
	    	labelWidth: 80,
			name: 'profit',
         	allowBlank: false,
         	value:this.profit,
         	anchor: '99%'
		},{
			xtype: 'numberfield',
	    	fieldLabel: '<fmt:message key="member.years.template.sort" />',
	    	labelWidth: 80,
			name: 'sort',
         	allowBlank: false,
         	value:this.sort,
         	anchor: '99%'
		}];
		
		this.reader = new Ext.data.JsonReader({
			totalProperty: 'total',
			model: 'MemberYearsTemplateForm',
			root: 'data'
		}); 
    	this.callParent(arguments);
    }
});