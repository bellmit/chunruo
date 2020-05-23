<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('RollingNotice', {
	extend: 'Ext.data.Model',
	idProperty: 'noticeId',
    fields: [
		{name: 'noticeId',	 		mapping: 'noticeId',		type: 'int'},
		{name: 'type',	 		    mapping: 'type',			type: 'string'},
		{name: 'content',	 		mapping: 'content',			type: 'string'},
		{name: 'isEnabled',			mapping: 'isEnabled',		type: 'int'},
		{name: 'createTime',		mapping: 'createTime',		type: 'string'},
		{name: 'updateTime',		mapping: 'updateTime',		type: 'string'}
	]
});

Ext.define('MyExt.couponManager.RollingNoticeFormPanel', {
    extend : 'Ext.form.Panel',
 	header: false,
 	labelAlign: 'right',
 	labelWidth: 55,
 	bodyPadding: '5 5 0',
    defaultType: 'textfield',
	
	initComponent : function(config) {
		Ext.apply(this, config);
		
		this.useRangeTypeStore = Ext.create('Ext.data.Store', {
			autoDestroy: true,
			model: 'InitModel',
			data: [
			    {id: '1', name: '<fmt:message key="system.roolingNotice.type1"/>'},
				{id: '2', name: '<fmt:message key="system.roolingNotice.type2"/>'},
			]
		});
		
		this.items = [
			{xtype: 'hiddenfield', name: 'noticeId', allowBlank: true},
			{
			    xtype:'textarea',
            	fieldLabel: '<fmt:message key="system.roolingNotice.content" />',
				name: 'content',
           		readOnly: this.isEditor,
            	allowBlank: false,
            	labelWidth: 120,
            	anchor: '98%'
        	},{
			    xtype:"combo",
			    name:'type',
			    displayField:'name',
			    valueField:'id',
			    store:this.useRangeTypeStore,
			    triggerAction:'all',
			    fieldLabel: '<fmt:message key="system.roolingNotice.type"/>', 
				labelWidth: 120,
				anchor: '98%',
			    selectOnFocus:true,
			    forceSelection: true,
			    allowBlank:false,
			    editable:false,
			    id:'types'
			},{
				xtype: 'container',
				layout: 'hbox',
				items: [{
					xtype: 'radiogroup',
		    		fieldLabel:'<fmt:message key="system.roolingNotice.isEnabled" />',
		    		name: 'isEnabled',
		 			labelWidth: 120,
					itemId: 'isEnabled',
					items:[
                      	{ boxLabel: '<fmt:message key="button.no" />', id:'ty', name: 'isEnabled',  inputValue:0},
						{ boxLabel: '<fmt:message key="button.yes" />',id:'qy', name: 'isEnabled', inputValue:1}
					]
				}]
		    }
        ];
        
        this.reader = new Ext.data.JsonReader({
			totalProperty: 'total',
			model: 'RollingNotice',
			root: 'data'
		}); 
    	this.callParent();
    }
});
