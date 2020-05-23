<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.userManager.UserCountForm', {
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
					labelWidth: 80,
		        	xtype:'textfield',
		          	fieldLabel: '<fmt:message key="user.firstDeclareCount"/>',
					name: 'firstDeclareCount',
		            anchor: '99%',
		            width:280 
	        	},{    
					labelWidth: 80,
		        	xtype:'textfield',
		          	fieldLabel: '<fmt:message key="user.firstAgentCount"/>',
					name: 'firstAgentCount',
		            anchor: '99%',
		            width:280 
	        	},{    
					labelWidth: 80,
		        	xtype:'textfield',
		          	fieldLabel: '<fmt:message key="user.secondDeclareCount"/>',
					name: 'secondDeclareCount',
		            anchor: '99%',
		            width:280 
	        	},{    
					labelWidth: 80,
		        	xtype:'textfield',
		          	fieldLabel: '<fmt:message key="user.secondAgentCount"/>',
					name: 'secondAgentCount',
		            anchor: '99%',
		            width:280 
	        	}]
        	}]
    	});
    	this.add(form);
    },
    
    sure : function(combo, record, index){
        var mobile = this.down('textfield[name=mobile]').getValue();
        var firstDeclareCount = this.down('textfield[name=firstDeclareCount]');
        var firstAgentCount = this.down('textfield[name=firstAgentCount]');
        var secondDeclareCount = this.down('textfield[name=secondDeclareCount]');
        var secondAgentCount = this.down('textfield[name=secondAgentCount]');
    	firstDeclareCount.setValue();
    	firstAgentCount.setValue();
    	secondDeclareCount.setValue();
    	secondAgentCount.setValue();
    	
    	Ext.Ajax.request({
        	url: '<c:url value="/user/getUserCountByMobile.json"/>',
         	method: 'post',
			scope: this,
			params:{mobile: mobile},
          	success: function(response){
        		var responseObject = Ext.JSON.decode(response.responseText);
        		if (responseObject.success == true){
       					try{
       						firstDeclareCount.setValue(responseObject.firstDeclareCount);
       						firstAgentCount.setValue(responseObject.firstAgentCount);
       						secondDeclareCount.setValue(responseObject.secondDeclareCount);
       						secondAgentCount.setValue(responseObject.secondAgentCount);
       					}catch(e){
    					}
        		}else{
	        		showFailMsg(responseObject.message, 4);	
	        	}
			}
     	})
    },
});
