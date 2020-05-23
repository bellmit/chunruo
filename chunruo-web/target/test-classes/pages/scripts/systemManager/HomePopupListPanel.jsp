<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>


Ext.define('HomePopup', {
	extend: 'Ext.data.Model',
	idProperty: 'popupId',
    fields: [
		{name: 'popupId',			mapping: 'popupId',		    type: 'int'},
		{name: 'pushLevel',		    mapping: 'pushLevel',		type: 'int'},
		{name: 'productId',		    mapping: 'productId',		type: 'int'},
		{name: 'isInvitePage',		mapping: 'isInvitePage',    type: 'bool'},
		{name: 'type',		        mapping: 'type',		    type: 'int'},
		{name: 'jumpPageType',		mapping: 'jumpPageType',    type: 'int'},
		{name: 'imageUrl',	 		mapping: 'imageUrl',		type: 'string'},
		{name: 'webUrl',	 	    mapping: 'webUrl',		    type: 'string'},
		{name: 'beginTime',	 	    mapping: 'beginTime',		type: 'string'},
		{name: 'endTime',	 	    mapping: 'endTime',		    type: 'string'},
		{name: 'isEnable',			mapping: 'isEnable',		type: 'bool'},
		{name: 'createTime',	 	mapping: 'createTime',		type: 'string'},
		{name: 'updateTime',	 	mapping: 'updateTime',		type: 'string'}
    ]
});

