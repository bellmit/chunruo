<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('UserInfo', {
	extend: 'Ext.data.Model',
	idProperty: 'userId',
    fields: [
		{name: 'userId',		    mapping: 'userId',		        type: 'int'},
		{name: 'nickname',		    mapping: 'nickname',		    type: 'string'},
		{name: 'headerImage',	    mapping: 'headerImage',         ype: 'string'},
		{name: 'level',	            mapping: 'level',               type: 'int'},
		{name: 'pushLevel',	        mapping: 'pushLevel',           type: 'int'},
		{name: 'mobile',	 	    mapping: 'mobile',				type: 'string'},
		{name: 'inviteV1Number',	mapping: 'inviteV1Number',	    type: 'int'},
		{name: 'remarks',	        mapping: 'remarks',	            type: 'string'},
		{name: 'pushPartnerTime',	mapping: 'pushPartnerTime',	    type: 'string'},
		{name: 'pushManagerTime',	mapping: 'pushManagerTime',	    type: 'string'}
    ]
});

Ext.define('MyExt.userManager.PushUserListPanel', {
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
			model: 'UserInfo',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/pushUser/list.json"/>',
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
		
		 this.rendererlevelStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        	    {id: '5', name: '<fmt:message key="user.level5"/>'},
	        	{id: '4', name: '<fmt:message key="user.level4"/>'},
        		{id: 3, name: '<fmt:message key="user.level3"/>'},
        		{id: 2, name: '<fmt:message key="user.level2"/>'},
        		{id: 1, name: '<fmt:message key="user.level1"/>'},
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
        	{text: '<fmt:message key="user.level"/>', dataIndex: 'level', width: 100, sortable : true, locked: true,
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
        	{text: '<fmt:message key="user.pushLevel"/>', dataIndex: 'pushLevel', width: 100, sortable : true, locked: true,
        		renderer : this.rendererPushLevelStuts,
        		filter: {
					xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.rendererPushLevelStore,
			        queryMode: 'local',
			        typeAhead: true
				},
        	},
        	{text: '<fmt:message key="user.mobile"/>',         dataIndex: 'mobile', width: 120, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.inviteV1Number"/>', dataIndex: 'inviteV1Number', width: 120, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.pushPartnerTime"/>',   dataIndex: 'pushPartnerTime', width: 150, sortable: true, align: 'center',filter: {xtype: 'textfield'}},
        	{text: '<fmt:message key="user.pushManagerTime"/>',   dataIndex: 'pushManagerTime', width: 150, sortable: true, filter: {xtype: 'textfield'}},
			{text: '<fmt:message key="user.remarks"/>',           dataIndex: 'remarks', width: 500, sortable : true,
        		filter: {xtype: 'textfield'}
        	}
        ];
        
        this.tbar = [
        <jkd:haveAuthorize access="/pushUser/list.json">
        {
	    	text: '<fmt:message key="button.refresh"/>', 
	        iconCls: 'refresh', 	
	       	handler: function(){this.store.reload();}, 
	       	scope: this
	  	}
	  	<c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/pushUser/updatePushLevel.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
	  	'-',{
        	text: '<fmt:message key="user.edit.pushLevel"/>', 
        	iconCls: 'enable', 	
        	handler: this.editPushLevel, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/pushUser/updateUserRemarks.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        {
        	text: '<fmt:message key="user.remarks.edit"/>', 
        	iconCls: 'add', 	
        	handler: this.editRemarks, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/pushUser/exportPushUserExcel.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '->',{
        	text: '<fmt:message key="push.user.xls"/>',
        	iconCls: 'excel', 	
        	handler: this.exportPushUser,
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
    	
    	this.gsm = this.productList.getSelectionModel();
    	this.items = [this.productList];
		this.callParent(arguments);
	    
	    <jkd:haveAuthorize access="/pushUser/list.json">
	   	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.productList.filters)
			});
	    }, this);
	    this.store.load();  
	    </jkd:haveAuthorize>
    },
    
    editPushLevel : function(){
        var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}else if(records.length > 1){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="coupon.sendStatus.bigCount"/>');
			return;
		}
		
    	var pushUserLevelFormPanel = Ext.create('MyExt.userManager.PushUserLevelFormPanel', {
			id: 'add@pushUserLevelFormPanel' + this.id,
    		viewer: this.viewer,
    		userId:records[0].data.userId,
    		pushLevel:records[0].data.pushLevel
   	 	});
    	
    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
		    	if(pushUserLevelFormPanel.form.isValid()){
					Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
		             		pushUserLevelFormPanel.form.submit({
		                 		waitMsg: 'Loading...',
		                 		url: '<c:url value="/pushUser/updatePushLevel.json"/>',
		               			scope: this,
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
      	openWin('<fmt:message key="button.add"/>', pushUserLevelFormPanel, buttons,260, 130);
    },    
    
    editRemarks : function(){
        var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}else if(records.length > 1){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="coupon.sendStatus.bigCount"/>');
			return;
		}
		
    	var pushUserRemarksFormPanel = Ext.create('MyExt.userManager.PushUserRemarksFormPanel', {
			id: 'add@pushUserRemarksFormPanel' + this.id,
    		viewer: this.viewer,
    		userId:records[0].data.userId,
    		remarks:records[0].data.remarks
   	 	});
    	
    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
		    	if(pushUserRemarksFormPanel.form.isValid()){
					Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
		             		pushUserRemarksFormPanel.form.submit({
		                 		waitMsg: 'Loading...',
		                 		url: '<c:url value="/pushUser/updateUserRemarks.json"/>',
		               			scope: this,
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
      	openWin('<fmt:message key="button.add"/>', pushUserRemarksFormPanel, buttons,400, 400);
    }, 
    
    exportPushUser : function(){
		var excelPanel = Ext.create('MyExt.orderManager.ExportExcel', {id: 'ExportExcel@ExportExcel', title: '<fmt:message key="button.add"/>'});
		var buttons = [{
			text: '<fmt:message key="push.user.xls"/>',
			handler: function(){
	            if(excelPanel.form.isValid()){
	            	Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="push.user.export.confirm"/>', function(e){
						if(e == 'yes'){
						var formValues=excelPanel.getForm().getValues();
							var begin = Ext.util.Format.date(Ext.getCmp('beginTime').getValue(), 'Y-m-d H:i:s');
							
							var end = Ext.util.Format.date(Ext.getCmp('endTime').getValue(), 'Y-m-d H:i:s');
							window.location.href = "/pushUser/exportPushUserExcel.json?beginTime="+begin+"&endTime="+end;
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
		openWin('<fmt:message key="exporter.push.user.xls"/>', excelPanel, buttons, 400, 175);
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
   	
   	levelRenderer: function(val){
   	    if(val == 1) {
            return '<b><fmt:message key="invites.courtesy.level1"/></b>';
         }else if(val == 2){
            return '<b><fmt:message key="invites.courtesy.level2"/></b>';
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
	rendererStuts : function(val){
		if(val == true) {
            return '<b><fmt:message key="button.yes"/></b>';
        }else{
            return '<fmt:message key="button.no"/>';
        }
	},
});