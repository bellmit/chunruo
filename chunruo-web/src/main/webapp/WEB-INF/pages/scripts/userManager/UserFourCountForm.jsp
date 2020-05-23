<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.userManager.UserFourCountForm', {
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
       	 	layout: 'column',  
        	border: false,  
        	items: [{    
            	xtype: 'container',
	            flex: 1,
	            layout: 'hbox',
	            defaultType: 'textfield', 
	            style: 'padding: 10px 10px 10px;',
            	items: [{    
					labelWidth: 80,
		        	xtype:'textfield',
		          	fieldLabel: '<fmt:message key="store.withdrawa.mobile"/>',
					name: 'mobile',
					id: 'mobile',
		            allowBlank: false,
		            anchor: '99%',
		            width:280 
	        	},{    
					hideLabel: true,  
                	xtype: 'button',
			        iconCls: 'add',
			        scope: this,
			        handler: function(){
			        	 this.sure(); 
			        } 
	        	}]
        	},{    
            	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            defaultType: 'textfield', 
	            style: 'padding: 10px 10px 10px;',
            	items: [{
				    xtype:'datefield',
				    fieldLabel: '<fmt:message key="order.beginTime"/>',
				    labelWidth: 80,
				    format: 'Y-m-d H:i:s',
				    readOnly: false,
				    name: 'beginTime',
				    value:Ext.Date.add(new Date(),Ext.Date.DAY,-1),
				    anchor:'99%',
				    width:280 
				},{
				    xtype:'datefield',
				    fieldLabel: '<fmt:message key="order.endTime"/>',
				    labelWidth: 80,
				    readOnly: false,
				    format: 'Y-m-d H:i:s',
				    name: 'endTime',
				    value:Ext.Date.add(new Date(),Ext.Date.DAY),
				    anchor:'99%',
				    width:280 
				},{    
					labelWidth: 80,
		        	xtype:'textfield',
		          	fieldLabel: '<fmt:message key="user.level1"/>',
					name: 'common',
		            anchor: '99%',
		            width:280 
	        	},{    
					labelWidth: 80,
		        	xtype:'textfield',
		          	fieldLabel: '<fmt:message key="user.level2"/>',
					name: 'declare',
		            anchor: '99%',
		            width:280 
	        	}]
        	}]
    	});
    	this.add(form);
    },
    
    sure : function(combo, record, index){
        var mobile = this.down('textfield[name=mobile]').getValue();
        var beginTime = Ext.util.Format.date(this.down('datefield[name=beginTime]').getValue(), 'Y-m-d H:i:s');
         var endTime = Ext.util.Format.date(this.down('datefield[name=endTime]').getValue(), 'Y-m-d H:i:s');
        var commonCount = this.down('textfield[name=common]');
        var declareCount = this.down('textfield[name=declare]');
    	commonCount.setValue();
    	declareCount.setValue();
    	
    	Ext.Ajax.request({
        	url: '<c:url value="/user/getUserFourCountByMobile.json"/>',
         	method: 'post',
			scope: this,
			params:{mobile: mobile,beginTime:beginTime,endTime:endTime},
          	success: function(response){
        		var responseObject = Ext.JSON.decode(response.responseText);
        		if (responseObject.success == true){
       					try{
       						commonCount.setValue(responseObject.commonCount);
       						declareCount.setValue(responseObject.declareCount);
       					}catch(e){
    					}
        		}else{
	        		showFailMsg(responseObject.message, 4);	
	        	}
			}
     	})
    },
});
