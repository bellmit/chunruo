<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>


Ext.define('Player', {
	extend: 'Ext.data.Model',
	idProperty: 'playerId',
    fields: [
		{name: 'playerId',			mapping: 'playerId',		       type: 'int'},
		{name: 'userId',		    mapping: 'userId',		           type: 'int'},
		{name: 'playerName',	    mapping: 'playerName',             type: 'string'},
		{name: 'playerMobile',	    mapping: 'playerMobile',		   type: 'string'},
		{name: 'address',	 	    mapping: 'address',                type: 'string'},
		{name: 'content',	 	    mapping: 'content',                type: 'string'},
		{name: 'status',	 	    mapping: 'status',                 type: 'int'},
		{name: 'score',	 	        mapping: 'score',                  type: 'int'},
		{name: 'reason',	     	mapping: 'reason',                 type: 'string'},
		{name: 'level',	 	        mapping: 'level',                  type: 'int'},
		{name: 'grade',	 	        mapping: 'grade',                 type: 'string'},
		{name: 'number',	 	    mapping: 'number',                 type: 'string'},
		{name: 'image1',	 	    mapping: 'image1',                 type: 'string'},
		{name: 'image2',	 	    mapping: 'image2',                 type: 'string'},
		{name: 'image3',	 	    mapping: 'image3',                 type: 'string'},
		{name: 'image4',	 	    mapping: 'image4',                 type: 'string'},
		{name: 'createTime',	 	mapping: 'createTime',		       type: 'string'},
		{name: 'updateTime',	 	mapping: 'updateTime',		       type: 'string'},
    ]
});

