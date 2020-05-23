<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('Coupon', {
	extend: 'Ext.data.Model',
	idProperty: 'couponId',
    fields: [
		{name: 'couponId',			mapping: 'couponId',		type: 'int'},
		{name: 'couponName',		mapping: 'couponName',		type: 'string'},
		{name: 'totalCount',		mapping: 'totalCount',		type: 'string'},
		{name: 'usedCount',	 		mapping: 'usedCount',		type: 'string'},
		{name: 'couponType',	 	mapping: 'couponType',		type: 'string'},
		{name: 'receiveType',	 	mapping: 'receiveType',		type: 'string'},
		{name: 'fullAmount',		mapping: 'fullAmount',		type: 'string'},
		{name: 'giveAmount',		mapping: 'giveAmount',		type: 'string'},
		{name: 'remark',		    mapping: 'remark',		    type: 'string'},
		{name: 'attribute',	 		mapping: 'attribute',		type: 'string'},
		{name: 'attributeContent',	mapping: 'attributeContent',type: 'string'},
		{name: 'receiveBeginTime',	mapping: 'receiveBeginTime',type: 'string'},
		{name: 'receiveEndTime',	mapping: 'receiveEndTime',	type: 'string'},
		{name: 'effectiveTime',		mapping: 'effectiveTime',	type: 'string'},
		{name: 'sender',			mapping: 'sender',			type: 'string'},
		{name: 'adminUserName',		mapping: 'adminUserName',	type: 'string'},
		{name: 'useRangeType',	    mapping: 'useRangeType',	type: 'string'},
		{name: 'isEnable',			mapping: 'isEnable',		type: 'bool'},
		{name: 'isGiftCoupon',	    mapping: 'isGiftCoupon',    type: 'bool'},
		{name: 'title',	        	mapping: 'title',			type: 'string'},
		{name: 'bindNames',	 		mapping: 'bindNames',		type: 'string'},
		{name: 'createTime',	 	mapping: 'createTime',		type: 'string'},
		{name: 'updateTime',	 	mapping: 'updateTime',		type: 'string'}
    ]
});

