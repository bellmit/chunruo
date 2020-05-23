<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.refundManager.RefundExportExcel', {
    extend : 'Ext.form.Panel',
    alias: ['widget.refundExport'],
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
            title: Ext.String.format(this.fontStyle, '<fmt:message key="button.batch.exporter.xls"/>'),
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
				}]
			   }]
        }];
        
       
    	this.callParent();
    }
});