Ext.define('MyExt.activityManager.PlayerWaitListPanel', {
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
	    Ext.apply(this, config);
		
		var now = new Date();
   		var expiry = new Date(now.getTime() + 10 * 60 * 1000);
   		Ext.util.Cookies.set('isCheck','1',expiry);
		
    	this.store = Ext.create('Ext.data.Store', {
    	    id:'waitStore',
    		pageSize: 50,
	        autoLoad: false,
			autoDestroy: true,
			sortOnLoad: true,
			remoteSort: true,
			model: 'Player',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/activity/playerList.json?status=0"/>',
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
		
		this.booleanStore = Ext.create('Ext.data.Store', {
			autoDestroy: true,
			model: 'InitModel',
			data: [
				{id: '1', name: '<fmt:message key="button.yes"/>'},
				{id: '0', name: '<fmt:message key="button.no"/>'},
			]
		});
		
		this.rendererlevelStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: 3, name: '<fmt:message key="user.level3"/>'},
        		{id: 2, name: '<fmt:message key="user.level2"/>'},
        		{id: 1, name: '<fmt:message key="user.level1"/>'},
        	]
        });
        
        this.couponTypeStore = Ext.create('Ext.data.Store', {
			autoDestroy: true,
			model: 'InitModel',
			data: [
				{id: '1', name: '<fmt:message key="coupon.couponType1"/>'},
				{id: '2', name: '<fmt:message key="coupon.couponType2"/>'},
			]
		});
		
        this.receiveTypeStore= Ext.create('Ext.data.Store', {
			autoDestroy: true,
			model: 'InitModel',
			data: [
			    {id: '0', name: '<fmt:message key="coupon.receiveType0"/>'},
				{id: '1', name: '<fmt:message key="coupon.receiveType1"/>'},
				{id: '2', name: '<fmt:message key="coupon.receiveType2"/>'},
			]
		});
        
         this.attributeStore= Ext.create('Ext.data.Store', {
			autoDestroy: true,
			model: 'InitModel',
			data: [
			    {id: '0', name: '<fmt:message key="activity.status0"/>'},
				{id: '1', name: '<fmt:message key="activity.status1"/>'},
				{id: '2', name: '<fmt:message key="activity.status2"/>'},
			]
		});
		
		this.isEnableStore= Ext.create('Ext.data.Store', {
			autoDestroy: true,
			model: 'InitModel',
			data: [
        		{id: 1, name: '<fmt:message key="button.yes"/>'},
        		{id: 0, name: '<fmt:message key="button.no"/>'}
        	]
		});
		
		this.columns = [
			{text: '<fmt:message key="acitivity.playerId"/>', dataIndex: 'playerId', width: 30, sortable: true, locked: true, filter: {xtype: 'textfield'}},
			{text: '<fmt:message key="acitivity.userId"/>', dataIndex: 'userId', width: 150, sortable: true, locked: true, filter: {xtype: 'textfield'}},
        	{text: '<fmt:message key="acitivity.playerName"/>', dataIndex: 'playerName', width: 80, sortable: true, locked: true, filter: {xtype: 'textfield'}},
        	{text: '<fmt:message key="acitivity.playerMobile"/>', dataIndex: 'playerMobile', width: 105, sortable: true, locked: true, filter: {xtype: 'textfield'}}, 
            {text: '<fmt:message key="acitivity.level"/>', dataIndex: 'level', width: 150, sortable : true, filter: {xtype: 'textfield'}}, 
        	{text: '<fmt:message key="acitivity.number"/>', dataIndex: 'number', width: 150, sortable : true, filter: {xtype: 'textfield'}}, 
            {text: '<fmt:message key="acitivity.score"/>', dataIndex: 'score', width: 150, sortable : true, filter: {xtype: 'textfield'}}, 
            {text: '<fmt:message key="acitivity.grade"/>', dataIndex: 'grade', width: 150, sortable : true, filter: {xtype: 'textfield'}},
            {
            	text: '<fmt:message key="acitivity.image1"/>',
                menuDisabled: true,
                sortable: false,
                xtype: 'actioncolumn',
                align: 'center',
                width: 140,
                higth: 80,
                locked: true,
                dataIndex: 'image1',
                renderer: function(value, metadata, record) {
        			return Ext.String.format('<img height="60" width="60" src="{0}"></img>', value);
    			}
            },
        	{text: '<fmt:message key="acitivity.status"/>', dataIndex: 'status', width: 77, sortable : true,
        		renderer :this.attributeRenderer,
        		filter: {
					xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.attributeStore,
			        queryMode: 'local',
			        typeAhead: true
				}
        	}, 
        	{text: '<fmt:message key="acitivity.address"/>', dataIndex: 'address', width: 150, sortable : true, filter: {xtype: 'textfield'}}, 
        	{text: '<fmt:message key="acitivity.content"/>', dataIndex: 'content', width: 300, sortable : true, filter: {xtype: 'textfield'}}, 
        	{text: '<fmt:message key="acitivity.reason"/>', dataIndex: 'reason', width: 150, sortable : true, filter: {xtype: 'textfield'}}, 
			{text: '<fmt:message key="acitivity.createTime"/>', dataIndex: 'createTime', width: 120, sortable : true, align: 'center',filter: {xtype: 'textfield'}},
        	{text: '<fmt:message key="acitivity.updateTime"/>', dataIndex: 'updateTime', width: 120, sortable : true,filter: {xtype: 'textfield'}},
        ];
        
        this.tbar = [{
	    	text: '<fmt:message key="button.refresh"/>', 
	        iconCls: 'refresh', 	
	       	handler: function(){this.store.reload();}, 
	       	scope: this
	  	},'-', {
        	text: '<fmt:message key="activity.agree"/>', 
        	iconCls: 'enable', 	
        	handler: this.agree, 
        	scope: this
        }, {
        	text: '<fmt:message key="activity.unagree"/>', 
        	iconCls: 'delete', 	
        	handler: this.unagree, 
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
	    
	    this.east =  Ext.create('MyExt.activityManager.PlayerTabPanel', {
        	productList: this.productList,
		 	viewer: this.viewer,
		 	layout: 'card',
	        border: false,
	       	region: 'east',
	        width: 550,
	        split: true,
	        header: false,
	        hidden: true
        });
    	
    	
    	this.gsm = this.productList.getSelectionModel();
    	this.items = [this.productList, this.east];	
		this.east.hide();
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
    
     agree : function() {
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	

		for(var i = 0; i < records.length; i++){	
			rowsData.push(records[i].data.playerId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/activity/agree.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData),status : 2},
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
    
    
    unagree : function() {
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	

		for(var i = 0; i < records.length; i++){	
			rowsData.push(records[i].data.playerId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/activity/agree.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData),status : 1},
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
    
    
    sendStatus : function(){
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}else if(records.length > 1){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="coupon.sendStatus.bigCount"/>');
			return;
		}	
		
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.applyId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="user.applyAgent.nonapproveConfirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/coupon/setSendStatus.json"/>',
		         	method: 'post',
					scope: this,
					params:{couponId: records[0].data.couponId},
		          	success: function(response){
          				var responseObject = Ext.JSON.decode(response.responseText);
          				if (responseObject.success == true){
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
   	
   	sendCouponByLevel: function(){
   		var rowsData = [];
		var records = this.gsm.getSelection();
		if(records.length == 0 ){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}else if(records.length > 1 ){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="user.approve.records"/>');
			return;
		}
	
    	var sendByLevelFormPanel = Ext.create('MyExt.couponManager.SendByLevelFormPanel', {
			id: 'sendByLevelFormPanel@' + this.id,
    		viewer: this.viewer,	
   	 	});
    	
    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
				var isCheckSucc = false;
		    	sendByLevelFormPanel.items.each(function(form) {
	        		if(form.isValid()){
	        			isCheckSucc = true;	
	        			var items = sendByLevelFormPanel.down('checkboxgroup[name=level]').items;   
	            		for (var i = 0; i < items.length; i++){    
	                		if (items.get(i).checked){    
	                   			rowsData.push(items.get(i).inputValue);                    
	                		}    
	            		}
	        		}
				}, this);
				
				if(!isCheckSucc){
					showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
					return;
				}	
			
				Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
					if(e == 'yes'){
						popWin.close();
						Ext.getBody().mask("please wait ..."); 
						Ext.Ajax.request({
				        	url: '<c:url value="/coupon/setSendStatus.json"/>',
				         	method: 'post',
							scope: this,
							params:{couponId: records[0].data.couponId, idListGridJson: Ext.JSON.encode(rowsData)},
				          	success: function(response){
				          		Ext.getBody().unmask(); 
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
				     rowsData = [];
				}, this)
			},
			scope: this
		},{
			text: '<fmt:message key="button.cancel"/>',
			handler : function(){popWin.close();},
			scope: this
		}];
      	openWin(Ext.String.format('<fmt:message key="coupon.sender"/>'), sendByLevelFormPanel, buttons, 300, 120);
    },
   	
   	 sendCouponByExcel : function(){
   	    var records = this.gsm.getSelection();
   	    if(records.length == 0 ){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}else if(records.length > 1 ){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="user.approve.records"/>');
			return;
		}
		
   	    var couponId = records[0].data.couponId;
		var formPanel = Ext.create('Ext.form.Panel', {
		    width: 400,
		    header: false,
		    labelHidder: true,
		    items:[{
                xtype: 'container',
                flex: 1,
                layout: 'anchor',
			    items: [
			    	{
				        xtype: 'filefield',
				        name: 'file',
				        msgTarget: 'side',
				        allowBlank: false,
				        anchor: '100%',
				        buttonText: '<fmt:message key="order.import.select.file"/>'
			    	}
			    ]
			}]
		});
		
		var buttons = [{
			text: '<fmt:message key="button.confirm"/>',
			handler : function(){
				var formValues = formPanel.getValues();
	            if(formPanel.isValid()){
	            	formPanel.submit({
	                    url: '<c:url value="/import/baseImportFile.json"/>',
	                    waitMsg: '<fmt:message key="ajax.loading"/>',
	                    scope: this,
	                    success: function(form, action) {
	                    	var responseObject = Ext.JSON.decode(action.response.responseText);
			                if(responseObject.error == false || responseObject.error == 'false'){
	                        	popFormWin.close();
								var testPanel = Ext.create('MyExt.BaseImportFileGrid');
								testPanel.setObject(responseObject);
								
								var importButtons = [{
									text: '<fmt:message key="button.confirm"/>',
									handler : function(){
										var rowsData = [];		
										if(testPanel.store.getCount() == 0){
											showFailMsg('<fmt:message key="errors.noRecord"/>', 4);
											return;
										}	
										
										for(var i = 0; i < testPanel.store.getCount(); i++){	
											rowsData.push(testPanel.store.getAt(i).data);			
										}
										
							            Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.import.confirm"/>', function(e){
											if(e == 'yes'){
									        	Ext.Ajax.request({
										        	url: '<c:url value="/coupon/setSendStatusByExcel.json"/>',
										         	method: 'post',
													scope: this,
													params:{dataGridJson: Ext.JSON.encode(rowsData), headerGridJson: Ext.JSON.encode(testPanel.keyValueHeaderData), couponId: couponId},
										          	success: function(xresponse){
												    	var xresponseObject = Ext.JSON.decode(xresponse.responseText);
								          				if (xresponseObject.success == true){
								          					showSuccMsg(xresponseObject.message);
								          					popWin.close();
								          				}else{
								          					showFailMsg(xresponseObject.message, 4);
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
								openWin('<fmt:message key="user.batch.register"/>', testPanel, importButtons, 750, 450);
	                        }else{
								showFailMsg(responseObject.message, 4);
							}
	                    }
	                });
	            }
			},
			scope: this
		},{
			text: '<fmt:message key="button.cancel"/>',
			handler : function(){popFormWin.close();},
			scope: this
		}];
		openFormWin('<fmt:message key="user.batch.register"/>', formPanel, buttons, 420, 120);
    },
   	
   	disableCoupon : function(){
   		var records = this.gsm.getSelection();
   		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}else if(records.length > 1){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.only.one.record"/>');
			return;
		}
	
	    Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="disabled.confirm"/>', function(e){
			if(e == 'yes'){
	        	Ext.Ajax.request({
		        	url: '<c:url value="/coupon/setCouponTimeOut.json"/>',
		         	method: 'post',
					scope: this,
					params:{couponId : records[0].data.couponId},
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
   	
   	setCouponEnabled : function(){
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
		        	url: '<c:url value="/coupon/setCouponEnabled.json"/>',
		         	method: 'post',
					scope: this,
					params:{couponId : records[0].data.couponId},
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
   	
   	fontRenderer: function(value, meta, record) {
    	meta.style = 'overflow:auto;padding: 3px 6px;text-overflow: ellipsis;white-space: nowrap;white-space:normal;line-height:24px;font-weight:bold;';      
       	return value;     
   	},
   	
   	couponTypeRenderer : function(val){
   	    if(val == 1) {
            return '<b><fmt:message key="coupon.couponType1"/></b>';
         }else if(val == 2){
            return '<b><fmt:message key="coupon.couponType2"/></b>';
         }
   	},
   	
   	receiveTypeRenderer: function(val){
   	    if(val == 0) {
            return '<b><fmt:message key="coupon.receiveType0"/></b>';
         }else if(val == 1){
            return '<b><fmt:message key="coupon.receiveType1"/></b>';
         }else if(val == 2){
            return '<b><fmt:message key="coupon.receiveType2"/></b>';
         }
   	},
   	
   	attributeRenderer : function(val){
   	    if(val == 0) {
            return '<b><fmt:message key="activity.status0"/></b>';
         }else if(val == 1){
            return '<b><fmt:message key="activity.status1"/></b>';
         }else if(val == 2){
            return '<b><fmt:message key="activity.status2"/></b>';
         }
   	},
	
	rendererStuts : function(val){
		if(val == true) {
            return '<b><fmt:message key="button.yes"/></b>';
        }else{
            return '<fmt:message key="button.no"/>';
        }
	},
});