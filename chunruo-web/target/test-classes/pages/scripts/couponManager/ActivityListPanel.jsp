<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('Activity', {
	extend: 'Ext.data.Model',
	idProperty: 'activityId',
    fields: [
		{name: 'activityId',		mapping: 'activityId',		type: 'int'},
		{name: 'startTime',			mapping: 'startTime',		type: 'string'},
		{name: 'status',			mapping: 'status',			type: 'int'},
		{name: 'voteTime',			mapping: 'voteTime',		type: 'int'},
		{name: 'activityEndTime',	mapping: 'activityEndTime',	type: 'string'},
		{name: 'createTime',	 	mapping: 'createTime',		type: 'string'},
		{name: 'updateTime',	 	mapping: 'updateTime',		type: 'string'},
    ]
});

Ext.define('MyExt.couponManager.ActivityListPanel', {
	extend : 'Ext.panel.Panel',
    requires : ['Ext.ux.grid.GridHeaderFilters','Ext.ux.grid.Exporter'],
    header: false,
	closable: true,
	columnLines: true,
	animCollapse: true,
	layout: 'border',
   	defaults: {  
    	split: true,    
        collapsible: false
    },
    
    initComponent : function(config) {
		
    	this.store = Ext.create('Ext.data.Store', {
    		pageSize: 50,
	        autoLoad: false,
			autoDestroy: true,
			sortOnLoad: true,
			remoteSort: true,
			model: 'Activity',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/activity/activityList.json"/>',
				reader: {
					type : 'json',
                	root: 'data',
                	totalProperty: 'totalCount'
            	}
			},
			sorters: [{
	            property: 'createTime',
	            direction: 'desc'
	        }]
		});
		
		this.statusStore = Ext.create('Ext.data.Store', {
			autoDestroy: true,
			model: 'InitModel',
			data: [
				{id: '0', name: '<fmt:message key="activity.status.ready"/>'},
				{id: '1', name: '<fmt:message key="activity.status.vote"/>'},
				{id: '2', name: '<fmt:message key="activity.status.playing"/>'},
				{id: '3', name: '<fmt:message key="activity.status.end"/>'},
			]
		});
    
	    this.columns = [
			{text: '<fmt:message key="activity.activityId"/>', dataIndex: 'activityId', width: 75, sortable: true, locked: true, filter: {xtype: 'textfield'}},
			{text: '<fmt:message key="activity.startTime"/>', dataIndex: 'startTime', width: 200, sortable: true, locked: true, filter: {xtype: 'textfield'}},
        	{text: '<fmt:message key="activity.status"/>', dataIndex: 'status', width: 90, locked: true,  align: 'center', sortable : true,
		    	renderer: this.rendererStuts,
	        	filter: {
					xtype: 'combobox',
				    displayField: 'name',
				    valueField: 'id',
				   	store: this.statusStore,
				   	queryMode: 'local',
				    typeAhead: true
				}
			},
			{text: '<fmt:message key="activity.activityEndTime"/>', dataIndex: 'activityEndTime', align: 'center', width: 200, sortable: true, locked: true, filter: {xtype: 'textfield'}},
        	{text: '<fmt:message key="activity.voteTime"/>', dataIndex: 'voteTime', width: 120, sortable: true, locked: true, filter: {xtype: 'textfield'}},
        	{text: '<fmt:message key="keywords.createTime"/>', dataIndex: 'createTime', width: 150, sortable : true, align: 'center',filter: {xtype: 'textfield'}},
        	{text: '<fmt:message key="keywords.updateTime"/>', dataIndex: 'updateTime', width: 150, sortable : true,filter: {xtype: 'textfield'}},
        ];
        
         this.tbar = [{
	    	text: '<fmt:message key="button.refresh"/>', 
	        iconCls: 'refresh', 	
	       	handler: function(){this.store.reload();}, 
	       	scope: this
	  	},'-',{
        	text: '<fmt:message key="activity.add"/>', 
        	iconCls: 'add', 	
        	handler: this.saveActivity,
        	scope: this
        },'-',{
        	text: '<fmt:message key="activityPlayer.add"/>', 
        	iconCls: 'add', 	
        	handler: this.addActivityPlayer,
        	scope: this
        },'-',{
        	text: '<fmt:message key="activityPlayer.achieveNum"/>', 
            iconCls: 'Chartpieadd',
        	handler: this.edit, 
        	scope: this
        },'-',{
        	text: '<fmt:message key="activity.start.vote"/>', 
        	iconCls: 'enable', 	
        	handler: this.activityVote,
        	scope: this
        },'-',{
        	text: '<fmt:message key="playing.start"/>', 
        	iconCls: 'enable', 	
        	handler: this.activityStart,
        	scope: this
        },'-',{
        	text: '<fmt:message key="activity.status.end"/>', 
        	iconCls: 'delete', 	
        	handler: this.activityEnd,
        	scope: this
        }];
        
        this.pagingToolbar = new Ext.PagingToolbar({
        	pageSize: 50,
			store: this.store,
			autoheigth: true,
			displayInfo: true,
			displayMsg: '<fmt:message key="ajax.record"/>',
			emptyMsg: '<fmt:message key="ajax.no.record"/>',
			scope: this,
			items: ['-',{ 
				xtype: 'numberfield', 
				width: 120, 
				labelWidth: 65,
				value: 50, 
				minValue: 1, 
				fieldLabel: '<fmt:message key="ajax.record.size"/>',
                allowBlank: false,
               	scope: this,
                listeners:{
                	scope: this,
               		change: function (field, newValue, oldValue) {
                    	var number = parseInt(newValue);
                        if (isNaN(number) || !number || number < 1) {
                        	number = 50;
                           	Field.setValue(number);
                        }
                       	this.store.pageSize = number;
                       	this.store.load();
                   	}
               	}
        	}]	
		});
        
        this.productList = Ext.create('Ext.grid.GridPanel', {
			region: 'center',
			header: false,
			autoScroll: true,   
			closable: true,
			selType: 'checkboxmodel',
			multiSelect: true,
			columnLines: true,
			animCollapse: false,
	        enableLocking: true,
		    columns: this.columns,
		    store: this.store,
		    bbar: this.pagingToolbar,
		    plugins: ['gridHeaderFilters','gridexporter'],
		    viewConfig: {
		        stripeRows: true,
		        enableTextSelection: true
		    }
	    }); 
        
        this.gsm = this.productList.getSelectionModel();
    	this.items = [this.productList];	
		this.callParent(arguments);
	    
	   	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.productList.filters)
			});
	    }, this);
	    this.store.load();   
	    
	    this.productList.on('itemdblclick', function(view, record, item, index, e, eOpts) {
	    	this.east.transferData(this.east, record, this.body.dom.clientWidth);
	    	this.east.show();
	    }, this); 
    },
        
    saveActivity : function(){
    	var activityFormPanel = Ext.create('MyExt.couponManager.ActivityFormPanel', {
			id: 'activityFormPanel@' + this.id,
    		viewer: this.viewer,
   	 	});
    	
    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
			var startTime='';
			var voteTime='';
			activityFormPanel.items.each(function(form) {
	        		if(!form.isValid()){
	        			isCheckSucc = false;
	        		}else{
	        			 startTime= Ext.getCmp('startTime').getValue();
	        			 voteTime= Ext.getCmp('voteTime').getValue();
	        		}
				}, this);
				Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
					if(e == 'yes'){
	             		Ext.Ajax.request({
	                 		waitMsg: 'Loading...',
	                 		url: '<c:url value="/activity/saveActivity.json"/>',
	               			scope: this,
	               			params:{startTime: startTime,voteTime: voteTime},
	               			success: function(response) {
	                   			var responseObject = Ext.JSON.decode(response.responseText);
	                   			if(responseObject.error == false){
	                  				showSuccMsg(responseObject.message);
	                  				this.store.load();
	                  				popWin.close();
								}else{
									showFailMsg(responseObject.message, 4);
								}
	               			}
	   					})
	   				}
	   			}, this)
		 		
			},
			scope: this
		},{
			text: '<fmt:message key="button.cancel"/>',
			handler : function(){popWin.close();},
			scope: this
		}];
      	openWin('<fmt:message key="activity.add"/>', activityFormPanel, buttons, 310, 170);
    },	
        
    addActivityPlayer : function(){
    	var rowsData = [];
		var records = this.gsm.getSelection();

		if(records.length == 0 ){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}
		if(records.length > 1 ){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="user.approve.records"/>');
			return;
		}
    	
    	var activityPlayerFormPanel = Ext.create('MyExt.couponManager.ActivityPlayerFormPanel', {
			id: 'activityPlayerFormPanel@' + this.id,
    		viewer: this.viewer,
   	 	});
    	
    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
				var isCheckSucc = true;
				var expressMap = [];
		    	activityPlayerFormPanel.items.each(function(form) {
	        		if(!form.isValid()){
	        			isCheckSucc = false;
	        		}else{
	        			var playerName = form.down('[name=playerName]').getValue();
	        			var height = form.down('[name=height]').getValue();
	        			var weight = form.down('[name=weight]').getValue();
	        			expressMap.push({key: playerName, value: height+','+weight});
	        		}
				}, this);
				
				if(!isCheckSucc){
					showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
					return;
				}	
				Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
					if(e == 'yes'){
	             		Ext.Ajax.request({
	                 		waitMsg: 'Loading...',
	                 		url: '<c:url value="/activity/addActivityPlayer.json"/>',
	               			scope: this,
	               			params:{activityId : records[0].data.activityId, expressMap: Ext.JSON.encode(expressMap)},
	               			success: function(response) {
	                   			var responseObject = Ext.JSON.decode(response.responseText);
	                   			if(responseObject.error == false){
	                  				showSuccMsg(responseObject.message);
	                  				this.store.loadPage(1);
                        			this.gsm.deselectAll();
	                  				popWin.close();
								}else{
									showFailMsg(responseObject.message, 4);
								}
	               			}
	   					})
	   				}
	   			}, this)
		 		
			},
			scope: this
		},{
			text: '<fmt:message key="button.cancel"/>',
			handler : function(){popWin.close();},
			scope: this
		}];
      	openWin('<fmt:message key="activityPlayer.add"/>', activityPlayerFormPanel, buttons, 520, 320);
    },	    
        
     edit : function(){
        var rowsData = [];
		var records = this.gsm.getSelection();

		if(records.length == 0 ){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}
		if(records.length > 1 ){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="user.approve.records"/>');
			return;
		}
		
		var activityId=records[0].data.activityId;
		var playerAchieveFormPanel = Ext.create('MyExt.couponManager.PlayerAchieveFormPanel', {
			id: 'playerAchieveFormPanel@' + this.id,
    		viewer: this.viewer,
    		activityId:activityId,
    		read: true,
   	 	});
         
    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
				var isCheckSucc = true;
	            var expressMap = [];
	            
		    	playerAchieveFormPanel.items.each(function(form) {
	        		if(!form.isValid()){
	        			isCheckSucc = false;
	        		}else{
	        			  var playerId = form.down('[name=playerId]').getValue();
	        			  var achieveNum = form.down('[name=achieveNum]').getValue();
	        			  expressMap.push({key: playerId, value: achieveNum});
	        		}
				}, this);
				if(!isCheckSucc){
					showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
					return;
				}	
				Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
					if(e == 'yes'){
				     	Ext.Ajax.request({
				        	url: '<c:url value="/activity/setPlayerAchieveNum.json"/>',
				         	method: 'post',
							scope: this,
							params:{activityId : activityId,expressMap: Ext.JSON.encode(expressMap)},
				          	success: function(response){
		          				var responseObject = Ext.JSON.decode(response.responseText);
		          				if(responseObject.success == true){
		                       		showSuccMsg(responseObject.message);
		                            this.store.loadPage(1);
                        			this.gsm.deselectAll();
		                       		popWin.close();
								}else{
									showFailMsg(responseObject.message, 4);
								}
							}
				     	})
				     }
				}, this)
			},
			scope: this
		},{
			text: '<fmt:message key="button.cancel"/>',
			handler : function(){popWin.close();},
			scope: this
		}];
      	openWin(Ext.String.format('<fmt:message key="activityPlayer.achieveNum"/>'), playerAchieveFormPanel, buttons, 500, 290);
    },
    
    activityVote : function(){
   		var records = this.gsm.getSelection();
   		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}else if(records.length > 1){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.only.one.record"/>');
			return;
		}
		
	    Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="enable.confirm"/>', function(e){
			if(e == 'yes'){
	        	Ext.Ajax.request({
		        	url: '<c:url value="/activity/setActivityStatus.json"/>',
		         	method: 'post',
					scope: this,
					params:{activityId : records[0].data.activityId, status : 1},
		          	success: function(xresponse){
				    	var xresponseObject = Ext.JSON.decode(xresponse.responseText);
         				if (xresponseObject.success == true){
         					showSuccMsg(xresponseObject.message);
         					this.store.loadPage(1);
                        	this.gsm.deselectAll();
         				}else{
         					showFailMsg(xresponseObject.message, 4);
         				}
					}
		     	})
	        }
		}, this)
   	},
   	
   	activityStart : function(){
   		var records = this.gsm.getSelection();
   		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}else if(records.length > 1){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.only.one.record"/>');
			return;
		}
		
	    Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="enable.confirm"/>', function(e){
			if(e == 'yes'){
	        	Ext.Ajax.request({
		        	url: '<c:url value="/activity/setActivityStatus.json"/>',
		         	method: 'post',
					scope: this,
					params:{activityId : records[0].data.activityId, status : 2},
		          	success: function(xresponse){
				    	var xresponseObject = Ext.JSON.decode(xresponse.responseText);
         				if (xresponseObject.success == true){
         					showSuccMsg(xresponseObject.message);
         					this.store.loadPage(1);
                    		this.gsm.deselectAll();
         				}else{
         					showFailMsg(xresponseObject.message, 4);
         				}
					}
		     	})
	        }
		}, this)
   	},
   	
   	activityEnd : function(){
   		var records = this.gsm.getSelection();
   		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}else if(records.length > 1){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.only.one.record"/>');
			return;
		}
		
	    Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="enable.confirm"/>', function(e){
			if(e == 'yes'){
	        	Ext.Ajax.request({
		        	url: '<c:url value="/activity/setActivityStatus.json"/>',
		         	method: 'post',
					scope: this,
					params:{activityId : records[0].data.activityId, status : 3},
		          	success: function(xresponse){
				    	var xresponseObject = Ext.JSON.decode(xresponse.responseText);
         				if (xresponseObject.success == true){
         					showSuccMsg(xresponseObject.message);
         					this.store.loadPage(1);
                        	this.gsm.deselectAll();
         				}else{
         					showFailMsg(xresponseObject.message, 4);
         				}
					}
		     	})
	        }
		}, this)
   	},
    
    rendererStuts : function(val){
		if(val == 0) {
            return '<b><fmt:message key="activity.status.ready"/></b>';
        }else if(val == 1){
            return '<fmt:message key="activity.status.vote"/>';
        }else if(val == 2){
            return '<fmt:message key="activity.status.playing"/>';
        }else{
            return '<fmt:message key="activity.status.end"/>';
        }
	},    
});        
        
        
        
        
        