<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.storeManager.ExportWithdrawalExcel', {
    extend : 'Ext.form.Panel',
    alias: ['widget.orderForm'],
    requires : [],
 	header: false,
 	buttonAlign: 'center',
 	labelAlign: 'right',
 	labelWidth: 40,
 	bodyPadding: '5 5 0',
    defaultType: 'textfield',
    fontStyle: '<span style="font-size:14px;font-weight:bold;">{0}</span>',
    autoScroll: true,
    viewConfig: {
        stripeRows: true,
        enableTextSelection: true
    },
    
	initComponent : function(config) {
		Ext.apply(this, config);
	    
	    
	    	this.rendererApply= Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        	    {id: '5', name: '<fmt:message key="store.withDrawal.statusAll"/>'},
        	    {id: '4', name: '<fmt:message key="store.withDrawal.status4"/>'},
        	    {id: '3', name: '<fmt:message key="store.withDrawal.status3"/>'},
        		{id: '2', name: '<fmt:message key="store.withDrawal.status2"/>'},
        		{id: '1', name: '<fmt:message key="store.withDrawal.status1"/>'}
        	]
        }
        );
	    
	    
	    this.items = [{
        	xtype: 'fieldset',
            title: Ext.String.format(this.fontStyle, '<fmt:message key="order.info"/>'),
            bodyPadding: '5 5 0',
            anchor: '99%',
            layout: 'hbox',
            items:[{
                xtype: 'container',
                flex: 1,
                layout: 'anchor',
                items: [{
       
				    xtype:'datefield',
				    fieldLabel: '<fmt:message key="order.beginTime"/>',
				    labelWidth: 100,
				    format: 'Y-m-d H:i:s',
				    readOnly: false,
				    id:'beginTime',
				    name: 'beginTime',
				    value:Ext.Date.add(new Date(),Ext.Date.DAY,-1),
				    anchor:'97%'
				},{

				    xtype:'datefield',
				    fieldLabel: '<fmt:message key="order.endTime"/>',
				    labelWidth: 100,
				    readOnly: false,
				    format: 'Y-m-d H:i:s',
				    name: 'endTime',
				    id:'endTime',
				    value:new Date(),
				    anchor:'97%'
				},{
	        		labelWidth: 100,
					xtype: 'combobox',
					fieldLabel: '<fmt:message key="store.withDrawal.status" />',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.rendererApply,
			        editable: false,
			        allowBlank: false,
			        queryMode: 'local',
			        typeAhead: true,
			        anchor: '97%' ,
			        id: 'status',
                    value: 5,
                    emptyValue:'<fmt:message key="store.withDrawal.statusAll" />'
				}]
			   }]
        }];
        
       
    	this.callParent();
    }
});