<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('HelpQuestion', {
	extend: 'Ext.data.Model',
	idProperty: 'questionId',
    fields: [
		{name: 'questionId',	mapping: 'questionId',		type: 'int'},
		{name: 'sort',	 		mapping: 'sort',		    type: 'int'},
		{name: 'type',	 		mapping: 'type',		    type: 'int'},
		{name: 'name',	 		mapping: 'name',			type: 'string'},
		{name: 'questionDesc',	mapping: 'questionDesc',	type: 'string'},
		{name: 'isNoteRed',		mapping: 'isNoteRed',		type: 'bool'},
		{name: 'createTime',    mapping: 'createTime',		type: 'string'},
		{name: 'updateTime',    mapping: 'updateTime',		type: 'string'}
	]
});

Ext.define('MyExt.systemManager.HelpQuestionFormPanel', {
    extend : 'Ext.form.Panel',
 	header: false,
 	labelAlign: 'right',
 	labelWidth: 55,
 	bodyPadding: '5 5 0',
    defaultType: 'textfield',
	
	initComponent : function(config) {
		Ext.apply(this, config);
		
		this.typeStore = Ext.create('Ext.data.Store', {
			autoDestroy: true,
			model: 'InitModel',
			data: [
			    {id: '1', name: '<fmt:message key="help.question.type1"/>'},
				{id: '2', name: '<fmt:message key="help.question.type2"/>'},
			]
		});
		
		this.items = [
			{xtype: 'hiddenfield', name: 'questionId', allowBlank: true},
			{
						xtype: 'combobox',
						labelWidth: 120,
						fieldLabel: '<fmt:message key="help.question.type" />',
						name: 'type',
				        displayField: 'name',
				        valueField: 'id',
				        store: this.typeStore,
				        editable: false,
				        queryMode: 'local',
				        typeAhead: true,
				        anchor: '98%'
	       			},
			{
			    xtype:'numberfield',
            	fieldLabel: '<fmt:message key="help.question.sort" />',
				name: 'sort',
            	allowBlank: false,
            	labelWidth: 120,
            	anchor: '98%'
        	},
        	{
			    xtype:'textfield',
            	fieldLabel: '<fmt:message key="help.question.name" />',
				name: 'name',
            	allowBlank: false,
            	labelWidth: 120,
            	anchor: '98%'
        	},
        	{
			    xtype:'textarea',
            	fieldLabel: '<fmt:message key="help.question.questionDesc" />',
				name: 'questionDesc',
            	allowBlank: false,
            	labelWidth: 120,
            	anchor: '98%'
        	},{
				xtype: 'container',
				layout: 'hbox',
				items: [{
					xtype: 'radiogroup',
		    		fieldLabel:'<fmt:message key="help.question.isNoteRed" />',
		    		name: 'isNoteRed',
		 			labelWidth: 120,
					itemId: 'isNoteRed',
					anchor: '98%',
					items:[
                      	{ boxLabel: '<fmt:message key="button.no" />', id:'ty', name: 'isNoteRed',  inputValue:0},
						{ boxLabel: '<fmt:message key="button.yes" />',id:'qy', name: 'isNoteRed', inputValue:1}
					]
				}]
		    }
        ];
        
        this.reader = new Ext.data.JsonReader({
			totalProperty: 'total',
			model: 'HelpQuestion',
			root: 'data'
		}); 
    	this.callParent();
    }
});
