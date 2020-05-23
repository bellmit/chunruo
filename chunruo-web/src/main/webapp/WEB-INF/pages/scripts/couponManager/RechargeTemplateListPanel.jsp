<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('RechargeTemplate', {
	extend: 'Ext.data.Model',
	idProperty: 'templateId',
    fields: [
		{name: 'templateId',		mapping: 'templateId',		 type: 'int'},
        {name: 'amount',			mapping: 'amount',		     type: 'string'},
		{name: 'giftAmount',		mapping: 'giftAmount',		 type: 'string'},
		{name: 'giftUserLevel',		mapping: 'giftUserLevel',	 type: 'int'},
		{name: 'giftUserLevelTime',	mapping: 'giftUserLevelTime',type: 'string'},
		{name: 'productId',			mapping: 'productId',		 type: 'int'},
		{name: 'type',			    mapping: 'type',		     type: 'int'},
		{name: 'couponId',			mapping: 'couponId',		 type: 'string'},
		{name: 'giftName',			mapping: 'giftName',		 type: 'string'},
		{name: 'imageUrl',			mapping: 'imageUrl',		 type: 'string'},
		{name: 'isRecommend',		mapping: 'isRecommend',		 type: 'bool'},
		{name: 'userLevel',			mapping: 'userLevel',		 type: 'int'},
		{name: 'createTime',		mapping: 'createTime',		 type: 'string'},
		{name: 'updateTime',		mapping: 'updateTime',		 type: 'string'}

    ],
});

