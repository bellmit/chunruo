<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('UserForm', {
	extend: 'Ext.data.Model',
    fields: [
    	{name: 'userId', 		     mapping: 'userId', 			type: 'int'},
    	{name: 'password', 	         mapping: 'password', 		    type: 'string'},
    	{name: 'confirmPassword',    mapping: 'confirmPassword',    type: 'string'},
    	{name: 'username', 	         mapping: 'username', 		    type: 'string'},
    	{name: 'groupName', 	     mapping: 'groupName', 		    type: 'string'},
    	{name: 'realname', 		     mapping: 'realname', 			type: 'string'},
    	{name: 'sex', 			     mapping: 'sex',				type: 'string'},
    	{name: 'isAdmin', 		     mapping: 'isAdmin',			type: 'bool'},
    	{name: 'enabled', 		     mapping: 'enabled', 		    type: 'bool'},
    	{name: 'email', 		     mapping: 'email', 			    type: 'string'},
    	{name: 'mobile', 		     mapping: 'mobile', 			type: 'string'},
    	{name: 'birthday', 	         mapping: 'birthday', 		    type: 'string'},
    	{name: 'addresss', 	         mapping: 'addresss', 		    type: 'string'},
    	{name: 'loginErrorTimes',    mapping: 'loginErrorTimes',    type: 'string'},
    	{name: 'createTime', 	     mapping: 'createTime', 		type: 'string'},
    	{name: 'updateTime', 	     mapping: 'updateTime',		    type: 'string'}
    ],
    idProperty: 'userId'
});

Ext.define('UserGroup', {
	extend: 'Ext.data.Model',
    fields: [
    	{name: 'groupId',     type: 'int'},
    	{name: 'name',        type: 'string'},
    	{name: 'rolePath',    type: 'string'},
    	{name: 'isEnable',    type: 'bool'},
    	{name: 'description', type: 'string'},
    	{name: 'createTime',  type: 'string'},
    	{name: 'updateTime',  type: 'string'}
    ],
    idProperty: 'groupId'
});

Ext.define('MyExt.systemManager.UserFormPanel', {
    extend : 'Ext.form.Panel',
 	header: false,
 	labelAlign: 'right',
 	labelWidth: 55,
 	isEditor: false,
 	isMyEditor: false,
 	bodyPadding: '5 5 0',
    defaultType: 'textfield',
	
	initComponent : function(config) {
		Ext.apply(this, config);
		
		this.groupStore = Ext.create('Ext.data.Store', {
			pageSize: 50,
	        autoLoad: true,
			autoDestroy: true,
			sortOnLoad: true,
			remoteSort: true,
			model: 'UserGroup',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/groupSys/list.json"/>',
				reader: {
					type : 'json',
                	root: 'data',
                	totalProperty: 'totalCount'
            	}
			},
			sorters: [{
	            property: 'createTime',
	            direction: 'desc'
	        }]
		});
		
		this.rendererApply = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: '1', name: '<fmt:message key="user.sex1"/>'},
        		{id: '0', name: '<fmt:message key="user.sex0"/>'},
        	]
        });
        	
        this.levelStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: '3', name: '<fmt:message key="user.admin.level3"/>'},
        		{id: '2', name: '<fmt:message key="user.admin.level2"/>'},
        		{id: '1', name: '<fmt:message key="user.admin.level1"/>'},
        	]
        });
		
		this.items = [{
			xtype: 'hiddenfield', 
			name: 'userId', 
			allowBlank: true, 
			value: this.userId
		},{
           	fieldLabel: '<fmt:message key="user.account" />',
			name: 'username',
          	readOnly: this.isEditor,
           	allowBlank: false,
           	anchor: '98%',
           	value:this.username
       	},{
           	fieldLabel: '<fmt:message key="user.password" />',
           	name: 'password',
           	inputType: 'password',
           	allowBlank: false,
           	anchor: '98%',
       	},{
       		fieldLabel: '<fmt:message key="user.confirmPassword" />',
           	name: 'confirmPassword',
           	inputType: 'password',
           	allowBlank: true,
           	anchor: '98%'
       	},{
			xtype: 'combobox',
			fieldLabel: '<fmt:message key="user.sex" />',
	        displayField: 'name',
	        valueField: 'id',
	        store: this.rendererApply,
	        editable: false,
	        allowBlank: false,
	        queryMode: 'local',
	        typeAhead: true,
	        anchor: '98%' ,
	        value: this.sex,
	        name: 'sex'
		},{
			xtype: 'combobox',
			fieldLabel: '<fmt:message key="user.admin.level" />',
	        displayField: 'name',
	        valueField: 'id',
	        store: this.levelStore,
	        readOnly: this.isMyEditor,
	        editable: false,
	        allowBlank: false,
	        queryMode: 'local',
	        typeAhead: true,
	        anchor: '98%' ,
	        value: this.level,
	        name: 'level'
		},{
			xtype: 'combobox',
			fieldLabel: '<fmt:message key="user.groupName" />',
	        displayField: 'name',
	        valueField: 'groupId',
	        store: this.groupStore,
	        readOnly: this.isMyEditor,
	        editable: false,
	        allowBlank: false,
	        typeAhead: true,
	        anchor: '98%' ,
	        value: this.level,
	        name: 'groupId'
		},{
       		fieldLabel: '<fmt:message key="user.realname" />',
           	name: 'realname',
           	allowBlank: false,
           	anchor: '98%',
           	value:this.realname
       	},{
       		fieldLabel: '<fmt:message key="user.mobile" />',
           	name: 'mobile',
           	allowBlank: false,
           	readOnly: this.isMyEditor,
           	anchor: '98%',
           	value:this.mobile
       	},{
       		fieldLabel: '<fmt:message key="user.email" />',
           	name: 'email',
           	allowBlank: false,
           	anchor: '98%',
           	value:this.email
       	}];
        
        this.reader = new Ext.data.JsonReader({
			totalProperty: 'total',
			model: 'UserForm',
			root: 'data'
		}); 
    	this.callParent();
    	
    	this.on('actioncomplete', function(form, action, eOpts ){
    		var responseObject = Ext.JSON.decode(action.response.responseText);
    		if(responseObject.data != null && responseObject.data.length > 0){
    			this.down('[name=sex]').setValue(0);
    			if(responseObject.data[0].sex){
    				this.down('[name=sex]').setValue(1);
    			}
    			this.down('[name=groupId]').setValue(responseObject.data[0].groupId);
    		}
    	}, this);
    }
});
