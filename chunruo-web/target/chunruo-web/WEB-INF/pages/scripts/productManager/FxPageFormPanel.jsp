<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('FxPageForm', {
	extend: 'Ext.data.Model',
    fields: [
    	{name: 'pageId',		mapping: 'pageId',			type: 'int'},
		{name: 'channelId',	 	mapping: 'channelId',		type: 'int'},
		{name: 'channelName',	mapping: 'channelName',		type: 'string'},
		{name: 'pageName',	 	mapping: 'pageName',		type: 'string'},
		{name: 'categoryType',	mapping: 'categoryType',	type: 'int'},
		{name: 'categoryName',	mapping: 'categoryName',	type: 'string'},
		{name: 'createTime',	mapping: 'createTime',		type: 'string'},
		{name: 'updateTime',	mapping: 'updateTime',		type: 'string'},
    ],
    idProperty: 'pageId'
});

Ext.define('MyExt.productManager.FxPageFormPanel', {
    extend : 'Ext.form.Panel',
 	header: false,
 	labelAlign: 'right',
 	labelWidth: 55,
 	bodyPadding: '5 5 0',
    defaultType: 'textfield',
	
	initComponent : function(config) {
		Ext.apply(this, config);
		 
	    this.fageTypeStore = Ext.create('Ext.data.Store', {
        	data: [
        		{id: '0', name: '<fmt:message key="fx.page.categoryType.0" />'},
        		{id: '1', name: '<fmt:message key="fx.page.categoryType.1" />'},
        		{id: '2', name: '<fmt:message key="fx.page.categoryType.2" />'}
        	]
        });
        if(this.isEditPage){
        		this.items = [
					{xtype: 'hiddenfield', name: 'channelId' , value: this.channelId, allowBlank: true},
					{
		            	fieldLabel: '<fmt:message key="fx.page.pageName" />',
						name: 'pageName',
		           		readOnly: this.isEditor,
		            	allowBlank: false,
		            	anchor: '98%',
		            	value:this.pageName
		        	},{
		   				xtype: 'combobox',
						fieldLabel: '<fmt:message key="fx.page.categoryType"/>',
						name: 'typeId',
				        displayField: 'name',
				        valueField: 'id',
				        store: this.fageTypeStore,
				        anchor: '98%',
				        value:this.categoryType
		        	}
		        ];
        }else{
        		this.items = [
				{xtype: 'hiddenfield', name: 'pageId', allowBlank: true},
				{xtype: 'hiddenfield', name: 'channelId' , value: this.channelId, allowBlank: true},
				{
	            	fieldLabel: '<fmt:message key="fx.page.pageName" />',
					name: 'pageName',
	           		readOnly: this.isEditor,
	            	allowBlank: false,
	            	anchor: '98%'
	        	},{
	            	fieldLabel: '<fmt:message key="fx.page.channelName" />',
					name: 'channelName',
					value : this.channelName,
	           		readOnly: true,
	            	allowBlank: false,
	            	anchor: '98%'
	        	},{
	   				xtype: 'combobox',
					fieldLabel: '<fmt:message key="fx.page.categoryType"/>',
					name: 'typeId',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.fageTypeStore,
			        anchor: '98%'
	        	}
	        ];
        }

        
        this.reader = new Ext.data.JsonReader({
			totalProperty: 'total',
			model: 'FxPageForm',
			root: 'data'
		}); 
    	this.callParent();
    }
});
