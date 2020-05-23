<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('ResourceForm', {
	extend: 'Ext.data.Model',
    fields: [
    	{name: 'resourceId',	mapping: 'resourceId', 	type: 'int'},
    	{name: 'name',        	mapping: 'name', 		type: 'string'},
    	{name: 'menuId', 		mapping: 'menuId', 		type: 'int'},
    	{name: 'menuPath', 		mapping: 'menuPath', 	type: 'string'},
    	{name: 'linkPath', 		mapping: 'linkPath', 	type: 'string'},
    	{name: 'isEnable', 	  	mapping: 'isEnable', 	type: 'bool'}
    ],
    idProperty: 'resourceId'
});

Ext.define('MyExt.systemManager.ResourceFormPanel', {
	extend : 'Ext.form.Panel',
    requires : ['MyExt.systemManager.ResourceMenuPicker'],
 	header: false,
 	labelAlign: 'right',
 	labelWidth: 75,
 	bodyPadding: '5 5 0',
    defaultType: 'textfield',
	
	initComponent : function(config) {
		Ext.apply(this, config);
		
		this.enableCheckbox = new Ext.create('Ext.form.Checkbox', {
        	boxLabel: '<fmt:message key="button.enable"/>',
        	margins: '3 5 0 0',
        	labelAlign: 'after',
        	name: 'isEnable',
        	listeners: {
                change : function (checkbox, newValue, oldValue, eOpts) {
                	if(newValue){
                    	this.disabledCheckbox.setValue(false);
                    }
                },
        		scope: this
            },
        	scope: this
        });
        
        this.disabledCheckbox = new Ext.create('Ext.form.Checkbox', {
        	boxLabel: '<fmt:message key="button.disable"/>',
        	margins: '3 5 0 25',
        	style: {'padding-left': '20px'},
        	labelAlign: 'after',
        	listeners: {
                change : function (checkbox, newValue, oldValue, eOpts) {
                	if(newValue){
                    	this.enableCheckbox.setValue(false);
                    }
                },
        		scope: this
            },
        	scope: this
        });
        
        this.meunIdField = Ext.create('Ext.form.Hidden', {
			xtype: 'hiddenfield', 
			name: 'menuId', 
			allowBlank: true,
			multiSelect: true
		});
		
		this.items = [{
            xtype: 'hidden',
            name: 'resourceId',
            allowBlank: true
        },this.meunIdField,{
            fieldLabel: '<fmt:message key="resource.name" />',
            name: 'name',
            allowBlank: false,
            anchor: '98%'
        },{
            fieldLabel: '<fmt:message key="resource.linkPath" />',
            name: 'linkPath',
            allowBlank: false,
            anchor: '98%'
        },{
        	xtype: 'resourceMenuPicker',
	    	fieldLabel: '<fmt:message key="resource.menuPath" />',
	    	objPanel: this.meunIdField,
			name: 'menuPath',
			editable: false,
         	allowBlank: false,
         	anchor: '95%',
         	multiSelect: true
        },{
        	fieldLabel: '<fmt:message key="resource.isEnable" />',
           	height: 24,
            xtype: 'fieldcontainer',
            layout: 'hbox',
            defaultType: 'textfield',
            items: [this.enableCheckbox, this.disabledCheckbox]
        }];
        
        this.reader = new Ext.data.JsonReader({
			totalProperty: 'total',
			model: 'ResourceForm',
			root: 'data'
		}); 
    	this.callParent();
    	
    	this.on('actioncomplete', function(form, action, eOpts ){
    		var responseObject = Ext.JSON.decode(action.response.responseText);
    		if(responseObject.data != null && responseObject.data.length > 0){
    			if(!responseObject.data[0].isEnable){
    				this.disabledCheckbox.setValue(true);
    				this.enableCheckbox.setValue(false);
    			}
    		}
    	}, this);
    }
});
