<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>
Ext.define('MyExt.couponManager.ActivityManagerFormPanel', {
    extend : 'Ext.form.Panel',
    alias: ['widget.activityManagerForm'],
 	header: false,
	closable: true,
	columnLines: true,
	animCollapse: true,
	collapsible: true,
    scroll: 'both',
    autoScroll: true,
    isEditor: false,
   	items:[],
	viewConfig: {	
		stripeRows: true,
		enableTextSelection: true
	},
		    
	initComponent : function(config) {
		Ext.apply(this, config);
		
		if(this.isEditor){
		this.buttons = [{
				text: '<fmt:message key="button.save"/>', 
				style: 'font-size: 14px;background: rgba(22, 155, 213, 1) none repeat scroll 0 0;border-color: rgba(22, 155, 213, 1);',
				scope: this,  
	        	handler: this.updateActivity
	    	}];
		}
    	this.callParent(arguments);
    	this.addItems(true);
    },
    
     updateActivity : function(){
    	if(this.form.isValid()){
			Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
				if(e == 'yes'){
             		this.form.submit({
                 		waitMsg: 'Loading...',
                 		url: '<c:url value="/activity/updateActivity.json"/>',
               			scope: this,
               			method : 'post', 
               			params:{
               			},
               			success: function(form, action) {
                   			var responseObject = Ext.JSON.decode(action.response.responseText);
                   			if(responseObject.success == true){
                  				showSuccMsg(responseObject.message);
                  				this.tabPanel.loadData();
                  				Ext.StoreMgr.get('activitystore').reload();
							}else{
								showFailMsg(responseObject.message, 4);
							}
               			}
   					})
   				}
   			}, this)
 		}
    },
    
    addItems : function(isHiddenDelete){
    	var form = Ext.create('Ext.form.Panel', {
    		header: false,
       	 	layout: 'column',  
        	border: false,  
        	items: [{    
            	xtype: 'container',
	            style: 'padding: 10px 10px 10px;',
	            flex: 10,
                layout: 'anchor',
            	items: [{
			          xtype: 'hiddenfield', 
			          name: 'activityId', 
			          value: this.activityId
		               },{
						fieldLabel: '<fmt:message key="activity.isEnable" />',
	           			name: 'isEnable',
	           			labelWidth: 100,
	           			xtype: 'checkbox',
	          			anchor: '99%'
					},{
	        		labelWidth: 100,
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="activity.statusName"/>',
				    anchor:'99%',
				    name: 'statusName'
				   },{
				    xtype:'datefield',
				    fieldLabel: '<fmt:message key="activity.startTime"/>',
				    labelWidth: 100,
				    format: 'Y-m-d H:i:s',
				    readOnly: false,
				    name: 'startTime',
				    anchor:'99%'
				},{
				    xtype:'datefield',
				    fieldLabel: '<fmt:message key="activity.endTime"/>',
				    labelWidth: 100,
				    format: 'Y-m-d H:i:s',
				    readOnly: false,
				    name: 'endTime',
				    anchor:'99%'
				   }]
        	}]
    	});
    	this.add(form);
    },  
});