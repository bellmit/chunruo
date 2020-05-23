<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('PostageTemplate', {
	extend: 'Ext.data.Model',
	idProperty: 'templateId',
    fields: [
		{name: 'templateId',		mapping: 'templateId',		type: 'int'},
		{name: 'warehouseId',		mapping: 'warehouseId',		type: 'int'},
		{name: 'warehouseName',		mapping: 'warehouseName',	type: 'string'},
		{name: 'productType',       mapping: 'productType', 	type: 'int'},
		{name: 'name',	 			mapping: 'name',			type: 'string'},
		{name: 'isFreeTemplate',	mapping: 'isFreeTemplate',	type: 'string'},
		{name: 'freePostageAmount',	mapping: 'freePostageAmount',type: 'string'},
    ]
});

Ext.define('MyExt.productManager.PostageTemplateFormPanel', {
    extend : 'Ext.form.Panel',
 	header: false,
 	labelAlign: 'right',
 	bodyPadding: '5 5 0',
    defaultType: 'textfield',
    autoScroll: true,
    isFreePostage: false,
    viewConfig: {
        stripeRows: true,
        enableTextSelection: true
    },
    
	initComponent : function(config) {
		Ext.apply(this, config);
		
	    this.items = [{
			xtype: 'hiddenfield', 
			name: 'warehouseId',
		    value: this.record.data.warehouseId
		},{
			xtype: 'textfield',
			labelWidth: 60,
			fieldLabel: '<fmt:message key="postage.template.name"/>',
			name: 'name',
			anchor:'97%'
		},{
			xtype: 'textfield',
			labelWidth: 60,
			fieldLabel: '<fmt:message key="postage.template.warehouseName"/>',
			anchor:'97%',
			editable: false,
			readOnly:true,
			value: this.record.data.warehouseName, 
		},{
			xtype: 'fieldset',
			title: '<fmt:message key="postage.template.default"/>',
           	layout: 'hbox',
           	anchor: '98%',
           	items: [{
                xtype: 'container',
                flex: 1,
                layout: 'anchor',
                items: [{
                	xtype: 'textfield',
       				fieldLabel: (this.record.data.productType == 3 || this.record.data.productType == 4) ? '<fmt:message key="postage.template.firstWeigth2" />' : '<fmt:message key="postage.template.firstWeigth" />',
       				labelWidth: 90,
           			name: 'firstWeigth',
           			allowBlank: false,
           			anchor: '98%'
       			},{
       				xtype: 'textfield',
           			fieldLabel: (this.record.data.productType == 3 || this.record.data.productType == 4) ? '<fmt:message key="postage.template.firstPrice2" />' : '<fmt:message key="postage.template.firstPrice" />',
           			labelWidth: 90,
           			name: 'firstPrice',
           			allowBlank: false,
           			anchor: '98%'
       			},{
       				xtype: 'textfield',
           			fieldLabel: (this.record.data.productType == 3 || this.record.data.productType == 4) ? '<fmt:message key="postage.template.afterWeigth2" />' :'<fmt:message key="postage.template.afterWeigth" />',
           			labelWidth: 90,
           			name: 'afterWeigth',
           			allowBlank: false,
           			anchor: '98%'
       			},{
       				xtype: 'textfield',
           			fieldLabel: (this.record.data.productType == 3 || this.record.data.productType == 4) ? '<fmt:message key="postage.template.afterPrice2" />' :'<fmt:message key="postage.template.afterPrice" />',
           			labelWidth: 90,
           			name: 'afterPrice',
           			allowBlank: false,
           			anchor: '98%'
       			},{
       				xtype: 'textfield',
       				hidden: (this.record.data.productType == 3 || this.record.data.productType == 4) ? false : true,
           			fieldLabel: '<fmt:message key="postage.template.packageWeigth" />',
           			labelWidth: 90,
           			name: 'packageWeigth',
           			anchor: '98%'
       			}]
            }]
        }];
        
		this.reader = new Ext.data.JsonReader({
			totalProperty: 'total',
			model: 'PostageTemplate',
        	root: 'data'
		});
    	this.callParent();
    }
});