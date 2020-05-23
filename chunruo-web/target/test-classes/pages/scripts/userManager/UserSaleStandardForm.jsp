<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.userManager.UserSaleStandardForm', {
    extend : 'Ext.form.Panel',
    alias: ['widget.userSaleStandardForm'],
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
	    
	    this.items = [{
        	xtype: 'fieldset',
            bodyPadding: '5 5 0',
            anchor: '99%',
            layout: 'hbox',
            items:[{
                xtype: 'container',
                flex: 1,
                layout: 'anchor',
                items: [{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="user.salesNum"/>',
				    labelWidth: 120,
				    readOnly: false,
				    id:'salesNum',
				    anchor:'97%',
				    },{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="user.hours"/>',
				    labelWidth: 120,
				    readOnly: false,
				    id:'hours',
				    anchor:'97%',
				    }]
			   }]
        }];
        
       
    	this.callParent();
    }
});