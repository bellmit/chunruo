<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.refundManager.RefundAgreeFormPanel', {
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
		
	    if(this.refundType == '<fmt:message key="order.refund.refundType.2"/>'){
    	
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
							
		      },{
				    xtype:'hiddenfield',
				    value:'<fmt:message key="refund_default_address"/>',
				    name: 'address',
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="order.item.productName"/>',
				    labelWidth: 100,
				    value:this.productName,
				    name:'productName',
				    readOnly: true,
				    anchor:'97%',
				    allowBlank : true
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="order.refund.refundType"/>',
				    labelWidth: 100,
				    value:this.refundType,
				    name:'refundType',
				    readOnly: true,
				    anchor:'97%',
				    allowBlank : true
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="order.refund.refundAmount"/>',
				    labelWidth: 100,
				    value:this.refundAmount,
				    name:'refundAmount',
				    readOnly: false,
				    anchor:'97%',
				    allowBlank : false
				},{
				   xtype:'textfield',
				    fieldLabel: '<fmt:message key="refund.agree.reason"/>',
				    labelWidth: 100,
				    height: 50,
				    readOnly: false,
				    maxLengthText: '<fmt:message key="refund.reason.blankText"/>',
				    minLengthText: '<fmt:message key="refund.reason.blankText"/>',
				    name: 'reason',
				    anchor:'97%',
				    hidden: !(this.refundStatus == '<fmt:message key="order.refund.refundStatus_9"/>'),
				}]
			   }]
        }];
	    
	 
	    
	    }else{
	    
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
		      },{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="order.item.productName"/>',
				    labelWidth: 100,
				    value:this.productName,
				    name:'productName',
				    readOnly: true,
				    anchor:'97%',
				    allowBlank : true
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="order.refund.refundType"/>',
				    labelWidth: 100,
				    value:this.refundType,
				    name:'refundType',
				    readOnly: true,
				    anchor:'97%',
				    allowBlank : false
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="order.refund.refundAmount"/>',
				    labelWidth: 100,
				    value:this.refundAmount,
				    name:'refundAmount',
				    readOnly: false,
				    anchor:'97%',
				    allowBlank : false
				},{
				   xtype:'textfield',
				    fieldLabel: '<fmt:message key="refund.agree.reason"/>',
				    labelWidth: 100,
				    height: 50,
				    readOnly: false,
				    maxLength:50,
				    minLength:10,
				    maxLengthText: '<fmt:message key="refund.reason.blankText"/>',
				    minLengthText: '<fmt:message key="refund.reason.blankText"/>',
				    name: 'reason',
				    anchor:'97%',
				    hidden: !(this.refundStatus == '<fmt:message key="order.refund.refundStatus_9"/>'),
				}]
			   }]
        }];
	    
	   
	    }
	   this.callParent();
    }
});