Ext.define('MyExt.couponManager.CouponListPanel', {
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
			model: 'Coupon',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/coupon/couponList.json"/>',
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
		
		this.useRangeTypeStore= Ext.create('Ext.data.Store', {
			autoDestroy: true,
			model: 'InitModel',
			data: [
			    {id: '1', name: '<fmt:message key="coupon.useRangeType1"/>'},
				{id: '2', name: '<fmt:message key="coupon.useRangeType2"/>'},
			    
			]
		});
        
        this.attributeStore= Ext.create('Ext.data.Store', {
			autoDestroy: true,
			model: 'InitModel',
			data: [
			    {id: '0', name: '<fmt:message key="coupon.receiveType0"/>'},
				{id: '1', name: '<fmt:message key="coupon.receiveType1"/>'},
				{id: '2', name: '<fmt:message key="coupon.receiveType2"/>'},
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
			{text: '<fmt:message key="coupon.couponId"/>', dataIndex: 'couponId', width: 65, sortable: true, locked: true, filter: {xtype: 'textfield'}},
			{text: '<fmt:message key="coupon.couponName"/>', dataIndex: 'couponName', width: 150, sortable: true, locked: true, filter: {xtype: 'textfield'}},
        	{text: '<fmt:message key="coupon.totalCount"/>', dataIndex: 'totalCount', width: 80, sortable: true, locked: true, filter: {xtype: 'textfield'}},
        	{text: '<fmt:message key="coupon.usedCount"/>', dataIndex: 'usedCount', width: 77, sortable: true, locked: true, filter: {xtype: 'textfield'}}, 
        	{text: '<fmt:message key="coupon.couponType"/>', dataIndex: 'couponType', width: 77, sortable: true, locked: true,
        		renderer :this.couponTypeRenderer,
        		filter: {
					xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.couponTypeStore,
			        queryMode: 'local',
			        typeAhead: true
				}
		    }, 
		    {text: '<fmt:message key="coupon.isEnable"/>', dataIndex: 'isEnable', width: 80, locked: true,  align: 'center', sortable : true,
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
			{text: '<fmt:message key="coupon.isGiftCoupon"/>', dataIndex: 'isGiftCoupon', width: 80, locked: true,  align: 'center', sortable : true,
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
            {text: '<fmt:message key="coupon.useRangeType"/>', dataIndex: 'useRangeType', width: 160, sortable : true,
            	renderer :this.useRangeTypeRenderer,
            	filter: {
					xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.useRangeTypeStore,
			        queryMode: 'local',
			        typeAhead: true
				}
            },
             {text: '<fmt:message key="coupon.receiveType"/>', dataIndex: 'receiveType', width: 80, sortable : true,
            	renderer :this.receiveTypeRenderer,
            	filter: {
					xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.receiveTypeStore,
			        queryMode: 'local',
			        typeAhead: true
				}
            },
			{text: '<fmt:message key="coupon.fullAmount"/>', dataIndex: 'fullAmount', width: 100, sortable : true, align: 'center',filter: {xtype: 'textfield'}},
        	{text: '<fmt:message key="coupon.giveAmount"/>', dataIndex: 'giveAmount', width: 120, sortable : true,filter: {xtype: 'textfield'}},
        	{text: '<fmt:message key="coupon.attribute"/>', dataIndex: 'attribute', width: 77, sortable : true,
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
        	{text: '<fmt:message key="coupon.remark"/>', dataIndex: 'remark', width: 150, sortable : true, filter: {xtype: 'textfield'}}, 
        	{text: '<fmt:message key="coupon.attributeContent"/>', dataIndex: 'attributeContent', width: 150, sortable : true, filter: {xtype: 'textfield'}}, 
        	{text: '<fmt:message key="coupon.receiveBeginTime"/>', dataIndex: 'receiveBeginTime', width: 100, sortable : true, filter: {xtype: 'textfield'}},
			{text: '<fmt:message key="coupon.receiveEndTime"/>', dataIndex: 'receiveEndTime', width: 100, sortable : true, align: 'center', filter: {xtype: 'textfield'}},
        	{text: '<fmt:message key="coupon.effectiveTime"/>', dataIndex: 'effectiveTime', width: 120, sortable : true, filter: {xtype: 'textfield'}},
        	{text: '<fmt:message key="coupon.sender"/>', dataIndex: 'sender', width: 120, sortable : true, filter: {xtype: 'textfield'}},
        	{text: '<fmt:message key="coupon.title"/>', dataIndex: 'title', width: 77, sortable : true,filter: {xtype: 'textfield'}}, 
        	{text: '<fmt:message key="coupon.bindNames"/>', dataIndex: 'bindNames', width: 77, sortable : true,filter: {xtype: 'textfield'}}, 
			{text: '<fmt:message key="coupon.adminUserName"/>', dataIndex: 'adminUserName', width: 100, sortable : true, align: 'center',filter: {xtype: 'textfield'}},
			{text: '<fmt:message key="coupon.createTime"/>', dataIndex: 'createTime', width: 120, sortable : true, align: 'center',filter: {xtype: 'textfield'}},
        	{text: '<fmt:message key="coupon.updateTime"/>', dataIndex: 'updateTime', width: 120, sortable : true,filter: {xtype: 'textfield'}},
        ];
        
        this.tbar = [
        <jkd:haveAuthorize access="/coupon/couponList.json">
        {
	    	text: '<fmt:message key="button.refresh"/>', 
	        iconCls: 'refresh', 	
	       	handler: function(){this.store.reload();}, 
	       	scope: this
	  	}
	  	<c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/coupon/saveCoupon.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
	  	'-',{
        	text: '<fmt:message key="button.add"/>', 
        	iconCls: 'add', 	
        	handler: this.saveCoupon,
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/coupon/setSendStatus.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
        	text: '<fmt:message key="coupon.sendCoupon.byLevel"/>', 
        	iconCls: 'enable', 	
        	handler: this.sendCouponByLevel, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/coupon/setSendStatusByExcel.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
        	text: '<fmt:message key="coupon.sendCoupon.byExcel"/>', 
        	iconCls: 'enable', 	
        	handler: this.sendCouponByExcel, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/coupon/setCouponEnabled.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
        	text: '<fmt:message key="button.enable"/>', 
        	iconCls: 'add', 	
        	handler: this.setCouponEnabled, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/coupon/setCouponTimeOut.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
        	text: '<fmt:message key="button.disable"/>', 
        	iconCls: 'delete', 	
        	handler: this.disableCoupon, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/coupon/setGiftCoupon.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
        	text: '<fmt:message key="coupon.set.gift"/>', 
        	iconCls: 'add', 	
        	handler: this.setGiftCoupon, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/coupon/exportCoupon.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '->',{
        	text: '<fmt:message key="button.exporter.coupon.xls"/>', 
        	iconCls: 'excel', 	
        	handler: this.batchExport,
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
	    
	    this.east =  Ext.create('MyExt.couponManager.CouponTabPanel', {
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
	    
	    <jkd:haveAuthorize access="/coupon/couponList.json">
	   	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.productList.filters)
			});
	    }, this);
	    this.store.load();   
	    </jkd:haveAuthorize>
	    
	    this.productList.on('itemdblclick', function(view, record, item, index, e, eOpts) {
	    	<jkd:haveAuthorize access="/coupon/getCouponById.json">
	    	this.east.transferData(this.east, record, this.body.dom.clientWidth);
	    	this.east.show();
	    	</jkd:haveAuthorize>
	    }, this); 
    },
    
    saveCoupon : function(){
    	var couponFormPanel = Ext.create('MyExt.couponManager.CouponFormPanel', {
			id: 'couponFormPanel@' + this.id,
    		viewer: this.viewer,
   	 	});
    	
    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
		    	//var rowsDatas = couponFormPanel.down('selectType').getValue();  
		    	var rowsDatas = Ext.getCmp('selectType').getValue();
		    	if(couponFormPanel.form.isValid()){
					Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
		             		couponFormPanel.form.submit({
		                 		waitMsg: 'Loading...',
		                 		url: '<c:url value="/coupon/saveCoupon.json"/>',
		               			scope: this,
		               			params:{recodeGridJson: Ext.JSON.encode(rowsDatas)},
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
      	openWin('<fmt:message key="coupon.add"/>', couponFormPanel, buttons, 800, 400);
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
    
    batchExport : function(){
		var couponExportExcel = Ext.create('MyExt.couponManager.CouponExportExcel', {id: 'couponExportExcel@CouponExportExcel', title: '<fmt:message key="button.add"/>'});
		var buttons = [{
			text: '<fmt:message key="button.batch.exporter.xls"/>',
			handler: function(){
	            if(couponExportExcel.form.isValid()){
	            	Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="order.export.confirm"/>', function(e){
						if(e == 'yes'){
						var formValues=couponExportExcel.getForm().getValues();
							var begin = Ext.util.Format.date(Ext.getCmp('beginTime').getValue(), 'Y-m-d H:i:s');
							var end = Ext.util.Format.date(Ext.getCmp('endTime').getValue(), 'Y-m-d H:i:s');
							var couponId = Ext.getCmp('couponId').getValue();
							window.location.href = "/coupon/exportCoupon.json?beginTime="+begin+"&endTime="+end+"&couponId="+couponId;
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
		openWin('<fmt:message key="button.exporter.xls"/>', couponExportExcel, buttons, 400, 200);
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
				}, this)
			},
			scope: this
		},{
			text: '<fmt:message key="button.cancel"/>',
			handler : function(){popWin.close();},
			scope: this
		}];
      	openWin(Ext.String.format('<fmt:message key="coupon.sender"/>'), sendByLevelFormPanel, buttons, 300, 160);
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
		    height:300,
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
		    	},
		    	{
			        xtype: 'numberfield',
			        name: 'number',
			        msgTarget: 'side',
			        allowBlank: false,
			        anchor: '100%',
			        emptyText:'<fmt:message key="coupon.send.number"/>'
		    	}]
			}]
		});
		
		var buttons = [{
			text: '<fmt:message key="button.confirm"/>',
			handler : function(){
				var formValues = formPanel.getValues();
				console.log(formValues.number);
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
													params:{dataGridJson: Ext.JSON.encode(rowsData), headerGridJson: Ext.JSON.encode(testPanel.keyValueHeaderData), couponId: couponId,number:formValues.number},
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
		openFormWin('<fmt:message key="user.batch.register"/>', formPanel, buttons, 420, 160);
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
   	
   	setGiftCoupon : function(){
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
		        	url: '<c:url value="/coupon/setGiftCoupon.json"/>',
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
   	
   	useRangeTypeRenderer: function(val){
  	    if(val == 1) {
           return '<b><fmt:message key="coupon.useRangeType1"/></b>';
        }else if(val == 2){
           return '<b><fmt:message key="coupon.useRangeType2"/></b>';
        }
   	},
   	
   	attributeRenderer : function(val){
   	    if(val == 1) {
            return '<b><fmt:message key="coupon.attribute1"/></b>';
         }else if(val == 2){
            return '<b><fmt:message key="coupon.attribute2"/></b>';
         }else if(val == 3){
            return '<b><fmt:message key="coupon.attribute3"/></b>';
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