Ext.define('MyExt.couponManager.RechargeTemplateListPanel', {
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
			model: 'RechargeTemplate',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/rechargeTemplate/list.json"/>',
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

	    this.columns = [
	    	{text: '<fmt:message key="recharge.template.templateId" />', dataIndex: 'templateId', width: 80, sortable : true},
            {text: '<fmt:message key="recharge.template.amount" />', dataIndex: 'amount', width: 80, sortable : true,
            	filter: {xtype: 'textfield'}
            },
            {text: '<fmt:message key="recharge.template.giftAmount" />', dataIndex: 'giftAmount', width: 80, sortable: true,
            	filter: {xtype: 'textfield'}
            },
            {text: '<fmt:message key="recharge.template.isEnable"/>', dataIndex: 'isEnable', width: 60,  align: 'center', sortable : true,
		    	renderer: this.inviteStus,
	        	filter: {
					xtype: 'combobox',
				    displayField: 'name',
				    valueField: 'id',
				   	store: this.isEnableStore,
				   	queryMode: 'local',
				    typeAhead: true
				}
			},
             {text: '<fmt:message key="recharge.template.isRecommend"/>', dataIndex: 'isRecommend', width: 80,  align: 'center', sortable : true,
		    	renderer: this.inviteStus,
	        	filter: {
					xtype: 'combobox',
				    displayField: 'name',
				    valueField: 'id',
				   	store: this.isEnableStore,
				   	queryMode: 'local',
				    typeAhead: true
				}
			},
            {text: '<fmt:message key="recharge.template.type" />', dataIndex: 'type', width: 100, sortable: true,
            	filter: {
            		xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: Ext.create('Ext.data.Store', {
						autoDestroy: true,
						model: 'InitModel',
						data: [
							{id: '1', name: '<fmt:message key="recharge.template.type1"/>'},
							{id: '2', name: '<fmt:message key="recharge.template.type2"/>'},
							{id: '3', name: '<fmt:message key="recharge.template.type3"/>'},
							{id: '4', name: '<fmt:message key="recharge.template.type4"/>'},
							{id: '0', name: '<fmt:message key="recharge.template.type0"/>'},
						]
					}),
			        queryMode: 'local',
			        typeAhead: true
            	},
            	renderer: this.typeRenderer
            },
            {text: '<fmt:message key="recharge.template.giftUserLevel" />', 
            	dataIndex: 'giftUserLevel',
            	width: 65, 
            	align: 'center',
            	sortable : false,
            	renderer: this.giftUserLevelRenderer,
            	filter: {
        			xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: Ext.create('Ext.data.Store', {
						autoDestroy: true,
						model: 'InitModel',
						data: [
							{id: '2', name: '<fmt:message key="recharge.template.giftUserLevel2"/>'},
							{id: '4', name: '<fmt:message key="recharge.template.giftUserLevel4"/>'},
							{id: '5', name: '<fmt:message key="recharge.template.giftUserLevel5"/>'},
							{id: '0', name: '<fmt:message key="recharge.template.giftUserLevel.none"/>'}
						]
					}),
			        queryMode: 'local',
			        typeAhead: true
				}
           	},
            {text: '<fmt:message key="recharge.template.userLevel" />', 
            	dataIndex: 'userLevel',
            	width: 65, 
            	align: 'center',
            	sortable : false,
            	renderer: this.userLevelRenderer,
            	filter: {
        			xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: Ext.create('Ext.data.Store', {
						autoDestroy: true,
						model: 'InitModel',
						data: [
							{id: '1', name: '<fmt:message key="recharge.template.userLevel1"/>'},
							{id: '2', name: '<fmt:message key="recharge.template.userLevel2"/>'},
							{id: '4', name: '<fmt:message key="recharge.template.userLevel4"/>'},
							{id: '5', name: '<fmt:message key="recharge.template.userLevel5"/>'},
							{id: '6', name: '<fmt:message key="recharge.template.userLevel6"/>'},
							{id: '7', name: '<fmt:message key="recharge.template.userLevel7"/>'}
						]
					}),
			        queryMode: 'local',
			        typeAhead: true
				}
           	},
            {text: '<fmt:message key="recharge.template.productId" />', dataIndex: 'productId', width: 80, sortable: true},
    		{text: '<fmt:message key="recharge.template.couponId" />', dataIndex: 'couponId', width: 80, sortable: true},
            {text: '<fmt:message key="recharge.template.giftName" />', dataIndex: 'giftName', width: 200, sortable: true},
            {text: '<fmt:message key="recharge.template.createTime" />', dataIndex: 'createTime', width: 180, sortable: true},
            {text: '<fmt:message key="recharge.template.updateTime" />', dataIndex: 'updateTime', width: 180, sortable: true}
           
        ];
        
        this.tbar = [
        <jkd:haveAuthorize access="/rechargeTemplate/saveRechargeTemplate.json">
        {
        	text: '<fmt:message key="button.add"/>', 
        	iconCls: 'add', 
        	handler: this.saveRechargeTemplate, 
        	scope: this
        }<c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/rechargeTemplate/deleteRechargeTemplate.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
		'-',{
        	text: '<fmt:message key="button.delete"/>', 
        	iconCls: 'delete',	
        	handler: this.deleteRechargeTemplate, 
        	scope: this
        }
        </jkd:haveAuthorize>
        <jkd:haveAuthorize access="/rechargeTemplate/updateIsEnable.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        {
        	text: '<fmt:message key="product.task.stop"/>', 
        	iconCls: 'disabled', 	
        	handler: this.stopRechargeTemplate, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/rechargeTemplate/updateIsEnable.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        {
        	text: '<fmt:message key="product.task.start"/>', 
        	iconCls: 'enable', 	
        	handler: this.startRechargeTemplate, 
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
    	<jkd:haveAuthorize access="/rechargeTemplate/list.json">
	   	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.productList.filters)
			});
	    }, this);
	    this.store.load();   
	    </jkd:haveAuthorize>
	    
	    this.productList.on('itemdblclick', function(view, record, item, index, e, eOpts) {
	    	<jkd:haveAuthorize access="/rechargeTemplate/getRechargeTemplateById.json">
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
        if(value =="1") {
            return '<b><fmt:message key="recharge.template.type1"/></b>';
        }else if(value == '2'){
            return '<b><fmt:message key="recharge.template.type2"/></b>';
        }else if(value == '3'){
            return '<b><fmt:message key="recharge.template.type3"/></b>';
        } else if(value == '4'){
            return '<b><fmt:message key="recharge.template.type4"/></b>';
        } else {
            return '<b><fmt:message key="recharge.template.type0"/></b>';
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
