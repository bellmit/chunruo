<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>


Ext.define('ScheduleTask', {
	extend: 'Ext.data.Model',
	idProperty: 'taskId',
    fields: [
		{name: 'taskId',	 		mapping: 'taskId',		type: 'int'},
		{name: 'type',	 		    mapping: 'type',		type: 'int'},
		{name: 'objectId',	 	    mapping: 'objectId',	type: 'int'},
		{name: 'level',			    mapping: 'level',	    type: 'string'},
		{name: 'isEnable',	        mapping: 'isEnable',    type: 'bool'},
		{name: 'isVip0',	        mapping: 'isVip0',    type: 'bool'},
		{name: 'isVip1',	        mapping: 'isVip1',    type: 'bool'},
		{name: 'isVip2',	        mapping: 'isVip2',    type: 'bool'},
		{name: 'isVip3',	        mapping: 'isVip3',    type: 'bool'},
		{name: 'beginTime',		    mapping: 'beginTime',   type: 'string'},
		{name: 'adminUserName',		mapping: 'adminUserName',type: 'string'},
		{name: 'createTime',		mapping: 'createTime',	type: 'string'},
		{name: 'updateTime',		mapping: 'updateTime',	type: 'string'}
	
	]
});

Ext.define('MyExt.systemSendMsg.SendMsgScheduleListPanel', {
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
   		Ext.util.Cookies.set('isCheck','0',expiry);
		
    	this.store = Ext.create('Ext.data.Store', {
    		pageSize: 50,
	        autoLoad: false,
			autoDestroy: true,
			sortOnLoad: true,
			remoteSort: true,
			model: 'ScheduleTask',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/scheduleTask/list.json?type=1,3,4,5"/>',
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
		
		this.columns = [
			{text: '<fmt:message key="schedule.task.taskId"/>', dataIndex: 'taskId', width: 70, locked: true, sortable : true,
        		align: 'center'
        	},
        	{text: '<fmt:message key="schedule.task.isEnable"/>', dataIndex: 'isEnable', width: 65, sortable : true,locked: true, 
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
							{id: '1', name: '<fmt:message key="button.yes"/>'},
							{id: '0', name: '<fmt:message key="button.no"/>'},
						]
					}),
			        queryMode: 'local',
			        typeAhead: true
				}
        	},
        	{text: '<fmt:message key="schedule.task.type" />', dataIndex: 'type', width: 100, sortable: true,locked: true, 
            	filter: {
            		xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: Ext.create('Ext.data.Store', {
						autoDestroy: true,
						model: 'InitModel',
						data: [
						     {id: '5', name: '<fmt:message key="schedule.task.type5"/>'},
							 {id: '4', name: '<fmt:message key="schedule.task.type4"/>'},
						     {id: '3', name: '<fmt:message key="schedule.task.type3"/>'},
							 {id: '1', name: '<fmt:message key="schedule.task.type1"/>'},
						]
					}),
			        queryMode: 'local',
			        typeAhead: true
            	},
            	renderer: this.messageTypeRenderer
            },
        	{text: '<fmt:message key="schedule.task.messageId"/>', dataIndex: 'objectId', width: 300,  sortable : true,locked: true,
        		align: 'center',filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="schedule.task.beginTime"/>', dataIndex: 'beginTime', width: 180, locked: true, sortable : true,
        		align: 'center'
        	},
        	{text: '<fmt:message key="schedule.task.adminUserName"/>', dataIndex: 'adminUserName', width: 300,  sortable : true,locked: true,
        		align: 'center',filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="system.roolingNotice.createTime"/>', dataIndex: 'createTime', width: 180, locked: true, sortable : true,
        		align: 'center'
        	},
        	{text: '<fmt:message key="system.roolingNotice.updateTime"/>', dataIndex: 'updateTime', width: 180, locked: true, sortable : true,
        		align: 'center'
			}
			 ];
        
        this.tbar = [{
	    	text: '<fmt:message key="button.refresh"/>', 
	        iconCls: 'refresh', 	
	       	handler: function(){this.store.reload();}, 
	       	scope: this
	  	},'-', {
        	text: '<fmt:message key="discovery.save"/>', 
        	iconCls: 'add', 	
        	handler: this.saveScheduleTask, 
        	scope: this
        },{
        	text: '<fmt:message key="discovery.delete"/>', 
        	iconCls: 'delete', 	
        	handler: this.deleteScheduleTask, 
        	scope: this
        }, {
        	text: '<fmt:message key="product.task.stop"/>', 
        	iconCls: 'disabled', 	
        	handler: this.stopScheduleTask, 
        	scope: this
        }, {
        	text: '<fmt:message key="product.task.start"/>', 
        	iconCls: 'enable', 	
        	handler: this.startScheduleTask, 
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
	    
	    this.east =  Ext.create('MyExt.couponManager.DiscoveryTabPanel', {
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
	   
	    }, this); 
    },
    
    saveScheduleTask : function(){
       var records = this.gsm.getSelection();
       if(records.length > 1 ){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="user.approve.records"/>');
			return;
		}
		
		var taskId = '';
		var beginTime = '';
		var type = '';
		var objectId = '';
		var vip0= '';
		var vip1 = '';
		var vip2 = '';
		var vip3 = '';
		if(records.length == 1){
		taskId = records[0].data.taskId;
		beginTime = records[0].data.beginTime;
		type  =  records[0].data.type;
		objectId  =  records[0].data.objectId;
		vip0 = records[0].data.isVip0;
		vip1 = records[0].data.isVip1;
		vip2 = records[0].data.isVip2;
		vip3 = records[0].data.isVip3;
		}
    	var sendMsgScheduleFormPanel = Ext.create('MyExt.systemSendMsg.SendMsgScheduleFormPanel', {
			id: 'add@sendMsgScheduleFormPanel' + this.id,
    		viewer: this.viewer,
    		beginTime:beginTime,
    		taskId:taskId,
    		type:type,
    		objectId:objectId,
    		vip0:vip0,
    		vip1:vip1,
    		vip2:vip2,
    		vip3:vip3
   	 	});
    	
    	
    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
		    	if(sendMsgScheduleFormPanel.form.isValid()){
					Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
		             		sendMsgScheduleFormPanel.form.submit({
		                 		waitMsg: 'Loading...',
		                 		url: '<c:url value="/scheduleTask/saveScheduleTask.json"/>',
		               			scope: this,
		               			params:{},
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
      	openWin('<fmt:message key="button.add"/>', sendMsgScheduleFormPanel, buttons,300, 300);
    },    
    
   deleteScheduleTask : function() {
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	

		for(var i = 0; i < records.length; i++){	
			rowsData.push(records[i].data.taskId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="limit.confirm.delete"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/scheduleTask/deleteScheduleTask.json"/>',
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
     startScheduleTask : function(){
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}else if(records.length > 1){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.only.one.record"/>');
			return;
		}	
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="product.task.confirm.start"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/scheduleTask/updateIsEnable.json"/>',
		         	method: 'post',
					scope: this,
					params:{taskId: records[0].data.taskId,isEnable :true },
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
   stopScheduleTask : function(){
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}else if(records.length > 1){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.only.one.record"/>');
			return;
		}	
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="product.task.confirm.stop"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/scheduleTask/updateIsEnable.json"/>',
		         	method: 'post',
					scope: this,
					params:{taskId: records[0].data.taskId,isEnable :false},
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
            return '<b><fmt:message key="order.evaluate.status0"/></b>';
         }else if(val == 1){
            return '<b><fmt:message key="order.evaluate.status1"/></b>';
         }else if(val == 2){
            return '<b><fmt:message key="order.evaluate.status2"/></b>';
         }else if(val == 3){
            return '<b><fmt:message key="order.evaluate.status3"/></b>';
         }
   	},
   	
   	 messageTypeRenderer: function(value, meta, record) { 
           	if(value =="1") {
            return '<b><fmt:message key="schedule.task.type1"/></b>';
        }else if(value =="3") {
            return '<fmt:message key="schedule.task.type3"/>';
        }else if(value =="4") {
            return '<fmt:message key="schedule.task.type4"/>';
        }else if(value =="5") {
            return '<fmt:message key="schedule.task.type5"/>';
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