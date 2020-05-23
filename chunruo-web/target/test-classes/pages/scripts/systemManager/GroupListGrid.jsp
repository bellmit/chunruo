<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('Group', {
	extend: 'Ext.data.Model',
    fields: [
    	{name: 'groupId',     type: 'int'},
    	{name: 'name',        type: 'string'},
    	{name: 'rolePath',    type: 'string'},
    	{name: 'isEnable',    type: 'bool'},
    	{name: 'description', type: 'string'},
    	{name: 'createTime',  type: 'string'},
    	{name: 'updateTime',  type: 'string'}
    ],
    idProperty: 'groupId'
});

Ext.define('MyExt.systemManager.GroupListGrid', {
	extend : 'Ext.grid.GridPanel',
	region: 'center',
	autoScroll: true,   
	closable: true,
	selType: 'checkboxmodel',
	multiSelect: true,
	columnLines: true,
	animCollapse: false,
    enableLocking: true,
    viewConfig: {
        stripeRows: true,
        enableTextSelection: true
    },
    
	initComponent : function(config) {
		Ext.apply(this, config);
		
		this.store = Ext.create('Ext.data.Store', {
        	pageSize: 50,
	        autoLoad: false,
			autoDestroy: true,
			sortOnLoad: true,
			remoteSort: true,
			model: 'Group',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/groupSys/list.json"/>',
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
	    	{text: '<fmt:message key="group.userId" />', dataIndex: 'groupId', width: 70, align: 'center', sortable: true},
            {text: '<fmt:message key="group.name" />', dataIndex: 'name', width: 280, sortable : true},
            {text: '<fmt:message key="group.role.desc" />', dataIndex: 'rolePath', flex: 1, sortable: false},
            {text: '<fmt:message key="group.disable" />', dataIndex: 'isEnable', width: 80, align: 'center', sortable: false, 
            	renderer: function(val){
            		if (val == true) {
						return '<span style="color:green;"><b><fmt:message key="button.enable"/></b></span>';
					} else if (val == false) {
						return '<span style="color:red;"><b><fmt:message key="button.disable"/></b></span>';
					}
				}
			},  
            {text: '<fmt:message key="group.createTime" />', dataIndex: 'createTime', width: 140, sortable: true},
            {text: '<fmt:message key="group.updateTime" />', dataIndex: 'updateTime', width: 140, sortable: true}
        ];
        
        this.tbar = [
        <jkd:haveAuthorize access="/roleSys/getRoleList.json,/groupSys/addGroup.json">
        {
        	text: '<fmt:message key="button.add"/>', 
        	iconCls: 'add', 
        	handler: this.addGroup, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/groupSys/deleteGroup.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
        	text: '<fmt:message key="button.delete"/>', 
        	iconCls: 'delete',	
        	handler: this.deleteGroup, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/groupSys/updateGroupStatus.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
        	text: '<fmt:message key="button.enable"/>', 
        	iconCls: 'enable',	
        	handler: this.enableGroup, 
        	scope: this
        },'-',{
        	text: '<fmt:message key="button.disable"/>', 
        	iconCls: 'disabled',	
        	handler: this.disableGroup, 
        	scope: this
        }
        </jkd:haveAuthorize>
        ];
        
        this.bbar = Ext.create('Ext.PagingToolbar', {
            store: this.store,
            displayInfo: true,
            displayMsg: '<fmt:message key="display.record"/>',
			emptyMsg: '<fmt:message key="pagebar.empty"/>'	
        })
    	this.callParent();
    	
    	this.gsm = this.getSelectionModel();
    	<jkd:haveAuthorize access="/groupSys/list.json">
    	this.store.load();
    	</jkd:haveAuthorize>
    	
    	<jkd:haveAuthorize access="/roleSys/getRoleList.json">
    	this.on('itemdblclick', this.onDbClick, this);
    	</jkd:haveAuthorize>
    },
    
    onDbClick : function(view, record, item, index, e, eOpts) {
    	Ext.Ajax.request({
	       	url: '<c:url value="/roleSys/getRoleList.json"/>',
	        method: 'post',
			scope: this,
	        success: function(response){
	       		var responseObject = Ext.JSON.decode(response.responseText);
	       		var roleIdStore = Ext.create('Ext.data.Store', {
     				autoDestroy: true,
     				model: 'InitModel',
     				data: []
     			});
     	
	       		if(responseObject.roleList != null && responseObject.roleList.length > 0){
	    			for(var i = 0; i < responseObject.roleList.length; i ++){
	    				var model = Ext.create('InitModel');
						model.set('id', responseObject.roleList[i].id);
						model.set('name', responseObject.roleList[i].name);
						roleIdStore.insert(i, model);
					}
	    		}
	    		
	    		var groupFormPanel = Ext.create('MyExt.systemManager.GroupFormPanel', {id: 'modifyGroup@SystemManager', roleIdStore: roleIdStore});
		    	groupFormPanel.load({   
		    		waitMsg: '<fmt:message key="ajax.waitMsg"/>',   
		    		waitTitle: '<fmt:message key="ajax.waitTitle"/>',
		    		url: '<c:url value="/groupSys/editGroup.json"/>', 
		    		params: {groupId: record.data.groupId}, 
		    		failure : function (form, action) {
		     			showMsg('<fmt:message key="ajax.waitTitle"/>', '<fmt:message key="ajax.load.failure"/>', 4);   
		    		}   
		   		});
		   		
		    	var buttons = [
		    	<jkd:haveAuthorize access="/groupSys/updateGroup.json">
		    	{
					text: '<fmt:message key="button.save"/>',
					handler: function(){
						if(groupFormPanel.form.isValid()){
							Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
								if(e == 'yes'){
					                groupFormPanel.form.submit({
					                    waitMsg: 'Loading...',
					                    url: '<c:url value="/groupSys/updateGroup.json"/>',
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
				},
				</jkd:haveAuthorize>
				{
					text: '<fmt:message key="button.cancel"/>',
					handler : function(){popWin.close();},
					scope: this
				}];
				openWin('<fmt:message key="group.edit.title"/>', groupFormPanel, buttons, 550, 410);
			}
	  	});
    },

	addGroup : function(){
		Ext.Ajax.request({
	       	url: '<c:url value="/roleSys/getRoleList.json"/>',
	        method: 'post',
			scope: this,
	        success: function(response){
	       		var responseObject = Ext.JSON.decode(response.responseText);
	       		var roleIdStore = Ext.create('Ext.data.Store', {
     				autoDestroy: true,
     				model: 'InitModel',
     				data: []
     			});
     	
	       		if(responseObject.roleList != null && responseObject.roleList.length > 0){
	    			for(var i = 0; i < responseObject.roleList.length; i ++){
	    				var model = Ext.create('InitModel');
						model.set('id', responseObject.roleList[i].id);
						model.set('name', responseObject.roleList[i].name);
						roleIdStore.insert(i, model);
					}
	    		}
	    		
	    		var groupFormPanel = Ext.create('MyExt.systemManager.GroupFormPanel', {id: 'addGroup@SystemManager', roleIdStore: roleIdStore});
				var buttons = [{
					text: '<fmt:message key="button.save"/>',
					handler: function(){
			            if(groupFormPanel.form.isValid()){
			            	Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
								if(e == 'yes'){
					                groupFormPanel.form.submit({
					                    waitMsg: 'Loading...',
					                    url: '<c:url value="/groupSys/addGroup.json"/>',
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
				openWin('<fmt:message key="group.add.title"/>', groupFormPanel, buttons, 550, 410);
			}
	  	});
	},
	
	deleteGroup : function() {
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.groupId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="delete.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/groupSys/deleteGroup.json"/>',
		         	method: 'post',
					scope: this,
					params:{groupGridJson: Ext.JSON.encode(rowsData)},
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
	
	enableGroup : function() {
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.groupId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/groupSys/updateGroupStatus.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData), isEnable: true},
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
	
	disableGroup : function() {
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.groupId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/groupSys/updateGroupStatus.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData), isEnable: false},
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
	}
})
