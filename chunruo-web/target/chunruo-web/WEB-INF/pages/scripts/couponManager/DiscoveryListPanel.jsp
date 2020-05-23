<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('Discovery', {
	extend: 'Ext.data.Model',
	idProperty: 'discoveryId',
    fields: [
		{name: 'discoveryId',		mapping: 'discoveryId',		              type: 'int'},
		{name: 'createrId',		    mapping: 'createrId',		              type: 'int'},
		{name: 'type',		        mapping: 'type',		                  type: 'int'},
		{name: 'shareType',		    mapping: 'shareType',		              type: 'int'},
		{name: 'moduleId',	        mapping: 'moduleId',                      type: 'int'},
		{name: 'productId',	        mapping: 'productId',		              type: 'int'},
	    {name: 'videoWidth',	    mapping: 'videoWidth',		              type: 'int'},
	    {name: 'videoHeight',	    mapping: 'videoHeight',		              type: 'int'},
		{name: 'title',	 	        mapping: 'title',                         type: 'string'},
		{name: 'downLoadCount',	    mapping: 'downLoadCount',                 type: 'string'},
		{name: 'viewNumber',	    mapping: 'viewNumber',                    type: 'string'},
		{name: 'likeNumber',	    mapping: 'likeNumber',                    type: 'string'},
		{name: 'shareNumber',	    mapping: 'shareNumber',                   type: 'string'},
		{name: 'content',	 	    mapping: 'content',                       type: 'string'},
		{name: 'imagePath',	     	mapping: 'imagePath',                     type: 'string'},
		{name: 'productName',	    mapping: 'productName',                   type: 'string'},
		{name: 'createrName',	    mapping: 'createrName',                   type: 'string'},
		{name: 'moduleName',	    mapping: 'moduleName',                    type: 'string'},
		{name: 'createTime',	 	mapping: 'createTime',		              type: 'string'},
		{name: 'updateTime',	 	mapping: 'updateTime',		              type: 'string'}
    ]
});

