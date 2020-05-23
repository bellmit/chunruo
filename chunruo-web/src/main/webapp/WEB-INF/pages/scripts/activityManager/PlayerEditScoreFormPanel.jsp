<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.activityManager.PlayerEditScoreFormPanel', {
    extend : 'Ext.form.Panel',
    alias: ['widget.agreeForm'],
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
            title: Ext.String.format(this.fontStyle, '<fmt:message key="button.check"/>'),
            bodyPadding: '5 5 0',
            anchor: '99%',
            layout: 'hbox',
            items:[{
                xtype: 'container',
                flex: 1,
                layout: 'anchor',
                items: [{
		      		xtype:'textfield',
				    fieldLabel: '<fmt:message key="activity.origin.score"/>',
				    labelWidth: 100,
				    height: 10,
				    readOnly: true,
				    maxLengthText: '<fmt:message key="refund.reason.blankText"/>',
				    minLengthText: '<fmt:message key="refund.reason.blankText"/>',
				    name: 'originScore',
				    anchor:'97%',
				    allowBlank : false,
				    value:this.score
				},{
		      		xtype:'textfield',
				    fieldLabel: '<fmt:message key="activity.now.score"/>',
				    labelWidth: 100,
				    height: 10,
				    readOnly: false,
				    maxLengthText: '<fmt:message key="refund.reason.blankText"/>',
				    minLengthText: '<fmt:message key="refund.reason.blankText"/>',
				    name: 'nowScore',
				    anchor:'97%',
				    allowBlank : false
				}]
			   }]
        }];
    	this.callParent();
    }
});