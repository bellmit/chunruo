<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('User', {
	extend: 'Ext.data.Model',
	idProperty: 'userId',
    fields: [
		{name: 'userId',	 			mapping: 'userId',				type: 'int'},
    	{name: 'unionId',	 			mapping: 'unionId',				type: 'string'},
    	{name: 'openId',	 			mapping: 'openId',				type: 'string'},
		{name: 'nickname',	 			mapping: 'nickname',			type: 'string'},
		{name: 'countryCode',	 		mapping: 'countryCode',			type: 'string'},
		{name: 'isAgent',	 			mapping: 'isAgent',				type: 'bool'},
		{name: 'isCustomerManager',	    mapping: 'isCustomerManager',	type: 'bool'},
		{name: 'isSpecialDealer',	 	mapping: 'isSpecialDealer',		type: 'bool'},
		{name: 'mobile',	 			mapping: 'mobile',				type: 'string'},
		{name: 'registerIp',	 		mapping: 'registerIp',			type: 'string'},
		{name: 'lastIp',	 			mapping: 'lastIp',				type: 'string'},
		{name: 'loginCount',	 		mapping: 'loginCount',			type: 'string'},
		{name: 'status',	 			mapping: 'status',				type: 'bool'},
		{name: 'introduce',	 			mapping: 'introduce',			type: 'string'},
		{name: 'headerImage',	 		mapping: 'headerImage',			type: 'string'},
		{name: 'sex',	 				mapping: 'sex',					type: 'string'},
		{name: 'realName',	 			mapping: 'realName',			type: 'string'},
		{name: 'identityNo',	 		mapping: 'identityNo',			type: 'string'},
		{name: 'topUserId',	 			mapping: 'topUserId',			type: 'string'},
		{name: 'topStoreName',	 		mapping: 'topStoreName',		type: 'string'},
		{name: 'provinceName',	 		mapping: 'provinceName',		type: 'string'},
		{name: 'cityName',	 			mapping: 'cityName',			type: 'string'},
		{name: 'areaName',	 			mapping: 'areaName',			type: 'string'},
		{name: 'logo',	 				mapping: 'logo',				type: 'string'},
		{name: 'profitTop',	 			mapping: 'profitTop',			type: 'string'},
		{name: 'storeNumber',	 		mapping: 'storeNumber',			type: 'string'},
		{name: 'isBindWechat',	 		mapping: 'isBindWechat',		type: 'bool'},
		{name: 'isSpecialDealerDown',	mapping: 'isSpecialDealerDown',	type: 'bool'},
		{name: 'isSystem',				mapping: 'isSystem',			type: 'bool'},
		{name: 'isVipCustomer',			mapping: 'isVipCustomer',		type: 'bool'},
		{name: 'createTime',	 		mapping: 'createTime',			type: 'string'},
		{name: 'updateTime',	 		mapping: 'updateTime',			type: 'string'},
	   	{name: 'level',		           	mapping: 'level',			    type: 'int'},
	   	{name: 'pushLevel',		        mapping: 'pushLevel',			type: 'int'},
	   	{name: 'expireEndDate',	 		mapping: 'expireEndDate',		type: 'string'},
	   	{name: 'storeName',	 			mapping: 'storeName',			type: 'string'},
	   	{name: 'storeMobile',	 		mapping: 'storeMobile',			type: 'string'},
	   	{name: 'linkman',	 			mapping: 'linkman',				type: 'string'},
	   	{name: 'balance',	 			mapping: 'balance',				type: 'string'},
	   	{name: 'sales',	 				mapping: 'sales',				type: 'string'},
	   	{name: 'income',	 			mapping: 'income',				type: 'string'},
	   	{name: 'topUserId',	 			mapping: 'topUserId',			type: 'string'},
	   	{name: 'inviterCode',	 		mapping: 'inviterCode',			type: 'string'},
	   	{name: 'withdrawalAmount',	 	mapping: 'withdrawalAmount',	type: 'string'},
	   	{name: 'bankId',	 			mapping: 'bankId',				type: 'string'},
	   	{name: 'bankCard',	 			mapping: 'bankCard',			type: 'string'},
	   	{name: 'bankCardUser',	 		mapping: 'bankCardUser',		type: 'string'},
	   	{name: 'openingBank',	 		mapping: 'openingBank',			type: 'string'},
	   	{name: 'registerTime',	 		mapping: 'registerTime',		type: 'string'}
    ]
});

