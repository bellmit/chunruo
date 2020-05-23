<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.productManager.FxChannelFormPanel', {
    extend : 'Ext.form.Panel',
 	header: false,
 	labelAlign: 'right',
 	bodyPadding: '5 5 0',
    defaultType: 'textfield',
    autoScroll: true,
    viewConfig: {
        stripeRows: true,
        enableTextSelection: true
    },
    
	initComponent : function(config) {
		Ext.apply(this, config);
		
	    if(this.isEditChannel){
		     this.items = [{
				xtype:'textfield',
				labelWidth: 60,
				fieldLabel: '<fmt:message key="fx.channel.channelName"/>',
				name: 'channelName',
				value:this.channelName,
				anchor:'97%'
			},{
				xtype:'numberfield',
				labelWidth: 60,
				fieldLabel: '<fmt:message key="fx.channel.sort"/>',
				name: 'sort',
				anchor:'97%',
				value:this.channelSort
			}];
	    }else{
		     this.items = [{
				xtype: 'hiddenfield', 
				name: 'channelId', 
			},{
				xtype:'textfield',
				labelWidth: 60,
				fieldLabel: '<fmt:message key="fx.channel.channelName"/>',
				name: 'channelName',
				anchor:'97%'
			},{
				xtype:'numberfield',
				labelWidth: 60,
				fieldLabel: '<fmt:message key="fx.channel.sort"/>',
				name: 'sort',
				anchor:'97%'
			}];
	    
	    };
	  
		this.reader = new Ext.data.JsonReader({
			totalProperty: 'total',
			model: 'FxChannel',
        	root: 'data'
		});
    	this.callParent();
    }
});