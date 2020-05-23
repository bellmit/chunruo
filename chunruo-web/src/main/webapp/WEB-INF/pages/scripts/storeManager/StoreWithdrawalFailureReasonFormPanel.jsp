<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.storeManager.StoreWithdrawalFailureReasonFormPanel', {
    extend : 'Ext.form.Panel',
    alias: ['widget.storeWithdrawalFailureReasonForm'],
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
            title: Ext.String.format(this.fontStyle, '<fmt:message key="store.withdrawal.failure.remarks"/>'),
            bodyPadding: '5 5 0',
            anchor: '99%',
            layout: 'hbox',
            items:[{
                xtype: 'container',
                flex: 1,
                layout: 'anchor',
                items: [{
		      		xtype:'textarea',
				    fieldLabel: '<fmt:message key="user.replyMsg"/>',
				    labelWidth: 100,
				    height: 75,
				    readOnly: false,
//				    maxLength:50,
//				    minLength:10,
//				    maxLengthText: '<fmt:message key="refund.reason.blankText"/>',
//				    minLengthText: '<fmt:message key="refund.reason.blankText"/>',
				    name: 'replyMsg',
				    value:'<fmt:message key="user.withdrawal.failure.default.reason"/>',
				    anchor:'97%',
				    allowBlank : false
				}]
			   }]
        }];

	  
        
       
    	this.callParent();
    }
});