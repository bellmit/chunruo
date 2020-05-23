<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MemberGift', {
	extend: 'Ext.data.Model',
    fields: [
    	{name: 'giftId',		    mapping: 'giftId',		    type: 'int'},
		{name: 'templateId',		mapping: 'templateId',		type: 'int'},
		{name: 'name',		        mapping: 'name',		    type: 'string'},
		{name: 'imagePath',	 		mapping: 'imagePath',		type: 'string'},
		{name: 'price',		        mapping: 'price',           type: 'string'},
		{name: 'status',	 	    mapping: 'status',		    type: 'bool'},
		{name: 'isDelete',	        mapping: 'isDelete',	    type: 'bool'},
		{name: 'productCode',	    mapping: 'productCode',		type: 'string'},
		{name: 'productSku',	    mapping: 'productSku',	    type: 'string'},
		{name: 'stockNumber',	 	mapping: 'stockNumber',		type: 'int'},
		{name: 'wareHouseId',	 	mapping: 'wareHouseId',		type: 'int'},
		{name: 'couponIds',		    mapping: 'couponIds',       type: 'string'},
		{name: 'updateTime',	 	mapping: 'updateTime',		type: 'string'},
		{name: 'createTime',	 	mapping: 'createTime',		type: 'string'}
    ]
});

Ext.define('MyExt.productManager.MemberGiftPanel', {
   	extend : 'Ext.panel.Panel',
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
		
    	this.store = Ext.create('Ext.data.Store', {
    		pageSize: 50,
	        autoLoad: false,
			autoDestroy: true,
			sortOnLoad: true,
			remoteSort: true,
			model: 'MemberGift',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/memberYears/getMemberGiftListByTemplateId.json"/>',
				reader: {
					type : 'json',
                	root: 'data',
                	totalProperty: 'totalCount'
            	}
			},
			sorters: [{
	            property: 'id',
	            direction: 'desc'
	        }]
		});
		
		this.rendererStutsStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: 1, name: '<fmt:message key="start.image.phoneType1"/>'},
        		{id: 0, name: '<fmt:message key="start.image.phoneType0"/>'}
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
			{
	           	text: '<fmt:message key="product.wholesale.image"/>',
	          	menuDisabled: true,
	           	sortable: false,
	            xtype: 'actioncolumn',
	            align: 'center',
	          	width: 30,
	           	higth: 30,
	           	locked: true,
	            dataIndex: 'imagePath',
	            renderer: function(value, metadata, record) {
	       			return Ext.String.format('<img height="25" width="25" src="{0}"></img>', value);
	   			}
	        },
	       	{text: '<fmt:message key="member.gift.giftId"/>', dataIndex: 'giftId', width: 70, sortable : false},
	       	{text: '<fmt:message key="member.gift.name"/>', dataIndex: 'name', width: 250, sortable : true},
	       	{text: '<fmt:message key="member.gift.price"/>', dataIndex: 'price',  width: 70, sortable : true},
	       	{text: '<fmt:message key="member.gift.stockNumber"/>', dataIndex: 'stockNumber', width: 70, sortable : true},
	       	{text: '<fmt:message key="member.years.template.status"/>', dataIndex: 'status', width: 70, sortable : true,
	      			renderer : function(value){
					if(value == 0){
						return '<span style="color:red;"><b><fmt:message key='button.no'/></b></span>';
					}else if(value == 1){
						return '<span style="color:blue;"><b><fmt:message key='button.yes'/></b></span>';
					}
				}
	       	},
	       	{text: '<fmt:message key="member.gift.productCode"/>', dataIndex: 'productCode',  width: 210, sortable : true},
	       	{text: '<fmt:message key="member.gift.productSku"/>', dataIndex: 'productSku', width: 210, sortable : true},
	       	{text: '<fmt:message key="member.gift.wareHouseId"/>', dataIndex: 'wareHouseId', width: 70, sortable : true},
	    	{text: '<fmt:message key="member.gift.createTime"/>', dataIndex: 'createTime', width: 140, sortable : true},
	       	{text: '<fmt:message key="member.gift.updateTime"/>', dataIndex: 'updateTime', width: 140, sortable : true}
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
		
		this.list = Ext.create('Ext.grid.GridPanel', {
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
	    
	    this.tbar = [
	    <jkd:haveAuthorize access="/memberYears/saveOrUpdateMemberGift.json">
	    {
        	text: '<fmt:message key="button.add"/>', 
        	iconCls: 'add', 	
        	handler: this.addMemberGift, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/memberYears/deleteMemberGift.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
        	text: '<fmt:message key="button.delete"/>', 
        	iconCls: 'delete', 	
        	handler:  this.deleteMemberGift, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/memberYears/changeStatus.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
        	text: '<fmt:message key="button.enable"/>', 
        	iconCls: 'enable', 	
        	handler: this.enable, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/memberYears/changeStatus.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
        	text: '<fmt:message key="button.disable"/>', 
        	iconCls: 'Cancel', 	
        	handler: this.disable, 
        	scope: this
        }
        </jkd:haveAuthorize>
        ];
	    
	    this.east =  Ext.create('MyExt.productManager.MemberGiftTabPanel', {
		 	viewer: this.viewer,
		 	layout: 'card',
	        border: false,
	       	region: 'east',
	        width: 550,
	        split: true,
	        header: false,
	        hidden: true,
	        store: this.store
        });
    	
    	this.items = [this.list, this.east];	
		this.east.hide();
		this.callParent(arguments);
	    
	    this.gsm = this.list.getSelectionModel();
	   	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.list.filters),
	        	templateId: this.record.data.templateId
			});
	    }, this);
	    
	    this.list.on('itemdblclick', function(view, record, item, index, e, eOpts) {
	    	<jkd:haveAuthorize access="/memberYears/getMemberGiftByGiftId.json">
	    	this.east.transferData(this.east, record, this.body.dom.clientWidth);
	    	this.east.show();
	    	</jkd:haveAuthorize>
	    }, this);
    },
    
    transferData : function( record){
    	this.record = record;
    	this.store.load();
    },
	
    enable : function(){
    	var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.giftId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="enable.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/memberYears/changeStatus.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData), status: 1},
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
    
    disable : function(){
    	var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.giftId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="disabled.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/memberYears/changeStatus.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData), status: 0},
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
    
    deleteMemberGift : function(){
    	var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.giftId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="delete.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/memberYears/deleteMemberGift.json"/>',
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

    addMemberGift : function(){
		var memberGiftFormPanel = Ext.create('MyExt.productManager.MemberGiftFormPanel', {id: 'memberGiftFormPanel@MemberGiftFormPanel',store: this.store,templateId: this.record.data.templateId, title: '<fmt:message key="button.add"/>'});
		openWin('<fmt:message key="button.add"/>', memberGiftFormPanel, [], 500, 450);
	},
	
	rendererStatus : function(val){
		var str =  "";
		if(val == 0){
			str = '<b><fmt:message key="start.image.phoneType0"/></b>';
		}else if(val == 1 ){
			str = '<b><fmt:message key="start.image.phoneType1"/></b>';
		}
		return str;
	},
	
	inviteStus : function(val){
		if(val == true) {
            return '<b><fmt:message key="button.yes"/></b>';
        }else{
            return '<fmt:message key="button.no"/>';
        }
	}
});