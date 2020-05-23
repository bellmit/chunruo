<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.productManager.ProductQuestionFormPanel', {
    extend : 'Ext.form.Panel',
    alias: ['widget.questionForm'],
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
            xtype: 'hidden',
            name: 'questionId',
            allowBlank: true
        },{
        	xtype: 'fieldset',
            title: Ext.String.format(this.fontStyle, '<fmt:message key="product.question.info"/>'),
            bodyPadding: '5 5 0',
            anchor: '99%',
            layout: 'hbox',
            items:[{
                xtype: 'container',
                flex: 1,
                layout: 'anchor',
                items: [{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="product.question.questionId"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'questionId',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="product.question.userId"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'userId',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="product.question.wholesaleId"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'productId',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="product.question.createtime"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'createTime',
				    anchor:'97%'
				}]
			   },{
			       xtype: 'container',
			       flex: 1,
			       layout: 'anchor',
			       items: [{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="product.question.status"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'status',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="product.question.userName"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'userName',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="product.question.productName"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'productName',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="product.question.updatetime"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'updateTime',
				    anchor:'97%'
				}]
			   }]
        },{
            xtype: 'fieldset',
            title: Ext.String.format(this.fontStyle, '<fmt:message key="product.question.content"/>'),
            bodyPadding: '5 5 0',
            anchor: '99%',
            items:[{
            	xtype: 'container',
            	layout: 'hbox',
            	items: [{
	                xtype: 'container',
	                flex: 1,
	                layout: 'anchor',
	                items: [{
				    	xtype:'textarea',
				    	labelAlign: 'top',
				    	fieldLabel: '<fmt:message key="product.question.content"/>',
				    	readOnly: true,
				    	name: 'content',
				    	anchor:'97%'
					}]
	            }]
            }]
        }];
        
       
		
    	this.callParent();
    }
});