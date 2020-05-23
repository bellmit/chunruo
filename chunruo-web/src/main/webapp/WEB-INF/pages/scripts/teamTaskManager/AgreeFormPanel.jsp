<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.teamTaskManager.AgreeFormPanel', {
    extend : 'Ext.form.Panel',
    alias: ['widget.agreeFormPanel'],
 	header: false,
	closable: true,
	columnLines: true,
	animCollapse: true,
	collapsible: true,
    scroll: 'both',
    autoScroll: true,
   	items:[],
	viewConfig: {	
		stripeRows: true,
		enableTextSelection: true
	},
		    
	initComponent : function(config) {
		Ext.apply(this, config);
		
		
        	this.rendererApply= Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: '2', name: '<fmt:message key="user.recordType2"/>'},
        		{id: '1', name: '<fmt:message key="user.recordType1"/>'}
        	]
        }
        );
        
        this.renderer= Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: '2', name: '<fmt:message key="user.payStatus2"/>'},
        		{id: '1', name: '<fmt:message key="user.payStatus1"/>'}
        	]
        }
        );
        
        this.rendererPaymentType= Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: '1', name: '<fmt:message key="user.paymentType1"/>'},
        		{id: '0', name: '<fmt:message key="user.paymentType0"/>'}
        	]
        }
        );
        
    	this.callParent(arguments);
    	this.addItems(true);
    },
    
    
    
    
    addItems : function(isHiddenDelete){
    	var form = Ext.create('Ext.form.Panel', {
    		header: false,
       	 	layout: 'column',  
        	border: false,  
        	items: [{xtype: 'hiddenfield', name: 'isAgree' , value : this.isAgree, allowBlank: true},
        	{xtype: 'hiddenfield', name: 'recordId' , value : this.recordId, allowBlank: true},{    
            	xtype: 'container',
	            style: 'padding: 10px 10px 10px;',
	            flex: 10,
                layout: 'anchor',
            	items: [{
	  			xtype: 'combobox',
				fieldLabel: '<fmt:message key="team.task.user.manager"/>',
				name: 'userId',
		        displayField: 'name',
		        valueField: 'id',
		        queryMode: 'local',
		        hidden:!this.isAgree,
		        store: {
					xtype: 'store',
					autoLoad: true,
					autoDestroy: true,
					sortOnLoad: true,
					remoteSort: true,
					model: 'InitModel',
					proxy: {
						type: 'ajax',
						url: '<c:url value="/teamTask/getCustomerManagerUserInfo.json"/>',
						reader: {
							type : 'json',
							root: 'data'
						
	  					}
					},
					scope: this
				},
		        anchor: '98%'
	       	},
            	{
				    width:360,
				    height:100,
	        		labelWidth: 70,
				    xtype:'textarea',
				    fieldLabel: '<fmt:message key="team.task.record.refuse"/>',
				    style: 'padding: 0px 4px',   
				    anchor:'99%',
				    name: 'remark',
				    value:this.content,
				    hidden:this.isAgree
				}
            	]
        	}]
    	});
    	this.add(form);
    },  
});
