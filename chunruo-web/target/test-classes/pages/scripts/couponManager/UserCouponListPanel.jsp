<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('UserCoupon', {
	extend: 'Ext.data.Model',
	idProperty: 'userCouponId',
    fields: [
		{name: 'userCouponId',		mapping: 'userCouponId',		type: 'int'},
		{name: 'couponId',			mapping: 'couponId',			type: 'int'},
		{name: 'coupon.couponName',	mapping: 'coupon.couponName',   type: 'string'},
		{name: 'userId',			mapping: 'userId',				type: 'int'},
		{name: 'mobile',			mapping: 'mobile',				type: 'string'},
		{name: 'nickName',			mapping: 'nickName',		    type: 'string'},
		{name: 'couponNo',	 		mapping: 'couponNo',			type: 'string'},
		{name: 'couponTaskId',	 	mapping: 'couponTaskId',		type: 'string'},
		{name: 'couponStatus',	 	mapping: 'couponStatus',		type: 'string'},
		{name: 'isGiftCoupon',      mapping: 'isGiftCoupon',        type: 'bool'},
		{name: 'receiveTime',		mapping: 'receiveTime',			type: 'string'},
		{name: 'effectiveTime',		mapping: 'effectiveTime',		type: 'string'},
		{name: 'createTime',		mapping: 'createTime',			type: 'string'},
		{name: 'updateTime',	 	mapping: 'updateTime',			type: 'string'},
    ]
});

Ext.define('MyExt.couponManager.UserCouponListPanel', {
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
			model: 'UserCoupon',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/coupon/userCouponList.json"/>',
				reader: {
					type : 'json',
                	root: 'data',
                	totalProperty: 'totalCount'
            	}
			},
			sorters: [{
	            property: 'userCouponId',
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
		this.isEnableStore= Ext.create('Ext.data.Store', {
			autoDestroy: true,
			model: 'InitModel',
			data: [
        		{id: 1, name: '<fmt:message key="button.yes"/>'},
        		{id: 0, name: '<fmt:message key="button.no"/>'}
        	]
		});
        
        this.couponStatusStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        	    {id: -2, name: '<fmt:message key="userCoupon.couponStatus-2"/>'},
        	    {id: -1, name: '<fmt:message key="userCoupon.couponStatus-1"/>'},
        		{id: 0, name: '<fmt:message key="userCoupon.couponStatus0"/>'},
        		{id: 1, name: '<fmt:message key="userCoupon.couponStatus1"/>'},
        		{id: 2, name: '<fmt:message key="userCoupon.couponStatus2"/>'},
        	]
        });
        
		this.columns = [
			{text: '<fmt:message key="userCoupon.userCouponId"/>', dataIndex: 'userCouponId', width: 65, sortable : true,filter: {xtype: 'textfield'}},
			{text: '<fmt:message key="userCoupon.couponId"/>', dataIndex: 'couponId', width: 100, sortable : true, align: 'center',filter: {xtype: 'textfield'}},
        	{text: '<fmt:message key="coupon.couponName"/>', dataIndex: 'coupon.couponName', width: 77, sortable : true,filter: {xtype: 'textfield'}},
        	{text: '<fmt:message key="userCoupon.userId"/>', dataIndex: 'userId', width: 120, sortable : true,filter: {xtype: 'textfield'}},
        	{text: '<fmt:message key="coupon.isGiftCoupon"/>', dataIndex: 'isGiftCoupon', width: 80,  align: 'center', sortable : true,
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
        	{text: '<fmt:message key="user.mobile"/>', dataIndex: 'mobile', width: 120, sortable : true,filter: {xtype: 'textfield'}},
        	{text: '<fmt:message key="user.nickname"/>', dataIndex: 'nickName', width: 120, sortable : true,filter: {xtype: 'textfield'}},
        	{text: '<fmt:message key="userCoupon.couponNo"/>', dataIndex: 'couponNo', width: 150, sortable : true,filter: {xtype: 'textfield'}}, 
        	{text: '<fmt:message key="userCoupon.couponTaskId"/>', dataIndex: 'couponTaskId', width: 77, sortable : true,filter: {xtype: 'textfield'}}, 
            {text: '<fmt:message key="userCoupon.couponStatus"/>', dataIndex: 'couponStatus', width: 80, align: 'center', sortable : true,
            filter: {
					xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.couponStatusStore,
			        queryMode: 'local',
			        typeAhead: true
				},
		     renderer :this.couponStatusRenderer
            },
			{text: '<fmt:message key="userCoupon.receiveTime"/>', dataIndex: 'receiveTime', width: 100, sortable : true, align: 'center',filter: {xtype: 'textfield'}},
        	{text: '<fmt:message key="userCoupon.effectiveTime"/>', dataIndex: 'effectiveTime', width: 120, sortable : true,filter: {xtype: 'textfield'}}, 
        	{text: '<fmt:message key="userCoupon.createTime"/>', dataIndex: 'createTime', width: 140, sortable : true,filter: {xtype: 'textfield'}}, 
        	{text: '<fmt:message key="userCoupon.updateTime"/>', dataIndex: 'updateTime', width: 140, sortable : true,filter: {xtype: 'textfield'}}, 
        ];
        
        this.tbar = [{
	    	text: '<fmt:message key="button.refresh"/>', 
	        iconCls: 'refresh', 	
	       	handler: function(){this.store.reload();}, 
	       	scope: this
	  	},'-',{
        	text: '<fmt:message key="user.coupon.stop"/>', 
        	iconCls: 'disabled', 	
        	handler: this.stopUserCoupon, 
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
	    
	    <jkd:haveAuthorize access="/coupon/userCouponList.json">
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
    
    
    stopUserCoupon : function(){
        var rowsData = [];	
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}
		for(var i = 0; i < records.length; i++){	
			rowsData.push(records[i].data.userCouponId);	
		}
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="product.task.confirm.stop"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/coupon/deleteUserCoupon.json"/>',
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
   	
   	fontRenderer: function(value, meta, record) {
    	meta.style = 'overflow:auto;padding: 3px 6px;text-overflow: ellipsis;white-space: nowrap;white-space:normal;line-height:24px;font-weight:bold;';      
       	return value;     
   	},
   	
   	couponStatusRenderer:function(val){
   	  if(val == 0){
	        return '<b><fmt:message key="userCoupon.couponStatus0"/></b>';
	     }else if(val == 1) {
            return '<b><fmt:message key="userCoupon.couponStatus1"/></b>';
         }else if(val == 2){
            return '<b><fmt:message key="userCoupon.couponStatus2"/></b>';
         }else if(val == -1){
            return '<b><fmt:message key="userCoupon.couponStatus-1"/></b>';
         }else if(val == -2){
            return '<b><fmt:message key="userCoupon.couponStatus-2"/></b>';
         }
   	 },
   	 
   	 rendererStuts : function(val){
		if(val == true) {
            return '<b><fmt:message key="button.yes"/></b>';
        }else{
            return '<fmt:message key="button.no"/>';
        }
	}
});