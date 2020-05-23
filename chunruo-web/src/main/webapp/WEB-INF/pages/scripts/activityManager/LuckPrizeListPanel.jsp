<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('Luck', {
	extend: 'Ext.data.Model',
	idProperty: 'luckId',
    fields: [
		{name: 'luckId',		mapping: 'luckId',		type: 'int'},
		{name: 'name',			mapping: 'name',		type: 'string'},
		{name: 'image',		    mapping: 'image',		type: 'string'},
		{name: 'number',		mapping: 'number',		type: 'int'},
		{name: 'isThanks',		mapping: 'isThanks',	type: 'bool'},
		{name: 'weight',		mapping: 'weight',		type: 'int'},
		{name: 'createTime',	mapping: 'createTime',	type: 'string'},
		{name: 'updateTime',	mapping: 'updateTime',	type: 'string'},
    ]
});

Ext.define('MyExt.activityManager.LuckPrizeListPanel', {
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
    	    id: 'store',
    		pageSize: 50,
	        autoLoad: false,
			autoDestroy: true,
			sortOnLoad: true,
			remoteSort: true,
			model: 'Luck',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/activity/luckList.json"/>',
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
			{text: '<fmt:message key="activity.info.luckId"/>', dataIndex: 'luckId', width: 75, sortable: true, locked: true, filter: {xtype: 'textfield'}},
           	{text: '<fmt:message key="activity.luck.name"/>', dataIndex: 'name', width: 75, sortable: true, locked: true, filter: {xtype: 'textfield'}},
            {	text: '<fmt:message key="acitivity.image1"/>',
                menuDisabled: true,
                sortable: false,
                xtype: 'actioncolumn',
                align: 'center',
                width: 70,
                higth: 80,
                dataIndex: 'image',
                renderer: function(value, metadata, record) {
        			return Ext.String.format('<img height="60" width="60" src="{0}"></img>', value);
    			}
            },
           {text: '<fmt:message key="activity.luck.isThanks"/>', dataIndex: 'isThanks', locked: true, width: 65, sortable : true,
        		align: 'center',
        		renderer: function(value, meta, record) {    
			       	if(value == false) {
			            return '<span style="color:green;"><fmt:message key="button.no"/></span>';
			        }else{
			            return '<span style="color:red;"><b><fmt:message key="button.yes"/></b></span>';
			        }  
			   	},
        		filter: {
					xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: Ext.create('Ext.data.Store', {
						autoDestroy: true,
						model: 'InitModel',
						data: [
							{id: '0', name: '<fmt:message key="button.yes"/>'},
							{id: '1', name: '<fmt:message key="button.no"/>'},
						]
					}),
			        queryMode: 'local',
			        typeAhead: true
				}
        	},            {text: '<fmt:message key="activity.luck.number"/>', dataIndex: 'number', width: 200, sortable: true, locked: true, filter: {xtype: 'textfield'}},
            {text: '<fmt:message key="activity.luck.weight"/>', dataIndex: 'weight', width: 200, sortable: true, locked: true, filter: {xtype: 'textfield'}},
            {text: '<fmt:message key="keywords.info.createTime"/>', dataIndex: 'createTime', width: 150, sortable : true, align: 'center',filter: {xtype: 'textfield'}},
        	{text: '<fmt:message key="keywords.info.updateTime"/>', dataIndex: 'updateTime', width: 150, sortable : true,filter: {xtype: 'textfield'}},
        ];
        
         this.tbar = [{
	    	text: '<fmt:message key="button.refresh"/>', 
	        iconCls: 'refresh', 	
	       	handler: function(){this.store.reload();}, 
	       	scope: this
	  	},'-',{
        	text: '<fmt:message key="activity.info.add"/>', 
        	iconCls: 'add', 	
        	handler: this.saveLuck,
        	scope: this
        },{
        	text: '<fmt:message key="activity.info.edit"/>', 
        	iconCls: 'Chartpieadd', 	
        	handler: this.editLuck,
        	scope: this
        }, {
        	text: '<fmt:message key="activity.info.delete"/>', 
        	iconCls: 'delete', 	
        	handler: this.deleteLuck, 
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
        
    saveLuck : function(){
    var rowsData = [];		
    	var infoFormPanel = Ext.create('MyExt.activityManager.LuckPrizeFormPanel', {
			id: 'infoFormPanel@InfoFormPanel',
    		viewer: this.viewer,
   	 	});
    	
    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
			    var rowsDatas = [];    
		       	infoFormPanel.down('imagepanel').store.each(function(record) {
		       		record.data.input_file = null;
		            rowsDatas.push(record.data);    
		      	}, this);
			if(infoFormPanel.form.isValid()){
				Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
					if(e == 'yes'){
	             		infoFormPanel.form.submit({
			                    waitMsg: 'Loading...',
			                    url: '<c:url value="/activity/saveLuck.json"/>',
			                    scope: this,
			                    params:{level: 2,recodeGridJson: Ext.JSON.encode(rowsDatas)},
			                    success: function(form, action) {
			                        var responseObject = Ext.JSON.decode(action.response.responseText);
			                    	if(responseObject.error == false){
			                       		showSuccMsg(responseObject.message);
			                        	this.store.loadPage(1);
										popWin.close();
									}else{
										showFailMsg(responseObject.message, 4);
									}
			                    },
			                    failure: function(form, action) {
				                    var responseObject = Ext.JSON.decode(action.response.responseText);
				                    showFailMsg(responseObject.msg, 4);
			                    }
			        		})
	   				}
	   			}, this)
		 		}
			},
			scope: this
		},{
			text: '<fmt:message key="button.cancel"/>',
			handler : function(){popWin.close();},
			scope: this
		}];
      	openWin('<fmt:message key="activity.add"/>', infoFormPanel, buttons, 310, 300);
    },	
        
        
      editLuck : function(){
        var luckId = 0;	
        var name='';	
        var number =0;
        var weight =0;
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	

		luckId = records[0].data.luckId;	
		name = records[0].data.name;
		number = records[0].data.number;
        weight = records[0].data.weight;
    	var infoFormPanel = Ext.create('MyExt.activityManager.LuckPrizeFormPanel', {
			id: 'infoFormPanel@InfoFormPanel',
    		viewer: this.viewer,
    		luckId :luckId,
    		name : name,
    		number : number,
    		weight: weight
   	 	});
    	
    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
			var rowsDatas = [];    
		       	infoFormPanel.down('imagepanel').store.each(function(record) {
		       		record.data.input_file = null;
		            rowsDatas.push(record.data);    
		      	}, this);
			if(infoFormPanel.form.isValid()){
				Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
					if(e == 'yes'){
	             		infoFormPanel.form.submit({
			                    waitMsg: 'Loading...',
			                    url: '<c:url value="/activity/saveLuck.json"/>',
			                    scope: this,
			                    params:{level: 2,recodeGridJson: Ext.JSON.encode(rowsDatas)},
			                    success: function(form, action) {
			                        var responseObject = Ext.JSON.decode(action.response.responseText);
			                    	if(responseObject.error == false){
			                       		showSuccMsg(responseObject.message);
			                        	this.store.loadPage(1);
                        				this.gsm.deselectAll();
										popWin.close();
									}else{
										showFailMsg(responseObject.message, 4);
									}
			                    },
			                    failure: function(form, action) {
				                    var responseObject = Ext.JSON.decode(action.response.responseText);
				                    showFailMsg(responseObject.msg, 4);
			                    }
			        		})
	   				}
	   			}, this)
		 		}
			},
			scope: this
		},{
			text: '<fmt:message key="button.cancel"/>',
			handler : function(){popWin.close();},
			scope: this
		}];
      	openWin('<fmt:message key="activity.add"/>', infoFormPanel, buttons, 310, 300);
    },  
      
       deleteLuck : function() {
        
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	

		for(var i = 0; i < records.length; i++){	
			rowsData.push(records[i].data.luckId);	
		}
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/activity/deleteLuck.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData)},
		          	success: function(response){
          				var responseObject = Ext.JSON.decode(response.responseText);
                        if(responseObject.success == true){
                       		showSuccMsg(responseObject.message);
                        	this.store.loadPage(1);
                        	this.gsm.deselectAll();
						}else{
							showFailMsg(responseObject.message, 4);
						}
					}
		     	})
	     	}
	 	}, this)  
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
        
        
        
        
        