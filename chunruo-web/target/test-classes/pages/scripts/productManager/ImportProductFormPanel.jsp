<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.productManager.ImportProductFormPanel', {
    extend : 'Ext.form.Panel',
    alias: ['widget.importProductFormPanel'],
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
            title: Ext.String.format(this.fontStyle, '<fmt:message key="product.wholesale.import"/>'),
            bodyPadding: '5 5 0',
            anchor: '99%',
            layout: 'hbox',
            items:[{
            	xtype: 'container',
                flex: 1,
                layout: 'anchor',
                items: [{
				        xtype: 'filefield',
				        name: 'productFile',
				        msgTarget: 'side',
				        allowBlank: false,
				        anchor: '100%',
				        buttonText: '<fmt:message key="product.wholesale.import"/>',
			    	}]
			}]
        }];
        
        if(this.isCheck != '0'){
        	<jkd:haveAuthorize access="/userRecharge/downloadFile.msp">
        	this.buttons = [{ 	
				text: '<fmt:message key="user.recharge.downloadFile"/>', 
				scope: this,  
		        handler: function(){
			    	var recordId = this.getForm().findField('recordId').getValue();
			    	window.location.href  = '<c:url value="/userRecharge/downloadFile.msp?recordId="/>' + recordId;
				}
		    }];
		    </jkd:haveAuthorize>
		}
    	this.callParent();
    }
});