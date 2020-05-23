<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('UserDiscoveryHotWordRecord', {
	extend: 'Ext.data.Model',
	idProperty: 'recordId',
    fields: [
		{name: 'recordId',	 		mapping: 'recordId',		    type: 'int'},
		{name: 'word',	 	     	mapping: 'word',			    type: 'string'},
		{name: 'isRecommend',		mapping: 'isRecommend',		    type: 'bool'},
		{name: 'isHot',		        mapping: 'isHot',		        type: 'bool'},
		{name: 'createTime',		mapping: 'createTime',		    type: 'string'},
		{name: 'updateTime',		mapping: 'updateTime',		    type: 'string'}
	]
});

Ext.define('MyExt.couponManager.UserDiscoveryHotWordListPanel', {
    extend : 'Ext.grid.GridPanel',
    requires : ['Ext.ux.grid.GridHeaderFilters','Ext.ux.grid.Exporter'],
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
	        autoLoad: false,
			autoDestroy: true,
			sortOnLoad: true,
			remoteSort: true,
			model: 'UserDiscoveryHotWordRecord',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/discovery/userDiscoveryHotWordList.json"/>',
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
      
		this.recommendStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: 0, name: '<fmt:message key="button.no"/>'},
        		{id: 1, name: '<fmt:message key="button.yes"/>'}
        	]
        });
        
        this.typeStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: 2, name: '<fmt:message key="system.roolingNotice.type2"/>'},
        		{id: 1, name: '<fmt:message key="system.roolingNotice.type1"/>'}
        	]
        });
        
		this.columns = [
			{text: '<fmt:message key="discovery.hot.recordId"/>', dataIndex: 'recordId', width: 70, locked: true, sortable : true,
        		align: 'center'
        	},
        	{text: '<fmt:message key="discovery.hot.word"/>', dataIndex: 'word', flex: 1,  sortable : true,locked: true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="discovery.hot.isHot"/>', dataIndex: 'isHot', width: 100,  sortable : true,locked: true,
        		align: 'center',
        		renderer: this.rendererStatus,
        		filter: {
					xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.recommendStore,
			        queryMode: 'local',
			        typeAhead: true
				}
        	},
        	{text: '<fmt:message key="discovery.hot.isRecommend"/>', dataIndex: 'isRecommend', width: 100,  sortable : true,locked: true,
        		align: 'center',
        		renderer: this.rendererStatus,
        		filter: {
					xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.recommendStore,
			        queryMode: 'local',
			        typeAhead: true
				}
        	},
        	{text: '<fmt:message key="discovery.hot.createTime"/>', dataIndex: 'createTime', width: 180, locked: true, sortable : true,
        		align: 'center'
        	},
        	{text: '<fmt:message key="discovery.hot.updateTime"/>', dataIndex: 'updateTime', width: 180, locked: true, sortable : true,
        		align: 'center'
			}
        ];
        
        this.bbar = new Ext.PagingToolbar({
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
		
	    this.tbar = [
        ];
    	this.callParent(arguments);
    	
    	<jkd:haveAuthorize access="/discovery/userDiscoveryHotWordList.json">
    	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.filters)
			});
	    }, this);
	    this.store.load();
	    </jkd:haveAuthorize>
	    
	    this.gsm = this.getSelectionModel();
    },
    
  	saveHotWord : function(){
		var discoveryHotWordForm = Ext.create('MyExt.couponManager.DiscoveryHotWordFormPanel', {id: 'discoveryHotWordForm@SystemManager', title: '<fmt:message key="discovery.hot.title.add"/>'});
		var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
	            if(discoveryHotWordForm.form.isValid()){
	            	Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
			                discoveryHotWordForm.form.submit({
			                    waitMsg: 'Loading...',
			                    url: '<c:url value="/discovery/saveHotWord.json"/>',
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
		openWin('<fmt:message key="discovery.hot.title.add"/>', discoveryHotWordForm, buttons, 450, 200);
	},
	
	deleteHotWord : function() {
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.wordId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="delete.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/discovery/deleteHotWord.json"/>',
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
	
    onDbClick : function(view, record, item, index, e, eOpts) {
    	var rollingNoticeFormPanel = Ext.create('MyExt.couponManager.RollingNoticeFormPanel', {id: 'modifyRollingNoticeFormPanel@SystemManager', isEditor: true, title: '<fmt:message key="rollingNoticeForm.title.modify"/>'});
    	rollingNoticeFormPanel.load({   
    		waitMsg: '<fmt:message key="ajax.waitMsg"/>',   
    		waitTitle: '<fmt:message key="ajax.waitTitle"/>', 
    		url: '<c:url value="/rollingNotice/getRollingNoticeById.json"/>', 
    		params: {noticeId: record.data.noticeId}, 
    		success : function(form, action) {
             	var data = action.result.data;
             	Ext.getCmp("types").setValue(data.type);
               	if(data.isEnabled == 1){
                  	Ext.getCmp("qy").setValue(true);
                }else{
                 	Ext.getCmp("ty").setValue(true);
                }
            },
    		failure : function (form, action) {   
     			showMsg('<fmt:message key="ajax.waitTitle"/>', '<fmt:message key="ajax.load.failure"/>', 4);   
    		}   
   		});
   		
    	var buttons = [
    	<jkd:haveAuthorize access="/rollingNotice/addRollingNotice.json">
    	{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
				if(rollingNoticeFormPanel.form.isValid()){
					Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
			                rollingNoticeFormPanel.form.submit({
			                    waitMsg: 'Loading...',
			                    url: '<c:url value="/rollingNotice/addRollingNotice.json"/>',
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
		openWin('<fmt:message key="rollingNoticeForm.title.modify"/>', rollingNoticeFormPanel, buttons, 450, 200);
    },

	rendererStatus : function(val){
		var str =  "";
		if(val == 1){
			str = '<b><fmt:message key="button.yes"/></b>';
		}else{
			str = '<b><fmt:message key="button.no"/></b>';
		}
		return str;
	},
	typeStatus : function(val){
		var str =  "";
		if(val == 1){
			str = '<b><fmt:message key="system.roolingNotice.type1"/></b>';
		}else{
			str = '<b><fmt:message key="system.roolingNotice.type2"/></b>';
		}
		return str;
	}
	
});