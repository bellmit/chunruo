<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('CouponTask', {
	extend: 'Ext.data.Model',
	idProperty: 'taskId',
    fields: [
		{name: 'taskId',		mapping: 'taskId',			type: 'int'},
		{name: 'taskName',		mapping: 'taskName',		type: 'string'},
		{name: 'taskStatus',	mapping: 'taskStatus',		type: 'string'},
		{name: 'couponId',	 	mapping: 'couponId',		type: 'string'},
		{name: 'coupon.couponName',	 mapping: 'coupon.couponName', type: 'string'},
		{name: 'taskContent',	mapping: 'taskContent',		type: 'string'},
		{name: 'createTime',	mapping: 'createTime',		type: 'string'},
		{name: 'updateTime',	mapping: 'updateTime',		type: 'string'}
    ]
});

Ext.define('MyExt.couponManager.CouponTaskListPanel', {
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
			model: 'CouponTask',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/coupon/couponTaskList.json"/>',
				reader: {
					type : 'json',
                	root: 'data',
                	totalProperty: 'totalCount'
            	}
			},
			sorters: [{
	            property: 'taskId',
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
        
        this.taskStatusStore = Ext.create('Ext.data.Store', {
			autoDestroy: true,
			model: 'InitModel',
			data: [
				{id: '0', name: '<fmt:message key="coupon.taskStatus0"/>'},
				{id: '1', name: '<fmt:message key="coupon.taskStatus1"/>'},
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
			    {id: '1', name: '<fmt:message key="coupon.attribute1"/>'},
				{id: '2', name: '<fmt:message key="coupon.attribute2"/>'},
				{id: '3', name: '<fmt:message key="coupon.attribute3"/>'},
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
			{text: '<fmt:message key="coupon.taskId"/>', dataIndex: 'taskId', width: 65, sortable : true,filter: {xtype: 'textfield'}},
			{text: '<fmt:message key="coupon.taskStatus"/>', dataIndex: 'taskStatus', width: 60, align: 'center', sortable: true,
        		renderer :this.taskStatusRenderer,
        		filter: {
					xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.taskStatusStore,
			        queryMode: 'local',
			        typeAhead: true
				}
		    },
			{text: '<fmt:message key="coupon.taskName"/>', dataIndex: 'taskName', width: 200, sortable: true, filter: {xtype: 'textfield'}},
        	{text: '<fmt:message key="coupon.couponId"/>', dataIndex: 'couponId', width: 77, sortable : true,filter: {xtype: 'textfield'}}, 
        	{text: '<fmt:message key="coupon.couponName"/>', dataIndex: 'coupon.couponName', width: 77, sortable : true,filter: {xtype: 'textfield'}}, 
        	{text: '<fmt:message key="coupon.taskContent"/>', dataIndex: 'taskContent', flex: 1, sortable : true,filter: {xtype: 'textfield'}}, 
			{text: '<fmt:message key="coupon.createTime"/>', dataIndex: 'createTime', width: 140, sortable : true, align: 'center',filter: {xtype: 'textfield'}},
        	{text: '<fmt:message key="coupon.updateTime"/>', dataIndex: 'updateTime', width: 140, sortable : true,filter: {xtype: 'textfield'}},
        ];
        
        this.tbar = [
        <jkd:haveAuthorize access="/coupon/couponTaskList.json">
        {
	    	text: '<fmt:message key="button.refresh"/>', 
	        iconCls: 'refresh', 	
	       	handler: function(){this.store.reload();}, 
	       	scope: this
	  	}
	  	<c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/coupon/setTaskStatus.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
	  	'-',{
        	text: '<fmt:message key="coupon.taskStatus1"/>', 
        	iconCls: 'enable', 	
        	handler: this.setTaskStatusEnable,
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/coupon/setTaskStatus.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
        	text: '<fmt:message key="coupon.taskStatus0"/>', 
        	iconCls: 'delete', 	
        	handler: this.setTaskStatusStop, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/coupon/saveCoupon.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
        	text: '<fmt:message key="coupon.edit"/>', 
        	iconCls: 'Chartpieadd', 	
        	handler: this.edit, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/coupon/sendCouponToVIP1.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
            text: '<fmt:message key="coupo.sendCoupon"/>', 
        	iconCls: 'add', 	
        	handler: this.sendCoupon, 
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
	    
	    <jkd:haveAuthorize access="/coupon/couponTaskList.json">
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
    
    setTaskStatusEnable : function(){
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		if(records.length >1 ){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="coupon.sendStatus.bigCount"/>');
			return;
		}	
		
		var taskId = records[0].data.taskId;
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="coupon.sendStatus.enable"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/coupon/setTaskStatus.json"/>',
		         	method: 'post',
					scope: this,
					params:{taskId: taskId,status : 1},
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
   	
   	setTaskStatusStop : function(){
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		if(records.length >1 ){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="coupon.sendStatus.bigCount"/>');
			return;
		}	
		
		var taskId = records[0].data.taskId;
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="coupon.sendStatus.stop"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/coupon/setTaskStatus.json"/>',
		         	method: 'post',
					scope: this,
					params:{taskId: taskId,status : 0},
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
    
    edit : function(){
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		if(records.length >1 ){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="coupon.sendStatus.bigCount"/>');
			return;
		}	
		
		var taskId=records[0].data.taskId;
    	var couponTaskEditFormPanel = Ext.create('MyExt.couponManager.CouponTaskEditFormPanel', {
			id: 'couponTaskEditFormPanel@' + this.id,
    		viewer: this.viewer,
   	 	});
    	
    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
				var rowsDatas = Ext.getCmp('selectType').getValue();  
		    	if(couponTaskEditFormPanel.form.isValid()){
					Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
		             		couponTaskEditFormPanel.form.submit({
		                 		waitMsg: 'Loading...',
		                 		url: '<c:url value="/coupon/saveCoupon.json"/>',
		               			scope: this,
		               			params:{recodeGridJson: Ext.JSON.encode(rowsDatas),taskId : taskId},
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
      	openWin('<fmt:message key="product.wholesale.add"/>', couponTaskEditFormPanel, buttons, 800, 400);
    },
    
    sendCoupon : function(){
    	var sendCouponFormPanel = Ext.create('MyExt.couponManager.SendCouponFormPanel', {
			id: 'sendCouponFormPanel@' + this.id,
    		viewer: this.viewer,
   	 	});
    	
    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
		    	if(sendCouponFormPanel.form.isValid()){
					Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
		             		sendCouponFormPanel.form.submit({
		                 		waitMsg: 'Loading...',
		                 		url: '<c:url value="/coupon/sendCouponToVIP1.json"/>',
		               			scope: this,
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
      	openWin('<fmt:message key="product.wholesale.add"/>', sendCouponFormPanel, buttons, 400, 300);
    },
   	
   	fontRenderer: function(value, meta, record) {
    	meta.style = 'overflow:auto;padding: 3px 6px;text-overflow: ellipsis;white-space: nowrap;white-space:normal;line-height:24px;font-weight:bold;';      
       	return value;     
   	},
   	
   	taskStatusRenderer : function(val){
   	    if(val == 0) {
            return '<b><fmt:message key="coupon.taskStatus0"/></b>';
         }else if(val == 1){
            return '<b><fmt:message key="coupon.taskStatus1"/></b>';
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
   	    if(val == 1) {
            return '<b><fmt:message key="coupon.attribute1"/></b>';
         }else if(val == 2){
            return '<b><fmt:message key="coupon.attribute2"/></b>';
         }else if(val == 3){
            return '<b><fmt:message key="coupon.attribute3"/></b>';
         }
   	},
});