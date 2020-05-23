<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.couponManager.ActivityPlayerFormPanel', {
    extend : 'Ext.Panel',
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
		
    	this.callParent(arguments);
    	this.addItems(true);
    },
    
    addItems : function(isHiddenDelete){
    	var form = Ext.create('Ext.form.Panel', {
    		header: false,
    		xtype: 'container',
       	 	layout: 'hbox',  
        	border: false,  
        	baseCls: 'my-panel-no-border',
        	items: [{    
            	xtype: 'container',
	            layout: 'hbox',
	            defaultType: 'textfield', 
	            style: 'padding: 10px 10px 10px;',
            	items: [{
			        labelWidth: 55,
		        	xtype:'textfield',
		          	fieldLabel: '<fmt:message key="activityPlayer.playerName"/>',
					name: 'playerName',
		            allowBlank: false,
		            width: 150,
		            style: 'padding: 2px 2px 2px;',
		            anchor: '99%'   
				},{    
					labelWidth: 80,
		        	xtype:'textfield',
		          	fieldLabel: '<fmt:message key="activityPlayer.height"/>',
					name: 'height',
		            allowBlank: false,
		            style: 'padding: 2px 2px 2px;',
		            width: 140,
		            anchor: '99%'  
	        	},{    
					labelWidth: 80,                                              
		        	xtype:'textfield',
		          	fieldLabel: '<fmt:message key="activityPlayer.weight"/>',
					name: 'weight',
		            allowBlank: false,
		            style: 'padding: 2px 2px 2px;',
		            width: 140,
		            anchor: '99%'  
	        	},{    
					hideLabel: true,  
                	xtype: 'button',
			        iconCls: 'delete',
			        scope: this,
			        hidden: isHiddenDelete,
			        handler: function(){
			        	this.remove(form, false);
			        	form.hide();  
			        } 
	        	},{    
					hideLabel: true,  
                	xtype: 'button',
			        iconCls: 'add',
			        scope: this,
			        handler: function(){
			        	this.addItems(false);
			        } 
	        	}]
        	}]
    	});
    	this.add(form);
    },
});
           	
