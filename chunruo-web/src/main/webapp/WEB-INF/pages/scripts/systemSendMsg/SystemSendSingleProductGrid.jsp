<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('SystemGoodsMsg', {
	extend: 'Ext.data.Model',
    fields: [
    	{name: 'id', 			type: 'int'},
    	{name: 'content', 		type: 'string'},
    	{name: 'messageType', 	type: 'string'},
    	{name: 'objectId', 		type: 'string'},
    	{name: 'createTime', 	type: 'string'},
    	{name: 'title', 		type: 'string'},
    	{name: 'imageUrl', 		type: 'string'},
    	{name: 'productIds',    type: 'string'},
    	{name: 'objectType', 	type: 'string'},
    	{name: 'productName', 	type: 'string'},
    	{name: 'typeName', 	    type: 'string'},
    	{name: 'beginTime', 	type: 'string'},
    	{name: 'endTime', 	    type: 'string'}
    ],
    idProperty: 'id'
});

Ext.define('MyExt.systemSendMsg.SystemSendSingleProductGrid', {
	extend : 'Ext.grid.GridPanel',
    requires : ['Ext.ux.grid.GridHeaderFilters'],
	region: 'center',
	autoScroll: true,   
	closable: true,
	selType: 'checkboxmodel',
	multiSelect: true,
	columnLines: true,
	animCollapse: false,
    enableLocking: true,
    plugins: ['gridHeaderFilters'],
    viewConfig: {
        stripeRows: true,
        enableTextSelection: true
    },
    
	initComponent : function(config) {
		Ext.apply(this, config);
		
		this.store = Ext.create('Ext.data.Store', {
			pageSize: 50,
	        autoLoad:false,
			autoDestroy: true,
			sortOnLoad: true,
			remoteSort: true,
			model: 'SystemGoodsMsg',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/pushMessage/list.json?messageType=4"/>',
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

	    this.columns = [
	    	{text: '<fmt:message key="system.sendmsg.id" />', dataIndex: 'id', width: 120, sortable : true},
            {text: '<fmt:message key="system.sendmsg.title" />', dataIndex: 'title', width: 240, sortable : true,
            	filter: {xtype: 'textfield'}
            },
            {text: '<fmt:message key="system.sendmsg.content" />', dataIndex: 'content', width: 240, sortable: true,
            	filter: {xtype: 'textfield'}
            },
            {text: '<fmt:message key="system.sendmsg.type" />', dataIndex: 'messageType', width: 240, sortable: true,
            	filter: {
            		xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: Ext.create('Ext.data.Store', {
						autoDestroy: true,
						model: 'InitModel',
						data: [
							{id: '1', name: '<fmt:message key="system.sendmsg.type.notice"/>'},
							{id: '2', name: '<fmt:message key="system.sendmsg.type.activity"/>'},
							{id: '3', name: '<fmt:message key="system.sendmsg.type.goods"/>'},
							{id: '4', name: '<fmt:message key="system.sendmsg.type.single"/>'}
						]
					}),
			        queryMode: 'local',
			        typeAhead: true
            	},
            	renderer: this.messageTypeRenderer
            },
            {
            	text: '<fmt:message key="system.sendmsg.image" />', dataIndex: 'imageUrl',
            	width: 240, 
            	sortable: true, 
            	align: 'center', 
            	renderer: function(value, metadata, record) {
        			return Ext.String.format('<img height="60" width="60" src="{0}"></img>', value);
    			}
    		},
    		{
    			text: '<fmt:message key="system.sendmsg.object" />', 
            	dataIndex: 'objectType',
            	width: 65, 
            	align: 'center',
            	sortable : false,
            	renderer: this.objectTypeRenderer,
            	filter: {
        			xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        queryMode: 'local',
			        typeAhead: true,
			        store: Ext.create('Ext.data.Store', {
						autoDestroy: true,
						model: 'InitModel',
						data: [
							{id: '1', name: '<fmt:message key="user.level1"/>'},
				            {id: '2', name: '<fmt:message key="user.level2"/>'},
				            {id: '6', name: '<fmt:message key="user.level.all"/>'},
				            {id: '5', name: '<fmt:message key="user.level.custom"/>'}
						]
					})
				}
           	},
    		{text: '<fmt:message key="system.sendmsg.objectId" />', dataIndex: 'objectId', width: 360, align: 'center', sortable: true, renderer: this.openStuts},
            {text: '<fmt:message key="system.sendmsg.createTime" />', dataIndex: 'createTime', width: 180, sortable: true}
        ];
        
        this.tbar = [
        <jkd:haveAuthorize access="/pushMessage/addProductMessage.json">
        {
        	text: '<fmt:message key="button.add"/>', 
        	iconCls: 'add', 
        	handler: this.addMessage, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/pushMessage/deleteMessage.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
        	text: '<fmt:message key="button.delete"/>', 
        	iconCls: 'delete',	
        	handler: this.deleteMessage, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/pushMessage/pushMessageByLevel.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
        	text: '<fmt:message key="push.message.bylevel"/>', 
        	iconCls: 'enable', 	
        	handler: this.pushByLevel, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/pushMessage/pushMessageByCustom.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
        	text: '<fmt:message key="push.message.custom"/>', 
        	iconCls: 'enable', 	
        	handler: this.pushByCustom, 
        	scope: this
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
		this.bbar = this.pagingToolbar;
    	this.callParent();
    	
    	this.gsm = this.getSelectionModel();
    	<jkd:haveAuthorize access="/pushMessage/list.json">
    	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.filters)
			});
	    }, this);
	    this.store.load();
	    </jkd:haveAuthorize>
    },
    
	addMessage : function(){
		var systemSendSingleProductForm = Ext.create('MyExt.systemSendMsg.SystemSendSingleProductFormPanel', {id: 'systemSendSingleProductForm@id', title: '<fmt:message key="system.sendmsg.add"/>'});
		var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
			var levels = [];
			var items = systemSendSingleProductForm.down('checkboxgroup[name=level]').items;   
         	for (var i = 0; i < items.length; i++){    
             		if (items.get(i).checked){    
                			levels.push(items.get(i).inputValue);              
             		}    
         	}
				var rowsData = [];    
		       	systemSendSingleProductForm.down('imagepanel').store.each(function(record) {
		       		record.data.input_file = null;
		            rowsData.push(record.data);    
		      	}, this);
		      	
	            if(systemSendSingleProductForm.form.isValid()){
	            	Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
			                systemSendSingleProductForm.form.submit({
			                    waitMsg: 'Loading...',
			                    url: '<c:url value="/pushMessage/addProductMessage.json"/>',
			                    scope: this,
			                    params:{recodeGridJson: Ext.JSON.encode(rowsData),levels:Ext.JSON.encode(levels)},
			                    success : function(form, action) {
			                    	var responseObject = Ext.JSON.decode(action.response.responseText);
			                    	if(responseObject.error == false){
			                       		showSuccMsg(responseObject.message);
			                        	this.store.loadPage(1);
										popWin.close();
									}else{
										showFailMsg(responseObject.message, 4);
									}
			                    },
			                    failure : function(form, action) {  
							     //   console.log(action);
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
		openWin('<fmt:message key="system.sendmsg.add"/>', systemSendSingleProductForm, buttons, 800,500);
	},
	
	deleteMessage : function() {
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.id);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="delete.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/pushMessage/deleteMessage.json"/>',
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
	
	pushByLevel: function(){
	    var level = '';
		var records = this.gsm.getSelection();
		if(records.length == 0 ){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}else if(records.length > 1 ){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="user.approve.records"/>');
			return;
		}
	
    	var sendByLevelFormPanel = Ext.create('MyExt.systemSendMsg.SendByLevelFormPanel', {
			id: 'sendByLevelFormPanel@' + this.id,
    		viewer: this.viewer,	
   	 	});
    	
    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
				var levels = [];
				var items = sendByLevelFormPanel.down('checkboxgroup[name=level]').items;   
         		for (var i = 0; i < items.length; i++){    
     				if (items.get(i).checked){    
        				levels.push(items.get(i).inputValue);              
     				}    
         		}
			
				Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
					if(e == 'yes'){
						popWin.close();
						Ext.getBody().mask("please wait ..."); 
						Ext.Ajax.request({
				        	url: '<c:url value="/pushMessage/pushMessageByLevel.json"/>',
				         	method: 'post',
							scope: this,
							params:{id: records[0].data.id,levels:Ext.JSON.encode(levels)},
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
				}, this)
			},
			scope: this
		},{
			text: '<fmt:message key="button.cancel"/>',
			handler : function(){popWin.close();},
			scope: this
		}];
      	openWin(Ext.String.format('<fmt:message key="coupon.sender"/>'), sendByLevelFormPanel, buttons, 500, 150);
    },
    
      pushByCustom : function(){
      var records = this.gsm.getSelection();
		if(records.length == 0 ){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}else if(records.length > 1 ){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="user.approve.records"/>');
			return;
		}
		
		var formPanel = Ext.create('Ext.form.Panel', {
		    width: 400,
		    header: false,
		    labelHidder: true,
		    items:[{
                xtype: 'container',
                flex: 1,
                layout: 'anchor',
			    items: [{
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
										        	url: '<c:url value="/pushMessage/pushMessageByCustom.json"/>',
										         	method: 'post',
													scope: this,
													params:{id: records[0].data.id,dataGridJson: Ext.JSON.encode(rowsData), headerGridJson: Ext.JSON.encode(testPanel.keyValueHeaderData)},
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
								openWin('<fmt:message key="batch.push.message"/>', testPanel, importButtons, 750, 450);
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
    
    objectTypeRenderer: function(value, meta, record) { 
       	if(value =="1") {
            return '<b><fmt:message key="user.level1"/></b>';
        }else if(value == '2'){
            return '<fmt:message key="user.level2"/>';
        }else if(value == '5'){
            return '<fmt:message key="user.level.custom"/>';
        }  
   	},
   	
    messageTypeRenderer: function(value, meta, record) { 
           	if(value =="1") {
            return '<b><fmt:message key="system.sendmsg.type.notice"/></b>';
        }else if(value == "2"){
            return '<fmt:message key="system.sendmsg.type.activity"/>';
        }else{
         return '<fmt:message key="system.sendmsg.type.goods"/>';
        } 
   	}
})
