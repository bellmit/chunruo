<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.refundManager.RefundUnagreeFormPanel', {
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
  					xtype: 'radiogroup',  
					fieldLabel: "<fmt:message key="refund.is.agree"/>",  
					items : [{  
						boxLabel: '<fmt:message key="refund.unagree"/>',  
						name: 'result',  
						inputValue:'false',
						checked : true,
						allowBlank : false
					}]  		
		      	},{
		      		xtype:'textfield',
				    fieldLabel: '<fmt:message key="refund.reason"/>',
				    labelWidth: 100,
				    height: 50,
				    readOnly: false,
				    maxLength:50,
				    minLength:10,
				    maxLengthText: '<fmt:message key="refund.reason.blankText"/>',
				    minLengthText: '<fmt:message key="refund.reason.blankText"/>',
				    name: 'reason',
				    anchor:'97%',
				    allowBlank : false
				}]
			}]
        }];
    	this.callParent();
    }
});