<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('RoleForm', {
	extend: 'Ext.data.Model',
    fields: [
    	{name: 'id',  	mapping: 'id', 	type: 'int'},
    	{name: 'name', 	mapping: 'name', type: 'string'}
    ],
    idProperty: 'id'
});

Ext.define('MyExt.systemManager.RoleFormPanel', {
	extend : 'Ext.form.Panel',
    requires : [],
 	header: false,
 	labelAlign: 'right',
 	labelWidth: 55,
 	bodyPadding: '5 5 0',
    defaultType: 'textfield',
	
	initComponent : function(config) {
		Ext.apply(this, config);
		
		this.items = [{
            xtype: 'hidden',
            name: 'id',
            allowBlank: true
        },{
            fieldLabel: '<fmt:message key="role.name" />',
            name: 'name',
            allowBlank: false,
            anchor: '98%'
        }];
        
        this.reader = new Ext.data.JsonReader({
			totalProperty: 'total',
			model: 'RoleForm',
			root: 'data'
		}); 
    	this.callParent();
    }
});
