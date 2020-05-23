<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('GroupForm', {
	extend: 'Ext.data.Model',
    fields: [
    	{name: 'groupId', mapping: 'groupId', type: 'int'},
    	{name: 'name', mapping: 'name', type: 'string'},
    	{name: 'isEnable', mapping: 'isEnable', type: 'bool'},
    	{name: 'roleIds', mapping: 'roleIds', type: 'string'},
    ],
    idProperty: 'groupId'
});

Ext.define('MyExt.systemManager.GroupFormPanel', {
	extend : 'Ext.form.Panel',
 	header: false,
 	labelAlign: 'right',
 	labelWidth: 55,
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
        	margins: '30 5 0 25',
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
		
		this.items = [{
            xtype: 'hidden',
            name: 'groupId',
            allowBlank: true
        },{
            fieldLabel: '<fmt:message key="group.name" />',
            name: 'name',
            allowBlank: false,
            anchor: '98%'
        },{
        	fieldLabel: '<fmt:message key="group.disable" />',
           	height: 24,
            xtype: 'fieldcontainer',
            layout: 'hbox',
            defaultType: 'textfield',
            items: [this.enableCheckbox, this.disabledCheckbox]
        },{
        	anchor: '100%',
            fieldLabel: '<fmt:message key="group.roles"/>',
            xtype: 'itemselectorfield',
            imagePath: '../ux/images/',
            name: 'roleIds',
            store: this.roleIdStore,
            displayField: 'name',
            valueField: 'id',
            msgTarget: 'side',
            fromTitle: '<fmt:message key="group.roles.available"/>',
            toTitle: '<fmt:message key="group.roles.selected"/>',
            height: 260,
            anchor: '98%'
        }];
        
        this.reader = new Ext.data.JsonReader({
			totalProperty: 'total',
			model: 'GroupForm',
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
