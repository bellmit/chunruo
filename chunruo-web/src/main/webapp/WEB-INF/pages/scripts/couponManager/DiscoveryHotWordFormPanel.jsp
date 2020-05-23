<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('DiscoveryHotWord', {
	extend: 'Ext.data.Model',
	idProperty: 'wordId',
    fields: [
		{name: 'wordId',	 		mapping: 'wordId',		    type: 'int'},
		{name: 'hotWord',	 		mapping: 'hotWord',			type: 'string'},
		{name: 'isRecommend',		mapping: 'isRecommend',		type: 'bool'},
		{name: 'createTime',		mapping: 'createTime',		type: 'string'},
		{name: 'updateTime',		mapping: 'updateTime',		type: 'string'}
	]
});

Ext.define('MyExt.couponManager.DiscoveryHotWordFormPanel', {
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
			    xtype:'textfield',
            	fieldLabel: '<fmt:message key="discovery.hot.hotWord" />',
				name: 'hotWord',
           		readOnly: this.isEditor,
            	allowBlank: false,
            	labelWidth: 120,
            	anchor: '98%'
        	},{
				xtype: 'container',
				layout: 'hbox',
				items: [{
					xtype: 'radiogroup',
		    		fieldLabel:'<fmt:message key="discovery.hot.isRecommend" />',
		    		name: 'isEnabled',
		 			labelWidth: 120,
					itemId: 'isEnabled',
					items:[
                      	{ boxLabel: '<fmt:message key="button.no" />', id:'ty', name: 'isRecommend',  inputValue:0},
						{ boxLabel: '<fmt:message key="button.yes" />',id:'qy', name: 'isRecommend', inputValue:1}
					]
				}]
		    }
        ];
        
        this.reader = new Ext.data.JsonReader({
			totalProperty: 'total',
			model: 'DiscoveryHotWord',
			root: 'data'
		}); 
    	this.callParent();
    }
});