Ext.define('MyExt.userManager.UserListGrid', {
    extend : 'Ext.panel.Panel',
    requires : ['Ext.ux.grid.GridHeaderFilters','Ext.ux.grid.Exporter'],
    header: false,
	closable: true,
	columnLines: true,
	animCollapse: true,
	layout: 'border',
   	defaults: {  
    	split: true,    
        collapsible: true,
        collapseDirection: 'left'
    },

	initComponent : function(config) {
		Ext.apply(this, config);
        
        this.store = Ext.create('Ext.data.Store', {
        	pageSize: 50,
	        autoLoad: false,
			autoDestroy: true,
			sortOnLoad: true,
			remoteSort: true,
			model: 'User',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/user/list.json"/>',
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
		
		this.rendererStutsStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: 1, name: '<fmt:message key="button.yes"/>'},
        		{id: 0, name: '<fmt:message key="button.no"/>'}
        	]
        });
        
        this.rendererlevelStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: 2, name: '<fmt:message key="user.level2"/>'},
        		{id: 0, name: '<fmt:message key="user.level0"/>'}
        	]
        });
        
        this.rendererPushLevelStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: 3, name: '<fmt:message key="user.pushLevel3"/>'},
        		{id: 2, name: '<fmt:message key="user.pushLevel2"/>'},
        		{id: 1, name: '<fmt:message key="user.pushLevel1"/>'},
        		{id: 0, name: '<fmt:message key="user.pushLevel0"/>'}
        	]
        });
        
		this.columns = [
			{text: '<fmt:message key="user.headerImage"/>', dataIndex: 'headerImage', width: 35, sortable : false, locked: true,
		        renderer : function(val, metadata, record, rowIndex, columnIndex, store){ 
					return '<img height="20" width="20" src="' + val + '">';
				}
        	},
        	{text: '<fmt:message key="user.userId"/>', dataIndex: 'userId', width: 70, locked: true, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.nickname"/>', dataIndex: 'nickname', width: 140, sortable : true, locked: true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.level"/>', dataIndex: 'level', width: 80, sortable : true, locked: true,
        		renderer : this.rendererlevelStuts,
        		filter: {
					xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.rendererlevelStore,
			        queryMode: 'local',
			        typeAhead: true
				},
        	},
        	
        	{text: '<fmt:message key="user.mobile"/>', dataIndex: 'mobile', width: 100, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.countryCode"/>', dataIndex: 'countryCode', width: 70, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.openId"/>', dataIndex: 'openId', width: 210, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        
        	{text: '<fmt:message key="user.topUserId"/>', dataIndex: 'topUserId', width: 120, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.topStoreName"/>', dataIndex: 'topStoreName', width: 120, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.registerTime"/>', dataIndex: 'registerTime', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.updateTime"/>', dataIndex: 'updateTime', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	}
        ];
        
        this.keywordField = new Ext.create('Ext.form.TextField', {
			width: 200,
			emptyText:'<fmt:message key="app.user.search" />',
        	scope: this
        });
        
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
		this.userListBbar = this.pagingToolbar; 
		
    	this.userList = Ext.create('Ext.grid.GridPanel', {
	    	id: 'userList@UserPanel' + this.id,
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
		    bbar: this.userListBbar,
        	plugins: ['gridHeaderFilters','gridexporter'],
		    viewConfig: {
		        stripeRows: true,
		        enableTextSelection: true
		    }
	    });
	    
	  
        this.east =  Ext.create('MyExt.userManager.UserTabPanel', {
        	userList: this.userList,
		 	viewer: this.viewer,
		 	layout: 'card',
	        border: false,
	       	region: 'east',
	        width: 550,
	        split: true,
	        header: false,
	        hidden: true
        });
	    
    	this.items = [this.userList, this.east];	
		this.east.hide();
    	this.callParent(arguments);
    	
    	<jkd:haveAuthorize access="/user/list.json">
    	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.userList.filters),
				keyword: this.keywordField.getRawValue()
			});
	    }, this);
	    this.store.load();
	    </jkd:haveAuthorize>
	    
	    this.gsm = this.userList.getSelectionModel();
	    this.userList.on('itemdblclick', function(view, record, item, index, e, eOpts) {
	    	<jkd:haveAuthorize access="/user/getUserById.json">
	    	this.east.transferData(this.east, record, this.body.dom.clientWidth);
	    	this.east.show();
	    	</jkd:haveAuthorize>
	    }, this);
    },
    
     rendererlevelStuts : function(val){
	     if(val == 2){
            return '<b><fmt:message key="user.level2"/></b>';
         }else{
           return '<b><fmt:message key="user.level0"/></b>';
        }
	},
	
	rendererPushLevelStuts : function(val){
	     if(val == 1) {
            return '<b><fmt:message key="user.pushLevel1"/></b>';
         }else if(val == 2){
            return '<b><fmt:message key="user.pushLevel2"/></b>';
         }else if(val == 3){
            return '<b><fmt:message key="user.pushLevel3"/></b>';
        }else{
           return '<b><fmt:message key="user.pushLevel0"/></b>';
        }
	},
    
    cleanSearch : function(){
    	this.keywordField.setRawValue();
		this.store.loadPage(1);
    },
    
   	resetPassword : function(){
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.userId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="order.cancel.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({  
		        	url: '<c:url value="/user/resetPassword.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData)},
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
    
    cancelUser : function(){
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.userId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="order.cancel.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/user/cancelUser.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData)},
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
    
    changeSpecialDealer : function(){
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.userId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="user.changeSpecialDealer.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/user/changeSpecialDealer.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData)},
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
    
    getUserCountByMobile : function(){
		var userCountForm = Ext.create('MyExt.userManager.UserCountForm', {
			id: 'userCountForm@' + this.id,
    		viewer: this.viewer,	
    		edit: false,
   	 	});

    	var buttons = [{
			text: '<fmt:message key="button.cancel"/>',
			handler : function(){popWin.close();},
			scope: this
		}];
      	openWin(Ext.String.format('<fmt:message key="user.get.count"/>'), userCountForm, buttons, 380, 300);
    },
    
    getFourCount : function(){
		var userFourCountForm = Ext.create('MyExt.userManager.UserFourCountForm', {
			id: 'userFourCountForm@' + this.id,
    		viewer: this.viewer,	
    		edit: false,
   	 	});

    	var buttons = [{
			text: '<fmt:message key="button.cancel"/>',
			handler : function(){popWin.close();},
			scope: this
		}];
      	openWin(Ext.String.format('<fmt:message key="user.get.count"/>'), userFourCountForm, buttons, 380, 300);
    },
    
       
    setSystemUser : function(){
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.userId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="user.changeSpecialDealer.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/user/setSystemUser.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData)},
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
   
    batchRegisterUser : function(){
		var formPanel = Ext.create('Ext.form.Panel', {
		    width: 400,
		    header: false,
		    labelHidder: true,
		    items:[{
                xtype: 'container',
                flex: 1,
                layout: 'anchor',
			    items: [{
					xtype:'textfield',
					fieldLabel: '<fmt:message key="store.inviterCode"/>',
					name: 'inviterCode',
					anchor:'97%'
				},{
				    xtype: 'filefield',
				   	name: 'file',
				   	msgTarget: 'side',
				 	allowBlank: false,
				  	anchor: '100%',
				  	buttonText: '<fmt:message key="order.import.select.file"/>'
			    }]
			}]
		});
		
		var buttons = [{
			text: '<fmt:message key="button.confirm"/>',
			handler : function(){
				var formValues = formPanel.getValues();
  				var inviterCode = formValues["inviterCode"];
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
										        	url: '<c:url value="/user/batchRegisterUser.json"/>',
										         	method: 'post',
													scope: this,
													params:{dataGridJson: Ext.JSON.encode(rowsData), headerGridJson: Ext.JSON.encode(testPanel.keyValueHeaderData), inviterCode : inviterCode},
										          	success: function(xresponse){
												    	var xresponseObject = Ext.JSON.decode(xresponse.responseText);
								          				if (xresponseObject.success == true){
								          					showSuccMsg(xresponseObject.message);
								          					popWin.close();
								          					this.loadData();
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
    
    exportDeclar : function(){
		var excelPanel = Ext.create('MyExt.userManager.ExportExcel', {id: 'ExportExcel@ExportExcel', title: '<fmt:message key="button.add"/>'});
		var buttons = [{
			text: '<fmt:message key="button.exporter.declar.xls"/>',
			handler: function(){
	            if(excelPanel.form.isValid()){
	            	Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="order.export.confirm"/>', function(e){
						if(e == 'yes'){
							var begin = Ext.util.Format.date(Ext.getCmp('beginTime').getValue(), 'Y-m-d H:i:s');
							var end = Ext.util.Format.date(Ext.getCmp('endTime').getValue(), 'Y-m-d H:i:s');
					        console.log(begin);
					        console.log(end);
					        window.location.href = '<c:url value="/user/exportDeclarDownLineInfo.json?beginTime="/>' + begin + '&endTime=' + end;
			              	popWin.close();
							
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
		openWin('<fmt:message key="button.exporter.declar.xls"/>', excelPanel, buttons, 400, 175);
	},
	
	exportTeamLine : function(){	
       	var teamExcelPanel = Ext.create('MyExt.userManager.TeamExcel', {id: 'TeamExcel@TeamExcel', title: '<fmt:message key="button.add"/>'});
		var buttons = [{
			text: '<fmt:message key="button.exporter.teamLine.xls"/>',
			handler: function(){
	            if(teamExcelPanel.form.isValid()){
	            	Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="user.export.team.confirm"/>', function(e){
						if(e == 'yes'){
							var mobile = Ext.getCmp('mobile').getValue();
							var level = Ext.getCmp('level').getValue();
							window.location.href = '<c:url value="/user/exportTeamLineByMobile.json?mobile="/>' + mobile +'&level=' + level;
			              	popWin.close();
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
		openWin('<fmt:message key="button.exporter.teamLine.xls"/>', teamExcelPanel, buttons, 400, 150);

	}, 
	
	exportVIP : function(){
		var excelPanel = Ext.create('MyExt.userManager.ExportExcel', {id: 'ExportExcel@ExportExcel', title: '<fmt:message key="button.add"/>'});
		var buttons = [{
			text: '<fmt:message key="button.exporter.vip.xls"/>',
			handler: function(){
	            if(excelPanel.form.isValid()){
	            	Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="order.export.confirm"/>', function(e){
						if(e == 'yes'){
							var begin = Ext.util.Format.date(Ext.getCmp('beginTime').getValue(), 'Y-m-d H:i:s');
							var end = Ext.util.Format.date(Ext.getCmp('endTime').getValue(), 'Y-m-d H:i:s');
					        console.log(begin);
					        console.log(end);
					        window.location.href = '<c:url value="/user/exportUserVipExcel.json?beginTime="/>' + begin + '&endTime =' + end;
			              	popWin.close();
							
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
		openWin('<fmt:message key="button.exporter.vip.xls"/>', excelPanel, buttons, 400, 175);
	},
	
	exportNewAddDealer : function(){
		var excelPanel = Ext.create('MyExt.userManager.ExportExcel', {id: 'ExportExcel@ExportExcel', title: '<fmt:message key="button.add"/>'});
		var buttons = [{
			text: '<fmt:message key="button.exporter.dealer.xls"/>',
			handler: function(){
	            if(excelPanel.form.isValid()){
	            	Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="order.export.confirm"/>', function(e){
						if(e == 'yes'){
							var begin = Ext.util.Format.date(Ext.getCmp('beginTime').getValue(), 'Y-m-d H:i:s');
							var end = Ext.util.Format.date(Ext.getCmp('endTime').getValue(), 'Y-m-d H:i:s');
					        window.location.href = '<c:url value="/user/exportNewAddDealer.json?beginTime="/>' + begin + '&endTime=' + end;
			              	popWin.close();
							
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
		openWin('<fmt:message key="button.exporter.dealer.xls"/>', excelPanel, buttons, 400, 175);
	},
	
	editMobile : function(){	
		var editMobileForm = Ext.create('MyExt.userManager.EditMobileForm', {id: 'editMobileForm@editMobileForm', title: '<fmt:message key="button.add"/>'});
		var buttons = [{
			text: '<fmt:message key="button.user.edit.confirm"/>',
			handler: function(){
	            if(editMobileForm.form.isValid()){
	            	Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="button.user.edit.confirm"/>', function(e){
						if(e == 'yes'){
							var oldMobile = Ext.getCmp('oldMobile').getValue();
							var newMobile = Ext.getCmp('newMobile').getValue();
							Ext.Ajax.request({
					        	url: '<c:url value="/user/editUserMobile.json"/>',
					         	method: 'post',
								scope: this,
								params:{oldMobile: oldMobile, newMobile: newMobile},
					          	success: function(xresponse){
							    	var xresponseObject = Ext.JSON.decode(xresponse.responseText);
			          				if (xresponseObject.success == true){
			          					showSuccMsg(xresponseObject.message);
			          					popWin.close();
			          					this.loadData();
			          				}else{
			          					showFailMsg(xresponseObject.message, 4);
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
		openWin('<fmt:message key="button.user.editMobile"/>', editMobileForm, buttons, 400,150);
	}, 
	
	userSaleStandard:function(){	
		var userSaleStandardForm = Ext.create('MyExt.userManager.UserSaleStandardForm', {id: 'userSaleStandardForm@userSaleStandardForm', title: '<fmt:message key="button.add"/>'});
		Ext.Ajax.request({
       		url: '<c:url value="/user/getUserStandard.json"/>',
        	method: 'post',
         	success: function(response){
       			var responseObject = Ext.JSON.decode(response.responseText);
       			if (responseObject.success == true){
       				var salesNum = Ext.getCmp('salesNum');
					var hours = Ext.getCmp('hours');
					salesNum.setValue(responseObject.data.salesNum);
					hours.setValue(responseObject.data.hours);
       			}
			}
    	});
		
		var buttons = [
		<jkd:haveAuthorize access="/user/userSaleStandard.json">
		{
			text: '<fmt:message key="button.user.edit.saleAmount"/>',
			handler: function(){
	            if(userSaleStandardForm.form.isValid()){
	            	Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="button.user.edit.confirm"/>', function(e){
						if(e == 'yes'){
							var salesNum = Ext.getCmp('salesNum').getValue();
							var hours = Ext.getCmp('hours').getValue();
							Ext.Ajax.request({
					        	url: '<c:url value="/user/userSaleStandard.json"/>',
					         	method: 'post',
								scope: this,
								params:{v2SaleAmount: v2SaleAmount, hours: hours},
					          	success: function(xresponse){
							    	var xresponseObject = Ext.JSON.decode(xresponse.responseText);
			          				if (xresponseObject.success == true){
			          					showSuccMsg(xresponseObject.message);
			          					popWin.close();
			          					this.loadData();
			          				}else{
			          					showFailMsg(xresponseObject.message, 4);
			          				}
								}
					     	})
			        	}
			        }, this)
	        	}
			},
			scope: this
		},
		</jkd:haveAuthorize>
		{
			text: '<fmt:message key="button.cancel"/>',
			handler : function(){popWin.close();},
			scope: this
		}];
		openWin('<fmt:message key="button.user.saleAmount"/>', userSaleStandardForm, buttons, 400,150);
	}, 
	
	enableVipCustomer : function() {
   		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){	
			rowsData.push(records[i].data.userId);	
		}
		
	    Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
			if(e == 'yes'){
	        	Ext.Ajax.request({
		        	url: '<c:url value="/user/setVipCustomer.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData), isEnabled: true},
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
   	
   	cancelVipCustomer : function() {
   		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.userId);	
		}
		
	    Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
			if(e == 'yes'){
	        	Ext.Ajax.request({
		        	url: '<c:url value="/user/setVipCustomer.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData), isEnabled: false},
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
   	
   	enableCustomerManager : function() {
   		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){	
			rowsData.push(records[i].data.userId);	
		}
		
	    Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
			if(e == 'yes'){
	        	Ext.Ajax.request({
		        	url: '<c:url value="/user/setCustomerManager.json"/>',
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
   	}
});