Ext.define('MyExt.systemManager.HomePopupListPanel', {
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
	        autoLoad: false,
			autoDestroy: true,
			sortOnLoad: true,
			remoteSort: true,
			model: 'HomePopup',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/homePopup/list.json?type=0"/>',
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
		
		this.rendererPushLevelStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: 1, name: '<fmt:message key="user.pushLevel1"/>'},
        		{id: 2, name: '<fmt:message key="user.higher.pushLevel"/>'},
        		{id: '3', name: '<fmt:message key="user.level1"/>'},
				{id: '4', name: '<fmt:message key="user.level2"/>'}
        	]
        });
          
        this.renderertypeStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: 0, name: '<fmt:message key="home.popup.type0"/>'},
        		{id: 1, name: '<fmt:message key="home.popup.type1"/>'},
        	]
       	});
       	
        this.rendererjumptypeStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: 0, name: '<fmt:message key="home.popup.jump.type0"/>'},
        		{id: 1, name: '<fmt:message key="home.popup.jump.type1"/>'},
        		{id: 2, name: '<fmt:message key="home.popup.jump.type2"/>'},
        		{id: '3', name: '<fmt:message key="link.type.theme"/>'},
				{id: '6', name: '<fmt:message key="link.type.award"/>'},
				{id: '7', name: '<fmt:message key="link.type.mini"/>'},
				{id: '8', name: '<fmt:message key="link.type.web"/>'},
				{id: '9', name: '<fmt:message key="discovery.detail"/>'},
				{id: '10', name: '<fmt:message key="discovery.module.topic"/>'},
				{id: '11', name: '<fmt:message key="discovery.creater"/>'},
				{id: '13', name: '<fmt:message key="product.brand.detail"/>'},
				{id: '14', name: '<fmt:message key="invite.page"/>'},
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
			{text: '<fmt:message key="home.popup.popupId"/>', dataIndex: 'popupId', width: 65, sortable: true, filter: {xtype: 'textfield'}},
           	{text: '<fmt:message key="home.popup.productId"/>', dataIndex: 'productId', width: 65, sortable: true, filter: {xtype: 'textfield'}},
            {text: '<fmt:message key="home.popup.pushLevel"/>', dataIndex: 'pushLevel', width: 160, sortable : true, 
        		renderer : this.rendererPushLevelStuts,
        		filter: {
					xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.rendererPushLevelStore,
			        queryMode: 'local',
			        typeAhead: true
				}
        	 },   
        	 {text: '<fmt:message key="home.popup.type"/>', dataIndex: 'type', width: 160, sortable : true, 
        		renderer : this.renderertypeStuts,
        		filter: {
					xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.renderertypeStore,
			        queryMode: 'local',
			        typeAhead: true
				}
        	 },    
        	 {text: '<fmt:message key="home.popup.jump.type"/>', dataIndex: 'jumpPageType', width: 160, sortable : true, 
        		renderer : this.rendererjumptypeStuts,
        		filter: {
					xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.rendererjumptypeStore,
			        queryMode: 'local',
			        typeAhead: true
				}
        	 },       
        	 {
            	text: '<fmt:message key="home.popup.image"/>',
                menuDisabled: true,
                sortable: false,
                xtype: 'actioncolumn',
                align: 'center',
                width: 160,
                higth: 80,
                dataIndex: 'imageUrl',
                renderer: function(value, metadata, record) {
        			return Ext.String.format('<img height="60" width="60" src="{0}"></img>', value);
    			}
            }, 
            {text: '<fmt:message key="home.popup.isInvitePage"/>', dataIndex: 'isInvitePage', width: 120,  align: 'center', sortable : true,
		    	renderer: this.rendererStuts,
	        	filter: {
					xtype: 'combobox',
				    displayField: 'name',
				    valueField: 'id',
				   	store: this.isEnableStore,
				   	queryMode: 'local',
				    typeAhead: true
				}
			},
		    {text: '<fmt:message key="home.popup.isEnable"/>', dataIndex: 'isEnable', width: 120,  align: 'center', sortable : true,
		    	renderer: this.rendererStuts,
	        	filter: {
					xtype: 'combobox',
				    displayField: 'name',
				    valueField: 'id',
				   	store: this.isEnableStore,
				   	queryMode: 'local',
				    typeAhead: true
				}
			},
        	{text: '<fmt:message key="home.popup.beginTime"/>', dataIndex: 'beginTime', width: 160, sortable : true, filter: {xtype: 'textfield'}},
        	{text: '<fmt:message key="home.popup.endTime"/>', dataIndex: 'endTime', width: 160, sortable : true,filter: {xtype: 'textfield'}}, 
			{text: '<fmt:message key="home.popup.createTime"/>', dataIndex: 'createTime', width: 120, sortable : true, align: 'center',filter: {xtype: 'textfield'}},
        	{text: '<fmt:message key="home.popup.updateTime"/>', dataIndex: 'updateTime', width: 120, sortable : true,filter: {xtype: 'textfield'}},
        ];
        
        this.tbar = [
        <jkd:haveAuthorize access="/homePopup/list.json">
        {
	    	text: '<fmt:message key="button.refresh"/>', 
	        iconCls: 'refresh', 	
	       	handler: function(){this.store.reload();}, 
	       	scope: this
	  	}
	  	<c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/homePopup/saveHomePopup.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
	  	'-',{
        	text: '<fmt:message key="button.add"/>', 
        	iconCls: 'add', 	
        	handler: this.saveHomePopup,
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/homePopup/deleteHomePopup.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
        	text: '<fmt:message key="button.delete"/>', 
        	iconCls: 'delete', 	
        	handler: this.deleteHomePopup, 
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
	    
	    this.east =  Ext.create('MyExt.systemManager.HomePopupTabPanel', {
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
	    
	    <jkd:haveAuthorize access="/homePopup/list.json">
	   	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.productList.filters)
			});
	    }, this);
	    this.store.load();  
	    </jkd:haveAuthorize> 
	    
	    this.productList.on('itemdblclick', function(view, record, item, index, e, eOpts) {
	    	<jkd:haveAuthorize access="/homePopup/getHomePopupById.json,/homePopup/saveHomePopup.json">
	    	this.east.transferData(this.east, record, this.body.dom.clientWidth);
	    	this.east.show();
	    	</jkd:haveAuthorize> 
	    }, this); 
    },
    
    saveHomePopup : function(){
    	var homePopupFormPanel = Ext.create('MyExt.systemManager.HomePopupFormPanel', {
			id: 'homePopupFormPanel@' + this.id,
    		viewer: this.viewer,
   	 	});
    	
    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
			var levels = [];
			var items = homePopupFormPanel.down('checkboxgroup[name=level]').items;   
         	for (var i = 0; i < items.length; i++){    
             		if (items.get(i).checked){    
                			levels.push(items.get(i).inputValue);              
             		}    
         	}
         	
		    	if(homePopupFormPanel.form.isValid()){
			    	var rowsDatas = [];
			       	homePopupFormPanel.down('imagepanel').store.each(function(record) {
			       		record.data.input_file = null;
			            rowsDatas.push(record.data);    
			      	}, this);
			      	
					Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
		             		homePopupFormPanel.form.submit({
		                 		waitMsg: 'Loading...',
		                 		url: '<c:url value="/homePopup/saveHomePopup.json"/>',
		               			scope: this,
		               			params:{recodeGridJson: Ext.JSON.encode(rowsDatas),level:Ext.JSON.encode(levels)},
		               			success: function(form, action) {
		                   			var responseObject = Ext.JSON.decode(action.response.responseText);
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
		 		}
			},
			scope: this
		},{
			text: '<fmt:message key="button.cancel"/>',
			handler : function(){popWin.close();},
			scope: this
		}];
      	openWin('<fmt:message key="home.popup.add"/>', homePopupFormPanel, buttons, 800, 400);
    },
    
     deleteHomePopup : function() {
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
		        	url: '<c:url value="/homePopup/deleteHomePopup.json"/>',
		         	method: 'post',
					scope: this,
					params:{popupId : records[0].data.popupId},
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
   	
   	rendererPushLevelStuts : function(val){
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
   	
   	rendererPushLevelStuts: function(val){
  	    if(val == 1) {
           return '<b><fmt:message key="user.pushLevel1"/></b>';
        }else if(val == 2){
           return '<b><fmt:message key="user.higher.pushLevel"/></b>';
        }else if(val == 3){
           return '<b><fmt:message key="user.level1"/></b>';
        }else if(val == 4){
           return '<b><fmt:message key="user.level2"/></b>';
        }
   	},
   	
   	renderertypeStuts : function(val){
   	    if(val == 0) {
            return '<b><fmt:message key="home.popup.type0"/></b>';
         }else if(val == 1){
            return '<b><fmt:message key="home.popup.type1"/></b>';
         }
   	},
   	
   rendererjumptypeStuts : function(val){
   	    if(val == 0) {
            return '<b><fmt:message key="home.popup.jump.type0"/></b>';
         }else if(val == 1){
            return '<b><fmt:message key="home.popup.jump.type1"/></b>';
         }else if(val == 2){
            return '<b><fmt:message key="home.popup.jump.type2"/></b>';
         }else if(val == 3){
            return '<b><fmt:message key="link.type.theme"/></b>';
         }else if(val == 6){
            return '<b><fmt:message key="link.type.award"/></b>';
         }else if(val == 7){
            return '<b><fmt:message key="link.type.mini"/></b>';
         }else if(val == 8){
            return '<b><fmt:message key="link.type.web"/></b>';
         }else if(val == 9){
            return '<b><fmt:message key="discovery.detail"/></b>';
         }else if(val == 10){
            return '<b><fmt:message key="discovery.module.topic"/></b>';
         }else if(val == 11){
            return '<b><fmt:message key="discovery.creater"/></b>';
         }else if(val == 13){
           return '<b><fmt:message key="product.brand.detail"/></b>';
         }else if(val == 14){
           return '<b><fmt:message key="invite.page"/></b>';
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