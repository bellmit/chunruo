<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.orderManager.OrderEidtorExpressPanel', {
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
		
		this.expressCodeStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		<c:forEach var="map" varStatus="cvs" items="${allExpressCodeMaps}">
        		{strId: '${map.value.expressCode}', name: '${map.value.companyName}'}<c:if test="${!cvs.last}">,</c:if>
        		</c:forEach>
        	]
        });
    	this.callParent(arguments);
    	this.addItems(true);
    },
    
    addItems : function(isHiddenDelete){
    	var form = Ext.create('Ext.form.Panel', {
    		header: false,
       	 	layout: 'column',  
        	border: false,  
        	items: [{    
            	xtype: 'container',
	            layout: 'hbox',
	            defaultType: 'textfield', 
	            style: 'padding: 2px 2px 2px;',
            	items: [{
	        		width: 190,
	        		labelWidth: 55,
					xtype: 'combobox',
					fieldLabel: '<fmt:message key="order.expressCode" />',
			        displayField: 'name',
			        valueField: 'strId',
			        store: this.expressCodeStore,
			        style: 'padding: 0px 4px',
			        editable: false,
			        allowBlank: false,
			        queryMode: 'local',
			        typeAhead: true,
			        anchor: '99%'  
				},{    
					labelWidth: 55,
		        	xtype:'textfield',
		          	fieldLabel: '<fmt:message key="order.expressNo"/>',
					name: 'expressNo',
		            allowBlank: false,
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
			        hidden:this.isEdit,
			        handler: function(){
			        	this.addItems(false);
			        } 
	        	}]
        	}]
    	});
    	this.add(form);
    },
});
