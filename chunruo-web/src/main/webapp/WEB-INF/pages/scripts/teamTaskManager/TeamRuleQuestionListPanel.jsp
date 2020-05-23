<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('TeamRuleQuestionExplain', {
	extend: 'Ext.data.Model',
	idProperty: 'explainId',
     fields: [
    	{name: 'explainId',	    mapping: 'explainId',	    type: 'int'},
		{name: 'level',	 		mapping: 'level',		    type: 'int'},
		{name: 'type',	        mapping: 'type',	        type: 'int'},
		{name: 'content',	    mapping: 'content',         type: 'string'},
		{name: 'question',	    mapping: 'question',	    type: 'string'},
		{name: 'sort',	        mapping: 'sort',	        type: 'int'},
		{name: 'createTime',	mapping: 'createTime',	    type: 'string'},
		{name: 'updateTime',	mapping: 'updateTime',	    type: 'string'},
    ]
});

Ext.define('MyExt.teamTaskManager.TeamRuleQuestionListPanel', {
    extend : 'Ext.panel.Panel',
    requires : ['Ext.ux.grid.GridHeaderFilters','MyExt.DateSelectorPicker'],
    header: false,
	closable: true,
	columnLines: true,
	animCollapse: true,
	layout: 'border',
   	defaults: {  
    	split: true,    
        collapsible: true,
        collapseDirection: 'left'
    },

	initComponent : function(config) {
		Ext.apply(this, config);
		
		this.store = Ext.create('Ext.data.Store', {
        	pageSize: 50,
	        autoLoad: false,
			autoDestroy: true,
			sortOnLoad: true,
			remoteSort: true,
			model: 'TeamRuleQuestionExplain',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/teamTask/ruleQuestionlist.json?type=2"/>',
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
        
	    this.rendererStore= Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: '1', name: '<fmt:message key="button.yes"/>'},
        		{id: '0', name: '<fmt:message key="button.no"/>'}
        	]
        });
	        
       	this.rendererProductTypeStore= Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: '1', name: '<fmt:message key="product.wareHouse.productType1"/>'},
        		{id: '2', name: '<fmt:message key="product.wareHouse.productType2"/>'},
        		{id: '3', name: '<fmt:message key="product.wareHouse.productType3"/>'},
        	]
        });
		
		this.levelStore= Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        	    {id: '1', name: '<fmt:message key="team.rule.question.level1"/>'},
        	 	{id: '2', name: '<fmt:message key="team.rule.question.level2"/>'},
        		{id: '3', name: '<fmt:message key="team.rule.question.level3"/>'},
        		
        	]
        });
		
		this.columns = [
			{text: '<fmt:message key="product.intro.introId"/>', dataIndex: 'explainId', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="product.intro.sort"/>', dataIndex: 'sort', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="team.rule.question.type"/>', dataIndex: 'type', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="team.rule.question.level"/>', dataIndex: 'level', width: 140, sortable : true,
        	renderer: this.rendererLevelType,
        	filter: {
					xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.levelStore,
			        queryMode: 'local',
			        typeAhead: true
				}
        		
        	},
        	{text: '<fmt:message key="team.rule.question.question"/>', dataIndex: 'question', width: 500, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="team.rule.question.content"/>', dataIndex: 'content', width: 500, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	
        	{text: '<fmt:message key="product.intro.createTime"/>', dataIndex: 'createTime', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="product.intro.updateTime"/>', dataIndex: 'updateTime', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	}
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
		this.productWarehouseListBbar = this.pagingToolbar; 
		
    	this.productWarehouseList = Ext.create('Ext.grid.GridPanel', {
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
		    bbar: this.productWarehouseListBbar,
        	plugins: ['gridHeaderFilters'],
		    viewConfig: {
		        stripeRows: true,
		        enableTextSelection: true
		    }
	    });
	    
	    this.tbar = [{
        	text: '<fmt:message key="team.rule.question.add"/>', 
            iconCls: 'add', 
            scope: this,
        	handler: this.adds, 
        	
        },{
        	text: '<fmt:message key="team.rule.question.edit"/>', 
            iconCls: 'Chartpieadd',
        	handler: this.edit, 
        	scope: this
        },{
        	text: '<fmt:message key="team.rule.delete"/>', 
            iconCls: 'delete',
        	handler: this.delete, 
        	scope: this
        }];

    	this.items = [this.productWarehouseList];	
    	this.callParent(arguments);
    	
    	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.productWarehouseList.filters)
			});
	    }, this);
	    this.store.load();
	    
	    this.gsm = this.productWarehouseList.getSelectionModel();
	    this.productWarehouseList.on('itemdblclick', function(view, record, item, index, e, eOpts) {
	    	
	    
	    }, this);
    },
    
    cleanSearch : function(){
    	this.keywordField.setRawValue();
		this.store.loadPage(1);
    },
    
    rendererStuts : function(val){
		if(val == true) {
            return '<b><fmt:message key="button.yes"/></b>';
        }else{
            return '<fmt:message key="button.no"/>';
        }
	},
    
    approves : function(){
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.applyId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="user.applyAgent.approveConfirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/applyAgent/approve.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData),tag:1},
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
    
    adds : function(){
        var rowsData = [];
		var records = this.gsm.getSelection();
		var teamTaskRuleForm = Ext.create('MyExt.teamTaskManager.TeamTaskRuleForm', {
			id: 'teamTaskRuleForm@' + this.id,
    		viewer: this.viewer,	
    		isQuestion:true
   	 	});
   	 	
    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
			if(teamTaskRuleForm.form.isValid()){
				Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
					if(e == 'yes'){
				     	teamTaskRuleForm.form.submit({
	                 		waitMsg: 'Loading...',
	                 		url: '<c:url value="/teamTask/saveOrUpdateRule.json"/>',
	               			scope: this,
	               			params:{type: 2},
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
      	openWin(Ext.String.format('<fmt:message key="team.rule.question.add"/>'), teamTaskRuleForm, buttons, 600, 420);
    },
    
    edit : function(){
        var rowsData = [];
		var records = this.gsm.getSelection();
		if(records.length == 0 ){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}else if(records.length > 1 ){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="user.approve.records"/>');
			return;
		}	
		
		var explainId=records[0].data.explainId;
		var level=records[0].data.level;
		var sort=records[0].data.sort;
		var content=records[0].data.content;
		var question=records[0].data.question;
		var teamTaskRuleForm = Ext.create('MyExt.teamTaskManager.TeamTaskRuleForm', {
			id: 'teamTaskRuleForm@' + this.id,
    		viewer: this.viewer,
    		level:level,
    		sort:sort,
    		content:content,	
    		question:question,
    		explainId:explainId,
    		isQuestion:true
   	 	});


    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){	
			    if(teamTaskRuleForm.form.isValid()){
					Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
					     	teamTaskRuleForm.form.submit({
		                 		waitMsg: 'Loading...',
		                 		url: '<c:url value="/teamTask/saveOrUpdateRule.json"/>',
		               			scope: this,
		               			params:{type: 2,explainId:explainId},
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
      	openWin(Ext.String.format('<fmt:message key="team.rule.question.edit"/>'), teamTaskRuleForm, buttons, 400, 320);
    },
    
   delete : function(){
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.explainId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="product.confirm.delete"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/teamTask/deleteRule.json"/>',
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
   
   	rendererProductTypeStuts : function(val){
		if(val == 1) {
            return '<b><fmt:message key="product.wareHouse.productType1"/></b>';
        }else if(val == 2){
            return '<fmt:message key="product.wareHouse.productType2"/>';
        }else {
           return '<fmt:message key="product.wareHouse.productType3"/>';
        }
	},
	
	rendererStuts : function(val){
		if(val == true) {
            return '<b><fmt:message key="button.yes"/></b>';
        }else{
            return '<b><fmt:message key="button.no"/></b>';
        }
	},
    
   	rendererWarehouseTypeStuts : function(val){
		if(val == 1) {
            return '<b><fmt:message key="product.wareHouse.warehouseType1"/></b>';
        }else{
            return '<fmt:message key="product.wareHouse.warehouseType2"/>';
        }
	},
	
	rendererLevelType : function(val){
		if(val == 1) {
            return '<b><fmt:message key="team.rule.question.level1"/></b>';
        }else if(val == 2) {
            return '<b><fmt:message key="team.rule.question.level2"/></b>';
        }else{
            return '<fmt:message key="team.rule.question.level3"/>';
        }
	},
});