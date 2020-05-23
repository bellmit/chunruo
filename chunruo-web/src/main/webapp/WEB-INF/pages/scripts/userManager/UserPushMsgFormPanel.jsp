<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.userManager.UserPushMsgFormPanel', {
    extend : 'Ext.form.Panel',
    alias: ['widget.pushMsgForm'],
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
            title: Ext.String.format(this.fontStyle, '<fmt:message key="order.info"/>'),
            bodyPadding: '5 5 0',
            anchor: '99%',
            layout: 'hbox',
            items:[{
                xtype: 'container',
                flex: 1,
                layout: 'anchor',
                items: [{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="user.push.msg"/>',
				    labelWidth: 100,
				    readOnly: false,
				    id:'phone',
				    name: 'phone',
				    anchor:'97%'
				},{
				   xtype:'textfield',
				    fieldLabel: '<fmt:message key="user.mobile"/>',
				    labelWidth: 100,
				    readOnly: false,
				    id:'phone',
				    name: 'phone',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="user.mobile"/>',
				    labelWidth: 100,
				    readOnly: false,
				    id:'phone',
				    name: 'phone',
				    anchor:'97%'
				}]
			   }]
        }];
        
       
    	this.callParent();
    }
});