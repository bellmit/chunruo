<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>
Ext.define('MyExt.couponManager.PlayerAchieveFormPanel', {
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
		this.initWidth = this.width;
        
    	this.callParent(arguments);
    	this.loadData();
    },
    
    loadData : function(){
        Ext.Ajax.request({
       		url: '<c:url value="/activity/getNotAchievePlayer.json"/>',
        	method: 'post',
			scope: this,
			async: false,
			params:{activityId: this.activityId},
         	success: function(response){
       			var responseObject = Ext.JSON.decode(response.responseText);
       			if (responseObject.success == true){
       					if(responseObject.playerList != null && responseObject.playerList.length > 0){
	       					try{
	       						for(var i = 0; i < responseObject.playerList.length; i ++){
	       							var playerId = responseObject.playerList[i].playerId;
	       							var playerName = responseObject.playerList[i].playerName;
	       							var achieveNum = responseObject.playerList[i].achieveNum;
	       							this.addItems(false,playerId,playerName,achieveNum,false);
	       						}
	       					}catch(e){
	    					}
	    		       }
       			}else{
       				showFailMsg(responseObject.message, 4);
       			}
			}
    	}, this);                                        
    },
    
     addItems : function(isHiddenDelete,playerId,playerName,achieveNum,isLast){
    	var form = Ext.create('Ext.form.Panel', {
   			xtype: 'container',
            layout: 'hbox',
            defaultType: 'textfield', 
            style: 'padding: 10px 10px 10px;',
            baseCls: 'my-panel-no-border',
           	items: [{
		        labelWidth: 40,
	        	xtype:'textfield',
	          	fieldLabel: '<fmt:message key="activityPlayer.playerId"/>',
				name: 'playerId',
	            allowBlank: false,
	            width: 100,
	            anchor: '99%',
	            editable: false,
	            style: 'padding: 5px 5px 5px;',
	            value:playerId   
			},{    
				labelWidth: 55,
	        	xtype:'textfield',
	          	fieldLabel: '<fmt:message key="activityPlayer.playerName"/>',
				name: 'playerName',
	            allowBlank: false,
	            width: 160,
	            anchor: '99%',
	            style: 'padding: 5px 5px 5px;',
	            editable: false,
	            value:playerName  
        	},{    
				labelWidth: 80,
	        	xtype:'textfield',
	          	fieldLabel: '<fmt:message key="activityPlayer.achieveNum"/>',
				name: 'achieveNum',
	            allowBlank: false,
	            style: 'padding: 5px 5px 5px;',
	            width: 140,
	            anchor: '99%',
	            value:achieveNum  
        	}]
    	});
    	if(close){
    	}
    	this.add(form);
    },
});