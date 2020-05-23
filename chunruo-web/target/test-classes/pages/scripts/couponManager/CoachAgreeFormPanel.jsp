<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.couponManager.CoachAgreeFormPanel', {
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
						boxLabel: '<fmt:message key="refund.agree"/>',  
						name: 'result',  
						inputValue:'true',  
						checked : true,
						allowBlank : false  
							}]  
							
		      }]
			   }]
        }];
	   this.callParent();
    }
});