Ext.define('MyExt.couponManager.DiscoveryListPanel', {
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
			model: 'Discovery',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/discovery/list.json"/>',
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
			{text: '<fmt:message key="discovery.discoveryId"/>', dataIndex: 'discoveryId', width: 50, sortable: true, filter: {xtype: 'textfield'}},
			{text: '<fmt:message key="discovery.createrId"/>',   dataIndex: 'createrId',   width: 60, sortable : true, filter: {xtype: 'textfield'}}, 
			{text: '<fmt:message key="discovery.createrName"/>', dataIndex: 'createrName',       width: 260, sortable: true, filter: {xtype: 'textfield'}},
			{text: '<fmt:message key="discovery.moduleId"/>',    dataIndex: 'moduleId',    width: 60, sortable: true,  filter: {xtype: 'textfield'}},
			{text: '<fmt:message key="discovery.moduleName"/>',  dataIndex: 'moduleName',       width: 260, sortable: true, filter: {xtype: 'textfield'}},
			{text: '<fmt:message key="discovery.isSyncProductMaterial"/>', dataIndex: 'isSyncProductMaterial', locked: true, width: 65, sortable : true,
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
        	  {text: '<fmt:message key="discovery.type" />', dataIndex: 'type', width: 100, sortable: true,
            	filter: {
            		xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: Ext.create('Ext.data.Store', {
						autoDestroy: true,
						model: 'InitModel',
						data: [
							{id: '1', name: '<fmt:message key="discovery.type1"/>'},
							{id: '2', name: '<fmt:message key="discovery.type2"/>'},
							{id: '3', name: '<fmt:message key="discovery.type3"/>'},
						]
					}),
			        queryMode: 'local',
			        typeAhead: true
            	},
            	renderer: this.couponTypeRenderer
            },
			{text: '<fmt:message key="discovery.productIds"/>',   dataIndex: 'productIds',   width: 60, sortable: true,  filter: {xtype: 'textfield'}},
        	{text: '<fmt:message key="discovery.title"/>',       dataIndex: 'title',       width: 260, sortable: true, filter: {xtype: 'textfield'}},
        	{text: '<fmt:message key="discovery.content"/>',     dataIndex: 'content',     width: 560, sortable: true,  filter: {xtype: 'textfield'}}, 
			{text: '<fmt:message key="discovery.shareNumber"/>', dataIndex: 'shareNumber',       width: 80, sortable: true, filter: {xtype: 'textfield'}},
			{text: '<fmt:message key="discovery.likeNumber"/>',  dataIndex: 'likeNumber',       width: 80, sortable: true, filter: {xtype: 'textfield'}},
			{text: '<fmt:message key="discovery.viewNumber"/>',  dataIndex: 'viewNumber',       width: 80, sortable: true, filter: {xtype: 'textfield'}},
			{text: '<fmt:message key="discovery.downLoadCount"/>',dataIndex: 'downLoadCount',       width: 80, sortable: true, filter: {xtype: 'textfield'}},
			{text: '<fmt:message key="discovery.createTime"/>',  dataIndex: 'createTime',  width: 150, sortable : true, align: 'center',filter: {xtype: 'textfield'}},
        	{text: '<fmt:message key="discovery.updateTime"/>',  dataIndex: 'updateTime',  width: 150, sortable : true,filter: {xtype: 'textfield'}},
        ];
        
        this.tbar = [
        <jkd:haveAuthorize access="/discovery/list.json">
        {
	    	text: '<fmt:message key="button.refresh"/>', 
	        iconCls: 'refresh', 	
	       	handler: function(){this.store.reload();}, 
	       	scope: this
	  	}
	  	<c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/discovery/saveDiscovery.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
	  	'-', {
        	text: '<fmt:message key="discovery.save"/>', 
        	iconCls: 'enable', 	
        	handler: this.saveDiscovery, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/discovery/deleteDiscovery.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        {
        	text: '<fmt:message key="discovery.delete"/>', 
        	iconCls: 'delete', 	
        	handler: this.deleteDiscovery, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/discovery/syncProductMaterialImage.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        {
        	text: '<fmt:message key="discovery.sync"/>', 
        	iconCls: 'Packageadd', 	
        	handler: this.syncProductMaterialImage, 
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
	    
	     this.menu = Ext.create('Ext.menu.Menu', {
        	items:[{
		       	iconCls: 'add',
				text: '<fmt:message key="discovery.edit.image.text"/>',
				handler: this.addImage,
		    	scope: this
		    }]
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
	    
	    <jkd:haveAuthorize access="/discovery/list.json">
	   	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.productList.filters)
			});
	    }, this);
	    this.store.load();   
	    </jkd:haveAuthorize>
	    
	    this.productList.on('itemdblclick', function(view, record, item, index, e, eOpts) {
	        e.stopEvent();
	    	<jkd:haveAuthorize access="/discovery/getDiscoveryById.json">
	    	this.east.transferData(this.east, record, this.body.dom.clientWidth);
	    	this.east.show();
	    	</jkd:haveAuthorize>
	    }, this); 
	    
	    this.productList.on('itemcontextmenu', function(view, record, item, index, e){
    		e.stopEvent();
    		this.menu.items.each(function(m){
    		    m.hide();
    		    if(record.data.type == 1 || record.data.type == 2){
    		      m.show();
    		    }
			});
			this.menu.record = record;
	    	this.menu.showAt(e.getXY());	
		}, this);
    },
    
    addImage : function(view, record, item, index, e, eOpts){
        console.log(this.id);
    	var ueditorId = Ext.String.format('ueditor-{0}', 'ext-comp-1143');
    	var type = this.menu.record.data.type;
    	var componentEdit = Ext.create('Ext.Component', {
    		autoScroll: true,
            region: 'east',
            border: true,
            header: false,
            scope : this,  
            style:"background: #fff;",
            html: Ext.String.format('<div id="{0}" sytle="background: #fff;"></div>', ueditorId),
            listeners: {
            	scope : this,  
                boxready: function (t, layout, opts) {
                	try{
                		customField.init(ueditorId,3,type);
                		sendAjax.get('discovery/getArticleContentById.msp', {
             				discoveryId: this.menu.record.data.discoveryId
         				}, function(res) {
             				if (res.code == 1) {
                 				customField.setHtml(res.data, 0,0,3,type);
             				}
        				});
					}catch(e){}
				}
            }
    	});
    	
    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
				Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
					if(e == 'yes'){
						Ext.Ajax.request({
				        	url: '<c:url value="/discovery/saveDiscoveryArticle.msp"/>',
				         	method: 'post',
							scope: this,
							params:{discoveryId: this.menu.record.data.discoveryId, custom: customField.getData()},
				          	success: function(response){
		          				var responseObject = Ext.JSON.decode(response.responseText);
		                        if(responseObject.code == 1){
		                       		showSuccMsg(responseObject.msg);
		                       		popWin.close();
		                        	this.store.loadPage(1);
								}else{
									showFailMsg(responseObject.msg, 4);
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
		var height = document.body.clientHeight;
      	openWin(this.menu.record.data.title, componentEdit, buttons, 870, height);
    },
    
    
    saveDiscovery : function(){
    	var discoveryFormPanel = Ext.create('MyExt.couponManager.DiscoveryFormPanel', {
			id: 'add@discoveryFormPanel' + this.id,
    		viewer: this.viewer,
   	 	});
    	
    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
		    	var rowsData = [];    
		    	var tagNameDatas = [];
		       	discoveryFormPanel.down('imagepanel').store.each(function(record) {
		       		record.data.input_file = null;
		            rowsData.push(record.data);    
		      	}, this);
		      	
		      	var rowsDatas = [];    
		    	var tagNameDatas = [];
		       	discoveryFormPanel.down('imagepanels').store.each(function(record) {
		       		record.data.input_file = null;
		            rowsDatas.push(record.data);    
		      	}, this);
		      
		    	if(discoveryFormPanel.form.isValid()){
					Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
		             		discoveryFormPanel.form.submit({
		                 		waitMsg: 'Loading...',
		                 		url: '<c:url value="/discovery/saveDiscovery.json"/>',
		               			scope: this,
		               			params:{recodeGridJson: Ext.JSON.encode(rowsData),videoJson: Ext.JSON.encode(rowsDatas)},
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
      	openWin('<fmt:message key="button.add"/>', discoveryFormPanel, buttons,600, 600);
    },    
    
    deleteDiscovery : function() {
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	

		for(var i = 0; i < records.length; i++){	
			rowsData.push(records[i].data.discoveryId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm.agree"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/discovery/deleteDiscovery.json"/>',
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
    
    syncProductMaterialImage : function(){
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}else if(records.length > 1){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.only.one.record"/>');
			return;
		}	
		
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.discoveryId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="discovery.sync.product.material.image"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/discovery/syncProductMaterialImage.json"/>',
		         	method: 'post',
					scope: this,
					params:{discoveryId: records[0].data.discoveryId},
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
            return '<b><fmt:message key="discovery.type1"/></b>';
         }else if(val == 2){
            return '<b><fmt:message key="discovery.type2"/></b>';
         }else if(val == 3){
            return '<b><fmt:message key="discovery.type3"/></b>';
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
	
	rendererStuts : function(val){
		if(val == true) {
            return '<b><fmt:message key="button.yes"/></b>';
        }else{
            return '<fmt:message key="button.no"/>';
        }
	},
});