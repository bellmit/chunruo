<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('ProductAnswerForm', {
	extend: 'Ext.data.Model',
    fields: [
    	{name: 'answerId',	 		mapping: 'answerId',		type: 'int'},
		{name: 'questionId',	 	mapping: 'questionId',		type: 'int'},
		{name: 'userId',	 		mapping: 'userId',			type: 'int'},
		{name: 'userName',	        mapping: 'userName',		type: 'string'},
		{name: 'status',	 		mapping: 'status',			type: 'string'},
		{name: 'content',	 		mapping: 'content',			type: 'string'},
		{name: 'createTime',		mapping: 'createTime',		type: 'string'},
		{name: 'updateTime',	 	mapping: 'updateTime',		type: 'string'}
	],
    idProperty: 'answerId'
});

Ext.define('MyExt.productManager.ProductAnswerFormPanel', {
    extend : 'Ext.form.Panel',
 	header: false,
 	labelAlign: 'right',
 	labelWidth: 55,
 	bodyPadding: '5 5 0',
    defaultType: 'textfield',
	
	initComponent : function(config) {
		Ext.apply(this, config);
		 
	    this.fageTypeStore = Ext.create('Ext.data.Store', {
	    	fields:['fileId', 'fileName'],  
	    	proxy: {
				type: 'ajax',
				url: '<c:url value="/user/getSystemUserInfo.json"/>',
				reader: {
					type : 'json',
                	root: 'data'
            	}
			}
	    	
        });
      	this.items = [
			{xtype: 'hiddenfield', name: 'questionId' , value : this.questionId, allowBlank: true},
			{
				xtype: 'textareafield',
	           	fieldLabel: '<fmt:message key="product.answer.content" />',
				name: 'content',
				emptyText : '<fmt:message key="product.answer.add.default.content" />',
				maxLength: 100,
				minLength : 4,
				rows : 5,
	           	allowBlank: false,
	           	anchor: '98%'
	       	},{
	  			xtype: 'combobox',
				fieldLabel: '<fmt:message key="product.answer.userName"/>',
				name: 'userId',
		        displayField: 'name',
		        valueField: 'id',
		        queryMode: 'local',
		        store: {
					xtype: 'store',
					autoLoad: true,
					autoDestroy: true,
					sortOnLoad: true,
					remoteSort: true,
					model: 'InitModel',
					proxy: {
						type: 'ajax',
						url: '<c:url value="/user/getSystemUserInfo.json"/>',
						reader: {
							type : 'json',
							root: 'data'
						
	  					}
					},
					scope: this
				},
		        anchor: '98%'
	       	}
        ];

        this.reader = new Ext.data.JsonReader({
			totalProperty: 'total',
			model: 'ProductAnswerForm',
			root: 'data'
		}); 
    	this.callParent();
    }
});
