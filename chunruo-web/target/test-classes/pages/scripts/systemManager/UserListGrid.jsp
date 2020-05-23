<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('User', {
	extend: 'Ext.data.Model',
    fields: [
    	{name: 'userId', 		type: 'int'},
    	{name: 'username', 		type: 'string'},
    	{name: 'groupName', 	type: 'string'},
    	{name: 'realname', 		type: 'string'},
    	{name: 'sex', 			type: 'string'},
    	{name: 'enabled', 		type: 'bool'},
    	{name: 'email', 		type: 'string'},
    	{name: 'level', 		type: 'string'},
    	{name: 'mobile', 		type: 'string'},
    	{name: 'birthday', 		type: 'string'},
    	{name: 'addresss', 		type: 'string'},
    	{name: 'loginErrorTimes', type: 'string'},
    	{name: 'createTime', 	type: 'string'},
    	{name: 'updateTime', 	type: 'string'}
    ],
    idProperty: 'userId'
});

Ext.define('MyExt.systemManager.UserListGrid', {
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
			model: 'User',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/userSys/list.json"/>',
				reader: {
					type : 'json',
                	root: 'data',
                	totalProperty: 'totalCount'
            	}
			},
			sorters: [{
	            property: 'userId',
	            direction: 'ASC'
	        }]
		});
		
		this.levelStore= Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: '3', name: '<fmt:message key="user.admin.level3"/>'},
        		{id: '2', name: '<fmt:message key="user.admin.level2"/>'},
        		{id: '1', name: '<fmt:message key="user.admin.level1"/>'},
        	]});
        	
        	this.rendererStutsStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: 1, name: '<fmt:message key="button.yes"/>'},
        		{id: 0, name: '<fmt:message key="button.no"/>'}
        	]
        });

	    this.columns = [
	    	{text: '<fmt:message key="user.userId" />', dataIndex: 'userId', width: 90, sortable : true},
            {text: '<fmt:message key="user.account" />', dataIndex: 'username', width: 120, sortable : true,
            	filter: {xtype: 'textfield'}
            },
            {text: '<fmt:message key="user.realname" />', dataIndex: 'realname', width: 120, sortable: true,
            	filter: {xtype: 'textfield'}
            },
            {text: '<fmt:message key="user.sex"/>', dataIndex: 'sex', width: 80, sortable : true, 
		        filter: {
					xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.rendererStutsStore,
			        queryMode: 'local',
			        typeAhead: true
				},
		        renderer : function(val, metadata, record, rowIndex, columnIndex, store){ 
					var str =  "";
					if(val == 'true'){
						str = '<b><fmt:message key="user.sex1"/></b>';
					}else{
						str = '<fmt:message key="user.sex0"/>';
					}
					return str;
				}
        	},           
           	{text: '<fmt:message key="user.admin.level"/>', dataIndex: 'level', width: 80, sortable : true, 
        		renderer : this.levelStuts,
        		filter: {
					xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.levelStore,
			        queryMode: 'local',
			        typeAhead: true
				},
        	},
        	{text: '<fmt:message key="user.groupName" />', dataIndex: 'groupName', width: 120, sortable: true,
        		filter: {xtype: 'textfield'}
        	},
            {text: '<fmt:message key="user.mobile" />', dataIndex: 'mobile', width: 120, sortable: true},
            {text: '<fmt:message key="user.email" />', dataIndex: 'email', width: 180, sortable: true},
            {text: '<fmt:message key="user.enabled" />', dataIndex: 'enabled', width: 65, align: 'center', renderer: this.openStuts, sortable : false},
            {text: '<fmt:message key="user.createTime" />', dataIndex: 'createTime', width: 120, sortable: true},
            {text: '<fmt:message key="user.updateTime" />', dataIndex: 'updateTime', width: 120, sortable: true}
        ];
        
        this.tbar = [
        <jkd:haveAuthorize access="/userSys/saveAdminUser.json">
        {
        	text: '<fmt:message key="button.add"/>', 
        	iconCls: 'add', 
        	handler: this.addUser, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/userSys/editUser.json,/userSys/saveAdminUser.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        {
        	text: '<fmt:message key="user.admin.edit"/>', 
            iconCls: 'Chartpieadd',
        	handler: this.editUser, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/userSys/deleteAdminUser.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
        	text: '<fmt:message key="button.delete"/>', 
        	iconCls: 'delete',	
        	handler: this.deleteUser, 
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
    	<jkd:haveAuthorize access="/userSys/list.json">
    	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.filters)
			});
	    }, this);
	    this.store.load();
	    </jkd:haveAuthorize>
    },

	addUser : function(){
		var userFormPanel = Ext.create('MyExt.systemManager.UserFormPanel', {id: 'addUser@SystemManager', title: '<fmt:message key="user.title.add"/>'});
		var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
	            if(userFormPanel.form.isValid()){
	            	Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
			                userFormPanel.form.submit({
			                    waitMsg: 'Loading...',
			                    url: '<c:url value="/userSys/saveAdminUser.json"/>',
			                    scope: this,
			                    success: function(form, action) {
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
		openWin('<fmt:message key="user.title.add"/>', userFormPanel, buttons, 450, 400);
	},
	
	editUser : function(){
        var rowsData = [];
		var records = this.gsm.getSelection();
		if(records.length == 0 ){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}else if(records.length > 1 ){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="user.approve.records"/>');
			return;
		}	
		
		var userId = records[0].data.userId;
		var userFormPanel = Ext.create('MyExt.systemManager.UserFormPanel', {id: 'userFormPanel@' + this.id, isEditor: true});
   	 	userFormPanel.load({   
    		waitMsg: '<fmt:message key="ajax.waitMsg"/>',   
    		waitTitle: '<fmt:message key="ajax.waitTitle"/>',
    		url: '<c:url value="/userSys/editUser.json"/>', 
    		params: {userId: userId}, 
    		failure : function (form, action) {
     			showMsg('<fmt:message key="ajax.waitTitle"/>', '<fmt:message key="ajax.load.failure"/>', 4);   
    		}   
   		});

    	var buttons = [
    	<jkd:haveAuthorize access="/userSys/saveAdminUser.json">
    	{
			text: '<fmt:message key="button.save"/>',
			handler: function(){	
			    if(userFormPanel.form.isValid()){
					Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
					     	userFormPanel.form.submit({
		                 		waitMsg: 'Loading...',
		                 		url: '<c:url value="/userSys/saveAdminUser.json"/>',
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
		},
		</jkd:haveAuthorize>
		{
			text: '<fmt:message key="button.cancel"/>',
			handler : function(){popWin.close();},
			scope: this
		}];
      	openWin(Ext.String.format('<fmt:message key="user.title.modify"/>'), userFormPanel, buttons, 450, 400);
    },
	
	deleteUser : function() {
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.userId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="delete.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/userSys/deleteAdminUser.json"/>',
		         	method: 'post',
					scope: this,
					params:{userIdGridJson: Ext.JSON.encode(rowsData)},
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
	
	sexStuts : function(val){
		if(val == true) {
            return '<b><fmt:message key="user.sex.true"/></b>';
        }else{
            return '<b><fmt:message key="user.sex.false"/></b>';
        }
	},
	
	levelStuts : function(val){
	     if(val == 1) {
            return '<b><fmt:message key="user.admin.level1"/></b>';
         }else if(val == 2){
            return '<b><fmt:message key="user.admin.level2"/></b>';
         }else{
           return '<b><fmt:message key="user.admin.level3"/></b>';
        }
	},
	
	openStuts : function(val){
		if(val == true) {
            return '<b><fmt:message key="button.yes"/></b>';
        }else{
            return '<fmt:message key="button.no"/>';
        }
	}
})
