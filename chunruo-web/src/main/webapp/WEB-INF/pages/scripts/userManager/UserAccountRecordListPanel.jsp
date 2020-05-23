<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('UserAccountRecord', {
	extend: 'Ext.data.Model',
	idProperty: 'recordId',
    fields: [
		{name: 'recordId',		    mapping: 'recordId',		 type: 'int'},
        {name: 'amount',			mapping: 'amount',		     type: 'string'},
		{name: 'tradeType',			mapping: 'tradeType',		 type: 'int'},
		{name: 'giftName',			mapping: 'giftName',		 type: 'string'},
		{name: 'userId',			mapping: 'userId',		     type: 'int'},
		{name: 'templateType',		mapping: 'templateType',	 type: 'int'},
		{name: 'paymentType',		mapping: 'paymentType',		 type: 'int'},
		{name: 'idCardName',		mapping: 'idCardName',		 type: 'string'},
		{name: 'idCardNo',			mapping: 'idCardNo',		 type: 'string'},
		{name: 'createTime',		mapping: 'createTime',		 type: 'string'},
		{name: 'updateTime',		mapping: 'updateTime',		 type: 'string'}

    ],
});

Ext.define('MyExt.userManager.UserAccountRecordListPanel', {
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
		
		this.store = Ext.create('Ext.data.Store', {
			pageSize: 50,
	        autoLoad:false,
			autoDestroy: true,
			sortOnLoad: true,
			remoteSort: true,
			model: 'UserAccountRecord',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/userAccount/list.json"/>',
				reader: {
					type : 'json',
                	root: 'data',
                	totalProperty: 'totalCount'
            	}
			},
			sorters: [{
	            property: 'createTime',
	            direction: 'DESC'
	        }]
		});
		
		this.isEnableStore = Ext.create('Ext.data.Store', {
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
	        	{id: 5, name: '<fmt:message key="user.level5"/>'},
	        	{id: 4, name: '<fmt:message key="user.level4"/>'},
        		{id: 3, name: '<fmt:message key="user.level3"/>'},
        		{id: 2, name: '<fmt:message key="user.level2"/>'},
        		{id: 1, name: '<fmt:message key="user.level1"/>'},
        		{id: 0, name: '<fmt:message key="user.level0"/>'}
        	]
        });

	    this.columns = [
	    	{text: '<fmt:message key="user.account.recordId" />', dataIndex: 'recordId', width: 80, sortable : true,filter: {xtype: 'textfield'}},
            {text: '<fmt:message key="user.account.userId" />', dataIndex: 'userId', width: 80, sortable: true,filter: {xtype: 'textfield'}},
            {text: '<fmt:message key="user.account.level"/>', dataIndex: 'level', width: 80, sortable : true,
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
            {text: '<fmt:message key="user.account.mobile" />', dataIndex: 'mobile', width: 200, sortable: true,filter: {xtype: 'textfield'}},
            {text: '<fmt:message key="user.account.amount" />', dataIndex: 'amount', width: 80, sortable : true,
            	filter: {xtype: 'textfield'}
            },
            {text: '<fmt:message key="user.account.giftName" />', dataIndex: 'giftName', width: 200, sortable: true,filter: {xtype: 'textfield'}},
           {text: '<fmt:message key="user.account.tradeType" />', dataIndex: 'tradeType', width: 100, sortable: true,
            	filter: {
            		xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: Ext.create('Ext.data.Store', {
						autoDestroy: true,
						model: 'InitModel',
						data: [
							{id: '1', name: '<fmt:message key="user.account.tradeType1"/>'},
							{id: '2', name: '<fmt:message key="user.account.tradeType2"/>'},
							{id: '3', name: '<fmt:message key="user.account.tradeType3"/>'},
							{id: '4', name: '<fmt:message key="user.account.tradeType4"/>'},
						]
					}),
			        queryMode: 'local',
			        typeAhead: true
            	},
            	renderer: this.tradeTypeRenderer
            },
            {text: '<fmt:message key="user.account.paymentType" />', dataIndex: 'paymentType', width: 100, sortable: true,
            	filter: {
            		xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: Ext.create('Ext.data.Store', {
						autoDestroy: true,
						model: 'InitModel',
						data: [
							{id: '0', name: '<fmt:message key="user.account.paymentType0"/>'},
							{id: '1', name: '<fmt:message key="user.account.paymentType1"/>'},
							{id: '2', name: '<fmt:message key="user.account.paymentType2"/>'},
						]
					}),
			        queryMode: 'local',
			        typeAhead: true
            	},
            	renderer: this.typeRenderer
            },
            {text: '<fmt:message key="user.account.idCardName" />', dataIndex: 'idCardName', width: 100, sortable: true,filter: {xtype: 'textfield'}},
            {text: '<fmt:message key="user.account.idCardNo" />', dataIndex: 'idCardNo', width: 100, sortable: true,filter: {xtype: 'textfield'}},
            {text: '<fmt:message key="recharge.template.createTime" />', dataIndex: 'createTime', width: 180, sortable: true,filter: {xtype: 'textfield'}},
            {text: '<fmt:message key="recharge.template.updateTime" />', dataIndex: 'updateTime', width: 180, sortable: true,filter: {xtype: 'textfield'}}
           
        ];
        
        this.tbar = [	
        <jkd:haveAuthorize access="/userAccount/list.json?isExporter=true">
        {
        	text: '<fmt:message key="user.account.exporter.xls"/>', 
        	iconCls: 'excel', 	
        	handler: this.exporter,
        	scope:this
        }
		</jkd:haveAuthorize>
        ];
        
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
	    
	    this.east =  Ext.create('MyExt.couponManager.RechargeTemplateTabPanel', {
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
    	<jkd:haveAuthorize access="/userAccount/list.json">
	   	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.productList.filters)
			});
	    }, this);
	    this.store.load();   
	    </jkd:haveAuthorize>
	    
	    this.productList.on('itemdblclick', function(view, record, item, index, e, eOpts) {
	    	<jkd:haveAuthorize access="/rechargeTemplate/getRechargeTemplateByIdssssssssss.json">
	    	this.east.transferData(this.east, record, this.body.dom.clientWidth);
	    	this.east.show();
	    	</jkd:haveAuthorize>
	    }, this); 
    },
    
	saveRechargeTemplate : function(){
		var rechargeTemplateFormPanel = Ext.create('MyExt.couponManager.RechargeTemplateFormPanel',
		 {id: 'rechargeTemplateFormPanel@RechargeTemplateFormPanel',
		  title: '<fmt:message key="system.sendmsg.add"/>',
		  isEditor:false
		  });
		var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
				var rowsData = [];    
		       	rechargeTemplateFormPanel.down('imagepanel').store.each(function(record) {
		       		record.data.input_file = null;
		            rowsData.push(record.data);    
		      	}, this);
		      	
	            if(rechargeTemplateFormPanel.form.isValid()){
	            	Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
			                rechargeTemplateFormPanel.form.submit({
			                    waitMsg: 'Loading...',
			                    url: '<c:url value="/rechargeTemplate/saveRechargeTemplate.json"/>',
			                    scope: this,
			                    params:{recodeGridJson: Ext.JSON.encode(rowsData)},
			                    success : function(form, action) {
			                    	var responseObject = Ext.JSON.decode(action.response.responseText);
			                    	if(responseObject.error == false){
			                       		showSuccMsg(responseObject.message);
			                        	this.store.loadPage(1);
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
		openWin('<fmt:message key="system.sendmsg.add"/>', rechargeTemplateFormPanel, buttons, 800, 450);
	},
	
	 deleteRechargeTemplate : function() {
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	

		for(var i = 0; i < records.length; i++){	
			rowsData.push(records[i].data.templateId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm.agree"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/rechargeTemplate/deleteRechargeTemplate.json"/>',
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
	
	stopRechargeTemplate : function(){
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}else if(records.length > 1){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.only.one.record"/>');
			return;
		}	
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="recharge.template.confirm.stop"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/rechargeTemplate/updateIsEnable.json"/>',
		         	method: 'post',
					scope: this,
					params:{templateId: records[0].data.templateId,isEnable :false},
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
    
     startRechargeTemplate : function(){
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}else if(records.length > 1){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.only.one.record"/>');
			return;
		}	
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="recharge.template.confirm.start"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/rechargeTemplate/updateIsEnable.json"/>',
		         	method: 'post',
					scope: this,
					params:{templateId: records[0].data.templateId,isEnable :true },
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
    
     exporter : function(){
		var excelPanel = Ext.create('MyExt.orderManager.ExportExcel', {id: 'ExportExcel@ExportExcel', title: '<fmt:message key="button.add"/>'});
		var buttons = [{
			text: '<fmt:message key="button.exporter.xls"/>',
			handler: function(){
	            if(excelPanel.form.isValid()){
	            	Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="order.export.confirm"/>', function(e){
						if(e == 'yes'){
							var columns = [];
							var begin = Ext.util.Format.date(Ext.getCmp('beginTime').getValue(), 'Y-m-d H:i:s');
							var end = Ext.util.Format.date(Ext.getCmp('endTime').getValue(), 'Y-m-d H:i:s');
					
				    		Ext.Array.each(this.productList.getColumns(), function(object, index, countriesItSelf) {
				    			if(object.dataIndex){
				    				columns.push({key: object.dataIndex, value: object.text});
				    			}
							});
							
							Ext.Ajax.request({
					        	url: this.productList.store.proxy.url,
					         	method: 'post',
								scope: this,
								params:{
									columns: Ext.JSON.encode(columns), 
									filters: Ext.JSON.encode(this.productList.filters), 
									beginTime: begin, 
									endTime: end, 
									isExporter: true
								},
					          	success: function(response){
			          				var responseObject = Ext.JSON.decode(response.responseText);
			          				if(responseObject.success == true 
			          						&& responseObject.filePath != null 
			          						&& responseObject.filePath != ''){
			                        	window.location.href  = '<c:url value="/userAccount/downLoadExportFile.msp?filePath="/>' + responseObject.filePath;
			                        	popWin.close();
			                        }else{
										showFailMsg('<fmt:message key="order.nomessage"/>', 4);
									}
								}
					     	});
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
		openWin('<fmt:message key="button.exporter.xls"/>', excelPanel, buttons, 400, 175);
	},
    
	userLevelRenderer: function(value, meta, record) { 
       	if(value =="1") {
            return '<b><fmt:message key="recharge.template.userLevel1"/></b>';
        }else if(value == "2"){
            return '<fmt:message key="recharge.template.userLevel2"/>';
        }else if(value == "4"){
            return '<fmt:message key="recharge.template.userLevel4"/>';
        }else if(value == "5"){
            return '<fmt:message key="recharge.template.userLevel5"/>';
        }else if(value == "6"){
            return '<fmt:message key="recharge.template.userLevel6"/>';
        }else if(value == "7"){
            return '<fmt:message key="recharge.template.userLevel7"/>';
        }
   	},
   	giftUserLevelRenderer: function(value, meta, record) { 
       	if(value =="2") {
            return '<b><fmt:message key="recharge.template.giftUserLevel2"/></b>';
        }else if(value == "4"){
            return '<fmt:message key="recharge.template.giftUserLevel4"/>';
        }else if(value == "5"){
            return '<fmt:message key="recharge.template.giftUserLevel5"/>';
        }else {
          return '<fmt:message key="recharge.template.giftUserLevel.none"/>';
        }
   	},
   	
    typeRenderer: function(value, meta, record) { 
        if(value =="0") {
            return '<b><fmt:message key="user.account.paymentType0"/></b>';
        }else if(value == '1'){
            return '<b><fmt:message key="user.account.paymentType1"/></b>';
        }else if(value == '2'){
            return '<b><fmt:message key="user.account.paymentType2"/></b>';
        }else{
        return '<b><fmt:message key="user.account.paymentType.none"/></b>';
        }
   	},
   	
   	tradeTypeRenderer: function(value, meta, record) { 
        if(value =="1") {
            return '<b><fmt:message key="user.account.tradeType1"/></b>';
        }else if(value == '2'){
            return '<b><fmt:message key="user.account.tradeType2"/></b>';
        }else if(value == '3'){
            return '<b><fmt:message key="user.account.tradeType3"/></b>';
        }else if(value == '4'){
            return '<b><fmt:message key="user.account.tradeType4"/></b>';
        }
   	},
   	
   	 rendererlevelStuts : function(val){
	     if(val == 1) {
            return '<b><fmt:message key="user.level1"/></b>';
         }else if(val == 2){
            return '<b><fmt:message key="user.level2"/></b>';
         }else if(val == 3){
            return '<b><fmt:message key="user.level3"/></b>';
        }else if(val == 4){
            return '<b><fmt:message key="user.level4"/></b>';
        }else if(val == 5){
            return '<b><fmt:message key="user.level5"/></b>';
        }else{
           return '<b><fmt:message key="user.level0"/></b>';
        }
	},
   	
   	inviteStus : function(val){
		if(val == true) {
            return '<b><fmt:message key="button.yes"/></b>';
        }else{
            return '<fmt:message key="button.no"/>';
        }
	}
})
