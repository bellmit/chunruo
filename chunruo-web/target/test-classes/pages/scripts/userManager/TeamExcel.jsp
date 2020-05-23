<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.userManager.TeamExcel', {
    extend : 'Ext.form.Panel',
    alias: ['widget.teamForm'],
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
	    
	    this.levelStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
				{id: '2', name: '<fmt:message key="user.export.level2"/>'},
				{id: '3', name: '<fmt:message key="user.export.level3"/>'},
				{id: '4', name: '<fmt:message key="user.export.level4"/>'}
				
        	]
        });
        
	    this.items = [{
        	xtype: 'fieldset',
            bodyPadding: '5 5 0',
            anchor: '99%',
            layout: 'hbox',
            items:[{
                xtype: 'container',
                flex: 1,
                layout: 'anchor',
                items: [{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="user.mobile"/>',
				    labelWidth: 100,
				    readOnly: false,
				    id:'mobile',
				    name: 'mobile',
				    anchor:'97%',
				},{
	  			xtype: 'combobox',
	  			value: '2',
				fieldLabel: '<fmt:message key="system.sendmsg.object"/>',
				labelWidth: 100,
				name: 'level',
		        displayField: 'name',
		        valueField: 'id',
		        queryMode: 'local',
		        store: this.levelStore,
		        id: 'level',
		        anchor: '97%'
	       	}]
			   }]
        }];
        
       
    	this.callParent();
    }
});